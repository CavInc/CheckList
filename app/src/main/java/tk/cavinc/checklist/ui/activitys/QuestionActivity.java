package tk.cavinc.checklist.ui.activitys;

import android.content.ContentValues;
import android.content.Intent;
import android.icu.text.UFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.vadel.yandexdisk.YandexDiskApi;
import org.vadel.yandexdisk.webdav.WebDavFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import tk.cavinc.checklist.R;
import tk.cavinc.checklist.data.manager.DataManager;
import tk.cavinc.checklist.data.models.CheckItemModel;
import tk.cavinc.checklist.ui.adapters.CustomExpandListAdapter;
import tk.cavinc.checklist.ui.dialogs.CommentDialog;
import tk.cavinc.checklist.utils.ConstantManager;
import tk.cavinc.checklist.utils.Utils;

//https://sohabr.net/post/227549/   - а вот тут то что надо
//https://androidexample.com/Custom_Expandable_ListView_Tutorial_-_Android_Example/index.php?view=article_discription&aid=107&aaid=129
//https://ru.androids.help/q10836    -- только поглядеть возможно и нафиг не нужно
//https://www.codeproject.com/Articles/1151814/Android-ExpandablelistView-Tutorial-with-Android-C
//http://abhiandroid.com/ui/expandablelistadapter-example-android.html
// https://stackoverflow.com/questions/5188196/how-to-write-custom-expandablelistadapter - тоже самое

public class QuestionActivity extends AppCompatActivity implements ExpandableListView.OnChildClickListener,AdapterView.OnItemLongClickListener{
    private static final String TAG = "QA";
    private DataManager mDataManager;
    private String mDateCheck;
    private String mTime;
    private String mLongData;
    private int mTag;

    private ExpandableListView mExpandList;
    private CustomExpandListAdapter adapter;
    private File mPhotoFile;
    private int countItem = 0;

    private String CLIENT_ID = "00a1d2b7031c483a892ccbef3c4bd13c";
    private ArrayList<String> loginPass;

    private boolean sendDirect = false;

    private YandexDiskApi api;
    private String yandexFolder;
    private ActionBar actionBar;
    private int checkedCount = 0;

    private boolean getPhoto = false;

    {
        YandexDiskApi.DEBUG = true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        mDataManager = DataManager.getInstance();


        mLongData = getIntent().getStringExtra(ConstantManager.WORK_DATA_LONG);
        mDateCheck = getIntent().getStringExtra(ConstantManager.WORK_DATA);
        mTime = getIntent().getStringExtra(ConstantManager.WORK_TIME);
        mTag = Integer.parseInt(getIntent().getStringExtra(ConstantManager.WORK_ID_TAG));

        mExpandList = findViewById(R.id.expond_question);
        mExpandList.setOnItemLongClickListener(this);
        //mExpandList.setOnItemClickListener(this);
        mExpandList.setOnChildClickListener(this);

        loginPass = mDataManager.getPrefManager().getLoginPassword();
        if (loginPass.get(0) != null && loginPass.get(1) != null) {
            if (mDataManager.isOnline()) {
                api = new YandexDiskApi(CLIENT_ID);
                api.setCredentials(loginPass.get(0), loginPass.get(1));
                Log.d(TAG, "XF : " + api.isAuthorization());
                Log.d(TAG," OAUTH :" +api.getOAthRequestUrl());

                Log.d(TAG,"USER LOGIN :"+api.getUserLogin());
               // checkLogin();

                yandexFolder = "/CheckList/" + Utils.pathToData(mLongData) + "/";
                if (api.isAuthorization()) {
                    sendDirect = true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<WebDavFile> filesCheck = api.getFiles("/");
                            if (filesCheck == null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionActivity.this);
                                        builder.setTitle("Внимание !")
                                                .setMessage("Ошибка аудентификации")
                                                .setNegativeButton(R.string.dialog_close,null)
                                                .show();
                                    }
                                });
                                return;
                            }

