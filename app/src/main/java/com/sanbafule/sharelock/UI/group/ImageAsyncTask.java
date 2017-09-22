package com.sanbafule.sharelock.UI.group;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.CurrencyHelp;
import com.sanbafule.sharelock.comm.help.FileAccessor;
import com.sanbafule.sharelock.util.FileUtil;

import java.io.File;

/**
 * 作者:Created by 简玉锋 on 2016/12/7 14:12
 * 邮箱: jianyufeng@38.hn
 */

public class ImageAsyncTask extends AsyncTask {
    private ImgCallBack callBack;

    public ImageAsyncTask(Context context, ImgCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        String filePath = (String) params[0]; //传参文件路径
        if (!TextUtils.isEmpty(filePath)) {   //检查是否传参
            String thumbName = CurrencyHelp.Lowermd5(System.currentTimeMillis() + filePath) + ".jpg";
            if (filePath.equals(SString.GROUP_PRE)) { //使用的是默认的图像

                return FileUtil.saveResToLocal(R.drawable.group_default_icon, FileAccessor.getGroupThumbPath().getAbsolutePath(), thumbName);

            }
            //是否含有文件 及 图片路径是否存在
            if (!FileUtil.checkFile(filePath) || FileAccessor.getGroupThumbPath() == null) {
                return null;
            }
            //小图
            if (!BitmapCompress.createThumbnailFromOrig(filePath, 760, 1280, Bitmap.CompressFormat.JPEG, 100, FileAccessor.getGroupThumbPath().getAbsolutePath(), thumbName)) {
                return null;  //不成功
            }
            return FileAccessor.getGroupThumbPath().getAbsolutePath() + File.separator + thumbName;    //成功
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if (o instanceof String) {
            callBack.callBackImg((String) o);
        } else {
            callBack.callBackErr();
        }
    }

    public interface ImgCallBack {
        void callBackImg(String thumbPath);

        void callBackErr();
    }
}
