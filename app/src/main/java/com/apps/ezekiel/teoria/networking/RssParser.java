package com.apps.ezekiel.teoria.networking;

import android.util.Xml;

import com.apps.ezekiel.teoria.entity.QuestionItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RssParser {

    // At the time of writing this code, there are 1800 approx. questions (as reported at:
    // http://tq.mot.gov.il/index.php?option=com_content&view=article&id=1:mivhan-iuni-herkev&catid=8:help-info&Itemid=52)
    // or exactly 1802 (by count) in the official collection of questions published by the ministry
    // of transportation. The 1900 constant is an extra value used to avoid reallocating the
    // list of QuestionItems, by providing a little more space then counted, only because the
    // official report is non-determinate and I can't say that the actual count wont change.
    // If the count of questions in the official collection changes to exceed the value of
    // FORMAT_QUESTION_COUNT, it is beneficial for app performance to update this value
    // (though the app should not break if not updated).
    public static final int FORMAL_QUESTION_COUNT = 1900;
    private int balance = 1;

    public List<QuestionItem> parse(InputStream inputStream)
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

    private List<QuestionItem> readFeed(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        ArrayList<QuestionItem> result = new ArrayList<>(FORMAL_QUESTION_COUNT);
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

    private QuestionItem readItem(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        QuestionItem result = new QuestionItem();
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
                        parseDescription(result, readText(parser));
                        break;
                    case "category":
                        result.setCategory(readText(parser));
                        break;
                    case "pubDate":
                        result.setPubDate(readText(parser));
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

    private void parseDescription(QuestionItem result, String htmlDescription)
            throws IOException, XmlPullParserException {
        HtmlContent content = new HtmlParser().parse(htmlDescription);
        result.setOptions(content.options);
        for (String classStr : content.classes) {
            String cls = classStr.substring(1, classStr.length() - 1);
            result.addClass(cls);
        }
        result.setCorrectAnswerIndex(content.correctAnswer);
        result.setImage(content.image);
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = null;
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    public static class HtmlContent {
        String[] options = new String[4];
        int optionsIndex;
        HashSet<String> classes = new HashSet<>(11);
        int correctAnswer;
        String image;
    }
}
