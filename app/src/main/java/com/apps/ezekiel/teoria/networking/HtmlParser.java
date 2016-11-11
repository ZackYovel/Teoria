package com.apps.ezekiel.teoria.networking;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class HtmlParser {

    public static final int OPTION_COUNT = 4;
    private int balance = 1;

    public RssParser.HtmlContent parse(String html) throws XmlPullParserException, IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(html.getBytes());
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            inputStream.close();
        }
    }

    private RssParser.HtmlContent readFeed(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        RssParser.HtmlContent result = new RssParser.HtmlContent();
        int balanceSnapshot = balance;
        while (balance > balanceSnapshot - 1) {
            parser.next();
            int eventType = parser.getEventType();
            if (eventType == XmlPullParser.START_TAG) {
                switch (parser.getName()) {
                    case "ul":
                        readOptions(parser, result);
                        balance++;
                        break;
                    case "img":
                        result.image = readImage(parser);
                        break;
                    case "div":
                        result.classes = readClasses(parser);
                        break;
                    default:
                        balance++;
                        break;
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                balance--;
            }
        }
        return result;
    }

    private HashSet<String> readClasses(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        HashSet<String> result = null;
        int balanceSnapshot = balance;
        while (balance > balanceSnapshot - 1) {
            parser.next();
            int eventType = parser.getEventType();
            if (eventType == XmlPullParser.START_TAG){
                if (parser.getName().equals("span")) {
                    result = tryReadClasses(parser);
                }
                balance++;
            } else if (eventType == XmlPullParser.END_TAG){
                balance--;
            }
        }
        return result;
    }

    private HashSet<String> tryReadClasses(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        HashSet<String> result = null;
        int balanceSnapshot = balance;
        String name = "";
        while (balance > balanceSnapshot - 1) {
            parser.next();
            int eventType = parser.getEventType();
            if (eventType == XmlPullParser.START_TAG) {
                name = parser.getName();
                balance++;
            } else if (eventType == XmlPullParser.END_TAG) {
                balance--;
            } else if (eventType == XmlPullParser.TEXT && !name.equals("button")){
                String classesText = parser.getText();
                result = new HashSet<>(Arrays.asList(classesText.split(" | ")));
                result.remove("|");
            }
        }
        return result;
    }

    private String readImage(XmlPullParser parser) throws IOException, XmlPullParserException {
        String image = parser.getAttributeValue(null, "src");
        parser.nextTag();
        return image;
    }

    private void readOptions(XmlPullParser parser, RssParser.HtmlContent result)
            throws IOException, XmlPullParserException {
        int balanceSnapshot = balance;
        while (balance > balanceSnapshot - 1) {
            parser.next();
            int eventType = parser.getEventType();
            if (eventType == XmlPullParser.START_TAG && parser.getName().equals("li")) {
                readOption(parser, result);
            } else if (eventType == XmlPullParser.START_TAG) {
                balance++;
            } else if (eventType == XmlPullParser.END_TAG) {
                balance--;
            }
        }
    }

    private void readOption(XmlPullParser parser, RssParser.HtmlContent result)
            throws XmlPullParserException, IOException {
        parser.next();
        String id = parser.getAttributeValue(null, "id");
        if (id != null && id.startsWith("correctAnswer")) {
            result.correctAnswer = result.optionsIndex;
        }
        result.options[result.optionsIndex++] = readText(parser);
        balance++;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = null;
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
}
