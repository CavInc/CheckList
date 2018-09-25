package tk.cavinc.checklist.ui.activitys;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.vadel.yandexdisk.YandexDiskApi;
import org.vadel.yandexdisk.webdav.WebDavFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import tk.cavinc.checklist.R;
import tk.cavinc.checklist.data.manager.DataManager;
import tk.cavinc.checklist.data.models.ArhiveDocModel;
import tk.cavinc.checklist.data.models.ArhiveHeadModel;
import tk.cavinc.checklist.data.models.CountTimeModel;
import tk.cavinc.checklist.ui.dialogs.LoginDialog;
import tk.cavinc.checklist.utils.ConstantManager;
import tk.cavinc.checklist.utils.PrepareArhiveData;
import tk.cavinc.checklist.utils.StoreXlsFile;
import tk.cavinc.checklist.utils.Utils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MA";
    private static final int PERMISSION_REQUEST_CODE = 200;

    private DataManager mDataManager;

    private TextView mTextView;
    private String mLongData;
    private String mShortData;

    private Button mBt0900;
    private Button mBt1300;
    private Button mBt1700;
    private Button mBt2100;
    private Button mBt0100;
    private Button mBt0500;

    private YandexDiskApi api;
    private boolean lockDialog = false;
    private Bundle store;
    private String sessionDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDataManager = DataManager.getInstance();

        store =  getIntent().getExtras();

        mBt0900 = findViewById(R.id.bt0900);
        mBt1300 = findViewById(R.id.bt1300);
        mBt1700 = findViewById(R.id.bt1700);
        mBt2100 = findViewById(R.id.bt2100);
        mBt0100 = findViewById(R.id.bt0100);
        mBt0500 = findViewById(R.id.bt0500);

        mBt0900.setOnClickListener(this);
        mBt1300.setOnClickListener(this);
        mBt1700.setOnClickListener(this);
        mBt2100.setOnClickListener(this);
        mBt0100.setOnClickListener(this);
        mBt0500.setOnClickListener(this);



        if (store != null) {
            String lx = store.getString(ConstantManager.EDIT_DATA);
            if (lx == null){
                Log.d(TAG,"LXX");
            }
            if (store.getString(ConstantManager.EDIT_DATA) != null) {
                setupTools();
            }
        }
    }

    private void setupTools() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        Button bt = (Button) v;
        String cp = bt.getText().toString();
        Object tag = bt.getTag();

        Intent intent = new Intent(this,QuestionActivity.class);
        intent.putExtra(ConstantManager.WORK_DATA_LONG,mLongData);
        intent.putExtra(ConstantManager.WORK_DATA,mShortData);
        intent.putExtra(ConstantManager.WORK_TIME,cp);
        intent.putExtra(ConstantManager.WORK_ID_TAG,tag.toString());
        startActivity(intent);
    }

    private MenuItem mLoginYa;
    private MenuItem mLogOutYa;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (store != null) {
            if (store.getString(ConstantManager.EDIT_DATA) != null) {
                return super.onCreateOptionsMenu(menu);
            }
        }

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        mLoginYa = menu.findItem(R.id.menu_login);
        mLogOutYa = menu.findItem(R.id.menu_logout);
        if (mDataManager.getPrefManager().getCheckAccount()) {
            mLoginYa.setVisible(false);
            mLogOutYa.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        if (item.getItemId() == R.id.menu_arhive) {
            Intent intent = new Intent(this,ArhiveActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.menu_login) {
            ArrayList<String> loginPass = mDataManager.getPrefManager().getLoginPassword();
            LoginDialog dialog = LoginDialog.newInstance(loginPass.get(0),loginPass.get(1));
            dialog.setOnLoginDialogListener(mListener);
            dialog.show(getFragmentManager(),"LD");
        }
        if (item.getItemId() == R.id.menu_logout) {
            mLogOutYa.setVisible(false);
            mLoginYa.setVisible(true);
            mDataManager.getPrefManager().setLoginPassword(null,null);
            mDataManager.getPrefManager().setCheckAccount(false);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (store == null) {
            Log.d(TAG,"STORE : null ");
            setWorkDate(new Date());
        } else {
            Log.d(TAG,"STORE : "+store.isEmpty());
            if (store.getString(ConstantManager.EDIT_DATA) != null) {
                try {
                    sessionDate = mDataManager.getPrefManager().getWorkData();
                    mDataManager.getPrefManager().setWorkData(store.getString(ConstantManager.EDIT_DATA));
                    setWorkDate(Utils.strToDate("yyyy-MM-dd", store.getString(ConstantManager.EDIT_DATA)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                setWorkDate(new Date());
            }
        }

        ArrayList<CountTimeModel> rec = mDataManager.getDB().getCountAll(mLongData);

        getAllCount();

        //TODO не забыть раскоментировать
        //setCountButton(rec);
        setAlertButton(rec);

        ArrayList<String> loginPass = mDataManager.getPrefManager().getLoginPassword();
        if (!lockDialog && loginPass.get(0) == null && loginPass.get(1) == null) {
            lockDialog = true;
            LoginDialog dialog = new LoginDialog();
            dialog.setOnLoginDialogListener(mListener);
            dialog.show(getFragmentManager(),"LD");
        }

        checkAndSetPermission();
        // TODO для проверки
        if (getCountTwo(rec,"05:00")){
            //TODO проверять оправляли ли уже или нет
            String lastSend = mDataManager.getPrefManager().getLastSendFile();
            if (!lastSend.equals(mLongData)) {
                try {
                    sendYandexDisk();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (store !=null) {
            mDataManager.getPrefManager().setWorkData(sessionDate);
            store.clear();
            store = null;
        }

    }

    private void setWorkDate(Date nowDate){
        //TODO а зедся надо проверять прешли ли мы через 0 если вдруг запустили систему ночью
        String workData = mDataManager.getPrefManager().getWorkData();

        if (workData != null) {
            if (Utils.testData(workData,nowDate)) {
                mDataManager.getPrefManager().setWorkData(Utils.dateToStr("yyyy-MM-dd",nowDate));
            } else {
                try {
                    nowDate = Utils.strToDate("yyyy-MM-dd",workData);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else {
            mDataManager.getPrefManager().setWorkData(Utils.dateToStr("yyyy-MM-dd",nowDate));
        }

        mLongData = Utils.dateToStr("yyyy-MM-dd",nowDate);
        mShortData = Utils.dateToStr("dd.MM.yy",nowDate);

        mTextView = findViewById(R.id.date_tv);
        mTextView.setText(Utils.dateToStr("dd.MM.yyyy",nowDate));
    }

    private void getAllCount(){
        if (mDataManager.getPrefManager().getAllQuestionCount() == 0){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int count = mDataManager.getAllQuestionCount();
                    mDataManager.getPrefManager().setAllQuestionCount(count);
                }
            }).start();
        }
    }

    //TODO запихать в AcyncTask ?
    private void sendYandexDisk() throws FileNotFoundException {

        ArrayList<String> loginPass = mDataManager.getPrefManager().getLoginPassword();
        if (loginPass.get(0) != null && loginPass.get(1) != null) {
            if (mDataManager.isOnline()) {
                api = new YandexDiskApi(getResources().getString(R.string.CLIENT_ID));
                api.setTokenFromCallBackURI(getResources().getString(R.string.CALL_BACK_URL));
                api.setCredentials(loginPass.get(0), loginPass.get(1));

                // данные
                ArhiveDocModel prepareData = new PrepareArhiveData(mLongData).get();
                new StoreXlsFile(this,mDataManager.getStorageAppPath(),mLongData+".xls",
                        prepareData.getArhive(),prepareData.getCommentModels()).write();
                final File sendFile = new File(mDataManager.getStorageAppPath()+"/"+mLongData+".xls");
                final InputStream io = new FileInputStream(sendFile);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean res = api.uploadFile("//CheckList/"+Utils.pathToData(sendFile.getName())
                                +"/"+sendFile.getName().replaceAll("-","_"),io,sendFile.length());
                        if (res) {
                            mTextView.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this,
                                            "Файл оправлен в облако\n"+sendFile.getName(),Toast.LENGTH_SHORT).show();
                                }
                            });
                            mDataManager.getPrefManager().setLastSendFile(mLongData);
                            sendFile.delete();
                        }
                    }
                }).start();
            } else {
                showNoNetwork();
            }
        }

    }

    private void showNoNetwork(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Внимание")
                .setMessage("Не включена передача данных")
                .setNegativeButton(R.string.dialog_close,null);
    }



    private boolean getCountTwo(ArrayList<CountTimeModel> rec, String time) {
        int countQuestion = 0;
        int countRec = 0 ;

        countQuestion = mDataManager.getPrefManager().getCountWorkTime(time);
        int id = rec.indexOf(new CountTimeModel(time,0));
        if (id != -1){
            countRec = rec.get(id).getCount();
            Log.d(TAG, " COUNT : " + countQuestion + " " + countRec);
        } else {
          return false;
        }
        if (countQuestion > countRec) return false;
        return true;
    }

    private void setAlertButton(ArrayList<CountTimeModel> rec){
        Drawable img = AppCompatResources.getDrawable(this, R.drawable.ic_warning_red_24dp);
       // mBt1300.setCompoundDrawablesWithIntrinsicBounds(img,null,null,null);

        if (rec.size() == 0) {
            return;
        }

        if (! getCountTwo(rec,"09:00")){
            mBt0900.setCompoundDrawablesWithIntrinsicBounds(img,null,null,null);
        } else {
            mBt0900.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        }
        if (! getCountTwo(rec,"13:00")) {
            mBt1300.setCompoundDrawablesWithIntrinsicBounds(img,null,null,null);
        } else {
            mBt1300.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        }
        if (! getCountTwo(rec,"17:00")){
            mBt1700.setCompoundDrawablesWithIntrinsicBounds(img,null,null,null);
        } else {
            mBt1700.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        }

        if (! getCountTwo(rec,"21:00")){
            mBt2100.setCompoundDrawablesWithIntrinsicBounds(img,null,null,null);
        } else {
            mBt2100.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        }

        if (! getCountTwo(rec,"01:00")){
            mBt0100.setCompoundDrawablesWithIntrinsicBounds(img,null,null,null);
        } else {
            mBt0100.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        }

        if (! getCountTwo(rec,"05:00")){
            mBt0500.setCompoundDrawablesWithIntrinsicBounds(img,null,null,null);
        } else {
            mBt0500.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        }

    }


    private void setCountButton(ArrayList<CountTimeModel> rec) {
        mBt1300.setEnabled(true);
        mBt1700.setEnabled(true);
        mBt2100.setEnabled(true);
        mBt0100.setEnabled(true);
        mBt0500.setEnabled(true);

        if (rec.size() == 0) {
            mBt1300.setEnabled(false);
            mBt1700.setEnabled(false);
            mBt2100.setEnabled(false);
            mBt0100.setEnabled(false);
            mBt0500.setEnabled(false);
            return;
        }

        if (getCountTwo(rec,"09:00")){
            mBt1300.setEnabled(false);
            mBt1700.setEnabled(false);
            mBt2100.setEnabled(false);
            mBt0100.setEnabled(false);
            mBt0500.setEnabled(false);
            return;
        }

        if (getCountTwo(rec,"13:00")) {
            mBt1700.setEnabled(false);
            mBt2100.setEnabled(false);
            mBt0100.setEnabled(false);
            mBt0500.setEnabled(false);
            return;
        }

        if (getCountTwo(rec,"17:00")) {
            mBt2100.setEnabled(false);
            mBt0100.setEnabled(false);
            mBt0500.setEnabled(false);
            return;
        }

        if (getCountTwo(rec,"21:00")) {
            mBt0100.setEnabled(false);
            mBt0500.setEnabled(false);
            return;
        }

        if (getCountTwo(rec,"01:00")) {
            mBt0500.setEnabled(false);
            return;
        }

        if (getCountTwo(rec,"05:00")) {
            // TODO если все проверено то оправить данные в облако

        }


        /*
        if (rec.get(0).getTime().equals("09:00")) {
            Log.d(TAG," COUNT : "+countQuestion+" "+rec.get(0).getCount());
        }
        countQuestion = mDataManager.getPrefManager().getCountWorkTime("13:00");
        if (rec.get(1).getTime().equals("09:00")) {
            Log.d(TAG," COUNT : "+countQuestion+" "+rec.get(1).getCount());
        }
        */

    }

    private void checkAndSetPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},PERMISSION_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
        }


    }

    private void checkLogin(String login,String pass){
        // проверяем можно ли зайти
        if (mDataManager.isOnline()) {
            if (api == null) {
                api = new YandexDiskApi(getResources().getString(R.string.CLIENT_ID));
            }
            api.setTokenFromCallBackURI(getResources().getString(R.string.CALL_BACK_URL));
            api.setCredentials(login, pass);
            // проверяем доступность чтением файлов из YA
            // может быть проблемма при пустом YA
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<WebDavFile> filesCheck = api.getFiles("/");
                    if (filesCheck == null) {
                        // файлов нет
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Внимание !")
                                        .setMessage("Ошибка аудентификации")
                                        .setNegativeButton(R.string.dialog_close,null)
                                        .show();
                                mLogOutYa.setVisible(false);
                                mLoginYa.setVisible(true);
                            }
                        });
                        mDataManager.getPrefManager().setLoginPassword(null,null);
                        mDataManager.getPrefManager().setCheckAccount(false);
                        return;

                    } else {
                        // файлы есть.
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mLoginYa.setVisible(false);
                                mLogOutYa.setVisible(true);
                            }
                        });
                        mDataManager.getPrefManager().setCheckAccount(true);
                    }
                }
            }).start();

        } else {
            showNoNetwork();
        }
    }

    LoginDialog.OnLoginDialogListener mListener = new LoginDialog.OnLoginDialogListener() {
        @Override
        public void onLogin(String login, String pass) {
            if (login.length() != 0 && pass.length() != 0){
                mDataManager.getPrefManager().setLoginPassword(login,pass);
                checkLogin(login,pass);
            } else {
                // TODO тут ругаемся

            }
        }
    };


}
