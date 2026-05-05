package com.aakash.lostandfoundapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "lostandfound.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_ITEMS = "items";
    public static final String COL_ID       = "id";
    public static final String COL_TYPE     = "post_type";
    public static final String COL_NAME     = "name";
    public static final String COL_PHONE    = "phone";
    public static final String COL_DESC     = "description";
    public static final String COL_DATE     = "date";
    public static final String COL_LOCATION = "location";
    public static final String COL_CATEGORY = "category";
    public static final String COL_IMAGE    = "image_path";
    public static final String COL_TIMESTAMP= "timestamp";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_ITEMS + " (" +
                    COL_ID        + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_TYPE      + " TEXT, " +
                    COL_NAME      + " TEXT, " +
                    COL_PHONE     + " TEXT, " +
                    COL_DESC      + " TEXT, " +
                    COL_DATE      + " TEXT, " +
                    COL_LOCATION  + " TEXT, " +
                    COL_CATEGORY  + " TEXT, " +
                    COL_IMAGE     + " TEXT, " +
                    COL_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    // INSERT
    public long insertItem(String type, String name, String phone, String desc,
                           String date, String location, String category, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TYPE,     type);
        cv.put(COL_NAME,     name);
        cv.put(COL_PHONE,    phone);
        cv.put(COL_DESC,     desc);
        cv.put(COL_DATE,     date);
        cv.put(COL_LOCATION, location);
        cv.put(COL_CATEGORY, category);
        cv.put(COL_IMAGE,    imagePath);
        long id = db.insert(TABLE_ITEMS, null, cv);
        db.close();
        return id;
    }

    // GET ALL
    public List<LostFoundItem> getAllItems() {
        List<LostFoundItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITEMS, null, null, null, null, null,
                COL_TIMESTAMP + " DESC");
        if (cursor.moveToFirst()) {
            do { list.add(cursorToItem(cursor)); } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // GET BY CATEGORY FILTER
    public List<LostFoundItem> getItemsByCategory(String category) {
        List<LostFoundItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITEMS, null,
                COL_CATEGORY + "=?", new String[]{category},
                null, null, COL_TIMESTAMP + " DESC");
        if (cursor.moveToFirst()) {
            do { list.add(cursorToItem(cursor)); } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // GET BY ID
    public LostFoundItem getItemById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITEMS, null,
                COL_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);
        LostFoundItem item = null;
        if (cursor.moveToFirst()) item = cursorToItem(cursor);
        cursor.close();
        db.close();
        return item;
    }

    // DELETE
    public void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    private LostFoundItem cursorToItem(Cursor cursor) {
        LostFoundItem item = new LostFoundItem();
        item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
        item.setType(cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE)));
        item.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
        item.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE)));
        item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_DESC)));
        item.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)));
        item.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)));
        item.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)));
        item.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE)));
        item.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(COL_TIMESTAMP)));
        return item;
    }
}