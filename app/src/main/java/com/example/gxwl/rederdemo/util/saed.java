package com.example.gxwl.rederdemo.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;

import android.view.MenuItem;

import android.widget.RadioButton;
import android.widget.RadioGroup;

import android.widget.TextView;

import butterknife.ButterKnife;

import com.example.gxwl.rederdemo.R;
import com.example.gxwl.rederdemo.adapter.RecycleViewAdapter;
import com.example.gxwl.rederdemo.entity.TagInfo;
import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.dal.HandlerDebugLog;
import com.gg.reader.api.dal.HandlerGpiOver;
import com.gg.reader.api.dal.HandlerGpiStart;
import com.gg.reader.api.dal.HandlerTag6bLog;
import com.gg.reader.api.dal.HandlerTag6bOver;
import com.gg.reader.api.dal.HandlerTagEpcLog;
import com.gg.reader.api.dal.HandlerTagEpcOver;
import com.gg.reader.api.dal.HandlerTagGJbLog;
import com.gg.reader.api.dal.HandlerTagGJbOver;
import com.gg.reader.api.dal.HandlerTagGbLog;
import com.gg.reader.api.dal.HandlerTagGbOver;
import com.gg.reader.api.protocol.gx.LogAppGpiOver;
import com.gg.reader.api.protocol.gx.LogAppGpiStart;
import com.gg.reader.api.protocol.gx.LogBase6bInfo;
import com.gg.reader.api.protocol.gx.LogBase6bOver;
import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
import com.gg.reader.api.protocol.gx.LogBaseEpcOver;
import com.gg.reader.api.protocol.gx.LogBaseGJbInfo;
import com.gg.reader.api.protocol.gx.LogBaseGJbOver;
import com.gg.reader.api.protocol.gx.LogBaseGbInfo;
import com.gg.reader.api.protocol.gx.LogBaseGbOver;

import com.gg.reader.api.protocol.gx.MsgBaseInventoryEpc;

import com.gg.reader.api.protocol.gx.MsgBaseStop;

import com.gg.reader.api.protocol.gx.Param6bReadUserdata;

