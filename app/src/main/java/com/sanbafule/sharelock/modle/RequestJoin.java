package com.sanbafule.sharelock.modle;

/**
 * 作者:Created by 简玉锋 on 2016/12/19 14:35
 * 邮箱: jianyufeng@38.hn
 */

public class RequestJoin {

    /**
     * gal_id : Number
     * gal_gname : String
     * gal_applicant : String
     * gal_reviewers : String
     * gal_status : Number
     * gal_note : String
     * gal_create_time : Number
     * gal_gid : Number
     */

    private String gal_id;
    private String gal_gname;
    private String gal_applicant;
    private String gal_reviewers;
    private int gal_status;
    private String gal_note;
    private String gal_create_time;
    private String gal_gid;

    public void setGal_id(String gal_id) {
        this.gal_id = gal_id;
    }

    public void setGal_gname(String gal_gname) {
        this.gal_gname = gal_gname;
    }

    public void setGal_applicant(String gal_applicant) {
        this.gal_applicant = gal_applicant;
    }

    public void setGal_reviewers(String gal_reviewers) {
        this.gal_reviewers = gal_reviewers;
    }

    public void setGal_status(int gal_status) {
        this.gal_status = gal_status;
    }

    public void setGal_note(String gal_note) {
        this.gal_note = gal_note;
    }

    public void setGal_create_time(String gal_create_time) {
        this.gal_create_time = gal_create_time;
    }

    public void setGal_gid(String gal_gid) {
        this.gal_gid = gal_gid;
    }

    public String getGal_id() {
        return gal_id;
    }

    public String getGal_gname() {
        return gal_gname;
    }

    public String getGal_applicant() {
        return gal_applicant;
    }

    public String getGal_reviewers() {
        return gal_reviewers;
    }

    public int getGal_status() {
        return gal_status;
    }

    public String getGal_note() {
        return gal_note;
    }

    public String getGal_create_time() {
        return gal_create_time;
    }

    public String getGal_gid() {
        return gal_gid;
    }
}
