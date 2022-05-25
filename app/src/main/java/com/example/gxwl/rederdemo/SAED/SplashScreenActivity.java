package com.example.gxwl.rederdemo.SAED;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;

import com.example.gxwl.rederdemo.AppConfig.SharedPreference;
import com.example.gxwl.rederdemo.R;

public class SplashScreenActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        SharedPreference.getInstance(this);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        new Handler().postDelayed((Runnable) this::startAuth, 2500);
    }

    private void startAuth() {
        startActivity(new Intent(this, AuthActivity.class));
        this.finish();
    }
}