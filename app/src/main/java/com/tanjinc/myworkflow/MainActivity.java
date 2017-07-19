package com.tanjinc.myworkflow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private MagicGroup mSettingView;
    private int i;

    private MyWorkFlowServiceHelper mHelper;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSettingView = (MagicGroup) findViewById(R.id.setting_layout);
        mHelper = new MyWorkFlowServiceHelper(getApplicationContext());

        if (mHelper.serviceEnable(getPackageName()+"/com.tanjinc.myworkflow.MyWorkFlowService")) {
            Toast.makeText(MainActivity.this, "服务已启动", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "服务未启动", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    openAccessbility();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
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


    // 打开无障碍辅助开关，此功能需要系统签名
    private void openAccessbility(){
        int i = 0;
        if (!isAccessibilitySettingsOn()) {
            while (i < 5) {
                openAccessibilityService();
                if (!isAccessibilitySettingsOn()) {
                    openAccessibilityService();
                    Log.d(TAG, "打开辅助开关失败");
                    i++;
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "打开辅助开关成功", Toast.LENGTH_LONG).show();
                        }
                    });

                    Log.d(TAG, "打开辅助开关成功");
                    return;
                }
            }
        }else{

            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "辅助开关状态：开启", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    private void openAccessibilityService() {
        Settings.Secure.putString(getContentResolver(), "enabled_accessibility_services", "com.meizu.testdevVideo/com.tanjinc.myworkflow.MyWorkFlowService");
        Settings.Secure.putInt(getContentResolver(), "accessibility_enabled", 1);
    }


    public boolean isAccessibilitySettingsOn() {
        int accessibilityEnabled = 0;
        String service = "com.meizu.testdevVideo/com.tanjinc.myworkflow.MyWorkFlowService";
        try {
            accessibilityEnabled = Settings.Secure.getInt(getApplicationContext().getContentResolver(), "accessibility_enabled");
        } catch (Settings.SettingNotFoundException e) {
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
        if (accessibilityEnabled != 1) {
            return false;
        }
        String settingValue = Settings.Secure.getString(getApplicationContext().getContentResolver(), "enabled_accessibility_services");
        if (settingValue == null) {
            return false;
        }
        TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
        splitter.setString(settingValue);
        while (splitter.hasNext()) {
            if (splitter.next().equalsIgnoreCase(service)) {
                return true;
            }
        }
        return false;
    }
}
