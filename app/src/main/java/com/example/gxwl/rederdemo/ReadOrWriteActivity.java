package com.example.gxwl.rederdemo;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.gxwl.rederdemo.AppConfig.SharedPreference;
import com.example.gxwl.rederdemo.SAED.Network.SocketClient;
import com.example.gxwl.rederdemo.adapter.RecycleViewAdapter;
import com.example.gxwl.rederdemo.entity.TagInfo;
import com.example.gxwl.rederdemo.util.GlobalClient;
import com.example.gxwl.rederdemo.util.ToastUtils;
import com.example.gxwl.rederdemo.util.UtilSound;
import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.dal.HandlerGpiOver;
import com.gg.reader.api.dal.HandlerGpiStart;
import com.gg.reader.api.protocol.gx.EnumG;
import com.gg.reader.api.protocol.gx.LogAppGpiOver;
import com.gg.reader.api.protocol.gx.LogAppGpiStart;
import com.gg.reader.api.protocol.gx.LogBase6bInfo;
import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
import com.gg.reader.api.protocol.gx.LogBaseGbInfo;
import com.gg.reader.api.protocol.gx.MsgBaseInventoryEpc;
import com.gg.reader.api.protocol.gx.MsgBaseStop;
import com.gg.reader.api.protocol.gx.Param6bReadUserdata;
import com.gg.reader.api.protocol.gx.ParamEpcReadReserved;
import com.gg.reader.api.protocol.gx.ParamEpcReadTid;
import com.gg.reader.api.protocol.gx.ParamEpcReadUserdata;
import com.gg.reader.api.utils.ThreadPoolUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class ReadOrWriteActivity extends AppCompatActivity {
    // region 组件变量

    @BindView(R.id.read)
    Button read;
    @BindView(R.id.stop)
    Button stop;
    @BindView(R.id.clean)
    Button clean;
    @BindView(R.id.tabHead)
    LinearLayout tabHead;

    //saed:
    @BindView(R.id.not_connect)
    TextView notConnectTv;
    @BindView(R.id.spinner_tybe_scan)
    Spinner spinnerTypeScan;
    @BindView(R.id.scan_type_tv)
    TextView typeScanTv;

    EditText w_epc;
    EditText w_tid;
    EditText w_pas;
    EditText w_len;
    EditText w_value;
    //6c
    EditText w_user_epc;
    EditText w_user_tid;
    EditText w_user_pas;
    EditText w_user_len;
    EditText w_user_value;
    //6b
    EditText w_6b_user_tid;
    EditText w_6b_user_start;
    EditText w_6b_user_value;
    //自定义写
    Spinner cus_mode;
    EditText cus_start;
    EditText cus_pas;
    EditText cus_epc;
    EditText cus_tid;
    EditText cus_user;
    //标签信息
    TextView info_index;
    TextView info_type;
    TextView info_epc;
    TextView info_tid;
    TextView info_userData;
    TextView info_Reserved;

    //gb user write
    EditText w_gb_user_epc;
    EditText w_gb_user_pas;
    EditText w_gb_user_tid;
    EditText w_gb_user_start;
    Spinner write_gb_user_child;
    EditText w_gb_user_value;
    //gb epc write
    EditText w_gb_epc;
    EditText w_gb_pas;
    EditText w_gb_tid;
    EditText w_gb_len;
    EditText w_gb_value;

    //自定义读
    Spinner cus_read_mode;
    EditText cus_read_start;
    EditText cus_read_epc;
    EditText cus_read_tid;
    EditText cus_read_user;
    Spinner cus_read_tid_mode;
    EditText cus_read_tid_len;
    CheckBox read_tid_true;
    EditText cus_read_match_content;

    EditText cus_read_user_start;
    EditText cus_read_user_len;
    CheckBox read_user_true;

    EditText cus_read_reserve_start;
    EditText cus_read_reserve_len;
    CheckBox read_reserve_true;

    CheckBox read_other_pas;
    EditText cus_read_pas;

    // endregion

    private final GClient client = GlobalClient.getClient();
    private boolean isClient = false;
    private final Map<String, TagInfo> tagInfoMap = new LinkedHashMap<String, TagInfo>();//去重数据源
    private final List<TagInfo> tagInfoList = new ArrayList<TagInfo>();//适配器所需数据源
    private Long index = 1L;//索引
    private RecycleViewAdapter adapter;
    private ParamEpcReadTid tidParam = null;
    private ParamEpcReadUserdata userParam = null;
    private ParamEpcReadReserved reserveParam = null;
    private Param6bReadUserdata user6bParam = null;
    private final boolean[] isChecked = new boolean[]{false, false, false};//标识读0-epc与1-user
    private Handler mHandler = new Handler();
    private Runnable r = null;
    private final int time = 0;
    private final boolean isSound = true;
    private boolean isReader = false;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

    //saed :
    public SocketClient socketClient = new SocketClient();
    /**
     * to checking if can reConnect with socket <p>
     * will be false when onDestroy Activity
     */
    public boolean tryConnect = true;
    /**
     * socket ip address
     */
    public String ip;
    /**
     * socket port
     */
    public int port;

    int typeScan;
    private static final int SINGLE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);

        isClient = getIntent().getBooleanExtra("isClient", false);

        ip = SharedPreference.getIp().replaceAll("\\s+", "");
        port = SharedPreference.getPort();

        initSocket(ip, port);

        listeners();

        if (isClient) {
            subHandler(GlobalClient.getClient());
        }
        initRecycleView();
        UtilSound.initSoundPool(this);
    }

    //saed :
    void listeners() {
        typeScanTv.setOnClickListener(view -> {
            spinnerTypeScan.performClick();
        });

        spinnerTypeScan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                typeScan = i;

                if (i == 0)
                    typeScanTv.setText(getResources().getString(R.string.single));

                else
                    typeScanTv.setText(getResources().getString(R.string.loop));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void computedSpeed() {
        Runnable runnable = new Runnable() {
            public void run() {
                synchronized (tagInfoList) {

                    tagInfoList.clear();
                    tagInfoList.addAll(tagInfoMap.values());
                    adapter.notifyData(tagInfoList);

                    mHandler.postDelayed(this, 1000L);
                }
            }
        };
        this.r = runnable;
        this.mHandler.postDelayed(runnable, 1000L);
    }

    /**
     * start connection with socket
     *
     * @param mIp   socket ip address
     * @param mPort socket port
     */
    public void initSocket(String mIp, int mPort) {
        new Thread(() -> { //لانه لا يمكن الاتصال من ال UI thread
            while (tryConnect) { // من أجل المحاولة والمحاولة حتى تمام عملية الاتصال

                //اذا الاتصال تم
                if (socketClient.connect(mIp, mPort)) {
                    //اخفاء noConnect textView
                    runOnUiThread(() -> notConnectTv.setVisibility(View.GONE));
                    break;
                } else // اذا لم يتصل
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }

        }).start();

        //call back active when connect stat change
        socketClient.setOnChangeConnectStatListener(connect -> {
            runOnUiThread(() -> {
                if (connect)
                    notConnectTv.setVisibility(View.GONE);
                else
                    notConnectTv.setVisibility(View.VISIBLE);
            });
        });
    }

    //初始化RecycleView
    public void initRecycleView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView rv = (RecyclerView) findViewById(R.id.recycle);
        rv.setLayoutManager(layoutManager);
        rv.addItemDecoration(new DividerItemDecoration(this, 1));
        adapter = new RecycleViewAdapter(tagInfoList, this);
        rv.setAdapter(adapter);

    }

    //订阅
    public void subHandler(GClient client) {
        client.onTagEpcLog = (readerName, info) -> {
            if (null != info && 0 == info.getResult()) {
                ReadOrWriteActivity.this.runOnUiThread(() -> {
                    synchronized (tagInfoList) {
                        pooled6cData(info);
                    }
                });
            }
        };

        client.onTagEpcOver = (readerName, info) -> {
            handlerStop.sendEmptyMessage(new Message().what = 1);
        };

//        client.onTag6bLog = (readerName, info) -> {
//            if (null != info && info.getResult() == 0) {
//                runOnUiThread(() -> {
//                    Map<String, TagInfo> infoMap = pooled6bData(info);
//                    tagInfoList.clear();
//                    tagInfoList.addAll(infoMap.values());
//                });
//            }
//        };
//        client.onTag6bOver = (readerName, info) -> {
//            handlerStop.sendEmptyMessage(new Message().what = 1);
//        };
//
//        client.onTagGbLog = (readerName, info) -> {
//            if (null != info && info.getResult() == 0) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Map<String, TagInfo> infoMap = pooledGbData(info);
//                        tagInfoList.clear();
//                        tagInfoList.addAll(infoMap.values());
//                    }
//                });
//            }
//
//        };
//        client.onTagGbOver = (readerName, info) -> {
//            handlerStop.sendEmptyMessage(new Message().what = 1);
//        };
//
//
//        client.onGpiOver = new HandlerGpiOver() {
//            @Override
//            public void log(String s, LogAppGpiOver logAppGpiOver) {
//                System.out.println(logAppGpiOver);
//            }
//        };
//        client.onGpiStart = new HandlerGpiStart() {
//            @Override
//            public void log(String s, LogAppGpiStart logAppGpiStart) {
//                System.out.println(logAppGpiStart);
//            }
//        };
    }

    //读卡
    @OnClick(R.id.read)
    public void readCard() {
        if (isClient) {
            if (!isReader) {

                //   if (type.getCheckedRadioButtonId() == R.id.c) {
                MsgBaseInventoryEpc msg = new MsgBaseInventoryEpc();
                msg.setAntennaEnable(EnumG.AntennaNo_1);
                if (typeScan == SINGLE) {
                    msg.setInventoryMode(EnumG.InventoryMode_Single);
                } else {
                    msg.setInventoryMode(EnumG.InventoryMode_Inventory);
                }

                if (isChecked[0]) {
                    tidParam = new ParamEpcReadTid();
                    tidParam.setMode(EnumG.ParamTidMode_Auto);
                    tidParam.setLen(6);
                    msg.setReadTid(tidParam);
                }
                if (isChecked[1]) {
                    userParam = new ParamEpcReadUserdata();
                    userParam.setStart(0);
                    userParam.setLen(6);
                    msg.setReadUserdata(userParam);
                }
                if (isChecked[2]) {
                    reserveParam = new ParamEpcReadReserved();
                    reserveParam.setStart(0);
                    reserveParam.setLen(4);
                    msg.setReadReserved(reserveParam);
                }

                client.sendSynMsg(msg);

                if (0x00 == msg.getRtCode()) {
                    ToastUtils.showText("Start ReadCard");
                    isReader = true;
                    computedSpeed();
                    soundTask();
                } else {
                    handlerStop.sendEmptyMessage(1);
                    ToastUtils.showText(msg.getRtMsg());
                }

            } else {
                ToastUtils.showText(getResources().getString(R.string.read_card_being));
            }
        } else {
            ToastUtils.showText(getResources().getString(R.string.ununited));
        }
    }

    private Runnable timeTask = null;
    private final Handler soundHandler = new Handler();
    long rateValue = 0L;

    private void soundTask() {

        Runnable runnable = new Runnable() {
            public void run() {
                if (rateValue != 0L)
                    UtilSound.play(1, 0);
                soundHandler.postDelayed(this, 20L);
            }
        };
        this.timeTask = runnable;
        this.soundHandler.postDelayed(runnable, 0L);
    }

    //停止
    @OnClick(R.id.stop)
    public void stopRead() {
        if (isClient) {
            MsgBaseStop msgStop = new MsgBaseStop();
            client.sendSynMsg(msgStop);
            if (0x00 == msgStop.getRtCode()) {
                isReader = false;
                ToastUtils.showText("Stop Success");
            } else {
                ToastUtils.showText("Stop Fail");
            }
        } else {
            ToastUtils.showText(getResources().getString(R.string.ununited));
        }
    }

    //清屏
    @OnClick(R.id.clean)
    public void cleanData() {
        if (isClient) {
            tagInfoList.clear();
            adapter.notifyData(tagInfoList);
//            initPane();
        } else {
            ToastUtils.showText(getResources().getString(R.string.ununited));
        }
    }

    //去重6C
    // FIXME: 28/05/2022  هون عم تصير تعبة ال map
    public Map<String, TagInfo> pooled6cData(LogBaseEpcInfo info) {

        if (tagInfoMap.containsKey(info.getTid() + info.getEpc())) {
            TagInfo tagInfo = tagInfoMap.get(info.getTid() + info.getEpc());
            Long count = tagInfoMap.get(info.getTid() + info.getEpc()).getCount();
            count++;
            tagInfo.setRssi(info.getRssi() + "");
            tagInfo.setReservedData(info.getReserved());
            tagInfo.setUserData(info.getUserdata());
            tagInfo.setCount(count);
            tagInfoMap.put(info.getTid() + info.getEpc(), tagInfo);
        } else {
            TagInfo tag = new TagInfo();
            tag.setIndex(index);
            tag.setType("6C");
            tag.setEpc(info.getEpc());
            tag.setCount(1l);
            tag.setUserData(info.getUserdata());
            tag.setReservedData(info.getReserved());
            tag.setTid(info.getTid());
            tag.setRssi(info.getRssi() + "");
            tag.setReadTime(new Date());
            tagInfoMap.put(info.getTid() + info.getEpc(), tag);
            index++;
        }

        handlerStop.sendEmptyMessage(2);//تشغيل الصوت

        return tagInfoMap;
    }

    //去重6B
    public Map<String, TagInfo> pooled6bData(LogBase6bInfo info) {
        if (tagInfoMap.containsKey(info.getTid())) {
            TagInfo tagInfo = tagInfoMap.get(info.getTid());
            Long count = tagInfoMap.get(info.getTid()).getCount();
            count++;
            tagInfo.setRssi(info.getRssi() + "");
            tagInfo.setCount(count);
            tagInfoMap.put(info.getTid(), tagInfo);
        } else {
            TagInfo tag = new TagInfo();
            tag.setIndex(index);
            tag.setType("6B");
            tag.setCount(1l);
            tag.setUserData(info.getUserdata());
            if (info.getTid() != null) {
                tag.setTid(info.getTid());
            }
            tag.setRssi(info.getRssi() + "");
            tagInfoMap.put(info.getTid(), tag);
            index++;
        }
        handlerStop.sendEmptyMessage(2);
        return tagInfoMap;
    }

    //去重GB
    public Map<String, TagInfo> pooledGbData(LogBaseGbInfo info) {
        if (tagInfoMap.containsKey(info.getTid() + info.getEpc())) {
            TagInfo tagInfo = tagInfoMap.get(info.getTid() + info.getEpc());
            Long count = tagInfoMap.get(info.getTid() + info.getEpc()).getCount();
            count++;
            tagInfo.setRssi(info.getRssi() + "");
            tagInfo.setCount(count);
            tagInfoMap.put(info.getTid() + info.getEpc(), tagInfo);
        } else {
            TagInfo tag = new TagInfo();
            tag.setIndex(index);
            tag.setType("GB");
            tag.setEpc(info.getEpc());
            tag.setCount(1l);
            tag.setUserData(info.getUserdata());
            tag.setTid(info.getTid());
            tag.setRssi(info.getRssi() + "");
            tagInfoMap.put(info.getTid() + info.getEpc(), tag);
            index++;
        }
        handlerStop.sendEmptyMessage(2);
        return tagInfoMap;
    }


    //程序退出
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isClient) {
            if (isReader) {
                MsgBaseStop msgStop = new MsgBaseStop();
                client.sendSynMsg(msgStop);
                if (msgStop.getRtCode() == 0) {
                    ToastUtils.showText(getResources().getString(R.string.stop_card));
                } else {
                    ToastUtils.showText(msgStop.getRtMsg());
                }
            }
        }

        this.tryConnect = false;

        adapter.threadKill = false;
        adapter.thread.interrupt();

        socketClient.close();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isClient) {
            if (isReader) {
                MsgBaseStop msgStop = new MsgBaseStop();
                client.sendSynMsg(msgStop);
                if (msgStop.getRtCode() == 0) {
                    isReader = false;
                    ToastUtils.showText(getResources().getString(R.string.stop_card));
                } else {
                    ToastUtils.showText(msgStop.getRtMsg());
                }
            }
        }
    }

