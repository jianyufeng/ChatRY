package com.sanbafule.sharelock.business;

import android.support.v4.util.ArrayMap;

import com.sanbafule.sharelock.S_interface.HttpInterface;
import com.sanbafule.sharelock.util.OkHttpClientManager;

import java.io.IOException;
import java.util.Map;

import okhttp3.Request;

/**
 * Created by Administrator on 2016/11/2.
 */
public class HttpBiz {



    private  HttpBiz(){

    }
    /**
     * Post  Body中带参数的请求
     * @param url
     * @param map
     * @param httpInterface
     * @throws IOException
     */
    public static void httpPostBiz(String url,ArrayMap<String,String>map, final HttpInterface httpInterface) throws IOException {

        OkHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                httpInterface.onFailure();
            }
            @Override
            public void onResponse(String s) {
                httpInterface.onSucceed(s);

            }
        }, map);


    }


    /**
     *
     * @param url
     * @param map
     * @param httpInterface
     * @throws IOException
     */
    public static void httpPostWithRESTFulBiz(String url,ArrayMap<String,String>map, final HttpInterface httpInterface) throws IOException {
        if(map!=null){
            for (Map.Entry<String, String> entry : map.entrySet()) {
                url+="/"+entry.getKey()+"/"+entry.getValue();
            }
        }
        OkHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                httpInterface.onFailure();
            }
            @Override
            public void onResponse(String s) {
                httpInterface.onSucceed(s);

            }
        });


    }



    /**
     * Get请求
     * @param url
     * @param httpInterface
     * @throws IOException
     */
    public static void httpGetBiz(String url,ArrayMap<String,String>map, final HttpInterface httpInterface) throws IOException {

        if(map!=null){
            for (Map.Entry<String, String> entry : map.entrySet()) {
                url+="/"+entry.getKey()+"/"+entry.getValue();

            }

        }
        OkHttpClientManager.getAsyn(url, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

                httpInterface.onFailure();
            }

            @Override
            public void onResponse(String response)  {

                httpInterface.onSucceed(response);
            }
        });


    }


}
