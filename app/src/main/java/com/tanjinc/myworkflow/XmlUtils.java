package com.tanjinc.myworkflow;

import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
    public static boolean saveXml(String xmlName, AutoTaskBean autoTaskBean) {
        try {
            Log.d(TAG, "video saveXml: actions = " + autoTaskBean.toString());

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

            serializer.startTag(null, "taskName");
            serializer.attribute(null, "taskId", autoTaskBean.getTaskName());

            serializer.startTag(null, "packetName");
            serializer.text(autoTaskBean.getPacketName());
            serializer.endTag(null, "packetName");

            serializer.startTag(null, "idInfos");
            int i=0;
            for (AutoTaskNodeBean msg : autoTaskBean.getNodeArray()) {
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

                serializer.startTag(null, "clazz");
                serializer.text(msg.getClazz() != null ? msg.getClazz() : "");
                serializer.endTag(null, "clazz");

                serializer.startTag(null, "clazzInstance");
                serializer.text(String.valueOf(msg.getClazzInstance()));
                serializer.endTag(null, "clazzInstance");

                serializer.startTag(null, "contentInstance");
                serializer.text(String.valueOf(msg.getContentInstance()));
                serializer.endTag(null, "contentInstance");

                serializer.startTag(null, "content");
                serializer.text(msg.getContent() != null ? msg.getContent() : "");
                serializer.endTag(null, "content");

                serializer.startTag(null, "actionType");
                serializer.text(msg.getActionType() != null ? msg.getActionType() : "");
                serializer.endTag(null, "actionType");

                serializer.startTag(null, "inputText");
                serializer.text(msg.getInputText() != null ? msg.getInputText() : "");
                serializer.endTag(null, "inputText");

                serializer.endTag(null, "idInfo");
            }
            serializer.endTag(null, "idInfos");
            serializer.endTag(null, "taskName");
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
    public static AutoTaskBean readXml(String xmlName) {

        AutoTaskBean autoTask = new AutoTaskBean();

        ArrayList<AutoTaskNodeBean> nodeList = new ArrayList<>();

        String packetName="";



        try {
            File xmlFile = new File(xmlDirPath, xmlName);
            FileInputStream fis = new FileInputStream(xmlFile);
            if (!xmlFile.exists()) {
                Log.e(TAG, "video readXml: " + xmlName + " not exit");
                return null;
            }

            // 获得pull解析器对象
            XmlPullParser parser = Xml.newPullParser();
            // 指定解析的文件和编码格式
            parser.setInput(fis, "utf-8");

            int eventType = parser.getEventType(); // 获得事件类型



            AutoTaskNodeBean msg = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName(); // 获得当前节点的名称
//                Log.d(TAG, "video readXml ====== : " + tagName);
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        Log.d(TAG, "video readXml START_DOCUMENT: " + parser.getText());
                        break;

                    case XmlPullParser.START_TAG: // 当前等于开始节点 <person>

                        if ("taskName".equals(tagName)) {
                            autoTask.setTaskName(parser.getAttributeValue(0));
                        } else if ("packetName".equals(tagName)) { // <person id="1">
                            packetName = parser.nextText();
                            autoTask.setPacketName(packetName);
                        } else if ("idInfo".equals(tagName)) {
                            msg = new AutoTaskNodeBean();
                        } else if ("text".equals(tagName)) {
                            msg.setText(parser.nextText());
                        } else if ("textInstance".equals(tagName)) {
                            String textInstance = parser.nextText();
                            if (textInstance != null) {
                                msg.setTextInstance(Integer.valueOf(textInstance));
                            }
                        } else if ("id".equals(tagName)) {
                            String id = parser.nextText();
                            msg.setId(id);
                        } else if ("idInstance".equals(tagName)) {
                            String idInstance = parser.nextText();
                            if (idInstance != null) {
                                msg.setIdInstance(Integer.valueOf(idInstance));
                            }
                        } else if ("clazz".equals(tagName)) {
                            String clazz = parser.nextText();
                            msg.setClazz(clazz);
                        } else if ("clazzInstance".equals(tagName)) {
                            String clazzInstance = parser.nextText();
                            if (clazzInstance != null) {
                                msg.setClazzInstance(Integer.valueOf(clazzInstance));
                            }
                        } else if ("content".equals(tagName)) {
                            String content = parser.nextText();
                            msg.setContent(content);
                        } else if ("contentInstance".equals(tagName)) {
                            String contentInstance = parser.nextText();
                            if (contentInstance != null) {
                                msg.setContentInstance(Integer.valueOf(contentInstance));
                            }
                        } else if ("actionType".equals(tagName)) {
                            String actionType = parser.nextText();
                            msg.setActionType(actionType);
                        } else if ("inputText".equals(tagName)) {
                            msg.setInputText(parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG: // </persons>
                        if ("idInfo".equals(tagName)) {
                            Log.d(TAG, "video readXml: " + msg.toString());
                            nodeList.add(msg);
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next(); // 获得下一个事件类型
            }
        } catch (Exception e) {
            Log.e(TAG, "video readXml: ",e );
        } finally {

        }
        autoTask.setNodeArray(nodeList);
        return autoTask;
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
