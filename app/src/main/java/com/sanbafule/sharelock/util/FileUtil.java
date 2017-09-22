package com.sanbafule.sharelock.util;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.sanbafule.sharelock.SApplication;
import com.sanbafule.sharelock.comm.help.FileAccessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


/**
 * 文件操作类
 */
public class FileUtil {
    private FileUtil() {
    }

    public static final String ROOT = "38fule";
    public static final String CHATTING = "chatting";
    public static final String IMAGE = "image";
    public static final String HEARD = "heard";
    public static final String VOICE = "voice";
    public static final String CONTACT = "contact";

    public static String getPhotoPathFromContentUri(Context context, Uri uri) {
        String photoPath = "";
        if (context == null || uri == null) {
            return photoPath;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if (isExternalStorageDocument(uri)) {
                String[] split = docId.split(":");
                if (split.length >= 2) {
                    String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        photoPath = Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
            } else if (isDownloadsDocument(uri)) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                photoPath = getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                String[] split = docId.split(":");
                if (split.length >= 2) {
                    String type = split[0];
                    Uri contentUris = null;
                    if ("image".equals(type)) {
                        contentUris = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUris = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUris = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    String selection = MediaStore.Images.Media._ID + "=?";
                    String[] selectionArgs = new String[]{split[1]};
                    photoPath = getDataColumn(context, contentUris, selection, selectionArgs);
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            photoPath = uri.getPath();
        } else {
            photoPath = getDataColumn(context, uri, null, null);
        }

        return photoPath;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return null;
    }


//    /**
//     * 或取所有文件的根目录
//     *
//     * @return
//     */
//    public static String getRootFilePath() {
//        if (isExistExternalStore()) {
//            File file = new File(Environment.getExternalStorageDirectory() + "/" + ROOT);
//            return getFilepath(file);
//        }
//        return "";
//
//    }
//
//    public static String getChattingFilePath() {
//        File file = new File(getRootFilePath() + "/" + CHATTING);
//        return getFilepath(file);
//    }
//
//    public static String getContactFilePath() {
//        File file = new File(getRootFilePath() + "/" + CONTACT);
//        return getFilepath(file);
//    }

//    public static String getFilepath(File file) {
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        return file.getAbsolutePath();
//    }

//    public static String makeChattingVoicePath() {
//        return Environment.getExternalStorageDirectory() + "/" + ROOT + "/" + CHATTING + "/" + ShareLockManager.getInstance().getClentUser().getU_username() + "/" + VOICE;
//    }


//    /**
//     * 根据用户名创建用户头像的的文件夹路径
//     *
//     * @param userName
//     * @return
//     */
//    public static File makeContactHeadImgUrl(String userName) {
//        String fileName = CurrencyHelp.Lowermd5(userName);
//        File file = new File(getContactFilePath(), fileName + ".jpg");
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        return file;
//    }
//
//    //根据用户名创建聊天图片的文件路径
//
//    public static File makeChattingImageFile(String userName) {
//        String fileName = CurrencyHelp.Lowermd5(String.valueOf(new Date().getTime()));
//        File file = new File((getChattingFilePath() + "/" + userName), fileName + ".jpg");
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        return file;
//    }

    // 保存文件
    public static void saveImage(Context context, String url, String userName) {
        File srcFile = new File(url);
        if (!srcFile.exists()) {
            srcFile.mkdirs();
        }
        if (srcFile.exists() || srcFile.length() > 0) {
            File file = FileAccessor.makeChattingImageFile(userName);
            nioTransferCopy(srcFile, file);

            //插入到本地图库中
            try {
                MediaStore.Images.Media.insertImage(context.getContentResolver(),
                        file.getAbsolutePath(), file.getName(), null);
                // 最后通知图库更新
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }


    public static void nioTransferCopy(File source, File target) {
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);
            in = inStream.getChannel();
            out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 检查文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean checkFile(String filePath) {
        if (TextUtils.isEmpty(filePath) || !(new File(filePath).exists())) {
            return false;
        }
        return true;
    }

    /**
     * 保存drawable 文件到本地
     *
     * @param resId    保存的资源id
     * @param dirPath  保存的文件路径
     * @param fileName 保存的文件名
     */
    public static String saveResToLocal(int resId, String dirPath, String fileName) {
        String path = null;
        if (resId < 0 || TextUtils.isEmpty(dirPath) || TextUtils.isEmpty(fileName)) {
            return path;
        }
        Bitmap bitmap = BitmapFactory.decodeResource(SApplication.getInstance().getResources(), resId);
        FileOutputStream fos = null;

        File file = new File(dirPath, fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            path = file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                }
            }
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
        return path;
    }

}
