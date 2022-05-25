package com.example.gxwl.rederdemo.adapter;


import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gxwl.rederdemo.R;
import com.example.gxwl.rederdemo.ReadOrWriteActivity;
import com.example.gxwl.rederdemo.SAED.SocketClient;
import com.example.gxwl.rederdemo.entity.TagInfo;

import java.util.List;
import java.util.concurrent.CountDownLatch;


public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {
    private List<TagInfo> mTagList;
    private Integer thisPosition = null;
    public boolean threadKill = true;
    Activity activity;
    onScanItem onScanItem;
    int latestPosition = 0;

    public Thread thread;

    public Integer getThisPosition() {
        return thisPosition;
    }

    public void setThisPosition(Integer thisPosition) {
        this.thisPosition = thisPosition;
    }

    public RecycleViewAdapter(List<TagInfo> list, Activity activity) {
        mTagList = list;
        this.activity = activity;

        thread = new Thread(() -> {

            onScanItem = () -> {
                thread.interrupt();
            };

            while (threadKill) {

                for (int i = latestPosition; i < mTagList.size(); i++) {
                    if (((ReadOrWriteActivity) activity).socketClient.isConnected())
                        ((ReadOrWriteActivity) activity).socketClient.sendString(mTagList.get(i).getEpc());
                }

                latestPosition = mTagList.size();

                try {
                    thread.sleep(1500000000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
    }

    @Override
    public int getItemCount() {
        return mTagList != null ? mTagList.size() : 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item, parent, false);
        final RecycleViewAdapter.ViewHolder holder = new RecycleViewAdapter.ViewHolder(view);
        view.setOnClickListener(v -> {
            TagInfo tag = mTagList.get(holder.getAdapterPosition());
            setThisPosition(holder.getAdapterPosition());
            notifyDataSetChanged();
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TagInfo tag = mTagList.get(position);
        holder.index.setText(tag.getIndex().toString());
        holder.type.setText(tag.getType());
        holder.epc.setText(tag.getEpc());
        holder.tid.setText(tag.getTid());
        holder.userData.setText(tag.getUserData());
        holder.reserveData.setText(tag.getReservedData());
        holder.count.setText(tag.getCount().toString());
        holder.rssi.setText(tag.getRssi());
        if (getThisPosition() != null && position == getThisPosition()) {
            holder.index.setBackgroundColor(Color.rgb(135, 206, 235));
            holder.type.setBackgroundColor(Color.rgb(135, 206, 235));
            holder.tid.setBackgroundColor(Color.rgb(135, 206, 235));
            holder.epc.setBackgroundColor(Color.rgb(135, 206, 235));
            holder.count.setBackgroundColor(Color.rgb(135, 206, 235));
            holder.rssi.setBackgroundColor(Color.rgb(135, 206, 235));
            holder.userData.setBackgroundColor(Color.rgb(135, 206, 235));
            holder.reserveData.setBackgroundColor(Color.rgb(135, 206, 235));
        } else {
            holder.index.setBackgroundColor(Color.WHITE);
            holder.type.setBackgroundColor(Color.WHITE);
            holder.tid.setBackgroundColor(Color.WHITE);
            holder.epc.setBackgroundColor(Color.WHITE);
            holder.count.setBackgroundColor(Color.WHITE);
            holder.rssi.setBackgroundColor(Color.WHITE);
            holder.userData.setBackgroundColor(Color.WHITE);
            holder.reserveData.setBackgroundColor(Color.WHITE);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView index;
        TextView type;
        TextView epc;
        TextView tid;
        TextView rssi;
        TextView count;
        TextView userData;
        TextView reserveData;


        public ViewHolder(final View view) {
            super(view);
            index = (TextView) view.findViewById(R.id.index);
            type = (TextView) view.findViewById(R.id.type);
            epc = (TextView) view.findViewById(R.id.epc);
            tid = (TextView) view.findViewById(R.id.tid);
            rssi = (TextView) view.findViewById(R.id.rssi);
            count = (TextView) view.findViewById(R.id.count);
            userData = (TextView) view.findViewById(R.id.userData);
            reserveData = (TextView) view.findViewById(R.id.reserveData);
        }
    }

    public void notifyData(List<TagInfo> poiItemList) {

        if (poiItemList != null) {
            mTagList = poiItemList;
            notifyDataSetChanged();
        }

        onScanItem.getETC();
    }

    interface onScanItem {
        void getETC();
    }

}













