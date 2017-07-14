package com.tanjinc.myworkflow;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by tanjincheng on 17/7/13.
 */
public class MyWorkFlowService extends AccessibilityService {
    private static final String TAG = "MyWorkFlowService";

    @Override
    protected void onServiceConnected() {
        Log.d(TAG, "video onServiceConnected: ");
        super.onServiceConnected();
        settingAccessibilityInfo();
    }

    private void settingAccessibilityInfo() {
        String[] packageNames = {"com.tencent.mm"};
        AccessibilityServiceInfo mAccessibilityServiceInfo = new AccessibilityServiceInfo();
        // 响应事件的类型，这里是全部的响应事件（长按，单击，滑动等）
        mAccessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        // 反馈给用户的类型，这里是语音提示
        mAccessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
        // 过滤的包名
        mAccessibilityServiceInfo.packageNames = packageNames;
        setServiceInfo(mAccessibilityServiceInfo);
    }

    private void inputClick(String clickId) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(clickId);
            for (AccessibilityNodeInfo item : list) {
                item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

//    private String findIdByText(String text) {
//        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
//        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(text);
//        return list[0];
//    }

    private void openTongxunlu() {
        Log.d(TAG, "video openTongxunlu: ");
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = nodeInfo
                    .findAccessibilityNodeInfosByText("通讯录");
            Log.d(TAG, "video openTongxunlu: list" + list.size());
            for (AccessibilityNodeInfo n : list) {
                Log.d(TAG, "video openTongxunlu: " + n.getViewIdResourceName());
                n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Log.d(TAG, "video onAccessibilityEvent: packetname = " + accessibilityEvent.getPackageName());
        int eventType = accessibilityEvent.getEventType();
        String eventText = "";
        switch (eventType) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                Log.d(TAG, "==============Start====================");
                eventText = "TYPE_VIEW_CLICKED";
                AccessibilityNodeInfo noteInfo = accessibilityEvent.getSource();
                Log.d(TAG, noteInfo.toString());
                Log.d(TAG, "=============END=====================");
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                eventText = "TYPE_VIEW_FOCUSED";
                break;
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                eventText = "TYPE_VIEW_LONG_CLICKED";
                break;
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                eventText = "TYPE_VIEW_SELECTED";
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                eventText = "TYPE_VIEW_TEXT_CHANGED";
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                eventText = "TYPE_WINDOW_STATE_CHANGED";
                break;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                eventText = "TYPE_NOTIFICATION_STATE_CHANGED";
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END:
                eventText = "TYPE_TOUCH_EXPLORATION_GESTURE_END";
                break;
            case AccessibilityEvent.TYPE_ANNOUNCEMENT:
                eventText = "TYPE_ANNOUNCEMENT";
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:
                eventText = "TYPE_TOUCH_EXPLORATION_GESTURE_START";
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
                eventText = "TYPE_VIEW_HOVER_ENTER";
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
                eventText = "TYPE_VIEW_HOVER_EXIT";
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                eventText = "TYPE_VIEW_SCROLLED";
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                eventText = "TYPE_VIEW_TEXT_SELECTION_CHANGED";
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                openTongxunlu();
                eventText = "TYPE_WINDOW_CONTENT_CHANGED";
                break;
        }
        Log.d(TAG, "video onAccessibilityEvent: eventText = " + eventText);
    }

    @Override
    public void onInterrupt() {

    }
}
