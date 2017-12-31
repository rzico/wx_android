package com.rzico.weex.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

import com.rzico.weex.Constant;
import com.rzico.weex.activity.BaseActivity;
import com.taobao.weex.utils.WXLogUtils;
import com.yixiang.mopian.constant.AllConstant;

import org.apache.http.util.EncodingUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Jinlesoft on 2017/9/26.
 * 管理本地资源路径 以及 weex访问的本地路径
 */

public class PathUtils {

    /*
      保存图片路径
      这里是 photohandle 保存的路径
     */
    //保存图片
    public static File saveBitmapJPG(String mBitmapName, Bitmap mBitmap) throws IOException {

//        File fileDir = new File(context.getExternalCacheDir() + "/MOPIAN");
        //缓存路径
        File fileDir = new File(AllConstant.CACHEIMAGEPATH);
        if (!fileDir.exists()) fileDir.mkdirs();
        String fileName =mBitmapName + ".jpg";
        File f = new File(fileDir, fileName);
        FileOutputStream fOut = new FileOutputStream(f);
        if(mBitmap != null && !mBitmap.isRecycled()){
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            mBitmap.recycle();
        }
        mBitmap = null;
        return f;
    }
    /*
  获取缓存路径
 */
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
        return System.currentTimeMillis() + ".jpg";
    }
    /**
     * @param context
     * @param fileName
     * @return
     */
    public static String readAssetsFile(Context context, String fileName) {
        String res = "";

        try {
            InputStream in = context.getResources().getAssets().open(fileName);

            int length = in.available();
            byte[] buffer = new byte[length];
            in.read(buffer);
            res = EncodingUtils.getString(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     *
     * @param path 绝对路径
     * @return
     */
    public static String loadLocal(String path, Activity activity) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        if(path.startsWith("file://")){
            path = path.replace("file://", "");
        }

// 这是参数 要带参数 二 下面的打开本地路径 不需要带参数
        path = path.contains("?") ? path.substring(0 ,path.indexOf("?")) : path;
        FileInputStream inputStream = null;
        BufferedReader bufferedReader = null;
        path = PathUtils.getResPath(activity) + path;
        try {
            inputStream = new FileInputStream(path);
            StringBuilder builder = new StringBuilder(inputStream.available() + 10);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            char[] data = new char[4096];
            int len = -1;
            while ((len = bufferedReader.read(data)) > 0) {
                builder.append(data, 0, len);
            }

            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            WXLogUtils.e("", e);
        } finally {
            try {
                if (bufferedReader != null)
                    bufferedReader.close();
            } catch (IOException e) {
                WXLogUtils.e("WXFileUtils loadAsset: ", e);
            }
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                WXLogUtils.e("WXFileUtils loadAsset: ", e);
            }
        }

        return "";
    }
    /**
     * 获取当前项目的weex资源路径
     * @return
     */
    public static String getResPath(Activity activity){
        String cacheDir = AllConstant.getDiskCachePath(activity);
        if(cacheDir.contains("/cache")){
            cacheDir = cacheDir.replace("/cache", "");
        }
        return cacheDir + "/res/";
    }

    public static String getCachePath(){
        return Environment.getExternalStorageDirectory().getPath() + "/" + Constant.app + "/cache/";
    }
    /**
     * 获取当前项目路径
     * @return
     */
    public static String getBasePath(){
        return Environment.getExternalStorageDirectory().getPath() + "/" + Constant.app + "/";
    }
}
