/*

 */
package com.sanbafule.sharelock.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.sanbafule.sharelock.modle.GroupMember;

import java.util.ArrayList;
import java.util.List;


/**
 * 作者:Created by 简玉锋 on 2016/12/13 18:46
 * 邮箱: jianyufeng@38.hn
 */

public class GroupMemberSqlManager extends AbstractSQLManager {
    private static final String TAG = "GroupMemberSqlManager";
    Object mLock = new Object();
    private static GroupMemberSqlManager sInstance;

    private static GroupMemberSqlManager getInstance() {
        if (sInstance == null) {
            sInstance = new GroupMemberSqlManager();
        }
        return sInstance;
    }

    private GroupMemberSqlManager() {

    }

    /**
     * 查询所有群组成员帐号
     *
     * @param groupId
     * @return
     */
    public static ArrayList<String> getGroupMemberAccounts(String groupId) {
        ArrayList<String> list = null;
        String[] columns = new String[]{GroupMembersColumn.MEMBER_ACCOUNT};
        String selection = GroupMembersColumn.GROUP_ID + " = ? ";
        String[] selectionArgs = new String[]{groupId};
        Cursor cursor = null;
        try {
            cursor = getInstance().sqliteDB().query(DatabaseHelper.TABLES_NAME_GROUP_MEMBERS, columns, selection, selectionArgs, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                list = new ArrayList<>();
                while (cursor.moveToNext()) {
                    list.add(cursor.getString(cursor.getColumnIndex(GroupMembersColumn.MEMBER_ACCOUNT)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 删除群组成员
     *
     * @param groupId 群组ID
     * @param account 群组成员的账号
     * @return
     */
    public static long delMember(String groupId, String account) {
        long row = -1;
        if (TextUtils.isEmpty(groupId) || TextUtils.isEmpty(account)) {
            return row;
        }
        String whereClause = GroupMembersColumn.GROUP_ID + " = ? and " + GroupMembersColumn.MEMBER_ACCOUNT + " = ? ";
        String[] whereArgs = new String[]{groupId, account};
        try {
            row = getInstance().sqliteDB().delete(DatabaseHelper.TABLES_NAME_GROUP_MEMBERS, whereClause, whereArgs);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return row;

    }

    /**
     * 插入群组成员列表
     *
     * @param members
     * @return
     */
    public static ArrayList<Long> insertGroupMembers(List<GroupMember> members) {

        ArrayList<Long> rows = new ArrayList<>();
        if (members == null) {
            return rows;
        }
        try {
            synchronized (getInstance().mLock) {
                // Set the start transaction
                getInstance().sqliteDB().beginTransaction();

                // Batch processing operation
                for (GroupMember member : members) {
                    try {
                        long row = insertGroupMember(member);
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
     * 插入群组成员到数据库
     *
     * @return
     */
    public static long insertGroupMember(GroupMember member) {
        if (member == null || TextUtils.isEmpty(member.getGid())
                || TextUtils.isEmpty(member.getU_username())) {
            return -1L;
        }
        ContentValues values = null;
        try {
            values = new ContentValues();
            values.put(GroupMembersColumn.GROUP_ID, member.getGid());
            values.put(GroupMembersColumn.MEMBER_ACCOUNT, member.getU_username());
            if (member.getGur_identity() != -1) {
                values.put(GroupMembersColumn.MEMBER_ROLE, member.getGur_identity());
            }
            if (!TextUtils.isEmpty(member.getGur_card())) {
                values.put(GroupMembersColumn.MEMBER_CARD, member.getGur_card());
            }
//            if (!TextUtils.isEmpty(member.getGur_card())) {  //禁言
//                values.put(GroupMembersColumn.MEMBER_BAN, member.getGur_card());;
//            }
            if (!isExitGroupMember(member.getGid(), member.getU_username())) {
                //如果不存在 插入
                return getInstance().sqliteDB().insert(DatabaseHelper.TABLES_NAME_GROUP_MEMBERS, null, values);
            } else { // 如果存在 更新
                String whereClause = GroupMembersColumn.GROUP_ID + " = ? and " + GroupMembersColumn.MEMBER_ACCOUNT + " = ? ";
                String[] whereArgs = new String[]{member.getGid(), member.getU_username()};
                return getInstance().sqliteDB().update(DatabaseHelper.TABLES_NAME_GROUP_MEMBERS, values, whereClause, whereArgs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (values != null) {
                values.clear();
            }

        }
        return -1L;
    }

    /**
     * 是否存在该成员
     *
     * @param groupId
     * @param account
     * @return
     */
    public static boolean isExitGroupMember(String groupId, String account) {
        String[] columns = new String[]{GroupMembersColumn.MEMBER_ACCOUNT};
        String selection = GroupMembersColumn.GROUP_ID + " = ? and " + GroupMembersColumn.MEMBER_ACCOUNT + " = ? ";
        String[] selectionArgs = new String[]{groupId, account};
        Cursor cursor = null;
        try {
            cursor = getInstance().sqliteDB().query(DatabaseHelper.TABLES_NAME_GROUP_MEMBERS, columns, selection, selectionArgs, null, null, null);
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
     * 查询群组成员
     *
     * @param groupId
     * @return
     */
    public static ArrayList<GroupMember> getGroupMembers(String groupId) {
        ArrayList<GroupMember> groupMembers = null;
        String selection = GroupMembersColumn.GROUP_ID + " = ? ";
        String[] selectionArgs = new String[]{groupId};
        Cursor cursor = null;
        try {
            cursor = getInstance().sqliteDB().query(DatabaseHelper.TABLES_NAME_GROUP_MEMBERS, null, selection, selectionArgs, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                groupMembers = new ArrayList<>();
                while (cursor.moveToNext()) {
                    GroupMember groupMember = new GroupMember();
                    groupMember.setGid(cursor.getString(cursor.getColumnIndex(GroupMembersColumn.GROUP_ID)));
                    groupMember.setU_username(cursor.getString(cursor.getColumnIndex(GroupMembersColumn.MEMBER_ACCOUNT)));
                    groupMember.setGur_card(cursor.getString(cursor.getColumnIndex(GroupMembersColumn.MEMBER_CARD)));
                    groupMember.setGur_identity(cursor.getInt(cursor.getColumnIndex(GroupMembersColumn.MEMBER_ROLE)));
//                    groupMember.setIsBan(cursor.getInt(cursor.getColumnIndex(GroupMembersColumn.ISBAN)) == 1 ? true : false);
                    groupMembers.add(groupMember);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return groupMembers;
    }

    /**
     * 查询成员的角色
     *
     * @param groupId
     * @param account
     * @return
     */
    public static int getMemberRole(String groupId, String account) {
        int role = 0;
        String[] columns = new String[]{GroupMembersColumn.MEMBER_ROLE};
        String selection = GroupMembersColumn.GROUP_ID + " = ? and " + GroupMembersColumn.MEMBER_ACCOUNT + " = ? ";
        String[] selectionArgs = new String[]{groupId, account};
        Cursor cursor = null;
        try {
            cursor = getInstance().sqliteDB().query(DatabaseHelper.TABLES_NAME_GROUP_MEMBERS, columns, selection, selectionArgs, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    role = cursor.getInt(cursor.getColumnIndex(GroupMembersColumn.MEMBER_ROLE));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return role;
    }

    /**
     * 查询群组成员
     *
     * @param groupId
     * @return
     */
    public static GroupMember getGroupMember(String groupId, String account) {
        GroupMember groupMember = null;
        String selection = GroupMembersColumn.GROUP_ID + " = ? and " +
                GroupMembersColumn.MEMBER_ACCOUNT + " = ? ";
        String[] selectionArgs = new String[]{groupId, account};
        Cursor cursor = null;
        try {
            cursor = getInstance().sqliteDB().query(DatabaseHelper.TABLES_NAME_GROUP_MEMBERS,
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    groupMember = new GroupMember();
                    groupMember.setGid(cursor.getString(cursor.getColumnIndex(GroupMembersColumn.GROUP_ID)));
                    groupMember.setGur_card(cursor.getString(cursor.getColumnIndex(GroupMembersColumn.MEMBER_CARD)));
                    groupMember.setU_username(cursor.getString(cursor.getColumnIndex(GroupMembersColumn.MEMBER_ACCOUNT)));
//                    groupMember.setU_header(cursor.getString(cursor.getColumnIndex(GroupMembersColumn.MEMBER_BAN)));
                    groupMember.setGur_identity(cursor.getInt(cursor.getColumnIndex(GroupMembersColumn.MEMBER_ROLE)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return groupMember;
    }

    /***
     * 更新群组成员的角色
     *
     * @param groupId 群组id
     * @param account 成员账号
     * @param role    角色   0 成员 1 管理员  2 群主
     * @return
     */
    public static void updateRole(String groupId, String account, int role) {
        ContentValues values = null;
        try {
            values = new ContentValues();
            values.put(GroupMembersColumn.MEMBER_ROLE, role);


            String whereClause = GroupMembersColumn.GROUP_ID + " = ? and " + GroupMembersColumn.MEMBER_ACCOUNT + " = ? ";
            String[] whereArgs = new String[]{groupId, account};

            getInstance().sqliteDB().update(DatabaseHelper.TABLES_NAME_GROUP_MEMBERS,
                    values, whereClause, whereArgs);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (values != null) {
                values.clear();
            }
        }
    }
    /**
     * 查询群组成员账号
     *
     * @param groupId
     * @return
     */
//    public static ArrayList<String> getGroupMemberID(String groupId) {
//        String sql = "select group_member_name from group_members where group_id ='" + groupId + "'";
//        ArrayList<String> list = null;
//        try {
//            Cursor cursor = getInstance().sqliteDB().rawQuery(sql, null);
//            if (cursor != null && cursor.getCount() > 0) {
//                list = new ArrayList<String>();
//                while (cursor.moveToNext()) {
//                    list.add(cursor.getString(cursor.getColumnIndex(GroupMembersColumn.NAME)));
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return list;
//    }


    /**
     * 查询4 个群组成员用于列显表示
     *
     * @param groupId
     * @return
     */
//    public static ArrayList<MyGroupMember> getGroupMemberWithName(String groupId, int limit) {
//        String sql = "select * from group_members where group_id =" + "'" + groupId + "'and  group_member_isRemove = 1 LIMIT '" + limit + "';";
//        ArrayList<MyGroupMember> list = null;
//        try {
//            Cursor cursor = getInstance().sqliteDB().rawQuery(sql, null);
//            if (cursor != null && cursor.getCount() > 0) {
//                list = new ArrayList<MyGroupMember>();
//                while (cursor.moveToNext()) {
//                    MyGroupMember groupMember = new MyGroupMember();
//                    groupMember.setName(cursor.getString(cursor.getColumnIndex(GroupMembersColumn.NAME)));
//                    groupMember.setDisplayName(cursor.getString(cursor.getColumnIndex(GroupMembersColumn.NICKNAME)));
//                    groupMember.setMobPhone(cursor.getString(cursor.getColumnIndex(GroupMembersColumn.TEL)));
//                    groupMember.setHeadImg(cursor.getString(cursor.getColumnIndex(GroupMembersColumn.THUMBIMG)));
//                    groupMember.setBelong(groupId);
//                    groupMember.setIsBan(cursor.getInt(cursor.getColumnIndex(GroupMembersColumn.ISBAN)) == 2 ? true : false);
////                    Log.i(TAG, "getGroupMemberWithName: " + cursor.getInt(cursor.getColumnIndex(GroupMembersColumn.ROLE)));
//                    groupMember.setRole(cursor.getInt(cursor.getColumnIndex(GroupMembersColumn.ROLE)));
//                    groupMember.setIsRemove(cursor.getInt(cursor.getColumnIndex(GroupMembersColumn.ISREMOVE)));
//                    groupMember.setGender(cursor.getInt(cursor.getColumnIndex(GroupMembersColumn.SEX)));
//                    groupMember.setLevel(cursor.getInt(cursor.getColumnIndex(GroupMembersColumn.LEVEL)));
//                    groupMember.setGrade(cursor.getInt(cursor.getColumnIndex(GroupMembersColumn.GRADE)));
//                    groupMember.setSign(cursor.getString(cursor.getColumnIndex(GroupMembersColumn.SIGN)));
//                    groupMember.setCity(cursor.getString(cursor.getColumnIndex(GroupMembersColumn.CITY)));
//                    groupMember.setBrithday(cursor.getString(cursor.getColumnIndex(GroupMembersColumn.BIRTH)));
//                    groupMember.setEmail(cursor.getString(cursor.getColumnIndex(GroupMembersColumn.MAIL)));
//                    groupMember.setUid(cursor.getString(cursor.getColumnIndex(GroupMembersColumn.UID)));
//                    list.add(groupMember);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return list;
//    }

    //获取群组成员数量
//    public static int getGroupMemberCount(String groupId) {
//        String sql = " select count(*) from group_members where group_id =" + "'" + groupId + "'and  group_member_isRemove = 1 ";
//        Cursor cursor = getInstance().sqliteDB().rawQuery(sql, null);
//        int count = 0;
//        if (cursor != null && cursor.getCount() > 0) {
//            while (cursor.moveToNext()) {
//                count = cursor.getInt(0);
//            }
//        }
//        return count;
//    }


//    /**
//     * 更新群组成员
//     * @param members
//     * @return
//     */
//    public static ArrayList<Long> insertGroupMembers(List<ECGroupMember> members) {
//
//        ArrayList<Long> rows = new ArrayList<Long>();
//        if (members == null) {
//            return rows;
//        }
//        try {
//            synchronized (getInstance().mLock) {
//                // Set the start transaction
//                getInstance().sqliteDB().beginTransaction();
//
//                // Batch processing operation
//                for (ECGroupMember member : members) {
//                    try {
//                        long row = inserterOldGroupMember(member);
//                        if(row != -1) {
//                            rows.add(row);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                // Set transaction successful, do not set automatically
//                // rolls back not submitted.
//                getInstance().sqliteDB().setTransactionSuccessful();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            getInstance().sqliteDB().endTransaction();
//        }
//        return rows;
//    }


//    private static void updateContact(MyGroupMember myGroupMember) {
//        ECContacts contacts = new ECContacts();
//        contacts.setFriendname(myGroupMember.getName());
//        contacts.setNickname(myGroupMember.getDisplayName());
//        contacts.setGender(myGroupMember.getGender());
//        contacts.setLv(myGroupMember.getLevel());
//        contacts.setSign(myGroupMember.getSign());
//        contacts.setAddress(myGroupMember.getCity());
//        contacts.setBrithday(myGroupMember.getBrithday());
//        contacts.setEmail(myGroupMember.getEmail());
//        contacts.setMoilePhone(myGroupMember.getMobPhone());
//        contacts.setFriendid(Integer.parseInt(myGroupMember.getUid()));
////        ContactSqlManager.insertContact(contacts);
//    }

    /**
     * 判断性格是否改变
     *
     * @param belong
     * @param userid
     * @param sex
     * @return
     */
//    public static boolean needUpdateSexPhoto(String belong, String userid, int sex) {
//        String sql = "select voipaccount ,sex from group_members where sex !=" + sex + " and voipaccount = '" + userid + "' and group_id='" + belong + "'";
//        Cursor cursor = getInstance().sqliteDB().rawQuery(sql, null);
//        if(cursor != null && cursor.getCount() > 0) {
//            cursor.moveToFirst();
//            int anInt = cursor.getInt(1);
//            String string = cursor.getString(0);
//            cursor.close();;
//            return true;
//        }
//        return false;
//    }


    /**
     * 更新群组成员
     *
     * @param groupId
     */
//    public static void insertGroupMembers(String groupId, ArrayList<ECContacts> list) {
//        if (TextUtils.isEmpty(groupId) || list == null || list.size() <= 0) {
//            return;
//        }
//        for (int i = 0; i < list.size(); i++) {
//            MyGroupMember myGroupMember = new MyGroupMember();
//            myGroupMember.setBelong(groupId);
//            myGroupMember.setName(list.get(i).getFriendname());
//            if (CCPAppManager.getClientUser() != null && (CCPAppManager.getUserId()).equals(list.get(i).getFriendname())) {
//                myGroupMember.setRole(1);
//            } else {
//                myGroupMember.setRole(3);
//            }
//            myGroupMember.setMobPhone(list.get(i).getMoilePhone());
//            myGroupMember.setGender(list.get(i).getGender());
//            myGroupMember.setHeadImg(list.get(i).getHeadimg());
//            myGroupMember.setEmail(list.get(i).getEmail());
//            myGroupMember.setCity(list.get(i).getAddress());
//            myGroupMember.setBrithday(list.get(i).getBrithday());
//            myGroupMember.setSign(list.get(i).getSign());
//            myGroupMember.setLevel(list.get(i).getLv());
//            myGroupMember.setDisplayName(list.get(i).getNickname());
//            myGroupMember.setUid(String.valueOf(list.get(i).getFriendid()));
//            insertGroupMember(myGroupMember);
//        }
//
//    }

    /**
     * 删除群组所有成员
     *
     * @param groupId
     */
//    public static void delAllMember(String groupId) {
//        String sqlWhere = "group_id ='" + groupId + "'";
//        try {
//            getInstance().sqliteDB().delete(DatabaseHelper.TABLES_NAME_GROUP_MEMBERS, sqlWhere, null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 删除群组成员
     *
     * @param groupId 群组ID
     * @param name    群组成员
     * @return
     */
//    public static long delMember(String groupId, String name) {
//
//
//        long row = -1;
//        if (!MyString.hasData(groupId) || !MyString.hasData(name)) {
//            return row;
//        }
//        ContentValues values = new ContentValues();
//        try {
//            values.put(GroupMembersColumn.ISREMOVE, 0);
//
//            String whereClause = GroupMembersColumn.OWN_GROUP_ID + "=?" + "and " + GroupMembersColumn.NAME + "=?";
//            String[] whereArgs = {groupId, name};//修改条件的参数
//            row = getInstance().sqliteDB().update(DatabaseHelper.TABLES_NAME_GROUP_MEMBERS, values, whereClause, whereArgs);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (values != null) {
//                values.clear();
//                values = null;
//            }
//
//        }
//        return row;
//    }

    /**
     * 删除群组成员
     *
     * @param groupId
     * @param members
     */
//    public static void delMember(String groupId, String[] members) {
//        StringBuilder builder = new StringBuilder("in(");
//        for (String member : members) {
//            builder.append("'").append(member).append("'").append(",");
//        }
//        if (builder.toString().endsWith(",")) {
//            builder.replace(builder.length() - 1, builder.length(), "");
//            builder.append(")");
//        } else {
//            builder.replace(0, builder.length(), "");
//        }
//        String sqlWhere = " group_id ='" + groupId + "'" + " and voipaccount " + builder.toString();
//        try {
//            getInstance().sqliteDB().delete(DatabaseHelper.TABLES_NAME_GROUP_MEMBERS, sqlWhere, null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    /**
     * 更新成员禁言状态
     *
     * @param groupid
     * @param member
     * @param enabled
     * @return
     */
//    public static long updateMemberSpeakState(String groupid, String member, boolean enabled) {
//        try {
//            String where = GroupMembersColumn.NAME + "='" + member + "' and " + GroupMembersColumn.OWN_GROUP_ID + "='" + groupid + "'";
//            ContentValues values = new ContentValues();
//            values.put(GroupMembersColumn.ISBAN, enabled ? 2 : 1);
//            return getInstance().sqliteDB().update(DatabaseHelper.TABLES_NAME_GROUP_MEMBERS, values, where, null);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return -1;
//        }
//
//    }

    /**
     * 搜索查询挺符合条件的群组
     *
     * @param keyWords 关键字
     * @return
     */
//    public static ArrayList<MyGroupMember> searchGroupMembers(String keyWords, String groupId) {
//        ArrayList<MyGroupMember> groupMembers = new ArrayList<>();
//        try {
//            String sql = "select group_member_name ,group_member_nickname ,group_member_role ,group_member_thumbimg from " + DatabaseHelper.TABLES_NAME_GROUP_MEMBERS + " where (group_member_nickname like '%" + keyWords + "%' or group_member_name like '%" + keyWords + "%') and  group_id =" + "'" + groupId + "' and group_member_isRemove = 1 order by group_member_role asc ,group_member_nickname asc";
//            Cursor cursor = getInstance().sqliteDB().rawQuery(sql, null);
//            if (cursor != null && cursor.getCount() > 0) {
//                while (cursor.moveToNext()) {
//                    MyGroupMember groupMember = new MyGroupMember();
//                    groupMember.setName(cursor.getString(cursor.getColumnIndex(GroupMembersColumn.NAME)));
//                    groupMember.setDisplayName(cursor.getString(cursor.getColumnIndex(GroupMembersColumn.NICKNAME)));
//                    groupMember.setHeadImg(cursor.getString(cursor.getColumnIndex(GroupMembersColumn.THUMBIMG)));
//                    groupMember.setRole(cursor.getInt(cursor.getColumnIndex(GroupMembersColumn.ROLE)));
//                    groupMembers.add(groupMember);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return groupMembers;
//
//    }
    public static void reset() {
        getInstance().release();
    }

    @Override
    protected void release() {
        super.release();
        sInstance = null;
    }
}
