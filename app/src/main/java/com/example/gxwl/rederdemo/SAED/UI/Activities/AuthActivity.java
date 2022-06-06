package com.example.gxwl.rederdemo.SAED.UI.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.gxwl.rederdemo.AppConfig.FC;
import com.example.gxwl.rederdemo.Helpers.View.FTH;
import com.example.gxwl.rederdemo.R;
import com.example.gxwl.rederdemo.SAED.UI.Fragments.Auth.AuthFragment;

import butterknife.ButterKnife;

public class AuthActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);
        FTH.replaceFadFragment(FC.AUTH_C, this, new AuthFragment());
    }

}


//    void setLanguage(String lang) {
//        Resources resources = this.getResources();
//        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
//        Configuration config = resources.getConfiguration();
//        Locale locale = new Locale(lang.toLowerCase());
//        Locale.setDefault(locale);
//        config.setLocale(locale);
//        resources.updateConfiguration(config, displayMetrics);
//    }