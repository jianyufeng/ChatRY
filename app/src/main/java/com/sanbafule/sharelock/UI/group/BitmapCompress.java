package com.sanbafule.sharelock.UI.group;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.sanbafule.sharelock.util.LogUtil;

import junit.framework.Assert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;




/**
 * 作者:Created by 简玉锋 on 2016/12/6 14:11
 * 邮箱: jianyufeng@38.hn
 */

public class BitmapCompress {
    private static final String TAG = "ImageC";
    private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;


    /**
     * 得到指定路径图片的options
     *
     * @param srcPath
     * @return Options {@link android.graphics.BitmapFactory.Options}
     */
    public final static BitmapFactory.Options getBitmapOptions(String srcPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcPath, options);
        return options;
    }

    /**
     * 压缩发送到服务器的图片
     *
     * @param origPath
     *            原始图片路径
     * @param widthLimit
     *            图片宽度限制
     * @param heightLimit
     *            图片高度限制
     * @param format
     *            图片格式
     * @param quality
     *            图片压缩率
     * @param authorityDir
     *            图片目录
     * @param outPath
     *            图片详细目录
     * @return
     */
    public static boolean createThumbnailFromOrig(String origPath,
                                                  int widthLimit, int heightLimit, Bitmap.CompressFormat format,
                                                  int quality, String authorityDir, String outPath) {
        Bitmap bitmapThumbNail = extractThumbNail(origPath, widthLimit,
                heightLimit, false);
        if (bitmapThumbNail == null) {
            return false;
        }

        try {
            saveImageFile(bitmapThumbNail, quality, format, authorityDir,
                    outPath);
            return true;
        } catch (IOException e) {
            LogUtil.e(TAG, "create thumbnail from orig failed: " + outPath);
        }
        return false;
    }

    public static void saveImageFile(Bitmap bitmap, int quality,
                                     Bitmap.CompressFormat format, String authorityDir, String outPath)
            throws IOException {
        if (!TextUtils.isEmpty(authorityDir) && !TextUtils.isEmpty(outPath)) {
            LogUtil.d(TAG, "saving to " + authorityDir + outPath);
            File file = new File(authorityDir);
            if (!file.exists()) {
                file.mkdirs();
            }
            File outfile = new File(file, outPath);
            outfile.createNewFile();

            try {
                FileOutputStream outputStream = new FileOutputStream(outfile);
                bitmap.compress(format, quality, outputStream);
                outputStream.flush();
            } catch (Exception e) {
                LogUtil.e(TAG, "saveImageFile fil=" + e.getMessage());
            }
        }
    }

    public static Bitmap getSuitableBitmap(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            LogUtil.e(TAG, "filepath is null or nil");
            return null;
        }

        if (!new File(filePath).exists()) {
            LogUtil.e(TAG,
                    "getSuitableBmp fail, file does not exist, filePath = "
                            + filePath);
            return null;
        }
        try {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap decodeFile = BitmapFactory.decodeFile(filePath, options);
            if (decodeFile != null) {
                decodeFile.recycle();
            }

            if ((options.outWidth <= 0) || (options.outHeight <= 0)) {
                LogUtil.e(TAG, "get bitmap fail, file is not a image file = "
                        + filePath);
                return null;
            }

            int maxWidth = 960;
            int width = 0;
            int height = 0;
            if ((options.outWidth <= options.outHeight * 2.0D)
                    && options.outWidth > 480) {
                height = maxWidth;
                width = options.outWidth;
            }
            if ((options.outHeight <= options.outWidth * 2.0D)
                    || options.outHeight <= 480) {
                width = maxWidth;
                height = options.outHeight;
            }

            Bitmap bitmap = extractThumbNail(filePath, width, height, false);
            if (bitmap == null) {
                LogUtil.e(TAG,
                        "getSuitableBmp fail, temBmp is null, filePath = "
                                + filePath);
                return null;
            }
            int degree = readPictureDegree(filePath);
            if (degree != 0) {
                bitmap = degreeBitmap(bitmap, degree);
            }
            return bitmap;
        } catch (Exception e) {
            LogUtil.e(TAG, "decode bitmap err: " + e.getMessage());
            return null;
        }
    }
    /**
     *
     * @param src
     * @param degree
     * @return
     */
    public static Bitmap degreeBitmap(Bitmap src, float degree) {
        if (degree == 0.0F) {
            return src;
        }
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.setRotate(degree, src.getWidth() / 2, src.getHeight() / 2);
        Bitmap resultBitmap = Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
        boolean filter = true;
        if (resultBitmap == null) {
            LogUtil.e(TAG, "resultBmp is null: ");
            filter = true;
        } else {
            filter = false;
        }

        if (resultBitmap != src) {
            src.recycle();
        }
        LogUtil.d(TAG, "filter: " + filter + "  degree:" + degree);
        return resultBitmap;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path
     *            图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }



    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static Bitmap extractThumbNail(final String path, final int width,
                                          final int height, final boolean crop) {
        Assert.assertTrue(path != null && !path.equals("") && height > 0
                && width > 0);

        BitmapFactory.Options options = new BitmapFactory.Options();

        try {
            options.inJustDecodeBounds = true;
            Bitmap tmp = BitmapFactory.decodeFile(path, options);
            if (tmp != null) {
                tmp.recycle();
                tmp = null;
            }

            LogUtil.d(TAG, "extractThumbNail: round=" + width + "x" + height
                    + ", crop=" + crop);
            final double beY = options.outHeight * 1.0 / height;
            final double beX = options.outWidth * 1.0 / width;
            LogUtil.d(TAG, "extractThumbNail: extract beX = " + beX
                    + ", beY = " + beY);
            options.inSampleSize = (int) (crop ? (beY > beX ? beX : beY)
                    : (beY < beX ? beX : beY));
            if (options.inSampleSize <= 1) {
                options.inSampleSize = 1;
            }

            // NOTE: out of memory error
            while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
                options.inSampleSize++;
            }

            int newHeight = height;
            int newWidth = width;
            if (crop) {
                if (beY > beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            } else {
                if (beY < beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            }

            options.inJustDecodeBounds = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                options.inMutable = true;
            }
            LogUtil.i(TAG, "bitmap required size=" + newWidth + "x" + newHeight
                    + ", orig=" + options.outWidth + "x" + options.outHeight
                    + ", sample=" + options.inSampleSize);
            Bitmap bm = BitmapFactory.decodeFile(path, options);
            setInNativeAlloc(options);
            if (bm == null) {
                Log.e(TAG, "bitmap decode failed");
                return null;
            }

            LogUtil.i(
                    TAG,
                    "bitmap decoded size=" + bm.getWidth() + "x"
                            + bm.getHeight());
            final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth,
                    newHeight, true);
            if (scale != null) {
                bm.recycle();
                bm = scale;
            }

            if (crop) {
                final Bitmap cropped = Bitmap.createBitmap(bm,
                        (bm.getWidth() - width) >> 1,
                        (bm.getHeight() - height) >> 1, width, height);
                if (cropped == null) {
                    return bm;
                }

                bm.recycle();
                bm = cropped;
                LogUtil.i(
                        TAG,
                        "bitmap croped size=" + bm.getWidth() + "x"
                                + bm.getHeight());
            }
            return bm;

        } catch (final OutOfMemoryError e) {
            LogUtil.e(TAG, "decode bitmap failed: " + e.getMessage());
            options = null;
        }

        return null;
    }
    public static void setInNativeAlloc(BitmapFactory.Options options) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
                && !inNativeAllocAccessError) {
            try {
                BitmapFactory.Options.class.getField("inNativeAlloc")
                        .setBoolean(options, true);
                return;
            } catch (Exception e) {
                inNativeAllocAccessError = true;
            }
        }
    }
    public static boolean inNativeAllocAccessError = false;
}
