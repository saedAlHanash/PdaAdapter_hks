package com.example.gxwl.rederdemo.SAED;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gxwl.rederdemo.EntryActivity;
import com.example.gxwl.rederdemo.R;

import java.util.Locale;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AuthActivity extends AppCompatActivity {

    @BindView(R.id.login)
    Button login;
    @BindView(R.id.guest)
    Button guest;
    @BindView(R.id.language)
    TextView language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);

        listeners();

    }

    void listeners() {
        login.setOnClickListener(adminListener);
        guest.setOnClickListener(guestListener);

        language.setOnClickListener(languageListener);
    }


    private final View.OnClickListener adminListener = view -> {
        startLoginActivity();
    };

    private final View.OnClickListener guestListener = view -> {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            guest.setBackgroundTintList(getResources().getColorStateList(R.color.main_color));
        else
            guest.setBackgroundColor(getResources().getColor(R.color.main_color));
        startEntryActivity();
    };

    private final View.OnClickListener languageListener = view -> {
        alert();
    };

    void startEntryActivity() {
        Intent intent = new Intent(this, EntryActivity.class);
        intent.putExtra("key", "user");
        startActivity(intent);
        this.finish();
    }

    void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    void setLanguage(String lang) {
        Resources resources = this.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        Locale locale = new Locale(lang.toLowerCase());
        Locale.setDefault(locale);
        config.setLocale(locale);
        resources.updateConfiguration(config, displayMetrics);
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