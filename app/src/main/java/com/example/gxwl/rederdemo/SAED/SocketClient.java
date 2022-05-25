package com.example.gxwl.rederdemo.SAED;

import android.view.View;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;

public class SocketClient {

    Socket socket;
    InetAddress inetAddress;
    PrintWriter mBufferOut;
    ConnectStat connectStat;

    String ip;
    int port;
    boolean reconnect = false;

    boolean isConnect = false;

    DataInputStream finalIn = null;

    public void setConnectStat(ConnectStat connectStat) {
        this.connectStat = connectStat;
    }

    private void initPrintWriter() throws IOException {
        mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        mBufferOut.println("connected with" + socket.getLocalAddress().getHostName() + socket.getPort());
    }

    public boolean connect(String ip, int port) {
        this.ip = ip;
        this.port = port;
        try {
            inetAddress = InetAddress.getByName(ip);
            socket = new Socket(inetAddress, port);// الاتصال بال socket

            initPrintWriter();// تهيئة ال buffer writer
            isConnect = true;

            connectStat.stat(true);

            new Thread(runnable).start();

            return socket.isConnected();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isConnected() {
        if (socket == null) {
            return false;
        }
        return socket.isConnected() && isConnect;
    }

    public void reConnect() {
        if (reconnect)
            return;

        reconnect = true;
        new Thread(() -> {
            while (reconnect) {
                //اذا الاتصال تم
                if (connect(ip, port)) {
                    reconnect = false;
                    break;
                } else // اذا لم يتصل
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
        }).start();
    }

    public void sendBoolean(boolean b) {
        if (!socket.isConnected()
                || mBufferOut == null
                || mBufferOut.checkError())
            return;

        mBufferOut.print(b);
//        mBufferOut.flush();
    }

    public void sendInt(int i) {
        if (!socket.isConnected()
                || mBufferOut == null
                || mBufferOut.checkError())
            return;
        mBufferOut.print(i);
//        mBufferOut.flush();
    }

    public void sendFloat(float f) {
        if (!socket.isConnected()
                || mBufferOut == null
                || mBufferOut.checkError())
            return;
        mBufferOut.print(f);
//        mBufferOut.flush();
    }

    public void sendLong(long l) {
        if (!socket.isConnected()
                || mBufferOut == null
                || mBufferOut.checkError())
            return;
        mBufferOut.println(l);
//        mBufferOut.flush();
    }

    public void sendString(String message) {
        if (!socket.isConnected()
                || mBufferOut == null
                || mBufferOut.checkError())
            return;

        mBufferOut.println(message);
//        mBufferOut.flush();
    }


    private final Runnable runnable = () -> {
        try {
            finalIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                finalIn.readBoolean();
            } catch (IOException ignore) {
                isConnect = false;
                connectStat.stat(false);
                break;
            }
        }
    };

    public void close() {


        new Thread(() ->
        {
            try {
                mBufferOut.println("close connect" + socket.getLocalAddress().getHostName() + socket.getPort());
                socket.close();
                mBufferOut.close();
                finalIn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ).start();


    }

    public interface ConnectStat {
        void stat(boolean b);
    }

}