package com.sanbafule.sharelock.view.customRecycleView;

import android.view.View;

/**
 * 作者:Created by 简玉锋 on 2016/12/29 15:22
 * 邮箱: jianyufeng@38.hn
 */

public class RecyclerViewClick implements View.OnClickListener, View.OnLongClickListener {


    private View view;
    private int pos;
    private Click click;

    public RecyclerViewClick(View itemView, int position, Click click) {
        this.view = itemView;
        this.pos = position;
        this.click = click;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        click.onItemClick(view, pos);
    }

    @Override
    public boolean onLongClick(View v) {
        click.onItemLongClick(view, pos);
        return true;
    }

    public interface Click {
        public void onItemClick(View view, int position);

        public void onItemLongClick(View view, int position);
    }

}
