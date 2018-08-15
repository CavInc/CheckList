package tk.cavinc.checklist.ui.activitys;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import tk.cavinc.checklist.R;
import tk.cavinc.checklist.data.manager.DataManager;
import tk.cavinc.checklist.data.models.ArhiveHeadModel;
import tk.cavinc.checklist.data.models.ArhiveModel;
import tk.cavinc.checklist.ui.adapters.ArhiveAdapter;
import tk.cavinc.checklist.utils.PrepareArhiveData;
import tk.cavinc.checklist.utils.StoreXlsFile;

/*
https://automated-testing.info/t/api-dlya-raboty-s-excel/1563/2
http://romanchekashov.blogspot.com/2014/09/create-excel-file-in-java.html
https://www.javaworld.com/article/2074940/learn-java/java-app-dev-reading-and-writing-excel-spreadsheets.html
 */

public class ArhiveActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private DataManager mDataManager;

    private ListView mListView;
    private ArhiveAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arhive);

        mDataManager = DataManager.getInstance();

        mListView = findViewById(R.id.arhive_lv);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setOnItemClickListener(this);

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
        }
        return true;
    }


    // создаем xls из отмеченных архивов
    private void createXls() {
        for (int i = 0 ; i<mAdapter.getCount();i++){
            ArhiveModel model = mAdapter.getItem(i);
            if (model.isCheck()) {
                Log.d("AA","Arhive Name :"+model.getTitle());
                ArrayList<ArhiveHeadModel> prepareData = new PrepareArhiveData(model.getTitle()).get();

            }
        }

        new StoreXlsFile(this,mDataManager.getStorageAppPath(),"text.xls").write();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI(){
        ArrayList<ArhiveModel> data = mDataManager.getDB().getArhiveChech();
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
}
