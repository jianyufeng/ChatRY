package com.sanbafule.sharelock.modle;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:Created by 简玉锋 on 2016/12/13 13:41
 * 邮箱: jianyufeng@38.hn
 */

public class GroupTransform {

    //  GroupListInfo  To   Group
    public static Group GroupListInfoToGroup(GroupListInfo groupInfo) {
        Group group = null;
        if (groupInfo != null && !TextUtils.isEmpty(groupInfo.getGur_gid())) {
            group = new Group(groupInfo.getGur_gid());
        }
        return group;
    }

    //List<GroupListInfo>  To   ArrayList<Group>
    public static ArrayList<Group> GroupListInfoToGroup(List<GroupListInfo> groupInfos) {
        ArrayList<Group> groups = null;
        if (groupInfos != null && !groupInfos.isEmpty()) {
            groups = new ArrayList<>();
            for (GroupListInfo groupInfo : groupInfos) {
                Group group = GroupListInfoToGroup(groupInfo);
                if (group != null) {
                    groups.add(group);
                }
            }
        }
        return groups;
    }

    // GroupListInfo  To  GroupInfo
    public static GroupInfo GroupListInfoToGroupInfo(GroupListInfo groupListInfo) {
        GroupInfo groupInfo = null;
        if (groupListInfo != null && !TextUtils.isEmpty(groupListInfo.getGur_gid())) {
            groupInfo = new GroupInfo();
            groupInfo.setG_type(-1);
            groupInfo.setG_permission(groupListInfo.getG_permission());
            groupInfo.setG_header(groupListInfo.getG_header());
            groupInfo.setG_id(groupListInfo.getGur_gid());
            groupInfo.setG_name(groupListInfo.getG_name());
            groupInfo.setG_admin_username(groupListInfo.getG_admin_username());
            groupInfo.setG_declared(groupListInfo.getG_declared());
            groupInfo.setG_create_time(groupListInfo.getG_create_time());
        }
        return groupInfo;
    }

    // List<GroupListInfo>  To   ArrayList<GroupInfo>
    public static ArrayList<GroupInfo> GroupListInfoToGroupInfo(List<GroupListInfo> groupListInfos) {
        ArrayList<GroupInfo> groupInfos = null;
        if (groupListInfos != null && !groupListInfos.isEmpty()) {
            groupInfos = new ArrayList<>();
            for (GroupListInfo groupListInfo : groupListInfos) {
                GroupInfo groupInfo = GroupListInfoToGroupInfo(groupListInfo);
                if (groupInfo != null) {
                    groupInfos.add(groupInfo);
                }
            }
        }
        return groupInfos;
    }
}
