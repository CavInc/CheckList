package tk.cavinc.checklist.ui.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.vadel.yandexdisk.YandexDiskApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import tk.cavinc.checklist.R;
import tk.cavinc.checklist.data.manager.DataManager;
import tk.cavinc.checklist.data.models.ArhiveDocModel;
import tk.cavinc.checklist.data.models.ArhiveModel;
import tk.cavinc.checklist.data.models.CheckItemModel;
import tk.cavinc.checklist.ui.adapters.ArhiveAdapter;
import tk.cavinc.checklist.utils.ConstantManager;
import tk.cavinc.checklist.utils.CustomFileNameFilter;
import tk.cavinc.checklist.utils.PrepareArhiveData;
import tk.cavinc.checklist.utils.StoreXlsFile;
import tk.cavinc.checklist.utils.Utils;

/*
https://automated-testing.info/t/api-dlya-raboty-s-excel/1563/2
http://romanchekashov.blogspot.com/2014/09/create-excel-file-in-java.html
https://www.javaworld.com/article/2074940/learn-java/java-app-dev-reading-and-writing-excel-spreadsheets.html
 */

public class ArhiveActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener{
    private static final String TAG = "AA";
    private DataManager mDataManager;

    private ListView mListView;
    private ArhiveAdapter mAdapter;

    private YandexDiskApi api;
    private ArrayList<String> loginPass;

    {
        YandexDiskApi.DEBUG = true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arhive);

        mDataManager = DataManager.getInstance();

        mListView = findViewById(R.id.arhive_lv);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        loginPass = mDataManager.getPrefManager().getLoginPassword();
        if (loginPass.get(0) != null && loginPass.get(1) != null) {
            if (mDataManager.isOnline()) {
                api = new YandexDiskApi(getResources().getString(R.string.CLIENT_ID));
                api.setTokenFromCallBackURI(getResources().getString(R.string.CALL_BACK_URL));
                api.setCredentials(loginPass.get(0), loginPass.get(1));
                Log.d(TAG,"YD : "+api.isAuthorization());
                Log.d(TAG,"YD : "+api.getAuthorization());

                Log.d(TAG,"LOGIN : "+api.getUserLogin());

            } else {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Внимание")
                        .setMessage("Не включена передача данных !\n действия по оправке архивов будут невозможны")
                        .setNegativeButton(R.string.dialog_close,null)
                        .show();
            }
        }