import com.gg.reader.api.protocol.gx.ParamEpcReadReserved;
import com.gg.reader.api.protocol.gx.ParamEpcReadTid;
import com.gg.reader.api.protocol.gx.ParamEpcReadUserdata;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class saed extends AppCompatActivity {

    private RecycleViewAdapter adapter;

    private GClient client = GlobalClient.getClient();

    final Handler handlerStop = new Handler() {
        public void handleMessage(Message param1Message) {
            if (param1Message.what == 1) {
                saed.this.soundHandler.removeCallbacks(saed.this.timeTask);
                saed.this.upDataPane();
            }
            super.handleMessage(param1Message);
        }
    };

    private Long index = Long.valueOf(1L);


    private boolean[] isChecked = new boolean[]{false, false, false};

    private boolean isClient = false;

    private int isCtesius = 0;

    private boolean isReader = false;

    long rateValue = 0L;


    TextView readCount;


    private ParamEpcReadReserved reserveParam = null;


    private Handler soundHandler = new Handler();

    TextView speed;


    TextView tagCount;

    private List<TagInfo> tagInfoList = new ArrayList<TagInfo>();

    private Map<String, TagInfo> tagInfoMap = new LinkedHashMap<String, TagInfo>();

    private ParamEpcReadTid tidParam = null;


    TextView timeCount;

    private Runnable timeTask = null;

    private Param6bReadUserdata user6bParam = null;

    private ParamEpcReadUserdata userParam = null;


    private long getReadCount(List<TagInfo> paramList) {
        long l = 0L;
        for (byte b = 0; b < paramList.size(); b++)
            l += ((TagInfo) paramList.get(b)).getCount().longValue();
        return l;
    }

    private void soundTask() {
        Runnable runnable = new Runnable() {
            public void run() {
                if (saed.this.rateValue != 0L)
                    UtilSound.play(1, 0);
                saed.this.soundHandler.postDelayed(this, 20L);
            }
        };
        this.timeTask = runnable;
        this.soundHandler.postDelayed(runnable, 0L);
    }

    private void upDataPane() {
        this.tagInfoList.clear();
        this.tagInfoList.addAll(this.tagInfoMap.values());
        this.adapter.notifyData(this.tagInfoList);
        TextView textView1 = this.readCount;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(getReadCount(this.tagInfoList));
        stringBuilder2.append("");
        textView1.setText(stringBuilder2.toString());
        TextView textView2 = this.tagCount;
        StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append(this.tagInfoList.size());
        stringBuilder1.append("");
        textView2.setText(stringBuilder1.toString());
    }

    protected void attachBaseContext(Context paramContext) {
        super.attachBaseContext(LocalManageUtil.setLocal(paramContext));
    }

    public double computedNmv2d(String paramString1, String paramString2) {
        int i;
        String str = paramString1.substring(0, 2);
        paramString1 = paramString1.substring(2, 4);
        if (str.contains("1")) {
            i = -Integer.parseInt(paramString1, 16);
        } else {
            i = Integer.parseInt(paramString1, 16);
        }
        double d = (Integer.parseInt(paramString2.substring(1, 4), 16) + i - 500);
        Double.isNaN(d);
        return d / 5.4817D + 24.9D;
    }

    public void initRecycleView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView rv = (RecyclerView) findViewById(R.id.recycle);
        rv.setLayoutManager(layoutManager);
        rv.addItemDecoration(new DividerItemDecoration(this, 1));
        adapter = new RecycleViewAdapter(tagInfoList, this);
        rv.setAdapter(adapter);
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(128);
        ButterKnife.bind(this);
        boolean bool = getIntent().getBooleanExtra("isClient", false);
        this.isClient = bool;

        if (bool)
            subHandler(GlobalClient.getClient());

        initRecycleView();

        UtilSound.initSoundPool(this);
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.isClient && this.isReader) {
            MsgBaseStop msgBaseStop = new MsgBaseStop();
            this.client.sendSynMsg(msgBaseStop);
            if (msgBaseStop.getRtCode() == 0) {
//                ToastUtils.showText(getResources().getString(2131755400));
            } else {
                ToastUtils.showText(msgBaseStop.getRtMsg());
            }
        }
    }

    public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(paramInt);
        stringBuilder.append("");
        Log.e("onKeyDown", stringBuilder.toString());
        if (paramInt == 131 || paramInt == 285 || paramInt == 289 || paramInt == 291)
            if (this.isReader) {
                stopRead();
            } else {
                readCard();
            }
        return super.onKeyDown(paramInt, paramKeyEvent);
    }

    public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(paramInt);
        stringBuilder.append("");
        Log.e("onKeyUp", stringBuilder.toString());
        return super.onKeyUp(paramInt, paramKeyEvent);
    }

    protected void onStop() {
        super.onStop();
        if (this.isClient && this.isReader) {
            MsgBaseStop msgBaseStop = new MsgBaseStop();
            this.client.sendSynMsg(msgBaseStop);
            if (msgBaseStop.getRtCode() == 0) {
                this.isReader = false;
//                ToastUtils.showText(getResources().getString(2131755400));
            } else {
                ToastUtils.showText(msgBaseStop.getRtMsg());
            }
        }
    }

    public Map<String, TagInfo> pooled6bData(LogBase6bInfo paramLogBase6bInfo) {
        if (this.tagInfoMap.containsKey(paramLogBase6bInfo.getTid())) {
            TagInfo tagInfo = this.tagInfoMap.get(paramLogBase6bInfo.getTid());
            long l = ((TagInfo) this.tagInfoMap.get(paramLogBase6bInfo.getTid())).getCount().longValue();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(paramLogBase6bInfo.getRssi());
            stringBuilder.append("");
            tagInfo.setRssi(stringBuilder.toString());
            tagInfo.setCount(Long.valueOf(l + 1L));
            this.tagInfoMap.put(paramLogBase6bInfo.getTid(), tagInfo);
        } else {
            TagInfo tagInfo = new TagInfo();
            tagInfo.setIndex(this.index);
            tagInfo.setType("6B");
            tagInfo.setCount(Long.valueOf(1L));
            tagInfo.setUserData(paramLogBase6bInfo.getUserdata());
            if (paramLogBase6bInfo.getTid() != null)
                tagInfo.setTid(paramLogBase6bInfo.getTid());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(paramLogBase6bInfo.getRssi());
            stringBuilder.append("");
            tagInfo.setRssi(stringBuilder.toString());
            tagInfo.setReadTime(new Date());
            this.tagInfoMap.put(paramLogBase6bInfo.getTid(), tagInfo);
            this.index = Long.valueOf(this.index.longValue() + 1L);
        }
        return this.tagInfoMap;
    }

    public Map<String, TagInfo> pooled6cData(LogBaseEpcInfo paramLogBaseEpcInfo) {
        Map<String, TagInfo> map = this.tagInfoMap;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(paramLogBaseEpcInfo.getTid());
        stringBuilder.append(paramLogBaseEpcInfo.getEpc());
        if (map.containsKey(stringBuilder.toString())) {
            Map<String, TagInfo> map1 = this.tagInfoMap;
            StringBuilder stringBuilder1 = new StringBuilder();
            stringBuilder1.append(paramLogBaseEpcInfo.getTid());
            stringBuilder1.append(paramLogBaseEpcInfo.getEpc());
            TagInfo tagInfo = map1.get(stringBuilder1.toString());
            Map<String, TagInfo> map2 = this.tagInfoMap;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(paramLogBaseEpcInfo.getTid());
            stringBuilder2.append(paramLogBaseEpcInfo.getEpc());
            long l = ((TagInfo) map2.get(stringBuilder2.toString())).getCount().longValue();
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(paramLogBaseEpcInfo.getRssi());
            stringBuilder2.append("");
            tagInfo.setRssi(stringBuilder2.toString());
            tagInfo.setReservedData(paramLogBaseEpcInfo.getReserved());
            tagInfo.setUserData(paramLogBaseEpcInfo.getUserdata());
            tagInfo.setCount(Long.valueOf(l + 1L));
            map2 = this.tagInfoMap;
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(paramLogBaseEpcInfo.getTid());
            stringBuilder2.append(paramLogBaseEpcInfo.getEpc());
            map2.put(stringBuilder2.toString(), tagInfo);
        } else {
            TagInfo tagInfo = new TagInfo();
            tagInfo.setIndex(this.index);
            tagInfo.setType("6C");
            tagInfo.setEpc(paramLogBaseEpcInfo.getEpc());
            tagInfo.setCount(Long.valueOf(1L));
            tagInfo.setUserData(paramLogBaseEpcInfo.getUserdata());
            tagInfo.setReservedData(paramLogBaseEpcInfo.getReserved());
            tagInfo.setTid(paramLogBaseEpcInfo.getTid());
            stringBuilder = new StringBuilder();
            stringBuilder.append(paramLogBaseEpcInfo.getRssi());
            stringBuilder.append("");
            tagInfo.setRssi(stringBuilder.toString());
            tagInfo.setReadTime(new Date());
            Map<String, TagInfo> map1 = this.tagInfoMap;
            StringBuilder stringBuilder1 = new StringBuilder();
            stringBuilder1.append(paramLogBaseEpcInfo.getTid());
            stringBuilder1.append(paramLogBaseEpcInfo.getEpc());
            map1.put(stringBuilder1.toString(), tagInfo);
            this.index = Long.valueOf(this.index.longValue() + 1L);
        }
        return this.tagInfoMap;
    }

    public Map<String, TagInfo> pooledGJbData(LogBaseGJbInfo paramLogBaseGJbInfo) {
        Map<String, TagInfo> map = this.tagInfoMap;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(paramLogBaseGJbInfo.getTid());
        stringBuilder.append(paramLogBaseGJbInfo.getEpc());
        if (map.containsKey(stringBuilder.toString())) {
            map = this.tagInfoMap;
            stringBuilder = new StringBuilder();
            stringBuilder.append(paramLogBaseGJbInfo.getTid());
            stringBuilder.append(paramLogBaseGJbInfo.getEpc());
            TagInfo tagInfo = map.get(stringBuilder.toString());
            Map<String, TagInfo> map2 = this.tagInfoMap;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(paramLogBaseGJbInfo.getTid());
            stringBuilder2.append(paramLogBaseGJbInfo.getEpc());
            long l = ((TagInfo) map2.get(stringBuilder2.toString())).getCount().longValue();
            StringBuilder stringBuilder1 = new StringBuilder();
            stringBuilder1.append(paramLogBaseGJbInfo.getRssi());
            stringBuilder1.append("");
            tagInfo.setRssi(stringBuilder1.toString());
            tagInfo.setCount(Long.valueOf(l + 1L));
            Map<String, TagInfo> map1 = this.tagInfoMap;
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(paramLogBaseGJbInfo.getTid());
            stringBuilder2.append(paramLogBaseGJbInfo.getEpc());
            map1.put(stringBuilder2.toString(), tagInfo);
        } else {
            TagInfo tagInfo = new TagInfo();
            tagInfo.setIndex(this.index);
            tagInfo.setType("GJB");
            tagInfo.setEpc(paramLogBaseGJbInfo.getEpc());
            tagInfo.setCount(Long.valueOf(1L));
            tagInfo.setUserData(paramLogBaseGJbInfo.getUserdata());
            tagInfo.setTid(paramLogBaseGJbInfo.getTid());
            stringBuilder = new StringBuilder();
            stringBuilder.append(paramLogBaseGJbInfo.getRssi());
            stringBuilder.append("");
            tagInfo.setRssi(stringBuilder.toString());
            tagInfo.setReadTime(new Date());
            Map<String, TagInfo> map1 = this.tagInfoMap;
            StringBuilder stringBuilder1 = new StringBuilder();
            stringBuilder1.append(paramLogBaseGJbInfo.getTid());
            stringBuilder1.append(paramLogBaseGJbInfo.getEpc());
            map1.put(stringBuilder1.toString(), tagInfo);
            this.index = Long.valueOf(this.index.longValue() + 1L);
        }
        return this.tagInfoMap;
    }

    public Map<String, TagInfo> pooledGbData(LogBaseGbInfo paramLogBaseGbInfo) {
        Map<String, TagInfo> map = this.tagInfoMap;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(paramLogBaseGbInfo.getTid());
        stringBuilder.append(paramLogBaseGbInfo.getEpc());
        if (map.containsKey(stringBuilder.toString())) {
            map = this.tagInfoMap;
            stringBuilder = new StringBuilder();
            stringBuilder.append(paramLogBaseGbInfo.getTid());
            stringBuilder.append(paramLogBaseGbInfo.getEpc());
            TagInfo tagInfo = map.get(stringBuilder.toString());
            map = this.tagInfoMap;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(paramLogBaseGbInfo.getTid());
            stringBuilder2.append(paramLogBaseGbInfo.getEpc());
            long l = ((TagInfo) map.get(stringBuilder2.toString())).getCount().longValue();
            StringBuilder stringBuilder1 = new StringBuilder();
            stringBuilder1.append(paramLogBaseGbInfo.getRssi());
            stringBuilder1.append("");
            tagInfo.setRssi(stringBuilder1.toString());
            tagInfo.setCount(Long.valueOf(l + 1L));
            Map<String, TagInfo> map1 = this.tagInfoMap;
            stringBuilder1 = new StringBuilder();
            stringBuilder1.append(paramLogBaseGbInfo.getTid());
            stringBuilder1.append(paramLogBaseGbInfo.getEpc());
            map1.put(stringBuilder1.toString(), tagInfo);
        } else {
            TagInfo tagInfo = new TagInfo();
            tagInfo.setIndex(this.index);
            tagInfo.setType("GB");
            tagInfo.setEpc(paramLogBaseGbInfo.getEpc());
            tagInfo.setCount(Long.valueOf(1L));
            tagInfo.setUserData(paramLogBaseGbInfo.getUserdata());
            tagInfo.setTid(paramLogBaseGbInfo.getTid());
            StringBuilder stringBuilder1 = new StringBuilder();
            stringBuilder1.append(paramLogBaseGbInfo.getRssi());
            stringBuilder1.append("");
            tagInfo.setRssi(stringBuilder1.toString());
            tagInfo.setReadTime(new Date());
            Map<String, TagInfo> map1 = this.tagInfoMap;
            stringBuilder1 = new StringBuilder();
            stringBuilder1.append(paramLogBaseGbInfo.getTid());
            stringBuilder1.append(paramLogBaseGbInfo.getEpc());
            map1.put(stringBuilder1.toString(), tagInfo);
            this.index = Long.valueOf(this.index.longValue() + 1L);
        }
        return this.tagInfoMap;
    }


    public void readCard() {
        if (this.isClient) {
            if (!this.isReader) {

                MsgBaseInventoryEpc msgBaseInventoryEpc = new MsgBaseInventoryEpc();

                msgBaseInventoryEpc.setAntennaEnable(Long.valueOf(1L));
                msgBaseInventoryEpc.setInventoryMode(1);

                if (this.isChecked[0]) {
                    ParamEpcReadTid paramEpcReadTid = new ParamEpcReadTid();
                    this.tidParam = paramEpcReadTid;
                    paramEpcReadTid.setMode(0);
                    this.tidParam.setLen(6);
                    msgBaseInventoryEpc.setReadTid(this.tidParam);
                }
                if (this.isChecked[1]) {
                    ParamEpcReadUserdata paramEpcReadUserdata = new ParamEpcReadUserdata();
                    this.userParam = paramEpcReadUserdata;
                    paramEpcReadUserdata.setStart(0);
                    this.userParam.setLen(2);
                    msgBaseInventoryEpc.setReadUserdata(this.userParam);
                }
                if (this.isChecked[2]) {
                    ParamEpcReadReserved paramEpcReadReserved = new ParamEpcReadReserved();
                    this.reserveParam = paramEpcReadReserved;
                    paramEpcReadReserved.setStart(0);
                    this.reserveParam.setLen(4);
                    msgBaseInventoryEpc.setReadReserved(this.reserveParam);
                }

                this.client.sendSynMsg(msgBaseInventoryEpc);

                if (msgBaseInventoryEpc.getRtCode() == 0) {
                    ToastUtils.showText("Start ReadCard");
                    this.isReader = true;
                    soundTask();
                } else {
                    this.handlerStop.sendEmptyMessage(1);
                    ToastUtils.showText(msgBaseInventoryEpc.getRtMsg());
                }

                this.isCtesius = 0;

            } else {
                ToastUtils.showText(getResources().getString(R.string.read_card_being));
            }
        } else {
            ToastUtils.showText(getResources().getString(R.string.ununited));
        }
    }

    public void stopRead() {
        if (this.isClient) {
            MsgBaseStop msgBaseStop = new MsgBaseStop();
            this.client.sendSynMsg(msgBaseStop);

            if (msgBaseStop.getRtCode() == 0) {
                this.isReader = false;
                ToastUtils.showText("Stop Success");
            } else {
                ToastUtils.showText("Stop Fail");
            }
        } else {
            ToastUtils.showText(getResources().getString(R.string.ununited));
        }
    }

    public void subHandler(GClient client) {
        client.onTagEpcLog = new HandlerTagEpcLog() {
            public void log(String param1String, LogBaseEpcInfo param1LogBaseEpcInfo) {
                if (param1LogBaseEpcInfo.getResult() == 0)
                    synchronized (saed.this.tagInfoList) {
                        saed.this.pooled6cData(param1LogBaseEpcInfo);
                    }
            }
        };
        client.onTagEpcOver = new HandlerTagEpcOver() {
            public void log(String param1String, LogBaseEpcOver param1LogBaseEpcOver) {
                saed.this.handlerStop.sendEmptyMessage(1);
            }
        };
        client.onTag6bLog = new HandlerTag6bLog() {
            public void log(String param1String, LogBase6bInfo param1LogBase6bInfo) {
                if (param1LogBase6bInfo != null && param1LogBase6bInfo.getResult() == 0)
                    synchronized (saed.this.tagInfoList) {
                        saed.this.pooled6bData(param1LogBase6bInfo);
                    }
            }
        };
        client.onTag6bOver = new HandlerTag6bOver() {
            public void log(String param1String, LogBase6bOver param1LogBase6bOver) {
                saed.this.handlerStop.sendEmptyMessage(1);
            }
        };
        client.onTagGbLog = new HandlerTagGbLog() {
            public void log(String param1String, LogBaseGbInfo param1LogBaseGbInfo) {
                if (param1LogBaseGbInfo != null && param1LogBaseGbInfo.getResult() == 0)
                    synchronized (saed.this.tagInfoList) {
                        saed.this.pooledGbData(param1LogBaseGbInfo);
                    }
            }
        };
        client.onTagGbOver = new HandlerTagGbOver() {
            public void log(String param1String, LogBaseGbOver param1LogBaseGbOver) {
                Log.e("HandlerTagGbOver", "-------------HandlerTagGbOver");
                saed.this.handlerStop.sendEmptyMessage(1);
            }
        };
        client.onTagGJbLog = new HandlerTagGJbLog() {
            public void log(String param1String, LogBaseGJbInfo param1LogBaseGJbInfo) {
                if (param1LogBaseGJbInfo.getResult() == 0)
                    synchronized (saed.this.tagInfoList) {
                        saed.this.pooledGJbData(param1LogBaseGJbInfo);
                    }
            }
        };
        client.onTagGJbOver = new HandlerTagGJbOver() {
            public void log(String param1String, LogBaseGJbOver param1LogBaseGJbOver) {
                Log.e("HandlerTagGJbOver", "-------------HandlerTagGJbOver");
                saed.this.handlerStop.sendEmptyMessage(1);
            }
        };
        client.onGpiOver = new HandlerGpiOver() {
            public void log(String param1String, LogAppGpiOver param1LogAppGpiOver) {
                System.out.println(param1LogAppGpiOver);
            }
        };
        client.onGpiStart = new HandlerGpiStart() {
            public void log(String param1String, LogAppGpiStart param1LogAppGpiStart) {
                System.out.println(param1LogAppGpiStart);
            }
        };
        client.debugLog = new HandlerDebugLog() {
            public void receiveDebugLog(String param1String) {
                Log.e("receiveDebugLog", param1String);
            }

            public void sendDebugLog(String param1String) {
                Log.e("sendDebugLog", param1String);
            }
        };
    }
}
