package com.tanjinc.myworkflow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private MagicGroup mSettingView;
    private int i;

    private MyWorkFlowServiceHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSettingView = (MagicGroup) findViewById(R.id.setting_layout);
        mHelper = new MyWorkFlowServiceHelper(getApplicationContext());
        if (mHelper.serviceEnable(getPackageName()+"/.MyWorkFlowService")) {
            Toast.makeText(MainActivity.this, "服务已启动", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "服务未启动", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn1:

                // 1. 打开一个应用
                // 2. 在应用中执行某些操作
//                Utils.runApp(getApplicationContext(), "com.meizu.media.video");
                AppListFragment appDialog = new AppListFragment();
                appDialog.show(getFragmentManager(),"Dialog");

                break;

            case R.id.add_btn:
//                mSettingView.addMagicView("控件 " + (i++));
                mHelper.notifyAccessibilityChange();
                break;
        }
        Toast.makeText(MainActivity.this, " id click: " + view.getId(), Toast.LENGTH_SHORT).show();
    }
}
