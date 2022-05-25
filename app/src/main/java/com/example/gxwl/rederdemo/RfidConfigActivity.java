package com.example.gxwl.rederdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.gxwl.rederdemo.util.CheckCommunication;
import com.example.gxwl.rederdemo.util.GlobalClient;
import com.example.gxwl.rederdemo.util.LocalManageUtil;
import com.example.gxwl.rederdemo.util.ToastUtils;
import com.gg.reader.api.protocol.gx.MsgBaseGetAutoDormancy;
import com.gg.reader.api.protocol.gx.MsgBaseGetBaseband;
import com.gg.reader.api.protocol.gx.MsgBaseGetFreqRange;
import com.gg.reader.api.protocol.gx.MsgBaseGetPower;
import com.gg.reader.api.protocol.gx.MsgBaseGetTagLog;
import com.gg.reader.api.protocol.gx.MsgBaseSetAutoDormancy;
import com.gg.reader.api.protocol.gx.MsgBaseSetBaseband;
import com.gg.reader.api.protocol.gx.MsgBaseSetFreqRange;
import com.gg.reader.api.protocol.gx.MsgBaseSetPower;
import com.gg.reader.api.protocol.gx.MsgBaseSetTagLog;

import java.util.Hashtable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RfidConfigActivity extends AppCompatActivity {

    @BindView(R.id.base_Speed)
    Spinner base_Speed;
    @BindView(R.id.session)
    Spinner session;
    @BindView(R.id.q)
    Spinner q;
    @BindView(R.id.inventory)
    Spinner inventory;

    @BindView(R.id.frequency)
    Spinner frequency;

    @BindView(R.id.spinner1)
    Spinner spinner1;

    @BindView(R.id.auto_mode)
    Spinner auto_mode;
    @BindView(R.id.auto_time)
    EditText auto_time;

    @BindView(R.id.filter_time)
    EditText filter_time;
    @BindView(R.id.rssi_value)
    EditText rssi_value;

    @BindView(R.id.ant_query)
    Button ant_query;
    @BindView(R.id.ant_config)
    Button ant_config;


    private Hashtable<Integer, Integer> mAntMap = new Hashtable<Integer, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rfid_config);
        ButterKnife.bind(this);
        if (CheckCommunication.check()) {//检测通信是否正常避免多次超时渲染卡顿
            baseQuery();
            frequencyQuery();
            upQuery();
            autoQuery();
            antQuery();
        }
    }

    @OnClick(R.id.base_query)
    public void baseQuery() {
        MsgBaseGetBaseband msg = new MsgBaseGetBaseband();
        GlobalClient.getClient().sendSynMsg(msg);
        if (msg.getRtCode() == 0) {
            ToastUtils.showText("The query is successful");
            if (msg.getBaseSpeed() == 255) {
                base_Speed.setSelection(5);
            } else {
                base_Speed.setSelection(msg.getBaseSpeed());
            }
            q.setSelection(msg.getqValue());
            session.setSelection(msg.getSession());
            inventory.setSelection(msg.getInventoryFlag());
        } else {
            ToastUtils.showText(msg.getRtMsg());
        }
    }

    @OnClick(R.id.base_config)
    public void baseConfig() {
        MsgBaseSetBaseband msg = new MsgBaseSetBaseband();
        if (base_Speed.getSelectedItemPosition() == 5) {
            msg.setBaseSpeed(255);
        } else {
            msg.setBaseSpeed(base_Speed.getSelectedItemPosition());
        }
        msg.setSession(session.getSelectedItemPosition());
        msg.setqValue(q.getSelectedItemPosition());
        msg.setInventoryFlag(inventory.getSelectedItemPosition());
        GlobalClient.getClient().sendSynMsg(msg);
        if (msg.getRtCode() == 0) {
            ToastUtils.showText(msg.getRtMsg());
        } else {
            ToastUtils.showText(msg.getRtMsg());
        }
    }

    @OnClick(R.id.frequency_query)
    public void frequencyQuery() {
        MsgBaseGetFreqRange msg = new MsgBaseGetFreqRange();
        GlobalClient.getClient().sendSynMsg(msg);
        if (msg.getRtCode() == 0) {
            ToastUtils.showText("The query is successful");
            System.out.println(msg.getFreqRangeIndex());
            frequency.setSelection(msg.getFreqRangeIndex());
        } else {
            ToastUtils.showText(msg.getRtMsg());
        }
    }

    @OnClick(R.id.frequency_config)
    public void frequencyConfig() {
        MsgBaseSetFreqRange msg = new MsgBaseSetFreqRange();
        msg.setFreqRangeIndex(frequency.getSelectedItemPosition());
        GlobalClient.getClient().sendSynMsg(msg);
        if (msg.getRtCode() == 0) {
            ToastUtils.showText(msg.getRtMsg());
        } else {
            ToastUtils.showText(msg.getRtMsg());
        }
    }

    @OnClick(R.id.auto_query)
    public void autoQuery() {
        MsgBaseGetAutoDormancy msg = new MsgBaseGetAutoDormancy();
        GlobalClient.getClient().sendSynMsg(msg);
        if (msg.getRtCode() == 0) {
            ToastUtils.showText("The query is successful");
            auto_mode.setSelection(msg.getOnOff());
            auto_time.setText(msg.getFreeTime() + "");
        } else {
            ToastUtils.showText(msg.getRtMsg());
        }
    }

    @OnClick(R.id.auto_config)
    public void autoConfig() {
        MsgBaseSetAutoDormancy msg = new MsgBaseSetAutoDormancy();
        msg.setOnOff(auto_mode.getSelectedItemPosition());
        msg.setFreeTime(Integer.parseInt(auto_time.getText().toString()));
        GlobalClient.getClient().sendSynMsg(msg);
        if (msg.getRtCode() == 0) {
            ToastUtils.showText(msg.getRtMsg());
        } else {
            ToastUtils.showText(msg.getRtMsg());
        }
    }

    @OnClick(R.id.up_query)
    public void upQuery() {
        MsgBaseGetTagLog msg = new MsgBaseGetTagLog();
        GlobalClient.getClient().sendSynMsg(msg);
        if (msg.getRtCode() == 0) {
            ToastUtils.showText("The query is successful");
            System.out.println(msg);
            filter_time.setText(msg.getRepeatedTime() + "");
            rssi_value.setText(msg.getRssiTV() + "");
        } else {
            ToastUtils.showText(msg.getRtMsg());
        }
    }

    @OnClick(R.id.up_config)
    public void upConfig() {
        MsgBaseSetTagLog msg = new MsgBaseSetTagLog();
        msg.setRepeatedTime(Integer.parseInt(filter_time.getText().toString()));
        msg.setRssiTV(Integer.parseInt(rssi_value.getText().toString()));
        GlobalClient.getClient().sendSynMsg(msg);
        if (msg.getRtCode() == 0) {
            ToastUtils.showText(msg.getRtMsg());
        } else {
            ToastUtils.showText(msg.getRtMsg());
        }
    }


    @OnClick(R.id.ant_query)
    public void antQuery() {
        MsgBaseGetPower msg = new MsgBaseGetPower();
        GlobalClient.getClient().sendSynMsg(msg);
        if (msg.getRtCode() == 0) {
            ToastUtils.showText("The query is successful");
            Integer powerIndex = msg.getDicPower().get(1);
            spinner1.setSelection(powerIndex);
        } else {
            ToastUtils.showText(msg.getRtMsg());
        }
    }

    @OnClick(R.id.ant_config)
    public void antConfig() {
        MsgBaseSetPower msg = new MsgBaseSetPower();
        int powerIndex = spinner1.getSelectedItemPosition();
//        if (powerIndex == 30 || powerIndex == 31 || powerIndex == 32) {
//            powerIndex = 33;
//        }
        mAntMap.put(1, powerIndex);
        msg.setDicPower(mAntMap);
        GlobalClient.getClient().sendSynMsg(msg);
        if (msg.getRtCode() == 0) {
            ToastUtils.showText(msg.getRtMsg());
        } else {
            ToastUtils.showText(msg.getRtMsg());
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalManageUtil.setLocal(newBase));
    }

}
