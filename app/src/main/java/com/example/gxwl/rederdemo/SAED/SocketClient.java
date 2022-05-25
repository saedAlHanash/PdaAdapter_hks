package com.example.gxwl.rederdemo.SAED;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class SocketClient {

    Socket socket;
    InetAddress inetAddress;
    PrintWriter mBufferOut;


    private void initPrintWriter() throws IOException {
        mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
    }

    public boolean connect(String ip, int port) {
        try {
            inetAddress = InetAddress.getByName(ip);
            socket = new Socket(inetAddress, port);// الاتصال بال socket

            initPrintWriter();// تهيئة ال buffer writer

            return socket.isConnected();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isConnected() {
        if (socket == null)
            return false;
        return socket.isConnected();
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

    public void close() {
        try {
            socket.close();
            mBufferOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}