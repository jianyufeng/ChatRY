/*
 *  Copyright (c) 2015 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.sanbafule.sharelock.comm.help;


import android.os.Environment;
import android.text.TextUtils;

import com.sanbafule.sharelock.global.MyString;
import com.sanbafule.sharelock.util.LogUtil;
import com.sanbafule.sharelock.util.ToastUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;


/**
 * 文件操作工具类
 * Created by Jorstin on 2015/3/17.
 */
public class FileAccessor {


    public static final String TAG = FileAccessor.class.getName();
    public static String A = File.separator;

    public static File userDir;
    public static File rootDir;
    public static File chattingDir;
    public static File groupDir;
    public static File contactDir;
    public static File myDir;
    public static File chatting_userDir;
    public static File contact_userDir;
    public static File group_userDir;

    // App的根路径
    public static final String APPS_ROOT_DIR = getExternalStorePath() + "/38fule";
    // 关于用户的根路径
    public static String USER_PATH = null;
    // 关于聊天的根路径
    public static final String CHATTING = "/chatting";
    // 聊天图片下载的路径
    public static final String IMAGE = "/image";
    // 头像的路径
    public static final String HEAD = "/head";
    // 发送的语音的路径
    public static final String VOICE = "/voice";
    // 联系人的路径
    public static final String CONTACT = "/contact";
    // 头像的缩略图路径
    public static final String HEAD_THUMB = "/thumb";
    // 头像的原图路径
    public static final String HEAD_SOURCE = "/source";
    // 关于用户自己信息的路径
    public static final String USER = "/user";
    // 群组的文件夹路径
    public static final String GROUP = "/group";
    //群组文件夹下的群组图像路径
    public static final String GROUP_THUMB = "/thumb";
    // 聊天图片下载的路径
    public static final String VIDEO = "/video";

    public static final String IMESSAGE_VOICE = getExternalStorePath() + "/39fule/voice";

    /**
     * 初始化应用文件夹目录
     */
    public static void initFileAccess(String userName) {
        if (!MyString.hasData(userName) || !isExistExternalStore()) {
            return;
        }
        USER_PATH = userName;
        rootDir = new File(APPS_ROOT_DIR);
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }
        LogUtil.i(APPS_ROOT_DIR + A + USER_PATH);
        userDir = new File(APPS_ROOT_DIR + A + USER_PATH);
        if (!userDir.exists()) {
            userDir.mkdirs();
        }

