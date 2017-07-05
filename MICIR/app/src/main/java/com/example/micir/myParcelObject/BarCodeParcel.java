package com.example.micir.myParcelObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 正文 on 2016/10/11.
 */

public class BarCodeParcel implements Parcelable {
    private int id;
    private String code;
    public BarCodeParcel(){
        super();
    }
    public BarCodeParcel(int id,String code){
        super();
        this.id=id;
        this.code=code;
    }
    public int getid(){
        return id;
    }
    public void setId(int id){
        this.id=id;
    }
    public String getCode(){
        return code;
    }
    public void setCode(String code){
        this.code=code;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(code);
    }

    public static final Parcelable.Creator<BarCodeParcel> CREATOR =new Creator<BarCodeParcel>() {
        @Override
        public BarCodeParcel createFromParcel(Parcel source) {
            return new BarCodeParcel(source.readInt(),source.readString());
        }

        @Override
        public BarCodeParcel[] newArray(int size) {
            return new BarCodeParcel[size];
        }
    };
}
