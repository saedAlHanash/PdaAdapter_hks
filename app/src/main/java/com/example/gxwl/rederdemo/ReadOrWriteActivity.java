package com.example.gxwl.rederdemo;


import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
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

import com.example.gxwl.rederdemo.SAED.ViewModels.All;
import com.example.gxwl.rederdemo.SAED.ViewModels.MyViewModel;
import com.example.gxwl.rederdemo.SAED.ViewModels.Product;
import com.example.gxwl.rederdemo.adapter.AdapterItemEpc;

import com.example.gxwl.rederdemo.entity.TagInfo;
import com.example.gxwl.rederdemo.util.GlobalClient;
import com.example.gxwl.rederdemo.util.ToastUtils;
import com.example.gxwl.rederdemo.util.UtilSound;
import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.dal.HandlerTagEpcOver;
import com.gg.reader.api.protocol.gx.EnumG;
import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
import com.gg.reader.api.protocol.gx.LogBaseEpcOver;
import com.gg.reader.api.protocol.gx.MsgBaseInventoryEpc;
import com.gg.reader.api.protocol.gx.MsgBaseStop;
import com.gg.reader.api.protocol.gx.Param6bReadUserdata;
import com.gg.reader.api.protocol.gx.ParamEpcReadReserved;
import com.gg.reader.api.protocol.gx.ParamEpcReadTid;
import com.gg.reader.api.protocol.gx.ParamEpcReadUserdata;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
    private final List<TagInfo> tagInfoList = new ArrayList<TagInfo>();//适配器所需数据
    private Handler mHandler = new Handler();
    private Runnable r = null;
    private boolean isReader = false;
    private Runnable timeTask = null;
    private final Handler soundHandler = new Handler();
    private long rateValue = 1L;

    MyViewModel myViewModel;
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
    ArrayList<String> sentEpc = new ArrayList<>();
    URI uri;

    AdapterItemEpc adapter;

    ArrayList<Product> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);

        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

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


    private void initUri(String mIp, int mPort) {
        try {
            uri = new URI("ws://" + mIp + ":" + mPort);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * start connection with socket
     *
     * @param mIp   socket ip address
     * @param mPort socket port
     */
    public void initSocket(String mIp, int mPort) {

        initUri(mIp, mPort);

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake h) {

                Log.d(TAG, "onOpen: ");
                handler.sendEmptyMessage(201);
            }

            @Override
            public void onMessage(String message) {

                if (message.trim().equals("401")) {
                    handler.sendEmptyMessage(401);
                    return;
                }

                if (message.trim().equals("200"))
                    handler.sendEmptyMessage(1000);
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

                if (message.trim().equals("401")) {
                    handler.sendEmptyMessage(401);
                    return;
                }

                if (message.trim().equals("200")) {
                    handler.sendEmptyMessage(1000);
                    return;
                }

                if (message.charAt(0) == '[') {

                    ArrayList<All> alls = gson.fromJson(message,
                            new TypeToken<ArrayList<All>>() {
                            }.getType());

                    if (myViewModel != null)
                        myViewModel.allLiveData.postValue(alls);
                    return;
                }

                Product product = gson.fromJson(message, Product.class);
                product.bitmap = ConverterImage.convertBase64ToBitmap(product.im);

                if (myViewModel != null)
                    myViewModel.productLiveData.postValue(product);

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
                    adapter.notifyItemInserted(list.size() - 1);
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

                client.sendSynMsg(msg);

                if (0x00 == msg.getRtCode()) {
                    ToastUtils.showText("Start ReadCard");
                    isReader = true;
                    //computedSpeed();
                    soundTask();

                } else {
                    handlerStop.sendEmptyMessage(1);
                    ToastUtils.showText(msg.getRtMsg());
                }
            } else
                ToastUtils.showText(getResources().getString(R.string.read_card_being));

        } else
            ToastUtils.showText(getResources().getString(R.string.ununited));

    }

    //停止
    @OnClick(R.id.stop)
    public void stopRead() {
        if (isClient) {
            MsgBaseStop msgStop = new MsgBaseStop();
            rateValue = 0L;
            client.sendSynMsg(msgStop);
            if (0x00 == msgStop.getRtCode()) {
                isReader = false;
                ToastUtils.showText("Stop Success");
                handlerStop.sendEmptyMessage(1);
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
                if (sentEpc.contains(info.getEpc()))
                    return;

                socket.send("0," + info.getEpc());

                sentEpc.add(info.getEpc());

                Log.d("SAED_", "log: " + info.getEpc());

            }
        };

        client.onTagEpcOver = (param1String, param1LogBaseEpcOver) -> handlerStop.sendEmptyMessage(1);

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

    public void getProduct(String epc) {
        myViewModel.getProduct(socket, epc);
        myViewModel.productLiveData.observe(this, product -> {

        });
    }


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
