package com.example.gxwl.rederdemo.SAED.UI.Fragments.Client;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gxwl.rederdemo.AppConfig.SharedPreference;
import com.example.gxwl.rederdemo.R;
import com.example.gxwl.rederdemo.util.GlobalClient;
import com.example.gxwl.rederdemo.util.ToastUtils;
import com.gg.reader.api.protocol.gx.MsgBaseGetAutoDormancy;
import com.gg.reader.api.protocol.gx.MsgBaseSetPower;

import java.util.Hashtable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.gxwl.rederdemo.AppConfig.SharedPreference.getIp;
import static com.example.gxwl.rederdemo.AppConfig.SharedPreference.getPort;
import static com.example.gxwl.rederdemo.AppConfig.SharedPreference.saveIp;
import static com.example.gxwl.rederdemo.AppConfig.SharedPreference.savePort;

public class SettingsFragment extends Fragment {

    @BindView(R.id.ip)
    EditText ip;
    @BindView(R.id.port)
    EditText port;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.ant_query)
    Button query;
    @BindView(R.id.editTextNumber2)
    EditText count;
    @BindView(R.id.editTextNumber)
    EditText delay;
    View view;

    private final Hashtable<Integer, Integer> mAntMap = new Hashtable<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);

        initIpEditText();

        initIpAnPort();

        listeners();

        return view;
    }

    void listeners() {
        save.setOnClickListener(saveListener);

        query.setOnClickListener(queryListener);
    }


    private final View.OnClickListener saveListener = v -> {
        saveAnt_power();
        saveInFile();

    };

    private void saveAnt_power() {
        //نسخ لصق من واجهة ال RfidConfigActivity
        MsgBaseSetPower msg = new MsgBaseSetPower();
        int powerIndex = spinner.getSelectedItemPosition();
        mAntMap.put(1, powerIndex);
        msg.setDicPower(mAntMap);
        GlobalClient.getClient().sendSynMsg(msg);
        ToastUtils.showText(msg.getRtMsg());
    }

    private final View.OnClickListener queryListener = v -> {
        //نسخ لصق من واجهة ال RfidConfigActivity
        MsgBaseGetAutoDormancy msg = new MsgBaseGetAutoDormancy();
        GlobalClient.getClient().sendSynMsg(msg);
        if (msg.getRtCode() == 0)
            ToastUtils.showText("The query is successful");
        else
            ToastUtils.showText(msg.getRtMsg());

    };

    //saed :
    void initIpEditText() {
        InputFilter[] filters = new InputFilter[1];
        filters[0] = (source, start, end, dest, dstart, dend) -> {
            if (end > start) {
                String destTxt = dest.toString();
                String resultingTxt = destTxt.substring(0, dstart)
                        + source.subSequence(start, end)
                        + destTxt.substring(dend);
                if (!resultingTxt
                        .matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                    return "";
                } else {
                    String[] splits = resultingTxt.split("\\.");
                    for (String split : splits) {
                        if (Integer.parseInt(split) > 255) {
                            return "";
                        }
                    }
                }
            }
            return null;
        };

        ip.setFilters(filters);
    }

    void initIpAnPort() {

        int mPort = getPort();
        String mIp = getIp();

        int count = SharedPreference.getCount();
        int delay = SharedPreference.getDelay();

        if (!mIp.isEmpty())
            this.ip.setText(mIp);

        if (mPort != 0)
            this.port.setText(String.valueOf(mPort));

        if (count > 10)
            this.count.setText(String.valueOf(count));

        if (delay > 10)
            this.delay.setText(String.valueOf(delay));


    }

    void saveInFile() {
        String mIp = ip.getText().toString();
        int mPort = Integer.parseInt(port.getText().toString());
        int c = Integer.parseInt(count.getText().toString());
        int d = Integer.parseInt(delay.getText().toString());


        if (mIp.isEmpty()) {
            ip.setError("required");
            return;
        }
        if (mPort == 0) {
            port.setError("required");
            return;
        }

        saveIp(mIp);
        savePort(mPort);

        SharedPreference.saveCount(c);
        SharedPreference.saveDelay(d);

        Toast.makeText(requireActivity(), getResources().getString(R.string.done), Toast.LENGTH_SHORT).show();

        requireActivity().onBackPressed();
    }
}