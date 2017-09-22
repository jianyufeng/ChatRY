package com.sanbafule.sharelock.util;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.sanbafule.sharelock.SApplication;
import com.sanbafule.sharelock.db.ContactInfoSqlManager;
import com.sanbafule.sharelock.db.GroupInfoSqlManager;
import com.sanbafule.sharelock.db.GroupMemberSqlManager;
import com.sanbafule.sharelock.db.GroupSqlManager;
import com.sanbafule.sharelock.modle.ContactInfo;
import com.sanbafule.sharelock.modle.Group;
import com.sanbafule.sharelock.modle.GroupInfo;
import com.sanbafule.sharelock.modle.GroupMember;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by 简玉锋 on 2016/9/1.
 * 创建了固定线程池  2 个线程
 */
public class ThreadPool {
    private static final String TAG = "ThreadPool";
    public ExecutorService executorService = Executors.newFixedThreadPool(2);
    private static ThreadPool mInstance;
    private Handler mDelivery;

    //获取实例对象
    public static ThreadPool getInstnce() {
        if (mInstance == null) synchronized (new Object()) {
            return new ThreadPool();
        }
        else {
            return mInstance;
        }

    }

    private ThreadPool() {
        mDelivery = new Handler(Looper.getMainLooper());
    }

    //获取到的群组列表 存入数据库
    public void saveGroupList(final List<Group> groups, final List<GroupInfo> groupInfos) {
        ThreadPool.getInstnce().executorService.submit(new Runnable() {
            @Override
            public void run() {
                //解析完 判断解析结果   进行数据库处理
                if (groups == null || groups.isEmpty()) {  //如果获取到的是空 ，则清除原有的数据 与服务器保持一致
                    GroupSqlManager.delALLGroup();  //删除数据
                } else {  //更新群列表数据库
                    /**
                     *  第一步  获取当前表中加入的群组列表
                     *
                     *  第二部  比对最新的群组  注意返回的groupID 需要手动添加 GROUP前缀
                     *
                     *  第三部  是更新   不是删除
                     */
                    List<String> allGroupIdByJoin = GroupSqlManager.getAllGroupIdBy();
                    ArrayList<String> ids = new ArrayList<>();
                    for (Group group : groups) {
                        ids.add(group.getG_id());
                    }
                    // 查找不是我的群组
                    if (!allGroupIdByJoin.isEmpty()) {
                        for (String groupId : allGroupIdByJoin) {
                            if (ids.contains(groupId)) {
                                continue;
                            }
                            // 不是我的群组  删除
                            GroupSqlManager.deleteGroup(groupId);
                        }
                    }
                    //插入数据
                    GroupSqlManager.insertGroupList(groups);
                }

                if (groupInfos == null && groupInfos.isEmpty()) {

                } else {
                    GroupInfoSqlManager.insertGroupListInfo(groupInfos);
                }
            }
        });


    }

    //获取到的群组列表 存入数据库
    public void saveGroupMembersInfo(final GroupInfo groupInfo, final List<GroupMember> groupMembers) {
        ThreadPool.getInstnce().executorService.submit(new Runnable() {
            @Override
            public void run() {

                //插入群组详情
                if (groupInfo == null || TextUtils.isEmpty(groupInfo.getG_id())) {
                    return;
                } else {
                    GroupInfoSqlManager.insertGroupInfo(groupInfo);
                }
                if (groupMembers == null || groupMembers.isEmpty()) { //此处有问题 简玉锋2016-12-14

                } else {
                    //获取当前数据库中的群组成员账号
                    ArrayList<String> groupMemberAccounts = GroupMemberSqlManager.getGroupMemberAccounts(groupInfo.getG_id());
                    ArrayList<String> ids1 = new ArrayList<>();
                    for (int j = 0; j < groupMembers.size(); j++) {
                        ids1.add(groupMembers.get(j).getU_username());
                    }
                    // 查找不是群组成员
                    if (groupMemberAccounts != null && !groupMemberAccounts.isEmpty()) {
                        for (String memberAccount : groupMemberAccounts) {
                            if (ids1.contains(memberAccount)) {
                                continue;
                            }
                            // 不是群组成员、从数据库删除
                            GroupMemberSqlManager.delMember(groupInfo.getG_id(), memberAccount);
                        }
                    }
                    //插入群组成员到群组成员表
                    GroupMemberSqlManager.insertGroupMembers(groupMembers);

                }
            }
        });


    }

    //获取到的群组列表 存入数据库
    public void saveGroupInfo(final GroupInfo groupInfo) {
        ThreadPool.getInstnce().executorService.submit(new Runnable() {
            @Override
            public void run() {

                //插入群组详情
                if (groupInfo == null || TextUtils.isEmpty(groupInfo.getG_id())) {

                } else {
                    GroupInfoSqlManager.insertGroupInfo(groupInfo);
                }
            }
        });
    }

    //获取到的群组列表 存入数据库
    public void saveGroupInfos(final ArrayList<GroupInfo> groupInfos) {
        ThreadPool.getInstnce().executorService.submit(new Runnable() {
            @Override
            public void run() {

                //插入群组详情
                if (groupInfos == null || groupInfos.isEmpty()) {

                } else {
                    for (GroupInfo groupInfo : groupInfos) {
                        GroupInfoSqlManager.insertGroupInfo(groupInfo);
                    }

                }
            }
        });
    }

    //获取到的成员详情存入数据库
    public void saveGroupMembersInfo(final ContactInfo contactInfo) {
        ThreadPool.getInstnce().executorService.submit(new Runnable() {
            @Override
            public void run() {

                //插入详情
                ContactInfoSqlManager.insertContactInfo(contactInfo);

            }
        });


    }


    /**
     * 实现了如何根据时间间隔判断是否需要刷新服务器数据，true表示不需要，false表示需要
     *
     * @param cacheFileName 文件名
     * @param CACHE_TIME    过期时间 毫秒
     * @return
     */
    public boolean isCacheDataFailure(String cacheFileName, int CACHE_TIME) {
        boolean failure = false;
        File data = SApplication.getInstance().getFileStreamPath(cacheFileName);
        if (data.exists()
                && (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME) {
            failure = true;
        } else if (!data.exists()) {
            failure = true;
        }
        return failure;
    }

    public void saveCache(final Handler handler, final Serializable parcelable, final String cacheFileName, final int saveCacheFlag) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean b = saveObject(parcelable, cacheFileName);
                    Message message = Message.obtain();
                    message.what = saveCacheFlag;
                    message.obj = b;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    Log.e(TAG, "run: " + "file save fail");
                }
            }
        });
    }

    public void readObject(final Handler handler, final String cacheFileName, final int getCacheFlag) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Serializable parcelable = readObject(cacheFileName);
                    Message message = Message.obtain();
                    message.what = getCacheFlag;
                    message.obj = parcelable;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    Log.e(TAG, "run: " + "file get fail");
                }
            }
        });
    }


    /************************************
     * 下面是工具
     **************************************************/

    private boolean saveObject(Serializable parcelable, String cacheFileName) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = SApplication.getInstance().openFileOutput(cacheFileName, SApplication.getInstance().MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(parcelable);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
            }
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 读取对象
     *
     * @param cacheFileName
     * @return
     * @throws
     */
    private Serializable readObject(String cacheFileName) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = SApplication.getInstance().openFileInput(cacheFileName);
            ois = new ObjectInputStream(fis);
            return (Serializable) ois.readObject();
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ois.close();
            } catch (Exception e) {
            }
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

}
