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
                String xmlName = bundle.getString("xmlName");
                Log.d(TAG, "startTime = " + startTime);
                Log.d(TAG, "xmlName = " + xmlName);
                // 设置重复闹钟
                AlarmSetting.getInstance().setRepeatAlarm(context,
                        Constants.ACTION.ACTION_AUTOBOX_TASK, startTime);
            }
        }
    }
}
