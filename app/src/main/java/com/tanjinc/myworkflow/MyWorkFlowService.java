package com.tanjinc.myworkflow;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tanjincheng on 17/7/13.
 */
public class MyWorkFlowService extends AccessibilityService {
    private static final String TAG = "MyWorkFlowService";

    private String packetName = null;
    private HashMap<String, Long> mNodeMap = new HashMap<>();

    @Override
    protected void onServiceConnected() {
        Log.d(TAG, "video onServiceConnected: ");
        super.onServiceConnected();
        settingAccessibilityInfo();
        packetName = getPackageName();
    }

    private void settingAccessibilityInfo() {
        String[] packageNames = {"com.tanjinc.myworkflow", "com.tencent.mm"};
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
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return super.bindService(service, conn, flags);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        int eventType = accessibilityEvent.getEventType();
        String packetName = String.valueOf(accessibilityEvent.getPackageName());
        String eventText = "";
        switch (eventType) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                Log.d(TAG, "==============Start====================");
                eventText = "TYPE_VIEW_CLICKED";
                long id = getSourceNodeId(accessibilityEvent);
                Log.d(TAG, "onclick id = " + id) ;
                mNodeMap.put(packetName, id);
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
//                AccessibilityNodeInfo noteInfo = getRootInActiveWindow();
//                if (noteInfo != null) {
//                    Log.d(TAG, noteInfo.toString());
//                    Log.d(TAG, "video onAccessibilityEvent: " + noteInfo.findAccessibilityNodeInfosByText("通讯录"));
//                    List<AccessibilityNodeInfo> list = noteInfo.findAccessibilityNodeInfosByViewId("btn1");
//                    for (AccessibilityNodeInfo n : list) {
//                        Log.d(TAG, "video onAccessibilityEvent: id name =" + n.getViewIdResourceName());
//                        n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                    }
//                }
                if (packetName != null && packetName.equals("com.tencent.mm") && mNodeMap != null) {
                    Log.d(TAG, "video onAccessibilityEvent: id == " + mNodeMap.get(packetName));
                }

                eventText = "TYPE_WINDOW_CONTENT_CHANGED";
                break;
        }
        Log.d(TAG, "video onAccessibilityEvent: eventText = " + eventText);
    }

    @Override
    public void onInterrupt() {

    }

    private Method getSourceNodeIdMethod;
    private long mLastSourceNodeId;
    private long mLastClickTime;

    private long getSourceNodeId(AccessibilityEvent event)  {
        if (getSourceNodeIdMethod==null) {
            Class<AccessibilityEvent> eventClass = AccessibilityEvent.class;
            try {
                getSourceNodeIdMethod = eventClass.getMethod("getSourceNodeId");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        if (getSourceNodeIdMethod!=null) {
            try {
                return (long) getSourceNodeIdMethod.invoke(event);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    private void doClick(long id) {
        Log.d(TAG, "video doClick: id=" + id);
    }

}
