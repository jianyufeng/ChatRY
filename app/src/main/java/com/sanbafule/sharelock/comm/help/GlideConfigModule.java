package com.sanbafule.sharelock.comm.help;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.module.GlideModule;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/13 15:43
 * cd : 三八妇乐
 * 描述：
 */
public class GlideConfigModule  implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {


        MemorySizeCalculator calculator = new MemorySizeCalculator(context);
        int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
        int defaultBitmapPoolSize = calculator.getBitmapPoolSize();

        int customMemoryCacheSize = (int) (1.2 * defaultMemoryCacheSize);
        int customBitmapPoolSize = (int) (1.2 * defaultBitmapPoolSize);

        builder.setMemoryCache( new LruResourceCache( customMemoryCacheSize ));
        builder.setBitmapPool( new LruBitmapPool( customBitmapPoolSize ));

        String downloadDirectoryPath = Environment.getDownloadCacheDirectory().getPath();

        builder.setDiskCache(
                new DiskLruCacheFactory( downloadDirectoryPath, 100*1024*1024 )
        );
//        builder.setDiskCache(
//        new ExternalCacheDiskCacheFactory(context, 100*1024*1024));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }




}
