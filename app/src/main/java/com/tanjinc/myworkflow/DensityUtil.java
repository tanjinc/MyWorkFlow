package com.tanjinc.myworkflow;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;


/**
 * 分辨率转换，获取手机界面尺寸大小相关
 * Created by maxueming on 2017/6/6.
 */
public class DensityUtil {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取通知栏高度
     * @return int
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen","android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    /**
     * 返回View的高度和宽度
     * @param view 视图
     * @param a true 返回高度，否则返回宽度
     * @return
     */
    public static int getViewWidthHeight(View view, boolean a){
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height =View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(width,height);
        return a? view.getMeasuredHeight() : view.getMeasuredWidth();
    }
}
