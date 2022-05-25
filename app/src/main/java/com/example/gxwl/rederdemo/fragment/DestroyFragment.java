package com.example.gxwl.rederdemo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.gxwl.rederdemo.R;
import com.example.gxwl.rederdemo.entity.TagInfo;
import com.example.gxwl.rederdemo.util.GlobalClient;
import com.example.gxwl.rederdemo.util.ToastUtils;
import com.gg.reader.api.protocol.gx.EnumG;
import com.gg.reader.api.protocol.gx.MsgBaseDestroyEpc;
import com.gg.reader.api.protocol.gx.MsgBaseDestroyGb;
import com.gg.reader.api.protocol.gx.ParamEpcFilter;
import com.gg.reader.api.utils.StringUtils;

import butterknife.ButterKnife;


/**
 * Created by tong.wan on 2019/3/8 17:12.
 * <p>
 * 销毁Fragment
 */
public class DestroyFragment extends Fragment {
    View view;
    private TagInfo info;
    String[] listener = new String[3];//0:匹配参数 1:匹配起始地址 2:访问密码
    private long ant;
    int tagType = 0;
    EditText cus_6c_destroy_pas;
    Button cus_6c_destroy_event;

    Button cus_gb_destroy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (tagType == R.id.c) {
            view = inflater.inflate(R.layout.destory_6c_item, container, false);
            cus_6c_destroy_pas = view.findViewById(R.id.cus_6c_destroy_pas);
            cus_6c_destroy_event = view.findViewById(R.id.cus_6c_destroy_event);
            cus_6c_destroy_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    destroy6c();
                }
            });
        } else if (tagType == R.id.gb) {
            view = inflater.inflate(R.layout.destory_gb_item, container, false);
            cus_gb_destroy = view.findViewById(R.id.cus_gb_destroy);
            cus_gb_destroy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    destroyGb();
                }
            });
        }
        ButterKnife.bind(this, view);
        return view;
    }

    public void destroy6c() {
        MsgBaseDestroyEpc msg = new MsgBaseDestroyEpc();
        msg.setAntennaEnable(ant);
        //灭活密码
        if (StringUtils.isNullOfEmpty(cus_6c_destroy_pas.getText().toString())) {
            ToastUtils.showText("Please fill in the destroy code");
            return;
        }
        msg.setHexPassword(cus_6c_destroy_pas.getText().toString());
        //灭活参数
        ParamEpcFilter filter = getFilter(info.getEpc(), info.getTid(), info.getUserData());
        if (null != filter) {
            msg.setFilter(filter);
        }
        //发送指令

        GlobalClient.getClient().sendSynMsg(msg);
        if (0x00 == msg.getRtCode()) {
            ToastUtils.showText(msg.getRtMsg());
        } else {
            ToastUtils.showText(msg.getRtMsg());
        }

    }

    public void destroyGb() {
        MsgBaseDestroyGb msg = new MsgBaseDestroyGb();
        msg.setAntennaEnable(ant);

        int matchIndex = Integer.parseInt(listener[0]);
        if (matchIndex != 0) {
            ParamEpcFilter filter = new ParamEpcFilter();
            if (matchIndex == 1) {
                filter.setArea(0x00);
                filter.setHexData(info.getTid());
                filter.setBitLength(info.getTid().length() * 4);
            } else if (matchIndex == 2) {
                filter.setArea(0x10);
                filter.setHexData(info.getEpc());
                filter.setBitLength(info.getEpc().length() * 4);
            } else if (matchIndex == 3) {
                filter.setArea(0x20);
                filter.setHexData(info.getTid());//安全区不知道哪个区 默认写成tid
                filter.setBitLength(info.getTid().length() * 4);
            } else {
                filter.setArea((matchIndex - 4) + (3 * 16));
                filter.setHexData(info.getUserData());
                filter.setBitLength(info.getUserData().length() * 4);
            }
            filter.setBitStart(Integer.parseInt(listener[1]));
            msg.setFilter(filter);
        }
        //发送指令
        GlobalClient.getClient().sendSynMsg(msg);
        if (0x00 == msg.getRtCode()) {
            ToastUtils.showText(msg.getRtMsg());
        } else {
            ToastUtils.showText(msg.getRtMsg());
        }

    }

    //选择参数
    public ParamEpcFilter getFilter(String epc, String tid, String userdata) {
        //选择写入参数
        ParamEpcFilter filter = new ParamEpcFilter();
        //要匹配区域
        int toWriteMatchWay = Integer.parseInt(listener[0]);
        if (toWriteMatchWay == 1) {
            filter.setArea(EnumG.ParamFilterArea_EPC);
            if (epc != null) {
                int bitLength = epc.length() * 4;//每个字母与数字转换为二进制是4位，因此bit长度×4
                //匹配数据位长度
                filter.setBitLength(bitLength);
                //匹配内容
                filter.setHexData(epc);
            }
        } else if (toWriteMatchWay == 2 || toWriteMatchWay == 0) {
            filter.setArea(EnumG.ParamFilterArea_TID);
            if (tid != null) {
                int bitLength = tid.length() * 4;
                //匹配数据位长度
                filter.setBitLength(bitLength);
                //匹配内容
                filter.setHexData(tid);
            } else {
                return null;
            }
        } else if (toWriteMatchWay == 3) {
            filter.setArea(EnumG.ParamFilterArea_Userdata);
            if (userdata != null) {
                int bitLength = userdata.length() * 4;
                //匹配数据位长度
                filter.setBitLength(bitLength);
                //匹配内容
                filter.setHexData(userdata);
            } else {
                return null;
            }
        }
        //匹配的起始地址
        String toWriteMatchStart = listener[1];
        if (toWriteMatchStart != null) {
            filter.setBitStart(Integer.parseInt(toWriteMatchStart.trim()));
        } else {
            filter.setBitStart(0);
        }

        return filter;
    }

    //接收标签信息
    public void receiveTag(TagInfo tagInfo, long ant, int tagType) {
        if (null != tagInfo) {
            this.info = tagInfo;
            this.ant = ant;
            this.tagType = tagType;
        }
    }

    //接收监听信息
    public void receiveListener(String[] listener) {
        if (null != listener) {
            System.out.println(listener);
            this.listener = listener;
        }
    }
}
