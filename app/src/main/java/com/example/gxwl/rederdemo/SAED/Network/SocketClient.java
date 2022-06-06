package com.example.gxwl.rederdemo.SAED.Network;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class SocketClient {

    Socket socket;
    InetAddress inetAddress;

    PrintWriter mBufferOut;
    /**
     * call back fro listen connect change stat
     */
    ConnectStat connectStat;

    String ip;
    int port;

    /**
     * to checking if there thread actually running and try to connect <br>
     * then no need to open new thread to re connect <br>
     * just lat current thread try
     */
    boolean reconnect = false;

    /**
     * set true when socket connect
     * set false when socket lost connect
     */
    boolean isConnect = false;

    DataInputStream finalIn = null;

    /**
     * set listener for connect change in this Socket<br>
     * if lost connect return false and if reconnect return true
     */
    public void setOnChangeConnectStatListener(ConnectStat connectStat) {
        this.connectStat = connectStat;
    }

    /**
     * init PrintWriter to send data in socket
     */
    private void initPrintWriter() throws IOException {
        mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        mBufferOut.println("connected with" + socket.getLocalAddress().getHostName() + socket.getPort()); // ارسال من اتصل للسيرفر
    }

    /**
     * start connect with server Socket
     *
     * @param ip   socket ip address
     * @param port socket port
     * @return true if connecting
     */
    public boolean connect(String ip, int port) {
        this.ip = ip;
        this.port = port;
        try {
            inetAddress = InetAddress.getByName(ip);
            socket = new Socket(inetAddress, port);// الاتصال بال socket

            initPrintWriter();// تهيئة ال buffer writer
            isConnect = true;

            connectStat.stat(true); // تغيير الحالة في ال callBack


            //تهيئة متنصت قراءة من ال socket بحيث عند انقطاع الاتصال يقوم ب exception من خلاله نقوم بتغير حالة التصال بال callBack
            //تم اللجوء لهذا الحل لان ال socket.isConnected() تقوم بإرجاع true دوما
            new Thread(runnable).start();

            return socket.isConnected();

        } catch (IOException e) {
            return false;
        }
    }

    /**
     * checking if socket is connecting
     */
    public boolean isConnected() {
        if (socket == null) {
            return false;
        }
        return socket.isConnected() && isConnect;
    }

    /**
     * reconnect with server socket
     */
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

    /**
     * start listener reed in socket <br>
     * الاستفادة : عند فقدان الاتصال يقوم برد أكسبشن لذلك نستفيد منه فقط لمعرفة حالة الاتصال
     */
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
                reConnect();
                break;
            }
        }
    };

    /**
     * close connect with server socket
     */
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