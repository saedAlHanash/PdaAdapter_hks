package com.example.gxwl.rederdemo.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gxwl.rederdemo.R;
import com.example.gxwl.rederdemo.SAED.Product;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
created on 19/08/2022 - 11:29 ص
to project PdaAdapter_hks
*/
public class AdapterItemEpc extends RecyclerView.Adapter<AdapterItemEpc.Holder> {

    Activity activity;
    ArrayList<Product> list;

    OnItemClicked listener;


    public AdapterItemEpc(Activity activity, ArrayList<Product> list) {
        this.activity = activity;
        this.list = list;
    }

    /**
     * set new Item List to the adapter and refreshing all item
     *
     * @param list list of new Item {@link #list}
     */
    public void setAndRefresh(ArrayList<Product> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.epc.setText(getItem(position).epc);
        holder.name.setText(getItem(position).pn);
        holder.price.setText(getItem(position).wn);
        holder.imageView9.setImageBitmap(getItem(position).bitmap);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    @SuppressLint("NonConstantResourceId")
    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageView9)
        ImageView imageView9;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.epc)
        TextView epc;
        @BindView(R.id.price)
        TextView price;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * set and init listener to take an action when an item is pressed
     *
     * @param listener Object form interface @{@link OnItemClicked}
     */
    public void setOnItemClicked(OnItemClicked listener) {
        this.listener = listener;
    }

    public interface OnItemClicked {
        /**
         * to call it when item clicked
         *
         * @param position index item in adapter and list
         * @param list     items list of Product
         */
        void onClicked(int position, ArrayList<Product> list);
    }

    /**
     * get an item form {@link #list}
     *
     * @param i position adapter == (index in list)
     * @return object Product from list {@link #list}
     */
    private Product getItem(int i) {
        // return item in index i
        return this.list.get(i);
    }

    /**
     * delete an item form {@link #list}
     *
     * @param i position adapter == (index in list)
     */
    private void deleteItem(int i) {
        // delete item with index i in the list
        this.list.remove(i);
        this.notifyItemRemoved(i);
    }

    /**
     * editing an item form {@link #list}
     *
     * @param i    @param i position adapter == (index in list)
     * @param item new Item data
     */
    private void editItem(int i, Product item) {
        // replace item in index i with new item
        this.list.set(i, item);
        this.notifyItemChanged(i);
    }

    /**
     * adding an item to begin {@link #list}
     *
     * @param item new Item to add it to the list
     */
    private void insertItem(Product item) {
        // add item in begin of list
        this.list.add(0, item);
        this.notifyItemInserted(0);
    }


}
