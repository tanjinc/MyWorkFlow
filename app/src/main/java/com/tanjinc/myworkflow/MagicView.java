package com.tanjinc.myworkflow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by tanjincheng on 17/7/15.
 */
public class MagicView extends View {

    private static final String TAG = "MagicView";
    private String mTextInfo;
    private int mLastX;
    private int mLastY;

    public MagicView(Context context) {
        super(context);
    }

    public MagicView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MagicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        Paint paint = new Paint();
        paint.setTextSize(16);
        paint.setColor(Color.WHITE);
        if (mTextInfo != null) {
            canvas.drawText(mTextInfo, width / 2, height / 2, paint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setTextInfo(String textInfo) {
        mTextInfo = textInfo;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
//                autoAttach();
                break;
            case MotionEvent.ACTION_MOVE:
                //计算移动的距离
                int offX = x - mLastX;
                int offY = y - mLastY;
                //调用layout方法来重新放置它的位置
                layout(getLeft()+offX, getTop()+offY,
                        getRight()+offX  , getBottom()+offY);
                break;
        }
        return true;
    }

    private void autoAttach() {
        int left = getLeft() > 0 ? getLeft() : 0;
        int right = getRight() > 0 ? getRight() : 0;
        layout(left, getTop(), right, getBottom());
    }
}
