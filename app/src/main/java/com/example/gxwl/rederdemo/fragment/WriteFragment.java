package com.example.gxwl.rederdemo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.gxwl.rederdemo.R;
import com.example.gxwl.rederdemo.entity.TagInfo;
import com.example.gxwl.rederdemo.util.ComputedPc;
import com.example.gxwl.rederdemo.util.DataUtils;
import com.example.gxwl.rederdemo.util.GlobalClient;
import com.example.gxwl.rederdemo.util.ToastUtils;
import com.gg.reader.api.protocol.gx.EnumG;
import com.gg.reader.api.protocol.gx.MsgBaseWrite6b;
import com.gg.reader.api.protocol.gx.MsgBaseWriteEpc;
import com.gg.reader.api.protocol.gx.MsgBaseWriteGb;
import com.gg.reader.api.protocol.gx.ParamEpcFilter;
import com.gg.reader.api.utils.StringUtils;
import com.gg.reader.api.utils.ThreadPoolUtils;

import butterknife.ButterKnife;

/**
 * Created by tong.wan on 2019/3/8 17:12.
 * <p>
 * 写操作Fragment
 */
public class WriteFragment extends Fragment {
    View view;
    Button w_f_btn;
    Spinner w_f_area;
    EditText w_f_content;
    EditText w_f_start;
    EditText w_f_block;
    CheckBox w_f_check;

    Spinner cus_6b_area;
    EditText cus_6b_start;
    EditText cus_6b_content;
    Button cus_6b_write_event;

    Spinner cus_gb_area;
    EditText cus_gb_start;
    EditText cus_gb_content;
    Button cus_gb_write_event;

