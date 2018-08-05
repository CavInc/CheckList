package tk.cavinc.checklist.ui.activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
        Intent intent = new Intent(this,QuestionActivity.class);
        intent.putExtra(ConstantManager.WORK_DATA,mTextView.getText().toString());
        startActivity(intent);
    }
}
