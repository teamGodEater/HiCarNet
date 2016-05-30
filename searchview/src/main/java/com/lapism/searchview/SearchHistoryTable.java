package com.lapism.searchview;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


public class SearchHistoryTable {

    private final SearchHistoryDatabase dbHelper;
    private SQLiteDatabase db;

    public SearchHistoryTable(Context mContext) {
        dbHelper = new SearchHistoryDatabase(mContext);
    }

    // TODO FOR onResume AND onPause
    @SuppressWarnings("unused")
    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    @SuppressWarnings("unused")
    public void close() {
        dbHelper.close();
    }

    public void addItem(SearchItem item) {
        if (!checkText(item.key,item.city,item.district)) {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(SearchHistoryDatabase.SEARCH_HISTORY_KEY, item.key);
            values.put(SearchHistoryDatabase.SEARCH_HISTORY_CITY, item.city);
            values.put(SearchHistoryDatabase.SEARCH_HISTORY_DISTRICT, item.district);
            values.put(SearchHistoryDatabase.SEARCH_HISTORY_LATITUDE, item.latitude);
            values.put(SearchHistoryDatabase.SEARCH_HISTORY_LONGITUDE, item.longitude);
            db.insert(SearchHistoryDatabase.SEARCH_HISTORY_TABLE, null, values);
            db.close();
        }
    }

    private boolean checkText(String k,String c,String d) {
        db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + SearchHistoryDatabase.SEARCH_HISTORY_TABLE + " WHERE "
                + SearchHistoryDatabase.SEARCH_HISTORY_KEY  + " =? AND "
                + SearchHistoryDatabase.SEARCH_HISTORY_CITY  + " =? AND "
                + SearchHistoryDatabase.SEARCH_HISTORY_DISTRICT  + " =?";
        Cursor cursor = db.rawQuery(query, new String[]{k,c,d});

        boolean hasObject = false;

        if (cursor.moveToFirst()) {
            hasObject = true;
        }

        cursor.close();
        db.close();

        return hasObject;
    }

    public List<SearchItem> getAllItems() {
        List<SearchItem> list = new ArrayList<>();

        String selectQuery =
                "SELECT * FROM " + SearchHistoryDatabase.SEARCH_HISTORY_TABLE +
                        " ORDER BY " + SearchHistoryDatabase.SEARCH_HISTORY_COLUMN_ID +
                        " DESC LIMIT 6";

        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                SearchItem item = new SearchItem();
                item.ico = R.drawable.search_ic_history_black_24dp;
                item.key = cursor.getString(1);
                item.city = cursor.getString(2);
                item.district = cursor.getString(3);
                item.latitude = cursor.getDouble(4);
                item.longitude = cursor.getDouble(5);
                list.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public void clearDatabase() {
        db = dbHelper.getWritableDatabase();
        db.delete(SearchHistoryDatabase.SEARCH_HISTORY_TABLE, null, null);
        db.close();
    }

    public int getItemsCount() {
        db = dbHelper.getReadableDatabase();
        String countQuery = "SELECT * FROM " + SearchHistoryDatabase.SEARCH_HISTORY_TABLE;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

}