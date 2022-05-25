package com.example.gxwl.rederdemo;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.annimon.stream.Collectors;
import com.annimon.stream.ComparatorCompat;
import com.annimon.stream.Stream;
import com.example.gxwl.rederdemo.SAED.SocketClient;
import com.example.gxwl.rederdemo.adapter.RecycleViewAdapter;
import com.example.gxwl.rederdemo.entity.TagInfo;
import com.example.gxwl.rederdemo.util.ComputedPc;
import com.example.gxwl.rederdemo.util.DataUtils;
import com.example.gxwl.rederdemo.util.ExcelUtil;
import com.example.gxwl.rederdemo.util.GlobalClient;
import com.example.gxwl.rederdemo.util.LocalManageUtil;
import com.example.gxwl.rederdemo.util.ToastUtils;
import com.example.gxwl.rederdemo.util.UtilSound;
import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.dal.HandlerGpiOver;
import com.gg.reader.api.dal.HandlerGpiStart;
import com.gg.reader.api.dal.HandlerTag6bLog;
import com.gg.reader.api.dal.HandlerTag6bOver;
import com.gg.reader.api.dal.HandlerTagEpcLog;
import com.gg.reader.api.dal.HandlerTagEpcOver;
import com.gg.reader.api.dal.HandlerTagGbLog;
import com.gg.reader.api.dal.HandlerTagGbOver;
import com.gg.reader.api.protocol.gx.EnumG;
import com.gg.reader.api.protocol.gx.LogAppGpiOver;
import com.gg.reader.api.protocol.gx.LogAppGpiStart;
import com.gg.reader.api.protocol.gx.LogBase6bInfo;
import com.gg.reader.api.protocol.gx.LogBase6bOver;
import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
import com.gg.reader.api.protocol.gx.LogBaseEpcOver;
import com.gg.reader.api.protocol.gx.LogBaseGbInfo;
import com.gg.reader.api.protocol.gx.LogBaseGbOver;
import com.gg.reader.api.protocol.gx.MsgBaseInventory6b;
import com.gg.reader.api.protocol.gx.MsgBaseInventoryEpc;
import com.gg.reader.api.protocol.gx.MsgBaseInventoryGb;
import com.gg.reader.api.protocol.gx.MsgBaseSetBaseband;
import com.gg.reader.api.protocol.gx.MsgBaseStop;
import com.gg.reader.api.protocol.gx.MsgBaseWrite6b;
import com.gg.reader.api.protocol.gx.MsgBaseWriteEpc;
import com.gg.reader.api.protocol.gx.MsgBaseWriteGb;
import com.gg.reader.api.protocol.gx.Param6bReadUserdata;
import com.gg.reader.api.protocol.gx.ParamEpcFilter;
import com.gg.reader.api.protocol.gx.ParamEpcReadEpc;
import com.gg.reader.api.protocol.gx.ParamEpcReadReserved;
import com.gg.reader.api.protocol.gx.ParamEpcReadTid;
import com.gg.reader.api.protocol.gx.ParamEpcReadUserdata;
import com.gg.reader.api.protocol.gx.ParamGbReadUserdata;
import com.gg.reader.api.utils.HexUtils;
import com.gg.reader.api.utils.StringUtils;
import com.gg.reader.api.utils.ThreadPoolUtils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;

@SuppressLint("NonConstantResourceId")
public class ReadOrWriteActivity extends AppCompatActivity {
    // region 组件变量
    @BindView(R.id.read)
    Button read;
    @BindView(R.id.stop)
    Button stop;
    @BindView(R.id.clean)
    Button clean;
    @BindView(R.id.way)
    RadioGroup way;
    @BindView(R.id.type)
    RadioGroup type;
    @BindView(R.id.single)
    RadioButton single;
    @BindView(R.id.loop)
    RadioButton loop;
    @BindView(R.id.c)
    RadioButton c;
    @BindView(R.id.b)
    RadioButton b;
    @BindView(R.id.readCount)
    TextView readCount;
    @BindView(R.id.tagCount)
    TextView tagCount;
    @BindView(R.id.speed)
    TextView speed;
    @BindView(R.id.timeCount)
    TextView timeCount;
    @BindView(R.id.tabHead)
    LinearLayout tabHead;
    @BindView(R.id.not_connect)
    TextView notConnectTv;

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

    //6b cus read
    EditText cus_read_filter_6b_tid;
    Spinner cus_read_6b_content;
    EditText cus_read_6b_start;
    EditText cus_read_6b_len;
    //gb ces read
    CheckBox cus_gb_read_check;
    Spinner cus_gb_read_mode;
    EditText cus_read_gb_start;
    EditText cus_read_gb_content;
    CheckBox cus_gb_read_check_tid;
    Spinner cus_readTid_gb_content;
    EditText filter_read_gb_len;
    CheckBox cus_gb_read_check_user;
    Spinner filter_read_gb_userChild;
    EditText filter_read_gb_userChild_start;
    EditText cus_read_gb_len;
    EditText filter_read_gb_userChild_pas;


    // endregion

    private GClient client = GlobalClient.getClient();
    private boolean isClient = false;
    private Map<String, TagInfo> tagInfoMap = new LinkedHashMap<String, TagInfo>();//去重数据源
    private List<TagInfo> tagInfoList = new ArrayList<TagInfo>();//适配器所需数据源
    private Long index = 1l;//索引
    private RecycleViewAdapter adapter;
    private ParamEpcReadTid tidParam = null;
    private ParamEpcReadUserdata userParam = null;
    private ParamEpcReadReserved reserveParam = null;
    private Param6bReadUserdata user6bParam = null;
    private boolean[] isChecked = new boolean[]{false, false, false};//标识读0-epc与1-user
    private Handler mHandler = new Handler();
    private Runnable r = null;
    private int time = 0;
    private boolean isSound = true;
    MenuItem gMenuItem = null;
    private boolean isReader = false;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

    //saed :
    public SocketClient socketClient = new SocketClient();

    /**
     * to checking if can reConnect with socket <p>
     * will be false when onDistroy Activity
     */
    boolean tryConnect = true;
    public Thread thread;
    public String ip;
    public int port;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);
        isClient = getIntent().getBooleanExtra("isClient", false);

        ip = getIntent().getStringExtra("ip").replaceAll("\\s+", "");
        port = getIntent().getIntExtra("port", 0);

        initSocket(ip, port);

        if (isClient) {
            subHandler(GlobalClient.getClient());
        }
        initRecycleView();
        UtilSound.initSoundPool(this);