        // 创建聊天文件夹
        makeChattingDirectory();
//        // 创建联系人文件夹
        makeContactDirectory();
//        // 创建群组文件夹
        makeGroupDirectory();
//        // 创建user文件夹
        makeUserDirectory();
    }


    private static void a() {
        if (USER_PATH == null) {
            initFileAccess(ShareLockManager.getInstance().getUserName());
        }
    }

    /**
     * 创建聊天文件夹
     */
    private static void makeChattingDirectory() {
        a();
        chattingDir = new File(APPS_ROOT_DIR + A + USER_PATH + CHATTING);
        if (!chattingDir.exists()) {
            chattingDir.mkdirs();
        }

    }

    /**
     * 创建user文件夹
     */
    private static void makeUserDirectory() {
        a();
        myDir = new File(APPS_ROOT_DIR + A + USER_PATH + A + USER);
        if (!myDir.exists()) {
            myDir.mkdirs();
        }

    }

    /**
     * 创建群组文件夹
     */
    private static void makeGroupDirectory() {
        a();
        groupDir = new File(APPS_ROOT_DIR + A + USER_PATH + GROUP);
        if (!groupDir.exists()) {
            groupDir.mkdirs();
        }
    }

    /**
     * 创建联系人的文件夹
     */
    private static void makeContactDirectory() {
        a();
        contactDir = new File(APPS_ROOT_DIR + A + USER_PATH + CONTACT);
        if (!contactDir.exists()) {
            contactDir.mkdirs();
        }
    }

    /**
     * 创建聊天文件夹中的user文件夹
     *
     * @param userName 用户名
     */
    private static void makeChatingUserDirectory(String userName) {

        if (!MyString.hasData(userName) || !isExistExternalStore()) {
            return;
        }

        makeChattingDirectory();
        chatting_userDir = new File(APPS_ROOT_DIR + A + USER_PATH + CHATTING + A + userName);
        if (!chatting_userDir.exists()) {
            chatting_userDir.mkdirs();
        }

    }

    /**
     * 创建联系人文家中的user文件夹
     *
     * @param userName 用户名
     */
    private static void makeContactUserDirectory(String userName) {

        if (!MyString.hasData(userName) || !isExistExternalStore()) {
            return;
        }
        makeContactDirectory();
        contact_userDir = new File(APPS_ROOT_DIR + A + USER_PATH + CONTACT + A + userName);
        if (!contact_userDir.exists()) {
            contact_userDir.mkdirs();
        }

    }

    /**
     * 创建联系人头像文件夹
     *
     * @param userName
     */
    private static void makeContactUserHeadDirectory(String userName) {
        makeContactUserDirectory(userName);
        File file = new File(APPS_ROOT_DIR + A + USER_PATH + CONTACT + A + userName + HEAD);
        if (!file.exists()) {
            file.mkdirs();
        }

    }

    /**
     * 创建联系人头像缩略图文件夹
     */
    public static String getContactHeadThumbPath(String userName) {
        if (!isExistExternalStore()) {
            return null;
        }
        makeContactUserHeadDirectory(userName);
        File file = new File(APPS_ROOT_DIR + A + USER_PATH + CONTACT + A + userName + HEAD + HEAD_THUMB);

        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();

    }

    /**
     * 创建联系人头像大图文件夹
     */
    public static String getContactHeadSourcePath(String userName) {
        if (!isExistExternalStore()) {
            return null;
        }
        makeContactUserHeadDirectory(userName);
        File file = new File(APPS_ROOT_DIR + A + USER_PATH + CONTACT + A + userName + HEAD + HEAD_SOURCE);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();

    }

    /**
     * 闯将群组文件夹中user文件夹
     *
     * @param groupName 群组名
     */
    private static void makeGroupUserDirectory(String groupName) {
        if (!MyString.hasData(groupName) || !isExistExternalStore()) {
            return;
        }
        makeGroupDirectory();
        group_userDir = new File(APPS_ROOT_DIR + A + USER_PATH + GROUP + A + groupName);
        if (!group_userDir.exists()) {
            group_userDir.mkdirs();
        }

    }

    public static File getGroupThumbPath() {
        if (!isExistExternalStore()) {
            ToastUtil.showMessage("存储卡已拔出");
            return null;
        }
        File directory = new File(APPS_ROOT_DIR + A + ShareLockManager.getInstance().getUserName() + GROUP + GROUP_THUMB);
        if (!directory.exists() && !directory.mkdirs()) {
            ToastUtil.showMessage("Path to file could not be created");
            return null;
        }
        return directory;
    }

    /**
     * 获取聊天语音的存放路劲
     *
     * @return
     */
    public static String getChattingVoicePath(String userName) {
        if (!isExistExternalStore()) {
            return null;
        }
        makeChatingUserDirectory(userName);
        if (!chatting_userDir.exists()) {
            chatting_userDir.mkdirs();
        }

        File file = new File(chatting_userDir.getPath() + VOICE);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /**
     * 获取聊天视频的存放路劲
     *
     * @return
     */
    public static String getChattingVideoPath(String userName) {
        if (!isExistExternalStore()) {
            return null;
        }
        makeChatingUserDirectory(userName);
        if (!chatting_userDir.exists()) {
            chatting_userDir.mkdirs();
        }

        File file = new File(chatting_userDir.getPath() + VIDEO);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /**
     * 获取聊天语音的存放路劲
     *
     * @return
     */
    public static String getChattingImagePath(String userName) {
        if (!isExistExternalStore()) {
            return null;
        }
        makeChatingUserDirectory(userName);
        if (!chatting_userDir.exists()) {
            chatting_userDir.mkdirs();
        }

        File file = new File(chatting_userDir.getPath() + IMAGE);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }


    //根据用户名创建聊天图片的文件路径

    public static File makeChattingImageFile(String userName) {
        String fileName = CurrencyHelp.Lowermd5(String.valueOf(new Date().getTime()));
        File file = new File(getChattingImagePath(userName), fileName + ".jpg");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static String readContentByFile(String path) {
        BufferedReader reader = null;
        String line = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new FileReader(file));
                while ((line = reader.readLine()) != null) {
                    sb.append(line.trim());
                }
                return sb.toString().trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    /**
     * 获取文件名
     *
     * @param pathName
     * @return
     */
    public static String getFileName(String pathName) {

        int start = pathName.lastIndexOf("/");
        if (start != -1) {
            return pathName.substring(start + 1, pathName.length());
        }
        return pathName;

    }

    /**
     * 外置存储卡的路径
     *
     * @return
     */
    public static String getExternalStorePath() {
        if (isExistExternalStore()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }

    /**
     * 是否有外存卡
     *
     * @return
     */
    public static boolean isExistExternalStore() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * @param filePaths
     */
    public static void delFiles(ArrayList<String> filePaths) {
        for (String url : filePaths) {
            if (!TextUtils.isEmpty(url))
                delFile(url);
        }
    }


    public static boolean delFile(String filePath) {
        File file = new File(filePath);
        if (file == null || !file.exists()) {
            return true;
        }

        return file.delete();
    }


    /**
     * @param root
     * @param srcName
     * @param destName
     */
    public static void renameTo(String root, String srcName, String destName) {
        if (TextUtils.isEmpty(root) || TextUtils.isEmpty(srcName) || TextUtils.isEmpty(destName)) {
            return;
        }

        File srcFile = new File(root + srcName);
        File newPath = new File(root + destName);

        if (srcFile.exists()) {
            srcFile.renameTo(newPath);
        }
    }

    public static File getTackPicFilePath() {
        File localFile = new File(getExternalStorePath() + "/fule/.tempchat", "temp.jpg");
        if ((!localFile.getParentFile().exists())
                && (!localFile.getParentFile().mkdirs())) {
            LogUtil.e("hhe", "SD卡不存在");
            localFile = null;
        }
        return localFile;
    }


}
