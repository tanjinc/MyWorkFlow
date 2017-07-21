package com.tanjinc.myworkflow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

/**
 * 悬浮球View
 * Created by maxueming on 2017/6/6.
 */
public class FloatBall extends View {

    public int width;
    public int height;
    private String text = "";
    private Paint ballPaint;
    private Paint textPaint;
    private Context mContext;
    private int status = 0;
    private final int UPDATE_VIEW = 1000;

    public FloatBall(Context context) {
        super(context);
        init(context);
    }

    public FloatBall(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FloatBall(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        mContext = context;
        width = DensityUtil.dip2px(context, 50);
        height = DensityUtil.dip2px(context, 50);

        ballPaint = new Paint();
        ballPaint.setColor(context.getResources().getColor(R.color.colorPrimary));
        ballPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setTextSize(DensityUtil.dip2px(context, 15));
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);
    }



    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_VIEW:
                    //更新
                    if(status == 0){
                        ballPaint.setColor(mContext.getResources().getColor(R.color.colorPrimary));
                        status = 1;
                    }else{
                        ballPaint.setColor(mContext.getResources().getColor(R.color.colorAccent));
                        status = 0;
                    }
                    invalidate();
                    handler.sendEmptyMessageDelayed(UPDATE_VIEW, 1000);
                    break;
            }
        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(UPDATE_VIEW);
        }
    };



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(width / 2, height / 2, width / 2, ballPaint);
        float textWidth = textPaint.measureText(text);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float dy = -(fontMetrics.descent + fontMetrics.ascent) / 2;
        canvas.drawText(text, width / 2 - textWidth / 2, height / 2 + dy, textPaint);
    }

    public void setFloatBallText(String text){
        this.text = text;
        invalidate();
    }

    public void isRecord(boolean isStart){
        if(isStart){
            handler.post(runnable);
        }else{
            handler.removeCallbacks(runnable);
            handler.removeMessages(UPDATE_VIEW);
        }
    }

}