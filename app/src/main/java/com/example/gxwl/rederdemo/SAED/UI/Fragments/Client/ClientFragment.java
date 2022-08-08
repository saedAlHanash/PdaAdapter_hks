package com.example.gxwl.rederdemo.SAED.UI.Fragments.Client;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gxwl.rederdemo.AppConfig.FC;
import com.example.gxwl.rederdemo.AppConfig.FN;
import com.example.gxwl.rederdemo.AppConfig.SharedPreference;
import com.example.gxwl.rederdemo.EntryActivity;
import com.example.gxwl.rederdemo.Helpers.View.FTH;
import com.example.gxwl.rederdemo.R;
import com.example.gxwl.rederdemo.ReadOrWriteActivity;
import com.example.gxwl.rederdemo.util.GlobalClient;
import com.example.gxwl.rederdemo.util.ToastUtils;
import com.gg.reader.api.dal.HandlerDebugLog;
import com.gg.reader.api.utils.HksPower;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClientFragment extends Fragment {


    @BindView(R.id.smart_scan)
    CardView smartScan;
    @BindView(R.id.inventory)
    CardView inventory;
    @BindView(R.id.scan)
    CardView scan;
    @BindView(R.id.settings)
    CardView settings;

    boolean isClient = false;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_client, container, false);
        ButterKnife.bind(this, view);

        listeners();

        initConnected();

        return view;
    }

    void listeners() {
        // المسح الذكي
        smartScan.setOnClickListener(smartScanListener);

        // جرد المخزن
        inventory.setOnClickListener(inventoryListener);

        // مسح منتج
        scan.setOnClickListener(scanListener);

        //اعدادات
        settings.setOnClickListener(settingsListener);
    }

    // المسح الذكي
    private final View.OnClickListener smartScanListener = v -> {

    };
    // جرد المخزن
    private final View.OnClickListener inventoryListener = v -> {
        startInventory();
    };
    // مسح منتج
    private final View.OnClickListener scanListener = v -> {
        startReadOrWriteActivity();
    };
    //اعدادات
    private final View.OnClickListener settingsListener = v -> {
        startSetting();
    };


    //初始化连接
    public void initConnected() {

        if (GlobalClient.getClient().
                openCusAndroidSerial("/dev/ttysWK0:115200", 64, 100)) {
            this.isClient = true;

            GlobalClient.getClient().debugLog = new HandlerDebugLog() {

                public void receiveDebugLog(String param1String) {
                    Log.e("receive", "snary"+param1String);
                }

                public void sendDebugLog(String param1String) {
                    Log.e("send","snary"+ param1String);
                }
            };
        } else {
            this.isClient = false;
        }

//        String hks = "/dev/ttysWK0:115200";
//        HksPower.uhf_power(1);
//
//        if (GlobalClient.getClient().openCusAndroidSerial(hks, 64, 0)) {
//            isClient = true;
//            ToastUtils.showText(getResources().getString(R.string.connect_rfid_success));
//        } else {
//            isClient = false;
//            ToastUtils.showText(getResources().getString(R.string.connect_rfid_fail));
//        }
    }

    void startReadOrWriteActivity() {

//        if (isClient) {

            Locale locale = new Locale(SharedPreference.getLanguage());
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getResources().updateConfiguration(config,
                    getResources().getDisplayMetrics());

            Intent intent = new Intent(requireActivity(), ReadOrWriteActivity.class);
            intent.putExtra("isClient", isClient);
            startActivity(intent);

//        } else {
//            ToastUtils.showText(getResources().getString(R.string.ununited));
//        }
    }

    void startSetting() {
        FTH.addFragmentUpFragment(FC.CLIENT_C, requireActivity(), new SettingsFragment(), FN.SETTING_FN);
    }

    void startInventory() {
        FTH.addFragmentUpFragment(FC.CLIENT_C, requireActivity(), new InventoryFragment(), FN.SETTING_FN);
    }
}