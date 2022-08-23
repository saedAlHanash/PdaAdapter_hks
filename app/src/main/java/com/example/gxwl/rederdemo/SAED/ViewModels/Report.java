package com.example.gxwl.rederdemo.SAED.ViewModels;


import java.util.ArrayList;
import java.util.Date;

public class Report {

    public int wid;
    public int lid;
    public Date dt;
    public String usr;
    public ArrayList<String> epcs1;
    public ArrayList<String> epcs2;
    public ArrayList<String> epcs3;
    public ArrayList<String> epcs4;

    public Report() {
        this.wid = 0;
        this.lid = 0;
        this.dt = new Date();
        this.usr = "";
        this.epcs1 = new ArrayList<>();
        this.epcs2 = new ArrayList<>();
        this.epcs3 = new ArrayList<>();
        this.epcs4 = new ArrayList<>();

    }
}