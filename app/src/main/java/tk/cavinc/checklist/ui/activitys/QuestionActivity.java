package tk.cavinc.checklist.ui.activitys;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tk.cavinc.checklist.R;
import tk.cavinc.checklist.data.manager.DataManager;
import tk.cavinc.checklist.ui.adapters.CustomExpandListAdapter;
import tk.cavinc.checklist.utils.ConstantManager;

//https://sohabr.net/post/227549/   - а вот тут то что надо
//https://androidexample.com/Custom_Expandable_ListView_Tutorial_-_Android_Example/index.php?view=article_discription&aid=107&aaid=129
//https://ru.androids.help/q10836    -- только поглядеть возможно и нафиг не нужно
//https://www.codeproject.com/Articles/1151814/Android-ExpandablelistView-Tutorial-with-Android-C
//http://abhiandroid.com/ui/expandablelistadapter-example-android.html
// https://stackoverflow.com/questions/5188196/how-to-write-custom-expandablelistadapter - тоже самое

public class QuestionActivity extends AppCompatActivity {
    private DataManager mDataManager;
    private String mDateCheck;

    private ExpandableListView mExpandList;
    private CustomExpandListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        mDataManager = DataManager.getInstance();

        mDateCheck = getIntent().getStringExtra(ConstantManager.WORK_DATA);

        mExpandList = findViewById(R.id.expond_question);

        costructData();

        setupTools();
    }

    public void setupTools(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Опрос: "+mDateCheck);
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

        // заполняем коллекцию групп из массива с названиями групп
        ArrayList<Map<String, String>> groupData = new ArrayList<Map<String, String>>();

        // создаем коллекцию для коллекций элементов
        ArrayList<ArrayList<Map<String, String>>> childData = new ArrayList<ArrayList<Map<String, String>>>();


        try {
            JSONObject obj = new JSONObject(json);
            JSONArray jArr = obj.getJSONArray("items");
            for (int i=0;i<jArr.length();i++){
                JSONObject quest = jArr.getJSONObject(i);
                Log.d("QA","TITEL:"+quest.get("title"));
                // заполняем список атрибутов для каждой группы
                HashMap<String, String> m = new HashMap<String, String>();
                m.put("groupName",quest.getString("title"));
                groupData.add(m);

                ArrayList<Map<String, String>> childDataItem = new ArrayList<Map<String, String>>();
                JSONArray jCheck = quest.getJSONArray("check");
                for (int j=0 ; j<jCheck.length() ; j++){
                    JSONObject checkItem = jCheck.getJSONObject(j);
                    Log.d("CI"," ITEM Title :"+checkItem.get("title"));
                    // TODO добавить убирание элемента по времени.
                    m = new HashMap<String, String>();
                    m.put("itemText",checkItem.getString("title"));
                    childDataItem.add(m);
                }
                childData.add(childDataItem);
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

    }


}
