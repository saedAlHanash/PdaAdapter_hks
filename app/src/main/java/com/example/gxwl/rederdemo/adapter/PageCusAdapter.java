package com.example.gxwl.rederdemo.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.gxwl.rederdemo.fragment.DestroyFragment;
import com.example.gxwl.rederdemo.fragment.LockFragment;
import com.example.gxwl.rederdemo.fragment.WriteFragment;

import java.util.ArrayList;
import java.util.List;

public class PageCusAdapter extends FragmentPagerAdapter {
    private List<String> mTitle = new ArrayList<>();

    private List<Fragment> fragments = new ArrayList<>();

    public PageCusAdapter(FragmentManager fm) {
        super(fm);
        mTitle.add("Write");
        mTitle.add("Lock");
        mTitle.add("Destroy");
        fragments.add(new WriteFragment());
        fragments.add(new LockFragment());
        fragments.add(new DestroyFragment());
        notifyDataSetChanged();
    }

    public List<String> getmTitle() {
        return mTitle;
    }

    public void setmTitle(List<String> mTitle) {
        this.mTitle = mTitle;
    }

    public List<Fragment> getFragments() {
        return fragments;
    }

    public void setFragments(List<Fragment> fragments) {
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return mTitle.get(position);
    }

}
