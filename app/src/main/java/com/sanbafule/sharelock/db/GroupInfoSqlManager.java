package com.sanbafule.sharelock.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.sanbafule.sharelock.chatting.db.OnMessageChange;
import com.sanbafule.sharelock.modle.GroupInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 简玉锋 on 2016/12/19
 * 群组详情数据库
 */
public class GroupInfoSqlManager extends AbstractSQLManager {

    Object mLock = new Object(); //锁
    private static GroupInfoSqlManager sInstance; //实例

    private static GroupInfoSqlManager getInstance() { //获取实例
        if (sInstance == null) {
            sInstance = new GroupInfoSqlManager();
        }
        return sInstance;
    }

    private GroupInfoSqlManager() {  //构造方法

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
     * @param groupInfos
     * @throws android.database.SQLException
     */
    public static ArrayList<Long> insertGroupListInfo(List<GroupInfo> groupInfos) {

        ArrayList<Long> rows = new ArrayList<>();
        if (groupInfos == null) {
            return rows;
        }
        try {
            synchronized (getInstance().mLock) {
                // Set the start transaction
                getInstance().sqliteDB().beginTransaction();
                // Batch processing operation
                for (GroupInfo groupInfo : groupInfos) {
                    try {
                        //插入群组详情
                        long row = insertGroupInfo(groupInfo);
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
     * 清空所有群组详情
     *
     * @return
     */
    public static int delALLGroupInfo() {
        try {
            return getInstance().sqliteDB().delete(DatabaseHelper.TABLES_NAME_GROUP_INFO, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * 删除指定群组详情
     *
     * @param groupId
     * @return
     */
    public static int deleteGroupInfo(String groupId) {
        String whereClause = GroupInfoColumn.GROUP_ID + " = ? ";
        String[] whereArgs = new String[]{groupId};
        return getInstance().sqliteDB().delete(DatabaseHelper.TABLES_NAME_GROUP_INFO, whereClause, whereArgs);
    }

    /**
     * 插入群组
     *
     * @param groupInfo
     */
    public static long insertGroupInfo(GroupInfo groupInfo) {
        //创建  ContentValues
        if (groupInfo == null || TextUtils.isEmpty(groupInfo.getG_id())) {
            return -1L;
        }
        ContentValues values = null;
        try {
            values = new ContentValues();
            values.put(GroupInfoColumn.GROUP_ID, groupInfo.getG_id()); //群组id
            if (!TextUtils.isEmpty(groupInfo.getG_name())) {
                values.put(GroupInfoColumn.GROUP_NAME, groupInfo.getG_name()); //群组名称
            }
            if (!TextUtils.isEmpty(groupInfo.getG_header())) {
                values.put(GroupInfoColumn.GROUP_THUMB, groupInfo.getG_header()); //群组图像
            }
            if (!TextUtils.isEmpty(groupInfo.getG_create_time())) {
                values.put(GroupInfoColumn.GROUP_CREATE_TIME, groupInfo.getG_create_time()); //群组创建时间
            }
            if (!TextUtils.isEmpty(groupInfo.getG_admin_username())) {
                values.put(GroupInfoColumn.GROUP_ADMIN, groupInfo.getG_admin_username());  //群主
            }
            if (groupInfo.getG_permission() != -1) {
                values.put(GroupInfoColumn.GROUP_PERMISSION, groupInfo.getG_permission());  //群组权限
            }
            if (groupInfo.getG_type() != -1) {
                values.put(GroupInfoColumn.GROUP_TYPE, groupInfo.getG_type());  //群组类型
            }
            if (!TextUtils.isEmpty(groupInfo.getG_declared())) {
                values.put(GroupInfoColumn.GROUP_DECLARED, groupInfo.getG_declared());  //群公告
            }
            if (!TextUtils.isEmpty(groupInfo.getG_qrcode())) {
                values.put(GroupInfoColumn.GROUP_QRCODE, groupInfo.getG_qrcode());  //群公告
            }

            //判断是本地数据库是否有本群  有更新 没有插入
            if (isExitGroupInfo(groupInfo.getG_id())) {
                //存在 更新
                return getInstance().sqliteDB().update(DatabaseHelper.TABLES_NAME_GROUP_INFO, values, GroupInfoColumn.GROUP_ID + " = ? ", new String[]{groupInfo.getG_id()});
            }
            //不存在  插入
            return getInstance().sqliteDB().insert(DatabaseHelper.TABLES_NAME_GROUP_INFO, null, values);

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
    public static boolean isExitGroupInfo(String groupId) {
        String[] columns = new String[]{GroupInfoColumn.GROUP_ID};
        String selection = GroupInfoColumn.GROUP_ID + " = ? ";
        String[] selectionArgs = new String[]{groupId};
        Cursor cursor = null;
        try {
            cursor = getInstance().sqliteDB().query(DatabaseHelper.TABLES_NAME_GROUP_INFO, columns, selection, selectionArgs, null, null, null);
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

    /**
     * 获取指定群组详情
     *
     * @param groupId
     * @return
     */
    public static GroupInfo getGroupInfo(String groupId) {
        String selection = GroupInfoColumn.GROUP_ID + " = ? ";
        String[] selectionArgs = new String[]{groupId};
        GroupInfo groupInfo = null;
        Cursor cursor = null;
        try {
            cursor = getInstance().sqliteDB().query(DatabaseHelper.TABLES_NAME_GROUP_INFO, null, selection, selectionArgs, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    //数据转换
                    groupInfo = new GroupInfo();
                    groupInfo.setG_id(cursor.getString(cursor.getColumnIndex(GroupInfoColumn.GROUP_ID)));
                    groupInfo.setG_name(cursor.getString(cursor.getColumnIndex(GroupInfoColumn.GROUP_NAME)));
                    groupInfo.setG_admin_username(cursor.getString(cursor.getColumnIndex(GroupInfoColumn.GROUP_ADMIN)));
                    groupInfo.setG_create_time(cursor.getString(cursor.getColumnIndex(GroupInfoColumn.GROUP_CREATE_TIME)));
                    groupInfo.setG_declared(cursor.getString(cursor.getColumnIndex(GroupInfoColumn.GROUP_DECLARED)));
                    groupInfo.setG_header(cursor.getString(cursor.getColumnIndex(GroupInfoColumn.GROUP_THUMB)));
                    groupInfo.setG_permission(cursor.getInt(cursor.getColumnIndex(GroupInfoColumn.GROUP_PERMISSION)));
                    groupInfo.setG_type(cursor.getInt(cursor.getColumnIndex(GroupInfoColumn.GROUP_TYPE)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return groupInfo;
    }

    /**
     * 获取指定群组的名称
     * 慎用
     *
     * @param groupName
     * @return
     */
    public static List<GroupInfo> searchGroup(String groupName) {
        String sql = "select groups.groupid, group_name, group_thumb FROM groups_info left join groups on groups.groupid = groups_info.groupid where groups_info.group_name = '"+groupName+"'";
        Cursor cursor = null;
        List<GroupInfo> groupInfos = null;
        try {
            cursor = getInstance().sqliteDB().rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                groupInfos = new ArrayList<>();
                while (cursor.moveToNext()) {
                    //数据转换
                    GroupInfo groupInfo = new GroupInfo();
                    groupInfo.setG_id(cursor.getString(0));
                    groupInfo.setG_name(cursor.getString(1));
                    groupInfo.setG_header(cursor.getString(2));
                    groupInfos.add(groupInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return groupInfos;
    }

    /**
     * 更新群组名称
     *
     * @param groupId
     * @param groupName
     * @return
     */
    public static int updateGroupInfoName(String groupId, String groupName) {
        int row = -1;
        if (TextUtils.isEmpty(groupId) || TextUtils.isEmpty(groupName)) {
            return row;
        }
        String whereClause = GroupInfoColumn.GROUP_ID + " = ? ";
        String[] whereArgs = new String[]{groupId};
        ContentValues values = null;
        try {
            values = new ContentValues();
            values.put(GroupInfoColumn.GROUP_NAME, groupName);
            row = getInstance().sqliteDB().update(DatabaseHelper.TABLES_NAME_GROUP_INFO, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (values != null)
                values.clear();
        }
        return row;
    }

    /**
     * 更新群组公告
     *
     * @param groupId
     * @param groupDeclare
     * @return
     */
    public static int updateGroupInfoDeclared(String groupId, String groupDeclare) {
        int row = -1;
        if (TextUtils.isEmpty(groupId) || TextUtils.isEmpty(groupDeclare)) {
            return row;
        }
        String whereClause = GroupInfoColumn.GROUP_ID + " = ? ";
        String[] whereArgs = new String[]{groupId};
        ContentValues values = null;
        try {
            values = new ContentValues();
            values.put(GroupInfoColumn.GROUP_DECLARED, groupDeclare);
            row = getInstance().sqliteDB().update(DatabaseHelper.TABLES_NAME_GROUP_INFO, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (values != null)
                values.clear();
        }
        return row;
    }

    /**
     * 更新群申请权限
     *
     * @param groupId
     * @param groupPermission
     * @return
     */
    public static int updateGroupInfoPermission(String groupId, int groupPermission) {
        int row = -1;
        if (TextUtils.isEmpty(groupId) || groupPermission < 0) {
            return row;
        }
        String whereClause = GroupInfoColumn.GROUP_ID + " = ? ";
        String[] whereArgs = new String[]{groupId};
        ContentValues values = null;
        try {
            values = new ContentValues();
            values.put(GroupInfoColumn.GROUP_PERMISSION, groupPermission);
            row = getInstance().sqliteDB().update(DatabaseHelper.TABLES_NAME_GROUP_INFO, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (values != null)
                values.clear();
        }
        return row;
    }

    /**
     * 更新群图像
     *
     * @param groupId
     * @param groupIcon
     * @return
     */
    public static int updateGroupInfoIcon(String groupId, String groupIcon) {
        int row = -1;
        if (TextUtils.isEmpty(groupId) || TextUtils.isEmpty(groupIcon)) {
            return row;
        }
        String whereClause = GroupInfoColumn.GROUP_ID + " = ? ";
        String[] whereArgs = new String[]{groupId};
        ContentValues values = null;
        try {
            values = new ContentValues();
            values.put(GroupInfoColumn.GROUP_THUMB, groupIcon);
            row = getInstance().sqliteDB().update(DatabaseHelper.TABLES_NAME_GROUP_INFO, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (values != null)
                values.clear();
        }
        return row;
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
