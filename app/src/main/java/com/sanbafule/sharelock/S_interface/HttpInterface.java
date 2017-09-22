package com.sanbafule.sharelock.S_interface;

import org.json.JSONException;

/**
 * 2016-10-8
 * ShareLock
 * http请求的回执接口
 */
public interface HttpInterface {


    void onFailure() ;

    void onSucceed(String s) ;

}
