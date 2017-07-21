package com.tanjinc.myworkflow;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.view.MotionEvent;

import com.tanjinc.myworkflow.utils.ShellUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by maxueming on 2017/7/17.
 */
public class AutoInterface {
    static String dumpstr = "";
    static ArrayList<ArrayList<String>> group_name = new ArrayList();
    static Instrumentation inst = new Instrumentation();
    private static final Object mLock = new Object();
    static String pathStr = Environment.getExternalStorageDirectory().getPath();
    static String picturename = "";
    static String savefile = "error";
    static boolean touchStart = true;

    public IBinder onBind(Intent arg0) {
        return null;
    }

    public static void sleep(int times) {
        long startTimeMillis = System.currentTimeMillis();
        synchronized (mLock) {
            while (System.currentTimeMillis() - startTimeMillis < ((long) times)) {
                try {
                    mLock.wait((long) times);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mLock.notify();
        }
    }

    public static ArrayList<ArrayList<String>> splitfiles() {
        group_name.clear();
        group_name = new ArrayList();
        readFile("/storage/emulated/legacy/window_dump.xml");
        getviews();
        ShellUtils.execCommand("rm /storage/emulated/legacy/window_dump.xml", false, true);
        return group_name;
    }

    public static String getDump() {
        readFile("/storage/emulated/legacy/window_dump.xml");
        ShellUtils.execCommand("rm /storage/emulated/legacy/window_dump.xml", false, true);
        return dumpstr;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint({"SimpleDateFormat"})
    public static String takepicture(String file) {
        String filename = new StringBuilder(String.valueOf(file)).append("_").append(MyWorkFlowImpl.DEVICENAME).append("_").append(new SimpleDateFormat("yyyyMMddHHmmss").format(Long.valueOf(System.currentTimeMillis()))).append(".jpg").toString();
        RemoteService.tackPict(pathStr + "/autotest/image/" + filename);
        picturename = filename;
        return filename;
    }

    private static void getviews() {
        String[] splitstr = dumpstr.split("><");
        String astr = "text=\"";
        int i = 0;
        while (i < splitstr.length) {
            int len = splitstr[i].indexOf(astr);
            if (len > -1 && splitstr[i].charAt(len + 6) != '\"') {
                String name = splitstr[i].substring(len + 6, splitstr[i].indexOf("\" resource-id"));
                String[] xy = splitstr[i].substring(splitstr[i].indexOf("bounds=\"[") + 9, splitstr[i].lastIndexOf("]")).replace("][", ",").split(",");
                int x = (Integer.parseInt(xy[0]) + Integer.parseInt(xy[2])) / 2;
                int y = (Integer.parseInt(xy[1]) + Integer.parseInt(xy[3])) / 2;
                ArrayList<String> grouptemp = new ArrayList();
                grouptemp.add(name);
                grouptemp.add(String.valueOf(x));
                grouptemp.add(String.valueOf(y));
                group_name.add(grouptemp);
            }
            i++;
        }
    }

    public static void readFile(String filePath) {
        try {
            String encoding = "UTF-8";
            File file = new File(filePath);
            dumpstr = "";
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                while (true) {
                    String lineTxt = bufferedReader.readLine();
                    if (lineTxt == null) {
                        read.close();
                        return;
                    }
                    dumpstr += lineTxt + ShellUtils.COMMAND_LINE_END;
                }
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
    }

    public static String hasFocused() {
        return "";
    }

    public static void fileSave(String file, String step, String result, String stand, String note, boolean itemcreat, String photoItem) {
        savefile = file;
        String point = "~";
        if (file.equals("Error")) {
            itemcreat = true;
        }
        if (photoItem.equals("")) {
            photoItem = " " + point;
        } else {
            photoItem = new StringBuilder(String.valueOf(photoItem)).append(point).toString();
        }
        if (stand.equals("")) {
            stand = " ";
        }
        if (note.equals("")) {
            note = " ";
        }
        safeItem(file, point, new StringBuilder(String.valueOf(step)).append(point).append(stand).append(point).append(result).append(point).append(note).append(point).append(photoItem).toString(), itemcreat);
    }

    public static void fileSave(String file, String step, boolean result, String stand, String note, boolean itemcreat) {
        savefile = file;
        String point = "~";
        String resultitem = "";
        if (result) {
            resultitem = " " + point;
        } else {
            resultitem = takepicture(file) + point;
        }
        if (stand.equals("")) {
            stand = " ";
        }
        if (note.equals("")) {
            note = " ";
        }
        safeItem(file, point, new StringBuilder(String.valueOf(step)).append(point).append(stand).append(point).append(result).append(point).append(note).append(point).append(resultitem).toString(), itemcreat);
    }

    @SuppressLint({"SimpleDateFormat"})
    private static void safeItem(String file, String point, String filecontact, boolean itemcreat) {
        Exception e;
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathStr + "/AutoTest/MeizuAutoTest.txt", true)));
            if (itemcreat) {
                try {
                    String version = ShellUtils.execCommand("getprop ro.build.inside.id", false, true).successMsg.trim();
                    out.write(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(file))
                            .append(point).append("型号:").append(MyWorkFlowImpl.DEVICENAME).append(point)
                            .append("版本号:").append(version).append(point).append("SN:")
                            .append(ShellUtils.execCommand("getprop  ro.serialno", false, true).successMsg.trim())
                            .append(point).append("测试日期:").append(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
                                    .format(Long.valueOf(System.currentTimeMillis()))).append(point).append("系统语言:")
                            .append(Locale.getDefault().toString()).append(point).append(" ").toString())).append("\r\n").toString());
                } catch (Exception e2) {
                    e = e2;
                    BufferedWriter bufferedWriter = out;
                    e.printStackTrace();
                }
            }
            out.write(new StringBuilder(String.valueOf(filecontact)).append("\r\n").toString());
            out.close();
        } catch (Exception e3) {
            e = e3;
            e.printStackTrace();
        }
    }

    @SuppressLint({"Recycle"})
    public static boolean touch(int x, int y, int times) {
        boolean successfull = false;
        int retry = 0;
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(downTime, eventTime, 0, (float) x, (float) y, 0);
        while (!successfull && retry < 20) {
            try {
                inst.sendPointerSync(event);
                successfull = true;
            } catch (SecurityException e) {
                SecurityException ex = e;
                sleep(300);
                retry++;
            }
        }
        if (times > 500) {
            inst.sendPointerSync(MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), 2, ((float) x) + 1.0f, ((float) y) + 1.0f, 0));
            sleep(times);
            inst.sendPointerSync(MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), 1, (float) x, (float) y, 0));
        } else {
            inst.sendPointerSync(MotionEvent.obtain(300 + downTime, 300 + eventTime, 1, (float) x, (float) y, 0));
        }
        return true;
    }

    @SuppressLint({"Recycle"})
    public static boolean drag(int fromX, int fromY, int toX, int toY, int stepCount) {
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        if (stepCount >= 10) {
            stepCount /= 10;
        } else {
            stepCount = 10;
        }
        float y = (float) fromY;
        float x = (float) fromX;
        float yStep = (float) ((toY - fromY) / stepCount);
        float xStep = (float) ((toX - fromX) / stepCount);
        try {
            inst.sendPointerSync(MotionEvent.obtain(downTime, eventTime, 0, (float) fromX, (float) fromY, 0));
        } catch (SecurityException e) {
        }
        float x2 = x;
        float y2 = y;
        for (int i = 0; i < stepCount; i++) {
            y2 += yStep;
            x2 += xStep;
            try {
                inst.sendPointerSync(MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), 2, x2, y2, 0));
            } catch (SecurityException e2) {
            }
        }
        try {
            inst.sendPointerSync(MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), 1, (float) toX, (float) toY, 0));
        } catch (SecurityException e3) {
        }
        return true;
    }

    public static void type(String str) {
        int i = 0;
        while (i < str.length()) {
            try {
                inst.sendStringSync(new StringBuilder(String.valueOf(str.charAt(i))).toString());
                sleep(100);
                i++;
            } catch (SecurityException e) {
                inst = new Instrumentation();
                return;
            }
        }
    }
}
