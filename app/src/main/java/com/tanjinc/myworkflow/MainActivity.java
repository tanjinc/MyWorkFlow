package com.tanjinc.myworkflow;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tanjinc.myworkflow.utils.XmlUtils;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

//    private MagicGroup mSettingView;
    private int i;

    private View mAddTaskBtn;
    private TaskListLayout mTaskListLayout;
    private MyWorkFlowServiceHelper mHelper;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTaskListLayout = (TaskListLayout) findViewById(R.id.task_layout);
        mTaskListLayout.setOnItemClickListener(new TaskListLayout.OnItemClickListener() {
            @Override
            public void onItemClick(TaskListLayout.TaskInfo taskInfo) {
                //执行任务
                Log.d(TAG, "video onItemClick: " + XmlUtils.readXml(taskInfo.taskName));
                startTask(taskInfo.taskName);
            }
        });

        mAddTaskBtn =  findViewById(R.id.add_btn);
        mAddTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加任务
//                mTaskListLayout.addTask(new TaskListLayout.TaskInfo("任务" + i));
//                i++;
                startActivity(new Intent(MainActivity.this, AddTaskActivity.class));
            }
        });


//        mSettingView = (MagicGroup) findViewById(R.id.setting_layout);
        mHelper = new MyWorkFlowServiceHelper(getApplicationContext());

    }


    private void startTask(String taskName) {
        Intent intent = new Intent();
        intent.putExtra("taskName", taskName);
        intent.setAction(MyWorkFlowService.AUTOBOX_START_APP);
        sendBroadcast(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    openAccessibility();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

        if (mHelper.serviceEnable(getPackageName()+"/com.tanjinc.myworkflow.MyWorkFlowService")) {
//            Toast.makeText(MainActivity.this, "服务已启动", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(MainActivity.this, "服务未启动", Toast.LENGTH_SHORT).show();
            showAccessibilityServiceEnableWarning();
        }

        mTaskListLayout.updateData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.add_btn:
//                mSettingView.addMagicView("控件 " + (i++));
                break;
        }
    }


    // 打开无障碍辅助开关，此功能需要系统签名
    private void openAccessibility(){
        int i = 0;
        if (!isAccessibilitySettingsOn()) {
            while (i < 5) {
                openAccessibilityService();
                if (!isAccessibilitySettingsOn()) {
                    openAccessibilityService();
//                    Log.d(TAG, "打开辅助开关失败");
                    i++;
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(MainActivity.this, "打开辅助开关成功", Toast.LENGTH_LONG).show();
                        }
                    });

//                    Log.d(TAG, "打开辅助开关成功");
                    return;
                }
            }
        }else{

            handler.post(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(MainActivity.this, "辅助开关状态：开启", Toast.LENGTH_LONG).show();
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


    private void showAccessibilityServiceEnableWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Please enable accessibility service")
                .setTitle("Accessibility service needed")
                .setPositiveButton("Take me to Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), 0);
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

}
