package tk.cavinc.checklist.ui.activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

import tk.cavinc.checklist.R;
import tk.cavinc.checklist.data.manager.DataManager;
import tk.cavinc.checklist.utils.ConstantManager;
import tk.cavinc.checklist.utils.Utils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private DataManager mDataManager;

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDataManager = DataManager.getInstance();

        Date nowDate = new Date();
        mTextView = findViewById(R.id.date_tv);
        mTextView.setText(Utils.dateToStr("dd.MM.yyyy",nowDate));


        findViewById(R.id.bt0900).setOnClickListener(this);
        findViewById(R.id.bt1300).setOnClickListener(this);
        findViewById(R.id.bt1700).setOnClickListener(this);
        findViewById(R.id.bt2100).setOnClickListener(this);
        findViewById(R.id.bt0100).setOnClickListener(this);
        findViewById(R.id.bt0500).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Button bt = (Button) v;
        String cp = bt.getText().toString();
        Object tag = bt.getTag();

        Intent intent = new Intent(this,QuestionActivity.class);
        intent.putExtra(ConstantManager.WORK_DATA,mTextView.getText().toString());
        intent.putExtra(ConstantManager.WORK_TIME,cp);
        intent.putExtra(ConstantManager.WORK_ID_TAG,tag.toString());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_arhive) {
            Intent intent = new Intent(this,ArhiveActivity.class);
            startActivity(intent);
        }
        return true;
    }
}
