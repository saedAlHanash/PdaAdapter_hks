package com.example.gxwl.rederdemo.SAED.UI.Activities;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelStoreOwner;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.arch.lifecycle.ViewModelProviders;

import com.example.gxwl.rederdemo.AppConfig.FC;
import com.example.gxwl.rederdemo.AppConfig.SharedPreference;
import com.example.gxwl.rederdemo.Helpers.Converters.GzipConverter;
import com.example.gxwl.rederdemo.Helpers.Images.ConverterImage;
import com.example.gxwl.rederdemo.Helpers.View.FTH;
import com.example.gxwl.rederdemo.R;
import com.example.gxwl.rederdemo.SAED.ViewModels.All;
import com.example.gxwl.rederdemo.SAED.Network.SaedSocket;
import com.example.gxwl.rederdemo.SAED.ViewModels.MyViewModel;
import com.example.gxwl.rederdemo.SAED.ViewModels.Product;
import com.example.gxwl.rederdemo.SAED.UI.Fragments.Client.ClientFragment;
import com.example.gxwl.rederdemo.entity.TagInfo;
import com.example.gxwl.rederdemo.util.GlobalClient;
import com.example.gxwl.rederdemo.util.ToastUtils;
import com.example.gxwl.rederdemo.util.UtilSound;
import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.dal.HandlerTagEpcLog;
import com.gg.reader.api.protocol.gx.EnumG;
import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
import com.gg.reader.api.protocol.gx.MsgBaseInventoryEpc;
import com.gg.reader.api.protocol.gx.MsgBaseStop;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class ClientActivity extends AppCompatActivity implements HandlerTagEpcLog {
    private static final String TAG = "ClientActivity_";

    //region GLOBAL VAR

    //region Views
    @BindView(R.id.client_container)
    FrameLayout clientContainer;
    @BindView(R.id.not_connect)
    TextView not_connect;

    //endregion

    //region connection config
    /**
     * socket ip address
     */
    public String ip;
    /**
     * socket port
     */
    public int port;
    URI uri;

    //endregion

    //region sockets
    WebSocketClient webSocketClient;
    public SaedSocket socket;
    //endregion

    private final GClient client = GlobalClient.getClient();
    private final boolean isClient = false;
    private final List<TagInfo> tagInfoList = new ArrayList<TagInfo>();//适配器所需数据
    private final Handler mHandler = new Handler();
    private final Runnable r = null;
    private boolean isReader = false;
    private Runnable timeTask = null;
    private final Handler soundHandler = new Handler();
    private long rateValue = 1L;


    public OnReadTag onReadTag;


    public MyViewModel myViewModel;
    Gson gson = new Gson();

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        ButterKnife.bind(this);
        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        startClientFragment();

        ip = SharedPreference.getIp().replaceAll("\\s+", "");
        port = SharedPreference.getPort();

        if (ip == null || ip.isEmpty())
            return;

        initSocket(ip, port);
    }

    //region getData
    public void getAll() {
        myViewModel.getAll(socket);
    }

    public void getProduct(String epc) {
        myViewModel.getProduct(socket, epc);
    }

    //endregion

    //region observers
    final Observer<Boolean> observer = isConnect -> {

        if (isConnect == null)
            return;
        if (isConnect)
            not_connect.setVisibility(View.GONE);
        else
            not_connect.setVisibility(View.VISIBLE);
    };

//endregion

    //region socket

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
                getAll();

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

                Log.d(TAG, "onMessage: " + product);

                if (myViewModel != null)
                    myViewModel.productLiveData.postValue(product);

            }
        };

        socket = new SaedSocket(webSocketClient);
    }

    private void initUri(String mIp, int mPort) {
        try {
            uri = new URI("ws://" + mIp + ":" + mPort);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    //endregion

    //region handler
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            switch (msg.what) {
                case 404:
                    Toast.makeText(ClientActivity.this,
                            "not found", Toast.LENGTH_SHORT).show();
                    break;

                case 500:
                    Toast.makeText(ClientActivity.this,
                            "have error", Toast.LENGTH_SHORT).show();
                    break;

                case 200:

                    break;

                case 201:
                    myViewModel.connectLiveData.postValue(true);
                    break;

                case 202:

                    handler.postDelayed(() -> {
                        if (!socket.isClosed) {
                            webSocketClient.setConnectionLostTimeout(25);
                            webSocketClient.reconnect();
                            Log.d(TAG, "handleMessage: reconnect");
                        }
                    }, 30000);

                    myViewModel.connectLiveData.postValue(false);
                    break;

                case 401:
                    myViewModel.sendReportLiveData.setValue(false);
                    break;

                case 1000:
                    myViewModel.sendReportLiveData.setValue(true);
            }


        }
    };

    //endregion

    //region start Fragments
    void startClientFragment() {
        FTH.replaceFadFragment(FC.CLIENT_C,
                this, new ClientFragment());
    }

    //endregion


    public void read() {
//        if (isClient) {
//            if (!isReader) {
//
//                MsgBaseInventoryEpc msg = new MsgBaseInventoryEpc();
//                msg.setAntennaEnable(EnumG.AntennaNo_1 | EnumG.AntennaNo_2);
//
//                msg.setInventoryMode(EnumG.InventoryMode_Inventory);
//
//                client.sendSynMsg(msg);
//
//                if (0x00 == msg.getRtCode()) {
//                    ToastUtils.showText("Start ReadCard");
//                    isReader = true;
//                    //computedSpeed();
//                    soundTask();
//
//                } else {
//                    handlerStop.sendEmptyMessage(1);
//                    ToastUtils.showText(msg.getRtMsg());
//                }
//            } else
//                ToastUtils.showText(getResources().getString(R.string.read_card_being));
//
//        } else
//            ToastUtils.showText(getResources().getString(R.string.ununited));

        x += 1;
        myViewModel.getProduct(socket, "000"+x + "");
    }

    int x = 0;

    public void stop() {
        if (!isReader)
            return;

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

    void removeObservers() {
        myViewModel.connectLiveData.removeObserver(observer);
    }


    @Override
    protected void onPause() {
        super.onPause();
        removeObservers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isClient) {
            if (isReader) {

                MsgBaseStop msgStop = new MsgBaseStop();
                client.sendSynMsg(msgStop);

                if (msgStop.getRtCode() == 0)
                    ToastUtils.showText(getResources().getString(R.string.stop_card));
                else
                    ToastUtils.showText(msgStop.getRtMsg());

            }
        }

        if (socket != null)
            socket.close();

        rateValue = 0L;
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
                stop();
            else
                read();

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.e("onKeyUp", keyCode + "");
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (webSocketClient != null && webSocketClient.isClosed())
            webSocketClient.reconnect();

        myViewModel.connectLiveData.observeForever(observer);
    }

    @Override
    public void log(String s, LogBaseEpcInfo log) {
        if (onReadTag == null)
            return;

        onReadTag.onRead(log.getEpc(), log.getRssi());
    }

    public interface OnReadTag {
        void onRead(@NonNull String epc, int rssi);
    }
}