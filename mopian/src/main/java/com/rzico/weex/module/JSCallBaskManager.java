package com.rzico.weex.module;

import com.taobao.weex.bridge.JSCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jinlesoft on 2017/9/21.
 */

public class JSCallBaskManager {
    private static Map<String, JSCallback> jsCallbacks = new HashMap<>();
    public static JSCallback closeUrlCallback;

    /**
     * 添加jscallbacks
     * @param key
     * @param callback
     */
    public  static void put(String key, JSCallback callback){
        jsCallbacks.put(key, callback);
    }

    /**
     * 获取回调并且回调
     * @param key
     * @return
     */
    public  static JSCallback get(String key){
        return jsCallbacks.get(key);
    }

    /**
     * 移除
     * @param key
     */
    public static void remove(String key){
        jsCallbacks.remove(key);
    }
}
