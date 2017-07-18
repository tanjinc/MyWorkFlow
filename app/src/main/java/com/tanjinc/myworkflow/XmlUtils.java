package com.tanjinc.myworkflow;

import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by tanjincheng on 17/7/18.
 */
public class XmlUtils {
    private static final String TAG = "XmlUtils";

    /**
     * 写xml
     * @param xmlName
     *
     */
    public static void saveXml(String xmlName, String packetName, List<Rect> list) {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), xmlName);
            FileOutputStream fos = new FileOutputStream(file);
            // 获得一个序列化工具
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "utf-8");
            // 设置文件头
            serializer.startDocument("utf-8", true);

            serializer.startTag(null, "packetName");
            serializer.attribute(null, "id", packetName);
            for (Rect r : list ) {
                serializer.startTag(null, "rect");
                serializer.attribute(null, "id", r.toString());
                serializer.endTag(null, "rect");
            }
            serializer.endTag(null, "packetName");

            serializer.endDocument();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 读取xml文件
     * @param xmlName
     */
    public static void readXml(String xmlName) {

        try {
            File path = new File(Environment.getExternalStorageDirectory(),xmlName);
            FileInputStream fis = new FileInputStream(path);

            // 获得pull解析器对象
            XmlPullParser parser = Xml.newPullParser();
            // 指定解析的文件和编码格式
            parser.setInput(fis, "utf-8");

            int eventType = parser.getEventType(); // 获得事件类型

            String id = null;
            String rect = null;
            String gender = null;
            String age = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName(); // 获得当前节点的名称

                switch (eventType) {
                    case XmlPullParser.START_TAG: // 当前等于开始节点 <person>
                        if ("packetName".equals(tagName)) { // <person id="1">
                            id = parser.getAttributeValue(null, "id");
                        } else if ("rect".equals(tagName)) { // <name>
                            rect = parser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG: // </persons>
                        if ("packetName".equals(tagName)) {
                            Log.i(TAG, "id---" + id);
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next(); // 获得下一个事件类型
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
}
