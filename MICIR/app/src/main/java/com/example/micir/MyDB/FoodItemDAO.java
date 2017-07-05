package com.example.micir.MyDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import com.example.micir.FoodItem;
import com.example.micir.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 正文 on 2016/11/8.
 */

public class FoodItemDAO {
    private Context context;
    public static final String TABLE_NAME="fooditems";

    public static final String KEY_ID="_id";
    public static final String COLUMN_NAME="name";
    public static final String COLUMN_Class="foodclass";
    public static final String COLUMN_QUANTITY="quantity";
    public static final String COLUMN_BUYDATE="buydate";
    public static final String COLUMN_FOODDATE="fooddate";
    public static final String COLUMN_BARCODE="barcode";
    public static final String COLUMN_PIC="pic";

    public static final String CREATE_TABLE="CREATE  TABLE "+TABLE_NAME+" ( "+
            KEY_ID+"  INTEGER PRIMARY KEY AUTOINCREMENT, "+
            COLUMN_NAME+" VARCHAR , "+
            COLUMN_Class+" INTEGER, " +
            COLUMN_QUANTITY+" INTEGER , "+
            COLUMN_BUYDATE+" VARCHAR , " +
            COLUMN_FOODDATE+" VARCHAR , "+
            COLUMN_BARCODE+" VARCHAR , "+
            COLUMN_PIC+" BLOB)";

    private SQLiteDatabase db;

    public FoodItemDAO(Context context){
        db=DBHelper.getDatabase(context);
        this.context=context;
    }

    public void close(){
        db.close();
    }
    public FoodItem insert(FoodItem foodItem) throws SQLiteException{
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_NAME,foodItem.getFoodName());
        cv.put(COLUMN_Class,foodItem.getFoodclass());
        cv.put(COLUMN_QUANTITY,foodItem.getFoodQuantity());
        cv.put(COLUMN_BUYDATE,foodItem.getBuyDate());
        cv.put(COLUMN_FOODDATE,foodItem.getFoodDate());
        cv.put(COLUMN_BARCODE,foodItem.getBarcode());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Bitmap pic=foodItem.getImg();
        if (pic !=null){
            pic.compress(Bitmap.CompressFormat.PNG,0,bos);
            byte[] picArray=bos.toByteArray();
            cv.put(COLUMN_PIC,picArray);
        }

        Long id=db.insert(TABLE_NAME,null,cv);
        foodItem.setId(id);
        return foodItem;
    }
    public boolean update(FoodItem foodItem){
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_NAME,foodItem.getFoodName());
        cv.put(COLUMN_Class,foodItem.getFoodclass());
        cv.put(COLUMN_QUANTITY,foodItem.getFoodQuantity());
        cv.put(COLUMN_BUYDATE,foodItem.getBuyDate());
        cv.put(COLUMN_FOODDATE,foodItem.getFoodDate());
        cv.put(COLUMN_BARCODE,foodItem.getBarcode());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Bitmap pic=foodItem.getImg();
        if (pic !=null){
            pic.compress(Bitmap.CompressFormat.PNG,0,bos);
            byte[] picArray=bos.toByteArray();
            cv.put(COLUMN_PIC,picArray);
        }
        String where =KEY_ID+"="+foodItem.getId();

        return db.update(TABLE_NAME,cv,where,null)>0;
    }
    public boolean delete(FoodItem foodItem){
        Long id =foodItem.getId();
        String where =KEY_ID+"="+id;
        return db.delete(TABLE_NAME,where,null)>0;
    }
    public ArrayList<FoodItem> getAll(){
        ArrayList<FoodItem> result=new ArrayList<>();
        Cursor cursor=db.query(TABLE_NAME,null,null,null,null,null,null,null);
        while (cursor.moveToNext()){
            Long id=cursor.getLong(0);
            String name=cursor.getString(1);
            int foodclass=cursor.getInt(2);
            int q=cursor.getInt(3);
            String buydate=cursor.getString(4);
            String fooddate=cursor.getString(5);
            String barcode=cursor.getString(6);
            byte[] imgArray=cursor.getBlob(7);
            Bitmap img=null;
            if (imgArray!=null ){
                img= BitmapFactory.decodeByteArray(imgArray,0,imgArray.length);
            }
            FoodItem item=new FoodItem(name,buydate,fooddate,q, img,barcode,foodclass);
            item.setId(id);
            result.add(item);
        }
        return result;
    }
    public int getCount(){
        int result=0;
        Cursor cursor=db.rawQuery("Select count(*) from "+TABLE_NAME,null);
        if (cursor.moveToNext()){
            result=cursor.getInt(0);
        }
        return result;
    }

    public ArrayList<FoodItem> getByClass(int position){
        ArrayList<FoodItem> result=new ArrayList<>();
        Cursor cursor=db.rawQuery("Select * from "+TABLE_NAME+" where "+COLUMN_Class + "  = "+position ,null);
        while (cursor.moveToNext()){
            Long id=cursor.getLong(0);
            String name=cursor.getString(1);
            int foodclass=cursor.getInt(2);
            int q=cursor.getInt(3);
            String buydate=cursor.getString(4);
            String fooddate=cursor.getString(5);
            String barcode=cursor.getString(6);
            byte[] imgArray=cursor.getBlob(7);
            Bitmap img=null;
            if (imgArray!=null ){
                img= BitmapFactory.decodeByteArray(imgArray,0,imgArray.length);
            }
            FoodItem item=new FoodItem(name,buydate,fooddate,q, img,barcode,foodclass);
            item.setId(id);
            result.add(item);
        }



        return result;
    }
    public ArrayList<FoodItem> getByBarCode(String code){
        ArrayList<FoodItem> result=new ArrayList<>();
        Cursor cursor=db.rawQuery("Select * from "+TABLE_NAME+" where "+COLUMN_BARCODE + "  =  "+code ,null);
        while (cursor.moveToNext()){
            Long id=cursor.getLong(0);
            String name=cursor.getString(1);
            int foodclass=cursor.getInt(2);
            int q=cursor.getInt(3);
            String buydate=cursor.getString(4);
            String fooddate=cursor.getString(5);
            String barcode=cursor.getString(6);
            byte[] imgArray=cursor.getBlob(7);
            Bitmap img=null;
            if (imgArray!=null ){
                img= BitmapFactory.decodeByteArray(imgArray,0,imgArray.length);
            }
            FoodItem item=new FoodItem(name,buydate,fooddate,q, img,barcode,foodclass);
            item.setId(id);
            result.add(item);
        }

        return result;
    }


    public ArrayList<FoodItem> search(String s){
        ArrayList<FoodItem> result=new ArrayList<>();
        Cursor cursor=db.rawQuery("Select * from "+TABLE_NAME+" where "+COLUMN_NAME + " LIKE  '%"+s +"%' ",null);
        while (cursor.moveToNext()){
            Long id=cursor.getLong(0);
            String name=cursor.getString(1);
            int foodclass=cursor.getInt(2);
            int q=cursor.getInt(3);
            String buydate=cursor.getString(4);
            String fooddate=cursor.getString(5);
            String barcode=cursor.getString(6);
            byte[] imgArray=cursor.getBlob(7);
            Bitmap img=null;
            if (imgArray!=null ){
                img= BitmapFactory.decodeByteArray(imgArray,0,imgArray.length);
            }
            FoodItem item=new FoodItem(name,buydate,fooddate,q, img,barcode,foodclass);
            item.setId(id);
            result.add(item);
        }
        return result;
    }





}
