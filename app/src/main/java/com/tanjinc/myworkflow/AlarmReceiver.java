package com.tanjinc.myworkflow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * 定时接收
 * Created by maxueming on 2017/7/22.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private String TAG = "AlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action != null && action.equals(Constants.ACTION.ACTION_AUTOBOX_TASK)){
            Log.d(TAG, "接收到广播");
            Bundle bundle = intent.getExtras();
            if(bundle != null){
                long startTime = bundle.getLong("startTime", 0);
                String taskName = bundle.getString("taskName");
                Log.d(TAG, "startTime = " + startTime);
                Log.d(TAG, "taskName = " + taskName);

                startTask(context, taskName);

                // 设置重复闹钟
//                AlarmSetting.getInstance().setRepeatAlarm(context,
//                        Constants.ACTION.ACTION_AUTOBOX_TASK, startTime);
            }
        }
    }

    private void startTask(Context context, String taskName) {
        Intent intent = new Intent();
        intent.putExtra("taskName", taskName);
        intent.setAction(MyWorkFlowService.AUTOBOX_START_APP);
        context.sendBroadcast(intent);
    }
}
