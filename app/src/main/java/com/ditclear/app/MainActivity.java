package com.ditclear.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    PickerDialog mSingleDialog, mLimitDialog, mNoLimitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSingleDialog = PickerDialog.newInstance(1);
        mLimitDialog = PickerDialog.newInstance(3);
        mNoLimitDialog = PickerDialog.newInstance(-1);
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
}
