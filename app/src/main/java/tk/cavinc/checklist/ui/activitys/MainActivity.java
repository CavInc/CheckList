package tk.cavinc.checklist.ui.activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.vadel.yandexdisk.YandexDiskApi;

import java.util.ArrayList;
import java.util.Date;

import tk.cavinc.checklist.R;
import tk.cavinc.checklist.data.manager.DataManager;
import tk.cavinc.checklist.data.models.CountTimeModel;
import tk.cavinc.checklist.utils.ConstantManager;
import tk.cavinc.checklist.utils.Utils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MA";
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

    private String CLIENT_ID = "00a1d2b7031c483a892ccbef3c4bd13c";

    {
        YandexDiskApi.DEBUG = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDataManager = DataManager.getInstance();

        Date nowDate = new Date();
        mLongData = Utils.dateToStr("yyyy-MM-dd",nowDate);
        mShortData = Utils.dateToStr("dd.MM.yy",nowDate);

        mTextView = findViewById(R.id.date_tv);
        mTextView.setText(Utils.dateToStr("dd.MM.yyyy",nowDate));

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


        final YandexDiskApi api = new YandexDiskApi(CLIENT_ID);
        api.setCredentials("kotov-197", "Auryn1245");
        Log.d(TAG,"XF : "+api.isAuthorization());
        new Thread(new Runnable() {
            @Override
            public void run() {
                api.createFolder("/CheckList/");

            }
        }).start();

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

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<CountTimeModel> rec = mDataManager.getDB().getCountAll(mLongData);
    }
}
