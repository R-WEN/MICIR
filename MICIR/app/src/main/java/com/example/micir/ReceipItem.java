package com.example.micir;

import android.graphics.Bitmap;

/**
 * Created by 正文 on 2016/12/7.
 */

public class ReceipItem {
    private String name;
    private String ID;
    private String imgurl;
    private String description;
    private String ingredint;
    private String httpurl;
    private Bitmap imgsoruce;

    public Bitmap getImgsoruce() {
        return imgsoruce;
    }
    public void setImgsoruce(Bitmap source){
        imgsoruce=source;
    }
    public String getHttpurl(){
        return httpurl;
    }
    public void setHttpurl(String httpurl) {
        this.httpurl = httpurl;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImgurl() {
        return imgurl;
    }

    public String getIngredint() {
        return ingredint;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public void setIngredint(String ingredint) {
        this.ingredint = ingredint;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
