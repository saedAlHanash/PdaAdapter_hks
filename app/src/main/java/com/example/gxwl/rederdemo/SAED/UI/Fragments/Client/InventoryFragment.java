package com.example.gxwl.rederdemo.SAED.UI.Fragments.Client;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.gxwl.rederdemo.R;
import com.example.gxwl.rederdemo.SAED.UI.Activities.ClientActivity;
import com.example.gxwl.rederdemo.SAED.ViewModels.All;
import com.example.gxwl.rederdemo.SAED.ViewModels.MyViewModel;
import com.example.gxwl.rederdemo.util.GlobalClient;
import com.example.gxwl.rederdemo.util.ToastUtils;
import com.example.gxwl.rederdemo.util.UtilSound;
import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.dal.HandlerTagEpcLog;
import com.gg.reader.api.protocol.gx.EnumG;
import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
import com.gg.reader.api.protocol.gx.MsgBaseInventoryEpc;
import com.gg.reader.api.protocol.gx.MsgBaseStop;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class InventoryFragment extends Fragment implements View.OnClickListener, HandlerTagEpcLog {

    //region GLOBAL VAR

    //region Views
    @BindView(R.id.warehouses_spinner)
    Spinner warehousesSpinner;
    @BindView(R.id.inventory_spinner)
    Spinner inventorySpinner;

    @BindView(R.id.expected)
    TextView expected;
    @BindView(R.id.scaned)
    TextView scanned;
    @BindView(R.id.not_found)
    TextView notFound;
    @BindView(R.id.undefined)
    TextView undefined;

    @BindView(R.id.read)
    Button read;
    @BindView(R.id.stop)
    Button stop;
    @BindView(R.id.progressIndicator)
    ProgressBar progressBar;

    //endregion

    //region RFID
    private final GClient client = GlobalClient.getClient();

    private boolean isReader = false;

    private Runnable timeTask = null;
    private final Handler soundHandler = new Handler();
    long rateValue = 1L;

    //endregion

    //region spinners list
    ArrayList<String> listStringWarehouses = new ArrayList<>();
    ArrayList<String> listStringInventory = new ArrayList<>();
    ArrayAdapter<String> adapterWarehouses;
    ArrayAdapter<String> adapterInventory;

    //endregion

    //region lists models
    ArrayList<All> list;
    ArrayList<All.Location> listLocations;
    ArrayList<All.Epc> listEpc;
    ArrayList<String> listStringEpc = new ArrayList<>();

    ArrayList<String> scannedList = new ArrayList<>();
    ArrayList<String> undefinedList = new ArrayList<>();

    //endregion

    ClientActivity myActivity;
    MyViewModel myViewModel;
    View view;

    //endregion

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myActivity = (ClientActivity) requireActivity();
        myViewModel = myActivity.myViewModel;
        view = inflater.inflate(R.layout.fragment_inventory, container, false);

        ButterKnife.bind(this, view);

        listeners();

        getAll();

        return view;
    }

    void listeners() {

        warehousesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initInventorySpinners(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        inventorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initListEpc(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        read.setOnClickListener(this);
        stop.setOnClickListener(this);
    }

    //region data socket
    void getAll() {
        startLoading();
        myViewModel.getAll(myActivity.socket);
        myViewModel.allLiveData.observe(myActivity, observer);
    }

    final Observer<ArrayList<All>> observer = list -> {
        if (!isAdded())
            return;
        endLoading();

        if (list == null || list.isEmpty())
            return;

        this.list = list;

        initWarehousesSpinners();
    };

    //endregion

    //region adapters spinner and init list

    // 1
    void initWarehousesSpinners() {

        listStringWarehouses.clear();
        for (All all : list)
            listStringWarehouses.add(all.name);

        adapterWarehouses = new ArrayAdapter<>(myActivity,
                R.layout.item_spinner, R.id.textView, listStringWarehouses);

        adapterWarehouses.setDropDownViewResource(R.layout.item_spinner_drop);
        warehousesSpinner.setAdapter(adapterWarehouses);
    }

    //2
    void initInventorySpinners(int id) {

        if (list == null || list.isEmpty())
            return;

        listLocations = list.get(id).locations;

        if (listLocations == null)
            return;

        listStringInventory.clear();
        for (All.Location location : listLocations)
            listStringInventory.add(location.name);

        adapterInventory = new ArrayAdapter<>(myActivity,
                R.layout.item_spinner, R.id.textView, listStringInventory);
        adapterInventory.setDropDownViewResource(R.layout.item_spinner_drop);

        inventorySpinner.setAdapter(adapterInventory);
    }

    //3
    void initListEpc(int id) {
        if (listLocations == null)
            return;
        listEpc = listLocations.get(id).epcs;
        listStringEpc.clear();

        if (listEpc == null)
            return;

        for (All.Epc epc : listEpc)
            listStringEpc.add(epc.epc);

        expected.setText(String.valueOf(listEpc.size()));
    }

    //endregion

    //region read RFID
    void read() {

        if (!isReader) {

            MsgBaseInventoryEpc msg = new MsgBaseInventoryEpc();
            msg.setAntennaEnable(EnumG.AntennaNo_1 | EnumG.AntennaNo_2);

            msg.setInventoryMode(EnumG.InventoryMode_Inventory);

            client.sendSynMsg(msg);

            if (0x00 == msg.getRtCode()) {
                ToastUtils.showText("Start ReadCard");
                isReader = true;
                soundTask();

            } else {
                handlerStop.sendEmptyMessage(1);
                ToastUtils.showText(msg.getRtMsg());
            }
        } else
            ToastUtils.showText(getResources().getString(R.string.read_card_being));
    }

    void stop() {
        if (client == null)
            return;

        rateValue = 0L;
        MsgBaseStop msgStop = new MsgBaseStop();
        client.sendSynMsg(msgStop);
        if (0x00 == msgStop.getRtCode()) {
            isReader = false;
            ToastUtils.showText("Stop Success");
            handlerStop.sendEmptyMessage(1);
        } else
            ToastUtils.showText("Stop Fail");
    }

    void soundTask() {
        rateValue = 1L;
        timeTask = () -> {

            if (rateValue != 0L)
                UtilSound.play(1, 0);

            soundHandler.postDelayed(timeTask, 20L);
        };

        this.soundHandler.postDelayed(timeTask, 0L);
    }

    @Override
    public void log(String s, LogBaseEpcInfo info) {

        if (listStringEpc.isEmpty()) {
            handler.sendEmptyMessage(2);
            stop();
        }

        if (info == null)
            return;

        if (listStringEpc.contains(info.getEpc())) {
            scannedList.add(info.getEpc());
            listStringEpc.remove(info.getEpc());

            handler.sendEmptyMessage(0);

        } else if (undefinedList.contains(info.getEpc())) {
            undefinedList.add(info.getEpc());
            handler.sendEmptyMessage(1);
        }

    }

    //endregion

    //region Handlers
    private final Handler handlerStop = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message param1Message) {
            if (param1Message.what == 1) {
                rateValue = 0L;
                soundHandler.removeCallbacks(timeTask);
            }

            super.handleMessage(param1Message);
        }
    };

    private final Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0: {
                    scanned.setText(String.valueOf(scannedList.size()));
                    notFound.setText(String.valueOf(listStringEpc.size()));

                    break;
                }
                case 1: {
                    undefined.setText(String.valueOf(undefinedList.size()));
                    notFound.setText(String.valueOf(listEpc.size()));
                    break;
                }
                case 2: {
                    ToastUtils.showText("تم قراءة جميع العناصر المتوقعة وإيقاف المسح");
                    break;
                }
            }

        }

    };

    //endregion

    //region show hied
    void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    void endLoading() {
        progressBar.setVisibility(View.GONE);
    }

    //endregion

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.read: {

                read();
                break;
            }
            case R.id.stop: {

                stop();
                break;
            }
        }
    }

    void removeObserver() {
        if (myViewModel.allLiveData != null)
            myViewModel.allLiveData.removeObserver(observer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        removeObserver();
    }
}