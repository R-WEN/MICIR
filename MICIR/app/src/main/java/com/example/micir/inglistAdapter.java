package com.example.micir;

import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.micir.MyDB.FoodItemDAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 正文 on 2016/12/23.
 */

public class inglistAdapter extends ArrayAdapter<RecipeWebPage.ingredient> {
    private Context mContext;
    private ArrayList<RecipeWebPage.ingredient> items;
    private String group="";
    private int layoutcount=0;
    private SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
    String date="";
    public inglistAdapter(Context context, int resource, ArrayList<RecipeWebPage.ingredient> objects) {
        super(context, resource, objects);
        mContext=context;
        items=objects;
        date=simpleDateFormat.format(new java.util.Date());
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.ingredient_item, null);
        }
        if (items!=null){
            RecipeWebPage.ingredient item=items.get(position);

            final TextView ing_group_name=(TextView) v.findViewById(R.id.ing_groupname);
            final ProgressBar ing_progress=(ProgressBar) v.findViewById(R.id.ing_progressBar);
            final ImageView ing_iv=(ImageView)v.findViewById(R.id.ing_info_iv);
            final ImageView ing_iv2=(ImageView)v.findViewById(R.id.ing_info_iv2);
            final ImageView ing_iv3=(ImageView)v.findViewById(R.id.ing_info_iv3);
            final TextView ing_tv=(TextView)v.findViewById(R.id.ing_info_tv);
            final TextView ing_tv2=(TextView)v.findViewById(R.id.ing_info_tv2);
            ing_group_name.setVisibility(View.GONE);
            if (group.equals("")){

                group=item.getGroup();
                ing_group_name.setText(item.getGroup());
                ing_group_name.setVisibility(View.VISIBLE);
            }
            if(layoutcount>0 & !group.equals(item.getGroup())){
                ing_group_name.setVisibility(View.VISIBLE);
                group=item.getGroup();
                ing_group_name.setText(group);

            }
            ing_iv.setVisibility(View.GONE);
            ing_iv2.setVisibility(View.GONE);
            ing_iv3.setVisibility(View.GONE);
            ing_tv.setText(item.getName());
            ing_tv2.setText(item.getCount());
            if (item.getStock()==-1){
                checking check=new checking();
                check.setView(ing_progress,ing_iv,ing_iv2,ing_iv3);
                check.execute(item);
            }else{
                ing_progress.setVisibility(View.GONE);
                if (item.getStock()==0){

                    ing_iv2.setVisibility(View.VISIBLE);

                }else if(item.getStock()==1){

                    ing_iv.setVisibility(View.VISIBLE);
                }else if(item.getStock()==2){
                    ing_iv3.setVisibility(View.VISIBLE);
                }
            }


        }

        layoutcount++;
        return v;
    }


    private class checking extends AsyncTask<RecipeWebPage.ingredient,Integer,Integer> {
        FoodItemDAO db=new FoodItemDAO(getContext());
        private ProgressBar progressbar;
        private ImageView instockview;
        private ImageView unstockview;
        private ImageView warnning;
        public void setView(ProgressBar p,ImageView v1,ImageView v2,ImageView v3){
            progressbar=p;
            instockview=v1;
            unstockview=v2;
            warnning=v3;
        }
        @Override
        protected void onPreExecute() {
            //執行前 設定可以在這邊設定
            super.onPreExecute();


        }


        @Override
        protected Integer doInBackground(RecipeWebPage.ingredient... params) {
            //執行中 在背景做事情
            int result=0;
            RecipeWebPage.ingredient item=params[0];
                if (item.getStock()==-1){
                    //ArrayList<FoodItem> searchresult=db.search(item.getName());
                    ArrayList<FoodItem> searchresult=new ArrayList<>();
                    ArrayList<FoodItem> allfoods=db.getAll();
                    for (FoodItem afood:allfoods){
                        if (!(date.compareTo(afood.getFoodDate())>0)){
                            if (afood.getFoodName().equals(item.getName())){
                                searchresult.add(afood);
                                item.setStock(1);
                                result=1;
                            }else if (afood.getFoodName().contains(item.getName())){
                                searchresult.add(afood);
                                if (result!=1) {
                                    item.setStock(2);
                                    result = 2;
                                }
                            }else if(item.getName().contains(afood.getFoodName())){
                                searchresult.add(afood);
                                if (result!=1) {
                                    item.setStock(2);
                                    result = 2;
                                }
                            }
                        }

                    }
                    if (searchresult.size()>0){
                        item.setStockfoods(searchresult);
                    }else{
                        item.setStock(0);
                        result=0;
                    }

                }else{
                    result=item.getStock();
                }


            publishProgress(100);

            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //執行中 可以在這邊告知使用者進度
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Integer result) {
            //執行後 完成背景任務
            super.onPostExecute(result);
            progressbar.setVisibility(View.GONE);
            if (result==0){
                warnning.setVisibility(View.GONE);
                instockview.setVisibility(View.GONE);
                unstockview.setVisibility(View.VISIBLE);
            }else if(result==1){
                warnning.setVisibility(View.GONE);
                unstockview.setVisibility(View.GONE);
                instockview.setVisibility(View.VISIBLE);
            }else if(result==2){
                warnning.setVisibility(View.VISIBLE);
                unstockview.setVisibility(View.GONE);
                instockview.setVisibility(View.GONE);
            }
            notifyDataSetChanged();
        }
    }
}
