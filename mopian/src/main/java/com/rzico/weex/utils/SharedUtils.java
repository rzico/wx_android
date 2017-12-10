package com.rzico.weex.utils;

import android.content.SharedPreferences;

import com.rzico.weex.WXApplication;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Jinlesoft on 2017/9/20.
 */

public class SharedUtils {


    public static final String LOGINID = "weex_loginid";
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

    public static String read(String key){
        SharedPreferences sp = WXApplication.getInstance().getSharedPreferences("PRIVATE", MODE_PRIVATE);
        return sp.getString(key, "");
    }
}
