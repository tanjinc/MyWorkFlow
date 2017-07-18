package com.tanjinc.myworkflow;

/**
 * Created by maxueming on 2017/7/18.
 */

public class AutoTestControllMsg {
    private String text;
    private int textInstance;
    private String id;
    private int idInstance;
    private String clazz;
    private int clazzInstance;
    private String content;
    private int contentInstance;

    public void clear(){
        text = null;
        textInstance = 0;
        id = null;
        idInstance = 0;
        clazz = null;
        clazzInstance = 0;
        content = null;
        contentInstance = 0;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTextInstance() {
        return textInstance;
    }

    public void setTextInstance(int textInstance) {
        this.textInstance = textInstance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIdInstance() {
        return idInstance;
    }

    public void setIdInstance(int idInstance) {
        this.idInstance = idInstance;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public int getClazzInstance() {
        return clazzInstance;
    }

    public void setClazzInstance(int clazzInstance) {
        this.clazzInstance = clazzInstance;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getContentInstance() {
        return contentInstance;
    }

    public void setContentInstance(int contentInstance) {
        this.contentInstance = contentInstance;
    }

    @Override
    public String toString() {
        return "控件信息{" +
                "text='" + text + '\'' +
                ", textInstance=" + textInstance +
                ", id='" + id + '\'' +
                ", idInstance=" + idInstance +
                ", clazz='" + clazz + '\'' +
                ", clazzInstance=" + clazzInstance +
                ", content='" + content + '\'' +
                ", contentInstance=" + contentInstance +
                '}';
    }
}
