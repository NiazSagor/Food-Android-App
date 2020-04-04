package com.angik.duodevloopers.food.Model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mylist.db";
    public static final String TABLE_NAME = "user";
    public static final String COL1 = "ID";
    public static final String COL2 = "ITEM";
    public static final String COL3 = "PRICE";
    private final Context context;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + " ITEM TEXT, " + " PRICE TEXT)";
        //String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + " ITEM1 TEXT)";
        String createTableQuery = "CREATE TABLE IF NOT EXISTS user (ID INTEGER PRIMARY KEY AUTOINCREMENT, item VARCHAR, price VARCHAR);";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addData(String item1, String item2) {
        SQLiteDatabase db = this.getWritableDatabase();
        //ContentValues contentValues = new ContentValues();
        //contentValues.put(COL2, item1);
        //contentValues.put(COL3, item2);
        if (getListContents().getCount() == 5) {
            Toast.makeText(context, "Can not order more than 5 items at a time", Toast.LENGTH_SHORT).show();
            return;
        }

        String query = "INSERT OR REPLACE INTO user(item, price) VALUES ('" + item1 + "','" + item2 + "');";
        db.execSQL(query);
        db.close();

        //long result = db.insert(TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        /*if (result == -1) {
            return false;
        } else {
            return true;
        }*/
    }

    public Cursor getListContents() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }
}
