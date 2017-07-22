package com.tanjinc.myworkflow;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "AddTaskActivity";

    private View mCancelBtn;
    private View mConfireBtn;
    private EditText mEditText;
    private View mRecordingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        mEditText = (EditText) findViewById(R.id.task_name_et);
    }

    private void openAppDialog() {
        Constants.recordTaskName = mEditText.getText().toString();
        Log.d(TAG, "video openAppDialog recordTaskName : " + Constants.recordTaskName);
        AppListFragment appDialog = new AppListFragment();
        appDialog.show(getFragmentManager(),"Dialog");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_btn:
                finish();
                break;
            case R.id.finish_btn:
                finish();
                break;
            case R.id.record_action_btn:
                openAppDialog();
                break;
            case R.id.alarm_btn:
                showTimeDialog();
                break;
            default:
                break;
        }
    }

    private void showTimeDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(AddTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // 设置定时任务
                long startTime = (hourOfDay * Constants.TIME.MINUTES_OF_HOUR + minute)
                        * Constants.TIME.SECONDS_OF_MINUTE * Constants.TIME.MILLS_OF_SECOND;
                AlarmSetting.getInstance().setRepeatAlarm(getApplicationContext(),
                        Constants.ACTION.ACTION_AUTOBOX_TASK, startTime);
            }
        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);
        timePickerDialog.setTitle("选择定时时间");
        timePickerDialog.show();
    }
}
