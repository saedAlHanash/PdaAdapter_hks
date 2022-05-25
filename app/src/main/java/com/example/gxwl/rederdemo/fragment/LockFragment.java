package com.example.gxwl.rederdemo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.gxwl.rederdemo.R;
import com.example.gxwl.rederdemo.entity.TagInfo;
import com.example.gxwl.rederdemo.util.GlobalClient;
import com.example.gxwl.rederdemo.util.ToastUtils;
import com.gg.reader.api.protocol.gx.EnumG;
import com.gg.reader.api.protocol.gx.MsgBaseLock6b;
import com.gg.reader.api.protocol.gx.MsgBaseLock6bGet;
import com.gg.reader.api.protocol.gx.MsgBaseLockEpc;
import com.gg.reader.api.protocol.gx.MsgBaseLockGb;
import com.gg.reader.api.protocol.gx.ParamEpcFilter;
import com.gg.reader.api.utils.StringUtils;

import butterknife.ButterKnife;


/**
 * Created by tong.wan on 2019/3/8 17:12.
 * <p>
 * 锁操作Fragment
 */
public class LockFragment extends Fragment {
    View view;
    private TagInfo info;
    String[] listener = new String[3];//0:匹配参数 1:匹配起始地址 2:访问密码
    private long ant;
    int tagType = 0;

    Spinner cus_6b_lock_area;
    Spinner cus_6b_lock_type;
    Button cus_6c_lock_event;

    EditText cus_lock_6b_address;
    Button cus_6b_lock_event;
    EditText cus_lock_6b_address_query;
    Button cus_6b_lock_event_query;

    Spinner cus_gb_lock_area;
    Spinner cus_gb_lock_type;
    Button cus_gb_lock_event;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (tagType == R.id.c) {
            view = inflater.inflate(R.layout.lock_epc_item, container, false);
            cus_6b_lock_area = view.findViewById(R.id.cus_6b_lock_area);
            cus_6b_lock_type = view.findViewById(R.id.cus_6b_lock_type);
            cus_6c_lock_event = view.findViewById(R.id.cus_6c_lock_event);
            cus_6c_lock_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lock6c();
                }
            });

        } else if (tagType == R.id.b) {
            view = inflater.inflate(R.layout.lock_6b_item, container, false);
            cus_lock_6b_address = view.findViewById(R.id.cus_lock_6b_address);
            cus_6b_lock_event = view.findViewById(R.id.cus_6b_lock_event);
            cus_lock_6b_address_query = view.findViewById(R.id.cus_lock_6b_address_query);
            cus_6b_lock_event_query = view.findViewById(R.id.cus_6b_lock_event_query);
            cus_6b_lock_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lock6b();
                }
            });

            cus_6b_lock_event_query.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lock6bQuery();
                }
            });

        } else if (tagType == R.id.gb) {
            view = inflater.inflate(R.layout.lock_gb_item, container, false);
            cus_gb_lock_area = view.findViewById(R.id.cus_gb_lock_area);
            cus_gb_lock_type = view.findViewById(R.id.cus_gb_lock_type);
            cus_gb_lock_event = view.findViewById(R.id.cus_gb_lock_event);
            cus_gb_lock_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lockGb();
                }
            });
        }
        ButterKnife.bind(this, view);
        return view;
    }

    public void lock6c() {
        MsgBaseLockEpc msg = new MsgBaseLockEpc();
        msg.setAntennaEnable(ant);
        //操作区域
        int lockArea = cus_6b_lock_area.getSelectedItemPosition();
        msg.setArea(lockArea);

        //操作类型
        int lockOperation = cus_6b_lock_type.getSelectedItemPosition();
        if (0 == lockOperation) {
            msg.setMode(EnumG.LockMode_Unlock);
        } else if (1 == lockOperation) {
            msg.setMode(EnumG.LockMode_Lock);
        } else if (2 == lockOperation) {
            msg.setMode(EnumG.LockMode_PermanentUnlock);
        } else if (3 == lockOperation) {
            msg.setMode(EnumG.LockMode_PermanentLock);
        }
        //选择锁定参数
        ParamEpcFilter filter = getFilter(info.getEpc(), info.getTid(), info.getUserData());
        if (null != filter) {
            msg.setFilter(filter);
        }
        //标签访问密码
        msg.setHexPassword(listener[2]);

        //发送指令
        GlobalClient.getClient().sendSynMsg(msg);
        if (0x00 == msg.getRtCode()) {
            ToastUtils.showText(msg.getRtMsg());
        } else {
            ToastUtils.showText(msg.getRtMsg());
        }

    }

    public void lock6b() {
        MsgBaseLock6b msg = new MsgBaseLock6b();
        //天线
        msg.setAntennaEnable(ant);
        //待锁定标签TID
        msg.setHexMatchTid(info.getTid());
        //锁定地址
        String text = cus_lock_6b_address.getText().toString();
        if (StringUtils.isNullOfEmpty(text)) {
            ToastUtils.showText("The address cannot be empty");
            return;
        }
        msg.setLockIndex(Integer.parseInt(text));
        //发送指令

        GlobalClient.getClient().sendSynMsg(msg);
        if (0x00 == msg.getRtCode()) {
            ToastUtils.showText(msg.getRtMsg());
        } else {
            ToastUtils.showText(msg.getRtMsg());
        }

    }

    public void lock6bQuery() {
        MsgBaseLock6bGet msg = new MsgBaseLock6bGet();
        msg.setAntennaEnable(ant);
        msg.setHexMatchTid(info.getTid());
        String text = cus_lock_6b_address_query.getText().toString();
        if (StringUtils.isNullOfEmpty(text)) {
            ToastUtils.showText("The address cannot be empty");
            return;
        }
        msg.setLockIndex(Integer.parseInt(text));
        //发送指令

        GlobalClient.getClient().sendSynMsg(msg);
        if (0x00 == msg.getRtCode()) {
            ToastUtils.showText(msg.getLockState() == 0 ? "unlocked" : "Has been locked");
        } else {
            ToastUtils.showText(msg.getRtMsg());
        }

    }

    public void lockGb() {
        MsgBaseLockGb msg = new MsgBaseLockGb();
        msg.setAntennaEnable(ant);
        int areaIndex = cus_gb_lock_area.getSelectedItemPosition();
        if (areaIndex == 0) {
            msg.setArea(0x00);
        } else if (areaIndex == 1) {
            msg.setArea(0x10);
        } else if (areaIndex == 2) {
            msg.setArea(0x20);
        } else {
            msg.setArea((areaIndex - 3) + (3 * 16));
        }
        int openIndex = cus_gb_lock_type.getSelectedItemPosition();
        switch (openIndex) {
            case 0:
                msg.setLockParam(0x00);
                break;
            case 1:
                msg.setLockParam(0x01);
                break;
            case 2:
                msg.setLockParam(0x02);
                break;
            case 3:
                msg.setLockParam(0x03);
                break;
            case 4:
                msg.setLockParam(0x11);
                break;
            case 5:
                msg.setLockParam(0x12);
                break;
            case 6:
                msg.setLockParam(0x13);
                break;

        }

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

        String password = listener[2];
        if (!StringUtils.isNullOfEmpty(password)) {
            msg.setHexPassword(password);
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
