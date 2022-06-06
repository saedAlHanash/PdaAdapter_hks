package com.example.gxwl.rederdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.gxwl.rederdemo.util.Frequency;
import com.example.gxwl.rederdemo.util.GlobalClient;
import com.example.gxwl.rederdemo.util.LocalManageUtil;
import com.example.gxwl.rederdemo.util.ToastUtils;
import com.gg.reader.api.protocol.gx.EnumG;
import com.gg.reader.api.protocol.gx.MsgBaseGetFreqRange;
import com.gg.reader.api.protocol.gx.MsgBaseStop;
import com.gg.reader.api.protocol.gx.MsgTestCarrierWave;
import com.gg.reader.api.protocol.gx.MsgTestPowerCalibration;
import com.gg.reader.api.protocol.gx.MsgTestPowerCalibrationGet;
import com.gg.reader.api.protocol.gx.MsgTestVSWRcheck;

import java.util.Hashtable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestActivity extends AppCompatActivity {

    @BindView(R.id.freq_point)
    Spinner freq_point;

    @BindView(R.id.standing_wave_pre)
    EditText standing_wave_pre;

    @BindView(R.id.standing_wave_suf)
    EditText standing_wave_suf;

    @BindView(R.id.childFrequency)
    Spinner childFrequency;

    @BindView(R.id.calibration_power)
    Spinner calibration_power;

    @BindView(R.id.calibrationParam)
    EditText calibrationParam;


    private Hashtable<Integer, Integer> mAntMap = new Hashtable<Integer, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        ButterKnife.bind(this);
        calibration_power.setSelection(30);
        MsgBaseGetFreqRange range = new MsgBaseGetFreqRange();
        GlobalClient.getClient().sendSynMsg(range);
        if (range.getRtCode() == 0) {
            ArrayAdapter<String> freAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, Frequency.indexGetFre(range.getFreqRangeIndex()));
//            freAdapter.setDropDownViewResource(R.layout.spinner_item);
            freq_point.setAdapter(freAdapter);

            ArrayAdapter<String> childAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, Frequency.indexGetChildFre(range.getFreqRangeIndex()));
//            childAdapter.setDropDownViewResource(R.layout.spinner_item);
            childFrequency.setAdapter(childAdapter);
        }
    }

    @OnClick(R.id.standing_wave_send)
    public void standing_wave_send() {
        MsgTestCarrierWave wave = new MsgTestCarrierWave();
        wave.setAntennaNum(EnumG.AntennaNo_1);
        wave.setFreqCursor(freq_point.getSelectedItemPosition());
        GlobalClient.getClient().sendSynMsg(wave);
        if (wave.getRtCode() == 0) {
            ToastUtils.showText(wave.getRtMsg());
        } else {
            ToastUtils.showText(wave.getRtMsg());
        }
    }

    @OnClick(R.id.standing_wave_stop)
    public void standing_wave_stop() {
        MsgBaseStop stop = new MsgBaseStop();
        GlobalClient.getClient().sendSynMsg(stop);
        if (stop.getRtCode() == 0) {
            ToastUtils.showText(stop.getRtMsg());
        } else {
            ToastUtils.showText(stop.getRtMsg());
        }
    }

    @OnClick(R.id.detection)
    public void detection() {
        MsgTestVSWRcheck msg = new MsgTestVSWRcheck();
        GlobalClient.getClient().sendSynMsg(msg);
        if (msg.getRtCode() == 0) {
            standing_wave_pre.setText(msg.getPreValue() + "");
            standing_wave_suf.setText(msg.getSufValue() + "");
        } else {
            ToastUtils.showText(msg.getRtMsg());
        }
    }

    @OnClick(R.id.calibration_end)
    public void calibration_end() {
        MsgTestPowerCalibration msg = new MsgTestPowerCalibration();
        msg.setChildFreqRange(childFrequency.getSelectedItemPosition());
        msg.setPower(calibration_power.getSelectedItemPosition());
        msg.setPowerParam(Integer.parseInt(calibrationParam.getText().toString()));
        msg.setOptionType(1);
        GlobalClient.getClient().sendSynMsg(msg);
        if (msg.getRtCode() == 0) {
            ToastUtils.showText(msg.getRtMsg());
        } else {
            ToastUtils.showText(msg.getRtMsg());
        }
    }

    @OnClick(R.id.calibration_action)
    public void calibration_action() {
        MsgTestPowerCalibration msg = new MsgTestPowerCalibration();
        msg.setChildFreqRange(childFrequency.getSelectedItemPosition());
        msg.setPower(calibration_power.getSelectedItemPosition());
        msg.setPowerParam(Integer.parseInt(calibrationParam.getText().toString()));
        msg.setOptionType(0);
        GlobalClient.getClient().sendSynMsg(msg);
        if (msg.getRtCode() == 0) {
            ToastUtils.showText(msg.getRtMsg());
        } else {
            ToastUtils.showText(msg.getRtMsg());
        }
    }

    @OnClick(R.id.calibration_query)
    public void calibration_query() {
        MsgTestPowerCalibrationGet msg = new MsgTestPowerCalibrationGet();
        msg.setChildFreqRange(childFrequency.getSelectedItemPosition());
        msg.setPower(calibration_power.getSelectedItemPosition());
        GlobalClient.getClient().sendSynMsg(msg);
        if (msg.getRtCode() == 0) {
            calibrationParam.setText(msg.getPowerParam() + "");
        } else {
            ToastUtils.showText(msg.getRtMsg());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        standing_wave_stop();
    }

   // @Override
   // protected void attachBaseContext(Context newBase) {
  //      super.attachBaseContext(LocalManageUtil.setLocal(newBase));
 //   }
}
