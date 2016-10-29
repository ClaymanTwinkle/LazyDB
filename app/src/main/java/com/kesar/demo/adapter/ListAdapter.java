package com.kesar.demo.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * ListAdapter for RecyclerView
 * Created by kesar on 2016/10/29 0029.
 */
public abstract class ListAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private List<T> mData = new ArrayList<>();

    public ListAdapter() {
    }

    public ListAdapter(List<T> data) {
        addAll(data);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        onBindViewHolder(holder, mData.get(position), position);
    }

    public abstract void onBindViewHolder(VH holder, T data, int position);

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public T getItem(int index) {
        return mData.get(index);
    }

    public void addAll(@NonNull List<T> data) {
        int oldSize = mData.size();
        this.mData.addAll(data);
        notifyItemRangeInserted(oldSize, data.size());
    }

    public void add(@NonNull T data) {
        int oldSize = mData.size();
        this.mData.add(data);
        notifyItemInserted(oldSize);
    }

    public void setItem(int index, T data) {
        this.mData.set(index, data);
        notifyItemChanged(index);
    }

    public void clearAll() {
        this.mData.clear();
        notifyDataSetChanged();
    }
}