package com.sanbafule.sharelock.business;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.comm.help.FileAccessor;
import com.sanbafule.sharelock.db.ContactInfoSqlManager;
import com.sanbafule.sharelock.global.MyString;
import com.sanbafule.sharelock.util.BitmapUtil;
import com.sanbafule.sharelock.util.FileUtil;
import com.sanbafule.sharelock.util.ToastUtil;

import java.io.File;
import java.io.IOException;


/**
 * Created by Administrator on 2016/5/10.
 */
public class DownImgBiz {

    private static final String TAG = "DownImgBiz";

    public DownImgBiz() {
    }


    // 下载联系人头像并显示
    public void downLoadHeardImg(final Context context, ImageView view, String url, final String userName, int errorId) {

        if (!MyString.hasData(url)) {
            view.setImageResource(errorId);
            return;
        }
        // 下载联系人
        else if (url.startsWith("http")) {
            Glide.with(context).load(url).asBitmap().error(R.drawable.icon_touxiang_persion_gray).listener(new RequestListener<String, Bitmap>() {
                @Override
                public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {

                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    // 获取文件的地址
                    final File file =  new File(FileAccessor.getContactHeadThumbPath(userName));
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    // 将Bitmap 转化成file对象     // 保存图像
                    if (BitmapUtil.saveBitmapFile(context,resource, file)) {
                        // 更新数据库中contactInfo的图片地址
                        ContactInfoSqlManager.updateHeadImg(userName, file.getAbsolutePath());
                    }

                    return false;
                }
            }).into(view);
        } else {
            Glide.
                    with(context).
                    load(url).
                    error(R.drawable.icon_touxiang_persion_gray).
                    fitCenter().
                    crossFade(300).
                    into(view);
        }




    }


    /**
     * @param context
     * @param url
     * @param userName // 如果是本地路径就
     *                 <p>
     *                 // 判断路劲是否是在SD卡中
     *                 <p>
     *                 // 如果是SD卡就不做处理
     *                 <p>
     *                 // 如果不是就拷贝到SD卡中
     */
    public void downLoadImage(final Context context, String url, final String userName) {

        if (!MyString.hasData(url) || !MyString.hasData(userName)) {
            return;
        }
        // http 下载
        if (url.startsWith("http")) {
            Glide.with(context).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> arg1) {


                    // 获取文件的地址
                    final File file = FileAccessor.makeChattingImageFile(userName);
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    // 将Bitmap 转化成file对象     // 保存图像
                    if (BitmapUtil.saveBitmapFile(context,bitmap, file)) {
                        // 更新数据库中contactInfo的图片地址
                        ToastUtil.showMessage("图片保存成功");
                    }
                }
            });

        } else {
            //文件拷贝

            FileUtil.saveImage(context,url,userName);

        }


    }



}
