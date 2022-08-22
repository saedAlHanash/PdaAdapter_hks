package com.example.gxwl.rederdemo.SAED;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class All {
    public int id;
    public String name;
    public ArrayList<Location> locations;

    public class Location {
        public int id;
        public String name;
        public ArrayList<Epc> epcs;
    }

    public class Epc {
        public String epc;
        @SerializedName("pn")
        public String productName;
    }
}
