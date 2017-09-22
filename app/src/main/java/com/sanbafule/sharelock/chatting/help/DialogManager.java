package com.sanbafule.sharelock.chatting.help;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanbafule.sharelock.R;

/**
 * Created by Administrator on 2016/10/25.
 */
public class DialogManager {
    private AlertDialog.Builder builder;
    private ImageView mIcon;
    private ImageView mVoice;
    private TextView mLable;

    private Context mContext;

    private AlertDialog dialog;//用于取消AlertDialog.Builder

    /**
     * 构造方法 传入上下文
     */
    public DialogManager(Context context) {
        this.mContext = context;
    }

    // 显示录音的对话框
    public void showRecordingDialog() {

        builder = new AlertDialog.Builder(mContext, R.style.ThemeAudioDialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.record_dialog,null);

        mIcon = (ImageView) view.findViewById(R.id.id_recorder_dialog_icon);
        mVoice = (ImageView) view.findViewById(R.id.id_recorder_dialog_voice);
        mLable = (TextView) view.findViewById(R.id.id_recorder_dialog_label);

        builder.setView(view);
        builder.create();
        dialog = builder.show();
    }

    public void recording(){
        if(dialog != null && dialog.isShowing()){ //显示状态
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.recorder);
            mLable.setText(R.string.str_recorder_recording);
        }
    }

    // 显示想取消的对话框
    public void wantToCancel() {
        if(dialog != null && dialog.isShowing()){ //显示状态
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLable.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.cancel);
            mLable.setText(R.string.str_recorder_want_cancel);
        }
    }

    // 显示时间过短的对话框
    public void tooShort() {
        if(dialog != null && dialog.isShowing()){ //显示状态
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLable.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.voice_to_short);
            mLable.setText(R.string.str_recorder_too_short);
        }
    }

    // 显示取消的对话框
    public void dimissDialog() {
        if(dialog != null && dialog.isShowing()){ //显示状态
            dialog.dismiss();
            dialog = null;
        }
    }

    // 显示更新音量级别的对话框
    public void updateVoiceLevel(int level) {
        if(dialog != null && dialog.isShowing()){ //显示状态
            //设置图片的id
            int resId = mContext.getResources().getIdentifier("v"+level, "drawable", mContext.getPackageName());
            mVoice.setImageResource(resId);
        }
    }
}


