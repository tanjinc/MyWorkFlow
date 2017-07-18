package com.tanjinc.myworkflow;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.support.annotation.RequiresApi;
import android.text.TextUtils.SimpleStringSplitter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class RemoteService extends Service {
    public static Context mContext;
    static MainActivity monitorActivity;
    float CurrentFps;
    String[] GROUP_TEXT_LIST = new String[0];
    String fPath = (this.pathStr + "/AutoTest/");
    String filename = "";
    boolean findAllBroad = true;
    Boolean logRun = Boolean.valueOf(true);
    String pathStr = Environment.getExternalStorageDirectory().getPath();
    Boolean proessRun = Boolean.valueOf(true);
    String[] runcommand = new String[]{"cat /proc/version", "cat /proc/cpuinfo", "cat /proc/meminfo", "cat /proc/last_kmsg", "cat /proc/reset_reason", "cat /data/anr/traces.txt", "cat /data/ril.log", "cat /data/ril_miss.log", "cat /data/ril_sn.log", "cat /cache/recovery/last_install", "cat /cache/recovery/last_log", "getprop", "ps", "logcat -v threadtime -d", "logcat -v threadtime -b radio -d", "logcat -v threadtime -b events -d", "dmesg", "dumpsys power", "dumpsys alarm", "dumpsys battery", "dumpsys batteryinfo", "dumpsys cpuinfo", "dumpsys meminfo", "dumpsys netpolicy", "dumpsys netstats --full --uid", "dumpsys SurfaceFlinger", "dumpsys wifi", "dumpsys activity broadcasts", "dumpsys batterystats", "ps -t", "cat /sys/devices/platform/soc-audio.0/reg_program", "cat /sys/fs/pstore/console-ramoops", "cat /sys/fs/pstore/dmesg-ramoops-0", "cat /sys/fs/pstore/dmesg-ramoops-1", "cat /sys/fs/pstore/ftrace-ramoops", "cat /sys/class/charger_class/charger_device/dump_reg", "ping -c 2 61.147.106.32", "ping -c 2 "};
    Boolean serviceRun = Boolean.valueOf(true);
    Runnable stop = new Runnable() {
        public void run() {
            Exception e;
            BufferedReader bufferedReader;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("logcat -s ANRManager AndroidRuntime ActivityManager").getInputStream()));
                while (RemoteService.this.proessRun.booleanValue()) {
                    String line = reader.readLine();
                    String str;
                    if (line.contains("): Process: ")) {
                        str = line.substring(line.indexOf("): Process: ") + 12);
                        str = str.substring(0, str.indexOf(", PID:"));
                        RemoteService.this.filename = "Crash_" + str;
                        RemoteService.this.CatchApplocationError();
                    } else {
                        try {
                            if (line.contains("): ANR in ")) {
                                str = line.substring(line.indexOf("ANR in ") + 7);
                                str = str.substring(0, str.indexOf(" ("));
                                RemoteService.this.filename = "NotRespond_" + str;
                                RemoteService.this.CatchApplocationError();
                            }
                        } catch (Exception e2) {
                            e = e2;
                            bufferedReader = reader;
                        }
                    }
                }
                bufferedReader = reader;
            } catch (Exception e3) {
                e = e3;
                e.printStackTrace();
            }
        }
    };
    Thread tt;

    public RemoteService() {
        mContext = this;
    }

    public void onCreate() {
        super.onCreate();
        ShellUtils.execCommand("logcat -c", false, true);
        this.tt = new Thread(this.stop);
        this.tt.start();
        MyWorkFlowImpl MyWorkFlowImpl = new MyWorkFlowImpl(this);
        int i = 0;
        while (i < 5) {
            openAccessibilityService();
            if (!isAccessibilitySettingsOn()) {
                openAccessibilityService();
                i++;
            } else {
                return;
            }
        }
    }

    private void openAccessibilityService() {
        Secure.putString(getContentResolver(), "enabled_accessibility_services", "com.meizu.meizuuser/com.meizu.meizuuser.CatchNodeWindows");
        Secure.putInt(getContentResolver(), "accessibility_enabled", 1);
    }

    public boolean isAccessibilitySettingsOn() {
        int accessibilityEnabled = 0;
        String service = "com.meizu.meizuuser/com.meizu.meizuuser.CatchNodeWindows";
        try {
            accessibilityEnabled = Secure.getInt(getApplicationContext().getContentResolver(), "accessibility_enabled");
        } catch (SettingNotFoundException e) {
        }
        SimpleStringSplitter mStringColonSplitter = new SimpleStringSplitter(':');
        if (accessibilityEnabled != 1) {
            return false;
        }
        String settingValue = Secure.getString(getApplicationContext().getContentResolver(), "enabled_accessibility_services");
        if (settingValue == null) {
            return false;
        }
        SimpleStringSplitter splitter = mStringColonSplitter;
        splitter.setString(settingValue);
        while (splitter.hasNext()) {
            if (splitter.next().equalsIgnoreCase("com.meizu.meizuuser/com.meizu.meizuuser.CatchNodeWindows")) {
                return true;
            }
        }
        return false;
    }

    public void CatchApplocationError() {
        Log.e("@@@", "程序出错了----------------------------------------");
        if (this.logRun.booleanValue()) {
            this.logRun = Boolean.valueOf(false);
            AutoInterface.sleep(500);
            takeScreenShot();
            AutoInterface.fileSave(MyWorkFlowImpl.FILE_NAME, this.filename + "程序出错", "false", "搜索到程序出错", "", false, this.filename);
            this.logRun = Boolean.valueOf(true);
        }
    }

    public void takeScreenShot() {
        this.filename = AutoInterface.takepicture(this.filename);
        ShellUtils.execCommand("input keyevent  21", false, true);
        ShellUtils.execCommand("input keyevent  23", false, true);
        logcat();
    }

    @SuppressLint({"SimpleDateFormat", "SdCardPath"})
    private void logcat() {
        File file = new File(this.pathStr + "/AutoTest/image");
        if (!file.exists()) {
            file.mkdirs();
        }
        String filePath1 = "  -f " + this.pathStr + "/AutoTest/image/" + this.filename + ".txt";
        String filePath2 = "  >> " + this.pathStr + "/AutoTest/image/" + this.filename + ".txt";
        savefirst();
        String cmdstr = "";
        for (int i = 0; i < this.runcommand.length; i++) {
            System.out.println("cur" + this.runcommand[i] + "------------------------------");
            if (this.runcommand[i].contains("logcat")) {
                savefile(this.runcommand[i]);
                ShellUtils.execCommand(new StringBuilder(String.valueOf(this.runcommand[i])).append(filePath1).toString(), false, true);
            } else {
                savefile(this.runcommand[i]);
                ShellUtils.execCommand(new StringBuilder(String.valueOf(this.runcommand[i])).append(filePath2).toString(), false, true);
            }
        }
    }

    private void savefirst() {
        BufferedWriter bufferedWriter;
        Exception e;
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.pathStr + "/AutoTest/image/" + this.filename + ".txt", true)));
            try {
                out.write("***********************************************\r\n");
                out.write("\r\n");
                out.write("commands that this app will run :\r\n");
                for (Object valueOf : this.runcommand) {
                    out.write(new StringBuilder(String.valueOf(valueOf)).append("\r\n").toString());
                }
                out.write("\r\n");
                out.write("***********************************************\r\n");
                out.close();
                bufferedWriter = out;
            } catch (Exception e2) {
                e = e2;
                bufferedWriter = out;
                e.printStackTrace();
            }
        } catch (Exception e3) {
            e = e3;
            e.printStackTrace();
        }
    }

    private void savefile(String runcommand) {
        BufferedWriter bufferedWriter;
        Exception e;
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.pathStr + "/AutoTest/image/" + this.filename + ".txt", true)));
            try {
                out.write("\r\n");
                out.write("\r\n");
                out.write("\r\n");
                out.write("************************" + runcommand + "************************\r\n");
                out.write("\r\n");
                out.write("\r\n");
                out.write("\r\n");
                out.close();
                bufferedWriter = out;
            } catch (Exception e2) {
                e = e2;
                bufferedWriter = out;
                e.printStackTrace();
            }
        } catch (Exception e3) {
            e = e3;
            e.printStackTrace();
        }
    }

    public IBinder onBind(Intent intent) {
        return (IBinder) new MyWorkFlowImpl(MyWorkFlowImpl.mCatchNodeWindows);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void tackPict(String picName) {
        Matrix mDisplayMatrix = new Matrix();
        Display mDisplay = ((WindowManager) mContext.getSystemService("window")).getDefaultDisplay();
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        mDisplay.getRealMetrics(mDisplayMetrics);
        float[] dims = new float[]{(float) mDisplayMetrics.widthPixels, (float) mDisplayMetrics.heightPixels};
        float degrees = getDegreesForRotation(mDisplay.getRotation());
        boolean requiresRotation = degrees > 0.0f;
        if (requiresRotation) {
            mDisplayMatrix.reset();
            mDisplayMatrix.preRotate(-degrees);
            mDisplayMatrix.mapPoints(dims);
            dims[0] = Math.abs(dims[0]);
            dims[1] = Math.abs(dims[1]);
        }
        Bitmap mScreenBitmap = screenShot(dims[0], dims[1]);
        if (requiresRotation) {
            Bitmap ss = Bitmap.createBitmap(mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels, Config.RGB_565);
            Canvas c = new Canvas(ss);
            c.translate((float) (ss.getWidth() / 2), (float) (ss.getHeight() / 2));
            c.rotate(degrees);
            c.translate((-dims[0]) / 2.0f, (-dims[1]) / 2.0f);
            c.drawBitmap(mScreenBitmap, 0.0f, 0.0f, null);
            c.setBitmap(null);
            mScreenBitmap = ss;
        }
        if (mScreenBitmap != null) {
            mScreenBitmap.setHasAlpha(false);
            mScreenBitmap.prepareToDraw();
            try {
                saveBitmap(mScreenBitmap, picName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Bitmap screenShot(float a, float b) {
        try {
            return (Bitmap) Class.forName("android.view.SurfaceControl").getMethod("screenshot", new Class[]{Integer.TYPE, Integer.TYPE}).invoke(null, new Object[]{Integer.valueOf((int) a), Integer.valueOf((int) b)});
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressLint({"SimpleDateFormat"})
    public static void saveBitmap(Bitmap bitmap, String picName) throws IOException {
        File file = new File(picName);
        if (!file.exists()) {
            file.createNewFile();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (picName.contains(".jpg")) {
                bitmap.compress(CompressFormat.JPEG, 70, out);
                out.flush();
                out.close();
                return;
            }
            bitmap.compress(CompressFormat.PNG, 70, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private static float getDegreesForRotation(int value) {
        switch (value) {
            case 1:
                return 270.0f;
            case 2:
                return 180.0f;
            case 3:
                return 90.0f;
            default:
                return 0.0f;
        }
    }

    public void onDestroy() {
        super.onDestroy();
        this.serviceRun = Boolean.valueOf(false);
        this.proessRun = Boolean.valueOf(false);
    }
}
