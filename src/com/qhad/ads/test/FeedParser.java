package com.qhad.ads.test;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;


public class FeedParser extends DefaultHandler {

    public ArrayList<HashMap> maps = new ArrayList<HashMap>();

    private HashMap map;

    private String key;

    private String value;

    /*
     * 此方法有三个参数 arg0是传回来的字符数组，其包含元素内容 arg1和arg2分别是数组的开始位置和结束位置
     */
    @Override
    public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
        String content = new String(arg0, arg1, arg2);
        value += content;

        super.characters(arg0, arg1, arg2);
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    /*
     * arg0是名称空间 arg1是包含名称空间的标签，如果没有名称空间，则为空 arg2是不包含名称空间的标签
     */
    @Override
    public void endElement(String arg0, String arg1, String arg2)
            throws SAXException {

        if (key != "" && value != "" && map != null) {

            if (key == "description") {
                value = value.substring(0, 50) + "...";
            }

            map.put(key, value);
        }

        if (arg2 == "item") {
            maps.add(map);
            map = null;
        }

        super.endElement(arg0, arg1, arg2);
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    /*
     * arg0是名称空间 arg1是包含名称空间的标签，如果没有名称空间，则为空 arg2是不包含名称空间的标签 arg3很明显是属性的集合
     */
    @Override
    public void startElement(String arg0, String arg1, String arg2,
                             Attributes arg3) throws SAXException {
        if (arg2 == "item") {
            map = new HashMap<String, String>();
        }

        key = "";
        value = "";

        if (arg2 == "title") {
            key = "title";
        }

        if (arg2 == "description") {
            key = "description";
        }

        if (arg2 == "image") {
            key = "image";
        }

        if (arg2 == "link") {
            key = "link";
        }


        super.startElement(arg0, arg1, arg2, arg3);
    }

}

