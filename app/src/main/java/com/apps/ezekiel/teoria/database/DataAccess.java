package com.apps.ezekiel.teoria.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

import com.apps.ezekiel.teoria.entity.QuestionItem;

import java.util.ArrayList;
import java.util.List;

public class DataAccess {

    private static final String TAG = "DataAccess";
    private static final String ORDER_BY_DISPLAY_COUNT = QuestionTable._DISPLAY_COUNT + " ASC, " +
            QuestionTable._ANSWER_ATTEMPTS + " DESC, RANDOM()";
    private static final String ORDER_BY_RANDOM = "RANDOM()";
    private final Helper helper;

    public DataAccess(Context context) {
        this.helper = Helper.getInstance(context);
    }

    public boolean save(List<QuestionItem> questionItems) {
        boolean result = false;
        SQLiteDatabase writableDatabase = helper.getWritableDatabase();
        for (QuestionItem item : questionItems) {

            ContentValues contentValues = QuestionTable.contentValues(item);

            // Try to update existing row
            int update = writableDatabase.update(QuestionTable.TABLE_NAME, contentValues,
                    QuestionTable._TITLE + " = ?", new String[]{String.valueOf(item.getTitle())});

            // If no rows affected insert a new row
            if (update == 0) {
                long id = writableDatabase.insert(QuestionTable.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    item.setId(id);
                    result = true;
                } else {
                    result = false;
                }
            } else {
                result = true;
            }
        }
        return result;
    }

    public List<String> readCategories() {
        List<String> result;
        SQLiteDatabase readableDatabase = helper.getReadableDatabase();
        Cursor cursor = readableDatabase.query(QuestionTable.TABLE_NAME,
                new String[]{QuestionTable._CATEGORY}, null, null, QuestionTable._CATEGORY,
                null, QuestionTable._CATEGORY + " ASC");
        result = new ArrayList<>(cursor.getCount());
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        Log.i(TAG, "readCategories produced categories");
        return result;
    }

    public List<QuestionItem> getAllQuestions() {
        return getAllQuestionsForClass(-1);
    }

    public List<QuestionItem> getAllQuestionsForClass(long cls) {
        return getAllQuestionsForClassUpTo(cls, ORDER_BY_DISPLAY_COUNT, null);
    }

    public List<QuestionItem> getAllQuestionsForClassUpTo(long cls, int quantity) {
        return getAllQuestionsForClassUpTo(cls, ORDER_BY_RANDOM,
                quantity > 0 ? String.valueOf(quantity) : null);
    }

    private List<QuestionItem> getAllQuestionsForClassUpTo(long cls, String orderBy,
                                                           String quantity) {
        return getQuestionsFromCategoryForClassUpTo(cls, null, orderBy, quantity);
    }

    public List<QuestionItem> getQuestionsFromCategoryForClass(long cls, String category) {
        return getQuestionsFromCategoryForClassUpTo(cls, category, ORDER_BY_DISPLAY_COUNT, null);
    }

    public List<QuestionItem> getQuestionsFromCategoryForClassUpTo(
            long cls, String category, int quantity
    ) {
        return getQuestionsFromCategoryForClassUpTo(cls, category, ORDER_BY_RANDOM,
                quantity > 0 ? String.valueOf(quantity) : null);
    }

    private List<QuestionItem> getQuestionsFromCategoryForClassUpTo(
            long cls, String category, String orderBy, String quantity
    ) {
        ArrayList<QuestionItem> result;
        SQLiteDatabase readableDatabase = helper.getReadableDatabase();

        String where = null;
        String[] args = null;
        if (category != null && cls > -1) {
            where = QuestionTable._CATEGORY + "=? AND " + QuestionTable._CLASSES + " & ? <> 0";
            args = new String[]{category, String.valueOf(cls)};
        } else if (category != null) {
            where = QuestionTable._CATEGORY + "=?";
            args = new String[]{category};
        } else if (cls > -1) {
            where = QuestionTable._CLASSES + " & ? <> 0";
            args = new String[]{String.valueOf(cls)};
        }

        Cursor cursor = readableDatabase.query(QuestionTable.TABLE_NAME, null, where,
                args, null, null, orderBy, quantity);

//        String sql = "SELECT * FROM " + QuestionTable.TABLE_NAME +
//                " WHERE " + where +
//                " AND " + QuestionTable._DISPLAY_COUNT + "=(SELECT MIN(" +
//                QuestionTable._DISPLAY_COUNT + ") FROM " + QuestionTable.TABLE_NAME + ")" +
//                " ORDER BY " + orderBy;
//
//        if (quantity != null) {
//            sql += " LIMIT " + quantity;
//        }
//
//        Cursor cursor = readableDatabase.rawQuery(sql, args);

        result = new ArrayList<>(cursor.getCount());
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            result.add(QuestionTable.questionItemFromCursor(cursor));
            cursor.moveToNext();
        }

        cursor.close();

        Log.i(TAG, "getQuestionsFromCategoryForClass produced " + result.size() + " questions of category " + category);

        return result;
    }

    public void setCountersForQuestion(long questionId, int answerAttempts) {
        SQLiteDatabase writableDatabase = helper.getWritableDatabase();
        writableDatabase.execSQL(
                "UPDATE " + QuestionTable.TABLE_NAME +
                        " SET " + QuestionTable._DISPLAY_COUNT + "=" +
                        QuestionTable._DISPLAY_COUNT + "+1, " +
                        QuestionTable._ANSWER_ATTEMPTS + "=? WHERE " +
                        QuestionTable._ID + "=?;",
                new Object[]{answerAttempts, questionId});
    }

    public static void saveQuestions(Context context, List<QuestionItem> questions) {
        DataAccess instance = new DataAccess(context);
        instance.save(questions);
    }
}
