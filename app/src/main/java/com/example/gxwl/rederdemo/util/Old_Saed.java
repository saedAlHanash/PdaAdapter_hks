//package com.example.gxwl.rederdemo.util;
//
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.os.Handler;
//import android.os.Message;
//
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.support.v7.widget.DividerItemDecoration;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.AdapterView;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.gxwl.rederdemo.AppConfig.SharedPreference;
//import com.example.gxwl.rederdemo.R;
//import com.example.gxwl.rederdemo.SAED.Network.SocketClient;
//import com.example.gxwl.rederdemo.adapter.RecycleViewAdapter;
//import com.example.gxwl.rederdemo.entity.TagInfo;
//import com.example.gxwl.rederdemo.util.GlobalClient;
//import com.example.gxwl.rederdemo.util.LocalManageUtil;
//import com.example.gxwl.rederdemo.util.ToastUtils;
//import com.example.gxwl.rederdemo.util.UtilSound;
//import com.example.gxwl.rederdemo.util.saed;
//import com.gg.reader.api.dal.GClient;
//import com.gg.reader.api.dal.HandlerDebugLog;
//import com.gg.reader.api.dal.HandlerGpiOver;
//import com.gg.reader.api.dal.HandlerGpiStart;
//import com.gg.reader.api.dal.HandlerTag6bLog;
//import com.gg.reader.api.dal.HandlerTag6bOver;
//import com.gg.reader.api.dal.HandlerTagEpcLog;
//import com.gg.reader.api.dal.HandlerTagEpcOver;
//import com.gg.reader.api.dal.HandlerTagGJbLog;
//import com.gg.reader.api.dal.HandlerTagGJbOver;
//import com.gg.reader.api.dal.HandlerTagGbLog;
//import com.gg.reader.api.dal.HandlerTagGbOver;
//import com.gg.reader.api.protocol.gx.EnumG;
//import com.gg.reader.api.protocol.gx.LogAppGpiOver;
//import com.gg.reader.api.protocol.gx.LogAppGpiStart;
//import com.gg.reader.api.protocol.gx.LogBase6bInfo;
//import com.gg.reader.api.protocol.gx.LogBase6bOver;
//import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
//import com.gg.reader.api.protocol.gx.LogBaseEpcOver;
//import com.gg.reader.api.protocol.gx.LogBaseGJbInfo;
//import com.gg.reader.api.protocol.gx.LogBaseGJbOver;
//import com.gg.reader.api.protocol.gx.LogBaseGbInfo;
//import com.gg.reader.api.protocol.gx.LogBaseGbOver;
//import com.gg.reader.api.protocol.gx.MsgBaseInventoryEpc;
//import com.gg.reader.api.protocol.gx.MsgBaseSetBaseband;
//import com.gg.reader.api.protocol.gx.MsgBaseStop;
//import com.gg.reader.api.protocol.gx.Param6bReadUserdata;
//import com.gg.reader.api.protocol.gx.ParamEpcReadReserved;
//import com.gg.reader.api.protocol.gx.ParamEpcReadTid;
//import com.gg.reader.api.protocol.gx.ParamEpcReadUserdata;
//import com.gg.reader.api.utils.ThreadPoolUtils;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//
//@SuppressLint("NonConstantResourceId")
//public class Old_Saed extends AppCompatActivity {
//    // region 组件变量
//
//    @BindView(R.id.read)
//    Button read;
//    @BindView(R.id.stop)
//    Button stop;
//    @BindView(R.id.clean)
//    Button clean;
//    @BindView(R.id.tabHead)
//    LinearLayout tabHead;
//
//    //saed:
//    @BindView(R.id.not_connect)
//    TextView notConnectTv;
//    @BindView(R.id.spinner_tybe_scan)
//    Spinner spinnerTypeScan;
//    @BindView(R.id.scan_type_tv)
//    TextView typeScanTv;
//
//    EditText w_epc;
//    EditText w_tid;
//    EditText w_pas;
//    EditText w_len;
//    EditText w_value;
//    //6c
//    EditText w_user_epc;
//    EditText w_user_tid;
//    EditText w_user_pas;
//    EditText w_user_len;
//    EditText w_user_value;
//    //6b
//    EditText w_6b_user_tid;
//    EditText w_6b_user_start;
//    EditText w_6b_user_value;
//    //自定义写
//    Spinner cus_mode;
//    EditText cus_start;
//    EditText cus_pas;
//    EditText cus_epc;
//    EditText cus_tid;
//    EditText cus_user;
//    //标签信息
//    TextView info_index;
//    TextView info_type;
//    TextView info_epc;
//    TextView info_tid;
//    TextView info_userData;
//    TextView info_Reserved;
//
//    //gb user write
//    EditText w_gb_user_epc;
//    EditText w_gb_user_pas;
//    EditText w_gb_user_tid;
//    EditText w_gb_user_start;
//    Spinner write_gb_user_child;
//    EditText w_gb_user_value;
//    //gb epc write
//    EditText w_gb_epc;
//    EditText w_gb_pas;
//    EditText w_gb_tid;
//    EditText w_gb_len;
//    EditText w_gb_value;
//
//    //自定义读
//    Spinner cus_read_mode;
//    EditText cus_read_start;
//    EditText cus_read_epc;
//    EditText cus_read_tid;
//    EditText cus_read_user;
//    Spinner cus_read_tid_mode;
//    EditText cus_read_tid_len;
//    CheckBox read_tid_true;
//    EditText cus_read_match_content;
//
//    EditText cus_read_user_start;
//    EditText cus_read_user_len;
//    CheckBox read_user_true;
//
//    EditText cus_read_reserve_start;
//    EditText cus_read_reserve_len;
//    CheckBox read_reserve_true;
//
//    CheckBox read_other_pas;
//    EditText cus_read_pas;
//
//    // endregion
//
//    private final GClient client = GlobalClient.getClient();
//    private boolean isClient = false;
//    private final Map<String, TagInfo> tagInfoMap = new LinkedHashMap<String, TagInfo>();//去重数据源
//    private final List<TagInfo> tagInfoList = new ArrayList<TagInfo>();//适配器所需数据源
//    private Long index = 1L;//索引
//    private RecycleViewAdapter adapter;
//    private ParamEpcReadTid tidParam = null;
//    private ParamEpcReadUserdata userParam = null;
//    private ParamEpcReadReserved reserveParam = null;
//    private Param6bReadUserdata user6bParam = null;
//    private final boolean[] isChecked = new boolean[]{false, false, false};//标识读0-epc与1-user
//    private Handler mHandler = new Handler();
//    private Runnable r = null;
//    private final int time = 0;
//    private boolean isSound = false;
//    private boolean isReader = false;
//
//    //saed :
//    public SocketClient socketClient = new SocketClient();
//    /**
//     * to checking if can reConnect with socket <p>
//     * will be false when onDestroy Activity
//     */
//    public boolean tryConnect = true;
//    /**
//     * socket ip address
//     */
//    public String ip;
//    /**
//     * socket port
//     */
//    public int port;
//
//    int typeScan;
//    private static final int SINGLE = 0;
//
//
//    private int isCtesius = 0;
//
//    long rateValue = 0L;
//
//
//    private Handler soundHandler = new Handler();
//
//    private Runnable timeTask = null;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        ButterKnife.bind(this);
//
//        isClient = getIntent().getBooleanExtra("isClient", false);
//
//        ip = SharedPreference.getIp().replaceAll("\\s+", "");
//        port = SharedPreference.getPort();
//
//        initSocket(ip, port);
//
//        listeners();
//
//        if (isClient) {
//            subHandler(GlobalClient.getClient());
//        }
//
//        UtilSound.initSoundPool(this);
//        initRecycleView();
//
//    }
//
//    //saed :
//    void listeners() {
//        typeScanTv.setOnClickListener(view -> {
//            spinnerTypeScan.performClick();
//        });
//
//        spinnerTypeScan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                typeScan = i;
//
//                if (i == 0)
//                    typeScanTv.setText(getResources().getString(R.string.single));
//
//                else
//                    typeScanTv.setText(getResources().getString(R.string.loop));
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
//    }
//
//    /**
//     * start connection with socket
//     *
//     * @param mIp   socket ip address
//     * @param mPort socket port
//     */
//    public void initSocket(String mIp, int mPort) {
//        new Thread(() -> { //لانه لا يمكن الاتصال من ال UI thread
//            while (tryConnect) { // من أجل المحاولة والمحاولة حتى تمام عملية الاتصال
//
//                //اذا الاتصال تم
//                if (socketClient.connect(mIp, mPort)) {
//                    //اخفاء noConnect textView
//                    runOnUiThread(() -> notConnectTv.setVisibility(View.GONE));
//                    break;
//                } else // اذا لم يتصل
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//            }
//
//        }).start();
//
//        //call back active when connect stat change
//        socketClient.setOnChangeConnectStatListener(connect -> {
//            runOnUiThread(() -> {
//                if (connect)
//                    notConnectTv.setVisibility(View.GONE);
//                else
//                    notConnectTv.setVisibility(View.VISIBLE);
//            });
//        });
//    }
//
//    //初始化RecycleView
//    public void initRecycleView() {
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        RecyclerView rv = (RecyclerView) findViewById(R.id.recycle);
//        rv.setLayoutManager(layoutManager);
//        rv.addItemDecoration(new DividerItemDecoration(this, 1));
//        adapter = new RecycleViewAdapter(tagInfoList, this);
//        rv.setAdapter(adapter);
//
//    }
//
//    //订阅
//
//    public void subHandler(GClient paramGClient) {
//        paramGClient.onTagEpcLog = new HandlerTagEpcLog() {
//            public void log(String param1String, LogBaseEpcInfo info) {
//                Toast.makeText(Old_Saed.this, "", Toast.LENGTH_SHORT).show();
//                if (info.getResult() == 0)
//                    synchronized (tagInfoList) {
//                        Map<String, TagInfo> infoMap = pooled6cData(info);
//
//                        Toast.makeText(Old_Saed.this,
//                                "size = " + infoMap.size(), Toast.LENGTH_SHORT).show();
//
//                        int oldSiz = tagInfoList.size();
//
//                        tagInfoList.clear();
//                        tagInfoList.addAll(infoMap.values());
//
//                        if (tagInfoList.size() != oldSiz)
//                            upDataPane();
//
//                    }
//            }
//        };
//        paramGClient.onTagEpcOver = new HandlerTagEpcOver() {
//            public void log(String param1String, LogBaseEpcOver param1LogBaseEpcOver) {
//                handlerStop.sendEmptyMessage(1);
//            }
//        };
//        paramGClient.onTag6bLog = new HandlerTag6bLog() {
//            public void log(String param1String, LogBase6bInfo param1LogBase6bInfo) {
//                if (param1LogBase6bInfo != null && param1LogBase6bInfo.getResult() == 0)
//                    synchronized (tagInfoList) {
//                        pooled6bData(param1LogBase6bInfo);
//                    }
//            }
//        };
//        paramGClient.onTag6bOver = new HandlerTag6bOver() {
//            public void log(String param1String, LogBase6bOver param1LogBase6bOver) {
//                handlerStop.sendEmptyMessage(1);
//            }
//        };
//        paramGClient.onTagGbLog = new HandlerTagGbLog() {
//            public void log(String param1String, LogBaseGbInfo param1LogBaseGbInfo) {
//                if (param1LogBaseGbInfo != null && param1LogBaseGbInfo.getResult() == 0)
//                    synchronized (tagInfoList) {
//                        pooledGbData(param1LogBaseGbInfo);
//                    }
//            }
//        };
//        paramGClient.onTagGbOver = new HandlerTagGbOver() {
//            public void log(String param1String, LogBaseGbOver param1LogBaseGbOver) {
//                Log.e("HandlerTagGbOver", "-------------HandlerTagGbOver");
//                handlerStop.sendEmptyMessage(1);
//            }
//        };
//        paramGClient.onTagGJbLog = new HandlerTagGJbLog() {
//            public void log(String param1String, LogBaseGJbInfo param1LogBaseGJbInfo) {
//                if (param1LogBaseGJbInfo.getResult() == 0)
//                    synchronized (tagInfoList) {
//                        pooledGJbData(param1LogBaseGJbInfo);
//                    }
//            }
//        };
//        paramGClient.onTagGJbOver = new HandlerTagGJbOver() {
//            public void log(String param1String, LogBaseGJbOver param1LogBaseGJbOver) {
//                Log.e("HandlerTagGJbOver", "-------------HandlerTagGJbOver");
//                handlerStop.sendEmptyMessage(1);
//            }
//        };
//        paramGClient.onGpiOver = new HandlerGpiOver() {
//            public void log(String param1String, LogAppGpiOver param1LogAppGpiOver) {
//                System.out.println(param1LogAppGpiOver);
//            }
//        };
//        paramGClient.onGpiStart = new HandlerGpiStart() {
//            public void log(String param1String, LogAppGpiStart param1LogAppGpiStart) {
//                System.out.println(param1LogAppGpiStart);
//            }
//        };
//        paramGClient.debugLog = new HandlerDebugLog() {
//            public void receiveDebugLog(String param1String) {
//                Log.e("receiveDebugLog", param1String);
//            }
//
//            public void sendDebugLog(String param1String) {
//                Log.e("sendDebugLog", param1String);
//            }
//        };
//    }
//
//    //读卡
//    @OnClick(R.id.read)
//    public void readCard() {
//        if (isClient) {
//            if (!isReader) {
//
//                //   if (type.getCheckedRadioButtonId() == R.id.c) {
//                MsgBaseInventoryEpc msg = new MsgBaseInventoryEpc();
//                msg.setAntennaEnable(EnumG.AntennaNo_1);
//
//                if (typeScan == SINGLE) {
//                    msg.setInventoryMode(EnumG.InventoryMode_Single);
//                } else {
//                    msg.setInventoryMode(EnumG.InventoryMode_Inventory);
//                }
//                if (this.isChecked[0]) {
//                    ParamEpcReadTid paramEpcReadTid = new ParamEpcReadTid();
//                    this.tidParam = paramEpcReadTid;
//                    paramEpcReadTid.setMode(0);
//                    this.tidParam.setLen(6);
//                    msg.setReadTid(this.tidParam);
//                }
//                if (this.isChecked[1]) {
//                    ParamEpcReadUserdata paramEpcReadUserdata = new ParamEpcReadUserdata();
//                    this.userParam = paramEpcReadUserdata;
//                    paramEpcReadUserdata.setStart(0);
//                    this.userParam.setLen(2);
//                    msg.setReadUserdata(this.userParam);
//                }
//                if (this.isChecked[2]) {
//                    ParamEpcReadReserved paramEpcReadReserved = new ParamEpcReadReserved();
//                    this.reserveParam = paramEpcReadReserved;
//                    paramEpcReadReserved.setStart(0);
//                    this.reserveParam.setLen(4);
//                    msg.setReadReserved(this.reserveParam);
//                }
//
//                client.sendSynMsg(msg);
//
//                if (0x00 == msg.getRtCode()) {
//                    ToastUtils.showText("Start ReadCard");
//                    isReader = true;
//                    soundTask();
//                } else {
//                    handlerStop.sendEmptyMessage(1);
//                    ToastUtils.showText(msg.getRtMsg());
//                }
//
//            } else {
//                ToastUtils.showText(getResources().getString(R.string.read_card_being));
//            }
//        } else {
//            ToastUtils.showText(getResources().getString(R.string.ununited));
//        }
//    }
//
//    //停止
//    @OnClick(R.id.stop)
//    public void stopRead() {
//        if (this.isClient) {
//            MsgBaseStop msgBaseStop = new MsgBaseStop();
//            this.client.sendSynMsg(msgBaseStop);
//
//            if (msgBaseStop.getRtCode() == 0) {
//                this.isReader = false;
//                ToastUtils.showText("Stop Success");
//            } else {
//                ToastUtils.showText("Stop Fail");
//            }
//        } else {
//            ToastUtils.showText(getResources().getString(R.string.ununited));
//        }
//    }
//
//    //清屏
//    @OnClick(R.id.clean)
//    public void cleanData() {
//        if (isClient) {
//            tagInfoList.clear();
//            adapter.notifyData(tagInfoList);
////            initPane();
//        } else {
//            ToastUtils.showText(getResources().getString(R.string.ununited));
//        }
//    }
//
//    @Override
//    protected void attachBaseContext(Context paramContext) {
//        super.attachBaseContext(LocalManageUtil.setLocal(paramContext));
//    }
//
//    public Map<String, TagInfo> pooled6bData(LogBase6bInfo paramLogBase6bInfo) {
//        if (this.tagInfoMap.containsKey(paramLogBase6bInfo.getTid())) {
//            TagInfo tagInfo = this.tagInfoMap.get(paramLogBase6bInfo.getTid());
//            long l = ((TagInfo) this.tagInfoMap.get(paramLogBase6bInfo.getTid())).getCount().longValue();
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append(paramLogBase6bInfo.getRssi());
//            stringBuilder.append("");
//            tagInfo.setRssi(stringBuilder.toString());
//            tagInfo.setCount(Long.valueOf(l + 1L));
//            this.tagInfoMap.put(paramLogBase6bInfo.getTid(), tagInfo);
//        } else {
//            TagInfo tagInfo = new TagInfo();
//            tagInfo.setIndex(this.index);
//            tagInfo.setType("6B");
//            tagInfo.setCount(Long.valueOf(1L));
//            tagInfo.setUserData(paramLogBase6bInfo.getUserdata());
//            if (paramLogBase6bInfo.getTid() != null)
//                tagInfo.setTid(paramLogBase6bInfo.getTid());
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append(paramLogBase6bInfo.getRssi());
//            stringBuilder.append("");
//            tagInfo.setRssi(stringBuilder.toString());
//            tagInfo.setReadTime(new Date());
//            this.tagInfoMap.put(paramLogBase6bInfo.getTid(), tagInfo);
//            this.index = Long.valueOf(this.index.longValue() + 1L);
//        }
//        return this.tagInfoMap;
//    }
//
//    public Map<String, TagInfo> pooled6cData(LogBaseEpcInfo paramLogBaseEpcInfo) {
//        Map<String, TagInfo> map = this.tagInfoMap;
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(paramLogBaseEpcInfo.getTid());
//        stringBuilder.append(paramLogBaseEpcInfo.getEpc());
//        if (map.containsKey(stringBuilder.toString())) {
//            Map<String, TagInfo> map1 = this.tagInfoMap;
//            StringBuilder stringBuilder1 = new StringBuilder();
//            stringBuilder1.append(paramLogBaseEpcInfo.getTid());
//            stringBuilder1.append(paramLogBaseEpcInfo.getEpc());
//            TagInfo tagInfo = map1.get(stringBuilder1.toString());
//            Map<String, TagInfo> map2 = this.tagInfoMap;
//            StringBuilder stringBuilder2 = new StringBuilder();
//            stringBuilder2.append(paramLogBaseEpcInfo.getTid());
//            stringBuilder2.append(paramLogBaseEpcInfo.getEpc());
//            long l = ((TagInfo) map2.get(stringBuilder2.toString())).getCount().longValue();
//            stringBuilder2 = new StringBuilder();
//            stringBuilder2.append(paramLogBaseEpcInfo.getRssi());
//            stringBuilder2.append("");
//            tagInfo.setRssi(stringBuilder2.toString());
//            tagInfo.setReservedData(paramLogBaseEpcInfo.getReserved());
//            tagInfo.setUserData(paramLogBaseEpcInfo.getUserdata());
//            tagInfo.setCount(Long.valueOf(l + 1L));
//            map2 = this.tagInfoMap;
//            stringBuilder2 = new StringBuilder();
//            stringBuilder2.append(paramLogBaseEpcInfo.getTid());
//            stringBuilder2.append(paramLogBaseEpcInfo.getEpc());
//            map2.put(stringBuilder2.toString(), tagInfo);
//        } else {
//            TagInfo tagInfo = new TagInfo();
//            tagInfo.setIndex(this.index);
//            tagInfo.setType("6C");
//            tagInfo.setEpc(paramLogBaseEpcInfo.getEpc());
//            tagInfo.setCount(Long.valueOf(1L));
//            tagInfo.setUserData(paramLogBaseEpcInfo.getUserdata());
//            tagInfo.setReservedData(paramLogBaseEpcInfo.getReserved());
//            tagInfo.setTid(paramLogBaseEpcInfo.getTid());
//            stringBuilder = new StringBuilder();
//            stringBuilder.append(paramLogBaseEpcInfo.getRssi());
//            stringBuilder.append("");
//            tagInfo.setRssi(stringBuilder.toString());
//            tagInfo.setReadTime(new Date());
//            Map<String, TagInfo> map1 = this.tagInfoMap;
//            StringBuilder stringBuilder1 = new StringBuilder();
//            stringBuilder1.append(paramLogBaseEpcInfo.getTid());
//            stringBuilder1.append(paramLogBaseEpcInfo.getEpc());
//            map1.put(stringBuilder1.toString(), tagInfo);
//            this.index = Long.valueOf(this.index.longValue() + 1L);
//        }
//        return this.tagInfoMap;
//    }
//
//    public Map<String, TagInfo> pooledGJbData(LogBaseGJbInfo paramLogBaseGJbInfo) {
//        Map<String, TagInfo> map = this.tagInfoMap;
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(paramLogBaseGJbInfo.getTid());
//        stringBuilder.append(paramLogBaseGJbInfo.getEpc());
//        if (map.containsKey(stringBuilder.toString())) {
//            map = this.tagInfoMap;
//            stringBuilder = new StringBuilder();
//            stringBuilder.append(paramLogBaseGJbInfo.getTid());
//            stringBuilder.append(paramLogBaseGJbInfo.getEpc());
//            TagInfo tagInfo = map.get(stringBuilder.toString());
//            Map<String, TagInfo> map2 = this.tagInfoMap;
//            StringBuilder stringBuilder2 = new StringBuilder();
//            stringBuilder2.append(paramLogBaseGJbInfo.getTid());
//            stringBuilder2.append(paramLogBaseGJbInfo.getEpc());
//            long l = ((TagInfo) map2.get(stringBuilder2.toString())).getCount().longValue();
//            StringBuilder stringBuilder1 = new StringBuilder();
//            stringBuilder1.append(paramLogBaseGJbInfo.getRssi());
//            stringBuilder1.append("");
//            tagInfo.setRssi(stringBuilder1.toString());
//            tagInfo.setCount(Long.valueOf(l + 1L));
//            Map<String, TagInfo> map1 = this.tagInfoMap;
//            stringBuilder2 = new StringBuilder();
//            stringBuilder2.append(paramLogBaseGJbInfo.getTid());
//            stringBuilder2.append(paramLogBaseGJbInfo.getEpc());
//            map1.put(stringBuilder2.toString(), tagInfo);
//        } else {
//            TagInfo tagInfo = new TagInfo();
//            tagInfo.setIndex(this.index);
//            tagInfo.setType("GJB");
//            tagInfo.setEpc(paramLogBaseGJbInfo.getEpc());
//            tagInfo.setCount(Long.valueOf(1L));
//            tagInfo.setUserData(paramLogBaseGJbInfo.getUserdata());
//            tagInfo.setTid(paramLogBaseGJbInfo.getTid());
//            stringBuilder = new StringBuilder();
//            stringBuilder.append(paramLogBaseGJbInfo.getRssi());
//            stringBuilder.append("");
//            tagInfo.setRssi(stringBuilder.toString());
//            tagInfo.setReadTime(new Date());
//            Map<String, TagInfo> map1 = this.tagInfoMap;
//            StringBuilder stringBuilder1 = new StringBuilder();
//            stringBuilder1.append(paramLogBaseGJbInfo.getTid());
//            stringBuilder1.append(paramLogBaseGJbInfo.getEpc());
//            map1.put(stringBuilder1.toString(), tagInfo);
//            this.index = Long.valueOf(this.index.longValue() + 1L);
//        }
//        return this.tagInfoMap;
//    }
//
//    public Map<String, TagInfo> pooledGbData(LogBaseGbInfo paramLogBaseGbInfo) {
//        Map<String, TagInfo> map = this.tagInfoMap;
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(paramLogBaseGbInfo.getTid());
//        stringBuilder.append(paramLogBaseGbInfo.getEpc());
//        if (map.containsKey(stringBuilder.toString())) {
//            map = this.tagInfoMap;
//            stringBuilder = new StringBuilder();
//            stringBuilder.append(paramLogBaseGbInfo.getTid());
//            stringBuilder.append(paramLogBaseGbInfo.getEpc());
//            TagInfo tagInfo = map.get(stringBuilder.toString());
//            map = this.tagInfoMap;
//            StringBuilder stringBuilder2 = new StringBuilder();
//            stringBuilder2.append(paramLogBaseGbInfo.getTid());
//            stringBuilder2.append(paramLogBaseGbInfo.getEpc());
//            long l = ((TagInfo) map.get(stringBuilder2.toString())).getCount().longValue();
//            StringBuilder stringBuilder1 = new StringBuilder();
//            stringBuilder1.append(paramLogBaseGbInfo.getRssi());
//            stringBuilder1.append("");
//            tagInfo.setRssi(stringBuilder1.toString());
//            tagInfo.setCount(Long.valueOf(l + 1L));
//            Map<String, TagInfo> map1 = this.tagInfoMap;
//            stringBuilder1 = new StringBuilder();
//            stringBuilder1.append(paramLogBaseGbInfo.getTid());
//            stringBuilder1.append(paramLogBaseGbInfo.getEpc());
//            map1.put(stringBuilder1.toString(), tagInfo);
//        } else {
//            TagInfo tagInfo = new TagInfo();
//            tagInfo.setIndex(this.index);
//            tagInfo.setType("GB");
//            tagInfo.setEpc(paramLogBaseGbInfo.getEpc());
//            tagInfo.setCount(Long.valueOf(1L));
//            tagInfo.setUserData(paramLogBaseGbInfo.getUserdata());
//            tagInfo.setTid(paramLogBaseGbInfo.getTid());
//            StringBuilder stringBuilder1 = new StringBuilder();
//            stringBuilder1.append(paramLogBaseGbInfo.getRssi());
//            stringBuilder1.append("");
//            tagInfo.setRssi(stringBuilder1.toString());
//            tagInfo.setReadTime(new Date());
//            Map<String, TagInfo> map1 = this.tagInfoMap;
//            stringBuilder1 = new StringBuilder();
//            stringBuilder1.append(paramLogBaseGbInfo.getTid());
//            stringBuilder1.append(paramLogBaseGbInfo.getEpc());
//            map1.put(stringBuilder1.toString(), tagInfo);
//            this.index = Long.valueOf(this.index.longValue() + 1L);
//        }
//        return this.tagInfoMap;
//    }
//
//
//    public double computedNmv2d(String paramString1, String paramString2) {
//        int i;
//        String str = paramString1.substring(0, 2);
//        paramString1 = paramString1.substring(2, 4);
//        if (str.contains("1")) {
//            i = -Integer.parseInt(paramString1, 16);
//        } else {
//            i = Integer.parseInt(paramString1, 16);
//        }
//        double d = (Integer.parseInt(paramString2.substring(1, 4), 16) + i - 500);
//        Double.isNaN(d);
//        return d / 5.4817D + 24.9D;
//    }
//
//    //程序退出
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        isSound = false;
//
//        if (isClient) {
//            if (isReader) {
//                MsgBaseStop msgStop = new MsgBaseStop();
//                client.sendSynMsg(msgStop);
//                if (msgStop.getRtCode() == 0) {
//                    ToastUtils.showText(getResources().getString(R.string.stop_card));
//                } else {
//                    ToastUtils.showText(msgStop.getRtMsg());
//                }
//            }
//        }
//
//        this.tryConnect = false;
//
//        adapter.threadKill = false;
//        adapter.thread.interrupt();
//
//        socketClient.close();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (this.isClient && this.isReader) {
//            MsgBaseStop msgBaseStop = new MsgBaseStop();
//            this.client.sendSynMsg(msgBaseStop);
//            if (msgBaseStop.getRtCode() == 0) {
//                this.isReader = false;
////                ToastUtils.showText(getResources().getString(2131755400));
//            } else {
//                ToastUtils.showText(msgBaseStop.getRtMsg());
//            }
//        }
//    }
//
////    @OnClick(R.id.tabHead)
////    public void getTabHead() {
////        AlertDialog dialog = new AlertDialog.Builder(this)
////                .setTitle(getResources().getString(R.string.select_type))
////                .setMultiChoiceItems(new String[]{
////                                getResources().getString(R.string.select_tid),
////                                getResources().getString(R.string.select_user),
////                                getResources().getString(R.string.select_reserve)},
////                        isChecked,
////                        (dialog1, which, isChecked) -> {
////                            System.out.println(which + "--" + isChecked);
////                        })
////                .setCancelable(false)
////                .setPositiveButton("confirm", new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialog, int which) {
////                    }
////                })
////                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialog, int which) {
////                        dialog.dismiss();
////                    }
////                })
////                .show();
////        //修改“确认”、“取消”按钮的字体大小
////        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(23);
////        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(23);
////        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.rgb(0, 87, 75));
////        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.rgb(0, 87, 75));
////        try {
////            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
////            mAlert.setAccessible(true);
////            Object mAlertController = mAlert.get(dialog);
////            //通过反射修改title字体大小和颜色
////            Field mTitle = mAlertController.getClass().getDeclaredField("mTitleView");
////            mTitle.setAccessible(true);
////            TextView mTitleView = (TextView) mTitle.get(mAlertController);
////            mTitleView.setTextSize(23);
////            mTitleView.setTextColor(Color.rgb(0, 87, 75));
////        } catch (IllegalAccessException e1) {
////            e1.printStackTrace();
////        } catch (NoSuchFieldException e2) {
////            e2.printStackTrace();
////        }
////    }
//
//
//    private void upDataPane() {
//        adapter.notifyData(tagInfoList);
//    }
//
//    @SuppressLint("HandlerLeak")
//    final Handler handlerStop = new Handler() {
//        public void handleMessage(Message param1Message) {
//            if (param1Message.what == 1) {
//                soundHandler.removeCallbacks(timeTask);
//                upDataPane();
//            }
//            super.handleMessage(param1Message);
//        }
//    };
//
//    @Override
//    public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(paramInt);
//        stringBuilder.append("");
//        Log.e("onKeyDown", stringBuilder.toString());
//        if (paramInt == 131 || paramInt == 285 || paramInt == 289 || paramInt == 291)
//            if (this.isReader) {
//                stopRead();
//            } else {
//                readCard();
//            }
//        return super.onKeyDown(paramInt, paramKeyEvent);
//    }
//
//    @Override
//    public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent) {
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(paramInt);
//        stringBuilder.append("");
//        Log.e("onKeyUp", stringBuilder.toString());
//        return super.onKeyUp(paramInt, paramKeyEvent);
//    }
//
//    private long getReadCount(List<TagInfo> paramList) {
//        long l = 0L;
//        for (byte b = 0; b < paramList.size(); b++)
//            l += ((TagInfo) paramList.get(b)).getCount().longValue();
//        return l;
//    }
//
//    private void soundTask() {
//        Runnable runnable = new Runnable() {
//            public void run() {
//                if (rateValue != 0L)
//
//                    UtilSound.play(1, 0);
//                soundHandler.postDelayed(this, 20L);
//            }
//        };
//
//        this.timeTask = runnable;
//        this.soundHandler.postDelayed(runnable, 0L);
//
//    }
//
//
//    // @Override
//    // protected void attachBaseContext(Context newBase) {
//    //      super.attachBaseContext(LocalManageUtil.setLocal(newBase));
//    //   }
//}
