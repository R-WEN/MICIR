package com.example.micir;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.micir.MyDB.FoodItemDAO;
import com.example.micir.myParcelObject.BarCodeParcel;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by 正文 on 2016/12/1.
 */

public class FoodListCardFragment extends Fragment {
    private static final String ARG_POSITION="position";

    private static final int ZBAR_CAMERA_PERMISSION = 1;
    private MyPagerAdapter myadapter;
    private Uri ImageUri;
    private int position;
    private int foodselectpostion;
    private ArrayList<FoodItem> foos;
    private FoodListAdapter foosAdapter;
    private static FoodItemDAO dba;
    private com.github.clans.fab.FloatingActionButton scanbtn;
    private com.github.clans.fab.FloatingActionButton takepicbtn;
    private com.github.clans.fab.FloatingActionButton searchbtn;
    private int pageindex=0;
    private GridView foodlistgv;
    public static FoodListCardFragment newInstance(int position,MyPagerAdapter adapter){
        FoodListCardFragment f=new FoodListCardFragment();
        Bundle b=new Bundle();
        b.putInt(ARG_POSITION,position);
        f.setArguments(b);
        f.setPageindex(position);
        f.setMyadapter(adapter);
        Log.d("alan","new FoodListCardFragment");
        dba=new FoodItemDAO(f.getContext());


        return f;
    }
    public void setMyadapter(MyPagerAdapter adapter){
        this.myadapter=adapter;
    }
    public void setPageindex(int pageindex){
        this.pageindex=pageindex;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d("alan","onCreate");



        position=getArguments().getInt(ARG_POSITION);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        Log.d("alan","onCreateView");


        ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        FrameLayout f1=new FrameLayout(getActivity());
        f1.setLayoutParams(params);
        final View gvlayout=LayoutInflater.from(getActivity()).inflate(R.layout.foodlist_gv,null,false);
        final GridView gv=(GridView) gvlayout.findViewById(R.id.foodlist_gv);

        final View v=LayoutInflater.from(getActivity()).inflate(R.layout.floatbtn,null,false);
        scanbtn=(FloatingActionButton) v.findViewById(R.id.menu_scan);
        scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
                } else {
                    Intent intent = new Intent(getActivity(), Scanner.class);
                    intent.putExtra("foodclass",pageindex);
                    startActivityForResult(intent,2);
                }
            }
        });
        takepicbtn=(FloatingActionButton)v.findViewById(R.id.menu_takepic);
        takepicbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picit=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                String dir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                String fname="Micir_p"+ System.currentTimeMillis()+".jpg";
                ImageUri= Uri.parse("file://"+dir+"/"+fname);
                System.out.println("file://"+dir+"/"+fname);

                picit.putExtra(MediaStore.EXTRA_OUTPUT,ImageUri);
                startActivityForResult(picit,1);
            }
        });
        searchbtn=(FloatingActionButton)v.findViewById(R.id.menu_searchfood);
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item[]={"輸入文字搜尋","掃描條碼搜尋"};
                new AlertDialog.Builder(getActivity()).setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                final EditText search_et=new EditText(getContext());


                                new AlertDialog.Builder(getContext())
                                        .setTitle("輸入食材名稱")
                                        .setView(search_et)
                                        .setNegativeButton("搜尋", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String searchtext = search_et.getText().toString();
                                                ArrayList<FoodItem> result=new ArrayList<FoodItem>();
                                                if (!searchtext.equals("")) {
                                                    result = dba.search(searchtext);
                                                }
                                                    if (result.size() > 0) {
                                                        final View v = LayoutInflater.from(getContext()).inflate(R.layout.ingredient_connectitem, null, false);
                                                        ListView gv = (ListView) v.findViewById(R.id.ing_connectfoodlist);

                                                        ConnectFoodAdapter list = new ConnectFoodAdapter(getContext(), android.R.layout.simple_list_item_1, result);
                                                        gv.setAdapter(list);

                                                        new AlertDialog.Builder(getContext())
                                                                .setTitle("冰箱庫存")
                                                                .setView(v)
                                                                .setPositiveButton("OK", null)
                                                                .show();
                                                    } else {
                                                        new AlertDialog.Builder(getContext())
                                                                .setTitle("冰箱庫存")
                                                                .setMessage("沒有該名稱的食材庫存")
                                                                .setPositiveButton("OK", null)
                                                                .show();
                                                    }

                                                    dialog.cancel();
                                                }

                                        })
                                        .setPositiveButton("取消",null)
                                        .show();
                                dialog.cancel();

                                break;
                            case 1:
                                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                                        != PackageManager.PERMISSION_GRANTED) {

                                    ActivityCompat.requestPermissions(getActivity(),
                                            new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
                                } else {
                                    Intent intent = new Intent(getActivity(), Scanner.class);
                                    intent.putExtra("mode",2);
                                    startActivityForResult(intent,4);
                                }
                                break;
                        }
                    }
                }).show();


            }
        });
        if (pageindex==6) {
            com.github.clans.fab.FloatingActionMenu fmenu = (com.github.clans.fab.FloatingActionMenu) v.findViewById(R.id.fbtn_menu);
            fmenu.setVisibility(View.GONE);
        }
        foodlistgv=gv;
        iniGridView(gv,position);
        gv.setNumColumns(2);
        gv.setPadding(30,0,0,0);
        gv.setGravity(Gravity.CENTER);
        gv.setLayoutParams(params);
        f1.addView(gvlayout);
        f1.addView(v);
        return f1;
    }
    public void addFood(FoodItem item){
        FoodItem f=dba.insert(item);
        if (pageindex==0){
            foos.add(f);
            sortbydate();
            //foosAdapter.notifyDataSetChanged();
        }else if ((pageindex-1)==item.getFoodclass()){
            foos.add(f);
            sortbydate();
            //foosAdapter.notifyDataSetChanged();
        }
        myadapter.updateAllFoods();

    }

    public void editFood(FoodItem nfi){
        FoodItem ofi=foos.get(foodselectpostion);
        ofi.setBarcode(nfi.getBarcode());
        ofi.setFoodName(nfi.getFoodName());
        ofi.setBuyDate(nfi.getBuyDate());
        ofi.setFoodDate(nfi.getFoodDate());
        ofi.setFoodQuantity(nfi.getFoodQuantity());
        ofi.setImg(nfi.getImg());
        System.out.println("update class:"+nfi.getFoodclass());
        ofi.setFoodclass(nfi.getFoodclass());
        dba.update(ofi);
        if (pageindex>0 && pageindex <6){
            if (nfi.getFoodclass()!=pageindex){
                System.out.println("remove page"+pageindex+" item:"+foodselectpostion);
                foos.remove(foodselectpostion);
            }
        }else if(pageindex==6){
            String today = getYear(date) + getMonth(date) + getDay(date);
            String s = getYear(ofi.getFoodDate()) + getMonth(ofi.getFoodDate()) + getDay(ofi.getFoodDate());
            if ((Long.parseLong(s) - Long.parseLong(today)) >= 0) {
                foos.remove(ofi);
            }
        }
        //foosAdapter.notifyDataSetChanged();
        myadapter.updateAllFoods();
    }


    private SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String date = sDateFormat.format(new Date());
    private void iniGridView(final GridView foodlist, int index){

        foos=dba.getAll();
        ArrayList<FoodItem> foodItems=new ArrayList<>();
        if (index ==6){
            for (FoodItem item:foos){
                String today = getYear(date) + getMonth(date) + getDay(date);
                String s = getYear(item.getFoodDate()) + getMonth(item.getFoodDate()) + getDay(item.getFoodDate());
                if ((Long.parseLong(s) - Long.parseLong(today)) < 0) {
                    foodItems.add(item);
                }
            }
            foos=foodItems;
        }else if (index >0){
            for (FoodItem item:foos){
                if (item.getFoodclass()==(index-1)){
                    foodItems.add(item);
                }
            }
            foos=foodItems;
        }

        sortbydate();

        foosAdapter=new FoodListAdapter(this.getContext(),android.R.layout.simple_list_item_1,foos);
        System.out.println("new adapter in : "+position);

        if (myadapter.getFoodListAdaptersSize()<7) {
            myadapter.addFoodlistAdapter(foosAdapter);
        }else{
            myadapter.setFoodListAdapters(foosAdapter,pageindex);
        }

        foodlist.setAdapter(foosAdapter);
        foodlist.setChoiceMode(foodlist.CHOICE_MODE_MULTIPLE_MODAL);
        foodlist.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            private ArrayList<FoodItem> deleteitems;
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                final int checkedCount=foodlistgv.getCheckedItemCount();
                mode.setTitle(checkedCount+" Selected");

                int correctposition=position-foodlist.getFirstVisiblePosition();
                if (checked){

                    //foodlist.getFocusedChild().setBackgroundColor(ContextCompat.getColor(getContext(),R.color.accent));
                    foodlist.getChildAt(correctposition).setBackgroundColor(ContextCompat.getColor(getContext(),R.color.accent));
                }else {
                    //foodlist.getSelectedView().setBackgroundColor(ContextCompat.getColor(getContext(),R.color.cardview_light_background));
                    foodlist.getChildAt(correctposition).setBackgroundColor(ContextCompat.getColor(getContext(),R.color.cardview_light_background));
                }

                foosAdapter.toggleSelection(position);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.foodlist_select_menu,menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                boolean result=false;
                SparseBooleanArray selected=foosAdapter.getSelectedIds();

                deleteitems=new ArrayList<FoodItem>();
                for (int i=(selected.size()-1);i>=0;i--){
                    if (selected.valueAt(i)) {
                        FoodItem foodItem = foosAdapter.getItem(selected.keyAt(i));
                        deleteitems.add(foodItem);
                    }
                }
                System.out.println("select size: "+deleteitems.size());
                switch (item.getItemId()){

                    case R.id.delete:
                        new AlertDialog.Builder(getContext())
                                .setMessage("確認要刪除?")
                                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        for (FoodItem deleteitem : deleteitems) {
                                            foosAdapter.remove(deleteitem);
                                            dba.delete(deleteitem);
                                            myadapter.updateAllFoods();
                                            mode.finish();
                                        }
                                    }
                                })
                                .setPositiveButton("NO",null)
                                .show();


                        result=true;
                        break;

                    case R.id.Search:
                        String searchstring="";
                        for (FoodItem deleteitem : deleteitems) {
                            if (searchstring.equals("")){
                                searchstring+=deleteitem.getFoodName();
                            }else{
                                searchstring+=" "+deleteitem.getFoodName();
                            }

                        }
                        myadapter.searchrecipe(searchstring);
                        System.out.println(searchstring);
                        mode.finish();
                        result=true;
                        break;
                    default:
                        result=false;

                }

                return result;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                for (int i=(foodlist.getChildCount()-1);i>=0;i--){
                    foodlist.getChildAt(i).setBackgroundColor(ContextCompat.getColor(getContext(),R.color.cardview_light_background));
                }
                foosAdapter.removeSelection();
            }
        });

        foodlistgv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getContext(),Newfood.class);
                intent.putExtra("Food",foos.get(position));
                System.out.println("foodselectedindex: "+position);
                foodselectpostion=position;
                startActivityForResult(intent,position);
            }
        });

    }

    private void sortbydate(){
        Collections.sort(foos, new Comparator<FoodItem>() {
            @Override
            public int compare(FoodItem lhs, FoodItem rhs) {
                return lhs.getFoodDate().compareTo(rhs.getFoodDate());
            }
        });

    }
    private int searchitemposition=0;
    public static int searchRequeatcode=32760;
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        System.out.println("FoodListCardFragment resultCode: "+resultCode);
        if(resultCode==2){
            FoodItem item=data.getParcelableExtra("newFood");
            System.out.println("新的食物照片："+item.getImg());

            addFood(item);



        }else if(resultCode==3){
            FoodItem nfi = data.getParcelableExtra("Food");

            System.out.println(requestCode);
            editFood(nfi);
            System.out.println("更新" + requestCode + "照片：" + nfi.getImg());

        }else if (resultCode==4){
            BarCodeParcel barCodeParcel=data.getParcelableExtra("BarCode");
            if (barCodeParcel!=null) {
                String Barcode = barCodeParcel.getCode();
                Toast.makeText(getContext(),"search by code: "+Barcode,Toast.LENGTH_SHORT);
                final ArrayList<FoodItem> foodItems=dba.getByBarCode(Barcode);
                if (foodItems.size()>0){
                    final View v= LayoutInflater.from(this.getContext()).inflate(R.layout.ingredient_connectitem,null,false);
                    ListView gv =(ListView)v.findViewById(R.id.ing_connectfoodlist);

                    ConnectFoodAdapter list = new ConnectFoodAdapter(getContext(), android.R.layout.simple_list_item_1,foodItems);
                    gv.setAdapter(list);
                    /*gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent=new Intent(FoodListCardFragment.this.getActivity(),Newfood.class);
                            intent.putExtra("Food",foodItems.get(position));
                            searchitemposition=position;

                            startActivityForResult(intent,FoodListCardFragment.searchRequeatcode);
                        }
                    });*/
                    new AlertDialog.Builder(getContext())
                            .setTitle("冰箱庫存")
                            .setView(v)
                            .setPositiveButton("OK", null)
                            .show();
                }else{
                    new AlertDialog.Builder(getContext())
                            .setTitle("冰箱庫存")
                            .setMessage("沒有該條碼編號的庫存")
                            .setPositiveButton("OK", null)
                            .show();
                }

            }
        }
        else if(resultCode==RESULT_OK){
            Intent intent=new Intent(getActivity(),Newfood.class);
            intent.putExtra("IMG",ImageUri);
            intent.putExtra("foodclass",pageindex);
            startActivityForResult(intent,2);
        }
    }
    private String getYear(String date) {
        System.out.println("Y:" + date.substring(0, 4));
        return date.substring(0, 4);

    }

    private String getMonth(String date) {
        System.out.println("M:" + date.substring(5, 7));
        return date.substring(5, 7);
    }

    private String getDay(String date) {
        System.out.println("D:" + date.substring(8, 10));
        return date.substring(8, 10);
    }

}

