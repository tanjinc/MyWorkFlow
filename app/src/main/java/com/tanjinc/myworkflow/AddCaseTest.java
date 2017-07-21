package com.tanjinc.myworkflow;

import java.util.ArrayList;

/**
 * Created by maxueming on 2017/7/21.
 */

public class AddCaseTest {

    public static ArrayList<AutoTaskNodeBean> getCaseList(){
        ArrayList<AutoTaskNodeBean> list = new ArrayList<AutoTaskNodeBean>();
        AutoTaskNodeBean msg1 = new AutoTaskNodeBean();
        msg1.clear();
        msg1.setText("录制 Log");
        msg1.setTextInstance(1);
        msg1.setId("com.meizu.logreport:id/pro_mode_button");
        msg1.setIdInstance(1);
        msg1.setClazz("android.widget.Button");
        msg1.setClazzInstance(2);
        list.add(msg1);

        AutoTaskNodeBean msg2 = new AutoTaskNodeBean();
        msg2.clear();
        msg2.setClazz("android.widget.RelativeLayout");
        msg2.setClazzInstance(3);
        list.add(msg2);

        AutoTaskNodeBean msg3 = new AutoTaskNodeBean();
        msg3.clear();
        msg3.setClazz("android.widget.RelativeLayout");
        msg3.setClazzInstance(4);
        list.add(msg3);

        return list;
    }

    public static ArrayList<AutoTaskNodeBean> getCaseList2(){
        ArrayList<AutoTaskNodeBean> list = new ArrayList<AutoTaskNodeBean>();
        AutoTaskNodeBean msg1 = new AutoTaskNodeBean();
        msg1.clear();
        msg1.setActionType("click");
        msg1.setContent("搜索");
        msg1.setContentInstance(1);
        msg1.setClazz("android.widget.TextView");
        msg1.setClazzInstance(8);
        list.add(msg1);

        AutoTaskNodeBean msg2 = new AutoTaskNodeBean();
        msg2.clear();
        msg2.setActionType("click");
        msg2.setText("搜索");
        msg2.setTextInstance(1);
        msg2.setClazz("com.meizu.common.widget.SearchEditText");
        msg2.setClazzInstance(1);
        list.add(msg2);

        AutoTaskNodeBean msg3 = new AutoTaskNodeBean();
        msg3.clear();
        msg3.setActionType("inputText");
        msg3.setInputText("哈哈测试");
        msg3.setText(null);
        msg3.setTextInstance(0);
        msg3.setClazz("com.meizu.common.widget.SearchEditText");
        msg3.setClazzInstance(1);
        list.add(msg3);

        AutoTaskNodeBean msg4 = new AutoTaskNodeBean();
        msg4.clear();
        msg4.setActionType("inputText");
        msg4.setInputText("再测试一下");
        msg4.setText(null);
        msg4.setTextInstance(0);
        msg4.setClazz("com.meizu.common.widget.SearchEditText");
        msg4.setClazzInstance(1);
        list.add(msg4);

        return list;
    }



}
