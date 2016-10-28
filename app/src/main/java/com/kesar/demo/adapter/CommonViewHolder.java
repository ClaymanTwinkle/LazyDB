package com.kesar.demo.adapter;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * 万能view holder
 * Created by kesar on 16-10-28.
 */
public class CommonViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> viewArray = new SparseArray<>();

    public CommonViewHolder(View itemView) {
        super(itemView);
    }

    public <T extends View> T getView(@IdRes int viewId){
        View view = viewArray.get(viewId);
        if(view==null){
            view=itemView.findViewById(viewId);
            viewArray.put(viewId,view);
        }
        return (T) view;
    }
}
