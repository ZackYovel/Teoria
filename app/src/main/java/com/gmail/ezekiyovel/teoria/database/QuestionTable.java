package com.gmail.ezekiyovel.teoria.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.gmail.ezekiyovel.teoria.entity.QuestionItem;


class QuestionTable implements BaseColumns {
    static final String TABLE_NAME = "Question";
    static final String _TITLE = "title";
    static final String _OPTION1 = "option1";
    static final String _OPTION2 = "option2";
    static final String _OPTION3 = "option3";
    static final String _OPTION4 = "option4";
    static final String _CORRECT_ANSWER = "correct_answer";
    static final String _CATEGORY = "category";
    static final String _PUB_DATE = "pub_date";
    static final String _IMAGE = "image";
    static final String _ANSWER_ATTEMPTS = "answer_attempts";
    static final String _DISPLAY_COUNT = "display_count";
    static final String _CLASSES = "classes";

    static final String CREATE = "CREATE TABLE " + TABLE_NAME + "(" +
            _ID + " INTEGER PRIMARY KEY, " +
            _TITLE + " TEXT UNIQUE, " +
            _OPTION1 + " TEXT, " +
            _OPTION2 + " TEXT, " +
            _OPTION3 + " TEXT, " +
            _OPTION4 + " TEXT, " +
            _CORRECT_ANSWER + " INTEGER, " +
            _CATEGORY + " TEXT, " +
            _PUB_DATE + " TEXT, " +
            _IMAGE + " TEXT, " +
            _ANSWER_ATTEMPTS + " INTEGER, " +
            _DISPLAY_COUNT + " INTEGER, " +
            _CLASSES + " INTEGER" +
            ");";

    static final String DELETE = "DELETE TABLE " + TABLE_NAME + " IF EXISTS;";

    private static final int INDEX_OF_ID = 0;
    private static final int INDEX_OF_TITLE = 1;
    private static final int INDEX_OF_OPTION_1 = 2;
    private static final int INDEX_OF_OPTION_2 = 3;
    private static final int INDEX_OF_OPTION_3 = 4;
    private static final int INDEX_OF_OPTION_4 = 5;
    private static final int INDEX_OF_CORRECT_ANSWER_INDEX = 6;
    private static final int INDEX_OF_CATEGORY = 7;
    private static final int INDEX_OF_PUB_DATE = 8;
    private static final int INDEX_OF_IMAGE = 9;
    private static final int INDEX_OF_ANSWER_ATTEMPTS = 10;
    private static final int INDEX_OF_DISPLAYED_COUNT = 11;
    private static final int INDEX_OF_CLASSES = 12;

    static ContentValues contentValues(QuestionItem item) {
        ContentValues result = new ContentValues();
        result.put(_TITLE, item.getTitle());
        result.put(_OPTION1, item.getOptions()[0]);
        result.put(_OPTION2, item.getOptions()[1]);
        result.put(_OPTION3, item.getOptions()[2]);
        result.put(_OPTION4, item.getOptions()[3]);
        result.put(_CORRECT_ANSWER, item.getCorrectAnswerIndex());
        result.put(_CATEGORY, item.getCategory());
        result.put(_PUB_DATE, item.getPubDate());
        result.put(_IMAGE, item.getImage());
        // WARNING: DO NOT PUT _ANSWER_ATTEMPTS OR _DISPLAY_COUNT IN THIS CONTENT VALUES!
        // IT WILL DELETE LOCALLY CREATED STATE OF TRAINING FROM DATABASE
//        result.put(_ANSWER_ATTEMPTS, item.getAnswerAttempts());
//        result.put(_DISPLAY_COUNT, item.getDisplayedCount());
        result.put(_CLASSES, item.getClasses());
        return result;
    }

    static QuestionItem questionItemFromCursor(Cursor cursor) {
        QuestionItem result = new QuestionItem();
        result.setId(cursor.getInt(INDEX_OF_ID));
        result.setTitle(cursor.getString(INDEX_OF_TITLE));
        String[] options = {
                cursor.getString(INDEX_OF_OPTION_1),
                cursor.getString(INDEX_OF_OPTION_2),
                cursor.getString(INDEX_OF_OPTION_3),
                cursor.getString(INDEX_OF_OPTION_4)
        };
        result.setOptions(options);
        result.setCorrectAnswerIndex(cursor.getInt(INDEX_OF_CORRECT_ANSWER_INDEX));
        result.setCategory(cursor.getString(INDEX_OF_CATEGORY));
        result.setPubDate(cursor.getString(INDEX_OF_PUB_DATE));
        result.setImage(cursor.getString(INDEX_OF_IMAGE));
        result.setAnswerAttempts(cursor.getInt(INDEX_OF_ANSWER_ATTEMPTS));
        result.setDisplayedCount(cursor.getInt(INDEX_OF_DISPLAYED_COUNT));
        result.setClasses(cursor.getLong(INDEX_OF_CLASSES));
        return result;
    }
}
