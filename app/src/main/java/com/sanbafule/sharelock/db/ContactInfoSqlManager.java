package com.sanbafule.sharelock.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.sanbafule.sharelock.global.MyString;
import com.sanbafule.sharelock.modle.ContactInfo;

/**
 * Created by Administrator on 2016/11/2.
 */
public class ContactInfoSqlManager extends AbstractSQLManager {
    public static ContactInfoSqlManager instance;

    private ContactInfoSqlManager() {

    }

    public static ContactInfoSqlManager getInstance() {
        if (instance == null) {
            instance = new ContactInfoSqlManager();
        }
        return instance;
    }


    /**
     * 根据id 查询用户信息
     *
     * @param userName
     * @return
     */
    public static ContactInfo getContactInfoFormuserName(String userName) {
        String sql = "select * from contacts_info where username = '" + userName + "'";
        ContactInfo contactInfo = null;
        Cursor cursor = getInstance().sqliteDB().rawQuery(sql, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                contactInfo = new ContactInfo();
                contactInfo.setU_id(cursor.getInt(cursor.getColumnIndex(ContactInfoColumn.USERID)));
                contactInfo.setU_username(cursor.getString(cursor.getColumnIndex(ContactInfoColumn.USERNAME)));
                contactInfo.setU_nickname(cursor.getString(cursor.getColumnIndex(ContactInfoColumn.NICKNAME)));
                contactInfo.setU_email(cursor.getString(cursor.getColumnIndex(ContactInfoColumn.EMAIL)));
                contactInfo.setU_sex(cursor.getInt(cursor.getColumnIndex(ContactInfoColumn.SEX)));
                contactInfo.setU_header(cursor.getString(cursor.getColumnIndex(ContactInfoColumn.HEADERURL)));
                contactInfo.setU_qr_code(cursor.getString(cursor.getColumnIndex(ContactInfoColumn.QR_CODE)));
                contactInfo.setU_create_time(cursor.getInt(cursor.getColumnIndex(ContactInfoColumn.CREATE_TIME)));
                contactInfo.setUser_type(cursor.getString(cursor.getColumnIndex(ContactInfoColumn.TYPE)));
                contactInfo.setU_signature(cursor.getString(cursor.getColumnIndex(ContactInfoColumn.SIGN)));
                contactInfo.setUser_level(cursor.getString(cursor.getColumnIndex(ContactInfoColumn.LEVEL)));

            }

        }

        return contactInfo;
    }


    /**
     * 插入用户信息
     *
     * @param info
     * @return
     */
    public static long insertContactInfo(ContactInfo info) {
        long row = -1;
        if (info == null) {
            return row;
        }
        ContentValues values = new ContentValues();
        if (info.getU_id() > -1) {
            values.put(ContactInfoColumn.USERID, info.getU_id());

        }
        if (!TextUtils.isEmpty(info.getU_username())) {
            values.put(ContactInfoColumn.USERNAME, info.getU_username());
        }
        if (!TextUtils.isEmpty(info.getU_nickname())) {
            values.put(ContactInfoColumn.NICKNAME, info.getU_nickname());
        }
        if (!TextUtils.isEmpty(info.getU_header())) {
            values.put(ContactInfoColumn.HEADERURL, info.getU_header());
        }
        if (!TextUtils.isEmpty(info.getU_email())) {
            values.put(ContactInfoColumn.EMAIL, info.getU_email());
        }
        if (!TextUtils.isEmpty(info.getUser_level())) {
            values.put(ContactInfoColumn.LEVEL, info.getUser_level());
        }
        if (info.getU_sex() > -1) {
            values.put(ContactInfoColumn.SEX, info.getU_sex());
        }
        if (!TextUtils.isEmpty(info.getU_qr_code())) {
            values.put(ContactInfoColumn.QR_CODE, info.getU_qr_code());
        }
        if (!TextUtils.isEmpty(info.getUser_type())) {
            values.put(ContactInfoColumn.TYPE, info.getUser_type());
        }
        if (!TextUtils.isEmpty(info.getU_signature())) {
            values.put(ContactInfoColumn.SIGN, info.getU_signature());
        }
        if (info.getU_create_time() > 0) {
            values.put(ContactInfoColumn.CREATE_TIME, info.getU_create_time());
        }
        if (haveThisContactInfo(info.getU_username())) {
            String whereClause = ContactInfoColumn.USERID + "=?";
            String[] whereArgs = {String.valueOf(info.getU_id())};//修改条件的参数
            row = getInstance().sqliteDB().update(DatabaseHelper.TABLES_NAME_CONTACTINFO, values, whereClause, whereArgs);
        } else {
            row = getInstance().sqliteDB().insert(DatabaseHelper.TABLES_NAME_CONTACTINFO, null, values);
        }
        return row;
    }


    /**
     * 判断是否有这条记录
     *
     * @param userName
     * @return
     */
    public static boolean haveThisContactInfo(String userName) {

        boolean have = false;
        String sql = "select username from contacts_info where username = '" + userName + "'";
        Cursor cursor = getInstance().sqliteDB().rawQuery(sql, null);
        if (cursor != null && cursor.getCount() > 0) {
            have = true;
            cursor.close();
        }

        return have;
    }

    /**
     * 更新头像地址
     *
     * @param userName
     * @param imgUrl
     */
    public static void updateHeadImg(String userName, String imgUrl) {
        if (userName == null || imgUrl == null) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(ContactInfoColumn.HEADERURL, imgUrl);
        String whereClause = ContactInfoColumn.USERNAME + "=?";
        String[] whereArgs = {userName};//修改条件的参数
        getInstance().sqliteDB().update(DatabaseHelper.TABLES_NAME_CONTACTINFO, values, whereClause, whereArgs);
    }

    public static void reset() {
        getInstance().release();
    }

    @Override
    protected void release() {
        super.release();
        instance = null;
    }
}
