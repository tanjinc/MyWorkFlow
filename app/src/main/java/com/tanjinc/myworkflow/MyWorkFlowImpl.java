package com.tanjinc.myworkflow;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;
import android.view.accessibility.AccessibilityNodeInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by maxueming on 2017/7/17.
 */

public class MyWorkFlowImpl implements MeizuApi {
    private static String BUILD_MODEL = ShellUtils.execCommand("getprop ro.build.inside.id", false, true).successMsg.trim();
    private static String CURRENT_FPS = "";
    public static String DEVICENAME = "";
    public static String FILE_NAME = "Error";
    private static int GET_X_COORDINATE = 0;
    private static int GET_Y_COORDINATE = 0;
    private static int LOG_RUN_TIME = 100;
    private static String MEM_AVAI = "";
    private static String PROCESS_MEM = "";
    private static String SDCARD_PATH_NAME = Environment.getExternalStorageDirectory().getPath();
    private static int SLEEP_WAIT_TIME = 500;
    private static String TAG = "MeizuUser";
    private static Boolean isFindedLogText = Boolean.valueOf(false);
    private static boolean isFisrtCheckDevices = true;
    private static boolean isHanderRuning = false;
    private static boolean isItemCreated = false;
    private static boolean isProessRuning = true;
    public static MyWorkFlowService mCatchNodeWindows;
    private static RemoteService monitorActivity;
    private static ArrayList<AccessibilityNodeInfo> recycleInfoView = new ArrayList();
    String CURRENT_MODEL = "";
    String DEVICE_NAME = "";
    private int HANDER_WAIT_TIME = 100;
    String LOGING_TEXT = "";
    String PACKAGE_NAME = "";
    String RUNING_LOG = "";
    String TEST_STEP = "";
    StringBuffer fpsCou = new StringBuffer();
    Runnable fpsMem = new Runnable() {
        public void run() {
            while (MyWorkFlowImpl.monitorActivity.serviceRun.booleanValue() && MyWorkFlowImpl.isHanderRuning) {
                MyWorkFlowImpl.this.updateFlingerFpsCounter();
                MyWorkFlowImpl.this.fillMemoryMonitorItems(MyWorkFlowImpl.this.PACKAGE_NAME);
                MyWorkFlowImpl.this.fpsCou.append(MyWorkFlowImpl.CURRENT_FPS + ",");
                MyWorkFlowImpl.this.memCou.append(MyWorkFlowImpl.MEM_AVAI + ",");
                MyWorkFlowImpl.this.processMemCou.append(MyWorkFlowImpl.PROCESS_MEM + ",");
                AutoInterface.sleep(MyWorkFlowImpl.this.HANDER_WAIT_TIME);
            }
        }
    };
    int intervalTime = 0;
    private Thread mThread;
    StringBuffer memCou = new StringBuffer();
    StringBuffer processMemCou = new StringBuffer();
    Runnable stop = new Runnable() {
        public void run() {
            BufferedReader bufferedReader;
            Exception e;
            try {
                Process mLogcatProc = Runtime.getRuntime().exec("logcat -v time");
                BufferedReader reader = new BufferedReader(new InputStreamReader(mLogcatProc.getInputStream()));
                try {
                    long startTimeMillis = System.currentTimeMillis();
                    while (MyWorkFlowImpl.isProessRuning) {
                        String line = reader.readLine();
                        if (line != null) {
                            MyWorkFlowImpl MyWorkFlowImpl;
                            if (line.contains(MyWorkFlowImpl.this.RUNING_LOG)) {
                                MyWorkFlowImpl = MyWorkFlowImpl.this;
                                MyWorkFlowImpl.LOGING_TEXT += line + ShellUtils.COMMAND_LINE_END;
                                MyWorkFlowImpl.isProessRuning = false;
                                MyWorkFlowImpl.isFindedLogText = Boolean.valueOf(true);
                                break;
                            }
                            MyWorkFlowImpl = MyWorkFlowImpl.this;
                            MyWorkFlowImpl.LOGING_TEXT += line;
                            if (System.currentTimeMillis() - startTimeMillis >= ((long) MyWorkFlowImpl.LOG_RUN_TIME)) {
                                MyWorkFlowImpl = MyWorkFlowImpl.this;
                                MyWorkFlowImpl.LOGING_TEXT += line + ShellUtils.COMMAND_LINE_END;
                                MyWorkFlowImpl.isProessRuning = false;
                                break;
                            }
                        }
                    }
                    reader.close();
                    mLogcatProc.destroy();
                    bufferedReader = reader;
                } catch (Exception e2) {
                    e = e2;
                    bufferedReader = reader;
                }
            } catch (Exception e3) {
                e = e3;
                e.printStackTrace();
            }
        }
    };

