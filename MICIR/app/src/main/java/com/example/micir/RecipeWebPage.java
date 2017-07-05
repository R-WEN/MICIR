package com.example.micir;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;

public class RecipeWebPage extends AppCompatActivity {
    private WebView wv;
    private String ID;
    private ImageButton ing_info_btn;
    private ArrayList<ingredient> ingredients=new ArrayList<>();
    private String inginfostring="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_web_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ing_info_btn=(ImageButton)findViewById(R.id.ing_info_btn);
        ing_info_btn.setVisibility(View.INVISIBLE);

        ing_info_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListView ing_list=new ListView(RecipeWebPage.this);
                inglistAdapter ingAdapter=new inglistAdapter(RecipeWebPage.this,android.R.layout.simple_list_item_1,ingredients);
                ing_list.setAdapter(ingAdapter);
                ing_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {


                        if (ingredients.get(position).getStockfoods() != null) {
                            final View v= LayoutInflater.from(getBaseContext()).inflate(R.layout.ingredient_connectitem,null,false);
                            ListView gv =(ListView)v.findViewById(R.id.ing_connectfoodlist);

                            ConnectFoodAdapter list = new ConnectFoodAdapter(RecipeWebPage.this, android.R.layout.simple_list_item_1, ingredients.get(position).getStockfoods());
                            gv.setAdapter(list);

                            new AlertDialog.Builder(RecipeWebPage.this)
                                    .setTitle("冰箱庫存")
                                    .setView(v)
                                    .setPositiveButton("OK", null)
                                    .show();


                        }
                        return true;
                    }
                });

                new AlertDialog.Builder(RecipeWebPage.this)
                        .setTitle("食材資訊")
                        .setView(ing_list)
                        .setPositiveButton("OK",null)
                        .show();
            }
        });

        Intent intent=getIntent();
        String title=intent.getStringExtra("Title");
        String url=intent.getStringExtra("Url");
        ID=intent.getStringExtra("ID");
        TextView tv_title=(TextView)findViewById(R.id.toolbarTitle);
        tv_title.setText(title);
        wv=(WebView)findViewById(R.id.recipe_wv);
        wv.getSettings().setSupportZoom(true);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebViewClient(new myBrowser());
        new getingredient().execute(ID) ;
        wv.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (wv.canGoBack()) {
            wv.goBack();
        } else {
            super.onBackPressed();
        }
    }


    private class myBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }


    }
    private class getingredient extends AsyncTask<String,Integer,String> {
        boolean intenterror=false;

        @Override
        protected void onPreExecute() {
            //執行前 設定可以在這邊設定
            super.onPreExecute();


        }


        @Override
        protected String doInBackground(String... params) {
            //執行中 在背景做事情


            String jsonStr="";
            String urlstring="http://micir2.serveirc.com/catching.php?id="+params[0];
            try{
                URL url=new URL(urlstring);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.connect();
                InputStream is = connection.getInputStream();
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                String inputStr;

                while((inputStr = streamReader.readLine())!=null) {
                    jsonStr+=inputStr;
                }

                JSONArray ingredArray=new JSONArray(jsonStr);

                for (int i=0;i<ingredArray.length();i++){
                    String group_name=ingredArray.getJSONObject(i).names().getString(0);
                    inginfostring+=group_name+"\n";
                    System.out.println(group_name);
                    JSONArray inglist=ingredArray.getJSONObject(i).getJSONArray(group_name);
                    for (int j=0;j<inglist.length();j++){
                        String ing_name=inglist.getJSONObject(j).getString("ing_name");
                        String ing_count=inglist.getJSONObject(j).getString("ing_count");
                        inginfostring+=ing_name+"    "+ing_count+"\n";
                        ingredient ing=new ingredient();
                        ing.setGroup(group_name);
                        ing.setCount(ing_count);
                        ing.setName(ing_name);
                        ingredients.add(ing);
                        System.out.println("ing_name: "+ing_name+" , ing_count :"+ing_count);
                    }
                    //String ing_name=ingredArray.getJSONObject(i).getString("ing_name");
                    //String ing_count=ingredArray.getJSONObject(i).getString("ing_count");
                    //System.out.println("ing_name: "+ing_name+" , ing_count :"+ing_count);
                }

            }catch (IOException e){
                e.printStackTrace();
                intenterror=true;
            }catch (JSONException e){


                e.printStackTrace();
            }

            publishProgress(100);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //執行中 可以在這邊告知使用者進度
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(String bitmap) {
            //執行後 完成背景任務
            super.onPostExecute(bitmap);
            if (!intenterror) {
                ing_info_btn.setVisibility(View.VISIBLE);
            }



        }
    }
    public class ingredient{
        private String group;
        private String name;
        private String count;
        private int stock=-1;
        private ArrayList<FoodItem> stockfoods;

        public ArrayList<FoodItem> getStockfoods() {
            return stockfoods;
        }

        public void setStockfoods(ArrayList<FoodItem> stockfoods) {
            this.stockfoods = stockfoods;
        }

        public int getStock() {
            return stock;
        }

        public void setStock(int stock) {
            this.stock = stock;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public String getGroup() {
            return group;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }
    }
}


