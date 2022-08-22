package com.example.gxwl.rederdemo;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gxwl.rederdemo.AppConfig.SharedPreference;
import com.example.gxwl.rederdemo.Helpers.Converters.GzipConverter;
import com.example.gxwl.rederdemo.Helpers.Images.ConverterImage;
import com.example.gxwl.rederdemo.SAED.Network.SaedSocket;

import com.example.gxwl.rederdemo.SAED.Product;
import com.example.gxwl.rederdemo.adapter.AdapterItemEpc;

import com.example.gxwl.rederdemo.entity.TagInfo;
import com.example.gxwl.rederdemo.util.GlobalClient;
import com.example.gxwl.rederdemo.util.ToastUtils;
import com.example.gxwl.rederdemo.util.UtilSound;
import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.dal.HandlerDebugLog;
import com.gg.reader.api.dal.HandlerGpiOver;
import com.gg.reader.api.dal.HandlerGpiStart;
import com.gg.reader.api.dal.HandlerTag6bLog;
import com.gg.reader.api.dal.HandlerTag6bOver;
import com.gg.reader.api.dal.HandlerTagEpcLog;
import com.gg.reader.api.dal.HandlerTagEpcOver;
import com.gg.reader.api.dal.HandlerTagGJbLog;
import com.gg.reader.api.dal.HandlerTagGJbOver;
import com.gg.reader.api.dal.HandlerTagGbLog;
import com.gg.reader.api.dal.HandlerTagGbOver;
import com.gg.reader.api.protocol.gx.EnumG;
import com.gg.reader.api.protocol.gx.LogAppGpiOver;
import com.gg.reader.api.protocol.gx.LogAppGpiStart;
import com.gg.reader.api.protocol.gx.LogBase6bInfo;
import com.gg.reader.api.protocol.gx.LogBase6bOver;
import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
import com.gg.reader.api.protocol.gx.LogBaseEpcOver;
import com.gg.reader.api.protocol.gx.LogBaseGJbInfo;
import com.gg.reader.api.protocol.gx.LogBaseGJbOver;
import com.gg.reader.api.protocol.gx.LogBaseGbInfo;
import com.gg.reader.api.protocol.gx.LogBaseGbOver;
import com.gg.reader.api.protocol.gx.MsgBaseInventoryEpc;
import com.gg.reader.api.protocol.gx.MsgBaseStop;
import com.gg.reader.api.protocol.gx.Param6bReadUserdata;
import com.gg.reader.api.protocol.gx.ParamEpcReadReserved;
import com.gg.reader.api.protocol.gx.ParamEpcReadTid;
import com.gg.reader.api.protocol.gx.ParamEpcReadUserdata;
import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class ReadOrWriteActivity extends AppCompatActivity {
    private static final String TAG = "ReadOrWriteActivity_";

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


    // endregion

    private final GClient client = GlobalClient.getClient();
    private boolean isClient = false;
    private final Map<String, TagInfo> tagInfoMap = new LinkedHashMap<String, TagInfo>();//去重数据源
    private final List<TagInfo> tagInfoList = new ArrayList<TagInfo>();//适配器所需数据源
    private Long index = 1L;//索引
    //    private RecycleViewAdapter adapter;
    private ParamEpcReadTid tidParam = null;
    private ParamEpcReadUserdata userParam = null;
    private ParamEpcReadReserved reserveParam = null;
    private Param6bReadUserdata user6bParam = null;
    private final boolean[] isChecked = new boolean[]{false, false, false};//标识读0-epc与1-user
    private Handler mHandler = new Handler();
    private Runnable r = null;
    private boolean isReader = false;
    private Runnable timeTask = null;
    private final Handler soundHandler = new Handler();
    private long rateValue = 1L;

    //saed :
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
    URI uri;

    AdapterItemEpc adapter;

    ArrayList<Product> list = new ArrayList<>();

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

        if (isClient)
            subHandler(GlobalClient.getClient());

        initAdapter();

        UtilSound.initSoundPool(this);
    }

    WebSocketClient webSocketClient;
    SaedSocket socket;
    Gson gson = new Gson();
    int lastIndex = 0;
    Runnable runnable;

    private void computedSpeed() {
        runnable = () -> {

            int siz = tagInfoList.size();

            tagInfoList.clear();
            tagInfoList.addAll(tagInfoMap.values());

            if (siz < tagInfoList.size())
                for (int i = lastIndex; i < tagInfoList.size(); i++) {
                    socket.send(tagInfoList.get(i).getEpc());
                    lastIndex += 1;
                }

            mHandler.postDelayed(runnable, 1000L);///سعيد
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

        try {
            uri = new URI("ws://" + mIp + ":" + mPort);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake h) {
                Log.d(TAG, "onOpen: ");
                handler.sendEmptyMessage(201);
            }

            @Override
            public void onMessage(String message) {

                Log.d(TAG, "onMessage: siz" + message.length());
                Log.d(TAG, "onMessage: " + message);

                if (message.trim().equals("404")) {
                    handler.sendEmptyMessage(404);
                    return;
                }

                Product mm = gson.fromJson(message, Product.class);
                mm.bitmap = ConverterImage.convertBase64ToBitmap(mm.im);

                list.add(mm);
                handler.sendEmptyMessage(200);

            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d(TAG, "onClose: ");
                handler.sendEmptyMessage(202);
            }

            @Override
            public void onError(Exception ex) {
                Log.e(TAG, "onError: ", ex);
                handler.sendEmptyMessage(500);
            }

            @Override
            public void onMessage(ByteBuffer bytes) {

                byte[] bytes1 = bytes.array();
                System.out.println(bytes1.length);
                String message = null;

                try {
                    message = GzipConverter.decompress(bytes1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (message == null) {
                    handler.sendEmptyMessage(500);
                    return;
                }

                if (message.trim().equals("404")) {
                    handler.sendEmptyMessage(404);
                    return;
                }

                Product mm = gson.fromJson(message, Product.class);
                mm.bitmap = ConverterImage.convertBase64ToBitmap(mm.im);

                list.add(mm);

                handler.sendEmptyMessage(200);
            }
        };

        socket = new SaedSocket(webSocketClient);
    }


    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            switch (msg.what) {
                case 404:
                    Toast.makeText(ReadOrWriteActivity.this,
                            "not found", Toast.LENGTH_SHORT).show();
                    break;

                case 500:
                    Toast.makeText(ReadOrWriteActivity.this,
                            "have error", Toast.LENGTH_SHORT).show();
                    break;

                case 200:
                    adapter.notifyDataSetChanged();
                    break;

                case 201:
                    notConnectTv.setVisibility(View.GONE);
                    break;

                case 202:
                    notConnectTv.setVisibility(View.VISIBLE);
                    break;
            }

        }
    };

    void initAdapter() {
        if (adapter == null)
            adapter = new AdapterItemEpc(this, list);
        else
            adapter.setAndRefresh(list);

        initRecycler();
    }

    void initRecycler() {
        RecyclerView rv = (RecyclerView) findViewById(R.id.recycle);
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        rv.setAdapter(adapter);
    }

    //读卡
    @OnClick(R.id.read)
    public void readCard() {
        if (isClient) {
            if (!isReader) {

                MsgBaseInventoryEpc msg = new MsgBaseInventoryEpc();
                msg.setAntennaEnable(EnumG.AntennaNo_1 | EnumG.AntennaNo_2);

                msg.setInventoryMode(EnumG.InventoryMode_Inventory);

//                if (isChecked[0]) {
//                    tidParam = new ParamEpcReadTid();
//                    tidParam.setMode(EnumG.ParamTidMode_Auto);
//                    tidParam.setLen(6);
//                    msg.setReadTid(tidParam);
//                }
//                if (isChecked[1]) {
//                    userParam = new ParamEpcReadUserdata();
//                    userParam.setStart(0);
//                    userParam.setLen(6);
//                    msg.setReadUserdata(userParam);
//                }
//                if (isChecked[2]) {
//                    reserveParam = new ParamEpcReadReserved();
//                    reserveParam.setStart(0);
//                    reserveParam.setLen(4);
//                    msg.setReadReserved(reserveParam);
//                }
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

    //停止
    @OnClick(R.id.stop)
    public void stopRead() {
        if (isClient) {
            MsgBaseStop msgStop = new MsgBaseStop();
            client.sendSynMsg(msgStop);
            if (0x00 == msgStop.getRtCode()) {
                isReader = false;
                ToastUtils.showText("Stop Success");
            } else
                ToastUtils.showText("Stop Fail");

        } else
            ToastUtils.showText(getResources().getString(R.string.ununited));

    }

    //清屏
    @OnClick(R.id.clean)
    public void cleanData() {
        if (isClient) {
            tagInfoList.clear();
//            adapter.notifyData(tagInfoList);
//            initPane();
        } else {
            ToastUtils.showText(getResources().getString(R.string.ununited));
        }
    }

    //订阅
    public void subHandler(GClient client) {

        client.onTagEpcLog = (readerName, info) -> {
            if (info.getResult() == 0) {
                Log.d("SAED_", "log: " + info.getEpc());
                pooled6cData(info);
            }
        };

        client.onTagEpcOver = new HandlerTagEpcOver() {
            public void log(String param1String, LogBaseEpcOver param1LogBaseEpcOver) {
                handlerStop.sendEmptyMessage(1);
            }
        };

//        client.onTag6bLog = new HandlerTag6bLog() {
//            public void log(String param1String, LogBase6bInfo param1LogBase6bInfo) {
//                if (param1LogBase6bInfo != null && param1LogBase6bInfo.getResult() == 0)
//                    synchronized (tagInfoList) {
//                        Log.d("SAED_", "log: " + "6b");
//                        pooled6bData(param1LogBase6bInfo);
//                    }
//            }
//        };
//        client.onTag6bOver = new HandlerTag6bOver() {
//            public void log(String param1String, LogBase6bOver param1LogBase6bOver) {
//                handlerStop.sendEmptyMessage(1);
//            }
//        };
//        client.onTagGbLog = new HandlerTagGbLog() {
//            public void log(String param1String, LogBaseGbInfo param1LogBaseGbInfo) {
//                if (param1LogBaseGbInfo != null && param1LogBaseGbInfo.getResult() == 0)
//                    synchronized (tagInfoList) {
//                        Log.d("SAED_", "log: " + "gb");
//                        pooledGbData(param1LogBaseGbInfo);
//                    }
//            }
//        };
//        client.onTagGbOver = new HandlerTagGbOver() {
//            public void log(String param1String, LogBaseGbOver param1LogBaseGbOver) {
//                Log.e("HandlerTagGbOver", "-------------HandlerTagGbOver");
//                handlerStop.sendEmptyMessage(1);
//            }
//        };
//        client.onTagGJbLog = new HandlerTagGJbLog() {
//            public void log(String param1String, LogBaseGJbInfo param1LogBaseGJbInfo) {
//                if (param1LogBaseGJbInfo.getResult() == 0)
//                    synchronized (tagInfoList) {
//                        Log.d("SAED_", "log: " + "6jb");
//                        pooledGJbData(param1LogBaseGJbInfo);
//                    }
//            }
//        };
//        client.onTagGJbOver = new HandlerTagGJbOver() {
//            public void log(String param1String, LogBaseGJbOver param1LogBaseGJbOver) {
//                Log.e("HandlerTagGJbOver", "-------------HandlerTagGJbOver");
//                handlerStop.sendEmptyMessage(1);
//            }
//        };
//        client.onGpiOver = new HandlerGpiOver() {
//            public void log(String param1String, LogAppGpiOver param1LogAppGpiOver) {
//                Log.d("SAED_", "log: " + "gpi");
//                System.out.println(param1LogAppGpiOver);
//            }
//        };
//        client.onGpiStart = new HandlerGpiStart() {
//            public void log(String param1String, LogAppGpiStart param1LogAppGpiStart) {
//                System.out.println(param1LogAppGpiStart);
//            }
//        };
//        client.debugLog = new HandlerDebugLog() {
//            public void receiveDebugLog(String param1String) {
//                Log.e("receiveDebugLog", param1String);
//            }
//
//            public void sendDebugLog(String param1String) {
//                Log.e("sendDebugLog", param1String);
//            }
//        };

//        client.onTagEpcLog = (readerName, info) -> {
//            if (null != info && 0 == info.getResult()) {
//                ReadOrWriteActivity.this.runOnUiThread(() -> {
//                    synchronized (tagInfoList) {
//                        pooled6cData(info);
//                    }
//                });
//            }
//        };

//        client.onTagEpcOver = (readerName, info) -> {
//            handlerStop.sendEmptyMessage(new Message().what = 1);
//        };

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

    //去重6C
    public Map<String, TagInfo> pooled6cData(LogBaseEpcInfo paramLogBaseEpcInfo) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(paramLogBaseEpcInfo.getTid());
        stringBuilder.append(paramLogBaseEpcInfo.getEpc());

        if (this.tagInfoMap.containsKey(stringBuilder.toString())) {

            String stringBuilder1 = paramLogBaseEpcInfo.getTid() +
                    paramLogBaseEpcInfo.getEpc();
            TagInfo tagInfo = this.tagInfoMap.get(stringBuilder1);
            Map<String, TagInfo> map2 = this.tagInfoMap;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(paramLogBaseEpcInfo.getTid());
            stringBuilder2.append(paramLogBaseEpcInfo.getEpc());
            long l = ((TagInfo) Objects.requireNonNull(map2.get(stringBuilder2.toString()))).getCount();
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(paramLogBaseEpcInfo.getRssi());
            stringBuilder2.append("");
            tagInfo.setRssi(stringBuilder2.toString());
            tagInfo.setReservedData(paramLogBaseEpcInfo.getReserved());
            tagInfo.setUserData(paramLogBaseEpcInfo.getUserdata());
            tagInfo.setCount(l + 1L);
            map2 = this.tagInfoMap;
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(paramLogBaseEpcInfo.getTid());
            stringBuilder2.append(paramLogBaseEpcInfo.getEpc());
            map2.put(stringBuilder2.toString(), tagInfo);

        } else {
            TagInfo tagInfo = new TagInfo();
            tagInfo.setIndex(this.index);
            tagInfo.setType("6C");
            tagInfo.setEpc(paramLogBaseEpcInfo.getEpc());
            tagInfo.setCount(1L);
            tagInfo.setUserData(paramLogBaseEpcInfo.getUserdata());
            tagInfo.setReservedData(paramLogBaseEpcInfo.getReserved());
            tagInfo.setTid(paramLogBaseEpcInfo.getTid());
            stringBuilder = new StringBuilder();
            stringBuilder.append(paramLogBaseEpcInfo.getRssi());
            stringBuilder.append("");
            tagInfo.setRssi(stringBuilder.toString());
            tagInfo.setReadTime(new Date());
            String stringBuilder1 = paramLogBaseEpcInfo.getTid() +
                    paramLogBaseEpcInfo.getEpc();
            this.tagInfoMap.put(stringBuilder1, tagInfo);
            this.index = this.index + 1L;
        }

        return this.tagInfoMap;
    }

//    //去重6B
//    public Map<String, TagInfo> pooled6bData(LogBase6bInfo info) {
//        if (tagInfoMap.containsKey(info.getTid())) {
//            TagInfo tagInfo = tagInfoMap.get(info.getTid());
//            Long count = tagInfoMap.get(info.getTid()).getCount();
//            count++;
//            tagInfo.setRssi(info.getRssi() + "");
//            tagInfo.setCount(count);
//            tagInfoMap.put(info.getTid(), tagInfo);
//        } else {
//            TagInfo tag = new TagInfo();
//            tag.setIndex(index);
//            tag.setType("6B");
//            tag.setCount(1l);
//            tag.setUserData(info.getUserdata());
//            if (info.getTid() != null) {
//                tag.setTid(info.getTid());
//            }
//            tag.setRssi(info.getRssi() + "");
//            tagInfoMap.put(info.getTid(), tag);
//            index++;
//        }
//        handlerStop.sendEmptyMessage(1);
//        return tagInfoMap;
//    }
//
//    //去重GB
//    public Map<String, TagInfo> pooledGbData(LogBaseGbInfo info) {
//        if (tagInfoMap.containsKey(info.getTid() + info.getEpc())) {
//            TagInfo tagInfo = tagInfoMap.get(info.getTid() + info.getEpc());
//            Long count = tagInfoMap.get(info.getTid() + info.getEpc()).getCount();
//            count++;
//            tagInfo.setRssi(info.getRssi() + "");
//            tagInfo.setCount(count);
//            tagInfoMap.put(info.getTid() + info.getEpc(), tagInfo);
//        } else {
//            TagInfo tag = new TagInfo();
//            tag.setIndex(index);
//            tag.setType("GB");
//            tag.setEpc(info.getEpc());
//            tag.setCount(1l);
//            tag.setUserData(info.getUserdata());
//            tag.setTid(info.getTid());
//            tag.setRssi(info.getRssi() + "");
//            tagInfoMap.put(info.getTid() + info.getEpc(), tag);
//            index++;
//        }
//        handlerStop.sendEmptyMessage(1);
//        return tagInfoMap;
//    }
//
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

    void soundTask() {
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

    final Handler handlerStop = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message param1Message) {
            if (param1Message.what == 1) {
                mHandler.removeCallbacks(r);
                soundHandler.removeCallbacks(timeTask);
//                upDataPane();
            }
            super.handleMessage(param1Message);
        }
    };

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

        rateValue = 0L;
        socket.close();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isClient) {
            if (isReader) {
                MsgBaseStop msgStop = new MsgBaseStop();
                client.sendSynMsg(msgStop);
                if (msgStop.getRtCode() == 0) {

                    mHandler.removeCallbacks(r);
                    soundHandler.removeCallbacks(timeTask);
//                    upDataPane();
                    isReader = false;
                    ToastUtils.showText(getResources().getString(R.string.stop_card));
                } else {
                    ToastUtils.showText(msgStop.getRtMsg());
                }
            }
        }
    }

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
