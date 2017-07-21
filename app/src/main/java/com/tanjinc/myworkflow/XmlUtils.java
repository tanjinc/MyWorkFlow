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
            serializer.text(autoTaskBean.getTaskName());

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


    public static void readXml2(String xmlName) {
 
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            
            File path = new File(xmlDirPath, xmlName);
            FileInputStream fis = new FileInputStream(path);

            Document document = builder.parse(fis);
            Element element = document.getDocumentElement();
            NodeList nodeList = element.getElementsByTagName("lan");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element lan = (Element) nodeList.item(i);
                Log.d(TAG, "video realXml2: " + ((Element) nodeList.item(i)).getTagName());
//                text.append(lan.getAttribute("id") + "\n");
//                text.append(lan.getElementsByTagName("name").item(0).getTextContent() + " ");
//                text.append(lan.getElementsByTagName("ide").item(0).getTextContent() + " ");
//                text.append(lan.getElementsByTagName("type").item(0).getTextContent() + " ");
//                text.append("\n");
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
            File path = new File(xmlDirPath, xmlName);
            FileInputStream fis = new FileInputStream(path);

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
//                            autoTask.setTaskName(parser.nextText());
                        } else if ("packetName".equals(tagName)) { // <person id="1">
                            packetName = parser.nextText();
                            autoTask.setPacketName(packetName);
                        } else if ("idInfo".equals(tagName)) {
                            msg = new AutoTaskNodeBean();
                        } else if ("text".equals(tagName)) {
                            msg.setText(parser.nextText());
                        } else if ("textInstance".equals(tagName)) {
                            String textInstance = parser.nextText();
                        } else if ("id".equals(tagName)) {
                            String id = parser.nextText();
                            msg.setId(id);
                        } else if ("idInstance".equals(tagName)) {
//                            idInstance = Integer.valueOf(parser.getText());
                        } else if ("clazz".equals(tagName)) {
                            String clazz = parser.getText();
                            msg.setClazz(clazz);
                        } else if ("clazzInstance".equals(tagName)) {
//                            clazzInstance = Integer.valueOf(parser.nextText());
                        } else if ("content".equals(tagName)) {
                            String content = parser.nextText();
                            msg.setContent(content);
                        } else if ("contentInstance".equals(tagName)) {
//                            contentInstance = Integer.valueOf(parser.nextText());
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
