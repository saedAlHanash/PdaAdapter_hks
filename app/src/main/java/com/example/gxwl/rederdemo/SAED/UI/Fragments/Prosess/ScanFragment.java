package com.example.gxwl.rederdemo.SAED.UI.Fragments.Prosess;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.gxwl.rederdemo.R;
import com.example.gxwl.rederdemo.SAED.UI.Activities.ClientActivity;
import com.example.gxwl.rederdemo.SAED.ViewModels.MyViewModel;
import com.example.gxwl.rederdemo.SAED.ViewModels.Product;
import com.example.gxwl.rederdemo.adapter.AdapterItemEpc;

import java.util.ArrayList;


@SuppressLint("NonConstantResourceId")
public class ScanFragment extends Fragment implements View.OnClickListener,
        ClientActivity.OnReadTag, AdapterItemEpc.OnItemClicked {

    Button btnStart;
    Button btnStop;
    Button btnClear;

    TextView typeScanTv;

    //region base

    RecyclerView recyclerView;

    AdapterItemEpc adapter;
    ArrayList<String> sentEpc = new ArrayList<>();
    ClientActivity myActivity;
    MyViewModel myViewModel;
    View view;

    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myActivity = (ClientActivity) requireActivity();
        myViewModel = myActivity.myViewModel;

        myViewModel.productLiveData = new MutableLiveData<>();

        view = inflater.inflate(R.layout.fragment_scan, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        initAdapter();

        initView();

        return view;
    }


    final Observer<Product> Observer = product -> {
        if (!isAdded())
            return;
        if (product == null)
            return;

        sentEpc.add(product.epc);
        adapter.insertItem(product);

    };

    void initView() {

        btnStart = view.findViewById(R.id.read);
        btnStop = view.findViewById(R.id.stop);
        btnClear = view.findViewById(R.id.clean);

        typeScanTv = view.findViewById(R.id.scan_type_tv);


        btnStart.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        typeScanTv.setOnClickListener(this);

    }

    void initAdapter() {

        if (adapter == null)
            adapter = new AdapterItemEpc(myActivity, new ArrayList<>());

        adapter.setOnItemClicked(this);

        recyclerView.setAdapter(adapter);
    }

    void observeProduct() {
        myViewModel.productLiveData.observe(myActivity, Observer);
    }

    void removeObserveProduct() {
        myViewModel.productLiveData.removeObserver(Observer);
    }

    int x = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.read: {
                myActivity.read();
//                x += 1;
//                myViewModel.getProduct(myActivity.socket, "000" + x);
                break;
            }

            case R.id.stop: {
                myActivity.stop();
                break;
            }

            case R.id.clean: {
                x = 0;
                sentEpc.clear();
                adapter.setAndRefresh(new ArrayList<>());
                break;
            }

        }
    }


    @Override
    public void onPause() {
        super.onPause();
        removeObserveProduct();
        myActivity.onReadTag = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        observeProduct();
        myActivity.onReadTag = this;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        myActivity.stop();
    }

    @Override
    public void onStop() {
        super.onStop();
        myActivity.stop();
    }

    @Override
    public void onRead(@NonNull String epc, int rssi) {
        if (sentEpc.contains(epc))
            return;

        myViewModel.getProduct(myActivity.socket, epc);
    }

    @Override
    public void onClicked(int position, ArrayList<Product> list) {

    }
}