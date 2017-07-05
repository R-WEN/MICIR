package com.example.micir;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.micir.MyDB.FoodItemDAO;
import com.pkmmte.view.CircularImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by 正文 on 2016/10/13.
 */

public class FoodListAdapter extends ArrayAdapter<FoodItem> {
    private Context mContext;
    private ArrayList<FoodItem> foodlist;
    private SparseBooleanArray mSelectedItemsIds;
    private SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
    String date="";
    private FoodItemDAO dba;

    public FoodListAdapter(Context context, int textViewResourceId, ArrayList<FoodItem> objects) {
        super(context, textViewResourceId, objects);
        mContext=context;
        foodlist=objects;
        date=simpleDateFormat.format(new java.util.Date());
        mSelectedItemsIds=new SparseBooleanArray();
        dba=new FoodItemDAO(mContext);
    }

    public View getView(int position,View convertView,ViewGroup parent){
        View v=convertView;
        if (v==null){
            LayoutInflater inflater =(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v=inflater.inflate(R.layout.food_item,null);
        }
        final FoodItem item=foodlist.get(position);
        //System.out.println(foodlist.size());

        if (item!=null){
            TextView tv=(TextView) v.findViewById(R.id.item_name);
            tv.setText(item.getFoodName());
            tv=(TextView)v.findViewById(R.id.item_fooddate);
            tv.setText("到期日："+item.getFoodDate());

            CircularImageView myimg=(CircularImageView)v.findViewById(R.id.icon);


            if (item.getImg()==null){
                myimg.setImageResource(R.drawable.minibar);
            }else{
                myimg.setImageBitmap(item.getImg());
            }
            String today = getYear(date) + getMonth(date) + getDay(date);
            String s = getYear(item.getFoodDate()) + getMonth(item.getFoodDate()) + getDay(item.getFoodDate());
            if ((Long.parseLong(s) - Long.parseLong(today)) < 0) {
                //System.out.println("mask postition:" +position);
                System.out.println(item.getFoodName());
                FrameLayout mask=(FrameLayout)v.findViewById(R.id.mask);
                Drawable maskimg=ContextCompat.getDrawable(mContext,R.drawable.timeouticon);
                mask.setForeground(maskimg);
            }

        }

        return v;
    }

    private void sortbydate(ArrayList<FoodItem> foos){
        Collections.sort(foos, new Comparator<FoodItem>() {
            @Override
            public int compare(FoodItem lhs, FoodItem rhs) {
                return lhs.getFoodDate().compareTo(rhs.getFoodDate());
            }
        });

    }
    private SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String dates = sDateFormat.format(new Date());

    public void remove(FoodItem object){
        foodlist.remove(object);
        notifyDataSetChanged();
    }
    public ArrayList<FoodItem> getFoodlist(){
        return foodlist;
    }
    public void toggleSelection(int position){
        selectView(position,!mSelectedItemsIds.get(position));
    }
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }
    public void selectView(int position,boolean value){
        if (value){
            mSelectedItemsIds.put(position,value);
        }else {
            mSelectedItemsIds.delete(position);
        }
        notifyDataSetChanged();
    }
    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }


    private String getYear(String date) {
        //System.out.println("Y:" + date.substring(0, 4));
        return date.substring(0, 4);

    }

    private String getMonth(String date) {
        //System.out.println("M:" + date.substring(5, 7));
        return date.substring(5, 7);
    }

    private String getDay(String date) {
        //System.out.println("D:" + date.substring(8, 10));
        return date.substring(8, 10);
    }

    class updateFoods extends AsyncTask<Integer, Integer, Integer> {
        protected void onPreExecute(){
            // in main thread
        }

        protected Integer doInBackground(Integer... params){
            // in background thread
            int index=params[0];
            if (index>0 && index<6){
                foodlist=dba.getByClass(index);
                sortbydate(foodlist);
            }else if(index==6) {
                foodlist=dba.getAll();
                ArrayList<FoodItem> foodItems=new ArrayList<>();
                for (FoodItem item:foodlist){
                    String today = getYear(dates) + getMonth(dates) + getDay(dates);
                    String s = getYear(item.getFoodDate()) + getMonth(item.getFoodDate()) + getDay(item.getFoodDate());
                    if ((Long.parseLong(s) - Long.parseLong(today)) < 0) {
                        foodItems.add(item);
                    }
                }
                foodlist=foodItems;
                sortbydate(foodlist);
            }else{
                foodlist=dba.getAll();

                sortbydate(foodlist);
            }
            return 0;
        }

        protected void onProgressUpdate(Integer... progress){
            // in main thread
        }

        protected void onPostExecute(Integer result){
            // in main thread
            notifyDataSetChanged();
        }

        protected void onCancelled(Integer result){
            // in main thread
        }

    }
}
