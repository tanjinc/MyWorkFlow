package com.tanjinc.myworkflow;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.util.List;

/**
 * Created by tanjincheng on 17/7/13.
 */
public class Utils {

    private static final String TAG = "Utils";
    public static void startApp(Context context, String packet, String className) {
        Intent intent = new Intent();
        ComponentName cmp=new ComponentName(packet,className);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(cmp);
        context.startActivity(intent);
    }

    public static void startApp(Context context, Intent intent) {
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void runApp(Context context, String packageName) {
        PackageInfo pi;
        PackageManager packageManager;
        try {
            pi = context.getPackageManager().getPackageInfo(packageName, 0);
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.setPackage(pi.packageName);
            packageManager = context.getPackageManager();
            List<ResolveInfo> apps = packageManager.queryIntentActivities(
                    resolveIntent, 0);

            ResolveInfo ri = apps.iterator().next();
            if (ri != null) {
                packageName = ri.activityInfo.packageName;
                String className = ri.activityInfo.name;

                Intent intent = new Intent();

                ComponentName cn = new ComponentName(packageName, className);
                intent.setComponent(cn);
                intent.setAction("android.intent.action.VIEW");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "video runApp: ", e );
        }
    }

    /**
     * 设置录制开始,结束
     * 保存参数
     */
    public static void setAutoBoxRecording(Context context, String packetName, boolean isStart) {
        SharedPreferences preferences = context.getSharedPreferences("AutoBox", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Log.d(TAG, "video setAutoBoxRecording: packet:" + packetName + " isStart=" + isStart);
        editor.putBoolean(packetName, isStart);
        editor.apply();
    }
    /**
     * 判断是否再录制
     * @return
     */
    public static boolean isAutoBoxRecording(Context context, String packetName){
        SharedPreferences preferences = context.getSharedPreferences("AutoBox", Context.MODE_PRIVATE);
        boolean ret = preferences.getBoolean(packetName, false);
        Log.d(TAG, "video isAutoBoxRecording: packetName:" + packetName + " recording=" + ret);
        return ret;
    }
}