    private TagInfo info;
    String[] listener = new String[3];//0:匹配参数 1:匹配起始地址 2:访问密码
    private long ant;
    int tagType = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (tagType == R.id.c) {
            view = inflater.inflate(R.layout.write_6c_cus, container, false);
            initEpc(view);
            initListener();
            w_f_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    writeEpc();
                }
            });
        } else if (tagType == R.id.b) {
            view = inflater.inflate(R.layout.write_6b_cus, container, false);
            init6b(view);
            cus_6b_write_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    write6b();
                }
            });
        } else if (tagType == R.id.gb) {
            view = inflater.inflate(R.layout.write_gb_cus, container, false);
            initGb(view);
            cus_gb_write_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    writeGb();
                }
            });
        }
        ButterKnife.bind(this, view);
        return view;
    }


    public void initEpc(View view) {
        w_f_btn = view.findViewById(R.id.w_f_btn);
        w_f_area = view.findViewById(R.id.w_f_area);
        w_f_content = view.findViewById(R.id.w_f_content);
        w_f_start = view.findViewById(R.id.w_f_start);
        w_f_block = view.findViewById(R.id.w_f_block);
        w_f_check = view.findViewById(R.id.w_f_check);
    }

    public void init6b(View view) {
        cus_6b_area = view.findViewById(R.id.cus_6b_area);
        cus_6b_start = view.findViewById(R.id.cus_6b_start);
        cus_6b_content = view.findViewById(R.id.cus_6b_content);
        cus_6b_write_event = view.findViewById(R.id.cus_6b_write_event);
    }

    public void initGb(View view) {
        cus_gb_area = view.findViewById(R.id.cus_gb_area);
        cus_gb_start = view.findViewById(R.id.cus_gb_start);
        cus_gb_content = view.findViewById(R.id.cus_gb_content);
        cus_gb_write_event = view.findViewById(R.id.cus_gb_write_event);
    }

    public void writeEpc() {
        MsgBaseWriteEpc msg = new MsgBaseWriteEpc();
        msg.setAntennaEnable(ant);
        int area = w_f_area.getSelectedItemPosition();
        String content = null;
        if (1 == area) {
            msg.setArea(EnumG.WriteArea_Epc);
            //字起始地址 第0个为CRC，不可写
            msg.setStart(Integer.parseInt(w_f_start.getText().toString()));
            //数据内容  pc值加上内容
            String text = w_f_content.getText().toString();
            if (StringUtils.isNullOfEmpty(text)) {
                ToastUtils.showText("The write cannot be empty");
                return;
            }
            int epcLength = ComputedPc.getEPCLength(w_f_content);
            if (msg.getStart() == 1) {
                content = ComputedPc.getPc(epcLength) + DataUtils.padLeft(text.trim().toUpperCase(), 4 * epcLength, '0');
            } else {
                content = DataUtils.padLeft(text.trim().toUpperCase(), 4 * epcLength, '0');
            }
            if (!StringUtils.isNullOfEmpty(content)) {
                msg.setHexWriteData(content);
            }
        } else {
            if (0 == area) {
                msg.setArea(EnumG.WriteArea_Reserved);
            } else if (2 == area) {
                msg.setArea(EnumG.WriteArea_Tid);
            } else if (3 == area) {
                msg.setArea(EnumG.WriteArea_Userdata);
            }
            //起始地址
            String writeStart = w_f_start.getText().toString().trim();
            msg.setStart(Integer.parseInt(writeStart));
            //数据内容
            content = w_f_content.getText().toString();
            if (content != null) {
                if (3 == area) {
                    int length = ComputedPc.getEPCLength(w_f_content);
                    String s = DataUtils.padLeft(content, 4 * length, '0');
                    msg.setHexWriteData(s);
                } else {
                    msg.setHexWriteData(content);
                }
            }
        }

        //选择写入参数
        ParamEpcFilter filter = getFilter(info.getEpc(), info.getTid(), info.getUserData());
        if (filter != null)
            msg.setFilter(filter);

        //标签访问密码
        msg.setHexPassword(listener[2]);

        if (w_f_check.isSelected()) {
            msg.setBlock(Integer.parseInt(w_f_block.getText().toString()) == 1 ? 1 : 0);
        }
        //发送指令
        GlobalClient.getClient().sendSynMsg(msg);
        if (0x00 == msg.getRtCode()) {
            ToastUtils.showText(msg.getRtMsg());
        } else {
            ToastUtils.showText(msg.getRtMsg());
        }

    }

    public void write6b() {
        MsgBaseWrite6b msg = new MsgBaseWrite6b();
        msg.setAntennaEnable(ant);
        msg.setHexMatchTid(info.getTid());
        //起始地址
        int i = Integer.parseInt(cus_6b_start.getText().toString().trim());
        msg.setStart(i);
        //数据内容
        String userData = cus_6b_content.getText().toString().trim();
        if (StringUtils.isNullOfEmpty(userData)) {
            ToastUtils.showText("The write cannot be empty");
            return;
        }
        int length = ComputedPc.getEPCLength(cus_6b_content);
        String s = DataUtils.padLeft(userData, 2 * length, '0');
        msg.setHexWriteData(s);

        GlobalClient.getClient().sendSynMsg(msg);
        if (0x00 == msg.getRtCode()) {
            ToastUtils.showText(msg.getRtMsg());
        } else {
            ToastUtils.showText(msg.getRtMsg());
        }

    }

    public void writeGb() {
        MsgBaseWriteGb msg = new MsgBaseWriteGb();
        msg.setAntennaEnable(ant);
        int areaIndex = cus_gb_area.getSelectedItemPosition();
        if (areaIndex == 0) {
            msg.setArea(0x10);
        } else if (areaIndex == 1) {
            msg.setArea(0x20);
        } else {
            msg.setArea((areaIndex - 2) + (3 * 16));
        }
        msg.setStart(Integer.parseInt(cus_gb_start.getText().toString()));
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
        String content = null;
        String text = cus_gb_content.getText().toString();
        int epcLength = ComputedPc.getEPCLength(cus_gb_content);
        if (msg.getStart() == 0) {
            content = ComputedPc.getGbPc(epcLength) + DataUtils.padLeft(text.trim().toUpperCase(), 4 * epcLength, '0');
            msg.setHexWriteData(content);
        } else {
            content = DataUtils.padLeft(text.trim().toUpperCase(), 4 * epcLength, '0');
            msg.setHexWriteData(content);
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


    //监听写入区域
    public void initListener() {
        w_f_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (tagType == R.id.c) {
                    if (position == 0) {
                        w_f_start.setText(2 + "");
                    } else if (position == 1) {
                        w_f_start.setText(1 + "");
                    } else {
                        w_f_start.setText(0 + "");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
