package tk.cavinc.checklist.ui.activitys;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import tk.cavinc.checklist.ui.fragments.PrefFragment;

/**
 * Created by cav on 09.10.18.
 */

public class PrefeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_pref);

        setToolBar();

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefFragment()).commit();
    }

    private void setToolBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
