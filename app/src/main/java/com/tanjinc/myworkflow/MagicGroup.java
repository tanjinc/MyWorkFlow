package com.tanjinc.myworkflow;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by tanjincheng on 17/7/15.
 */
public class MagicGroup extends RelativeLayout {

    public MagicGroup(Context context) {
        super(context);
        init();
    }

    public MagicGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MagicGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }

    public void addMagicView(String name) {
        MagicView magicView = new MagicView(getContext());
        magicView.setBackgroundColor(Color.BLUE);
        magicView.setTextInfo(name);
        addView(magicView, new ViewGroup.LayoutParams(200, 200));
    }

}
