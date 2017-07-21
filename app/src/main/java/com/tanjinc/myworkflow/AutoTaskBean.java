package com.tanjinc.myworkflow;

import java.util.ArrayList;

/**
 * Created by tanjincheng on 17/7/21.
 */
public class AutoTaskBean {
    String taskName;    //任务名
    ArrayList<AutoTestControllMsg> nodeArray;   //控件信息

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public ArrayList<AutoTestControllMsg> getNodeArray() {
        return nodeArray;
    }

    public void setNodeArray(ArrayList<AutoTestControllMsg> nodeArray) {
        this.nodeArray = nodeArray;
    }
}