                            boolean res;
                            ArrayList<WebDavFile> files = api.getFiles("/CheckList");
                            if (files != null) {
                                /*
                                for (int i = 0; i < files.size(); i++) {
                                    Log.d(TAG, files.get(i).getParentPath());
                                }
                                */
                                res = api.createFolder(yandexFolder);
                                Log.d(TAG,"CREATE FOLDER "+res);
                            } else {
                                res = api.createFolder("/CheckList");
                                if (res) {
                                    res = api.createFolder(yandexFolder);
                                    Log.d(TAG,"CREATE FOLDER "+res);
                                }
                            }
                        }
                    }).start();
                }
            } else {
                //TODO сказать что хуй а не работа с хуяндексом
                Toast.makeText(this, "Не включена передача данных", Toast.LENGTH_LONG).show();
            }
        }


        costructData();
        setupTools();

        actionBar.setSubtitle("Не пройдено :"+String.valueOf(countItem - checkedCount));
    }

    private void checkLogin() {
        Log.d(TAG,"USER LOGIN :"+api.getUserLogin());
        Log.d(TAG,"OAUTH :"+api.getOAthRequestUrl());

    }

    public void setupTools(){
        actionBar = getSupportActionBar();
        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Опрос: "+mDateCheck+" - "+mTime);
            //actionBar.setSubtitle(mTime);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        // TODO првоерка должа быть
        if (getPhoto) {
            getPhoto = false;
        } else {
            Log.d(TAG,"check cout : "+checkedCount);
            Log.d(TAG,"count Item : "+countItem);
            if (countItem>checkedCount) {
                Utils.setNotification(QuestionActivity.this, mLongData, mDateCheck, mTime, String.valueOf(mTag));
            }
        }
    }



    private void costructData(){
        String json = null;
        try {
            InputStream is = getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        ArrayList<CheckItemModel> storeData = mDataManager.getDB().getCheckInDate(mLongData);

        // заполняем коллекцию групп из массива с названиями групп
        ArrayList<Map<String, String>> groupData = new ArrayList<Map<String, String>>();

        // создаем коллекцию для коллекций элементов
        ArrayList<ArrayList<Map<String, CheckItemModel>>> childData = new ArrayList<ArrayList<Map<String, CheckItemModel>>>();


        try {
            JSONObject obj = new JSONObject(json);
            JSONArray jArr = obj.getJSONArray("items");
            for (int i=0;i<jArr.length();i++){
                JSONObject quest = jArr.getJSONObject(i);
                //Log.d("QA","TITEL:"+quest.get("title"));
                // заполняем список атрибутов для каждой группы
                int groupID = quest.getInt("id");
                HashMap<String, String> m = new HashMap<String, String>();
                m.put("groupName",quest.getString("title"));
                //groupData.add(m);

               // int ccx = 0;
                ArrayList<Map<String, CheckItemModel>> childDataItem = new ArrayList<Map<String, CheckItemModel>>();
                JSONArray jCheck = quest.getJSONArray("check");
                HashMap<String,CheckItemModel> mx = new HashMap<>();
                for (int j=0 ; j<jCheck.length() ; j++){
                    JSONObject checkItem = jCheck.getJSONObject(j);
                    // Log.d("CI"," ITEM Title :"+checkItem.get("title"));
                    // TODO добавить убирание элемента по времени.
                    JSONArray wt =  checkItem.getJSONArray("time_check");
                    if (wt.getInt(mTag-1) == 1) {
                        mx = new HashMap<String, CheckItemModel>();

                        if (checkItem.has("photo") && checkItem.getBoolean("photo")) {
                            mx.put("itemText",new CheckItemModel(groupID,checkItem.getInt("id"),checkItem.getString("title"),false,true));
                        } else {
                            mx.put("itemText", new CheckItemModel(groupID,checkItem.getInt("id"), checkItem.getString("title"), false));
                            countItem += 1;
                           // Log.d("CI"," ITEM count:"+countItem);
                            //ccx += 1;
                        }

                        mx.get("itemText").setTime(mTime);
                        int pos = storeData.indexOf(mx.get("itemText"));

                        if (pos != -1) {
                            if (storeData.get(0).isCheck()) {
                                checkedCount += 1;
                            }
                            mx.get("itemText").setCheck(storeData.get(pos).isCheck());
                            mx.get("itemText").setComment(storeData.get(pos).getComment());
                            mx.get("itemText").setPhotoName(storeData.get(pos).getPhotoName());
                        }

                        childDataItem.add(mx);
                    }
                }
                if (childDataItem.size() !=0) {
                    childData.add(childDataItem);
                    groupData.add(m);
                    //Log.d("CI","ITEMS COUNT:"+ccx);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        mDataManager.getPrefManager().setCountWorkTime(mTime,countItem);

        // создаем  expand list view

        // список атрибутов групп для чтения
        String groupFrom[] = new String[] {"groupName"};
        // список ID view-элементов, в которые будет помещены атрибуты групп
        int groupTo[] = new int[] {R.id.elg_name};

        // список атрибутов элементов для чтения
        String childFrom[] = new String[] {"itemText","itemValue"};
        // список ID view-элементов, в которые будет помещены атрибуты элементов
        int childTo[] = new int[] {R.id.expant_list_item_name};

        adapter = new CustomExpandListAdapter(
                this,
                groupData,
                R.layout.expant_list_group_item,
                groupFrom,
                groupTo,
                childData,
                R.layout.expand_list_item,
                childFrom,
                childTo);
        mExpandList.setAdapter(adapter);

        for (int i=0;i<groupData.size();i++) {
            mExpandList.expandGroup(i);
        }


    }


    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        Object model = adapterView.getItemAtPosition(position);
        selectData = (CheckItemModel) ((HashMap) model).get("itemText");
        CommentDialog dialog = new CommentDialog();
        dialog.setDialogListener(mCommentDialogListener);
        dialog.show(getFragmentManager(),"CD");
        return true;
    }

    CheckItemModel selectData;

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int groupID, int childID, long id) {
        //Log.d(TAG,"POST IS "+groupID+" "+childID+" "+id);

        HashMap group = (HashMap) adapter.getGroup(groupID);

        Object fmd = adapter.getChild(groupID, childID);
        selectData = (CheckItemModel) ((HashMap) fmd).get("itemText");

        Log.d(TAG," ITEM "+selectData.getTitle());
        if (selectData.isPhoto() ) {
            loadPhoto(groupID,childID, String.valueOf(group.get("groupName")));
        }else {
            selectData.setCheck(! selectData.isCheck());
            storeData();
            if (selectData.isCheck()) {
                checkedCount += 1;
            } else {
                checkedCount -= 1;
            }
            actionBar.setSubtitle("Не пройдено :"+String.valueOf(countItem - checkedCount));
        }
        //adapter.notifyDataSetChanged();
        return false;
    }

    CommentDialog.OnCommentDialogListener mCommentDialogListener = new CommentDialog.OnCommentDialogListener() {
        @Override
        public void onChange(String val) {
            selectData.setComment(val);
            storeData();
            //adapter.notifyDataSetChanged();
        }
    };

    private void loadPhoto(int group, int pos, String title){
        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            mPhotoFile = createFile(group,pos,title);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            Uri fileUri = FileProvider.getUriForFile(this,
                    this.getApplicationContext().getPackageName() + ".provider", mPhotoFile);
            photoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            photoIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
        } else {
            photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
        }
        getPhoto = true;
        startActivityForResult(photoIntent,ConstantManager.REQUEST_CAMERA_PICTURE);
    }

    private File createFile(int group, int pos, String title) throws IOException {
        String timeStamp = Utils.dateToStr("ddMMyy",new Date());
        String fname = timeStamp+"_"+mTime.replaceAll(":","")+"_"+title.replaceAll(":","").trim();
        if (mDataManager.isExternalStorageWritable()){
            String path = mDataManager.getStorageAppPath();
            //File pathPath = mDataManager.getStoragePath();
            File imgFile = File.createTempFile(fname,".jpg",new File(path));
            return imgFile;
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode == RESULT_OK && mPhotoFile !=null){
                    selectData.setPhotoName(mPhotoFile.getName());
                    // изменяем размер
                    Utils.resizeImgFile(mPhotoFile.getAbsolutePath());
                    storeData();
                    //adapter.notifyDataSetChanged();

                    if (sendDirect && api.isAuthorization()){
                        try {
                            sendFileInYandexDisk();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    mPhotoFile = null;
                }
                break;
        }
    }

    // сохраняем данные в базу
    private void storeData(){
        mDataManager.getDB().addCheckRec(selectData,mLongData,mTime);
        adapter.notifyDataSetChanged();
    }

    // оправили на YD
    private void sendFileInYandexDisk() throws FileNotFoundException {
        // формат имени - файла dd.MM.yy-time-цех
        final InputStream io = new FileInputStream(mPhotoFile);
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean res = api.uploadFile(yandexFolder + mPhotoFile.getName(), io, mPhotoFile.length());
                //TODO проверяем и если не ушло то ставим флаг
                Log.d(TAG," SEND FLG :"+res);
                if (res) {
                    mDataManager.getDB().setPhotoStatus(selectData,mLongData,mTime);
                    mPhotoFile.delete();
                }
            }
        }).start();
    }
}