        setupTools();
    }

    public void setupTools(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
           // actionBar.setTitle("Опрос: "+mDateCheck+"  ::  "+mTime);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.arhive_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        if (item.getItemId() == R.id.send_mail) {
            createXls();
            try {
                sendMail();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (item.getItemId() == R.id.send_cloud) {
            createXls();
            try {
                sendCloud();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (item.getItemId() == R.id.delete_arhive) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Удаление")
                    .setMessage("Удаляем архив(вы)\nВы уверены ?")
                    .setNegativeButton(R.string.dialog_no,null)
                    .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteArhive();
                        }
                    })
                    .show();
        }
        return true;
    }

    private void deleteArhive() {
        for (int i = 0 ; i<mAdapter.getCount();i++) {
            ArhiveModel model = mAdapter.getItem(i);
            if (model.isCheck()) {
                Log.d("AA", "Arhive Name :" + model.getTitle());
                mDataManager.getDB().deleteArhive(model.getTitle());
                mAdapter.remove(model);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    // оправляем в облако
    private void sendCloud() throws IOException {
        if (api == null) return;
        if (api.isAuthorization()) {
            String outPath = mDataManager.getStorageAppPath();
            File outDir = new File(outPath);
            final File[] fileList = outDir.listFiles(new CustomFileNameFilter(".xls"));
            for (int i = 0; i<fileList.length; i++){
                Log.d("AA","Files "+fileList[i].getCanonicalPath());
                final InputStream io = new FileInputStream(fileList[i].getCanonicalFile());
                final int finalI = i;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d("AA","/CheckList/" +
                                    Utils.pathToData(fileList[finalI].getName()).replaceAll(".xls",""));

                            String px = api.getDownloadUrl("/CheckList/" +
                                    Utils.pathToData(fileList[finalI].getName()).replaceAll(".xls",""));

                            if (px == null) {
                                boolean res = api.createFolder("/CheckList/" +
                                        Utils.pathToData(fileList[finalI].getName()).replaceAll(".xls",""));
                                Log.d("AA","CREATE FOLDER : "+res);
                            }

                            boolean res = api.uploadFile("/CheckList/"+
                                    Utils.pathToData(fileList[finalI].getName())+
                                    '/'+fileList[finalI].getName().replaceAll("-","_"),io,fileList[finalI].length());
                            Log.d("AA","UPLOAD FILE :"+res);
                            if (res) {
                                mListView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ArhiveActivity.this,
                                                "Файл оправлен в облако\n"+fileList[finalI].getName(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                                fileList[finalI].delete();
                                if (mDataManager.getPrefManager().isDeleteInStore()) {
                                    //TODO возможно тут тоже сделать удаление
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            sendNoSendPhoto(fileList[finalI].getName());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

            }
        }
    }

    // отправка неотправленых фотографий
    private void sendNoSendPhoto(String date) throws FileNotFoundException {
        Log.d(TAG,"DT : "+date);
        String dataFolder = date.replaceAll(".xls", "");
        String outPath = mDataManager.getStorageAppPath();
        ArrayList<CheckItemModel> photoList = mDataManager.getDB().getNoSendPhoto(dataFolder);
        for (CheckItemModel lx : photoList){
            File photo = new File(outPath+"/"+lx.getPhotoName());
            InputStream io = new FileInputStream(photo);

            boolean res = api.uploadFile("/CheckList/"+
                    Utils.pathToData(dataFolder)+"/"+photo.getName(), io, photo.length());
            Log.d("AA","SEND PHOTO : "+res);
            if (res) {
                mDataManager.getDB().setPhotoStatus(lx,dataFolder,lx.getTime());
                photo.delete();
            }
        }
    }


    // создаем xls из отмеченных архивов
    private void createXls() {
        for (int i = 0 ; i<mAdapter.getCount();i++){
            ArhiveModel model = mAdapter.getItem(i);
            if (model.isCheck()) {
                Log.d("AA","Arhive Name :"+model.getTitle());
                ArhiveDocModel prepareData = new PrepareArhiveData(model.getTitle()).get();
                //ArrayList<ArhiveHeadModel> prepareData = new PrepareArhiveData(model.getTitle()).get();
                model.setCheck(false);
                mAdapter.notifyDataSetChanged();
                new StoreXlsFile(this,mDataManager.getStorageAppPath(),model.getTitle()+".xls",
                        prepareData.getArhive(),prepareData.getCommentModels()).write();

            }
        }
    }

    // оправляем по почте
    private void sendMail() throws IOException {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Архивы проверки");
        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml("<b>Архивы проверки</b>"));
        emailIntent.setType("application/octet-stream");


        String outPath = mDataManager.getStorageAppPath();
        File outDir = new File(outPath);
        File[] fileList = outDir.listFiles(new CustomFileNameFilter(".xls"));
        for (int i = 0; i<fileList.length; i++){
           // Log.d("AA",fileList[i].getAbsolutePath());
            //Log.d("AA",fileList[i].getCanonicalPath());
           // Log.d("AA",Uri.parse("file://"+fileList[i].getCanonicalPath()).toString());
            Log.d("AA",fileList[i].toURI().toString());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
                Log.d("AA","A6 ? ");
                Uri fileUri = FileProvider.getUriForFile(this,
                        this.getApplicationContext().getPackageName() + ".provider", fileList[i]);

                Log.d("AA",fileUri.toString());

                emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                emailIntent.putExtra(Intent.EXTRA_STREAM,fileUri);
            }else {
                emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + fileList[i].getCanonicalPath()));
            }
        }

        startActivity(Intent.createChooser(emailIntent,"Отправить почту"));

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI(){
        ArrayList<ArhiveModel> data = mDataManager.getDB().getArhiveChech(mDataManager.getAllQuestionCount());
        if (mAdapter == null ){
            mAdapter = new ArhiveAdapter(this,R.layout.arhive_item,data);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        mAdapter.getItem(position).setCheck(!mAdapter.getItem(position).isCheck());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        Log.d(TAG,"LONG");
        ArhiveModel selectItem = (ArhiveModel) adapterView.getItemAtPosition(position);

        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra(ConstantManager.EDIT_DATA,selectItem.getTitle());
        startActivity(intent);
        return true;
    }
}
