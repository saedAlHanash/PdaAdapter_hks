package com.example.gxwl.rederdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.gxwl.rederdemo.adapter.PageCusAdapter;
import com.example.gxwl.rederdemo.entity.TagInfo;
import com.example.gxwl.rederdemo.fragment.DestroyFragment;
import com.example.gxwl.rederdemo.fragment.LockFragment;
import com.example.gxwl.rederdemo.fragment.WriteFragment;
import com.example.gxwl.rederdemo.util.LocalManageUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tong.wan on 2019/3/8 17:11.
 * <p>
 * 自定义写
 */
public class DialogCusActivity extends AppCompatActivity {

    @BindView(R.id.cus_6c_mode)
    Spinner cus_6c_mode;
    //    @BindView(R.id.cus_gb_mode)
//    Spinner cus_gb_mode;
    @BindView(R.id.cus_start)
    EditText cus_start;
    @BindView(R.id.cus_pas)
    EditText cus_pas;
    @BindView(R.id.cus_epc)
    EditText cus_epc;
    @BindView(R.id.cus_tid)
    EditText cus_tid;
    @BindView(R.id.cus_user)
    EditText cus_user;
    @BindView(R.id.tab)
    TabLayout tab;
    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.cus_title)
    TextView cus_title;

    TagInfo tag = null;
    PageCusAdapter adapter = null;
    long ant = 0;
    String[] listener = new String[3];//0:匹配参数 1:匹配起始地址 2:访问密码
    int tagType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_cus);
        ButterKnife.bind(this);
        adapter = new PageCusAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tab.setupWithViewPager(pager);
        tag = (TagInfo) getIntent().getSerializableExtra("Tag");
        ant = getIntent().getLongExtra("Ant", 1);
        tagType = getIntent().getIntExtra("Type", R.id.c);
        if (tagType == R.id.c) {
            String[] stringArray = getResources().getStringArray(R.array.cus_mode);
            ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, stringArray);
            cus_6c_mode.setAdapter(arrayAdapter);
        } else if (tagType == R.id.b) {
            cus_title.setText(getResources().getString(R.string.sixb_tag_cus));
        } else if (tagType == R.id.gb) {
            cus_title.setText(getResources().getString(R.string.gb_tag_cus));
            String[] stringArray = getResources().getStringArray(R.array.cus_gb_mode);
            ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, stringArray);
            cus_6c_mode.setAdapter(arrayAdapter);
        }
        cus_epc.setText(tag.getEpc());
        cus_tid.setText(tag.getTid());
        cus_user.setText(tag.getUserData());
        sendTag(adapter.getFragments(), ant, tagType);
        addListener();
    }

    //分发标签信息与天线、标签类型
    public void sendTag(List<Fragment> fragmentList, long ant, int tagType) {
        for (Fragment fragment : fragmentList) {
            if (fragment instanceof WriteFragment) {
                ((WriteFragment) fragment).receiveTag(tag, ant, tagType);
            } else if (fragment instanceof LockFragment) {
                ((LockFragment) fragment).receiveTag(tag, ant, tagType);
            } else if (fragment instanceof DestroyFragment) {
                ((DestroyFragment) fragment).receiveTag(tag, ant, tagType);
            }
        }
    }

    //分发匹配规则与起始地址、访问密码
    public void sendMessage(List<Fragment> fragmentList, String[] string) {
        for (Fragment fragment : fragmentList) {
            if (fragment instanceof WriteFragment) {
                ((WriteFragment) fragment).receiveListener(string);
            } else if (fragment instanceof LockFragment) {
                ((LockFragment) fragment).receiveListener(string);
            } else if (fragment instanceof DestroyFragment) {
                ((DestroyFragment) fragment).receiveListener(string);
            }
        }
    }

    //集体监听各属性
    public void addListener() {
        cus_6c_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String cusMode = DialogCusActivity.this.getResources().getStringArray(R.array.cus_mode)[position];
//                System.out.println(cusMode);
                listener[0] = position + "";
                if (listener[0].equals("1")) {
                    if (tagType == R.id.c)
                        cus_start.setText(32 + "");
                } else {
                    cus_start.setText(0 + "");
                }
                sendMessage(adapter.getFragments(), listener);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cus_start.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listener[1] = s.toString();
                sendMessage(adapter.getFragments(), listener);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cus_pas.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listener[2] = s.toString();
                sendMessage(adapter.getFragments(), listener);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(LocalManageUtil.setLocal(newBase));
    }
}
