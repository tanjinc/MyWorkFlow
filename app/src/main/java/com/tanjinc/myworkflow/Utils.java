package com.tanjinc.myworkflow;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by tanjincheng on 17/7/13.
 */
public class Utils {

    public static void startWeixin(Context context) {
        Intent intent = new Intent();
        ComponentName cmp=new ComponentName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(cmp);
        context.startActivity(intent);
    }
}
