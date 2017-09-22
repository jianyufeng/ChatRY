package com.sanbafule.sharelock.db;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;

import com.sanbafule.sharelock.SApplication;
import com.sanbafule.sharelock.chatting.db.OnMessageChange;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.global.MyString;
import com.sanbafule.sharelock.global.ReceiveAction;
import com.sanbafule.sharelock.modle.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/2.
 */
public class ContactSqlManager extends AbstractSQLManager {
    public static ContactSqlManager sInstance;
    private static final String TAG = "ContactSqlManager";

    public static ContactSqlManager getInstance() {
        if (sInstance == null) {
            sInstance = new ContactSqlManager();
        }
        return sInstance;
    }


    /**
     * 插入联系人列表
     *
     * @param list
     */
    public static void insertContacts(List<Contact> list) {

        for (int i = 0; i < list.size(); i++) {
            insertContact(list.get(i));
        }
        getInstance().notifyChanged(null);
    }


    /**
     * 添加联系人
     *
     * @param contact
     * @return
     */
    public static long insertContact(Contact contact) {
        long row = -1;
        if (contact == null) {
            return row;
        }
        ContentValues values = new ContentValues();
        values.put(ContactsColumn.USERNAME, contact.getU_username());
        if (MyString.hasData(contact.getU_nickname())) {
            values.put(ContactsColumn.NICKNAME, contact.getU_nickname());
        }
        if (MyString.hasData(contact.getU_note())) {
            values.put(ContactsColumn.NOTE, contact.getU_note());
        }
        if (hasThisFriend(contact.getU_username())) {
            String whereClause = ContactsColumn.USERNAME + "=?";
            String[] whereArgs = {contact.getU_username()};//修改条件的参数
            row = getInstance().sqliteDB().update(DatabaseHelper.TABLES_NAME_CONTACT, values, whereClause, whereArgs);
        } else {
            row = getInstance().sqliteDB().insert(DatabaseHelper.TABLES_NAME_CONTACT, null, values);
        }
        return row;
    }

    /**
     * 添加联系人
     *
     * @param name
     * @return
     */
    public static long insertContact(String name) {
        long row = -1;
        Contact contact = new Contact();
        contact.setU_username(name);
        contact.setU_note("");
        ContentValues values = new ContentValues();
        values.put(ContactsColumn.USERNAME, contact.getU_username());
        values.put(ContactsColumn.NOTE, contact.getU_note());
        row = getInstance().sqliteDB().insert(DatabaseHelper.TABLES_NAME_CONTACT, null, values);
        return row;
    }


    /**
     * 获取联系人列表
     */
    public static List<Contact> getAllContacts() {

        String sql = "select * from contacts ";
        List<Contact> list = new ArrayList<>();
        Contact contact = null;
        Cursor cursor = getInstance().sqliteDB().rawQuery(sql, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                contact = new Contact();
                contact.setU_username(cursor.getString(cursor.getColumnIndex(ContactsColumn.USERNAME)));
                contact.setU_note(cursor.getString(cursor.getColumnIndex(ContactsColumn.NOTE)));
                contact.setU_nickname(cursor.getString(cursor.getColumnIndex(ContactsColumn.NICKNAME)));
                list.add(contact);
            }
        }

