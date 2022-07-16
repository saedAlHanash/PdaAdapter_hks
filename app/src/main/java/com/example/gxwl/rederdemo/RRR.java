//package com.example.gxwl.rederdemo;
//import android.app.Activity;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Color;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.DividerItemDecoration;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.Spinner;
//import android.widget.TextView;
//import butterknife.ButterKnife;
//import com.example.gxwl.rederdemo.adapter.RecycleViewAdapter;
//import com.example.gxwl.rederdemo.entity.TagInfo;
//import com.example.gxwl.rederdemo.util.ComputedPc;
//import com.example.gxwl.rederdemo.util.DataUtils;
//import com.example.gxwl.rederdemo.util.ExcelUtil;
//import com.example.gxwl.rederdemo.util.GlobalClient;
//import com.example.gxwl.rederdemo.util.LocalManageUtil;
//import com.example.gxwl.rederdemo.util.SharedPreferencesUtil;
//import com.example.gxwl.rederdemo.util.UtilSound;
//import com.gg.reader.api.dal.GClient;
//import com.gg.reader.api.dal.HandlerDebugLog;
//import com.gg.reader.api.dal.HandlerGpiOver;
//import com.gg.reader.api.dal.HandlerGpiStart;
//import com.gg.reader.api.dal.HandlerTag6bLog;
//import com.gg.reader.api.dal.HandlerTag6bOver;
//import com.gg.reader.api.dal.HandlerTagEpcLog;
//import com.gg.reader.api.dal.HandlerTagEpcOver;
//import com.gg.reader.api.dal.HandlerTagGJbLog;
//import com.gg.reader.api.dal.HandlerTagGJbOver;
//import com.gg.reader.api.dal.HandlerTagGbLog;
//import com.gg.reader.api.dal.HandlerTagGbOver;
//import com.gg.reader.api.protocol.gx.LogAppGpiOver;
//import com.gg.reader.api.protocol.gx.LogAppGpiStart;
//import com.gg.reader.api.protocol.gx.LogBase6bInfo;
//import com.gg.reader.api.protocol.gx.LogBase6bOver;
//import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
//import com.gg.reader.api.protocol.gx.LogBaseEpcOver;
//import com.gg.reader.api.protocol.gx.LogBaseGJbInfo;
//import com.gg.reader.api.protocol.gx.LogBaseGJbOver;
//import com.gg.reader.api.protocol.gx.LogBaseGbInfo;
//import com.gg.reader.api.protocol.gx.LogBaseGbOver;
//import com.gg.reader.api.protocol.gx.MsgBaseInventory6b;
//import com.gg.reader.api.protocol.gx.MsgBaseInventoryEpc;
//import com.gg.reader.api.protocol.gx.MsgBaseInventoryGJb;
//import com.gg.reader.api.protocol.gx.MsgBaseInventoryGb;
//import com.gg.reader.api.protocol.gx.MsgBaseSetBaseband;
//import com.gg.reader.api.protocol.gx.MsgBaseStop;
//import com.gg.reader.api.protocol.gx.MsgBaseWrite6b;
//import com.gg.reader.api.protocol.gx.MsgBaseWriteEpc;
//import com.gg.reader.api.protocol.gx.MsgBaseWriteGJb;
//import com.gg.reader.api.protocol.gx.MsgBaseWriteGb;
//import com.gg.reader.api.protocol.gx.Param6bReadUserdata;
//import com.gg.reader.api.protocol.gx.ParamEpcFilter;
//import com.gg.reader.api.protocol.gx.ParamEpcReadReserved;
//import com.gg.reader.api.protocol.gx.ParamEpcReadTid;
//import com.gg.reader.api.protocol.gx.ParamEpcReadUserdata;
//import com.gg.reader.api.protocol.gx.ParamGbReadUserdata;
//import com.gg.reader.api.utils.HexUtils;
//import com.gg.reader.api.utils.StringUtils;
//import java.io.File;
//import java.io.PrintStream;
//import java.io.Serializable;
//import java.lang.reflect.Field;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.TimeZone;
//import me.weyye.hipermission.HiPermission;
//import me.weyye.hipermission.PermissionCallback;
//import me.weyye.hipermission.PermissionItem;
//
//public class RRR extends AppCompatActivity {
//    private RecycleViewAdapter adapter;
//
//    RadioButton b;
//
//    EditText block_size_edit;
//
//    RadioButton c;
//
//    Button clean;
//
//    private GClient client = GlobalClient.getClient();
//
//    TextView ctesius_title;
//
//    EditText cus_epc;
//
//    CheckBox cus_gb_read_check;
//
//    CheckBox cus_gb_read_check_tid;
//
//    CheckBox cus_gb_read_check_user;
//
//    Spinner cus_gb_read_mode;
//
//    CheckBox cus_gjb_read_check;
//
//    CheckBox cus_gjb_read_check_tid;
//
//    CheckBox cus_gjb_read_check_user;
//
//    Spinner cus_gjb_read_mode;
//
//    Spinner cus_mode;
//
//    EditText cus_pas;
//
//    Spinner cus_readTid_gb_content;
//
//    Spinner cus_readTid_gjb_content;
//
//    Spinner cus_read_6b_content;
//
//    EditText cus_read_6b_len;
//
//    EditText cus_read_6b_start;
//
//    EditText cus_read_epc;
//
//    EditText cus_read_filter_6b_tid;
//
//    EditText cus_read_gb_content;
//
//    EditText cus_read_gb_len;
//
//    EditText cus_read_gb_start;
//
//    EditText cus_read_gjb_content;
//
//    EditText cus_read_gjb_start;
//
//    EditText cus_read_gjb_user_len;
//
//    EditText cus_read_match_content;
//
//    Spinner cus_read_mode;
//
//    EditText cus_read_pas;
//
//    EditText cus_read_reserve_len;
//
//    EditText cus_read_reserve_start;
//
//    EditText cus_read_start;
//
//    EditText cus_read_tid;
//
//    EditText cus_read_tid_len;
//
//    Spinner cus_read_tid_mode;
//
//    EditText cus_read_user;
//
//    EditText cus_read_user_len;
//
//    EditText cus_read_user_start;
//
//    EditText cus_start;
//
//    EditText cus_tid;
//
//    EditText cus_user;
//
//    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
//
//    EditText filter_read_gb_len;
//
//    Spinner filter_read_gb_userChild;
//
//    EditText filter_read_gb_userChild_pas;
//
//    EditText filter_read_gb_userChild_start;
//
//    EditText filter_read_gjb_len;
//
//    EditText filter_read_gjb_pas;
//
//    EditText filter_read_gjb_user_start;
//
//    private SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
//
//    MenuItem gMenuItem = null;
//
//    final Handler handlerStop = new Handler() {
//        public void handleMessage(Message param1Message) {
//            if (param1Message.what == 1) {
//                mHandler.removeCallbacks(r);
//                soundHandler.removeCallbacks(timeTask);
//                upDataPane();
//            }
//            super.handleMessage(param1Message);
//        }
//    };
//
//    private Long index = Long.valueOf(1L);
//
//    TextView info_Reserved;
//
//    TextView info_epc;
//
//    TextView info_index;
//
//    TextView info_tid;
//
//    TextView info_type;
//
//    TextView info_userData;
//
//    private boolean[] isChecked = new boolean[] { false, false, false };
//
//    private boolean isClient = false;
//
//    private int isCtesius = 0;
//
//    private boolean isReader = false;
//
//    private boolean isSound = true;
//
//    RadioButton loop;
//
//    CheckBox ltu27Box;
//
//    CheckBox ltu31Box;
//
//    private Handler mHandler = new Handler();
//
//    CheckBox nmv2dBox;
//
//    private Runnable r = null;
//
//    long rateValue = 0L;
//
//    Button read;
//
//    TextView readCount;
//
//    CheckBox read_other_pas;
//
//    CheckBox read_reserve_true;
//
//    CheckBox read_tid_true;
//
//    CheckBox read_user_true;
//
//    private ParamEpcReadReserved reserveParam = null;
//
//    RadioButton single;
//
//    private Handler soundHandler = new Handler();
//
//    TextView speed;
//
//    Button stop;
//
//    LinearLayout tabHead;
//
//    TextView tagCount;
//
//    private List<TagInfo> tagInfoList = new ArrayList<TagInfo>();
//
//    private Map<String, TagInfo> tagInfoMap = new LinkedHashMap<String, TagInfo>();
//
//    private ParamEpcReadTid tidParam = null;
//
//    private int time = 0;
//
//    TextView timeCount;
//
//    private Runnable timeTask = null;
//
//    RadioGroup type;
//
//    private Param6bReadUserdata user6bParam = null;
//
//    private ParamEpcReadUserdata userParam = null;
//
//    EditText w_6b_user_start;
//
//    EditText w_6b_user_tid;
//
//    EditText w_6b_user_value;
//
//    EditText w_epc;
//
//    EditText w_gb_epc;
//
//    EditText w_gb_len;
//
//    EditText w_gb_pas;
//
//    EditText w_gb_tid;
//
//    EditText w_gb_user_epc;
//
//    EditText w_gb_user_pas;
//
//    EditText w_gb_user_start;
//
//    EditText w_gb_user_tid;
//
//    EditText w_gb_user_value;
//
//    EditText w_gb_value;
//
//    EditText w_gjb_epc;
//
//    EditText w_gjb_len;
//
//    EditText w_gjb_pas;
//
//    EditText w_gjb_tid;
//
//    EditText w_gjb_user_epc;
//
//    EditText w_gjb_user_pas;
//
//    EditText w_gjb_user_start;
//
//    EditText w_gjb_user_tid;
//
//    EditText w_gjb_user_value;
//
//    EditText w_gjb_value;
//
//    EditText w_len;
//
//    EditText w_pas;
//
//    EditText w_tid;
//
//    EditText w_user_epc;
//
//    EditText w_user_len;
//
//    EditText w_user_pas;
//
//    EditText w_user_tid;
//
//    EditText w_user_value;
//
//    EditText w_value;
//
//    RadioGroup way;
//
//    Spinner write_gb_user_child;
//
//    private void computedSpeed() {
//        Runnable runnable = new Runnable() {
//            public void run() {
//                List list;
//                StringBuilder stringBuilder1;
//                long l;
//
//
//                TextView textView = timeCount;
//                StringBuilder stringBuilder2 = new StringBuilder();
//                stringBuilder2.append(" (s)");
//                textView.setText(stringBuilder2.toString());
//                null = (Long)rateMap.get("after");
//                if (null != null) {
//                    l = longValue();
//                } else {
//                    l = 0L;
//                }
//
//                synchronized (tagInfoList) {
//                    tagInfoList.clear();
//                    tagInfoList.addAll(tagInfoMap.values());
//                    adapter.notifyData(tagInfoList);
//
//                    TextView textView1 = readCount;
//                    StringBuilder stringBuilder = new StringBuilder();
//
//                    stringBuilder.append(getReadCount(tagInfoList));
//                    stringBuilder.append("");
//                    textView1.setText(stringBuilder.toString());
//                    textView1 = tagCount;
//                    stringBuilder1 = new StringBuilder();
//                    stringBuilder1.append(tagInfoList.size());
//                    stringBuilder1.append("");
//                    textView1.setText(stringBuilder1.toString());
//                    rateMap.put("after", Long.valueOf(readCount.getText().toString()));
//                    long l1 = Long.valueOf(readCount.getText().toString()).longValue();
//                    if (l1 >= l) {
//                        rateValue = l1 - l;
//                        textView1 = speed;
//                        stringBuilder1 = new StringBuilder();
//                        stringBuilder1.append(rateValue);
//                        stringBuilder1.append(" (t/s)");
//                        textView1.setText(stringBuilder1.toString());
//                    }
//                    mHandler.postDelayed(this, 1000L);
//                    return;
//                }
//            }
//        };
//        this.r = runnable;
//        this.mHandler.postDelayed(runnable, 1000L);
//    }
//
//    private long getReadCount(List<TagInfo> paramList) {
//        long l = 0L;
//        for (byte b = 0; b < paramList.size(); b++)
//            l += ((TagInfo)paramList.get(b)).getCount().longValue();
//        return l;
//    }
//
//    private ArrayList<ArrayList<String>> getRecordData(List<TagInfo> paramList) {
//        ArrayList<ArrayList<String>> arrayList = new ArrayList();
//        for (byte b = 0; b < paramList.size(); b++) {
//            String str;
//            ArrayList<String> arrayList1 = new ArrayList();
//            TagInfo tagInfo = paramList.get(b);
//            StringBuilder stringBuilder2 = new StringBuilder();
//            stringBuilder2.append(tagInfo.getIndex());
//            stringBuilder2.append("");
//            arrayList1.add(stringBuilder2.toString());
//            arrayList1.add(tagInfo.getType());
//            if (tagInfo.getEpc() != null) {
//                str = tagInfo.getEpc();
//            } else {
//                str = "";
//            }
//            arrayList1.add(str);
//            if (tagInfo.getTid() != null) {
//                str = tagInfo.getTid();
//            } else {
//                str = "";
//            }
//            arrayList1.add(str);
//            if (tagInfo.getUserData() != null) {
//                str = tagInfo.getUserData();
//            } else {
//                str = "";
//            }
//            arrayList1.add(str);
//            if (tagInfo.getReservedData() != null) {
//                str = tagInfo.getReservedData();
//            } else {
//                str = "";
//            }
//            arrayList1.add(str);
//            StringBuilder stringBuilder1 = new StringBuilder();
//            stringBuilder1.append(tagInfo.getCount());
//            stringBuilder1.append("");
//            arrayList1.add(stringBuilder1.toString());
//            arrayList1.add(this.dateFormat.format(tagInfo.getReadTime()));
//            arrayList.add(arrayList1);
//        }
//        return arrayList;
//    }
//
//    private void initPane() {
//        this.index = Long.valueOf(1L);
//        this.time = 0;
//        this.rateValue = 0L;
//        this.tagInfoMap.clear();
//        this.tagInfoList.clear();
//        this.adapter.notifyData(this.tagInfoList);
//        this.tagCount.setText("0");
//        this.readCount.setText("0");
//        this.timeCount.setText("00:00:00 (s)");
//        this.speed.setText("0 (t/s)");
//        this.adapter.setThisPosition(null);
//    }
//
//    private void lightOn() {
//        MsgBaseWriteEpc msgBaseWriteEpc = new MsgBaseWriteEpc();
//        msgBaseWriteEpc.setAntennaEnable(Long.valueOf(1L));
//        msgBaseWriteEpc.setArea(3);
//        TagInfo tagInfo = this.tagInfoList.get(this.adapter.getThisPosition().intValue());
//        ParamEpcFilter paramEpcFilter = new ParamEpcFilter();
//        if (!StringUtils.isNullOfEmpty(tagInfo.getTid())) {
//            paramEpcFilter.setArea(2);
//            paramEpcFilter.setBitStart(0);
//            paramEpcFilter.setHexData(tagInfo.getTid());
//            paramEpcFilter.setBitLength(tagInfo.getTid().length() * 4);
//        } else {
//            paramEpcFilter.setArea(1);
//            paramEpcFilter.setHexData(tagInfo.getEpc());
//            paramEpcFilter.setBitStart(32);
//            paramEpcFilter.setBitLength(tagInfo.getEpc().length() * 4);
//        }
//        msgBaseWriteEpc.setFilter(paramEpcFilter);
//        msgBaseWriteEpc.setBlock(1);
//        msgBaseWriteEpc.setStart(0);
//        msgBaseWriteEpc.setHexWriteData("F5F5");
//        this.client.sendSynMsg(msgBaseWriteEpc, 7000);
//        Log.e("lightOn", msgBaseWriteEpc.getRtMsg());
//    }
//
//    private void mnv2d() {
//        MsgBaseInventoryEpc msgBaseInventoryEpc = new MsgBaseInventoryEpc();
//        msgBaseInventoryEpc.setAntennaEnable(Long.valueOf(1L));
//        msgBaseInventoryEpc.setInventoryMode(1);
//        ParamEpcReadUserdata paramEpcReadUserdata = new ParamEpcReadUserdata();
//        paramEpcReadUserdata.setStart(31);
//        paramEpcReadUserdata.setLen(1);
//        msgBaseInventoryEpc.setReadUserdata(paramEpcReadUserdata);
//        ParamEpcReadReserved paramEpcReadReserved = new ParamEpcReadReserved();
//        paramEpcReadReserved.setStart(8);
//        paramEpcReadReserved.setLen(1);
//        msgBaseInventoryEpc.setReadReserved(paramEpcReadReserved);
//        this.client.sendSynMsg(msgBaseInventoryEpc);
//        if (msgBaseInventoryEpc.getRtCode() == 0) {
//            System.out.println("MsgBaseInventoryEpc[OK].");
//        } else {
//            System.out.println(msgBaseInventoryEpc.getRtMsg());
//        }
//    }
//
//    private void soundTask() {
//        Runnable runnable = new Runnable() {
//            public void run() {
//                if (rateValue != 0L)
//                    UtilSound.play(1, 0);
//                soundHandler.postDelayed(this, 20L);
//            }
//        };
//        this.timeTask = runnable;
//        this.soundHandler.postDelayed(runnable, 0L);
//    }
//
//    private void upDataPane() {
//        this.tagInfoList.clear();
//        this.tagInfoList.addAll(this.tagInfoMap.values());
//        this.adapter.notifyData(this.tagInfoList);
//        TextView textView1 = this.readCount;
//        StringBuilder stringBuilder2 = new StringBuilder();
//        stringBuilder2.append(getReadCount(this.tagInfoList));
//        stringBuilder2.append("");
//        textView1.setText(stringBuilder2.toString());
//        TextView textView2 = this.tagCount;
//        StringBuilder stringBuilder1 = new StringBuilder();
//        stringBuilder1.append(this.tagInfoList.size());
//        stringBuilder1.append("");
//        textView2.setText(stringBuilder1.toString());
//    }
//
//    protected void attachBaseContext(Context paramContext) {
//        super.attachBaseContext(LocalManageUtil.setLocal(paramContext));
//    }
//
//    public void blockDialog() {
//        View view = getLayoutInflater().inflate(2131492893, null, false);
//        this.block_size_edit = (EditText)view.findViewById(2131296316);
//        String str = (String)SharedPreferencesUtil.getData("blockSize", "99");
//        this.block_size_edit.setText(str);
//        AlertDialog.Builder builder = new AlertDialog.Builder((Context)this);
//        builder.setCancelable(false);
//        builder.setView(view);
//        builder.setPositiveButton("OK", null);
//        final AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//        alertDialog.getButton(-1).setOnClickListener(new View.OnClickListener() {
//            public void onClick(View param1View) {
//                alertDialog.dismiss();
//                eBookWrite();
//            }
//        });
//    }
//
//    public void cleanData() {
//        if (this.isClient) {
//            initPane();
//        } else {
//        }
//    }
//
//    public double computedNmv2d(String paramString1, String paramString2) {
//        int i;
//        String str = paramString1.substring(0, 2);
//        paramString1 = paramString1.substring(2, 4);
//        if (str.contains("1")) {
//            i = -Integer.parseInt(paramString1, 16);
//        } else {
//            i = Integer.parseInt(paramString1, 16);
//        }
//        double d = (Integer.parseInt(paramString2.substring(1, 4), 16) + i - 500);
//        Double.isNaN(d);
//        return d / 5.4817D + 24.9D;
//    }
//
//
//    public void eBookWrite() {
//        if (this.adapter.getThisPosition() != null) {
//            MsgBaseWriteEpc msgBaseWriteEpc = new MsgBaseWriteEpc();
//            msgBaseWriteEpc.setAntennaEnable(Long.valueOf(1L));
//            msgBaseWriteEpc.setArea(3);
//            TagInfo tagInfo = this.tagInfoList.get(this.adapter.getThisPosition().intValue());
//            ParamEpcFilter paramEpcFilter = new ParamEpcFilter();
//            if (!StringUtils.isNullOfEmpty(tagInfo.getTid())) {
//                paramEpcFilter.setArea(2);
//                paramEpcFilter.setBitStart(0);
//                paramEpcFilter.setHexData(tagInfo.getTid());
//                paramEpcFilter.setBitLength(tagInfo.getTid().length() * 4);
//            } else {
//                paramEpcFilter.setArea(1);
//                paramEpcFilter.setHexData(tagInfo.getEpc());
//                paramEpcFilter.setBitStart(32);
//                paramEpcFilter.setBitLength(tagInfo.getEpc().length() * 4);
//            }
//            msgBaseWriteEpc.setFilter(paramEpcFilter);
//            msgBaseWriteEpc.setStart(0);
//            msgBaseWriteEpc.setHexWriteData("1234");
//            msgBaseWriteEpc.seteBookFlag(1);
//            msgBaseWriteEpc.setBlock(Integer.parseInt(this.block_size_edit.getText().toString()));
//            SharedPreferencesUtil.putData("blockSize", this.block_size_edit.getText().toString());
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append(msgBaseWriteEpc.getBlock());
//            stringBuilder.append("");
//            Log.e("blockSize", stringBuilder.toString());
//            this.client.sendSynMsg(msgBaseWriteEpc, 10000);
//        } else {
//        }
//    }
//
//
//    public void getTabHead() {
//        AlertDialog.Builder builder = (new AlertDialog.Builder((Context)this)).setTitle(getResources().getString(2131755378));
//        String str1 = getResources().getString(2131755377);
//        String str2 = getResources().getString(2131755380);
//        String str3 = getResources().getString(2131755376);
//        boolean[] arrayOfBoolean = this.isChecked;
//        DialogInterface.OnMultiChoiceClickListener onMultiChoiceClickListener = new DialogInterface.OnMultiChoiceClickListener() {
//            public void onClick(DialogInterface param1DialogInterface, int param1Int, boolean param1Boolean) {
//                PrintStream printStream = System.out;
//                StringBuilder stringBuilder = new StringBuilder();
//                stringBuilder.append(param1Int);
//                stringBuilder.append("--");
//                stringBuilder.append(param1Boolean);
//                printStream.println(stringBuilder.toString());
//            }
//        };
//        AlertDialog alertDialog = builder.setMultiChoiceItems((CharSequence[])new String[] { str1, str2, str3 }, arrayOfBoolean, onMultiChoiceClickListener).setCancelable(false).setPositiveButton("confirm", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface param1DialogInterface, int param1Int) {}
//        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface param1DialogInterface, int param1Int) {
//                param1DialogInterface.dismiss();
//            }
//        }).show();
//        alertDialog.getButton(-1).setTextSize(23.0F);
//        alertDialog.getButton(-2).setTextSize(23.0F);
//        alertDialog.getButton(-1).setTextColor(Color.rgb(0, 87, 75));
//        alertDialog.getButton(-2).setTextColor(Color.rgb(0, 87, 75));
//        try {
//            Field field1 = AlertDialog.class.getDeclaredField("mAlert");
//            field1.setAccessible(true);
//            Object object = field1.get(alertDialog);
//            Field field2 = object.getClass().getDeclaredField("mTitleView");
//            field2.setAccessible(true);
//            object = field2.get(object);
//            object.setTextSize(23.0F);
//            object.setTextColor(Color.rgb(0, 87, 75));
//        } catch (IllegalAccessException illegalAccessException) {
//            illegalAccessException.printStackTrace();
//        } catch (NoSuchFieldException noSuchFieldException) {
//            noSuchFieldException.printStackTrace();
//        }
//    }
//
//    public Integer getUserDataChild() {
//        return Integer.valueOf(this.write_gb_user_child.getSelectedItemPosition() + 48);
//    }
//
//    public void initCusRead(View paramView) {
//        this.cus_read_mode = (Spinner)paramView.findViewById(2131296419);
//        this.cus_read_start = (EditText)paramView.findViewById(2131296423);
//        this.cus_read_match_content = (EditText)paramView.findViewById(2131296418);
//        this.cus_read_tid_mode = (Spinner)paramView.findViewById(2131296425);
//        this.cus_read_tid_len = (EditText)paramView.findViewById(2131296424);
//        this.read_tid_true = (CheckBox)paramView.findViewById(2131296589);
//        this.cus_read_user_start = (EditText)paramView.findViewById(2131296427);
//        this.cus_read_user_len = (EditText)paramView.findViewById(2131296426);
//        this.read_user_true = (CheckBox)paramView.findViewById(2131296590);
//        this.cus_read_reserve_start = (EditText)paramView.findViewById(2131296422);
//        this.cus_read_reserve_len = (EditText)paramView.findViewById(2131296421);
//        this.read_reserve_true = (CheckBox)paramView.findViewById(2131296588);
//        this.read_other_pas = (CheckBox)paramView.findViewById(2131296587);
//        this.cus_read_pas = (EditText)paramView.findViewById(2131296420);
//        this.ltu27Box = (CheckBox)paramView.findViewById(2131296546);
//        this.ltu31Box = (CheckBox)paramView.findViewById(2131296547);
//        this.nmv2dBox = (CheckBox)paramView.findViewById(2131296260);
//        this.cus_read_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            public void onItemSelected(AdapterView<?> param1AdapterView, View param1View, int param1Int, long param1Long) {
//                if (param1Int == 1) {
//                    cus_read_start.setText("32");
//                } else {
//                    cus_read_start.setText("0");
//                }
//            }
//
//            public void onNothingSelected(AdapterView<?> param1AdapterView) {}
//        });
//    }
//
//    public void initRecycleView() {
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((Context)this);
//        linearLayoutManager.setOrientation(1);
//        RecyclerView recyclerView = (RecyclerView)findViewById(2131296591);
//        recyclerView.setLayoutManager((RecyclerView.LayoutManager)linearLayoutManager);
//        recyclerView.addItemDecoration((RecyclerView.ItemDecoration)new DividerItemDecoration((Context)this, 1));
//        RecycleViewAdapter recycleViewAdapter = new RecycleViewAdapter(this.tagInfoList);
//        this.adapter = recycleViewAdapter;
//        recyclerView.setAdapter((RecyclerView.Adapter)recycleViewAdapter);
//    }
//
//    public void notifySystemToScan(String paramString) {
//        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
//        intent.setData(Uri.fromFile(new File(paramString)));
//        sendBroadcast(intent);
//    }
//
//    @Override
//    protected void onCreate(Bundle paramBundle) {
//        super.onCreate(paramBundle);
//        getWindow().addFlags(128);
//        ButterKnife.bind((Activity)this);
//        boolean bool = getIntent().getBooleanExtra("isClient", false);
//        this.isClient = bool;
//
//        if (bool)
//            subHandler(GlobalClient.getClient());
//
//        initRecycleView();
//        UtilSound.initSoundPool((Context)this);
//    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu paramMenu) {
//        return true;
//    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (this.isClient && this.isReader) {
//            MsgBaseStop msgBaseStop = new MsgBaseStop();
//            this.client.sendSynMsg(msgBaseStop);
//            if (msgBaseStop.getRtCode() == 0) {
//            } else {
//            }
//        }
//    }
//    @Override
//    public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(paramInt);
//        stringBuilder.append("");
//        Log.e("onKeyDown", stringBuilder.toString());
//        if (paramInt == 131 || paramInt == 285 || paramInt == 289 || paramInt == 291)
//            if (this.isReader) {
//                stopRead();
//            } else {
//                readCard();
//            }
//        return super.onKeyDown(paramInt, paramKeyEvent);
//    }
//    @Override
//    public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent) {
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(paramInt);
//        stringBuilder.append("");
//        Log.e("onKeyUp", stringBuilder.toString());
//        return super.onKeyUp(paramInt, paramKeyEvent);
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
//        int i = paramMenuItem.getItemId();
//        if (i != 2131296556) {
//            if (i != 2131296639) {
//                if (i == 2131296647)
//                    if (this.isSound) {
//                        this.isSound = false;
//                        this.gMenuItem.setTitle(getResources().getString(2131755300));
//                    } else {
//                        this.isSound = true;
//                        this.gMenuItem.setTitle(getResources().getString(2131755299));
//                    }
//            } else {
//                MsgBaseSetBaseband msgBaseSetBaseband = new MsgBaseSetBaseband();
//                msgBaseSetBaseband.setqValue(0);
//                msgBaseSetBaseband.setInventoryFlag(2);
//                GlobalClient.getClient().sendSynMsg(msgBaseSetBaseband);
//                if (msgBaseSetBaseband.getRtCode() == 0) {
//                } else {
//                }
//            }
//        } else {
//            MsgBaseSetBaseband msgBaseSetBaseband = new MsgBaseSetBaseband();
//            msgBaseSetBaseband.setqValue(4);
//            msgBaseSetBaseband.setInventoryFlag(2);
//            GlobalClient.getClient().sendSynMsg(msgBaseSetBaseband);
//            if (msgBaseSetBaseband.getRtCode() == 0) {
//            } else {
//            }
//        }
//        return true;
//    }
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (this.isClient && this.isReader) {
//            MsgBaseStop msgBaseStop = new MsgBaseStop();
//            this.client.sendSynMsg(msgBaseStop);
//            if (msgBaseStop.getRtCode() == 0) {
//                this.isReader = false;
//            } else {
//            }
//        }
//    }
//
//
//    public Map<String, TagInfo> pooled6bData(LogBase6bInfo paramLogBase6bInfo) {
//        if (this.tagInfoMap.containsKey(paramLogBase6bInfo.getTid())) {
//            TagInfo tagInfo = this.tagInfoMap.get(paramLogBase6bInfo.getTid());
//            long l = ((TagInfo)this.tagInfoMap.get(paramLogBase6bInfo.getTid())).getCount().longValue();
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append(paramLogBase6bInfo.getRssi());
//            stringBuilder.append("");
//            tagInfo.setRssi(stringBuilder.toString());
//            tagInfo.setCount(Long.valueOf(l + 1L));
//            this.tagInfoMap.put(paramLogBase6bInfo.getTid(), tagInfo);
//        } else {
//            TagInfo tagInfo = new TagInfo();
//            tagInfo.setIndex(this.index);
//            tagInfo.setType("6B");
//            tagInfo.setCount(Long.valueOf(1L));
//            tagInfo.setUserData(paramLogBase6bInfo.getUserdata());
//            if (paramLogBase6bInfo.getTid() != null)
//                tagInfo.setTid(paramLogBase6bInfo.getTid());
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append(paramLogBase6bInfo.getRssi());
//            stringBuilder.append("");
//            tagInfo.setRssi(stringBuilder.toString());
//            tagInfo.setReadTime(new Date());
//            this.tagInfoMap.put(paramLogBase6bInfo.getTid(), tagInfo);
//            this.index = Long.valueOf(this.index.longValue() + 1L);
//        }
//        return this.tagInfoMap;
//    }
//
//    public Map<String, TagInfo> pooled6cData(LogBaseEpcInfo paramLogBaseEpcInfo) {
//        Map<String, TagInfo> map = this.tagInfoMap;
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(paramLogBaseEpcInfo.getTid());
//        stringBuilder.append(paramLogBaseEpcInfo.getEpc());
//        if (map.containsKey(stringBuilder.toString())) {
//            Map<String, TagInfo> map1 = this.tagInfoMap;
//            StringBuilder stringBuilder1 = new StringBuilder();
//            stringBuilder1.append(paramLogBaseEpcInfo.getTid());
//            stringBuilder1.append(paramLogBaseEpcInfo.getEpc());
//            TagInfo tagInfo = map1.get(stringBuilder1.toString());
//            Map<String, TagInfo> map2 = this.tagInfoMap;
//            StringBuilder stringBuilder2 = new StringBuilder();
//            stringBuilder2.append(paramLogBaseEpcInfo.getTid());
//            stringBuilder2.append(paramLogBaseEpcInfo.getEpc());
//            long l = ((TagInfo)map2.get(stringBuilder2.toString())).getCount().longValue();
//            stringBuilder2 = new StringBuilder();
//            stringBuilder2.append(paramLogBaseEpcInfo.getRssi());
//            stringBuilder2.append("");
//            tagInfo.setRssi(stringBuilder2.toString());
//            tagInfo.setReservedData(paramLogBaseEpcInfo.getReserved());
//            tagInfo.setUserData(paramLogBaseEpcInfo.getUserdata());
//            tagInfo.setCount(Long.valueOf(l + 1L));
//            map2 = this.tagInfoMap;
//            stringBuilder2 = new StringBuilder();
//            stringBuilder2.append(paramLogBaseEpcInfo.getTid());
//            stringBuilder2.append(paramLogBaseEpcInfo.getEpc());
//            map2.put(stringBuilder2.toString(), tagInfo);
//        } else {
//            TagInfo tagInfo = new TagInfo();
//            tagInfo.setIndex(this.index);
//            tagInfo.setType("6C");
//            tagInfo.setEpc(paramLogBaseEpcInfo.getEpc());
//            tagInfo.setCount(Long.valueOf(1L));
//            tagInfo.setUserData(paramLogBaseEpcInfo.getUserdata());
//            tagInfo.setReservedData(paramLogBaseEpcInfo.getReserved());
//            tagInfo.setTid(paramLogBaseEpcInfo.getTid());
//            stringBuilder = new StringBuilder();
//            stringBuilder.append(paramLogBaseEpcInfo.getRssi());
//            stringBuilder.append("");
//            tagInfo.setRssi(stringBuilder.toString());
//            tagInfo.setReadTime(new Date());
//            Map<String, TagInfo> map1 = this.tagInfoMap;
//            StringBuilder stringBuilder1 = new StringBuilder();
//            stringBuilder1.append(paramLogBaseEpcInfo.getTid());
//            stringBuilder1.append(paramLogBaseEpcInfo.getEpc());
//            map1.put(stringBuilder1.toString(), tagInfo);
//            this.index = Long.valueOf(this.index.longValue() + 1L);
//        }
//        return this.tagInfoMap;
//    }
//
//    public Map<String, TagInfo> pooledGJbData(LogBaseGJbInfo paramLogBaseGJbInfo) {
//        Map<String, TagInfo> map = this.tagInfoMap;
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(paramLogBaseGJbInfo.getTid());
//        stringBuilder.append(paramLogBaseGJbInfo.getEpc());
//        if (map.containsKey(stringBuilder.toString())) {
//            map = this.tagInfoMap;
//            stringBuilder = new StringBuilder();
//            stringBuilder.append(paramLogBaseGJbInfo.getTid());
//            stringBuilder.append(paramLogBaseGJbInfo.getEpc());
//            TagInfo tagInfo = map.get(stringBuilder.toString());
//            Map<String, TagInfo> map2 = this.tagInfoMap;
//            StringBuilder stringBuilder2 = new StringBuilder();
//            stringBuilder2.append(paramLogBaseGJbInfo.getTid());
//            stringBuilder2.append(paramLogBaseGJbInfo.getEpc());
//            long l = ((TagInfo)map2.get(stringBuilder2.toString())).getCount().longValue();
//            StringBuilder stringBuilder1 = new StringBuilder();
//            stringBuilder1.append(paramLogBaseGJbInfo.getRssi());
//            stringBuilder1.append("");
//            tagInfo.setRssi(stringBuilder1.toString());
//            tagInfo.setCount(Long.valueOf(l + 1L));
//            Map<String, TagInfo> map1 = this.tagInfoMap;
//            stringBuilder2 = new StringBuilder();
//            stringBuilder2.append(paramLogBaseGJbInfo.getTid());
//            stringBuilder2.append(paramLogBaseGJbInfo.getEpc());
//            map1.put(stringBuilder2.toString(), tagInfo);
//        } else {
//            TagInfo tagInfo = new TagInfo();
//            tagInfo.setIndex(this.index);
//            tagInfo.setType("GJB");
//            tagInfo.setEpc(paramLogBaseGJbInfo.getEpc());
//            tagInfo.setCount(Long.valueOf(1L));
//            tagInfo.setUserData(paramLogBaseGJbInfo.getUserdata());
//            tagInfo.setTid(paramLogBaseGJbInfo.getTid());
//            stringBuilder = new StringBuilder();
//            stringBuilder.append(paramLogBaseGJbInfo.getRssi());
//            stringBuilder.append("");
//            tagInfo.setRssi(stringBuilder.toString());
//            tagInfo.setReadTime(new Date());
//            Map<String, TagInfo> map1 = this.tagInfoMap;
//            StringBuilder stringBuilder1 = new StringBuilder();
//            stringBuilder1.append(paramLogBaseGJbInfo.getTid());
//            stringBuilder1.append(paramLogBaseGJbInfo.getEpc());
//            map1.put(stringBuilder1.toString(), tagInfo);
//            this.index = Long.valueOf(this.index.longValue() + 1L);
//        }
//        return this.tagInfoMap;
//    }
//
//    public Map<String, TagInfo> pooledGbData(LogBaseGbInfo paramLogBaseGbInfo) {
//        Map<String, TagInfo> map = this.tagInfoMap;
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(paramLogBaseGbInfo.getTid());
//        stringBuilder.append(paramLogBaseGbInfo.getEpc());
//        if (map.containsKey(stringBuilder.toString())) {
//            map = this.tagInfoMap;
//            stringBuilder = new StringBuilder();
//            stringBuilder.append(paramLogBaseGbInfo.getTid());
//            stringBuilder.append(paramLogBaseGbInfo.getEpc());
//            TagInfo tagInfo = map.get(stringBuilder.toString());
//            map = this.tagInfoMap;
//            StringBuilder stringBuilder2 = new StringBuilder();
//            stringBuilder2.append(paramLogBaseGbInfo.getTid());
//            stringBuilder2.append(paramLogBaseGbInfo.getEpc());
//            long l = ((TagInfo)map.get(stringBuilder2.toString())).getCount().longValue();
//            StringBuilder stringBuilder1 = new StringBuilder();
//            stringBuilder1.append(paramLogBaseGbInfo.getRssi());
//            stringBuilder1.append("");
//            tagInfo.setRssi(stringBuilder1.toString());
//            tagInfo.setCount(Long.valueOf(l + 1L));
//            Map<String, TagInfo> map1 = this.tagInfoMap;
//            stringBuilder1 = new StringBuilder();
//            stringBuilder1.append(paramLogBaseGbInfo.getTid());
//            stringBuilder1.append(paramLogBaseGbInfo.getEpc());
//            map1.put(stringBuilder1.toString(), tagInfo);
//        } else {
//            TagInfo tagInfo = new TagInfo();
//            tagInfo.setIndex(this.index);
//            tagInfo.setType("GB");
//            tagInfo.setEpc(paramLogBaseGbInfo.getEpc());
//            tagInfo.setCount(Long.valueOf(1L));
//            tagInfo.setUserData(paramLogBaseGbInfo.getUserdata());
//            tagInfo.setTid(paramLogBaseGbInfo.getTid());
//            StringBuilder stringBuilder1 = new StringBuilder();
//            stringBuilder1.append(paramLogBaseGbInfo.getRssi());
//            stringBuilder1.append("");
//            tagInfo.setRssi(stringBuilder1.toString());
//            tagInfo.setReadTime(new Date());
//            Map<String, TagInfo> map1 = this.tagInfoMap;
//            stringBuilder1 = new StringBuilder();
//            stringBuilder1.append(paramLogBaseGbInfo.getTid());
//            stringBuilder1.append(paramLogBaseGbInfo.getEpc());
//            map1.put(stringBuilder1.toString(), tagInfo);
//            this.index = Long.valueOf(this.index.longValue() + 1L);
//        }
//        return this.tagInfoMap;
//    }
//
//    public void readCard() {
//        if (this.isClient) {
//            if (!this.isReader) {
//                initPane();
//                if (this.type.getCheckedRadioButtonId() == 2131296326) {
//                    MsgBaseInventoryEpc msg = new MsgBaseInventoryEpc();
//                    msg.setAntennaEnable(Long.valueOf(1L));
//                    if (this.way.getCheckedRadioButtonId() == 2131296639) {
//                        msg.setInventoryMode(0);
//                    } else {
//                        msg.setInventoryMode(1);
//                    }
//
//                    if (this.isChecked[0]) {
//                        ParamEpcReadTid paramEpcReadTid = new ParamEpcReadTid();
//                        this.tidParam = paramEpcReadTid;
//                        paramEpcReadTid.setMode(0);
//                        this.tidParam.setLen(6);
//                        msg.setReadTid(this.tidParam);
//                    }
//                    if (this.isChecked[1]) {
//                        ParamEpcReadUserdata paramEpcReadUserdata = new ParamEpcReadUserdata();
//                        this.userParam = paramEpcReadUserdata;
//                        paramEpcReadUserdata.setStart(0);
//                        this.userParam.setLen(2);
//                        msg.setReadUserdata(this.userParam);
//                    }
//                    if (this.isChecked[2]) {
//                        ParamEpcReadReserved paramEpcReadReserved = new ParamEpcReadReserved();
//                        this.reserveParam = paramEpcReadReserved;
//                        paramEpcReadReserved.setStart(0);
//                        this.reserveParam.setLen(4);
//                        msg.setReadReserved(this.reserveParam);
//                    }
//
//                    this.client.sendSynMsg(msg);
//
//                    if (msg.getRtCode() == 0) {
//
//                        this.isReader = true;
//                        computedSpeed();
//                        soundTask();
//
//                    } else {
//                        this.handlerStop.sendEmptyMessage(1);
//                    }
//                } else if (this.type.getCheckedRadioButtonId() == 2131296306) {
//                    MsgBaseInventory6b msgBaseInventory6b = new MsgBaseInventory6b();
//                    msgBaseInventory6b.setAntennaEnable(Long.valueOf(1L));
//                    if (this.way.getCheckedRadioButtonId() == 2131296639) {
//                        msgBaseInventory6b.setInventoryMode(0);
//                    } else {
//                        msgBaseInventory6b.setInventoryMode(1);
//                    }
//                    if (this.isChecked[1]) {
//                        Param6bReadUserdata param6bReadUserdata = new Param6bReadUserdata();
//                        this.user6bParam = param6bReadUserdata;
//                        param6bReadUserdata.setStart(0);
//                        this.user6bParam.setLen(10);
//                        msgBaseInventory6b.setReadUserdata(this.user6bParam);
//                    }
//                    msgBaseInventory6b.setArea(0);
//                    this.client.sendSynMsg(msgBaseInventory6b);
//                    if (msgBaseInventory6b.getRtCode() == 0) {
//                        this.isReader = true;
//                        computedSpeed();
//                        soundTask();
//                    } else {
//                        this.handlerStop.sendEmptyMessage(1);
//                    }
//                } else if (this.type.getCheckedRadioButtonId() == 2131296503) {
//                    MsgBaseInventoryGb msgBaseInventoryGb = new MsgBaseInventoryGb();
//                    msgBaseInventoryGb.setAntennaEnable(Long.valueOf(1L));
//                    if (this.way.getCheckedRadioButtonId() == 2131296639) {
//                        msgBaseInventoryGb.setInventoryMode(0);
//                    } else {
//                        msgBaseInventoryGb.setInventoryMode(1);
//                    }
//                    if (this.isChecked[0]) {
//                        ParamEpcReadTid paramEpcReadTid = new ParamEpcReadTid();
//                        paramEpcReadTid.setMode(0);
//                        paramEpcReadTid.setLen(6);
//                        msgBaseInventoryGb.setReadTid(paramEpcReadTid);
//                    }
//                    if (this.isChecked[1]) {
//                        ParamGbReadUserdata paramGbReadUserdata = new ParamGbReadUserdata();
//                        paramGbReadUserdata.setStart(4);
//                        paramGbReadUserdata.setLen(1);
//                        paramGbReadUserdata.setChildArea(48);
//                        msgBaseInventoryGb.setReadUserdata(paramGbReadUserdata);
//                    }
//                    this.client.sendSynMsg(msgBaseInventoryGb);
//                    if (msgBaseInventoryGb.getRtCode() == 0) {
//                        this.isReader = true;
//                        computedSpeed();
//                        soundTask();
//                    } else {
//                        this.handlerStop.sendEmptyMessage(1);
//                    }
//                } else if (this.type.getCheckedRadioButtonId() == 2131296505) {
//                    MsgBaseInventoryGJb msgBaseInventoryGJb = new MsgBaseInventoryGJb();
//                    msgBaseInventoryGJb.setAntennaEnable(Long.valueOf(1L));
//                    if (this.way.getCheckedRadioButtonId() == 2131296639) {
//                        msgBaseInventoryGJb.setInventoryMode(0);
//                    } else {
//                        msgBaseInventoryGJb.setInventoryMode(1);
//                    }
//                    if (this.isChecked[0]) {
//                        ParamEpcReadTid paramEpcReadTid = new ParamEpcReadTid();
//                        paramEpcReadTid.setMode(0);
//                        paramEpcReadTid.setLen(6);
//                        msgBaseInventoryGJb.setReadTid(paramEpcReadTid);
//                    }
//                    if (this.isChecked[1]) {
//                        ParamEpcReadUserdata paramEpcReadUserdata = new ParamEpcReadUserdata();
//                        this.userParam = paramEpcReadUserdata;
//                        paramEpcReadUserdata.setStart(0);
//                        this.userParam.setLen(2);
//                        msgBaseInventoryGJb.setReadUserdata(this.userParam);
//                    }
//                    this.client.sendSynMsg(msgBaseInventoryGJb);
//                    if (msgBaseInventoryGJb.getRtCode() == 0) {
//                        this.isReader = true;
//                        computedSpeed();
//                        soundTask();
//                    } else {
//                        this.handlerStop.sendEmptyMessage(1);
//                    }
//                }
//
//                this.isCtesius = 0;
//            } else {
//            }
//        } else {
//        }
//    }
//
//    public String secToTime(long paramLong) {
//        this.formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
//        return this.formatter.format(Long.valueOf(paramLong * 1000L));
//    }
//
//    public void stopRead() {
//        if (this.isClient) {
//            MsgBaseStop msgBaseStop = new MsgBaseStop();
//            this.client.sendSynMsg(msgBaseStop);
//            if (msgBaseStop.getRtCode() == 0) {
//                this.isReader = false;
//            } else {
//            }
//        } else {
//        }
//    }
//
//    public void subHandler(GClient paramGClient) {
//
//        paramGClient.onTagEpcLog = new HandlerTagEpcLog() {
//            public void log(String param1String, LogBaseEpcInfo param1LogBaseEpcInfo) {
//                if (param1LogBaseEpcInfo.getResult() == 0)
//                    synchronized (tagInfoList) {
//                        pooled6cData(param1LogBaseEpcInfo);
//                    }
//            }
//        };
//        paramGClient.onTagEpcOver = new HandlerTagEpcOver() {
//            public void log(String param1String, LogBaseEpcOver param1LogBaseEpcOver) {
//                handlerStop.sendEmptyMessage(1);
//            }
//        };
//        paramGClient.onTag6bLog = new HandlerTag6bLog() {
//            public void log(String param1String, LogBase6bInfo param1LogBase6bInfo) {
//                if (param1LogBase6bInfo != null && param1LogBase6bInfo.getResult() == 0)
//                    synchronized (tagInfoList) {
//                        pooled6bData(param1LogBase6bInfo);
//                    }
//            }
//        };
//        paramGClient.onTag6bOver = new HandlerTag6bOver() {
//            public void log(String param1String, LogBase6bOver param1LogBase6bOver) {
//                handlerStop.sendEmptyMessage(1);
//            }
//        };
//        paramGClient.onTagGbLog = new HandlerTagGbLog() {
//            public void log(String param1String, LogBaseGbInfo param1LogBaseGbInfo) {
//                if (param1LogBaseGbInfo != null && param1LogBaseGbInfo.getResult() == 0)
//                    synchronized (tagInfoList) {
//                        pooledGbData(param1LogBaseGbInfo);
//                    }
//            }
//        };
//        paramGClient.onTagGbOver = new HandlerTagGbOver() {
//            public void log(String param1String, LogBaseGbOver param1LogBaseGbOver) {
//                Log.e("HandlerTagGbOver", "-------------HandlerTagGbOver");
//                handlerStop.sendEmptyMessage(1);
//            }
//        };
//        paramGClient.onTagGJbLog = new HandlerTagGJbLog() {
//            public void log(String param1String, LogBaseGJbInfo param1LogBaseGJbInfo) {
//                if (param1LogBaseGJbInfo.getResult() == 0)
//                    synchronized (tagInfoList) {
//                        pooledGJbData(param1LogBaseGJbInfo);
//                    }
//            }
//        };
//        paramGClient.onTagGJbOver = new HandlerTagGJbOver() {
//            public void log(String param1String, LogBaseGJbOver param1LogBaseGJbOver) {
//                Log.e("HandlerTagGJbOver", "-------------HandlerTagGJbOver");
//                handlerStop.sendEmptyMessage(1);
//            }
//        };
//        paramGClient.onGpiOver = new HandlerGpiOver() {
//            public void log(String param1String, LogAppGpiOver param1LogAppGpiOver) {
//                System.out.println(param1LogAppGpiOver);
//            }
//        };
//        paramGClient.onGpiStart = new HandlerGpiStart() {
//            public void log(String param1String, LogAppGpiStart param1LogAppGpiStart) {
//                System.out.println(param1LogAppGpiStart);
//            }
//        };
//        paramGClient.debugLog = new HandlerDebugLog() {
//            public void receiveDebugLog(String param1String) {
//                Log.e("receiveDebugLog", param1String);
//            }
//
//            public void sendDebugLog(String param1String) {
//                Log.e("sendDebugLog", param1String);
//            }
//        };
//    }
//
//    public void tagInfo() {
//        if (this.adapter.getThisPosition() != null) {
//            View view = getLayoutInflater().inflate(2131492950, null, false);
//            this.info_index = (TextView)view.findViewById(2131296522);
//            this.info_type = (TextView)view.findViewById(2131296524);
//            this.info_epc = (TextView)view.findViewById(2131296521);
//            this.info_tid = (TextView)view.findViewById(2131296523);
//            this.info_userData = (TextView)view.findViewById(2131296525);
//            this.info_Reserved = (TextView)view.findViewById(2131296520);
//            AlertDialog.Builder builder = new AlertDialog.Builder((Context)this);
//            builder.setView(view);
//            builder.create().show();
//            TextView textView = this.info_index;
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append(((TagInfo)this.tagInfoList.get(this.adapter.getThisPosition().intValue())).getIndex());
//            stringBuilder.append("");
//            textView.setText(stringBuilder.toString());
//            this.info_type.setText(((TagInfo)this.tagInfoList.get(this.adapter.getThisPosition().intValue())).getType());
//            this.info_epc.setText(((TagInfo)this.tagInfoList.get(this.adapter.getThisPosition().intValue())).getEpc());
//            this.info_tid.setText(((TagInfo)this.tagInfoList.get(this.adapter.getThisPosition().intValue())).getTid());
//            this.info_userData.setText(((TagInfo)this.tagInfoList.get(this.adapter.getThisPosition().intValue())).getUserData());
//            this.info_Reserved.setText(((TagInfo)this.tagInfoList.get(this.adapter.getThisPosition().intValue())).getReservedData());
//        } else {
//        }
//    }
//
//}
