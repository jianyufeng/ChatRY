package com.sanbafule.sharelock.chatting.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import io.rong.imlib.model.Conversation;

/**
 * Created by Administrator on 2016/11/11.
 */
public class CoversionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<Conversation>list;

    private Context mContext;
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
