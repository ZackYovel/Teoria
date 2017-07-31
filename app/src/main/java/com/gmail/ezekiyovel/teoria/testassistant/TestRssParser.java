package com.gmail.ezekiyovel.teoria.testassistant;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TestRssParser {
    public static final int FORMAL_QUESTION_COUNT = 1900;
    private int balance = 1;

    public List<QuestionTitleAndHtml> parse(InputStream inputStream)
            throws XmlPullParserException, IOException {
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

    private List<QuestionTitleAndHtml> readFeed(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        ArrayList<QuestionTitleAndHtml> result = new ArrayList<>(FORMAL_QUESTION_COUNT);
        int balanceSnapshot = balance;
        while (balance > balanceSnapshot - 1) {
            parser.next();
            int eventType = parser.getEventType();
            if (eventType == XmlPullParser.START_TAG && parser.getName().equals("item")) {
                result.add(readItem(parser));
                balance++;
            } else if (eventType == XmlPullParser.START_TAG) {
                balance++;
            } else if (eventType == XmlPullParser.END_TAG) {
                balance--;
            }
        }
        return result;
    }

    private QuestionTitleAndHtml readItem(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        QuestionTitleAndHtml result = new QuestionTitleAndHtml();
        int balanceSnapshot = balance;
        while (balance > balanceSnapshot - 1) {
            parser.next();
            int eventType = parser.getEventType();
            if (eventType == XmlPullParser.START_TAG) {
                switch (parser.getName()) {
                    case "title":
                        result.setTitle(readText(parser));
                        break;
                    case "description":
                        result.setHtml(readText(parser));
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

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = null;
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
}
