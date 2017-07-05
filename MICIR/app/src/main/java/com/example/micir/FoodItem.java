package com.example.micir;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;

/**
 * Created by 正文 on 2016/10/13.
 */

public class FoodItem implements Parcelable{
    private Long id;
    private int foodclass;
    private String FoodName;
    private String BuyDate;
    private String FoodDate;
    private int FoodQuantity;
    private Bitmap img;
    private String barcode;
    public FoodItem(String name,String BDate,String Fdate,int q,Bitmap i,String barcode,int foodclass){
        super();
        FoodName=name;
        BuyDate=BDate;
        FoodDate=Fdate;
        FoodQuantity=q;
        img=i;
        this.barcode=barcode;
        this.foodclass=foodclass;
    }
    public void setId(Long i){
        id=i;
    }
    public Long getId(){
        return id;
    }

    public void setFoodclass(int foodclass){
        this.foodclass=foodclass;
    }
    public int getFoodclass(){
        return foodclass;
    }
    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getFoodQuantity() {
        return FoodQuantity;
    }

    public String getBuyDate() {
        return BuyDate;
    }

    public void setBuyDate(String buyDate) {
        BuyDate = buyDate;
    }

    public void setFoodQuantity(int foodQuantity) {
        FoodQuantity = foodQuantity;
    }

    public String getFoodDate() {
        return FoodDate;
    }

    public String getFoodName() {
        return FoodName;
    }

    public void setFoodDate(String foodDate) {
        FoodDate = foodDate;
    }

    public void setFoodName(String foodName) {
        FoodName = foodName;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(FoodName);
        dest.writeString(BuyDate);
        dest.writeString(FoodDate);
        dest.writeInt(FoodQuantity);
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        if (img!=null){
            img.compress(Bitmap.CompressFormat.PNG,0,stream);
            byte[] imgByteArray=stream.toByteArray();
            dest.writeInt(imgByteArray.length);
            dest.writeByteArray(imgByteArray);
        }else{
            dest.writeInt(-1);
        }
        dest.writeString(barcode);
        dest.writeInt(foodclass);
    }
    public static final Parcelable.Creator<FoodItem> CREATOR =new Creator<FoodItem>() {
        @Override
        public FoodItem createFromParcel(Parcel source) {
            String FoodName=source.readString();
            String BuyDate=source.readString();
            String FoodDate=source.readString();
            int FoodQuantity=source.readInt();
            int imglen=source.readInt();;
            System.out.println(imglen);
            Bitmap img=null;
            if (imglen>-1){
                byte[] bytes=new byte[imglen];
                source.readByteArray(bytes);
                img=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            }


            String barcode=source.readString();;
            int foodclass=source.readInt();
            return new FoodItem(FoodName,BuyDate,FoodDate,FoodQuantity,img,barcode,foodclass);
        }

        @Override
        public FoodItem[] newArray(int size) {
            return new FoodItem[size];
        }
    };
}
