package com.apps.ezekiel.teoria.networking;

import com.apps.ezekiel.teoria.BaseActivity;
import com.apps.ezekiel.teoria.entity.QuestionItem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class DataIntegrityTest {

    private final long[] CLASSES_BIN = {
            QuestionItem.CLASS_BIN_A,
            QuestionItem.CLASS_BIN_A1,
            QuestionItem.CLASS_BIN_A2,
            QuestionItem.CLASS_BIN_B,
            QuestionItem.CLASS_BIN_C,
            QuestionItem.CLASS_BIN_C1,
            QuestionItem.CLASS_BIN_CPlusE,
            QuestionItem.CLASS_BIN_D,
            QuestionItem.CLASS_BIN_D1,
            QuestionItem.CLASS_BIN_D3,
            QuestionItem.CLASS_BIN_1
    };
    private final String[] CLASSES_STR = {
            QuestionItem.CLASS_STR_A,
            QuestionItem.CLASS_STR_B,
            QuestionItem.CLASS_STR_CYRILLIC_VE,
            QuestionItem.CLASS_STR_C,
            QuestionItem.CLASS_STR_C1,
            QuestionItem.CLASS_STR_CPlusE,
            QuestionItem.CLASS_STR_D,
            QuestionItem.CLASS_STR_D1,
            QuestionItem.CLASS_STR_D3,
            QuestionItem.CLASS_STR_1
    };

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testParse() throws Exception {
        try {
            URL url = new URL(BaseActivity.UPDATE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            int responseCode = connection.getResponseCode();
            assertEquals("http error", 200, responseCode);
            InputStream inputStream = connection.getInputStream();
            byte[] bytes = new Scanner(inputStream).useDelimiter("><><").next().getBytes();
            assertTrue("received empty rss", bytes.length > 0);
            RssParser rssParser = new RssParser();
            List<QuestionItem> questionItems = rssParser.parse(new ByteArrayInputStream(bytes));
            HashMap<String, QuestionItem> questionMap = new HashMap<>(questionItems.size());
            for (QuestionItem item : questionItems) {
                questionMap.put(item.getTitle(), item);
            }
            List<QuestionTitleAndHtml> questionTitleAndHtmls = new TestRssParser()
                    .parse(new ByteArrayInputStream(bytes));
            for (QuestionTitleAndHtml questionTitleAndHtml : questionTitleAndHtmls) {
                QuestionItem matchingQuestionItem = questionMap.get(questionTitleAndHtml.getTitle());
                for (String cls : CLASSES_STR) {
                    if (questionTitleAndHtml.getHtml().contains("«" + getCorrectClass(cls) + "»")) {
                        assertTrue("Class " + cls + " should be relevant, but is not.",
                                matchingQuestionItem
                                        .isRelevantToClass(QuestionItem.binClassForStrClass(cls)));
                    } else {
                        assertFalse("Class " + cls + " shouldn't be relavant, but it is.",
                                matchingQuestionItem
                                        .isRelevantToClass(QuestionItem.binClassForStrClass(cls)));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getCorrectClass(String cls) {
        if (cls.equals(QuestionItem.CLASS_STR_B)) {
            cls = QuestionItem.CLASS_STR_CYRILLIC_VE;
        }
        return cls;
    }
}

