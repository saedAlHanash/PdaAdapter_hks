package com.example.gxwl.rederdemo.SAED.UI.Fragments.Client;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.gxwl.rederdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SmartScanFragment extends Fragment implements View.OnClickListener {
    View view;

    @BindView(R.id.imageView8)
    ImageView imageView8;

    @BindView(R.id.read)
    Button read;
    @BindView(R.id.stop)
    Button stop;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_smart_scan, container, false);
        ButterKnife.bind(this, view);

        new Handler(Looper.getMainLooper()).postDelayed(() -> imageView8.setImageLevel(1), 1000);
        new Handler(Looper.getMainLooper()).postDelayed(() -> imageView8.setImageLevel(2), 3000);
        new Handler(Looper.getMainLooper()).postDelayed(() -> imageView8.setImageLevel(3), 5000);
        new Handler(Looper.getMainLooper()).postDelayed(() -> imageView8.setImageLevel(4), 7000);

        read.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.read: {

                new Handler(Looper.getMainLooper()).postDelayed(() -> imageView8.setImageLevel(1), 1000);
                new Handler(Looper.getMainLooper()).postDelayed(() -> imageView8.setImageLevel(2), 3000);
                new Handler(Looper.getMainLooper()).postDelayed(() -> imageView8.setImageLevel(3), 5000);
                new Handler(Looper.getMainLooper()).postDelayed(() -> imageView8.setImageLevel(1), 6000);
                new Handler(Looper.getMainLooper()).postDelayed(() -> imageView8.setImageLevel(4), 6500);
                new Handler(Looper.getMainLooper()).postDelayed(() -> imageView8.setImageLevel(2), 7000);
                new Handler(Looper.getMainLooper()).postDelayed(() -> imageView8.setImageLevel(4), 8000);

                break;
            }
            case R.id.stop: {
                break;
            }
        }
    }

}