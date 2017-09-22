package com.sanbafule.sharelock.business;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sanbafule.sharelock.R;

/**
 * Administrator
 * 作者:Created by ShareLock on 2017/1/6 10:20
 * cd : 三八妇乐
 * 描述：
 */
public class ImageBiz {

    public static void showImage(Context context, ImageView imageView, String url) {
        Glide.
                with(context).
                load(url).
                error(R.drawable.ic_launcher).
                fitCenter().
                crossFade(300).
                placeholder(R.drawable.ic_launcher).
                into(imageView);
    }

    public static void showImage(Context context, ImageView imageView, Uri uri) {
        Glide.
                with(context).
                load(uri).
                error(R.drawable.ic_launcher).
                fitCenter().
                crossFade(300).
                placeholder(R.drawable.ic_launcher).
                into(imageView);
    }
    public static void showImage(Context context, ImageView imageView, String url,int resId){
        Glide.
                with(context).
                load(url).
                error(resId).
                fitCenter().
                crossFade(300).
                placeholder(resId).
                into(imageView);
    }

}
