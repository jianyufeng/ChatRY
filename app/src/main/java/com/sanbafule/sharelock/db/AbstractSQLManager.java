/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
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
package com.sanbafule.sharelock.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.sanbafule.sharelock.SApplication;
import com.sanbafule.sharelock.chatting.db.MessageObservable;
import com.sanbafule.sharelock.chatting.db.OnMessageChange;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.util.LogUtil;

/**
 *
 */
public abstract class AbstractSQLManager {

    public static final String TAG = "tag";

    private static DatabaseHelper databaseHelper;
    private static SQLiteDatabase sqliteDB;

    public AbstractSQLManager() {

        openDatabase(SApplication.getInstance(), 1);
    }

    /**
     * 打开或新建数据库
     *
     * @param context
     * @param databaseVersion
     */
    private void openDatabase(Context context, int databaseVersion) {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context, this, databaseVersion);
        }

        if (sqliteDB == null) {
            sqliteDB = databaseHelper.getWritableDatabase();
        }

    }

    /**
     * 销毁数据库
     */
    public void destroy() {
        try {
            if (databaseHelper != null) {
                databaseHelper.close();
            }
            closeDB();
        } catch (Exception e) {
            LogUtil.i(e.toString());
        }
    }

    /**
     * 打开数据库
     *
     * @param isReadonly
     */
    private void open(boolean isReadonly) {
        if (sqliteDB == null) {
            if (isReadonly) {
                sqliteDB = databaseHelper.getReadableDatabase();
            } else {
                sqliteDB = databaseHelper.getWritableDatabase();/*DatabaseManager.getInstance().openDatabase()*/

            }
        }
    }

    /**
     * 重复打开
     */
    public final void reopen() {
        closeDB();
        open(false);
        LogUtil.w("[SQLiteManager] reopen this db.");
    }

    /***
     * 关闭数据库
     */
    private void closeDB() {
        if (sqliteDB != null) {

            sqliteDB.close();
            sqliteDB = null;
        }
    }

    protected final SQLiteDatabase sqliteDB() {
        open(false);
        return sqliteDB;
    }


    /**
     * 创建基础表结构
     */
    static class DatabaseHelper extends SQLiteOpenHelper {

        /**
         * 数据库名称
         */
        static final String DATABASE_NAME = "ShareLock.db";
        /***/
        static final String DESC = "DESC";
        /****/
        static final String ASC = "ASC";

        /**
         * 联系人表名
         */
        static final String TABLES_NAME_CONTACT = "contacts";
        /**
         * 联系人信息表
         */
        static final String TABLES_NAME_CONTACTINFO = "contacts_info";
        /**
         * 群组的表名
         */
        static final String TABLES_NAME_GROUP = "groups";
        /**
         * 群组信息表名
         */
        static final String TABLES_NAME_GROUP_INFO = "groups_info";
        /**
         * 群组成员表
         ***/
        static final String TABLES_NAME_GROUP_MEMBERS = "group_members";

        private AbstractSQLManager mAbstractSQLManager;

        /*****
         * 构造方法
         *
         * @param context
         * @param manager
         * @param version
         */
        public DatabaseHelper(Context context, AbstractSQLManager manager, int version) {
            this(context, manager, ShareLockManager.getInstance().getUserName() + "_" + DATABASE_NAME, null, version);

        }

        /**
         * 构造方法
         *
         * @param context
         * @param manager
         * @param name
         * @param factory
         * @param version
         */
        public DatabaseHelper(Context context, AbstractSQLManager manager, String name,
                              CursorFactory factory, int version) {
            super(context, name, factory, version);
            mAbstractSQLManager = manager;
        }

        /***
         * 创建表
         *
         * @param db
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            createTables(db);
        }


        /***
         * 更新表
         *
         * @param db
         * @param oldVersion
         * @param newVersion
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        }


        /**
         * 创建表
         *
         * @param db
         */
        private void createTables(SQLiteDatabase db) {
            // 创建联系人表
            createTableForContacts(db);
            // 创建联系人信息表
            createTableForContactsInfo(db);
            // 创建群组表
            createTableForGroup(db);
            // 创建群组信息表
            createTableForGroupInfo(db);
            //创建群组成员表
            createTableGroupMembers(db);
        }

        /**
         * 创建群组信息表
         *
         * @param db
         */
        private void createTableForGroupInfo(SQLiteDatabase db) {
            String sql = "CREATE TABLE IF NOT EXISTS "
                    + TABLES_NAME_GROUP_INFO
                    + " ("
                    + GroupInfoColumn.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + GroupInfoColumn.GROUP_ID + " INTEGER UNIQUE ON CONFLICT ABORT, "
                    + GroupInfoColumn.GROUP_NAME + " TEXT ,"
                    + GroupInfoColumn.GROUP_THUMB + " TEXT ,"
                    + GroupInfoColumn.GROUP_ADMIN + " TEXT ,"
                    + GroupInfoColumn.GROUP_CREATE_TIME + " TEXT ,"
                    + GroupInfoColumn.GROUP_QRCODE + " TEXT ,"
                    + GroupInfoColumn.GROUP_PERMISSION + " INTEGER ,"
                    + GroupInfoColumn.GROUP_TYPE + " INTEGER ,"
                    + GroupInfoColumn.GROUP_DECLARED + " TEXT "
                    + ")";
            LogUtil.v(TAG + ":" + sql);
            db.execSQL(sql);
        }

        /**
         * 创建群组表
         *
         * @param db
         */
        private void createTableForGroup(SQLiteDatabase db) {
            String sql = "CREATE TABLE IF NOT EXISTS "
                    + TABLES_NAME_GROUP
                    + " ("
                    + GroupColumn.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + GroupColumn.GROUP_ID + " TEXT "
                    + ")";
            LogUtil.v(TAG + ":" + sql);
            db.execSQL(sql);

        }

        /**
         * 创建群组成员数据库
         *
         * @param db
         */
        void createTableGroupMembers(SQLiteDatabase db) {
            String sql = "CREATE TABLE IF NOT EXISTS "
                    + TABLES_NAME_GROUP_MEMBERS
                    + " ("
                    + GroupMembersColumn.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + GroupMembersColumn.GROUP_ID + " TEXT, "
                    + GroupMembersColumn.MEMBER_ACCOUNT + " TEXT, "
                    + GroupMembersColumn.MEMBER_CARD + " TEXT, "
                    + GroupMembersColumn.MEMBER_ROLE + " INTEGER DEFAULT 0, "
                    + GroupMembersColumn.MEMBER_BAN + " INTEGER DEFAULT 0 "
                    + ")";
            LogUtil.v(TAG + ":" + sql);
            db.execSQL(sql);
        }

        /**
         * 创建联系人信息表
         *
         * @param db
         */
        private void createTableForContactsInfo(SQLiteDatabase db) {
            String sql = "CREATE TABLE IF NOT EXISTS "
                    + TABLES_NAME_CONTACTINFO
                    + " ("
                    + ContactInfoColumn.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + ContactInfoColumn.USERID + " INTEGER UNIQUE ON CONFLICT ABORT, "
                    + ContactInfoColumn.USERNAME + " TEXT , "
                    + ContactInfoColumn.NICKNAME + " TEXT, "
                    + ContactInfoColumn.EMAIL + " TEXT, "
                    + ContactInfoColumn.SEX + " INTEGER DEFAULT 0, "
                    + ContactInfoColumn.HEADERURL + " TEXT, "
                    + ContactInfoColumn.QR_CODE + " TEXT, "
                    + ContactInfoColumn.CREATE_TIME + " INTEGER DEFAULT 0,"
                    + ContactInfoColumn.LEVEL + " TEXT, "
                    + ContactInfoColumn.SIGN + " TEXT, "
                    + ContactInfoColumn.TYPE + " TEXT "
                    + ")";
            LogUtil.v(TAG + ":" + sql);
            db.execSQL(sql);
        }


        /**
         * 创建联系人表
         *
         * @param db
         */
        void createTableForContacts(SQLiteDatabase db) {
            String sql = "CREATE TABLE IF NOT EXISTS "
                    + TABLES_NAME_CONTACT
                    + " ("
                    + ContactsColumn.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + ContactsColumn.USERNAME + " TEXT , "
                    + ContactsColumn.NICKNAME + " TEXT , "
                    + ContactsColumn.NO_DISTURB + " INTEGER DEFAULT 0, "
                    + ContactsColumn.IS_BLACK + " INTEGER DEFAULT 0, "
                    + ContactsColumn.NOTE + " TEXT  "
                    + ")";
            LogUtil.v(TAG + ":" + sql);
            db.execSQL(sql);
        }

    }


    class BaseColumn {
        public static final String ID = "ID";
    }


    /**
     * 联系人表
     */
    public class ContactsColumn extends BaseColumn {
        // 用户名
        public static final String USERNAME = "username";
        // 昵称
        public static final String NICKNAME = "nickname";
        // 备注
        public static final String NOTE = "note";
        //免打扰
        public static final String NO_DISTURB="no_disturb";
        // 黑名单
        public static final String IS_BLACK="is_black";


    }

    public final class ContactInfoColumn extends BaseColumn {


        public static final String USERID = "userid";


        public static final String USERNAME = "username";

        public static final String NICKNAME = "nickname";


        public static final String EMAIL = "email";


        public static final String HEADERURL = "headerurl";


        public static final String LEVEL = "level";


        public static final String SEX = "sex";

        public static final String QR_CODE = "qr_code";

        public static final String IS_DISABLE = "is_disable";

        public static final String CREATE_TIME = "create_time";

        public static final String TYPE = "type";

        public static final String SIGN = "sign";


    }


    /**
     * 群组的表
     */
    class GroupColumn extends BaseColumn {
        /**
         * 群组ID
         */
        static final String GROUP_ID = "groupid";
    }

    /**
     * 群组信息的表
     */
    class GroupInfoColumn extends GroupColumn {
        /**
         * 群组名称
         */
        static final String GROUP_NAME = "group_name";
        /**
         * 群组图像
         */
        static final String GROUP_THUMB = "group_thumb";
        /**
         * 群组创建时间
         */
        static final String GROUP_CREATE_TIME = "group_create_time";
        /**
         * 群组创建者
         */
        static final String GROUP_ADMIN = "group_admin";
        /**
         * 群组权限
         */
        static final String GROUP_PERMISSION = "group_permission";
        /**
         * 群组类型
         */
        static final String GROUP_TYPE = "group_type";
        /**
         * 群组公告
         */
        static final String GROUP_DECLARED = "group_declared";
        /**
         * 群组二维码
         */
        static final String GROUP_QRCODE = "group_qrcode";
    }

    /**
     * 群组成员表
     */
    class GroupMembersColumn extends BaseColumn {
        // 所属群组id
        static final String GROUP_ID = "group_id";
        // 是否禁言
        static final String MEMBER_BAN = "member_ban";
        //成员账号
        static final String MEMBER_ACCOUNT = "member_account";
        // 成员名片
        static final String MEMBER_CARD = "member_card";
        // 成员角色
        static final String MEMBER_ROLE = "member_role";
    }


    private final MessageObservable mMsgObservable = new MessageObservable();

    /**
     * 注册观察者
     *
     * @param observer
     */
    protected void registerObserver(OnMessageChange observer) {
        mMsgObservable.registerObserver(observer);
    }

    /***
     * 注销观察者
     *
     * @param observer
     */
    protected void unregisterObserver(OnMessageChange observer) {
        mMsgObservable.unregisterObserver(observer);
    }

    /**
     * 分发数据库改变通知
     *
     * @param session
     */
    protected void notifyChanged(String session) {
        mMsgObservable.notifyChanged(session);
    }


    /**
     * 释放资源
     */
    protected void release() {
        destroy();
        closeDB();
        databaseHelper = null;
    }
}
