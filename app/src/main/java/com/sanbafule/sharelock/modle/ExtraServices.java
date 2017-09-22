package com.sanbafule.sharelock.modle;



import java.io.Serializable;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/11 15:04
 * cd : 三八妇乐
 * 描述：
 */
public final class ExtraServices implements Serializable {

    private  int bm_id;
    private String bm_icon;
    private  String bm_name;
    private  int bm_type;
    private String bm_url;
    private String bm_open_type;
    private String bm_open_type_permission;

    public int getBm_id() {
        return bm_id;
    }

    public void setBm_id(int bm_id) {
        this.bm_id = bm_id;
    }

    public String getBm_icon() {
        return bm_icon;
    }

    public void setBm_icon(String bm_icon) {
        this.bm_icon = bm_icon;
    }

    public String getBm_name() {
        return bm_name;
    }

    public void setBm_name(String bm_name) {
        this.bm_name = bm_name;
    }

    public int getBm_type() {
        return bm_type;
    }

    public void setBm_type(int bm_type) {
        this.bm_type = bm_type;
    }

    public String getBm_url() {
        return bm_url;
    }

    public void setBm_url(String bm_url) {
        this.bm_url = bm_url;
    }

    public String getBm_open_type() {
        return bm_open_type;
    }

    public void setBm_open_type(String bm_open_type) {
        this.bm_open_type = bm_open_type;
    }

    public String getBm_open_type_permission() {
        return bm_open_type_permission;
    }

    public void setBm_open_type_permission(String bm_open_type_permission) {
        this.bm_open_type_permission = bm_open_type_permission;
    }




    public ExtraServices() {
    }


}
