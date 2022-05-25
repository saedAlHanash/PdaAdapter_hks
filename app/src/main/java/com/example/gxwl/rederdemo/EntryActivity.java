package com.example.gxwl.rederdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.example.gxwl.rederdemo.AppConfig.SharedPreference;
import com.example.gxwl.rederdemo.util.CheckCommunication;
import com.example.gxwl.rederdemo.util.GlobalClient;
import com.example.gxwl.rederdemo.util.LocalManageUtil;
import com.example.gxwl.rederdemo.util.SerialPortFinder;
import com.example.gxwl.rederdemo.util.ToastUtils;
import com.example.gxwl.rederdemo.util.UriUtils;
import com.gg.reader.api.protocol.gx.MsgAppGetBaseVersion;
import com.gg.reader.api.protocol.gx.MsgAppGetReaderInfo;
import com.gg.reader.api.protocol.gx.MsgAppReset;
import com.gg.reader.api.protocol.gx.MsgUpgradeBaseband;
import com.gg.reader.api.utils.HksPower;
import com.gg.reader.api.utils.ThreadPoolUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;

import static com.example.gxwl.rederdemo.AppConfig.SharedPreference.*;


public class EntryActivity extends AppCompatActivity {


    private boolean isClient = false;
    private Spinner rs232_android_port;
    private Spinner rs232_android_baud;

    @BindView(R.id.connect_param)
    TextView connect_param;
    @BindView(R.id.ip)
    EditText ip;
    @BindView(R.id.port)
    EditText port;

    NumberProgressBar bar;
    EditText filePath;
    Button select_file;
    Button upgrade;
    long rate = 0;
    File file;
    AlertDialog dialog;
    long complete = 0;//完成度
    int len = -1;//读取长度
    int count = 1;//PacketNumber自增
    boolean isUpGrade = false;
    private Handler upDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                runOnUiThread(() -> bar.setProgress((int) rate));
            }
            return true;
        }
    });
    AlertDialog infoDialog = null;

//    static {
//        System.loadLibrary("native-lib");
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_layout);
        ButterKnife.bind(this);


        //saed :
        hideItemIfFromUser(getIntent());
        initIpEditText();
        initIpAnPort();


        Integer selectLanguage = LocalManageUtil.getSelectLanguage(this);
        if (selectLanguage != null) {
            LocalManageUtil.saveSelectLanguage(this, selectLanguage);
        } else {
            LocalManageUtil.saveSelectLanguage(this, 0);
        }
//        cusConnect();
        initConnected();
    }

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

    //saed :
    void hideItemIfFromUser(Intent intent) {

        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null)
                if (!bundle.getString("key").equals("admin")) {
                    findViewById(R.id.deviceInfo).setVisibility(View.GONE);
                    findViewById(R.id.upgradeBaseband).setVisibility(View.GONE);
                    findViewById(R.id.rfidConfig).setVisibility(View.GONE);
                }
        }
    }

    void initIpAnPort() {

        int mPort = getPort();
        String mIp = getIp();

        if (!mIp.isEmpty())
            this.ip.setText(mIp);

        if (mPort != 0)
            this.port.setText(String.valueOf(mPort));
    }

    //设备连接  /dev/ttyS1:115200