        return list;
    }


    // 判断联系人列表中是否有某人
    public static boolean hasThisFriend(String name) {
        String sql = "select * from contacts where username ='" + name + "'";
        Cursor cursor = getInstance().sqliteDB().rawQuery(sql, null);
        if (cursor != null && cursor.getCount() > 0) {
            return true;
        }
        return false;
    }


    /**
     * 更新备注
     *
     * @param userName
     * @param note
     */
    public static void updateNote(String userName, String note) {
        if (!MyString.hasData(userName) || !MyString.hasData(note)) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(ContactsColumn.NOTE, note);
        String whereClause = ContactsColumn.USERNAME + "=?";
        String[] whereArgs = {userName};//修改条件的参数
        getInstance().sqliteDB().update(DatabaseHelper.TABLES_NAME_CONTACT, values, whereClause, whereArgs);
        getInstance().notifyChanged(null);

    }

    /**
     * 获取备注
     *
     * @param userName
     * @return
     */
    public static String getContactNote(String userName) {

        String sql = "select note from contacts where username ='" + userName + "'";
        Cursor cursor = getInstance().sqliteDB().rawQuery(sql, null);
        if (cursor != null || cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                return cursor.getString(cursor.getColumnIndex(ContactsColumn.NOTE));
            }
        }
        return null;
    }

    /**
     * 删除好友
     *
     * @param name
     */
    public static void deleteContact(String name) {
        if (hasThisFriend(name)) {
            String[] whereArgs = {name};
            getInstance().sqliteDB().delete(DatabaseHelper.TABLES_NAME_CONTACT, ContactsColumn.USERNAME + "=?", whereArgs);
        }
        //删除好友发送广播
        SApplication.getInstance().sendBroadcast(new Intent(ReceiveAction.ACTION_DELETE_FRIEND).putExtra(SString.TARGETNAME, name));
        ContactsCache.getInstance().reload();
    }


    /**
     * 设置免打扰
     *
     * @param name
     * @param nodisturb
     */
    public static void setNodisturb(String name, boolean nodisturb) {
        if (!MyString.hasData(name)) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(ContactsColumn.NO_DISTURB, nodisturb ? 1 : 0);
        String whereClause = ContactsColumn.USERNAME + "=?";
        String[] whereArgs = {name};//修改条件的参数
        getInstance().sqliteDB().update(DatabaseHelper.TABLES_NAME_CONTACT, values, whereClause, whereArgs);
        getInstance().notifyChanged(null);
    }

    /**
     * 获取免打扰的设置
     *
     * @param name
     * @return
     */
    public static boolean isNodisturb(String name) {
        String sql = "select no_disturb from contacts where username ='" + name + "'";
        Cursor cursor = getInstance().sqliteDB().rawQuery(sql, null);
        if (cursor != null || cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                if (cursor.getInt(cursor.getColumnIndex(ContactsColumn.NO_DISTURB)) == 1) {
                    return true;
                }
            }
        }
        return false;
    }


    /***
     * 设置好友黑名单
     *
     * @param name
     * @param isBlackList
     */
    public static void setIsBlackList(String name, boolean isBlackList) {
        if (!MyString.hasData(name)) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(ContactsColumn.IS_BLACK, isBlackList ? 1 : 0);
        String whereClause = ContactsColumn.USERNAME + "=?";
        String[] whereArgs = {name};//修改条件的参数
        getInstance().sqliteDB().update(DatabaseHelper.TABLES_NAME_CONTACT, values, whereClause, whereArgs);
    }

    /**
     * 获取好友是不是黑名单
     *
     * @param name
     * @return
     */
    public static boolean isBlackList(String name) {
        String sql = "select is_black from contacts where username ='" + name + "'";
        Cursor cursor = getInstance().sqliteDB().rawQuery(sql, null);
        if (cursor != null || cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                if (cursor.getInt(cursor.getColumnIndex(ContactsColumn.IS_BLACK)) == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void deleteAll() {
        String sql = "DELETE FROM contacts";
        getInstance().sqliteDB().execSQL(sql);
    }


    /***
     * 注册联系人观察者
     *
     * @param observer
     */
    public static void registerMsgObserver(OnMessageChange observer) {
        getInstance().registerObserver(observer);
    }

    public static void unregisterMsgObserver(OnMessageChange observer) {
        getInstance().unregisterObserver(observer);
    }

    public static void notifyMsgChanged(String session) {
        getInstance().notifyChanged(session);
    }

    public static void reset() {
        getInstance().release();
    }

    @Override
    protected void release() {
        super.release();
        sInstance = null;
    }

}
