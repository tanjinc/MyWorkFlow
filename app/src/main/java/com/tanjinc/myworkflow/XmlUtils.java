package com.tanjinc.myworkflow;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanjincheng on 17/7/18.
 */
public class XmlUtils {
    private static final String TAG = "XmlUtils";

    public static String xmlDirPath = Environment.getExternalStorageDirectory().getPath() + "/autobox";
    /**
     * 写xml
     * @param xmlName
     *
     */
    public static boolean saveXml(String xmlName, String packetName, List<AutoTestControllMsg> actions) {
        try {
            Log.d(TAG, "video saveXml: actions = " + actions.toString());

            File dirFile = new File(xmlDirPath);
            if (!dirFile.exists()) {
                dirFile.mkdir();
            }

            File file = new File(xmlDirPath, xmlName);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            // 获得一个序列化工具
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "utf-8");
            // 设置文件头
            serializer.startDocument("utf-8", true);

            serializer.startTag(null, "packetName");
            serializer.text(packetName);
            if (actions != null) {
                int i=0;
                for (AutoTestControllMsg msg : actions) {
                    Log.d(TAG, "video saveXml: " + i++ + " msg"+ msg.toString());

                    serializer.startTag(null, "idInfo");

                    serializer.startTag(null, "id");
                    serializer.text(msg.getId() != null ? msg.getId() : "");
                    serializer.endTag(null, "id");

                    serializer.startTag(null, "idInstance");
                    serializer.text(String.valueOf(msg.getIdInstance()));
                    serializer.endTag(null, "idInstance");

                    serializer.startTag(null, "text");
                    serializer.text(msg.getText() != null ? msg.getText() : "");
                    serializer.endTag(null, "text");

                    serializer.startTag(null, "textInstance");
                    serializer.text(String.valueOf(msg.getTextInstance()));
                    serializer.endTag(null, "textInstance");

                    serializer.startTag(null, "contentInstance");
                    serializer.text(String.valueOf(msg.getContentInstance()));
                    serializer.endTag(null, "contentInstance");

                    serializer.startTag(null, "content");
                    serializer.text(msg.getContent() != null ? msg.getContent() : "");
                    serializer.endTag(null, "content");

                    serializer.endTag(null, "idInfo");
                }
            }
            serializer.endTag(null, "packetName");
            serializer.endDocument();
            Log.d(TAG, "video saveXml: " + serializer.toString());
            fos.close();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "video saveXml: ", e);
            return false;
        }

    }

    /**
     * 读取xml文件
     * @param xmlName
     */
    public static void readXml(String xmlName) {

        ArrayList<AutoTestControllMsg> msgList = new ArrayList<>();

        String packetName="";

        String text="";
        int textInstance=0;
        String id = "";
        int idInstance = 0;
        String clazz = "";
        int clazzInstance = 0;
        String content = "";
        int contentInstance = 0;

        try {
            File path = new File(xmlDirPath, xmlName);
            FileInputStream fis = new FileInputStream(path);

            // 获得pull解析器对象
            XmlPullParser parser = Xml.newPullParser();
            // 指定解析的文件和编码格式
            parser.setInput(fis, "utf-8");

            int eventType = parser.getEventType(); // 获得事件类型



            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName(); // 获得当前节点的名称
                Log.d(TAG, "video readXml: " + tagName);
                switch (eventType) {

                    case XmlPullParser.START_TAG: // 当前等于开始节点 <person>
                        if ("packetName".equals(tagName)) { // <person id="1">
                            packetName = parser.getAttributeValue(null, "packetName");
                        } else if ("text".equals(tagName)) {
                            text = parser.getAttributeValue(null, "text");
                        } else if ("textInstance".equals(tagName)) {
                            textInstance = Integer.valueOf(parser.getAttributeValue(null, "textInstance"));
                        } else if ("id".equals(tagName)) {
                            id = parser.getAttributeValue(null, "id");
                        } else if ("idInstance".equals(tagName)) {
                            idInstance = Integer.valueOf(parser.getAttributeValue(null, "idInstance"));
                        } else if ("clazz".equals(tagName)) {
                            clazz = parser.getAttributeValue(null, "clazz");
                        } else if ("clazzInstance".equals(tagName)) {
                            clazzInstance = Integer.valueOf(parser.getAttributeValue(null, "clazzInstance"));
                        } else if ("content".equals(tagName)) {
                            content = parser.getAttributeValue(null, "content");
                        } else if ("contentInstance".equals(tagName)) {
                            contentInstance = Integer.valueOf(parser.getAttributeValue(null, "contentInstance"));
                        }
                        break;
                    case XmlPullParser.END_TAG: // </persons>
                        if ("packetName".equals(tagName)) {
                        }
                        break;
                    default:
                        break;
                }
                Log.d(TAG, "video readXml: packetName = " + packetName);
                Log.d(TAG, "video readXml: text= " + text + " textInstance=" + textInstance);
                Log.d(TAG, "video readXml: id= " + id + " idInstance=" + idInstance);
                Log.d(TAG, "video readXml: clazz= " + clazz + " clazzInstance=" + clazzInstance);
                Log.d(TAG, "video readXml: content= " + content + " contentInstance=" + contentInstance);
                eventType = parser.next(); // 获得下一个事件类型
            }
        } catch (Exception e) {
            Log.e(TAG, "video readXml: ",e );
        } finally {

        }
    }

    static public ArrayList<String> queryXmlFiles(){
        ArrayList<String> xmlFiles = new ArrayList<>();

        File dir = new File(xmlDirPath);
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                Log.d(TAG, "video queryXmlFiles: " + file.getName());

                xmlFiles.add(file.getName());
            }
        }
        return xmlFiles;
    }
}