//        new Handler(getMainLooper()).postDelayed(() -> {
//
//
//            for (int i = 0; i < 20; i++) {
//                TagInfo info = new TagInfo();
//                info.setIndex(123L);
//                info.setUserData("22222223333333333333333333333333333333333333333333333333333333333333333333333333333333士大夫是非得失是的22222222222222222222222222222222222222222222222222222222222222222233333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333");
//                info.setReservedData("3333333333333333");
//                info.setCount(3243l);
//                info.setType("6c");
//                info.setRssi("123");
//                info.setEpc(i + "");
//                tagInfoList.add(info);
//            }
//            adapter.notifyData(tagInfoList);
//        }, 5000);
//
//        new Handler(getMainLooper()).postDelayed(() -> {
//
//            for (int i = 0; i < 20; i++) {
//                TagInfo info = new TagInfo();
//                info.setIndex(123L);
//                info.setUserData("22222223333333333333333333333333333333333333333333333333333333333333333333333333333333士大夫是非得失是的22222222222222222222222222222222222222222222222222222222222222222233333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333");
//                info.setReservedData("3333333333333333");
//                info.setCount(3243l);
//                info.setType("6c");
//                info.setRssi("123");
//                info.setEpc(i * 2 + "");
//                tagInfoList.add(info);
//            }
//            adapter.notifyData(tagInfoList);
//        }, 10000);


    }

    //saed :
    public void initSocket(String mIp, int mPort) {
        new Thread(() -> {
            while (tryConnect) {

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

        socketClient.setConnectStat(connect -> {

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
        client.onTagEpcLog = new HandlerTagEpcLog() {
            public void log(String readerName, LogBaseEpcInfo info) {
                if (null != info && 0 == info.getResult()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Map<String, TagInfo> infoMap = pooled6cData(info);
                            tagInfoList.clear();
                            tagInfoList.addAll(infoMap.values());
                        }
                    });
                }
            }
        };
        client.onTagEpcOver = new HandlerTagEpcOver() {
            public void log(String readerName, LogBaseEpcOver info) {
                handlerStop.sendEmptyMessage(new Message().what = 1);
            }
        };
        client.onTag6bLog = new HandlerTag6bLog() {
            public void log(String readerName, LogBase6bInfo info) {
                if (null != info && info.getResult() == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Map<String, TagInfo> infoMap = pooled6bData(info);
                            tagInfoList.clear();
                            tagInfoList.addAll(infoMap.values());
                        }
                    });
                }
            }
        };
        client.onTag6bOver = new HandlerTag6bOver() {
            public void log(String readerName, LogBase6bOver info) {
                handlerStop.sendEmptyMessage(new Message().what = 1);
            }
        };
        client.onTagGbLog = new HandlerTagGbLog() {
            public void log(String readerName, LogBaseGbInfo info) {
                if (null != info && info.getResult() == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Map<String, TagInfo> infoMap = pooledGbData(info);
                            tagInfoList.clear();
                            tagInfoList.addAll(infoMap.values());
                        }
                    });
                }

            }
        };
        client.onTagGbOver = new HandlerTagGbOver() {
            public void log(String readerName, LogBaseGbOver info) {
                handlerStop.sendEmptyMessage(new Message().what = 1);
            }
        };
        client.onGpiOver = new HandlerGpiOver() {
            @Override
            public void log(String s, LogAppGpiOver logAppGpiOver) {
                System.out.println(logAppGpiOver);
            }
        };
        client.onGpiStart = new HandlerGpiStart() {
            @Override
            public void log(String s, LogAppGpiStart logAppGpiStart) {
                System.out.println(logAppGpiStart);
            }
        };
    }

    //读卡
    @OnClick(R.id.read)
    public void readCard() {
        if (isClient) {
            if (!isReader) {
                if (type.getCheckedRadioButtonId() == R.id.c) {
                    MsgBaseInventoryEpc msg = new MsgBaseInventoryEpc();
                    msg.setAntennaEnable(EnumG.AntennaNo_1);
                    if (way.getCheckedRadioButtonId() == R.id.single) {
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
                    initPane();
                    computedSpeed();
                    if (0x00 == msg.getRtCode()) {
                        ToastUtils.showText("Start ReadCard");
                        isReader = true;
                    } else {
                        handlerStop.sendEmptyMessage(1);
                        ToastUtils.showText(msg.getRtMsg());
                    }
                } else if (type.getCheckedRadioButtonId() == R.id.b) {
                    MsgBaseInventory6b msg = new MsgBaseInventory6b();
                    msg.setAntennaEnable(EnumG.AntennaNo_1);
                    if (way.getCheckedRadioButtonId() == R.id.single) {
                        msg.setInventoryMode(EnumG.InventoryMode_Single);
                    } else {
                        msg.setInventoryMode(EnumG.InventoryMode_Inventory);
                    }
                    if (isChecked[1]) {
                        user6bParam = new Param6bReadUserdata();
                        user6bParam.setStart(0);
                        user6bParam.setLen(10);
                        msg.setReadUserdata(user6bParam);
                    }
                    msg.setArea(EnumG.ReadMode6b_Tid);

                    client.sendSynMsg(msg);
                    initPane();
                    computedSpeed();
                    if (0x00 == msg.getRtCode()) {
                        ToastUtils.showText("Start ReadCard");
                        isReader = true;
                    } else {
                        handlerStop.sendEmptyMessage(1);
                        ToastUtils.showText(msg.getRtMsg());
                    }
                } else if (type.getCheckedRadioButtonId() == R.id.gb) {
                    MsgBaseInventoryGb msg = new MsgBaseInventoryGb();
                    msg.setAntennaEnable(EnumG.AntennaNo_1);
                    if (way.getCheckedRadioButtonId() == R.id.single) {
                        msg.setInventoryMode(EnumG.InventoryMode_Single);
                    } else {
                        msg.setInventoryMode(EnumG.InventoryMode_Inventory);
                    }
                    if (isChecked[0]) {
                        ParamEpcReadTid tid = new ParamEpcReadTid();
                        tid.setMode(EnumG.ParamTidMode_Auto);
                        tid.setLen(6);
                        msg.setReadTid(tid);
                    }
                    if (isChecked[1]) {
                        ParamGbReadUserdata gbReadUserdata = new ParamGbReadUserdata();
                        gbReadUserdata.setStart(4);//字
                        gbReadUserdata.setLen(1);//字
                        gbReadUserdata.setChildArea(0x30);
                        msg.setReadUserdata(gbReadUserdata);
                    }

                    client.sendSynMsg(msg);
                    initPane();
                    computedSpeed();
                    if (0x00 == msg.getRtCode()) {
                        ToastUtils.showText("Start ReadCard");
                        isReader = true;
                    } else {
                        handlerStop.sendEmptyMessage(1);
                        ToastUtils.showText(msg.getRtMsg());
                    }
                }
            } else {
                ToastUtils.showText(getResources().getString(R.string.read_card_being));
            }
        } else {
            ToastUtils.showText(getResources().getString(R.string.ununited));
        }
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
            initPane();
        } else {
            ToastUtils.showText(getResources().getString(R.string.ununited));
        }
    }

    //去重6C
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
        handlerStop.sendEmptyMessage(2);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        gMenuItem = menu.findItem(R.id.sound);
        return true;
    }

    // 菜单的监听方法
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.single:
                MsgBaseSetBaseband single = new MsgBaseSetBaseband();
                single.setqValue(0);
                single.setInventoryFlag(2);
                GlobalClient.getClient().sendSynMsg(single);
                if (0x00 == single.getRtCode()) {
                    ToastUtils.showText(getResources().getString(R.string.set_success));
                } else {
                    ToastUtils.showText(single.getRtMsg());
                }
                break;
            case R.id.much:
                MsgBaseSetBaseband much = new MsgBaseSetBaseband();
                much.setqValue(4);
                much.setInventoryFlag(2);
                GlobalClient.getClient().sendSynMsg(much);
                if (0x00 == much.getRtCode()) {
                    ToastUtils.showText(getResources().getString(R.string.set_success));
                } else {
                    ToastUtils.showText(much.getRtMsg());
                }
                break;
            case R.id.sound:
                if (isSound) {
                    isSound = false;
                    gMenuItem.setTitle(getResources().getString(R.string.onBuzzer));
                } else {
                    isSound = true;
                    gMenuItem.setTitle(getResources().getString(R.string.offBuzzer));
                }
                break;
            default:
                break;
        }
        return true;

    }

    @OnClick(R.id.tabHead)
    public void getTabHead() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.select_type))
                .setMultiChoiceItems(new String[]{getResources().getString(R.string.select_tid), getResources().getString(R.string.select_user), getResources().getString(R.string.select_reserve)}, isChecked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        System.out.println(which + "--" + isChecked);

                    }
                })
                .setCancelable(false)
                .setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
        //修改“确认”、“取消”按钮的字体大小
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(23);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(23);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.rgb(0, 87, 75));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.rgb(0, 87, 75));
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(dialog);
            //通过反射修改title字体大小和颜色
            Field mTitle = mAlertController.getClass().getDeclaredField("mTitleView");
            mTitle.setAccessible(true);
            TextView mTitleView = (TextView) mTitle.get(mAlertController);
            mTitleView.setTextSize(23);
            mTitleView.setTextColor(Color.rgb(0, 87, 75));
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (NoSuchFieldException e2) {
            e2.printStackTrace();
        }
    }

    //一秒刷新计算
    private void computedSpeed() {
        Map<String, Long> rateMap = new Hashtable<String, Long>();
        r = new Runnable() {
            @Override
            public void run() {
                String toTime = secToTime(++time);
                timeCount.setText(toTime + " (s)");
                long before = 0;
                long after = 0;
                Long afterValue = rateMap.get("after");
                if (null != afterValue) {
                    before = afterValue;
                }
                adapter.notifyData(tagInfoList);
                readCount.setText(getReadCount(tagInfoList) + "");
                tagCount.setText(tagInfoList.size() + "");
                rateMap.put("after", Long.valueOf(readCount.getText().toString()));
                after = Long.valueOf(readCount.getText().toString());
                if (after >= before) {
                    long rateValue = after - before;
                    speed.setText(rateValue + " (t/s)");
                }
                //每隔1s循环执行run方法
                mHandler.postDelayed(this, 1000);
            }
        };
        //延迟一秒执行
        mHandler.postDelayed(r, 1000);
    }

    //初始化面板
    private void initPane() {
        index = 1l;
        time = 0;
        tagInfoMap.clear();
        tagInfoList.clear();
        adapter.notifyData(tagInfoList);
        tagCount.setText(0 + "");
        readCount.setText(0 + "");
        timeCount.setText("00:00:00" + " (s)");
        speed.setText(0 + " (t/s)");
        adapter.setThisPosition(null);

    }

    //更新面板
    private void upDataPane() {
        adapter.notifyData(tagInfoList);
        readCount.setText(getReadCount(tagInfoList) + "");
        tagCount.setText(tagInfoList.size() + "");
    }

    //获取读取总次数
    private long getReadCount(List<TagInfo> tagInfoList) {
        long readCount = 0;
        for (int i = 0; i < tagInfoList.size(); i++) {
            readCount += tagInfoList.get(i).getCount();
        }
        return readCount;
    }

    //格式化时间
    public String secToTime(long time) {
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        time = time * 1000;
        String hms = formatter.format(time);
        return hms;
    }

    //写EPC
    @OnClick(R.id.fab_epc)
    public void writeEPC() {
        if (adapter.getThisPosition() != null) {
            if (type.getCheckedRadioButtonId() == R.id.c) {
                View writeView = getLayoutInflater().inflate(R.layout.write_epc, null, false);
                w_epc = writeView.findViewById(R.id.w_epc);
                w_tid = writeView.findViewById(R.id.w_tid);
                w_pas = writeView.findViewById(R.id.w_pas);
                w_len = writeView.findViewById(R.id.w_len);
                w_value = writeView.findViewById(R.id.w_value);
                AlertDialog.Builder writeDialog = new AlertDialog.Builder(ReadOrWriteActivity.this);
                writeDialog.setView(writeView);
                writeDialog.setCancelable(false);
                writeDialog.setPositiveButton("OK", null);
                writeDialog.setNegativeButton("Cancel", null);
                AlertDialog dialog = writeDialog.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MsgBaseWriteEpc msg = new MsgBaseWriteEpc();
                        //天线
                        msg.setAntennaEnable(EnumG.AntennaNo_1);
                        //数据区 0保留区 1EPC区 2TID区 3用户数据区
                        msg.setArea(EnumG.WriteArea_Epc);
                        //字起始地址 第0个为CRC，不可写
                        msg.setStart(1);
                        //写入内容
                        String content = ComputedPc.getPc(ComputedPc.getEPCLength(w_value)) + DataUtils.padLeft(w_value.getText().toString().trim().toUpperCase(), 4 * ComputedPc.getEPCLength(w_value), '0');
                        if (!StringUtils.isNullOfEmpty(content)) {
                            msg.setBwriteData(HexUtils.hexString2Bytes(content));
                        }
                        //选择写入参数
                        ParamEpcFilter filter = new ParamEpcFilter();
                        if (!StringUtils.isNullOfEmpty(w_tid.getText().toString().trim())) {
                            filter.setArea(EnumG.ParamFilterArea_TID);
                            filter.setHexData(w_tid.getText().toString().trim());
                            filter.setBitStart(0);
                            filter.setBitLength(w_tid.getText().toString().length() * 4);
                        } else {
                            filter.setArea(EnumG.ParamFilterArea_EPC);
                            filter.setHexData(w_epc.getText().toString().trim());
                            filter.setBitStart(32);
                            filter.setBitLength(w_epc.getText().toString().length() * 4);
                        }
                        msg.setFilter(filter);
                        //密码
                        String writeEPCPassword = w_pas.getText().toString();
                        msg.setHexPassword(writeEPCPassword);
                        //发送指令
                        client.sendSynMsg(msg);
                        if (0x00 == msg.getRtCode()) {
                            ToastUtils.showText(msg.getRtMsg());
                        } else {
                            ToastUtils.showText(msg.getRtMsg());
                        }
                    }
                });
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                w_epc.setText(tagInfoList.get(adapter.getThisPosition()).getEpc());
                w_tid.setText(tagInfoList.get(adapter.getThisPosition()).getTid());
                w_value.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        System.out.println("onTextChanged" + s);
                        String taEPCDataText = s.toString().trim();
                        int iLength = 0;
                        if (taEPCDataText.length() % 4 == 0) {
                            iLength = taEPCDataText.length() / 4;
                        } else if (taEPCDataText.length() % 4 > 0) {
                            iLength = taEPCDataText.length() / 4 + 1;
                        }
                        w_len.setText(iLength + "");
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            } else if (type.getCheckedRadioButtonId() == R.id.b) {
                ToastUtils.showText(getResources().getString(R.string.not_sup_epc));
            } else if (type.getCheckedRadioButtonId() == R.id.gb) {
                View writeView = getLayoutInflater().inflate(R.layout.writegb_epc, null, false);
                w_gb_epc = writeView.findViewById(R.id.w_gb_epc);
                w_gb_pas = writeView.findViewById(R.id.w_gb_pas);
                w_gb_tid = writeView.findViewById(R.id.w_gb_tid);
                w_gb_len = writeView.findViewById(R.id.w_gb_len);
                w_gb_value = writeView.findViewById(R.id.w_gb_value);
                TagInfo tagInfo = tagInfoList.get(adapter.getThisPosition());
                AlertDialog.Builder writeDialog = new AlertDialog.Builder(ReadOrWriteActivity.this);
                writeDialog.setView(writeView);
                writeDialog.setCancelable(false);
                writeDialog.setPositiveButton("OK", null);
                writeDialog.setNegativeButton("Cancel", null);
                AlertDialog dialog = writeDialog.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!StringUtils.isNullOfEmpty(w_gb_value.getText().toString())) {
                            MsgBaseWriteGb msg = new MsgBaseWriteGb();
                            //天线
                            msg.setAntennaEnable(EnumG.AntennaNo_1);
                            //数据区 0x10标签编码区epc 0x20标签安全区 0x30~0x3F用户子区
                            msg.setArea(0x10);
                            //字起始地址
                            msg.setStart(0);
                            //数据内容
                            int epcLength = ComputedPc.getEPCLength(w_gb_value);
                            String pc = ComputedPc.getGbPc(epcLength);
                            String epcData = DataUtils.padLeft(w_gb_value.getText().toString().trim().toUpperCase(), 4 * epcLength, '0');
                            String content = pc + epcData;
                            msg.setHexWriteData(content);

                            //选择写入参数
                            ParamEpcFilter filter = new ParamEpcFilter();
                            if (!StringUtils.isNullOfEmpty(tagInfo.getTid())) {
                                filter.setArea(0x00);
                                filter.setBitStart(0);
                                filter.setBitLength(tagInfo.getTid().length() * 4);
                                filter.setHexData(tagInfo.getTid());
                            } else {
                                filter.setArea(0x10);
                                filter.setBitStart(16);
                                filter.setBitLength(tagInfo.getEpc().length() * 4);
                                filter.setHexData(tagInfo.getEpc());
                            }
                            msg.setFilter(filter);

                            //密码
                            msg.setHexPassword(w_gb_pas.getText().toString().trim());
                            //发送指令
                            client.sendSynMsg(msg);
                            if (0x00 == msg.getRtCode()) {
                                ToastUtils.showText("Write Success");
                            } else {
                                ToastUtils.showText(msg.getRtMsg());
                            }
                        } else {
                            ToastUtils.showText("Not Null");
                        }
                    }
                });
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                w_gb_epc.setText(tagInfo.getEpc());
                w_gb_tid.setText(tagInfo.getTid());
                w_gb_value.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String taEPCDataText = s.toString().trim();
                        int iLength = 0;
                        if (taEPCDataText.length() % 4 == 0) {
                            iLength = taEPCDataText.length() / 4;
                        } else if (taEPCDataText.length() % 4 > 0) {
                            iLength = taEPCDataText.length() / 4 + 1;
                        }
                        w_gb_len.setText(iLength + "");
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            }
        } else {
            ToastUtils.showText(getResources().getString(R.string.Unchecked_tag));
        }
    }

    //写用户区
    @OnClick(R.id.fab_user)
    public void writeUser() {
        if (adapter.getThisPosition() != null) {
            if (type.getCheckedRadioButtonId() == R.id.c) {
                View writeView = getLayoutInflater().inflate(R.layout.write6c_user, null, false);
                w_user_epc = writeView.findViewById(R.id.w_user_epc);
                w_user_tid = writeView.findViewById(R.id.w_user_tid);
                w_user_pas = writeView.findViewById(R.id.w_user_pas);
                w_user_len = writeView.findViewById(R.id.w_user_len);
                w_user_value = writeView.findViewById(R.id.w_user_value);
                AlertDialog.Builder writeDialog = new AlertDialog.Builder(ReadOrWriteActivity.this);
                writeDialog.setView(writeView);
                writeDialog.setCancelable(false);
                writeDialog.setPositiveButton("OK", null);
                writeDialog.setNegativeButton("Cancel", null);
                AlertDialog dialog = writeDialog.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MsgBaseWriteEpc msg = new MsgBaseWriteEpc();
                        //天线
                        msg.setAntennaEnable(EnumG.AntennaNo_1);
                        //数据区 0保留区 1EPC区 2TID区 3用户数据区
                        msg.setArea(EnumG.WriteArea_Userdata);
                        //字起始地址
                        String start = w_user_len.getText().toString();
                        if (null != start) {
                            msg.setStart(Integer.parseInt(start));
                        }
                        //数据内容

                        String userData = w_user_value.getText().toString().trim();
                        int length = ComputedPc.getEPCLength(w_user_value);
                        String s = DataUtils.padLeft(userData, 4 * length, '0');
                        if (!StringUtils.isNullOfEmpty(s)) {
                            msg.setHexWriteData(s);
                        }
                        //选择写入参数
                        ParamEpcFilter filter = new ParamEpcFilter();
                        if (!StringUtils.isNullOfEmpty(w_user_tid.getText().toString().trim())) {
                            filter.setArea(EnumG.ParamFilterArea_TID);
                            filter.setHexData(w_user_tid.getText().toString().trim());
                            filter.setBitLength(w_user_tid.getText().toString().length() * 4);
                        } else {
                            filter.setArea(EnumG.ParamFilterArea_EPC);
                            filter.setHexData(w_user_epc.getText().toString().trim());
                            filter.setBitLength(w_user_epc.getText().toString().length() * 4);
                        }
                        filter.setBitStart(0);
                        //密码
                        String writeEPCPassword = w_user_pas.getText().toString().trim();
                        msg.setHexPassword(writeEPCPassword);

                        client.sendSynMsg(msg);
                        if (0x00 == msg.getRtCode()) {
                            ToastUtils.showText(msg.getRtMsg());
                        } else {
                            ToastUtils.showText(msg.getRtMsg());
                        }
                    }
                });
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                w_user_epc.setText(tagInfoList.get(adapter.getThisPosition()).getEpc());
                w_user_tid.setText(tagInfoList.get(adapter.getThisPosition()).getTid());
            } else if (type.getCheckedRadioButtonId() == R.id.b) {
                View writeView = getLayoutInflater().inflate(R.layout.write6b_user, null, false);
                w_6b_user_tid = writeView.findViewById(R.id.w_6b_user_tid);
                w_6b_user_start = writeView.findViewById(R.id.w_6b_user_start);
                w_6b_user_value = writeView.findViewById(R.id.w_6b_user_value);
                AlertDialog.Builder writeDialog = new AlertDialog.Builder(ReadOrWriteActivity.this);
                writeDialog.setView(writeView);
                writeDialog.setCancelable(false);
                writeDialog.setPositiveButton("OK", null);
                writeDialog.setNegativeButton("Cancel", null);
                AlertDialog dialog = writeDialog.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MsgBaseWrite6b msg = new MsgBaseWrite6b();
                        //天线
                        msg.setAntennaEnable(EnumG.AntennaNo_1);
                        //待写标签的TID
                        msg.setHexMatchTid(w_6b_user_tid.getText().toString().trim());
                        //起始地址
                        String toWrite6bStartAdd = w_6b_user_start.getText().toString().trim();
                        if (null != toWrite6bStartAdd) {
                            msg.setStart((byte) Integer.parseInt(toWrite6bStartAdd));
                        }
                        //数据内容
                        String userData = w_6b_user_value.getText().toString().trim();
                        int length = ComputedPc.getEPCLength(w_6b_user_value);
                        String s = DataUtils.padLeft(userData, 2 * length, '0');
                        if (null != s) {
                            msg.setHexWriteData(s);
                        }
                        //发送指令
                        client.sendSynMsg(msg);
                        if (0x00 == msg.getRtCode()) {
                            ToastUtils.showText(msg.getRtMsg());
                        } else {
                            ToastUtils.showText(msg.getRtMsg());
                        }
                    }
                });
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                w_6b_user_tid.setText(tagInfoList.get(adapter.getThisPosition()).getTid());
            } else if (type.getCheckedRadioButtonId() == R.id.gb) {
                View writeView = getLayoutInflater().inflate(R.layout.writegb_user, null, false);
                w_gb_user_epc = writeView.findViewById(R.id.w_gb_user_epc);
                w_gb_user_pas = writeView.findViewById(R.id.w_gb_user_pas);
                w_gb_user_tid = writeView.findViewById(R.id.w_gb_user_tid);
                w_gb_user_start = writeView.findViewById(R.id.w_gb_user_start);
                write_gb_user_child = writeView.findViewById(R.id.write_gb_user_child);
                w_gb_user_value = writeView.findViewById(R.id.w_gb_user_value);
                TagInfo tagInfo = tagInfoList.get(adapter.getThisPosition());
                AlertDialog.Builder writeDialog = new AlertDialog.Builder(ReadOrWriteActivity.this);
                writeDialog.setCancelable(false);
                writeDialog.setView(writeView);
                writeDialog.setPositiveButton("OK", null);
                writeDialog.setNegativeButton("Cancel", null);
                AlertDialog dialog = writeDialog.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MsgBaseWriteGb msg = new MsgBaseWriteGb();
                        //天线
                        msg.setAntennaEnable(EnumG.AntennaNo_1);
                        //数据区
                        msg.setArea(getUserDataChild());
                        //起始地址
                        msg.setStart(Integer.parseInt(w_gb_user_start.getText().toString()));
                        //密码
                        msg.setHexPassword(w_gb_user_pas.getText().toString());
                        //数据内容
                        String userData = w_gb_user_value.getText().toString().trim();
                        int length = ComputedPc.getEPCLength(w_gb_user_value);
                        String s = DataUtils.padLeft(userData, 4 * length, '0');
                        if (null != s) {
                            msg.setHexWriteData(s);
                        }
                        //选择写入参数 信息区=TID 编码区=EPC
                        ParamEpcFilter filter = new ParamEpcFilter();
                        if (!StringUtils.isNullOfEmpty(w_gb_user_tid.getText().toString())) {
                            filter.setArea(0x00);//信息区
                            filter.setBitStart(0);
                            filter.setBitLength(w_gb_user_tid.getText().length() * 4);
                            filter.setHexData(w_gb_user_tid.getText().toString());
                        } else {
                            filter.setArea(0x10);//编码区
                            filter.setBitStart(16);//0100+data
                            filter.setBitLength(w_gb_user_epc.getText().length() * 4);
                            filter.setHexData(w_gb_user_epc.getText().toString());
                        }
                        msg.setFilter(filter);
                        //发送指令
                        client.sendSynMsg(msg);
                        if (0x00 == msg.getRtCode()) {
                            ToastUtils.showText("Write Success");
                        } else {
                            ToastUtils.showText(msg.getRtMsg());
                        }

                    }
                });
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                w_gb_user_epc.setText(tagInfo.getEpc());
                w_gb_user_tid.setText(tagInfo.getTid());
            }
        } else {
            ToastUtils.showText(getResources().getString(R.string.Unchecked_tag));
        }
    }

    //转换16进制用户子区为10进制
    public Integer getUserDataChild() {
        int userChild = 48;
        int selectedIndex = write_gb_user_child.getSelectedItemPosition();
        userChild = selectedIndex + (3 * 16);
        return userChild;
    }

    //自定义写
    @OnClick(R.id.fab_cus)
    public void writeCus() {
        if (adapter.getThisPosition() != null) {
            Intent intent = new Intent(this, DialogCusActivity.class);
            intent.putExtra("Tag", tagInfoList.get(adapter.getThisPosition()));
            intent.putExtra("Ant", EnumG.AntennaNo_1);
            intent.putExtra("Type", type.getCheckedRadioButtonId());
            startActivity(intent);
        } else {
            ToastUtils.showText(getResources().getString(R.string.Unchecked_tag));
        }
    }

    @OnClick(R.id.tag_info)
    public void tagInfo() {
        if (adapter.getThisPosition() != null) {
            View infoView = getLayoutInflater().inflate(R.layout.tag_info, null, false);
            info_index = infoView.findViewById(R.id.info_index);
            info_type = infoView.findViewById(R.id.info_type);
            info_epc = infoView.findViewById(R.id.info_epc);
            info_tid = infoView.findViewById(R.id.info_tid);
            info_userData = infoView.findViewById(R.id.info_userData);
            info_Reserved = infoView.findViewById(R.id.info_Reserved);
            AlertDialog.Builder infoDialog = new AlertDialog.Builder(ReadOrWriteActivity.this);
            infoDialog.setView(infoView);
            AlertDialog dialog = infoDialog.create();
            dialog.show();
            info_index.setText(tagInfoList.get(adapter.getThisPosition()).getIndex() + "");
            info_type.setText(tagInfoList.get(adapter.getThisPosition()).getType());
            info_epc.setText(tagInfoList.get(adapter.getThisPosition()).getEpc());
            info_tid.setText(tagInfoList.get(adapter.getThisPosition()).getTid());
            info_userData.setText(tagInfoList.get(adapter.getThisPosition()).getUserData());
            info_Reserved.setText(tagInfoList.get(adapter.getThisPosition()).getReservedData());
        } else {
            ToastUtils.showText(getResources().getString(R.string.Unchecked_tag));
        }
    }

    public void initCusRead(View view) {
        cus_read_mode = view.findViewById(R.id.cus_read_mode);
        cus_read_start = view.findViewById(R.id.cus_read_start);
        cus_read_match_content = view.findViewById(R.id.cus_read_match_content);
//        cus_read_epc = view.findViewById(R.id.cus_read_epc);
//        cus_read_tid = view.findViewById(R.id.cus_read_tid);
//        cus_read_user = view.findViewById(R.id.cus_read_user);
        cus_read_tid_mode = view.findViewById(R.id.cus_read_tid_mode);
        cus_read_tid_len = view.findViewById(R.id.cus_read_tid_len);
        read_tid_true = view.findViewById(R.id.read_tid_true);
        cus_read_user_start = view.findViewById(R.id.cus_read_user_start);
        cus_read_user_len = view.findViewById(R.id.cus_read_user_len);
        read_user_true = view.findViewById(R.id.read_user_true);
        cus_read_reserve_start = view.findViewById(R.id.cus_read_reserve_start);
        cus_read_reserve_len = view.findViewById(R.id.cus_read_reserve_len);
        read_reserve_true = view.findViewById(R.id.read_reserve_true);
        read_other_pas = view.findViewById(R.id.read_other_pas);
        cus_read_pas = view.findViewById(R.id.cus_read_pas);
        cus_read_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    cus_read_start.setText(32 + "");
                } else {
                    cus_read_start.setText(0 + "");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //自定义读
    @OnClick(R.id.cus_read)
    public void cusRead() {
        if (isClient) {
            if (!isReader) {
                if (type.getCheckedRadioButtonId() == R.id.c) {
                    View cusReadView = getLayoutInflater().inflate(R.layout.cus_read, null, false);
                    initCusRead(cusReadView);
                    AlertDialog.Builder writeDialog = new AlertDialog.Builder(ReadOrWriteActivity.this);
                    writeDialog.setView(cusReadView);
                    writeDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            initPane();
                            MsgBaseInventoryEpc msg = new MsgBaseInventoryEpc();
                            msg.setAntennaEnable(EnumG.AntennaNo_1);
                            if (way.getCheckedRadioButtonId() == R.id.single) {
                                msg.setInventoryMode(EnumG.InventoryMode_Single);
                            } else {
                                msg.setInventoryMode(EnumG.InventoryMode_Inventory);
                            }

                            if (cus_read_mode.getSelectedItemPosition() != 0) {
                                ParamEpcFilter filter = new ParamEpcFilter();
                                filter.setArea(cus_read_mode.getSelectedItemPosition());
                                filter.setBitStart(Integer.parseInt(cus_read_start.getText().toString()));
                                filter.setBitLength(cus_read_match_content.getText().toString().length() * 4);
                                filter.setHexData(cus_read_match_content.getText().toString());
                                msg.setFilter(filter);
                            }

                            if (read_tid_true.isChecked()) {
                                tidParam = new ParamEpcReadTid();
                                tidParam.setMode(cus_read_tid_mode.getSelectedItemPosition());
                                if (!StringUtils.isNullOfEmpty(cus_read_tid_len.getText().toString())) {
                                    tidParam.setLen(Integer.parseInt(cus_read_tid_len.getText().toString()));
                                } else {
                                    tidParam.setLen(6);
                                }
                                msg.setReadTid(tidParam);
                            }
                            if (read_user_true.isChecked()) {
                                userParam = new ParamEpcReadUserdata();
                                if (!StringUtils.isNullOfEmpty(cus_read_user_start.getText().toString())) {
                                    userParam.setStart(Integer.parseInt(cus_read_user_start.getText().toString()));
                                } else {
                                    userParam.setStart(0);
                                }
                                if (!StringUtils.isNullOfEmpty(cus_read_user_len.getText().toString())) {
                                    userParam.setLen(Integer.parseInt(cus_read_user_len.getText().toString()));
                                } else {
                                    userParam.setLen(6);
                                }
                                msg.setReadUserdata(userParam);
                            }
                            if (read_reserve_true.isChecked()) {
                                reserveParam = new ParamEpcReadReserved();
                                if (!StringUtils.isNullOfEmpty(cus_read_reserve_start.getText().toString())) {
                                    reserveParam.setStart(Integer.parseInt(cus_read_reserve_start.getText().toString()));
                                } else {
                                    reserveParam.setStart(0);
                                }
                                if (!StringUtils.isNullOfEmpty(cus_read_reserve_len.getText().toString())) {
                                    reserveParam.setLen(Integer.parseInt(cus_read_reserve_len.getText().toString()));
                                } else {
                                    reserveParam.setLen(4);
                                }
                                msg.setReadReserved(reserveParam);
                            }

                            if (read_other_pas.isChecked()) {
                                if (!StringUtils.isNullOfEmpty(cus_read_pas.getText().toString())) {
                                    msg.setHexPassword(cus_read_pas.getText().toString());
                                }
                            }
                            client.sendSynMsg(msg);
                            computedSpeed();
                            if (0x00 == msg.getRtCode()) {
                                isReader = true;
                                ToastUtils.showText("Start ReadCard");
                            } else {
                                handlerStop.sendEmptyMessage(1);
                                ToastUtils.showText(msg.getRtMsg());
                            }
                        }
                    });
                    AlertDialog dialog = writeDialog.create();
                    dialog.show();

                } else if (type.getCheckedRadioButtonId() == R.id.b) {
                    View cusReadView = getLayoutInflater().inflate(R.layout.cus_6b_read, null, false);
                    cus_read_filter_6b_tid = cusReadView.findViewById(R.id.cus_read_filter_6b_tid);
                    cus_read_6b_content = cusReadView.findViewById(R.id.cus_read_6b_content);
                    cus_read_6b_start = cusReadView.findViewById(R.id.cus_read_6b_start);
                    cus_read_6b_len = cusReadView.findViewById(R.id.cus_read_6b_len);
                    AlertDialog.Builder writeDialog = new AlertDialog.Builder(ReadOrWriteActivity.this);
                    writeDialog.setView(cusReadView);
                    writeDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            initPane();
                            MsgBaseInventory6b msg = new MsgBaseInventory6b();
                            //获取天线
                            msg.setAntennaEnable(EnumG.AntennaNo_1);
                            //读取模式 单次/循环
                            if (way.getCheckedRadioButtonId() == R.id.single) {
                                msg.setInventoryMode(EnumG.InventoryMode_Single);
                            } else {
                                msg.setInventoryMode(EnumG.InventoryMode_Inventory);
                            }
                            //读取内容
                            int position = cus_read_6b_content.getSelectedItemPosition();
                            if (position == 0) {
                                msg.setArea(EnumG.ReadMode6b_Tid);
                            } else {
                                msg.setArea(position == 1 ? EnumG.ReadMode6b_TidAndUserdata : EnumG.ReadMode6b_Userdata);
                                //匹配参数
                                Param6bReadUserdata userdata = new Param6bReadUserdata();
                                userdata.setStart(Integer.parseInt(cus_read_6b_start.getText().toString()));
                                userdata.setLen(Integer.parseInt(cus_read_6b_len.getText().toString()));
                                msg.setReadUserdata(userdata);
                            }

                            //待匹配的TID
                            String toMatchTid = cus_read_filter_6b_tid.getText().toString().trim();
                            if (!StringUtils.isNullOfEmpty(toMatchTid)) {
                                msg.setHexMatchTid(toMatchTid);
                            }
                            //发送指令
                            client.sendSynMsg(msg);
                            computedSpeed();
                            if (0x00 == msg.getRtCode()) {
                                isReader = true;
                                ToastUtils.showText("Start ReadCard");
                            } else {
                                handlerStop.sendEmptyMessage(1);
                                ToastUtils.showText(msg.getRtMsg());
                            }

                        }
                    });
                    AlertDialog dialog = writeDialog.create();
                    dialog.show();
                } else if (type.getCheckedRadioButtonId() == R.id.gb) {
                    View cusReadView = getLayoutInflater().inflate(R.layout.cus_gb_read, null, false);
                    cus_gb_read_check = cusReadView.findViewById(R.id.cus_gb_read_check);
                    cus_gb_read_mode = cusReadView.findViewById(R.id.cus_gb_read_mode);
                    cus_read_gb_start = cusReadView.findViewById(R.id.cus_read_gb_start);
                    cus_read_gb_content = cusReadView.findViewById(R.id.cus_read_gb_content);
                    cus_gb_read_check_tid = cusReadView.findViewById(R.id.cus_gb_read_check_tid);
                    cus_readTid_gb_content = cusReadView.findViewById(R.id.cus_readTid_gb_content);
                    filter_read_gb_len = cusReadView.findViewById(R.id.filter_read_gb_len);
                    cus_gb_read_check_user = cusReadView.findViewById(R.id.cus_gb_read_check_user);
                    filter_read_gb_userChild = cusReadView.findViewById(R.id.filter_read_gb_userChild);
                    filter_read_gb_userChild_start = cusReadView.findViewById(R.id.filter_read_gb_userChild_start);
                    cus_read_gb_len = cusReadView.findViewById(R.id.cus_read_gb_len);
                    filter_read_gb_userChild_pas = cusReadView.findViewById(R.id.filter_read_gb_userChild_pas);

                    AlertDialog.Builder writeDialog = new AlertDialog.Builder(ReadOrWriteActivity.this);
                    writeDialog.setView(cusReadView);
                    writeDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            initPane();
                            MsgBaseInventoryGb msg = new MsgBaseInventoryGb();
                            //获取天线
                            msg.setAntennaEnable(EnumG.AntennaNo_1);
                            //读取模式 单次/循环
                            if (way.getCheckedRadioButtonId() == R.id.single) {
                                msg.setInventoryMode(EnumG.InventoryMode_Single);
                            } else {
                                msg.setInventoryMode(EnumG.InventoryMode_Inventory);
                            }
                            //匹配读取
                            if (cus_gb_read_check.isChecked()) {

                                int position = cus_gb_read_mode.getSelectedItemPosition();
                                if (position != 0) {
                                    ParamEpcFilter filter = new ParamEpcFilter();
                                    if (position == 1) {
                                        filter.setArea(0x00);
                                    } else if (position == 2) {
                                        filter.setArea(0x10);
                                    } else if (position == 3) {
                                        filter.setArea(0x20);
                                    } else {
                                        filter.setArea((position - 4) + (3 * 16));
                                    }
                                    String start = cus_read_gb_start.getText().toString().trim();
                                    if (!StringUtils.isNullOfEmpty(start)) {
                                        filter.setBitStart(Integer.parseInt(start));
                                    }
                                    String hex = cus_read_gb_content.getText().toString().trim();
                                    if (!StringUtils.isNullOfEmpty(hex)) {
                                        filter.setHexData(hex);
                                        filter.setBitLength(hex.length() * 4);
                                    }
                                    msg.setFilter(filter);
                                }
                            }
                            //读TID
                            if (cus_gb_read_check_tid.isChecked()) {
                                int position = cus_readTid_gb_content.getSelectedItemPosition();
                                ParamEpcReadTid epcReadTid = new ParamEpcReadTid();
                                epcReadTid.setMode(position == 0 ? EnumG.ParamTidMode_Auto : EnumG.ParamTidMode_Fixed);
                                epcReadTid.setLen(Integer.parseInt(filter_read_gb_len.getText().toString().trim()));
                                msg.setReadTid(epcReadTid);
                            }
                            //读用户区
                            if (cus_gb_read_check_user.isChecked()) {
                                ParamGbReadUserdata userdata = new ParamGbReadUserdata();
                                int position = filter_read_gb_userChild.getSelectedItemPosition();
                                userdata.setChildArea(position + (3 * 16));
                                userdata.setStart(Integer.parseInt(filter_read_gb_userChild_start.getText().toString().trim()));
                                userdata.setLen(Integer.parseInt(cus_read_gb_len.getText().toString().trim()));
                                msg.setReadUserdata(userdata);
                            }
                            //密码
                            String password = filter_read_gb_userChild_pas.getText().toString().trim();
                            if (!StringUtils.isNullOfEmpty(password)) {
                                msg.setHexPassword(password);
                            }
                            //发送读的指令
                            client.sendSynMsg(msg);
                            computedSpeed();
                            if (0x00 == msg.getRtCode()) {
                                isReader = true;
                                ToastUtils.showText("Start ReadCard");
                            } else {
                                handlerStop.sendEmptyMessage(1);
                                ToastUtils.showText(msg.getRtMsg());
                            }
                        }
                    });
                    AlertDialog dialog = writeDialog.create();
                    dialog.show();
                    cus_gb_read_check_tid.setChecked(true);
                    cus_gb_read_check_user.setChecked(true);
                }
            } else {
                ToastUtils.showText(getResources().getString(R.string.read_card_being));
            }
        } else {
            ToastUtils.showText(getResources().getString(R.string.ununited));
        }
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

    //    @OnClick(R.id.exportExcel)
    public void fab_excel() {
        if (!isReader) {
            List<PermissionItem> permissonItems = new ArrayList<PermissionItem>();
            permissonItems.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "存储", R.drawable.permission_ic_storage));
            HiPermission.create(ReadOrWriteActivity.this)
                    .title("导出excel所需权限")
                    .permissions(permissonItems)
                    .checkMutiPermission(new PermissionCallback() {
                        @Override
                        public void onClose() {
                            Log.e("onClose", "onClose");
                        }

                        @Override
                        public void onFinish() {
                            Log.e("onFinish", "onFinish");
                            String filePath = Environment.getExternalStorageDirectory() + "/Download/";
                            String fileName = "Tag_" + dateFormat.format(new Date()) + ".xls";
                            String[] title = {"Index", "Type", "EPC", "TID", "UserData", "ReservedData", "TotalCount", "ReadTime"};
                            if (tagInfoList.size() > 0) {
                                try {
                                    ExcelUtil.initExcel(filePath, fileName, title);
                                    ExcelUtil.writeObjListToExcel(getRecordData(tagInfoList), filePath + fileName, this);
                                    ToastUtils.showText("Export success " + "Path=" + filePath + fileName);
                                } catch (Exception ex) {
                                    ToastUtils.showText("Export Failed");
                                }
                            } else {
                                ToastUtils.showText("No Data");
                            }
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
        } else {
            ToastUtils.showText(getResources().getString(R.string.read_card_being));
        }
    }


    /**
     * 将数据集合 转化成ArrayList<ArrayList<String>>
     *
     * @return
     */
    private ArrayList<ArrayList<String>> getRecordData(List<TagInfo> infos) {
        ArrayList<ArrayList<String>> recordList = new ArrayList<>();
        for (int i = 0; i < infos.size(); i++) {
            ArrayList<String> beanList = new ArrayList<String>();
            TagInfo info = infos.get(i);
            beanList.add(info.getIndex() + "");
            beanList.add(info.getType());
            beanList.add(info.getEpc() != null ? info.getEpc() : "");
            beanList.add(info.getTid() != null ? info.getTid() : "");
            beanList.add(info.getUserData() != null ? info.getUserData() : "");
            beanList.add(info.getReservedData() != null ? info.getReservedData() : "");
            beanList.add(info.getCount() + "");
            beanList.add(dateFormat.format(info.getReadTime()));
            recordList.add(beanList);
        }
        return recordList;
    }


    final Handler handlerStop = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mHandler.removeCallbacks(r);
                    upDataPane();
                    isReader = false;
                    break;
                case 2:
                    ThreadPoolUtils.run(new Runnable() {
                        @Override
                        public void run() {
                            if (isSound)
                                UtilSound.play(1, 0);
                        }
                    });
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        System.err.println(keyCode);
        Log.e("onKeyDown", keyCode + "");
        switch (keyCode) {
            case 131:
                if (isReader) {
                    stopRead();
                } else {
                    readCard();
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        ToastUtils.showText(keyCode + "");
        Log.e("onKeyUp", keyCode + "");
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalManageUtil.setLocal(newBase));
    }
}
