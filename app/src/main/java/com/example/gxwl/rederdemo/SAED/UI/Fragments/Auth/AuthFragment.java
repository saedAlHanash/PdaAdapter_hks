package com.example.gxwl.rederdemo.SAED.UI.Fragments.Auth;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.gxwl.rederdemo.AppConfig.FC;
import com.example.gxwl.rederdemo.AppConfig.FN;
import com.example.gxwl.rederdemo.AppConfig.SharedPreference;
import com.example.gxwl.rederdemo.Helpers.View.FTH;
import com.example.gxwl.rederdemo.R;
import com.example.gxwl.rederdemo.SAED.UI.Activities.AuthActivity;
import com.example.gxwl.rederdemo.SAED.UI.Activities.ClientActivity;

import org.w3c.dom.Text;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AuthFragment extends Fragment {

    @BindView(R.id.login)
    Button login;
    @BindView(R.id.guest)
    Button guest;
    @BindView(R.id.language)
    Spinner languageSpinner;
    @BindView(R.id.textView5)
    TextView languageIcon;

    int mSpinnerCheck = 0;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_auth, container, false);
        ButterKnife.bind(this, view);


        listeners();


        return view;
    }


    void initLanguage() {

        if (SharedPreference.getLanguage().equals("en"))
            setLanguage("ar");
        else
            setLanguage("en");
    }

    void listeners() {
        login.setOnClickListener(adminListener);
        guest.setOnClickListener(guestListener);

        languageSpinner.setOnItemSelectedListener(languageListener);
        languageIcon.setOnClickListener(view1 -> {
            languageSpinner.performClick();
        });
    }

    private final View.OnClickListener adminListener = view -> {
        startLoginFragment();
    };

    private final View.OnClickListener guestListener = view -> {
        startClientActivity();
    };


    final AdapterView.OnItemSelectedListener languageListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            switch (i) {

                case 0:
                    if (view == null)
                        break;
                    ((TextView) view).setTextColor(getResources().getColor(R.color.gray));
                    break;

                case 1: {
                    setLanguage("ar");
                    break;
                }

                case 2: {
                    setLanguage("en");
                    break;
                }
            }


        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    void startClientActivity() {
        Intent intent = new Intent(requireActivity(), ClientActivity.class);
        startActivity(intent);
    }

    void startLoginFragment() {
        FTH.addToStakeFragment(FC.AUTH_C, requireActivity(), new LoginFragment(), FN.LOGIN_FN);
    }

    void setLanguage(String lang) {

        Locale locale = new Locale(lang);

        SharedPreference.saveLanguage(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        requireActivity().getResources().updateConfiguration(config,
                requireActivity().getResources().getDisplayMetrics());

        Intent refresh = new Intent(requireActivity(), AuthActivity.class);
        refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(refresh);
        requireActivity().finish();
    }

    void alert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(R.string.change_langue);
        // Add the buttons
        builder.setPositiveButton("English", (dialog, id) -> {
            String languageToLoad = "en"; // your language
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            requireActivity().getResources().updateConfiguration(config,
                    requireActivity().getResources().getDisplayMetrics());
            dialog.dismiss();

            Intent refresh = new Intent(requireActivity(), AuthActivity.class);
            startActivity(refresh);
            requireActivity().finish();
        });

        builder.setNegativeButton("العربية", (dialog, id) -> {
            // User cancelled the dialog

            String languageToLoad = "ar"; // your language
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            requireActivity().getResources().updateConfiguration(config,
                    requireActivity().getResources().getDisplayMetrics());
            dialog.dismiss();

            Intent refresh = new Intent(requireActivity(), AuthActivity.class);
            startActivity(refresh);
            requireActivity().finish();

        });

        builder.create().show();
    }
}