//    @OnClick(R.id.cusConnect)
    public void cusConnect() {
        final View writeView = getLayoutInflater().inflate(R.layout.connect, null, false);
        initAndroidPorts(writeView);
        AlertDialog.Builder writeDialog = new AlertDialog.Builder(EntryActivity.this);
        writeDialog.setView(writeView);
        writeDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!isClient) {
                    if (rs232_android_port.getSelectedItem() != null) {
                        String param = "/dev/" + rs232_android_port.getSelectedItem().toString().trim() + ":" + rs232_android_baud.getSelectedItem().toString().substring(0, rs232_android_baud.getSelectedItem().toString().indexOf("b")).trim();
                        System.out.println(param);
                        if (GlobalClient.getClient().openAndroidSerial(param, 1000)) {
                            isClient = true;
                            setConnect_param("/dev/" + rs232_android_port.getSelectedItem() + ":" + rs232_android_baud.getSelectedItem());
                            ToastUtils.showText(getResources().getString(R.string.connect_success));
                        } else {
                            isClient = false;
                            setConnect_param("/dev/" + rs232_android_port.getSelectedItem() + ":" + rs232_android_baud.getSelectedItem());
                            ToastUtils.showText(getResources().getString(R.string.connect_fail));
                        }
                    } else {
                        ToastUtils.showText(getResources().getString(R.string.notSerial));
                    }
                } else {
                    ToastUtils.showText(getResources().getString(R.string.connect_again));
                }
            }
        });

        AlertDialog dialog = writeDialog.create();
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

    }

    //断开连接
