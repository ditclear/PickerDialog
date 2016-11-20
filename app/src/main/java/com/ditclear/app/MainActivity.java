package com.ditclear.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements PickerDialog.OnSelectedListener{

    PickerDialog mSingleDialog, mLimitDialog, mNoLimitDialog;
    TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView= (TextView) findViewById(R.id.selected_tv);
        mSingleDialog = PickerDialog.newInstance(1,"单位", new PickerItem.PickerItems().getList());
        mLimitDialog = PickerDialog.newInstance(3,"数量", new PickerItem.PickerItems().getList());
        mNoLimitDialog = PickerDialog.newInstance(-1,"名称", new PickerItem.PickerItems().getList());

        mSingleDialog.setOnSelectedListener(this);
        mLimitDialog.setOnSelectedListener(this);
        mNoLimitDialog.setOnSelectedListener(this);
    }

    public void pick1(View v) {
        mSingleDialog.show(getSupportFragmentManager(), "single");
    }

    public void pick2(View v) {
        mLimitDialog.show(getSupportFragmentManager(), "limit");
    }

    public void pick3(View v) {
        mNoLimitDialog.show(getSupportFragmentManager(), "nolimit");
    }

    @Override
    public void onSelected(List<? extends IContent> pos) {
        String content="已选择：\n";
        for (IContent c: pos) {
            content+="<"+c.getDesc()+">";
        }
        mTextView.setText(content);
    }
}