//    @OnClick(R.id.tabHead)
//    public void getTabHead() {
//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setTitle(getResources().getString(R.string.select_type))
//                .setMultiChoiceItems(new String[]{
//                                getResources().getString(R.string.select_tid),
//                                getResources().getString(R.string.select_user),
//                                getResources().getString(R.string.select_reserve)},
//                        isChecked,
//                        (dialog1, which, isChecked) -> {
//                            System.out.println(which + "--" + isChecked);
//                        })
//                .setCancelable(false)
//                .setPositiveButton("confirm", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                })
//                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .show();
//        //修改“确认”、“取消”按钮的字体大小
//        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(23);
//        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(23);
//        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.rgb(0, 87, 75));
//        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.rgb(0, 87, 75));
//        try {
//            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
//            mAlert.setAccessible(true);
//            Object mAlertController = mAlert.get(dialog);
//            //通过反射修改title字体大小和颜色
//            Field mTitle = mAlertController.getClass().getDeclaredField("mTitleView");
//            mTitle.setAccessible(true);
//            TextView mTitleView = (TextView) mTitle.get(mAlertController);
//            mTitleView.setTextSize(23);
//            mTitleView.setTextColor(Color.rgb(0, 87, 75));
//        } catch (IllegalAccessException e1) {
//            e1.printStackTrace();
//        } catch (NoSuchFieldException e2) {
//            e2.printStackTrace();
//        }
//    }


    private void upDataPane() {
        adapter.notifyData(tagInfoList);
    }

    @SuppressLint("HandlerLeak")
    final Handler handlerStop = new Handler() {
        public void handleMessage(Message param1Message) {
            if (param1Message.what == 1) {
                mHandler.removeCallbacks(r);
                soundHandler.removeCallbacks(timeTask);
                upDataPane();
            }
            super.handleMessage(param1Message);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        System.err.println(keyCode);
        Log.e("onKeyDown", keyCode + "");

//        Toast.makeText(this, "اختبار الزر" + keyCode, Toast.LENGTH_SHORT).show();

        if (keyCode == 285) //زر جهاز ال R.F.I.D
            if (isReader)
                stopRead();
            else
                readCard();

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.e("onKeyUp", keyCode + "");
        return super.onKeyUp(keyCode, event);
    }


}
