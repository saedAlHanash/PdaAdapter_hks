package com.example.gxwl.rederdemo.util;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import com.gg.reader.api.utils.HksPower;


public class initApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LocalManageUtil.setApplicationLanguage(this);
        SharedPreferencesUtil.getInstance(this, "rfid");

        AppStateTracker.track(this, new AppStateTracker.AppStateChangeListener() {
            public void appTurnIntoBackGround() {
                Log.e("appTurnIntoForeground", "下电");
                HksPower.uhf_power(0);
                HksPower.uhf_1io(0);
            }

            public void appTurnIntoForeground() {
                Log.e("appTurnIntoForeground", "上电");
                HksPower.uhf_power(1);
                HksPower.uhf_1io(1);
            }
        });
    }

//    @Override
//    protected void attachBaseContext(Context base) {
//        //保存系统选择语言
//        LocalManageUtil.saveSystemCurrentLanguage(base);
//        super.attachBaseContext(LocalManageUtil.setLocal(base));
//    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        //保存系统选择语言
//        LocalManageUtil.onConfigurationChanged(getApplicationContext());
//    }

}
