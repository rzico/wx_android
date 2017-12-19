package com.rzico.weex.utils;

import android.content.SharedPreferences;

import com.rzico.weex.Constant;
import com.rzico.weex.WXApplication;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Jinlesoft on 2017/9/20.
 */

public class SharedUtils {


    public static final String LOGINID = "weex_loginid";
    //以下是项目主页面路由
    public static final String INDEX1 = "weex_index_1";
    public static final String INDEX2 = "weex_index_2";
    public static final String INDEX3 = "weex_index_3";
    public static final String INDEX4 = "weex_index_4";
    public static final String ADD = "weex_add";
    public static final String RESVERSION = "res_version";
    public static final String IMID = "weex_imid";

    public static void saveImId(String imid){
        save(IMID, imid);
    }
    public static String readImId(){
        return read(IMID);
    }
    public static void saveResVersion(String version){
        save(RESVERSION, version);
    }
    public static String readResVersion(){
        return read(RESVERSION, Constant.resVerison);
    }

    public static void saveCenter(String url){
        save(ADD, url);
    }

    public static String readCenter(){
        String url = read(ADD);
        if(url != null && !url.equals("")){
            return url;
        }else {
            return Constant.center;
        }
    }

    public static void saveIndex4(String url){
        save(INDEX4, url);
    }

    public static String readIndex4(){
        String url = read(INDEX4);
        if(url != null && !url.equals("")){
            return url;
        }else {
            return Constant.index4;
        }
    }
    public static void saveIndex3(String url){
        save(INDEX3, url);
    }

    public static String readIndex3(){
        String url = read(INDEX3);
        if(url != null && !url.equals("")){
            return url;
        }else {
            return Constant.index3;
        }
    }

    public static void saveIndex2(String url){
        save(INDEX2, url);
    }

    public static String readIndex2(){
        String url = read(INDEX2);
        if(url != null && !url.equals("")){
            return url;
        }else {
            return Constant.index2;
        }
    }

    public static void saveIndex1(String url){
        save(INDEX1, url);
    }

    public static String readIndex1(){
        String url = read(INDEX1);
        if(url != null && !url.equals("")){
            return url;
        }else {
            return Constant.index1;
        }
    }

    public static void saveLoginId(long loginId){
        save(LOGINID, String.valueOf(loginId));
    }

    public static long readLoginId(){
        String loginId = read(LOGINID);
        if(loginId != null && !loginId.equals("")){
            return Long.valueOf(loginId);
        }else {
            return 0;
        }
    }
    public static void save(String key, String value){
        SharedPreferences sp = WXApplication.getInstance().getSharedPreferences("PRIVATE", MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String read(String key, String defaultStr){
        SharedPreferences sp = WXApplication.getInstance().getSharedPreferences("PRIVATE", MODE_PRIVATE);
        return sp.getString(key, defaultStr);
    }
    public static String read(String key){

        return read(key, "");
    }
}
