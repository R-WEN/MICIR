package com.example.micir;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by 正文 on 2016/12/24.
 */

public class ConnectFoodAdapter extends ArrayAdapter<FoodItem> {
    private Context mContext;
    private final String[] TITLES ={"肉類","菜類","水果","海鮮","其他"};
    private ArrayList<FoodItem> foodlist;
    private SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
    String date="";
    public ConnectFoodAdapter(Context context, int textViewResourceId, ArrayList<FoodItem> objects) {
        super(context, textViewResourceId, objects);
        mContext=context;
        foodlist=objects;
        date=simpleDateFormat.format(new java.util.Date());

    }

    public View getView(int position, View convertView, ViewGroup parent){
        View v=convertView;
        if (v==null){
            LayoutInflater inflater =(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v=inflater.inflate(R.layout.food_item2,null);
        }
        FoodItem item=foodlist.get(position);
        if (item!=null){
            TextView tv_class=(TextView) v.findViewById(R.id.connectfood_tv_class);
            tv_class.setText(TITLES[item.getFoodclass()]);
            TextView tv=(TextView) v.findViewById(R.id.connectfood_tv);
            tv.setText(item.getFoodName());
            //tv=(TextView)v.findViewById(R.id.item_fooddate);
            //tv.setText("到期日："+item.getFoodDate());

            CircularImageView iv=(CircularImageView)v.findViewById(R.id.connectfood_iv);
            //ImageView iv=(ImageView)v.findViewById(R.id.connectfood_iv);

            if (item.getImg()==null){
                iv.setImageResource(R.drawable.minibar);
            }else{
                iv.setImageBitmap(item.getImg());
            }


        }

        return v;
    }
}
