package com.example.micir;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.micir.myParcelObject.*;

import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 正文 on 2016/10/11.
 */

public class Newfood extends AppCompatActivity{
    private EditText editFoodName;
    private EditText editFoodQuantity;
    private Button editBuyDate;
    private Button editFoodDate;
    private Button barcodebtn;
    private ImageButton foodimg;
    private ImageButton addfood;
    private ImageButton cancel;
    private FoodItem Foodinfo;
    private Bitmap imgsource=null;
    private Uri ImageUri;
    private String formatstring;
    private Spinner foodclass;
    private String[] class_string={"肉類","菜類","水果","海鮮","其他"};
    private static final int ZBAR_CAMERA_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_food);

        editFoodName=(EditText) findViewById(R.id.editfoodname);
        editFoodQuantity=(EditText) findViewById(R.id.editquantity);
        editBuyDate=(Button)findViewById(R.id.editbuyDate);
        editFoodDate=(Button)findViewById(R.id.editfoodDate);
        barcodebtn=(Button) findViewById(R.id.barcode);
        foodimg=(ImageButton)findViewById(R.id.foodImgButton);
        addfood=(ImageButton)findViewById(R.id.addfood);
        cancel=(ImageButton)findViewById(R.id.canceladdfood);
        foodclass=(Spinner)findViewById(R.id.foodclass);
        ArrayAdapter<String> class_string_adapter=new ArrayAdapter<String>(Newfood.this,R.layout.myspinneritem,class_string);
        class_string_adapter.setDropDownViewResource(R.layout.myspinneritem);
        foodclass.setAdapter(class_string_adapter);

        Intent intent=getIntent();
        if (intent.getExtras().get("Food")!=null){
            Foodinfo=intent.getParcelableExtra("Food");
            inieditmode();
            iniEditDatePicker();
        }else if(intent.getExtras().get("IMG") !=null){
            showImg((Uri) intent.getExtras().get("IMG"));
            int clss=intent.getIntExtra("foodclass",0)-1;
            if (clss <0) {clss=0;}
            foodclass.setSelection(clss);
            iniNewDatepicker();
            iniNewbtn();


        }   else {
            BarCodeParcel code=getIntent().getParcelableExtra("BarCode");
            barcodebtn.setText(code.getCode());
            //barcodebtn.setEnabled(false);
            int clss=intent.getIntExtra("foodclass",0)-1;
            if (clss <0) {clss=0;}
            foodclass.setSelection(clss);
            iniNewbtn();
            iniNewDatepicker();
        }
        barcodebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Newfood.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(Newfood.this,
                            new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
                } else {
                    Intent intent = new Intent(Newfood.this, Scanner.class);
                    intent.putExtra("mode",1);
                    startActivityForResult(intent,3);
                }
            }
        });
        foodimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item[]={"拍一張新照片","從相簿裡選擇"};
                new AlertDialog.Builder(Newfood.this).setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                Intent picit=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                String dir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                                String fname="Micir_p"+ System.currentTimeMillis()+".jpg";
                                ImageUri=Uri.parse("file://"+dir+"/"+fname);
                                System.out.println("file://"+dir+"/"+fname);

                                picit.putExtra(MediaStore.EXTRA_OUTPUT,ImageUri);
                                startActivityForResult(picit,1);
                                break;
                            case 1:
                                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(intent,2);
                                break;
                        }
                    }
                }).show();

            }
        });

    }
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode==3){
            if (resultCode==2){
                BarCodeParcel code=data.getParcelableExtra("BarCode");
                barcodebtn.setText(code.getCode());
            }
        }else{
            if (resultCode==RESULT_OK) {
                if (requestCode == 1) {
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, ImageUri);
                    sendBroadcast(intent);

                    showImg(ImageUri);

                } else if (requestCode == 2) {
                    showImg(data.getData());
                }
            }
        }

    }

    private void showImg(final Uri uri){

        int iw,ih,vw,vh;
        try{

            Bitmap img=BitmapFactory.decodeStream(Newfood.this.getContentResolver().openInputStream(uri));
            img=ImageCrop(img);
            iw=img.getWidth();

            ih=img.getHeight();

            vw=foodimg.getLayoutParams().width;
            vh=foodimg.getLayoutParams().height;
            Matrix matrix = new Matrix();
            float scale=Math.min((float)vh/ih,(float)vw/iw);

            matrix.postScale(scale,scale);
            img=Bitmap.createBitmap(img,0,0,iw,ih,matrix,true);

            if (Foodinfo !=null){
                Foodinfo.setImg(img);
            }
            foodimg.setImageBitmap(img);
            imgsource=img;



        }catch (FileNotFoundException e){
            e.printStackTrace();
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
    private void inieditmode(){
        barcodebtn.setText(Foodinfo.getBarcode());
        editFoodName.setText(Foodinfo.getFoodName());
        editFoodQuantity.setText(String.valueOf(Foodinfo.getFoodQuantity()));
        editBuyDate.setText(Foodinfo.getBuyDate());
        editFoodDate.setText(Foodinfo.getFoodDate());
        foodclass.setSelection(Foodinfo.getFoodclass());
        if (Foodinfo.getImg()!=null) {
            int iw,ih,vw,vh;
            iw=Foodinfo.getImg().getWidth();
            System.out.println(iw);
            ih=Foodinfo.getImg().getHeight();
            System.out.println(ih);
            vw=foodimg.getLayoutParams().width;
            vh=foodimg.getLayoutParams().height;
            Matrix matrix = new Matrix();
            float scale=Math.min((float)vh/ih,(float)vw/iw);

            matrix.postScale(scale,scale);
            Bitmap img=Bitmap.createBitmap(Foodinfo.getImg(),0,0,iw,ih,matrix,true);
            foodimg.setImageBitmap(img);
        }
        addfood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Q=editFoodQuantity.getText().toString();
                if (Q.equals("")){
                    Toast.makeText(v.getContext(),"請填寫數量",Toast.LENGTH_SHORT);
                }else{
                    Foodinfo.setBarcode(barcodebtn.getText().toString());
                    Foodinfo.setFoodName(editFoodName.getText().toString());
                    Foodinfo.setFoodQuantity(Integer.parseInt(Q));
                    Foodinfo.setBuyDate(editBuyDate.getText().toString());
                    Foodinfo.setFoodDate(editFoodDate.getText().toString());
                    System.out.println("edit class:"+foodclass.getSelectedItemPosition());
                    Foodinfo.setFoodclass(foodclass.getSelectedItemPosition());
                    Intent intent=new Intent();
                    intent.putExtra("Food",Foodinfo);
                    setResult(3,intent);
                    finish();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void iniNewbtn(){



        addfood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Newfood.this,Scanner.class);
                String code=barcodebtn.getText().toString();
                String name=editFoodName.getText().toString();
                String buy_date=editBuyDate.getText().toString();
                String food_date=editFoodDate.getText().toString();
                int foodQ=0;
                if (!(editFoodQuantity.getText().toString().equals(""))) {
                    foodQ = Integer.parseInt(editFoodQuantity.getText().toString());
                }
                int foodclassindex=foodclass.getSelectedItemPosition();
                Foodinfo=new FoodItem(name,buy_date,food_date,foodQ,imgsource,code,foodclassindex);
                intent.putExtra("newFood",Foodinfo);
                setResult(2,intent);
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(1);
                finish();
            }
        });
    }

    private Calendar calendar=Calendar.getInstance();
    private int mYear;
    private int mMonth;
    private int mDay;
    private void iniNewDatepicker(){
        mYear = calendar.get(Calendar.YEAR);
        mMonth= calendar.get(Calendar.MONTH);
        mDay  = calendar.get(Calendar.DAY_OF_MONTH);
        Button buydate=(Button) findViewById(R.id.editbuyDate);
        formatstring=String.format("%04d-%02d-%02d",mYear,mMonth+1,mDay);
        buydate.setText(formatstring);

        Button fooddate=(Button) findViewById(R.id.editfoodDate);
        formatstring=String.format("%04d-%02d-%02d",mYear,mMonth+1,mDay+3);
        fooddate.setText(formatstring);



        buydate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(1);

            }
        });
        fooddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(2);

            }
        });
    }
    private void iniEditDatePicker(){
        Button buydate=(Button) findViewById(R.id.editbuyDate);
        buydate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(1);

            }
        });
        Button fooddate=(Button) findViewById(R.id.editfoodDate);
        fooddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(2);

            }
        });
    }

    private boolean compareDate(){
        String s=editBuyDate.getText().toString();
        Long buy=Long.parseLong(getYear(s)+getMonth(s)+getDay(s));
        s=editFoodDate.getText().toString();
        Long food=Long.parseLong(getYear(s)+getMonth(s)+getDay(s));
        return buy>food;
    }
    private String getYear(String date){
        System.out.println("Y:"+date.substring(0,4));
        return date.substring(0,4);

    }
    private String getMonth(String date){
        System.out.println("M:"+date.substring(5,7));
        return date.substring(5,7);
    }
    private String getDay(String date){
        System.out.println("D:"+date.substring(8,10));
        return date.substring(8,10);
    }

    private void showDatePicker(int id){
        int y,m,d;

        DatePickerDialog datePickerDialog;
        if (id==1){
            y=Integer.parseInt(getYear(editBuyDate.getText().toString()));
            m=Integer.parseInt(getMonth(editBuyDate.getText().toString()));
            d=Integer.parseInt(getDay(editBuyDate.getText().toString()));
            datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Button btn;
                    btn = (Button) findViewById(R.id.editbuyDate);
                    formatstring=String.format("%04d-%02d-%02d",year,monthOfYear+1,dayOfMonth);
                    btn.setText(formatstring);

                }
            },y,m-1,d);
            datePickerDialog.show();
        }else if(id==2){
            y=Integer.parseInt(getYear(editFoodDate.getText().toString()));
            m=Integer.parseInt(getMonth(editFoodDate.getText().toString()));
            d=Integer.parseInt(getDay(editFoodDate.getText().toString()));
            datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Button btn;
                    btn= (Button) findViewById(R.id.editfoodDate);
                    formatstring=String.format("%04d-%02d-%02d",year,monthOfYear+1,dayOfMonth);
                    btn.setText(formatstring);
                }
            },y,m-1,d);
            DatePicker datePicker=datePickerDialog.getDatePicker();
            datePicker.setMinDate(new Date().getTime());
            datePickerDialog.show();
        }

    }

}
