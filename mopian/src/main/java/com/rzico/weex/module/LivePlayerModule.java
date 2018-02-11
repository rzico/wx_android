package com.rzico.weex.module;

import android.content.Intent;

import com.rzico.weex.activity.LivePlayerActivity;
import com.rzico.weex.model.LivePlayerBean;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import java.io.Serializable;

/**
 * Created by Jinlesoft on 2018/2/1.
 */

public class LivePlayerModule extends WXModule {


    @JSMethod
    public void loadUrl(String url, String video, String method, JSCallback callback){
        LivePlayerBean livePlayerBean = new LivePlayerBean();
        livePlayerBean.setUrl(url);
        livePlayerBean.setVideo(video);
        livePlayerBean.setMethod(method);
        String key = System.currentTimeMillis() + "";
        JSCallBaskManager.put(key, callback);

        Intent intent = new Intent(mWXSDKInstance.getContext(), LivePlayerActivity.class);
        intent.putExtra("livePlayerParam",  livePlayerBean);
        intent.putExtra("key", key);
        mWXSDKInstance.getContext().startActivity(intent);
    }

}
