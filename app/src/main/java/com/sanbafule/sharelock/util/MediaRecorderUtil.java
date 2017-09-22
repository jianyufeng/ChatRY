package com.sanbafule.sharelock.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.widget.Toast;

import java.io.IOException;

/**
 * ShareLock
 * 录音的工具类
 */
public final class MediaRecorderUtil {


    private static MediaRecorderUtil instance;
    private static MediaRecorder mRecorder;
    private static MediaPlayer mediaPlayer;
private MediaRecorderUtil(){
    super();
}
    public static MediaRecorderUtil getIntance(){
        if(instance==null){
            instance=new MediaRecorderUtil();
            mRecorder=new MediaRecorder();
            mediaPlayer=new MediaPlayer();
        }
        return instance;
    }

    /**
     * 开始录音
     * @param context
     * @param outPath
     */
    public static  void startRecorder(  Context context,String outPath ){
        try {
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(outPath);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.prepare();
        } catch (IOException e) {

            Toast.makeText(context,"加载录音器失败,请稍后再试",Toast.LENGTH_LONG).show();
        }
        mRecorder.start();
    }

    /**
     * 录音完成
     * @param context
     * @param mediaRecorder
     */
    public static void endRecoder(Context context,MediaRecorder mediaRecorder){
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    /**
     * 开始播放
     */
    public static void startplay(Context context,String filePath){


        try{
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }catch(IOException e){
      e.printStackTrace();
            Toast.makeText(context,"播放失败",Toast.LENGTH_LONG).show();
        }

}


    /**
     * 关闭播放
     */
    public static void closed(){

        mediaPlayer.release();
        mediaPlayer = null;
    }


}


