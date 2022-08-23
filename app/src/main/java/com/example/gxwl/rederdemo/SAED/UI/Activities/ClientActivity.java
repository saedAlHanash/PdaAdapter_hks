package com.example.gxwl.rederdemo.SAED.UI.Activities;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelStoreOwner;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class ClientActivity extends AppCompatActivity {
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
                    if (!socket.isClosed)
                        webSocketClient.reconnect();
                    myViewModel.connectLiveData.postValue(false);
                    break;
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

    void removeObservers() {
        myViewModel.connectLiveData.removeObserver(observer);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (socket != null)
            socket.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeObservers();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (webSocketClient.isClosed())
            webSocketClient.reconnect();

        myViewModel.connectLiveData.observeForever(observer);
    }
}