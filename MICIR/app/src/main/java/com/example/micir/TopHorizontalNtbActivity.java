package com.example.micir;


import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.astuetz.PagerSlidingTabStrip;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;

import devlight.io.library.ntb.NavigationTabBar;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by 正文 on 2016/12/3.
 */



public class TopHorizontalNtbActivity extends AppCompatActivity {
    private com.github.clans.fab.FloatingActionButton scanbtn;
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    private int Foodmanage_page_index=0;
    private int iniviewcount=0;
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private ViewPager toppager;
    private MyPagerAdapter pagerAdapter;
    private ArrayList<ReceipItem> receipItems;
    private RecyclerView recycler_listview;
    private RecipeAdapter recipeAdapter;
    private ProgressWheel progressWheel;
    private int lastVisibleItem=0;
    private int searchpageindex=1;
    private boolean canSearch=true;
    private GridLayoutManager gridLayoutManager=new GridLayoutManager(TopHorizontalNtbActivity.this,2);
    private FloatingSearchView recipeSearchView;
    private boolean intenterror=false;
    private int searchcount=0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_top_ntb);
        initUI();

    }


    private void initUI() {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        toppager=viewPager;
        PagerAdapter mycontextPagerAdapter=new  PagerAdapter() {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public boolean isViewFromObject(final View view, final Object object) {
                return view.equals(object);
            }

            @Override
            public void destroyItem(final View container, final int position, final Object object) {
                ((ViewPager) container).removeView((View) object);
            }

            @Override
            public Object instantiateItem(final ViewGroup container, final int position) {
                View v=null;
                System.out.println("iniviewCount: "+iniviewcount);
                switch (iniviewcount){
                    case 0:
                        final View view2=LayoutInflater.from(getBaseContext()).inflate(R.layout.activity_food_manager,null,false);
                        iniFoodManageUI(view2);
                        container.addView(view2);
                        v=view2;

                        break;

                    case 1:

                        final View view=LayoutInflater.from(getBaseContext()).inflate(R.layout.recipe_page,null,false);
                        container.addView(view);
                        recipeSearchView=(FloatingSearchView)view.findViewById(R.id.recipe_search_view);
                        iniSearchView();

                        recycler_listview=(RecyclerView)view.findViewById(R.id.recipe_list_view);
                        recycler_listview.setVisibility(View.INVISIBLE);
                        progressWheel=(ProgressWheel)view.findViewById(R.id.progress_wheel);
                        progressWheel.setVisibility(View.INVISIBLE);
                        receipItems=new ArrayList<>();
                        recipeAdapter=new RecipeAdapter(receipItems,gridLayoutManager,view.getContext());
                        recycler_listview.setAdapter(recipeAdapter);
                        recycler_listview.setLayoutManager(gridLayoutManager);
                        recipeAdapter.setOnItemClickListener(new RecipeAdapter.OnRecyclerViewItemClickListener(){
                            public void onItemClick(View view,int p){
                                System.out.println("Recipe click "+p);
                                Intent intent=new Intent(TopHorizontalNtbActivity.this,RecipeWebPage.class);
                                intent.putExtra("Title",receipItems.get(p).getName());
                                intent.putExtra("Url",receipItems.get(p).getHttpurl());
                                intent.putExtra("ID",receipItems.get(p).getID());
                                startActivity(intent);
                            }
                        });
                        recycler_listview.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if (newState==recyclerView.SCROLL_STATE_IDLE && (lastVisibleItem+1)==recipeAdapter.getItemCount() && canSearch){
                                    new getRecipefromIcook().execute("http://micir2.serveirc.com/?receipIng="+recipeSearchView.getQuery().replaceAll(" ","+")+"&index="+searchpageindex);
                                    recipeAdapter.changeMoreStatus(RecipeAdapter.LOADING_MORE);
                                }
                            }

                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                lastVisibleItem=gridLayoutManager.findLastVisibleItemPosition();
                            }
                        });


                        v=view;
                        break;
                }
                iniviewcount++;
                return v;
            }
        };
        viewPager.setAdapter(mycontextPagerAdapter);
        final String[] colors = getResources().getStringArray(R.array.default_preview);

        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);

        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        ResourcesCompat.getDrawable(getResources(),R.drawable.refrigerator,null),
                        Color.parseColor(colors[0]))
                        .title("食物")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        ResourcesCompat.getDrawable(getResources(),R.drawable.recipe,null),
                        Color.parseColor(colors[1]))
                        .title("食譜")
                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 2);

        navigationTabBar.post(new Runnable() {
            @Override
            public void run() {
                final View viewPager = findViewById(R.id.vp_horizontal_ntb);
                ((ViewGroup.MarginLayoutParams) viewPager.getLayoutParams()).topMargin =
                        (int) -navigationTabBar.getBadgeMargin();
                viewPager.requestLayout();
            }
        });

        navigationTabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(final NavigationTabBar.Model model, final int index) {

            }

            @Override
            public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {
                model.hideBadge();
            }
        });
        //設定初始頁index 預設為食物管理
        viewPager.setCurrentItem(0);
        mycontextPagerAdapter.notifyDataSetChanged();

    }
    private void iniFoodManageUI(View view){


        tabs=(PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        pager=(ViewPager) view.findViewById(R.id.viewPager);

        pagerAdapter=new MyPagerAdapter(getSupportFragmentManager(),TopHorizontalNtbActivity.this);
        pager.setAdapter(pagerAdapter);
        final int pageMargin =(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,4,getResources().getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        pager.addOnPageChangeListener(new TopHorizontalNtbActivity.MyOnPageChanger());
        tabs.setViewPager(pager);
        tabs.setIndicatorColor(Color.parseColor("#76a285"));
    }


    class MyOnPageChanger implements ViewPager.OnPageChangeListener{
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }
        @Override
        public void onPageSelected(int arg0) {

            Foodmanage_page_index=arg0;
            System.out.println("現在是第"+Foodmanage_page_index+"頁");

        }

    }

    private class getRecipefromIcook extends AsyncTask<String,Integer,String>{

        ArrayList<ReceipItem> newitems;
        @Override
        protected void onPreExecute() {
            //執行前 設定可以在這邊設定
            super.onPreExecute();
            newitems=new ArrayList<>();
            searchcount++;
        }


        @Override
        protected String doInBackground(String... params) {
            //執行中 在背景做事情


            String jsonStr="";
            String urlstring=params[0];
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

                JSONArray recipeinfoArray=new JSONArray(jsonStr);
                System.out.println("recipeinfoArrayLength:"+recipeinfoArray.length());
                for (int i=0;i<recipeinfoArray.length();i++){
                    if (searchcount>1){
                        break;
                    }
                    String id=recipeinfoArray.getJSONObject(i).getString("id").replaceAll("Array","");
                    String title=recipeinfoArray.getJSONObject(i).getString("name");
                    String imgurl=recipeinfoArray.getJSONObject(i).getString("imgurl");
                    imgurl= URLDecoder.decode(imgurl,"UTF-8").replaceAll("150_full","600_fit");
                    System.out.println("食譜："+id+title+imgurl);

                    ReceipItem item=new ReceipItem();
                    item.setID(id);
                    item.setImgurl(imgurl);
                    item.setName(title);
                    item.setHttpurl("https://cookpad.com/tw/食譜/"+id);

                    item.setImgsoruce(ImageCrop(getBitmapFromURL(item.getImgurl())));
                    newitems.add(item);


                }

            }catch (IOException e){
                e.printStackTrace();
                intenterror=true;
            }catch (JSONException e){
                canSearch=false;

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
            if (intenterror){
                new AlertDialog.Builder(TopHorizontalNtbActivity.this)
                        .setMessage("網路連接錯誤")
                        .setPositiveButton("OK", null)
                        .show();
                progressWheel.setVisibility(View.INVISIBLE);
            }else {
                if (searchcount==1) {
                    receipItems.addAll(newitems);
                    recipeAdapter.notifyDataSetChanged();
                    recycler_listview.setVisibility(View.VISIBLE);
                    recipeAdapter.changeMoreStatus(RecipeAdapter.PULLUP_LOAD_MORE);
                    recipeAdapter.setnoMoreRecipe(!canSearch);
                    searchpageindex += 1;
                    progressWheel.setVisibility(View.INVISIBLE);

                }
            }
            searchcount--;


        }
    }

    private void iniSearchView(){
        recipeSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String s) {
                    intenterror=false;
                    canSearch=true;

                    searchpageindex=1;
                    new getRecipefromIcook().execute("http://micir2.serveirc.com/?receipIng="+s.replaceAll(" ","+")+"&index="+searchpageindex);
                    recipeAdapter.removeAll();
                    recycler_listview.setVisibility(View.INVISIBLE);
                    progressWheel.setVisibility(View.VISIBLE);


            }
        });

    }

    public void SearchRecipe(String s){
        intenterror=false;
        canSearch=true;

        searchpageindex=1;
        new getRecipefromIcook().execute("http://micir2.serveirc.com/?receipIng="+s.replaceAll(" ","+")+"&index="+searchpageindex);
        recipeAdapter.removeAll();
        recycler_listview.setVisibility(View.INVISIBLE);
        progressWheel.setVisibility(View.VISIBLE);
        recipeSearchView.setSearchText(s);
        toppager.setCurrentItem(1);
    }
    private Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Bitmap ImageCrop(Bitmap bitmap) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();

        int wh = w > h ? h : w;// 裁切后所取的正方形区域边长

        int retX = w > h ? (w - h) / 2 : 0;//基于原图，取正方形左上角x坐标
        int retY = w > h ? 0 : (h - w) / 2;


        return Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null, false);
    }

}
