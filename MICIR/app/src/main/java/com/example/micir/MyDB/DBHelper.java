package com.example.micir.MyDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 正文 on 2016/11/8.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="Foodinfo.db";
    public static final int VERSION=2;
    public static SQLiteDatabase database;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
    }

    public static SQLiteDatabase getDatabase(Context context){
        if (database==null || !database.isOpen()){
            database=new DBHelper(context,DATABASE_NAME,null,VERSION).getWritableDatabase();
        }
        return database;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FoodItemDAO.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop Table if EXISTS "+FoodItemDAO.TABLE_NAME);
        onCreate(db);
    }
}
