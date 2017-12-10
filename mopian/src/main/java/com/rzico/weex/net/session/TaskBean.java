package com.rzico.weex.net.session;

import android.app.Activity;

import com.rzico.weex.activity.BaseActivity;
import com.rzico.weex.net.HttpRequest;

import java.util.Map;

/**
 * Created by Jinlesoft on 2017/9/19.
 * 用来保存上一次请求会话实效 的请求以及回调函数
 */

public class TaskBean {

    private String url;
    private BaseActivity activity;
    private Map<String, Object> params;
    private String method;
    private HttpRequest.OnRequestListener listener;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public BaseActivity getActivity() {
        return activity;
    }

    public void setActivity(BaseActivity activity) {
        this.activity = activity;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public HttpRequest.OnRequestListener getListener() {
        return listener;
    }

    public void setListener(HttpRequest.OnRequestListener listener) {
        this.listener = listener;
    }
}
