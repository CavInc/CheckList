package tk.cavinc.checklist.ui.activitys;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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

        costructData();

        setupTools();
    }

    public void setupTools(){
        ActionBar actionBar = getSupportActionBar();
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
                Log.d("QA","TITEL:"+quest.get("title"));
                // заполняем список атрибутов для каждой группы
                int groupID = quest.getInt("id");
                HashMap<String, String> m = new HashMap<String, String>();
                m.put("groupName",quest.getString("title"));
                //groupData.add(m);

                ArrayList<Map<String, CheckItemModel>> childDataItem = new ArrayList<Map<String, CheckItemModel>>();
                JSONArray jCheck = quest.getJSONArray("check");
                HashMap<String,CheckItemModel> mx = new HashMap<>();
                for (int j=0 ; j<jCheck.length() ; j++){
                    JSONObject checkItem = jCheck.getJSONObject(j);
                    Log.d("CI"," ITEM Title :"+checkItem.get("title"));
                    // TODO добавить убирание элемента по времени.
                    JSONArray wt =  checkItem.getJSONArray("time_check");
                    if (wt.getInt(mTag-1) == 1) {
                        mx = new HashMap<String, CheckItemModel>();

                        if (checkItem.has("photo") && checkItem.getBoolean("photo")) {
                            mx.put("itemText",new CheckItemModel(groupID,checkItem.getInt("id"),checkItem.getString("title"),false,true));
                        } else {
                            mx.put("itemText", new CheckItemModel(groupID,checkItem.getInt("id"), checkItem.getString("title"), false));
                        }
                        childDataItem.add(mx);
                    }
                }
                if (childDataItem.size() !=0) {
                    childData.add(childDataItem);
                    groupData.add(m);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

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
        Log.d(TAG,"POS :"+position);
        Object model = adapterView.getItemAtPosition(position);
        selectData = (CheckItemModel) ((HashMap) model).get("itemText");
        Log.d(TAG,"ITEM "+selectData.getTitle());
        CommentDialog dialog = new CommentDialog();
        dialog.setDialogListener(mCommentDialogListener);
        dialog.show(getFragmentManager(),"CD");
        return true;
    }

    CheckItemModel selectData;

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int groupID, int childID, long id) {
        Log.d(TAG,"POST IS "+groupID+" "+childID+" "+id);

        Object fmd = adapter.getChild(groupID, childID);
        selectData = (CheckItemModel) ((HashMap) fmd).get("itemText");

        Log.d(TAG," ITEM "+selectData.getTitle());
        if (selectData.isPhoto() ) {
            loadPhoto(groupID,childID);
        }else {
            selectData.setCheck(! selectData.isCheck());
            storeData();
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

    private void loadPhoto(int group,int pos){
        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            mPhotoFile = createFile(group,pos);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
        startActivityForResult(photoIntent,ConstantManager.REQUEST_CAMERA_PICTURE);
    }

    private File createFile(int group,int pos) throws IOException {
        String timeStamp = Utils.dateToStr("yyyyyMMdd",new Date());
        String fname = String.valueOf(group)+"_"+String.valueOf(pos)+"_"+timeStamp+"_"+mTime.replaceAll(":","");
        if (mDataManager.isExternalStorageWritable()){
            String path = mDataManager.getStorageAppPath();
            //File pathPath = mDataManager.getStoragePath();
            File imgFile = File.createTempFile(fname,"jpg",new File(path));
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
                    storeData();
                    //adapter.notifyDataSetChanged();
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
}
