package com.example.gxwl.rederdemo.SAED.ViewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.gxwl.rederdemo.SAED.Network.SaedSocket;

import java.util.ArrayList;

public class MyViewModel extends ViewModel {

    public MutableLiveData<ArrayList<All>> allLiveData = new MutableLiveData<>();
    public MutableLiveData<Product> productLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> connectLiveData = new MutableLiveData<>();

    public void getAll(SaedSocket socket) {
        if (allLiveData.getValue() != null) {
            allLiveData.postValue(allLiveData.getValue());
            return;
        }

        socket.send("1,");
    }

    public void getProduct(SaedSocket socket, String epc) {
        socket.send("0," + epc);
    }

}
