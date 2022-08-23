package com.example.gxwl.rederdemo.SAED.Network;

import org.java_websocket.client.WebSocketClient;

public class Test extends Thread {

    private boolean threadKill = true;
    private final Object lockSend = new Object();

    WebSocketClient webSocketClient;
    private String s;

    public Test(WebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }

    @Override
    public void run() {
        synchronized (lockSend) {

            while (threadKill) {

                try {
                    lockSend.wait();
                } catch (InterruptedException ignored) {
                }

                if (!threadKill)
                    break;

                if (!webSocketClient.isOpen()) {
                    try {
                        boolean x = false;
                        while (!x)
                            x = webSocketClient.reconnectBlocking();
                    } catch (InterruptedException ignored) {
                    }
                }

                webSocketClient.send(s);
            }
        }
    }


    public void sendString(String s) {

        this.s = s;

        synchronized (lockSend) {
            lockSend.notify();
        }

    }

    public void killSelf() {
        this.threadKill = false;
        synchronized (lockSend) {
            lockSend.notify();
        }
    }
}
