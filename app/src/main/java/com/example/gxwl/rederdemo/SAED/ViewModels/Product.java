package com.example.gxwl.rederdemo.SAED.ViewModels;

import android.graphics.Bitmap;

public class Product {
    public String epc;
    public String pn;
    public String pp;
    public String wn;
    public String ln;
    public String im;
    public Bitmap bitmap;

    @Override
    public String toString() {
        return "Product{" +
                "epc='" + epc + '\'' +
                ", pn='" + pn + '\'' +
                ", pp='" + pp + '\'' +
                ", wn='" + wn + '\'' +
                ", ln='" + ln + '\'' +
                ", im='" + im + '\'' +
                ", bitmap=" + bitmap +
                '}';
    }
}
