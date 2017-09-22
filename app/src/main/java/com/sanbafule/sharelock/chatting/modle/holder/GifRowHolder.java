package com.sanbafule.sharelock.chatting.modle.holder;

import android.media.Image;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sanbafule.sharelock.R;

import static com.sanbafule.sharelock.R.id.chatting_click_area;

/**
 * Administrator
 * 作者:Created by ShareLock on 2017/1/7 16:53
 * cd : 三八妇乐
 * 描述： Gif 的图片显示
 */
public class GifRowHolder extends BaseHolder {



    public ImageView gifImageView;
    public ProgressBar uploadingPro;
    public TextView uploadingTv;
    public LinearLayout uploading_view;
    public ImageView chattingStateIv;

    public GifRowHolder(int type) {
        super(type);
    }

    public BaseHolder initBaseHolder(View baseView, boolean receive) {
        super.initBaseHolder(baseView);

        checkBox = (CheckBox) baseView.findViewById(R.id.chatting_checkbox);
        gifImageView= (ImageView) baseView.findViewById(R.id.img_gif);
        uploading_view= (LinearLayout) baseView.findViewById(R.id.uploading_view);
        uploadingPro= (ProgressBar) baseView.findViewById(R.id.uploading_pb);
        uploadingTv= (TextView) baseView.findViewById(R.id.uploading_tv);
        if(receive) {
            type = 12;
        } else {
            chattingStateIv= (ImageView) baseView.findViewById(R.id.chatting_state_iv);
            type = 13;
        }

        return this;
    }

}
