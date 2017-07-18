package com.tanjinc.myworkflow;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import java.util.List;

/**
 * Created by tanjincheng on 17/7/15.
 */
public class MyWorkFlowServiceHelper {
    private static final String TAG = "MyWorkFlowServiceHelper";
    private Context mContext;
    private AccessibilityManager mAccessibilityManager;

    MyWorkFlowServiceHelper(Context context) {
        mContext = context;
        mAccessibilityManager = (AccessibilityManager) mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
    }
    public boolean serviceEnable(String name) {
        List<AccessibilityServiceInfo> serviceInfos = mAccessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
        for (AccessibilityServiceInfo info : serviceInfos) {
            Log.d(TAG, "all -->" + info.getId());
            if (name.equals(info.getId())) {
                return true;
            }
        }
        return false;
    }

    public void notifyAccessibilityChange() {
        AccessibilityEvent event1 = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_ANNOUNCEMENT);
        mAccessibilityManager.sendAccessibilityEvent(event1);
    }

}
