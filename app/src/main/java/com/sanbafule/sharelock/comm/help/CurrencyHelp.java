package com.sanbafule.sharelock.comm.help;

import android.app.Activity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import me.iwf.photopicker.PhotoPicker;

/**
 * Created by Administrator on 2016/10/19.
 */
public class CurrencyHelp {

    /**
     * 打开自定义的相册带相机功能
     *
     * @param activity
     * @param singlePhoto
     */
    public static void openImageActivity(Activity activity, boolean singlePhoto) {
        PhotoPicker.builder()
                .setPhotoCount(9)
                .setShowCamera(singlePhoto)
                .setShowGif(singlePhoto)
                .setPreviewEnabled(singlePhoto)
                .start(activity);


    }

    private static MessageDigest md = null;
    public static String Lowermd5(final String c) {
        if (md == null) {
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        if (md != null) {
            md.update(c.getBytes());
            return Lowerbyte2hex(md.digest());
        }
        return "";
    }

    public static String Lowerbyte2hex(byte b[]) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xff);
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
            if (n < b.length - 1)
                hs = (new StringBuffer(String.valueOf(hs))).toString();
        }

        return hs.toLowerCase();
    }

}
