package com.sanbafule.sharelock.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.sanbafule.sharelock.chatting.db.OnMessageChange;
import com.sanbafule.sharelock.modle.Group;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/3.
 * <p>
 * 群组数据库
 */
public class GroupSqlManager extends AbstractSQLManager {

    Object mLock = new Object(); //锁
    private static GroupSqlManager sInstance; //实例

    private static GroupSqlManager getInstance() { //获取实例
        if (sInstance == null) {
            sInstance = new GroupSqlManager();
        }
        return sInstance;
    }

    private GroupSqlManager() {  //构造方法

    }

    /**
     * 批量更新群组
     * <p>
     * 第一步  获取当前表中加入的群组列表
     * <p>
     * 第二部  比对最新的群组  注意返回的groupID 需要手动添加 GROUP前缀
     * <p>
     * 第三部  是更新   不是删除
     *
     * @param groups
     * @throws android.database.SQLException
     */
    public static ArrayList<Long> insertGroupList(List<Group> groups) {

        ArrayList<Long> rows = new ArrayList<>();
        if (groups == null) {
            return rows;
        }
        try {
            synchronized (getInstance().mLock) {
                // Set the start transaction
                getInstance().sqliteDB().beginTransaction();
                // Batch processing operation
                for (Group group : groups) {
                    try {
                        //插入群组
                        long row = insertGroup(group);
                        if (row != -1) {
                            rows.add(row);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // Set transaction successful, do not set automatically
                // rolls back not submitted.
                getInstance().sqliteDB().setTransactionSuccessful();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getInstance().sqliteDB().endTransaction();
        }
        return rows;
    }

    /**
     * 清空所有群组
     *
     * @return
     */
    public static int delALLGroup() {
        try {
            return getInstance().sqliteDB().delete(DatabaseHelper.TABLES_NAME_GROUP, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * 查询所有加入的群组
     *
     * @return
     */
    public static List<String> getAllGroupIdBy() {
        ArrayList<String> groupsIds = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getInstance().sqliteDB().query(DatabaseHelper.TABLES_NAME_GROUP, new String[]{GroupColumn.GROUP_ID}, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String groupId = cursor.getString(0);
                    if (TextUtils.isEmpty(groupId)) {
                        continue;
                    }
                    groupsIds.add(groupId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return groupsIds;
    }

    /**
     * 删除群组
     *
     * @param groupId
     * @return
     */
    public static int deleteGroup(String groupId) {
        String whereClause = GroupColumn.GROUP_ID + " = ? ";
        String[] whereArgs = new String[]{groupId};
        return getInstance().sqliteDB().delete(DatabaseHelper.TABLES_NAME_GROUP, whereClause, whereArgs);
    }

    /**
     * 插入群组
     *
     * @param group
     */
    public static long insertGroup(Group group) {
        //创建  ContentValues
        if (group == null || TextUtils.isEmpty(group.getG_id())) {
            return -1L;
        }
        ContentValues values = null;
        try {
            values = new ContentValues();
            values.put(GroupColumn.GROUP_ID, group.getG_id());
            //判断是本地数据库是否有本群  有更新 没有插入
            if (isExitGroup(group.getG_id())) {
                //存在 更新
                return getInstance().sqliteDB().update(DatabaseHelper.TABLES_NAME_GROUP, values, GroupColumn.GROUP_ID + " = ? ", new String[]{group.getG_id()});
            }
            //不存在  插入
            return getInstance().sqliteDB().insert(DatabaseHelper.TABLES_NAME_GROUP, null, values);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (values != null) {
                values.clear();
            }
        }
        return -1;
    }

    /**
     * 群组是否存在
     *
     * @param groupId
     * @return
     */
    public static boolean isExitGroup(String groupId) {
        String[] columns = new String[]{GroupColumn.GROUP_ID};
        String selection = GroupColumn.GROUP_ID + " = ? ";
        String[] selectionArgs = new String[]{groupId};
        Cursor cursor = null;
        try {
            cursor = getInstance().sqliteDB().query(DatabaseHelper.TABLES_NAME_GROUP, columns, selection, selectionArgs, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }

    //获取所加入得群组
    public static ArrayList<Group> getGroups() {
        ArrayList<Group> groups = null;
        Cursor cursor = null;
        try {
            cursor = getInstance().sqliteDB().query(DatabaseHelper.TABLES_NAME_GROUP, null, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                groups = new ArrayList<>();
                while (cursor.moveToNext()) {
                    //数据转换
                    Group group = new Group();
                    group.setG_id(cursor.getString(cursor.getColumnIndex(GroupColumn.GROUP_ID)));
                    groups.add(group);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return groups;
    }


    /**
     * 注册观察者
     *
     * @param observer
     */
    public static void registerGroupObserver(OnMessageChange observer) {
        getInstance().registerObserver(observer);
    }

    /***
     * 注销观察者
     *
     * @param observer
     */
    public static void unregisterGroupObserver(OnMessageChange observer) {
        getInstance().unregisterObserver(observer);
    }

    /**
     * 分发数据库改变通知
     *
     * @param session
     */
    public static void notifyGroupChanged(String session) {
        getInstance().notifyChanged(session);
    }

    // 需要在登出是释放资源及数据库
    public static void reset() {
        getInstance().release();
    }

    /**
     * 释放资源
     */
    @Override
    protected void release() {

        super.release();
        sInstance = null;
    }
}
