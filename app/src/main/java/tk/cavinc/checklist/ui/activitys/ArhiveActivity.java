package tk.cavinc.checklist.ui.activitys;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

import tk.cavinc.checklist.R;
import tk.cavinc.checklist.data.manager.DataManager;
import tk.cavinc.checklist.ui.adapters.ArhiveAdapter;

public class ArhiveActivity extends AppCompatActivity {
    private DataManager mDataManager;

    private ListView mListView;
    private ArhiveAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arhive);

        mDataManager = DataManager.getInstance();

        mListView = findViewById(R.id.arhive_lv);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI(){
        ArrayList<String> data = mDataManager.getDB().getArhiveChech();
        if (mAdapter == null ){
            mAdapter = new ArhiveAdapter(this,R.layout.arhive_item,data);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }

    }
}
