package com.example.gxwl.rederdemo.util;

import com.gg.reader.api.protocol.gx.MsgBaseStop;

public class CheckCommunication {

    public static boolean check() {
        MsgBaseStop msg = new MsgBaseStop();
        GlobalClient.getClient().sendSynMsg(msg);
        if (msg.getRtCode() == 0) {
            return true;
        } else {
            ToastUtils.showText("time out");
            return false;
        }
    }
}
