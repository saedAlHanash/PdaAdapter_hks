package com.example.gxwl.rederdemo.SAED.UI.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.example.gxwl.rederdemo.AppConfig.FC;
import com.example.gxwl.rederdemo.Helpers.View.FTH;
import com.example.gxwl.rederdemo.R;
import com.example.gxwl.rederdemo.SAED.UI.Fragments.Client.ClientFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClientActivity extends AppCompatActivity {


    @BindView(R.id.client_container)
    FrameLayout clientContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        ButterKnife.bind(this);

        startClientFragment();

    }

    void startClientFragment() {
        FTH.replaceFadFragment(FC.CLIENT_C, this, new ClientFragment());
    }

}