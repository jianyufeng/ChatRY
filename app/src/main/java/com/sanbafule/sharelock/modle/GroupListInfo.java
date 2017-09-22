package com.sanbafule.sharelock.modle;

/**
 * 作者:Created by 简玉锋 on 2016/12/13 11:57
 * 邮箱: jianyufeng@38.hn
 */

public class GroupListInfo {
    /**
     * gur_gid : 3
     * gur_username : zhouliwen22
     * g_name : 简玉锋
     * g_header : files/2016/12/09/original_1481251638-acioszCH.jpg
     * g_permission : 0
     * g_declared : null
     * g_create_time : 1481251698
     * g_admin_username : zhouliwen22
     */

    private String gur_gid;
    private String gur_username;
    private String g_name;
    private String g_header;
    private int g_permission;
    private String g_declared;
    private String g_create_time;
    private String g_admin_username;

    public void setGur_gid(String gur_gid) {
        this.gur_gid = gur_gid;
    }

    public void setGur_username(String gur_username) {
        this.gur_username = gur_username;
    }

    public void setG_name(String g_name) {
        this.g_name = g_name;
    }

    public void setG_header(String g_header) {
        this.g_header = g_header;
    }

    public void setG_permission(int g_permission) {
        this.g_permission = g_permission;
    }

    public void setG_declared(String g_declared) {
        this.g_declared = g_declared;
    }

    public void setG_create_time(String g_create_time) {
        this.g_create_time = g_create_time;
    }

    public void setG_admin_username(String g_admin_username) {
        this.g_admin_username = g_admin_username;
    }

    public String getGur_gid() {
        return gur_gid;
    }

    public String getGur_username() {
        return gur_username;
    }

    public String getG_name() {
        return g_name;
    }

    public String getG_header() {
        return g_header;
    }

    public int getG_permission() {
        return g_permission;
    }

    public String getG_declared() {
        return g_declared;
    }

    public String getG_create_time() {
        return g_create_time;
    }

    public String getG_admin_username() {
        return g_admin_username;
    }
}
