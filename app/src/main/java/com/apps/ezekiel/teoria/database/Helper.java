package com.apps.ezekiel.teoria.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Helper extends SQLiteOpenHelper {

    public static final String DB_NAME = "teoria_questions_db";
    public static final int DB_VERSION = 1;

    public static Helper instance;

    // Using a singleton to prevent database blocking issues.
    // Using lazy initialization to allow for dependency injection of the Context
    // Not using CDL as it is known to have major concurrency issues in java.
    public static synchronized Helper getInstance(Context context) {
        if (instance == null) {
            instance = new Helper(context);
        }
        return instance;
    }

    private Helper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(QuestionTable.CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(QuestionTable.DELETE);
        onCreate(db);
    }
}
