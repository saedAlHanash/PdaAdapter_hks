package com.example.gxwl.rederdemo.SAED.UI.Fragments.Client;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gxwl.rederdemo.R;
import com.example.gxwl.rederdemo.entity.TagInfo;
import com.example.gxwl.rederdemo.util.GlobalClient;
import com.example.gxwl.rederdemo.util.ToastUtils;
import com.example.gxwl.rederdemo.util.UtilSound;
import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.dal.HandlerTagEpcLog;
import com.gg.reader.api.protocol.gx.EnumG;
import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
import com.gg.reader.api.protocol.gx.MsgBaseInventoryEpc;
import com.gg.reader.api.protocol.gx.MsgBaseStop;
import com.gg.reader.api.protocol.gx.ParamEpcReadReserved;
import com.gg.reader.api.protocol.gx.ParamEpcReadTid;
import com.gg.reader.api.protocol.gx.ParamEpcReadUserdata;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


@SuppressLint("NonConstantResourceId")
public class SmartScanFragment extends Fragment implements View.OnClickListener, HandlerTagEpcLog {
    View view;

    @BindView(R.id.imageView8)
    ImageView imageView8;
    @BindView(R.id.epc)
    EditText epc;

    @BindView(R.id.read)
    Button read;
    @BindView(R.id.stop)
    Button stop;
    @BindView(R.id.rssi)
    TextView rssiTv;

    private final GClient client = GlobalClient.getClient();

    private boolean isReader = false;

    private Runnable timeTask = null;
    private final Handler soundHandler = new Handler();
    long rateValue = 1L;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_smart_scan, container, false);
        ButterKnife.bind(this, view);

//        new Handler(Looper.getMainLooper()).postDelayed(() -> imageView8.setImageLevel(1), 1000);
//        new Handler(Looper.getMainLooper()).postDelayed(() -> imageView8.setImageLevel(2), 3000);
//        new Handler(Looper.getMainLooper()).postDelayed(() -> imageView8.setImageLevel(3), 5000);
//        new Handler(Looper.getMainLooper()).postDelayed(() -> imageView8.setImageLevel(4), 7000);
//        new Handler(Looper.getMainLooper()).postDelayed(() -> imageView8.setImageLevel(5), 8000);
//        new Handler(Looper.getMainLooper()).postDelayed(() -> imageView8.setImageLevel(6), 9000);

        read.setOnClickListener(this);
        stop.setOnClickListener(this);

        if (client != null)
            client.onTagEpcLog = this;

        return view;
    }

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

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            if (msg.what > 0 && msg.what <= 7) {
                imageView8.setImageLevel(msg.what);
                rssiTv.setText("-" + rssi + "dB");
            }
        }
    };

    private final Handler handlerStop = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message param1Message) {
            if (param1Message.what == 1) {
                rateValue = 0L;
                soundHandler.removeCallbacks(timeTask);
            }

            super.handleMessage(param1Message);
        }
    };

    int rssi;

    @Override
    public void log(String s, LogBaseEpcInfo info) {

        if (!info.getEpc().equals(epc.getText().toString()))
            return;

        rssi = info.getRssi();


        if (rssi < 60) {
            handler.sendEmptyMessage(0);
            return;
        }

        if (rssi > 60 && rssi < 68)
            handler.sendEmptyMessage(1);

        else if (rssi > 68 && rssi < 76)
            handler.sendEmptyMessage(2);

        else if (rssi > 76 && rssi < 84)
            handler.sendEmptyMessage(3);

        else if (rssi > 84 && rssi < 92)
            handler.sendEmptyMessage(4);

        else if (rssi > 92 && rssi < 100)
            handler.sendEmptyMessage(5);

        else if (rssi > 100 && rssi < 108)
            handler.sendEmptyMessage(6);

        else if (rssi > 108)
            handler.sendEmptyMessage(7);

    }

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

    //程序退出
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isReader) {
            MsgBaseStop msgStop = new MsgBaseStop();
            client.sendSynMsg(msgStop);
            if (msgStop.getRtCode() == 0)
                ToastUtils.showText(getResources().getString(R.string.stop_card));
            else
                ToastUtils.showText(msgStop.getRtMsg());

        }

        rateValue = 0L;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (client == null)
            return;

        if (isReader) {

            MsgBaseStop msgStop = new MsgBaseStop();
            client.sendSynMsg(msgStop);

            if (msgStop.getRtCode() == 0) {

                soundHandler.removeCallbacks(timeTask);
                isReader = false;
                ToastUtils.showText(getResources().getString(R.string.stop_card));

            } else
                ToastUtils.showText(msgStop.getRtMsg());

        }

    }
}