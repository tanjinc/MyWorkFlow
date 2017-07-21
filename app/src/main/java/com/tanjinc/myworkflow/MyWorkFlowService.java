package com.tanjinc.myworkflow;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by tanjincheng on 17/7/13.
 */
public class MyWorkFlowService extends AccessibilityService {
    private static final String TAG = "MyWorkFlowService";

    public static final String AUTOBOX_START_APP = "autobox_start_app";
    public static String CurFocused = "";
    public static String CurNotification = "";
    static AccessibilityNodeInfo rowNode = null;
    ArrayList<AccessibilityNodeInfo> infoView = new ArrayList();
    ArrayList<AccessibilityNodeInfo> recordView = new ArrayList();

    private String packetName = null;
    private HashMap<String, Long> mNodeMap = new HashMap<>();
    private Handler handler;



    @Override
    protected void onServiceConnected() {
        Log.d(TAG, "video onServiceConnected: ");
        super.onServiceConnected();
        settingAccessibilityInfo();
        packetName = getPackageName();
        ViewManager.getInstance(this).showFloatBall();
        ViewManager.getInstance(MyWorkFlowService.this).isRecord(true);
        handler = new Handler();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AUTOBOX_START_APP);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }


    private void doAutoTask(String taskName) {
        AutoTaskBean autoTaskBean = XmlUtils.readXml(taskName);
        Utils.runApp(getApplicationContext(), autoTaskBean.packetName);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (AutoTaskNodeBean caseDetail : autoTaskBean.getNodeArray()){
            runAutoCase(caseDetail, true, 5000);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "video onReceive: action = " + action);
            if (action.equals(AUTOBOX_START_APP)) {
                String taskName = intent.getStringExtra("taskName");
                doAutoTask(taskName);
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected AccessibilityNodeInfo getRootInWindow() {
        AccessibilityNodeInfo rootNode = null;
        for (int x = 0; x < 4; x++) {
            rootNode = getRootInActiveWindow();
            if (rootNode != null) {
                return rootNode;
            }
            if (x < 3) {
                SystemClock.sleep(250);
            }
        }
        return rootNode;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void performAction(int model) {
        performGlobalAction(model);
    }


    private void settingAccessibilityInfo() {
//        String[] packageNames = {"com.tanjinc.myworkflow", "com.tencent.mm"};
        AccessibilityServiceInfo mAccessibilityServiceInfo = new AccessibilityServiceInfo();
        // 响应事件的类型，这里是全部的响应事件（长按，单击，滑动等）
        mAccessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        // 反馈给用户的类型，这里是语音提示
        mAccessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
        // 过滤的包名
//        mAccessibilityServiceInfo.packageNames = packageNames;
        mAccessibilityServiceInfo.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
        setServiceInfo(mAccessibilityServiceInfo);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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

    private long id;
    private String recordPacketName; //当前录制的视频包名
    private ArrayList<AutoTaskNodeBean> mActionArray = new ArrayList();
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        int eventType = accessibilityEvent.getEventType();
        packetName = String.valueOf(accessibilityEvent.getPackageName());
        String eventText = "";
        switch (eventType) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                Log.d(TAG, "==============Start====================");
                eventText = "TYPE_VIEW_CLICKED";
//                id = getSourceNodeId(accessibilityEvent);
//                Log.d(TAG, "onclick id = " + id) ;
//                mNodeMap.put(packetName, id);

                // 获取到ID或索引值，就可以进行点击

                if (Utils.isAutoBoxRecording(getApplicationContext(), packetName)) {
                    AutoTaskNodeBean msg = new AutoTaskNodeBean();
                    recordView.clear();    // 暂存值，防止这一过程页面变化点击过快，infoView再出什么变化
                    recordView.addAll(infoView);

                    msg.setActionType("click");
                    msg.setId(getViewIdBySearchAllView(accessibilityEvent.getSource(), recordView));
                    msg.setIdInstance(getViewInstance(msg.getId(), accessibilityEvent.getSource(),
                            WidgetType.ID, recordView));
                    msg.setText(String.valueOf(accessibilityEvent.getSource().getText()));
                    msg.setTextInstance(getViewInstance(msg.getText(), accessibilityEvent.getSource(),
                            WidgetType.TEXT, recordView));

                    msg.setClazz((String) accessibilityEvent.getSource().getClassName());
                    msg.setClazzInstance(getViewInstance(msg.getClazz(), accessibilityEvent.getSource(),
                            WidgetType.CLASS, recordView));
                    msg.setContent((String) accessibilityEvent.getSource().getContentDescription());
                    msg.setContentInstance(getViewInstance(msg.getContent(), accessibilityEvent.getSource(),
                            WidgetType.CONTENT, recordView));


                    mActionArray.add(msg);

                    Log.d(TAG, "onclick :" + msg.toString());

                }
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
                if (Utils.isAutoBoxRecording(getApplicationContext(), packetName)) {
                    AutoTaskNodeBean msg = new AutoTaskNodeBean();
                    recordView.clear();    // 暂存值，防止这一过程页面变化点击过快，infoView再出什么变化
                    recordView.addAll(infoView);

                    msg.setActionType("inputText");
                    msg.setInputText(String.valueOf(accessibilityEvent.getSource().getText()));


                    msg.setId(getViewIdBySearchAllView(accessibilityEvent.getSource(), recordView));
                    msg.setIdInstance(getViewInstance(msg.getId(), accessibilityEvent.getSource(),
                            WidgetType.ID, recordView));

                    msg.setText(null);
                    msg.setTextInstance(0);

                    msg.setClazz((String) accessibilityEvent.getSource().getClassName());
                    msg.setClazzInstance(getViewInstance(msg.getClazz(), accessibilityEvent.getSource(),
                            WidgetType.CLASS, recordView));

                    msg.setContent((String) accessibilityEvent.getSource().getContentDescription());
                    msg.setContentInstance(getViewInstance(msg.getContent(), accessibilityEvent.getSource(),
                            WidgetType.CONTENT, recordView));


                    mActionArray.add(msg);

                    Log.d(TAG, "inputText :" + msg.toString());
                }

                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                eventText = "TYPE_WINDOW_STATE_CHANGED";

                if (!packetName.equals(recordPacketName)) {
                    //如果当前包名不等于录制包,则判断录制结束
                    if (Utils.isAutoBoxRecording(getApplicationContext(), recordPacketName)) {
                        //录制结束。保存到xml
                        if (mActionArray.size() >0) {
                            AutoTaskBean autoTaskBean = new AutoTaskBean();
                            autoTaskBean.setTaskName("任务:" + recordPacketName);
                            autoTaskBean.setPacketName(recordPacketName);
                            autoTaskBean.setNodeArray(mActionArray);
                            XmlUtils.saveXml(recordPacketName+".xml", autoTaskBean);
                            mActionArray.clear();
                        }
                        Utils.setAutoBoxRecording(getApplicationContext(), recordPacketName, false);
                    }
                    recordPacketName = packetName;
                }



                break;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                eventText = "TYPE_NOTIFICATION_STATE_CHANGED";
//                if (mActionArray.size() >0) {
//                    XmlUtils.saveXml(packetName+".xml", packetName, mActionArray);
//                }
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
                getrowNode();
                eventText = "TYPE_WINDOW_CONTENT_CHANGED";
                break;
        }
        Log.d(TAG, "video onAccessibilityEvent: packet=" + packetName +"eventText = " + eventText);


    }

    /**
     * 选择操作函数
     * @param nodeBean 节点记录值
     * @param info 节点node信息
     */
    private void chooseOperation(AutoTaskNodeBean nodeBean, AccessibilityNodeInfo info){
        if(nodeBean.getActionType().equals("click")){
            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }else if(nodeBean.getActionType().equals("inputText")){
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    nodeBean.getInputText());
            info.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
        }
    }

    /**
     * 执行函数
     * @param nodeBean 节点储存信息
     */
    private void runAutoCase(AutoTaskNodeBean nodeBean, boolean isFreshWindowView, long waitViewTime){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long currentTime = System.currentTimeMillis();
        boolean isViewVisiable = false;
        while (System.currentTimeMillis() - currentTime < waitViewTime){
            int count = 0;
            String keyValue;
            if(isFreshWindowView){
                getrowNode();
            }

            if(nodeBean.getActionType().equals("click")){
                if(isAvaliable(nodeBean.getText())){
                    for(AccessibilityNodeInfo info : infoView){
                        keyValue = String.valueOf(info.getText()) ;
                        if(keyValue != null && keyValue.equals(nodeBean.getText())){
                            ++count;
                        }

                        if(count == nodeBean.getTextInstance()){
                            Log.d(TAG, "执行" + info + "=============" + nodeBean.getText());
                            chooseOperation(nodeBean, info);
                            isViewVisiable = true;
                            break;
                        }
                    }

                    if(isViewVisiable){
                        break;
                    }
                }

                if(isAvaliable(nodeBean.getId())){
                    for(AccessibilityNodeInfo info : infoView){
                        keyValue = info.getViewIdResourceName();
                        if(keyValue != null && keyValue.equals(nodeBean.getId())){
                            ++count;
                        }

                        if(count == nodeBean.getIdInstance()){
                            Log.d(TAG, "执行" + info + "=============" + nodeBean.getId());
                            chooseOperation(nodeBean, info);
                            isViewVisiable = true;
                            break;
                        }
                    }
                    if(isViewVisiable){
                        break;
                    }
                }

                if(isAvaliable(nodeBean.getContent())){
                    for(AccessibilityNodeInfo info : infoView){
                        keyValue = (String) info.getContentDescription();
                        if(keyValue != null && keyValue.equals(nodeBean.getContent())){
                            ++count;
                        }

                        if(count == nodeBean.getContentInstance()){
                            Log.d(TAG, "执行" + info + "=============" + nodeBean.getContent());
                            chooseOperation(nodeBean, info);
                            isViewVisiable = true;
                            break;
                        }
                    }
                    if(isViewVisiable){
                        break;
                    }
                }

                if(isAvaliable(nodeBean.getClazz())){
                    for(AccessibilityNodeInfo info : infoView){
                        keyValue = (String) info.getClassName();
                        if(keyValue != null && keyValue.equals(nodeBean.getClazz())){
                            ++count;
                        }

                        if(count == nodeBean.getClazzInstance()){
                            Log.d(TAG, "执行" + info + "=============" + nodeBean.getClazz());
                            chooseOperation(nodeBean, info);
                            isViewVisiable = true;
                            break;
                        }
                    }
                    if(isViewVisiable){
                        break;
                    }
                }
            }

            if(nodeBean.getActionType().equals("inputText") && isAvaliable(nodeBean.getClazz())){
                for(AccessibilityNodeInfo info : infoView){
                    keyValue = (String) info.getClassName();
                    if(keyValue != null && keyValue.equals(nodeBean.getClazz())){
                        ++count;
                    }

                    if(count == nodeBean.getClazzInstance()){
                        Log.d(TAG, "执行" + info + "=============" + nodeBean.getClazz());
                        chooseOperation(nodeBean, info);
                        isViewVisiable = true;
                        break;
                    }
                }
                if(isViewVisiable){
                    break;
                }
            }
        }
    }

    private boolean isAvaliable(String str) {
        return str != null && !str.equals("null") && !str.equals("");
    }


    boolean isRunning;
    private synchronized String getViewIdBySearchAllView(AccessibilityNodeInfo accessibilityNodeInfo, ArrayList<AccessibilityNodeInfo> list){
        Rect srcBounds = new Rect();
        Rect compareBounds = new Rect();
        accessibilityNodeInfo.refresh();
        accessibilityNodeInfo.getBoundsInParent(compareBounds);
        Log.d(TAG, "控件坐标为:" + compareBounds.left  + "  " + compareBounds.right + "  " + compareBounds.bottom + "  " + compareBounds.top);

        Log.d(TAG, "video getViewIdBySearchAllView: " + accessibilityNodeInfo.toString());

        for(AccessibilityNodeInfo info : list){
            info.getBoundsInScreen(srcBounds);
            if(compareBounds.left == srcBounds.left
                    && compareBounds.right == srcBounds.right
                    && compareBounds.bottom == srcBounds.bottom
                    && compareBounds.top == srcBounds.top){
                Log.d(TAG, "重新查找得控件的ID:" + info.getViewIdResourceName());
                return info.getViewIdResourceName();
            }
        }
        return null;
    }

    private enum WidgetType{
        ID, TEXT, CLASS, CONTENT
    }



    /**
     * key值：ID名，TEXT，CLASS名或CONTENT名
     * @param key
     * @param accessibilityNodeInfo
     * @param type
     * @param list
     * @return
     */
    private synchronized int getViewInstance(String key, AccessibilityNodeInfo accessibilityNodeInfo, Enum type, ArrayList<AccessibilityNodeInfo> list){
        if(key == null){
            return 0;
        }
        int instance = 0;
        Rect srcBounds = new Rect();
        Rect compareBounds = new Rect();
        accessibilityNodeInfo.getBoundsInScreen(compareBounds);
        for(AccessibilityNodeInfo info : list){
            info.getBoundsInScreen(srcBounds);
            if(type == WidgetType.ID){
                String id = info.getViewIdResourceName();
                if(id != null && id.equals(key)){
                    ++instance;
                }
            }else if(type == WidgetType.TEXT){
                String text = info.getText() != null  ? info.getText().toString() : null;
                if(text != null && text.equals(key)){
                    ++instance;
                }
            }else if(type == WidgetType.CLASS){
                String clazz = (String)info.getClassName();
                if(clazz != null && clazz.equals(key)){
                    ++instance;
                }
            }else if(type == WidgetType.CONTENT){
                String content = (String) info.getContentDescription();
                if(content != null && content.equals(key)){
                    ++instance;
                }
            }

            if(compareBounds.left == srcBounds.left && compareBounds.right == srcBounds.right
                    && compareBounds.bottom == srcBounds.bottom && compareBounds.top == srcBounds.top){
                break;
            }
        }
        return instance;

    }

    @Override
    public void onInterrupt() {

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public ArrayList<AccessibilityNodeInfo> getrowNode() {
        for (int x = 0; x < 4; x++) {
            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
            if (rootNode != null) {
                this.infoView.clear();
                recycle(rootNode);
                return this.infoView;
            }
            if (x < 3) {
                SystemClock.sleep(250);
            }
        }
        return null;
    }

    public void recycle(AccessibilityNodeInfo info) {
        if (info.getChildCount() != 0) {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    this.infoView.add(info.getChild(i));
                    recycle(info.getChild(i));
//                    Log.d(TAG, "======================start=====================");
//                    Log.d(TAG, "控件名称:" + info.getChild(i).getClassName());
//                    Log.d(TAG, "控件中的值：" + info.getChild(i).getText());
//                    Log.d(TAG, "控件的ID:" + info.getChild(i).getViewIdResourceName());
//                    Log.d(TAG, "点击是否出现弹窗:" + info.getChild(i).canOpenPopup());
//                    Rect outBounds = new Rect();
//                    info.getChild(i).getBoundsInScreen(outBounds);
//                    Log.d(TAG, "坐标:" + outBounds.left  + "  " + outBounds.right + "  " + outBounds.bottom + "  " + outBounds.top);
//                    Log.d(TAG, "======================end=====================");
                }
            }
        }
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

    private String getViewSourceNodeId(AccessibilityNodeInfo event)  {
        if (getSourceNodeIdMethod==null) {
            Class<AccessibilityNodeInfo> eventClass = AccessibilityNodeInfo.class;
            try {
                getSourceNodeIdMethod = eventClass.getMethod("getViewIdResourceName");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        if (getSourceNodeIdMethod!=null) {
            try {
                return (String) getSourceNodeIdMethod.invoke(event);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void doClick(long id) {
        Log.d(TAG, "video doClick: id=" + id);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean touchTextByIndex(String text, int index) {
        int mIndex = 1;
        try {
            if (this.infoView == null) {
                return false;
            }
            for (int i = 0; i < this.infoView.size(); i++) {
                AccessibilityNodeInfo infoViewChild = (AccessibilityNodeInfo) this.infoView.get(i);
                CharSequence c = infoViewChild.getText();
                if (infoViewChild.isVisibleToUser() && c != null && c.toString().equals(text)) {
                    if (mIndex == index) {
                        Rect nodeRect = new Rect();
                        infoViewChild.getBoundsInScreen(nodeRect);
                        ShellUtils.execCommand("input tap " + nodeRect.centerX() + " " + nodeRect.centerY(), false, true);
                        return true;
                    }
                    mIndex++;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean touchIdByIndex(String viewIdName, int index) {
        try {
            if (this.infoView == null) {
                return false;
            }
            for (int i = 0; i < this.infoView.size(); i++) {
                int mIndex = 1;
                AccessibilityNodeInfo parentInfo = ((AccessibilityNodeInfo) this.infoView.get(i)).getParent();
                if (parentInfo != null) {
                    List<AccessibilityNodeInfo> findInfo = parentInfo.findAccessibilityNodeInfosByViewId(viewIdName);
                    if (findInfo.size() >= index) {
                        for (int j = 0; j < findInfo.size(); j++) {
                            if (((AccessibilityNodeInfo) findInfo.get(j)).isVisibleToUser()) {
                                if (mIndex == index) {
                                    Rect nodeRect = new Rect();
                                    ((AccessibilityNodeInfo) findInfo.get(j)).getBoundsInScreen(nodeRect);
                                    ShellUtils.execCommand("input tap " + nodeRect.centerX() + " " + nodeRect.centerY(), false, true);
                                    return true;
                                }
                                mIndex++;
                            }
                        }
                        continue;
                    } else {
                        continue;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ViewManager.getInstance(this).hideFloatBall();
        unregisterReceiver(mBroadcastReceiver);
    }
}
