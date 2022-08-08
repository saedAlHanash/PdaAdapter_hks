package com.example.gxwl.rederdemo.adapter;


import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gxwl.rederdemo.AppConfig.SharedPreference;
import com.example.gxwl.rederdemo.R;
import com.example.gxwl.rederdemo.ReadOrWriteActivity;
import com.example.gxwl.rederdemo.entity.TagInfo;

import java.util.List;


public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

    private List<TagInfo> mTagList;
    private Integer thisPosition = null;
    Activity activity;

    public Integer getThisPosition() {
        return thisPosition;
    }

    public void setThisPosition(Integer thisPosition) {
        this.thisPosition = thisPosition;
    }

    public RecycleViewAdapter(List<TagInfo> list, Activity activity) {
        mTagList = list;
        this.activity = activity;
    }

    void sendData() {
        ((ReadOrWriteActivity) activity).socketClient.sendDataList(this.mTagList, activity, this);
    }


    @Override
    public int getItemCount() {
        return mTagList != null ? mTagList.size() : 0;
    }

    @NonNull
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
//        holder.type.setText(tag.getType());
        holder.epc.setText(tag.getEpc());
//        holder.tid.setText(tag.getTid());
//        holder.userData.setText(tag.getUserData());
//        holder.reserveData.setText(tag.getReservedData());
//        holder.count.setText(tag.getCount().toString());
//        holder.rssi.setText(tag.getRssi());
//        sased:
        if (mTagList.get(position).isSanded)
            holder.imageView.setVisibility(View.VISIBLE);
        else
            holder.imageView.setVisibility(View.INVISIBLE);

        if (getThisPosition() != null && position == getThisPosition()) {
            holder.index.setBackgroundColor(Color.rgb(135, 206, 235));
//            holder.type.setBackgroundColor(Color.rgb(135, 206, 235));
//            holder.tid.setBackgroundColor(Color.rgb(135, 206, 235));
            holder.epc.setBackgroundColor(Color.rgb(135, 206, 235));
//            holder.count.setBackgroundColor(Color.rgb(135, 206, 235));
//            holder.rssi.setBackgroundColor(Color.rgb(135, 206, 235));
//            holder.userData.setBackgroundColor(Color.rgb(135, 206, 235));
//            holder.reserveData.setBackgroundColor(Color.rgb(135, 206, 235));
        } else {
            holder.index.setBackgroundColor(Color.WHITE);
//            holder.type.setBackgroundColor(Color.WHITE);
//            holder.tid.setBackgroundColor(Color.WHITE);
            holder.epc.setBackgroundColor(Color.WHITE);
//            holder.count.setBackgroundColor(Color.WHITE);
//            holder.rssi.setBackgroundColor(Color.WHITE);
//            holder.userData.setBackgroundColor(Color.WHITE);
//            holder.reserveData.setBackgroundColor(Color.WHITE);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView index;
//        TextView type;
        TextView epc;
//        TextView tid;
//        TextView rssi;
//        TextView count;
//        TextView userData;
//        TextView reserveData;
        ImageView imageView;

        public ViewHolder(final View view) {
            super(view);
            index = (TextView) view.findViewById(R.id.index);
            epc = (TextView) view.findViewById(R.id.epc);
//            type = (TextView) view.findViewById(R.id.type);
//            tid = (TextView) view.findViewById(R.id.tid);
//            rssi = (TextView) view.findViewById(R.id.rssi);
//            count = (TextView) view.findViewById(R.id.count);
//            userData = (TextView) view.findViewById(R.id.userData);
//            reserveData = (TextView) view.findViewById(R.id.reserveData);
            imageView = view.findViewById(R.id.imageView6);
        }
    }

    public void notifyData(List<TagInfo> poiItemList) {
        if (poiItemList != null) {
            mTagList = poiItemList;
            sendData();

            notifyDataSetChanged();
        }
    }


}