    public MyWorkFlowImpl(MyWorkFlowService MyWorkFlowService) {
        mCatchNodeWindows = MyWorkFlowService;
        Log.e("CurMeizuUser version:", "4.1-----------------------------");
        checkDevices();
        File file = new File(SDCARD_PATH_NAME + "/AutoTest/image");
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public MyWorkFlowImpl(RemoteService RemoteService) {
        monitorActivity = RemoteService;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected static AccessibilityNodeInfo getRootInWindow() {
        return mCatchNodeWindows.getRootInWindow();
    }

    protected static synchronized ArrayList<AccessibilityNodeInfo> recycle(AccessibilityNodeInfo info) {
        ArrayList<AccessibilityNodeInfo> arrayList;
        synchronized (MyWorkFlowImpl.class) {
            if (!(info.getChildCount() == 0 || info.toString().isEmpty())) {
                for (int i = 0; i < info.getChildCount(); i++) {
                    if (info.getChild(i) != null) {
                        recycle(info.getChild(i));
                        recycleInfoView.add(info.getChild(i));
                    }
                }
            }
            arrayList = recycleInfoView;
        }
        return arrayList;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected ArrayList<AccessibilityNodeInfo> getNewInfoView() {
        AccessibilityNodeInfo nodeInfo = getRootInWindow();
        if (nodeInfo == null) {
            return null;
        }
        recycleInfoView.clear();
        return recycle(nodeInfo);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected static AccessibilityNodeInfo getIndex(List<AccessibilityNodeInfo> nodeList, int index) {
        int mIndex = 1;
        AccessibilityNodeInfo mNode = null;
        int searchIndex = nodeList.size();
        if (nodeList != null && !nodeList.isEmpty() && searchIndex >= index) {
            for (int i = 0; i < nodeList.size(); i++) {
                mNode = (AccessibilityNodeInfo) nodeList.get(i);
                if (mNode != null && mNode.isVisibleToUser()) {
                    if (mIndex == index) {
                        break;
                    }
                    mIndex++;
                }
            }
        }
        return mNode;
    }

    protected static void getXY(String nodeText) {
        String[] text = nodeText.split(";");
        for (int i = 0; i < text.length; i++) {
            if (text[i].contains("boundsInScreen")) {
                text[i] = text[i].substring(text[i].indexOf("(") + 1, text[i].lastIndexOf(")"));
                text[i] = text[i].replace("-", ",");
                text[i] = text[i].replace(" ", "");
                String[] xy = text[i].split(",");
                GET_X_COORDINATE = (Integer.parseInt(xy[0]) + Integer.parseInt(xy[2])) / 2;
                GET_Y_COORDINATE = (Integer.parseInt(xy[1]) + Integer.parseInt(xy[3])) / 2;
                return;
            }
        }
    }

    protected void touchContorl(AccessibilityNodeInfo nodeInfo, int addX, int addY, boolean isNeedEven, int waitTimeOut) throws RemoteException {
        Rect nodeRect = new Rect();
        nodeInfo.getBoundsInScreen(nodeRect);
        touch(nodeRect.centerX() + addX, nodeRect.centerY() + addY, isNeedEven, waitTimeOut);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected boolean findOrTouchContorl(ArrayList<AccessibilityNodeInfo> infoView, String contorlModel, boolean isNeedTouch, boolean isNeedEnable, int contorlIndex, String contorlName, boolean isNeedEven, int waitTimeOut, int addX, int addY) throws RemoteException {
        int mIndex = 1;
        if (infoView != null) {
            for (int i = 0; i < infoView.size(); i++) {
                AccessibilityNodeInfo infoViewChild = (AccessibilityNodeInfo) infoView.get(i);
                if (infoViewChild != null) {
                    CharSequence c = infoViewChild.getContentDescription();
                    if (contorlModel.equals("TEXT")) {
                        c = infoViewChild.getText();
                    }
                    if (c != null && infoViewChild.isVisibleToUser() && c.toString().equals(contorlName)) {
                        if (mIndex == contorlIndex) {
                            if (isNeedTouch) {
                                touchContorl(infoViewChild, addX, addY, isNeedEven, waitTimeOut);
                                AutoInterface.sleep(this.intervalTime);
                            }
                            if (isNeedEnable) {
                                return infoViewChild.isEnabled();
                            }
                            return true;
                        }
                        mIndex++;
                    }
                }
            }
        }
        return false;
    }

    public String adbshell(String commandText) throws RemoteException {
        return ShellUtils.execCommand(commandText, false, true).successMsg;
    }

    @Override
    public boolean hasFocused(String str) throws RemoteException {
        return TextUtils.isEmpty(MyWorkFlowService.CurFocused);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public String getDump() throws RemoteException {
        AccessibilityNodeInfo nodeInfo = getRootInWindow();
        if (nodeInfo != null) {
            return recycle(nodeInfo).toString();
        }
        return "null";
    }

    public String getAllText(boolean isNeedContent) throws RemoteException {
        String mText = "";
        ArrayList<AccessibilityNodeInfo> lastInfoView = getNewInfoView();
        if (lastInfoView != null) {
            for (int i = 0; i < lastInfoView.size(); i++) {
                AccessibilityNodeInfo infoViewChild = (AccessibilityNodeInfo) lastInfoView.get(i);
                if (infoViewChild != null) {
                    CharSequence s = infoViewChild.getText();
                    if (s != null) {
                        mText = new StringBuilder(String.valueOf(mText)).append(s.toString()).append(ShellUtils.COMMAND_LINE_END).toString();
                    }
                    if (isNeedContent) {
                        CharSequence c = infoViewChild.getContentDescription();
                        if (c != null) {
                            mText = new StringBuilder(String.valueOf(mText)).append(c.toString()).append(ShellUtils.COMMAND_LINE_END).toString();
                        }
                    }
                }
            }
        }
        return mText;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean searchTextIndex(String textName, int textIndex, boolean isNeedFresh) throws RemoteException {
        return findOrTouchContorl(getNewInfoView(), "TEXT", false, false, textIndex, textName, false, 0, 0, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean searchContentTextIndex(String contentName, int contentIndex, boolean isNeedFresh) throws RemoteException {
        return findOrTouchContorl(getNewInfoView(), "CONTENT", false, false, contentIndex, contentName, false, 0, 0, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean touchTextByIndex(String textName, int textIndex, boolean isNeedFresh, boolean isNeedEven, int waitTimeOut) throws RemoteException {
        return findOrTouchContorl(getNewInfoView(), "TEXT", true, false, textIndex, textName, isNeedEven, waitTimeOut, 0, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean touchContentTextByIndex(String textName, int textIndex, boolean isNeedFresh, boolean isNeedEven, int waitTimeOut) throws RemoteException {
        return findOrTouchContorl(getNewInfoView(), "CONTENT", true, false, textIndex, textName, isNeedEven, waitTimeOut, 0, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean touchIdByIndex(String controlIdName, int controlIndex, boolean isNeedFresh, boolean isNeedEven, int waitTimeOut) throws RemoteException {
        AccessibilityNodeInfo nodeInfo = getRootInWindow();
        if (nodeInfo == null) {
            return false;
        }
        AccessibilityNodeInfo mNode = getIndex(nodeInfo.findAccessibilityNodeInfosByViewId(controlIdName), controlIndex);
        if (mNode == null) {
            return false;
        }
        touchContorl(mNode, 0, 0, isNeedEven, waitTimeOut);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean touchTextByAddXY(String textName, int textIndex, boolean isNeedFresh, boolean isNeedEven, int waitTimeOut, int addX, int addY) throws RemoteException {
        return findOrTouchContorl(getNewInfoView(), "TEXT", true, false, textIndex, textName, isNeedEven, waitTimeOut, addX, addY);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean touchContentTextByAddXY(String textName, int textIndex, boolean isNeedFresh, boolean isNeedEven, int waitTimeOut, int addX, int addY) throws RemoteException {
        return findOrTouchContorl(getNewInfoView(), "CONTENT", true, false, textIndex, textName, isNeedEven, waitTimeOut, addX, addY);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean touchIdByAddXY(String controlIdName, int controlIndex, boolean isNeedFresh, boolean isNeedEven, int waitTimeOut, int addX, int addY) throws RemoteException {
        AccessibilityNodeInfo nodeInfo = getRootInWindow();
        if (nodeInfo != null) {
            AccessibilityNodeInfo mNode = getIndex(nodeInfo.findAccessibilityNodeInfosByViewId(controlIdName), controlIndex);
            if (mNode != null) {
                touchContorl(mNode, addX, addY, isNeedEven, waitTimeOut);
                return true;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean isViewIdChecked(String controlIdName, int controlIndex, boolean isNeedFresh) throws RemoteException {
        AccessibilityNodeInfo nodeInfo = getRootInWindow();
        if (nodeInfo != null) {
            AccessibilityNodeInfo mNode = getIndex(nodeInfo.findAccessibilityNodeInfosByViewId(controlIdName), controlIndex);
            if (mNode != null) {
                return mNode.isChecked();
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean searchIdByIndex(String controlIdName, int controlIndex, boolean isNeedFresh) throws RemoteException {
        AccessibilityNodeInfo nodeInfo = getRootInWindow();
        if (nodeInfo == null || getIndex(nodeInfo.findAccessibilityNodeInfosByViewId(controlIdName), controlIndex) == null) {
            return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public String getSearchIdNumber(String controlIdName, boolean isNeedFresh) throws RemoteException {
        int mIndex = 0;
        AccessibilityNodeInfo nodeInfo = getRootInWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> nodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId(controlIdName);
            for (int i = 0; i < nodeInfoList.size(); i++) {
                if (((AccessibilityNodeInfo) nodeInfoList.get(i)) != null) {
                    mIndex++;
                }
            }
        }
        return String.valueOf(mIndex);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean isTextEnabled(String textName, int textIndex, boolean isNeedFresh) throws RemoteException {
        return findOrTouchContorl(getNewInfoView(), "TEXT", false, true, textIndex, textName, false, 0, 0, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean isContentTextEnabled(String contentName, int contentIndex, boolean isNeedFresh) throws RemoteException {
        return findOrTouchContorl(getNewInfoView(), "CONTENT", false, true, contentIndex, contentName, false, 0, 0, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean isIdEnabled(String controlIdName, int controlIndex, boolean isNeedFresh) throws RemoteException {
        AccessibilityNodeInfo nodeInfo = getRootInWindow();
        if (nodeInfo != null) {
            AccessibilityNodeInfo mNode = getIndex(nodeInfo.findAccessibilityNodeInfosByViewId(controlIdName), controlIndex);
            if (mNode != null) {
                return mNode.isEnabled();
            }
        }
        return false;
    }

    @SuppressLint({"NewApi"})
    public List<String> getItemVal(String controlName, int controlIndex, boolean isNeedFresh, String propertyVal) throws RemoteException {
        return getProp(findView(controlName, controlIndex), propertyVal);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected AccessibilityNodeInfo findView(String controlName, int controlIndex) {
        AccessibilityNodeInfo nodeInfo = getRootInWindow();
        if (nodeInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList;
        if (controlName.contains(":id/")) {
            nodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId(controlName);
        } else {
            nodeInfoList = nodeInfo.findAccessibilityNodeInfosByText(controlName);
        }
        return getIndex(nodeInfoList, controlIndex);
    }

    @SuppressLint({"NewApi"})
    protected List<String> getProp(AccessibilityNodeInfo info, String val) {
        String text = "";
        List<String> itemVal = new ArrayList();
        if (info != null) {
            CharSequence c;
            switch (val.hashCode()) {
                case -1964681502:
                    if (val.equals("clickable")) {
                        itemVal.add(info.isClickable() ? "true" : "false");
                        return itemVal;
                    }
                    break;
                case -1777688568:
                    if (val.equals("isvisible")) {
                        itemVal.add(info.isVisibleToUser() ? "true" : "false");
                        return itemVal;
                    }
                    break;
                case -1609594047:
                    if (val.equals("enabled")) {
                        itemVal.add(info.isEnabled() ? "true" : "false");
                        return itemVal;
                    }
                    break;
                case -1206239059:
                    if (val.equals("multiline")) {
                        itemVal.add(info.isMultiLine() ? "true" : "false");
                        return itemVal;
                    }
                    break;
                case -807062458:
                    if (val.equals("package")) {
                        c = info.getPackageName();
                        if (c != null) {
                            text = c.toString();
                        }
                        itemVal.add(text);
                        return itemVal;
                    }
                    break;
                case -691041417:
                    if (val.equals("focused")) {
                        itemVal.add(info.isFocusable() ? "true" : "false");
                        return itemVal;
                    }
                    break;
                case 3841:
                    if (val.equals("xy")) {
                        getXY(info.toString());
                        return Arrays.asList(new String[]{Integer.toString(GET_X_COORDINATE), Integer.toString(GET_Y_COORDINATE)});
                    }
                    break;
                case 3556653:
                    if (val.equals("text")) {
                        c = info.getText();
                        if (c != null) {
                            text = c.toString();
                        }
                        itemVal.add(text);
                        return itemVal;
                    }
                    break;
                case 66669991:
                    if (val.equals("scrollable")) {
                        itemVal.add(info.isScrollable() ? "true" : "false");
                        return itemVal;
                    }
                    break;
                case 94742904:
                    if (val.equals("class")) {
                        c = info.getClassName();
                        if (c != null) {
                            text = c.toString();
                        }
                        itemVal.add(text);
                        return itemVal;
                    }
                    break;
                case 398964322:
                    if (val.equals("checkable")) {
                        itemVal.add(info.isCheckable() ? "true" : "false");
                        return itemVal;
                    }
                    break;
                case 742313895:
                    if (val.equals("checked")) {
                        itemVal.add(info.isChecked() ? "true" : "false");
                        return itemVal;
                    }
                    break;
                case 951530617:
                    if (val.equals("content")) {
                        c = info.getContentDescription();
                        if (c != null) {
                            text = c.toString();
                        }
                        itemVal.add(text);
                        return itemVal;
                    }
                    break;
                case 1191572123:
                    if (val.equals("selected")) {
                        itemVal.add(info.isSelected() ? "true" : "false");
                        return itemVal;
                    }
                    break;
                case 1216985755:
                    if (val.equals("password")) {
                        itemVal.add(info.isPassword() ? "true" : "false");
                        return itemVal;
                    }
                    break;
                case 1602416228:
                    if (val.equals("editable")) {
                        itemVal.add(info.isEditable() ? "true" : "false");
                        return itemVal;
                    }
                    break;
                case 1720194257:
                    if (val.equals("long-clickable")) {
                        itemVal.add(info.isLongClickable() ? "true" : "false");
                        return itemVal;
                    }
                    break;
            }
            itemVal.add(text);
            return itemVal;
        }
        itemVal.add(text);
        return itemVal;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected AccessibilityNodeInfo findSwitchViewInfo(String controlText, int controlIndex, boolean isNeedfresh, String controlIdName) throws RemoteException {
        AccessibilityNodeInfo nodeInfo = getRootInWindow();
        if (nodeInfo != null) {
            AccessibilityNodeInfo mNode = getIndex(nodeInfo.findAccessibilityNodeInfosByText(controlText), controlIndex);
            if (mNode != null) {
                AccessibilityNodeInfo parentInfo = mNode.getParent();
                if (parentInfo != null) {
                    List<AccessibilityNodeInfo> findInfo = parentInfo.findAccessibilityNodeInfosByViewId(controlIdName);
                    if (findInfo != null && findInfo.size() >= 1) {
                        for (int i = 0; i < findInfo.size(); i++) {
                            Log.e("findInfo", "findInfo=" + findInfo.get(i));
                        }
                        return (AccessibilityNodeInfo) findInfo.get(0);
                    }
                }
            }
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean getSwitchIsChecked(String controlText, int controlIndex, boolean isNeedFresh, String controlIdName, int waitTimeOut) throws RemoteException {
        AccessibilityNodeInfo nodeInfo = findSwitchViewInfo(controlText, controlIndex, isNeedFresh, controlIdName);
        if (nodeInfo != null) {
            return nodeInfo.isChecked();
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean touchSwitchByText(String controlText, int controlIndex, boolean isNeedFresh, boolean isNeedEven, String controlIdName, int wait, int touchTimes) throws RemoteException {
        AccessibilityNodeInfo nodeInfo = findSwitchViewInfo(controlText, controlIndex, isNeedFresh, controlIdName);
        if (nodeInfo != null) {
            Rect nodeRect = new Rect();
            nodeInfo.getBoundsInScreen(nodeRect);
            touch(nodeRect.centerX(), nodeRect.centerY(), isNeedEven, touchTimes);
        }
        return false;
    }

    public boolean touch(int coordinateX, int coordinateY, boolean isNeedEven, int touchTimes) throws RemoteException {
        if (isNeedEven) {
            AutoInterface.touch(coordinateX, coordinateY, touchTimes);
        } else if (touchTimes > 0) {
            ShellUtils.execCommand("input swipe " + coordinateX + " " + coordinateY + " " + coordinateX + " " + coordinateY + " " + touchTimes, false, true);
        } else {
            ShellUtils.execCommand("input tap " + coordinateX + " " + coordinateY, false, true);
        }
        AutoInterface.sleep(SLEEP_WAIT_TIME);
        return true;
    }

    public boolean dragTo(int coordinateX, int coordinateY, int coordinateX1, int coordinateY1, int touchTimes) throws RemoteException {
        AutoInterface.sleep(SLEEP_WAIT_TIME);
        AutoInterface.drag(coordinateX, coordinateY, coordinateX1, coordinateY1, touchTimes);
        AutoInterface.sleep(SLEEP_WAIT_TIME);
        return true;
    }

    public boolean type(String needInputText) throws RemoteException {
        AutoInterface.sleep(SLEEP_WAIT_TIME);
        String mInputText = "";
        for (int i = 0; i < needInputText.length(); i++) {
            int asc = needInputText.charAt(i);
            if (asc == 42 || asc == 35 || asc == 64) {
                if (!mInputText.equals("")) {
                    AutoInterface.type(mInputText);
                    mInputText = "";
                }
                int keycode = 0;
                if (asc == 42) {
                    keycode = 17;
                } else if (asc == 35) {
                    keycode = 18;
                } else if (asc == 64) {
                    keycode = 77;
                }
                ShellUtils.execCommand("input keyevent " + keycode, false, true);
                AutoInterface.sleep(100);
            } else {
                mInputText = new StringBuilder(String.valueOf(mInputText)).append(needInputText.charAt(i)).toString();
            }
        }
        if (!mInputText.equals("")) {
            AutoInterface.type(mInputText);
        }
        AutoInterface.sleep(SLEEP_WAIT_TIME);
        return true;
    }

    public boolean press(String evenyKey) throws RemoteException {
        AutoInterface.sleep(SLEEP_WAIT_TIME);
        switch (evenyKey.hashCode()) {
            case 2030823:
                if (evenyKey.equals("BACK")) {
                    AutoInterface.inst.sendKeyDownUpSync(4);
                    break;
                }
                break;
            case 2223327:
                if (evenyKey.equals("HOME")) {
                    AutoInterface.inst.sendKeyDownUpSync(3);
                    break;
                }
                break;
        }
        AutoInterface.sleep(SLEEP_WAIT_TIME);
        return true;
    }

    public boolean sleep(int waitTimes) throws RemoteException {
        AutoInterface.sleep(waitTimes);
        return true;
    }

    public boolean startActivity(String ActivityName) throws RemoteException {
        String[] splitName;
        if (ActivityName.contains("/.")) {
            splitName = ActivityName.split("/.");
            splitName[1] = splitName[0] + "." + splitName[1];
        } else {
            splitName = ActivityName.split("/");
        }
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(splitName[0], splitName[1]));
        intent.setFlags(268435456);
        monitorActivity.startActivity(intent);
        AutoInterface.sleep(1000);
        return true;
    }

    public boolean setDeviceType(String str, int intervalTime) throws RemoteException {
        isItemCreated = true;
        DEVICENAME = str;
        this.intervalTime = intervalTime;
        return false;
    }

    public boolean fileSave(String file, String step, boolean result, String stand, String note) throws RemoteException {
        FILE_NAME = file;
        AutoInterface.fileSave(file, step, result, stand, note, isItemCreated);
        isItemCreated = false;
        return true;
    }

    public boolean fileSaveStr(String file, String step, String result, String stand, String note, String photo) throws RemoteException {
        FILE_NAME = file;
        AutoInterface.fileSave(file, step, result, stand, note, isItemCreated, photo);
        isItemCreated = false;
        return true;
    }

    private void checkDevices() {
        if (isFisrtCheckDevices) {
            String devices = ShellUtils.execCommand("getprop  ro.product.brand", false, true).successMsg.trim();
            String manufacturer = ShellUtils.execCommand("getprop  ro.product.manufacturer", false, true).successMsg.trim();
            if (!(devices.equals("Meizu") || devices.equals("alps") || devices.equals("samsung") || devices.equals("htc") || manufacturer.equals("Meizu") || manufacturer.equals("MEIZU") || manufacturer.equals("alps") || manufacturer.equals("samsung") || manufacturer.equals("HTC"))) {
                System.err.println("No MEIZU Devices find");
                System.exit(0);
            }
            isFisrtCheckDevices = false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint({"SdCardPath"})
    public boolean touchPicIndex(String path, int index, boolean even, int times) throws RemoteException {
        AutoInterface.sleep(SLEEP_WAIT_TIME);
        RemoteService.tackPict(SDCARD_PATH_NAME + "/screen.png");
        int[][] pic = new ImageMatching().imageMatching(SDCARD_PATH_NAME + "/screen.png", path);
        if (pic.length == 0 || index > pic.length) {
            return false;
        }
        touch(pic[index - 1][0], pic[index - 1][1], false, times);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint({"SdCardPath"})
    public boolean searchPicIndex(String path, int index) throws RemoteException {
        AutoInterface.sleep(SLEEP_WAIT_TIME);
        RemoteService.tackPict(SDCARD_PATH_NAME + "/screen.png");
        int[][] pic = new ImageMatching().imageMatching(SDCARD_PATH_NAME + "/screen.png", path);
        if (pic.length != 0 && index > pic.length) {
            return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public String takepicture(int num, String file) throws RemoteException {
        return AutoInterface.takepicture(file);
    }

    @SuppressLint({"Recycle"})
    public boolean zoomSlider(List<String> xyList) throws RemoteException {
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        float startX1 = (float) Integer.parseInt((String) xyList.get(0));
        float startY1 = (float) Integer.parseInt((String) xyList.get(1));
        float startX2 = (float) Integer.parseInt((String) xyList.get(2));
        float startY2 = (float) Integer.parseInt((String) xyList.get(3));
        float endX1 = (float) Integer.parseInt((String) xyList.get(4));
        float endY1 = (float) Integer.parseInt((String) xyList.get(5));
        float endX2 = (float) Integer.parseInt((String) xyList.get(6));
        float endY2 = (float) Integer.parseInt((String) xyList.get(7));
        PointerCoords[] pointerCoords = new PointerCoords[2];
        PointerCoords pc1 = new PointerCoords();
        PointerCoords pc2 = new PointerCoords();
        pc1.x = startX1;
        pc1.y = startY1;
        pc1.pressure = 1.0f;
        pc1.size = 1.0f;
        pc2.x = startX2;
        pc2.y = startY2;
        pc2.pressure = 1.0f;
        pc2.size = 1.0f;
        pointerCoords[0] = pc1;
        pointerCoords[1] = pc2;
        PointerProperties[] pointerProperties = new PointerProperties[2];
        PointerProperties pp1 = new PointerProperties();
        PointerProperties pp2 = new PointerProperties();
        pp1.id = 0;
        pp1.toolType = 1;
        pp2.id = 1;
        pp2.toolType = 1;
        pointerProperties[0] = pp1;
        pointerProperties[1] = pp2;
        AutoInterface.inst.sendPointerSync(MotionEvent.obtain(downTime, eventTime, 0, 1, pointerProperties, pointerCoords, 0, 0, 1.0f, 1.0f, 0, 0, 0, 0));
        AutoInterface.inst.sendPointerSync(MotionEvent.obtain(downTime, eventTime, (pp2.id << 8) + 5, 2, pointerProperties, pointerCoords, 0, 0, 1.0f, 1.0f, 0, 0, 0, 0));
        float stepX1 = (endX1 - startX1) / ((float) 100);
        float stepY1 = (endY1 - startY1) / ((float) 100);
        float stepX2 = (endX2 - startX2) / ((float) 100);
        float stepY2 = (endY2 - startY2) / ((float) 100);
        for (int i = 0; i < 100; i++) {
            eventTime += 10;
            PointerCoords pointerCoords2 = pointerCoords[0];
            pointerCoords2.x += stepX1;
            pointerCoords2 = pointerCoords[0];
            pointerCoords2.y += stepY1;
            pointerCoords2 = pointerCoords[1];
            pointerCoords2.x += stepX2;
            pointerCoords2 = pointerCoords[1];
            pointerCoords2.y += stepY2;
            AutoInterface.inst.sendPointerSync(MotionEvent.obtain(downTime, eventTime, 2, 2, pointerProperties, pointerCoords, 0, 0, 1.0f, 1.0f, 0, 0, 0, 0));
        }
        AutoInterface.sleep(500);
        return true;
    }

    @SuppressLint({"Recycle"})
    public boolean multiDrag(List<String> xyList) throws RemoteException {
        if (xyList.size() % 4 != 0) {
            System.err.println("length error");
            return false;
        }
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        float toY = 0.0f;
        float toX = 0.0f;
        Boolean start = Boolean.valueOf(true);
        for (int i = 0; i < xyList.size(); i += 4) {
            float fromY = (float) Integer.parseInt((String) xyList.get(i + 1));
            float fromX = (float) Integer.parseInt((String) xyList.get(i));
            toY = (float) Integer.parseInt((String) xyList.get(i + 3));
            toX = (float) Integer.parseInt((String) xyList.get(i + 2));
            float y = fromY;
            float x = fromX;
            float yStep = (toY - fromY) / ((float) 100);
            float xStep = (toX - fromX) / ((float) 100);
            if (start.booleanValue()) {
                MotionEvent event = MotionEvent.obtain(downTime, eventTime, 0, fromX, fromY, 0);
                start = Boolean.valueOf(false);
                try {
                    AutoInterface.inst.sendPointerSync(event);
                } catch (SecurityException e) {
                }
            }
            for (int j = 0; j < 100; j++) {
                y += yStep;
                x += xStep;
                eventTime = SystemClock.uptimeMillis();
                try {
                    AutoInterface.inst.sendPointerSync(MotionEvent.obtain(downTime, eventTime, 2, x, y, 0));
                } catch (SecurityException e2) {
                }
            }
        }
        try {
            AutoInterface.inst.sendPointerSync(MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), 1, toX, toY, 0));
        } catch (SecurityException e3) {
        }
        AutoInterface.sleep(1000);
        return true;
    }

    public List<String> getLogcat(String str, int timeout) throws RemoteException {
        this.RUNING_LOG = str;
        isProessRuning = true;
        LOG_RUN_TIME = timeout;
        this.mThread = new Thread(this.stop);
        this.mThread.start();
        isFindedLogText = Boolean.valueOf(false);
        this.LOGING_TEXT = "";
        List<String> logVal = new ArrayList();
        while (isProessRuning) {
            AutoInterface.sleep(100);
        }
        String[] strArr = new String[2];
        strArr[0] = isFindedLogText.toString();
        strArr[1] = this.LOGING_TEXT.length() > 1000 ? this.LOGING_TEXT.substring(this.LOGING_TEXT.length() - 1000) : this.LOGING_TEXT;
        logVal = Arrays.asList(strArr);
        this.LOGING_TEXT = "";
        return logVal;
    }

    protected void updateFlingerFpsCounter() {
        try {
            Class clazz = monitorActivity.getClassLoader().loadClass("android.os.ServiceManager");
            Method[] tmp = clazz.getMethods();
            Method method = clazz.getMethod("getService", new Class[]{String.class});
            tmp = method.getClass().getMethods();
            IBinder flinger = (IBinder) method.invoke(null, new Object[]{"SurfaceFlinger"});
            if (flinger != null) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.ISurfaceComposer");
                flinger.transact(1001, data, reply, 0);
                CURRENT_FPS = new StringBuilder(String.valueOf(reply.readFloat())).toString();
                reply.recycle();
                data.recycle();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e2) {
            e2.printStackTrace();
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
        } catch (IllegalAccessException e4) {
            e4.printStackTrace();
        } catch (IllegalArgumentException e5) {
            e5.printStackTrace();
        } catch (InvocationTargetException e6) {
            e6.printStackTrace();
        } catch (Exception e7) {
            e7.printStackTrace();
        }
    }

    public boolean startFpsMem(String pack, String model, String step, int times) throws RemoteException {
        this.PACKAGE_NAME = pack;
        this.CURRENT_MODEL = model;
        this.mThread = new Thread(this.fpsMem);
        this.mThread.start();
        this.TEST_STEP = step;
        this.HANDER_WAIT_TIME = times;
        isHanderRuning = true;
        return true;
    }

    public void fillMemoryMonitorItems(String pack) {
        ActivityManager am = (ActivityManager) monitorActivity.getSystemService("activity");
        List<RunningAppProcessInfo> appProcessList = am.getRunningAppProcesses();
        MemoryInfo memInfo = new MemoryInfo();
        am.getMemoryInfo(memInfo);
        int[] myMempid = null;
        Boolean findProcess = Boolean.valueOf(false);
        String procesName = "";
        for (RunningAppProcessInfo appProcessInfo : appProcessList) {
            int pid = appProcessInfo.pid;
            if (appProcessInfo.processName.equals(pack)) {
                procesName = appProcessInfo.processName;
                myMempid = new int[]{pid};
                findProcess = Boolean.valueOf(true);
                break;
            }
        }
        if (findProcess.booleanValue()) {
            Debug.MemoryInfo[] info = am.getProcessMemoryInfo(myMempid);
            MEM_AVAI = new StringBuilder(String.valueOf(memInfo.availMem)).toString();
            PROCESS_MEM = new StringBuilder(String.valueOf(info[0].dalvikPss)).toString();
            return;
        }
        MEM_AVAI = "0";
        PROCESS_MEM = "0";
    }

    public boolean endFpsMem() throws RemoteException {
        isHanderRuning = false;
        return saveFpsMemInfo() == null;
    }

    protected String saveFpsMemInfo() {
        FileNotFoundException e;
        BufferedWriter bufferedWriter;
        IOException e2;
        Exception e3;
        double proAvg = 0.0d;
        double fpsAvg = 0.0d;
        if (this.fpsCou.length() != 0) {
            try {
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(SDCARD_PATH_NAME + "/AutoTest/FpsMem.txt", true)));
                try {
                    String[] memTmp = this.memCou.toString().split(",");
                    String[] proTmp = this.processMemCou.toString().split(",");
                    String[] fpsTmp = this.fpsCou.toString().split(",");
                    for (int j = 0; j < fpsTmp.length; j++) {
                        out.write(System.currentTimeMillis() + "~" + BUILD_MODEL + "~" + this.CURRENT_MODEL + "~" + this.TEST_STEP + "~" + this.PACKAGE_NAME + "~" + memTmp[j] + "~" + proTmp[j] + "~" + fpsTmp[j] + ShellUtils.COMMAND_LINE_END);
                    }
                    out.close();
                    for (int i = 0; i < fpsTmp.length; i++) {
                        proAvg += Double.valueOf(proTmp[i]).doubleValue();
                        fpsAvg += Double.valueOf(fpsTmp[i]).doubleValue();
                    }
                    proAvg /= (double) fpsTmp.length;
                    fpsAvg /= (double) fpsTmp.length;
                } catch (FileNotFoundException e4) {
                    e = e4;
                    bufferedWriter = out;
                } catch (IOException e5) {
                    e2 = e5;
                    bufferedWriter = out;
                } catch (Exception e6) {
                    e3 = e6;
                    bufferedWriter = out;
                }
            } catch (FileNotFoundException e7) {
                e = e7;
                e.printStackTrace();
                return new StringBuilder(String.valueOf(new DecimalFormat("#0").format(proAvg))).append(",").append(new DecimalFormat("#0.0").format(fpsAvg)).toString();
            }catch (Exception e9) {
                e3 = e9;
                e3.printStackTrace();
                return new StringBuilder(String.valueOf(new DecimalFormat("#0").format(proAvg))).append(",").append(new DecimalFormat("#0.0").format(fpsAvg)).toString();
            }
        }
        return new StringBuilder(String.valueOf(new DecimalFormat("#0").format(proAvg))).append(",").append(new DecimalFormat("#0.0").format(fpsAvg)).toString();
    }

    public boolean performAction(int model) throws RemoteException {
        mCatchNodeWindows.performAction(model);
        return true;
    }

    public boolean setState(String packageName, int opNum, int StateNum) throws RemoteException {
        Secure.putInt(monitorActivity.getContentResolver(), new StringBuilder(String.valueOf(packageName)).append("_op_").append(opNum).toString(), StateNum);
        return true;
    }

    public String getNotificationText() throws RemoteException {
        return MyWorkFlowService.CurNotification;
    }

    @Override
    public IBinder asBinder() {
        return null;
    }
}