//    @OnClick(R.id.disConnect)
    public void disConnect() {
        if (isClient) {
            GlobalClient.getClient().close();
            isClient = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connect_param.setText(getResources().getString(R.string.disconnected));
                    connect_param.setTextColor(getResources().getColor(R.color.IndianRed));
                }
            });
        } else {
            ToastUtils.showText(getResources().getString(R.string.ununited));
        }
    }

    //初始化androidRs232连接参数
    public void initAndroidPorts(View view) {
        SerialPortFinder finder = new SerialPortFinder();
        List<String> devs = new ArrayList<>();
        for (String dev : finder.getAllDevices()) {
            devs.add(dev.substring(0, dev.indexOf("(")));
        }
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(view.getContext(), R.layout.spinner_item, devs);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        rs232_android_port = view.findViewById(R.id.rs232_android_port);
        rs232_android_port.setAdapter(adapter);

        rs232_android_baud = view.findViewById(R.id.rs232_android_baud);
        rs232_android_baud.setSelection(2);

    }

    //初始化连接
    public void initConnected() {
        String hks = "/dev/ttysWK0:115200";
        HksPower.uhf_power(1);
        if (GlobalClient.getClient().openCusAndroidSerial(hks, 64, 100)) {
//        if (GlobalClient.getClient().openAndroidSerial(hks, 0)) {
            isClient = true;
            ToastUtils.showText("连接成功");
        } else {
            isClient = false;
            ToastUtils.showText("连接失败");
        }
    }

    @OnClick(R.id.readOrWrite)
    public void readOrWrite() {
        if (isClient) {
        String mIp = ip.getText().toString();
        int mPort = Integer.parseInt(port.getText().toString());

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

        Intent intent = new Intent(this, ReadOrWriteActivity.class);
        intent.putExtra("isClient", isClient);
        intent.putExtra("ip", mIp);
        intent.putExtra("port", mPort);
        startActivity(intent);

        } else {
            ToastUtils.showText(getResources().getString(R.string.ununited));
        }
    }

    @OnClick(R.id.rfidConfig)
    public void rfidConfig() {
        if (isClient) {
            Intent intent = new Intent(this, RfidConfigActivity.class);
            startActivity(intent);
        } else {
            ToastUtils.showText(getResources().getString(R.string.ununited));
        }
    }

    @OnClick(R.id.version)
    public void test() {
        if (isClient) {
            Intent intent = new Intent(this, TestActivity.class);
            startActivity(intent);
        } else {
            ToastUtils.showText(getResources().getString(R.string.ununited));
        }
    }

    //查看设备信息
    @OnClick(R.id.deviceInfo)
    public void searchDevice() {
        if (isClient) {
            View infoView = getLayoutInflater().inflate(R.layout.device_info, null, false);
            TextView deviceSerial = infoView.findViewById(R.id.deviceSerial);
            TextView appVersion = infoView.findViewById(R.id.appVersion);
            TextView baseVersion = infoView.findViewById(R.id.baseVersion);
            TextView appCompile = infoView.findViewById(R.id.appCompile);
            TextView baseCompile = infoView.findViewById(R.id.baseCompile);
            TextView systemVersion = infoView.findViewById(R.id.systemVersion);
            TextView powerOnTime = infoView.findViewById(R.id.powerOnTime);
            AlertDialog.Builder upgradeDialog = new AlertDialog.Builder(EntryActivity.this);
            upgradeDialog.setView(infoView);
            infoDialog = upgradeDialog.create();
            infoDialog.show();
//            infoDialog.getWindow().setLayout(600, 270);

            if (CheckCommunication.check()) {
                MsgAppGetReaderInfo msg = new MsgAppGetReaderInfo();
                GlobalClient.getClient().sendSynMsg(msg);
                if (msg.getRtCode() == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            deviceSerial.setText(msg.getReaderSerialNumber());
                            appVersion.setText(msg.getAppVersions());

                            appCompile.setText(msg.getAppCompileTime());
                            baseCompile.setText(msg.getBaseCompileTime());
                            systemVersion.setText(msg.getSystemVersions());
                            powerOnTime.setText(msg.getFormatPowerOnTime());
                        }
                    });
                }
                MsgAppGetBaseVersion baseVersion1 = new MsgAppGetBaseVersion();
                GlobalClient.getClient().sendSynMsg(baseVersion1);
                if (baseVersion1.getRtCode() == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            baseVersion.setText(baseVersion1.getBaseVersions());
                        }
                    });
                }
            }


        } else {
            ToastUtils.showText(getResources().getString(R.string.ununited));
        }
    }


    @OnClick(R.id.upgradeBaseband)
    public void upgradeBaseband() {
        if (isClient) {
            View upgradeView = getLayoutInflater().inflate(R.layout.upgrade_layout, null, false);
            bar = upgradeView.findViewById(R.id.upgradeBar);
            filePath = upgradeView.findViewById(R.id.file);
            select_file = upgradeView.findViewById(R.id.select_file);
            upgrade = upgradeView.findViewById(R.id.upgrade);

            AlertDialog.Builder upgradeDialog = new AlertDialog.Builder(EntryActivity.this);
            upgradeDialog.setCancelable(false);
            upgradeDialog.setView(upgradeView);
            upgradeDialog.setPositiveButton("Cancel", null);
            dialog = upgradeDialog.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isUpGrade) {
                        ToastUtils.showText(getResources().getString(R.string.upgrade_hint));
                    } else {
                        dialog.dismiss();
                    }

                }
            });

            //选择文件
            select_file.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFile();
                }
            });
            //升级
            upgrade.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (file == null) {
                        ToastUtils.showText(getResources().getString(R.string.select_upgrade));
                    } else {
                        List<PermissionItem> permissonItems = new ArrayList<PermissionItem>();
                        permissonItems.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "存储", R.drawable.permission_ic_storage));
                        HiPermission.create(EntryActivity.this)
                                .title(getResources().getString(R.string.upgrade_permission))
                                .permissions(permissonItems)
                                .checkMutiPermission(new PermissionCallback() {
                                    @Override
                                    public void onClose() {
                                        Log.e("onClose", "onClose");
                                    }

                                    @Override
                                    public void onFinish() {
                                        Log.e("onFinish", "onFinish");
                                        MsgUpgradeBaseband msg = new MsgUpgradeBaseband();
                                        msg.setPacketNumber(0l);
                                        ThreadPoolUtils.run(new Runnable() {
                                            @Override
                                            public void run() {
                                                GlobalClient.getClient().sendSynMsg(msg);
                                                if (msg.getRtCode() == 0) {
                                                    try {
                                                        FileInputStream fileInputStream = new FileInputStream(file);
                                                        readInputStream(fileInputStream, file, msg);
                                                    } catch (FileNotFoundException e) {
                                                        ToastUtils.handlerText(getResources().getString(R.string.read_file_fail));
                                                    }
                                                } else {
                                                    GlobalClient.getClient().sendSynMsg(msg);
                                                    if (msg.getRtCode() == 0) {
                                                        try {
                                                            FileInputStream fileInputStream = new FileInputStream(file);
                                                            readInputStream(fileInputStream, file, msg);
                                                        } catch (FileNotFoundException e) {
                                                            ToastUtils.handlerText(getResources().getString(R.string.read_file_fail));
                                                        }
                                                    } else {
                                                        ToastUtils.handlerText(getResources().getString(R.string.try_again));
                                                    }
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onDeny(String permission, int position) {
                                        Log.e("onDeny", "onDeny");
                                    }

                                    @Override
                                    public void onGuarantee(String permission, int position) {
                                        Log.e("onGuarantee", "onGuarantee");
                                    }
                                });

                    }
                }
            });
        } else {
            ToastUtils.showText(getResources().getString(R.string.ununited));
        }
    }

    //选择基带文件
    public void openFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//无类型限制
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);

    }

    //读取基带升级文件
    public void readInputStream(FileInputStream fileInputStream, File file, MsgUpgradeBaseband msg) {
        byte[] buffer = new byte[256];
        long totalCount = file.length();
        complete = 0;//完成度
        len = -1;
        count = 1;//PacketNumber自增
        if (fileInputStream != null) {
            isUpGrade = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while ((len = fileInputStream.read(buffer)) != -1) {
//                            Long.parseLong("00000000" + count, 16)
                            msg.setPacketNumber(Long.valueOf(count));
                            msg.setPacketContent(buffer);
                            GlobalClient.getClient().sendSynMsg(msg);
                            if (msg.getRtCode() == 0) {
                                count++;
                            } else {
                                return;
                            }
                            complete += len;
                            rate = (int) ((float) complete / (float) totalCount * 100);
                            upDataHandler.sendEmptyMessage(1);
                        }
                        if (len == -1) {
                            msg.setPacketNumber(Long.parseLong("FFFFFFFF", 16));
                            msg.setPacketContent(null);
                            GlobalClient.getClient().sendSynMsg(msg);
                            ToastUtils.showText(getResources().getString(R.string.upgrade_complete));
                            GlobalClient.getClient().sendUnsynMsg(new MsgAppReset());
                            dialog.dismiss();
                        }
                        fileInputStream.close();
                        isUpGrade = false;
                    } catch (Exception e) {

                    }
                }
            }).start();
        }
    }

    //设置连接状态
    public void setConnect_param(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isClient) {
                    connect_param.setText(msg + " Success");
                    connect_param.setTextColor(getResources().getColor(R.color.DodgerBlue));
                } else {
                    connect_param.setText(msg + " Fail");
                    connect_param.setTextColor(getResources().getColor(R.color.IndianRed));
                }
            }
        });
    }

    @OnClick(R.id.english)
    public void englishSet() {
        Intent intent = new Intent(this, EntryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        LocalManageUtil.saveSelectLanguage(this, 2);
        getBaseContext().startActivity(intent);
    }

    @OnClick(R.id.chinese)
    public void chineseSet() {
        Intent intent = new Intent(this, EntryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        LocalManageUtil.saveSelectLanguage(this, 1);
        getBaseContext().startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isClient) {
            Log.e(this.getClass().getName(), "onDestroy");
            GlobalClient.getClient().close();
        }
        HksPower.uhf_power(0);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        final Uri uri = data.getData();
                        if (uri.getPath().lastIndexOf(".bin") != -1) {
                            String path = UriUtils.getPath(this, uri);
                            filePath.setText(path);
                            file = new File(path);
                        } else {
                            ToastUtils.showText(getResources().getString(R.string.correct_file));
                        }
                    }
                }
                break;
        }


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalManageUtil.setLocal(newBase));
    }


//    public native void uhf_power(int status);
//
//    public native void uhf_1io(int val);

}
