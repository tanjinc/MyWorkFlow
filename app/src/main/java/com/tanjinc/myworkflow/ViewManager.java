package com.tanjinc.myworkflow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.tanjinc.myworkflow.utils.Utils;


/**
 * 性能测试悬浮球定制页面
 * Created by maxueming on 2017/6/6.
 */
public class ViewManager implements View.OnClickListener{

    private static ViewManager manager;
    private Context context;
    private WindowManager windowManager = null;
    private FloatBall floatBall = null;
    private boolean isFloatBallHide = true;
    private int screeenWidth, screeenHeight, edgeDistance;
    private WindowManager.LayoutParams floatBallParams = null;
    private static final int UPDATE_U2_TASK_LIST = 100;
    private static final int SHOW_U2_TASK_LIST = 200;
    private static final int SHOW_LOADING = 300;
    private static final int SET_RUNTEST_BUTTON = 400;

    private ViewManager(Context context) {
        init(context);
    }

    public static ViewManager getInstance(Context context){
        if (manager == null) {
            manager = new ViewManager(context);
        }
        return manager;
    }

    public FloatBall getFloatBallView(){
        return floatBall;
    }


    public void init(final Context context) {
        this.context = context;
        floatBall = new FloatBall(this.context);
        floatBall.setFloatBallText("录制中");
        windowManager = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
        screeenWidth = windowManager.getDefaultDisplay().getWidth();
        screeenHeight = windowManager.getDefaultDisplay().getHeight();
        edgeDistance = Utils.DensityUtil.dip2px(this.context, 15);
        floatBall.setOnClickListener(onClickListener);
    }



    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


        }
    };




    // 处理刷新界面
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
        }
    };

    public void isRecord(boolean isRecord){
        floatBall.isRecord(isRecord);
    }


    /**
     * 展示悬浮球
     */
    public void showFloatBall(){
        if (floatBallParams == null) {
            floatBallParams = new WindowManager.LayoutParams();
            floatBallParams.x = Math.round(screeenWidth - floatBall.width - edgeDistance);
            floatBallParams.y = Math.round(screeenHeight - floatBall.height
                    - edgeDistance - Utils.DensityUtil.getStatusBarHeight(context));
            floatBallParams.width = floatBall.width;
            floatBallParams.height = floatBall.height;
            floatBallParams.gravity = Gravity.TOP | Gravity.START;
            floatBallParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            floatBallParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            floatBallParams.format = PixelFormat.RGBA_8888;
        }

        if(isFloatBallHide){
            windowManager.addView(floatBall, floatBallParams);
            isFloatBallHide = false;
        }

    }


    public void hideFloatBall() {
        if (floatBall != null && !isFloatBallHide) {
            windowManager.removeView(floatBall);
            isFloatBallHide = true;
        }
    }


    public void cancelCallBackHandler(){
        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onClick(View v) {

    }
}
