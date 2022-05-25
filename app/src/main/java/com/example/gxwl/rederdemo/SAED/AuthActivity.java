package com.example.gxwl.rederdemo.SAED;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gxwl.rederdemo.AppConfig.SharedPreference;
import com.example.gxwl.rederdemo.EntryActivity;
import com.example.gxwl.rederdemo.R;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.hoang8f.android.segmented.SegmentedGroup;

public class AuthActivity extends AppCompatActivity {

    @BindView(R.id.login)
    Button login;
    @BindView(R.id.guest)
    Button guest;
    @BindView(R.id.segmented2)
    SegmentedGroup segmentedGroup;

    @BindView(R.id.english)
    RadioButton english;
    @BindView(R.id.arabic)
    RadioButton arabic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);

        listeners();

        initLanguage();

    }

    void initLanguage() {

        segmentedGroup.setTintColor(getResources().getColor(R.color.main_color));

        if (SharedPreference.getLanguage().equals("en"))
            english.setChecked(true);
        else
            arabic.setChecked(true);

        segmentedGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.english) {
                if (SharedPreference.getLanguage().equals("en"))
                    return;
                SharedPreference.saveLanguage("en");
                setLanguage("en");
            } else {
                if (SharedPreference.getLanguage().equals("ar"))
                    return;
                SharedPreference.saveLanguage("ar");
                setLanguage("ar");
            }
        });

    }

    void listeners() {
        login.setOnClickListener(adminListener);
        guest.setOnClickListener(guestListener);



    }

    private final View.OnClickListener adminListener = view -> {
        startLoginActivity();
    };

    private final View.OnClickListener guestListener = view -> {
        startEntryActivity();
    };

    void startEntryActivity() {
        Intent intent = new Intent(this, EntryActivity.class);
        intent.putExtra("key", "user");
        startActivity(intent);
    }

    void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    void setLanguage(String lang) {

        Locale locale = new Locale(lang);

        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        Intent refresh = new Intent(this, AuthActivity.class);
        startActivity(refresh);
        finish();
    }

    void alert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.change_langue);
        // Add the buttons
        builder.setPositiveButton("English", (dialog, id) -> {
            String languageToLoad = "en"; // your language
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
            dialog.dismiss();

            Intent refresh = new Intent(this, AuthActivity.class);
            startActivity(refresh);
            finish();
        });

        builder.setNegativeButton("العربية", (dialog, id) -> {
            // User cancelled the dialog

            String languageToLoad = "ar"; // your language
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
            dialog.dismiss();

            Intent refresh = new Intent(this, AuthActivity.class);
            startActivity(refresh);
            finish();

        });

        builder.create().show();
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