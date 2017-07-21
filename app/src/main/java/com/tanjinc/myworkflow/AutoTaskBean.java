package com.tanjinc.myworkflow;

import java.util.ArrayList;

/**
 * Created by tanjincheng on 17/7/21.
 */
public class AutoTaskBean {
    String taskName;    //任务名
    String packetName;  //包名
    ArrayList<AutoTaskNodeBean> nodeArray;   //控件信息

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getPacketName() {
        return packetName;
    }

    public void setPacketName(String packetName) {
        this.packetName = packetName;
    }

    public ArrayList<AutoTaskNodeBean> getNodeArray() {
        return nodeArray;
    }

    public void setNodeArray(ArrayList<AutoTaskNodeBean> nodeArray) {
        this.nodeArray = nodeArray;
    }

    @Override
    public String toString() {
        return "AutoTaskBean{" +
                "taskName='" + taskName + '\'' +
                ", packetName='" + packetName + '\'' +
                ", nodeArray=" + nodeArray +
                '}';
    }
}
