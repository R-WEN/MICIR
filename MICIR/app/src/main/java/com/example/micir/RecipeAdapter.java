package com.example.micir;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 正文 on 2016/12/8.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ReceipItem> recipes;
    private OnRecyclerViewItemClickListener mOnItemClickListener=null;
    private static final int TYPE_ITEM =0;
    private static final int TYPE_FOOTER = 1;
    private int load_more_status=0;
    public static final int  PULLUP_LOAD_MORE=0;
    public static final int  LOADING_MORE=1;
    private RecyclerView.LayoutManager mlayoutManager;
    private Context mContext;
    private boolean noMore=false;
    public RecipeAdapter(ArrayList<ReceipItem> data, RecyclerView.LayoutManager mLayoutManager, Context context){
        mContext=context;
        mlayoutManager=mLayoutManager;
        recipes=data;
    }


    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener){
        this.mOnItemClickListener=listener;
    }
    public class ItemViewHoler extends RecyclerView.ViewHolder{
        public ImageView recipe_img;
        public TextView recipe_name;

        public ItemViewHoler(View v){
            super(v);
            recipe_img=(ImageView)v.findViewById(R.id.recipe_img);
            recipe_name=(TextView)v.findViewById(R.id.recipe_name);

        }
    }
    public static class FootViewHolder extends  RecyclerView.ViewHolder{
        private TextView foot_view_item_tv;
        private ProgressBar foot_view_progress;
        public FootViewHolder(View view) {
            super(view);
            foot_view_item_tv=(TextView)view.findViewById(R.id.foot_view_item_tv);
            foot_view_progress=(ProgressBar) view.findViewById(R.id.foot_view_progressbar);
            foot_view_progress.setVisibility(View.GONE);
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder=null;
        if (viewType==TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
            viewHolder= new ItemViewHoler(v);
        }else if (viewType==TYPE_FOOTER){
            View v =LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_load_more_layout,parent,false);
            GridLayoutManager.LayoutParams params=new GridLayoutManager.LayoutParams(GridLayoutManager.LayoutParams.MATCH_PARENT,GridLayoutManager.LayoutParams.WRAP_CONTENT);
            ((GridLayoutManager)mlayoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type=getItemViewType(position);
                    if (type==TYPE_FOOTER){
                        return 2;
                    }else{
                        return 1;
                    }

                }
            });
            v.setLayoutParams(params);
            viewHolder= new FootViewHolder(v);
        }

        return viewHolder;
    }


    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHoler){

            ReceipItem item=recipes.get(position);
            ((ItemViewHoler)holder).recipe_name.setText(item.getName());
            ((ItemViewHoler)holder).recipe_img.setImageBitmap(item.getImgsoruce());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mOnItemClickListener!=null){

                        mOnItemClickListener.onItemClick(v,position);
                    }
                }
            });
        }else if(holder instanceof FootViewHolder){
            FootViewHolder footViewHolder=(FootViewHolder)holder;
            switch (load_more_status) {
                case PULLUP_LOAD_MORE:
                    footViewHolder.foot_view_item_tv.setText("上拉加載更多食譜...");
                    footViewHolder.foot_view_progress.setVisibility(View.GONE);
                    break;
                case LOADING_MORE:
                    footViewHolder.foot_view_item_tv.setText("正在搜尋更多食譜...");
                    footViewHolder.foot_view_progress.setVisibility(View.VISIBLE);
                    break;
            }
            if (noMore){
                footViewHolder.foot_view_progress.setVisibility(View.GONE);
                footViewHolder.foot_view_item_tv.setText("沒有更多食譜");
            }


        }

    }

    @Override
    public int getItemCount() {
        return recipes.size()+1;
    }

    public interface OnRecyclerViewItemClickListener{
        void onItemClick(View view,int postion);
    }

    public int getItemViewType(int position) {
        if (position+1==getItemCount()){
            return TYPE_FOOTER;
        }else {
            return TYPE_ITEM;
        }

    }
    public void changeMoreStatus(int status){
        load_more_status=status;
        notifyDataSetChanged();
    }
    public void setnoMoreRecipe(boolean data){
        noMore=data;
        notifyDataSetChanged();
    }
    public void removeAll(){
        recipes.removeAll(recipes);
        notifyDataSetChanged();
    }
}
