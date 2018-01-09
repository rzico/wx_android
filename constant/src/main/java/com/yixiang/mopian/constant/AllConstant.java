package com.yixiang.mopian.constant;

import android.app.Activity;
import android.os.Environment;

import java.io.File;
import java.util.UUID;

/**
 * Created by Jinlesoft on 2017/11/29.
 */

public class AllConstant {
    public static final String PACKNAME = "com.rzico.assistant";

    public static int isClearAll = 1;


    //自己的缓存图片的路径
    public static final  String CACHEIMAGEPATH = Environment.getExternalStorageDirectory().getPath() + "/" + PACKNAME + "/cache/img/";


    /**
     * 图片缓存路径
     * @param activity
     * @return
     */
    public static String getDiskCachePath(Activity activity){
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            File externalCacheDir = activity.getExternalCacheDir();
            if (externalCacheDir != null) {
                cachePath = externalCacheDir.getPath();
            } else {
                cachePath = activity.getCacheDir().getPath();
            }
        } else {
            cachePath = activity.getCacheDir().getPath();
        }
        return cachePath;
    }


    public static File getDiskCacheDir(Activity activity) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            File externalCacheDir = activity.getExternalCacheDir();
            if (externalCacheDir != null) {
                cachePath = externalCacheDir.getPath();
            } else {
                cachePath = activity.getCacheDir().getPath();
            }
        } else {
            cachePath = activity.getCacheDir().getPath();
        }
        return new File(cachePath, imageName());
    }

    public static String imageName() {
        return UUID.randomUUID().toString() + ".jpg";
    }
}
