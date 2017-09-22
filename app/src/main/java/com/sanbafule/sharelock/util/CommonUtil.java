package com.sanbafule.sharelock.util;

import android.support.v4.util.ArrayMap;

/**
 * Created by ShareLock on 2016/8/9.
 *
 * 一些通用方法
 */


public class CommonUtil {




    /**
     * 获取应用运行的最大内存
     *
     * @return 最大内存
     */
    public static long getMaxMemory() {

        return Runtime.getRuntime().maxMemory() / 1024;
    }

    public static void getMap (ArrayMap<String,String>map){
        if(map==null){
            map=new ArrayMap<>();
        }else {
            map.clear();
        }
    }

    public static   int indexOfArr(String[] arr,String value2){
        for(int i=0;i<arr.length;i++){
            if(arr[i].equals(value2)){
                return i;
            }
        }
        return -1;
    }


}
