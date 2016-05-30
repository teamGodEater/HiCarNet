package com.lapism.searchview;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


class SearchHistoryDatabase extends SQLiteOpenHelper {

    public static final String SEARCH_HISTORY_TABLE = "search_history";
    public static final String SEARCH_HISTORY_COLUMN_ID = "_id";
    public static final String SEARCH_HISTORY_CITY = "_city";
    public static final String SEARCH_HISTORY_KEY = "_key";
    public static final String SEARCH_HISTORY_DISTRICT = "_district";
    public static final String SEARCH_HISTORY_LATITUDE = "_latitude";
    public static final String SEARCH_HISTORY_LONGITUDE = "_longitude";
    private static final String DATABASE_NAME = "search_history_database.db";
    private static final int DATABASE_VERSION = 2;
    private static final String CREATE_TABLE_SEARCH_HISTORY = "CREATE TABLE IF NOT EXISTS "
            + SEARCH_HISTORY_TABLE + " ( "
            + SEARCH_HISTORY_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SEARCH_HISTORY_KEY + " TEXT , "
            + SEARCH_HISTORY_CITY + " TEXT , "
            + SEARCH_HISTORY_DISTRICT + " TEXT , "
            + SEARCH_HISTORY_LATITUDE + " INTEGER , "
            + SEARCH_HISTORY_LONGITUDE + " INTEGER);";

    public SearchHistoryDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SEARCH_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropAllTables(db);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private void dropAllTables(SQLiteDatabase db) {
        dropTableIfExists(db);
    }

    private void dropTableIfExists(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + SearchHistoryDatabase.SEARCH_HISTORY_TABLE);
    }

}