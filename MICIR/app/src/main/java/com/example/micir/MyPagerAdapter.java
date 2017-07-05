package com.example.micir;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.text.style.SuperscriptSpan;

import java.util.ArrayList;

/**
 * Created by 正文 on 2016/12/1.
 */

public class MyPagerAdapter extends FragmentPagerAdapter {
    private TopHorizontalNtbActivity activity;
    private final String[] TITLES ={"全部","肉類","菜類","水果","海鮮","其他","過期"};
    private ArrayList<FoodListAdapter> foodListAdapters=new ArrayList<>();
    public MyPagerAdapter(FragmentManager fragmentManager,TopHorizontalNtbActivity activity){
        super(fragmentManager);
        this.activity=activity;
    }
    @Override
    public Fragment getItem(int position) {
        return FoodListCardFragment.newInstance(position,MyPagerAdapter.this);
    }
    public CharSequence getPageTitle(int position){
        return TITLES[position];
    }
    @Override
    public int getCount() {
        return TITLES.length;
    }

    public void searchrecipe(String s){
        activity.SearchRecipe(s);
    }
    public void addFoodlistAdapter(FoodListAdapter adapter){
        foodListAdapters.add(adapter);
    }
    public void setFoodListAdapters(FoodListAdapter adapters,int position){
        foodListAdapters.set(position,adapters);
    }
    public FoodListAdapter getFoodListAdapter(int position){
        return foodListAdapters.get(position);
    }
    public int getFoodListAdaptersSize(){
        return foodListAdapters.size();
}
    public void updateAllFoods(){
        int i=0;
        for (FoodListAdapter adapter:foodListAdapters){
            adapter.notifyDataSetChanged();
            i++;
        }
    }

}
