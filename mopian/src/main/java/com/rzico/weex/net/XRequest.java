package com.rzico.weex.net;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.rzico.weex.Constant;
import com.rzico.weex.WXApplication;
import com.rzico.weex.activity.BaseActivity;
import com.rzico.weex.db.DbUtils;
import com.rzico.weex.db.bean.Redis;
import com.rzico.weex.model.RequestBean;
import com.rzico.weex.net.HttpRequest;

import org.xutils.ex.DbException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by GuanYuCai on 16/3/9.
 */
public class XRequest {

    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String FILE = "FILE";
    public static final String HTTPCACHE = "httpCache";
    Context c;
    BaseActivity activity;
    AppCompatActivity compatActivity;
    String url;
    String cacheData = "";
    Map<String, Object> params;
    String body;
    List<RequestBean> requestBean;
    HttpRequest.OnRequestListener requestListener;
    String method;
    String a,b,cc,d,m;
    boolean backend = false;
    public String encodeUrl(String url) {
        if (url.startsWith("weex")) {
            return url.replaceFirst("weex://","http://");
        } else {
            return Constant.helperUrl + url;
        }
    }
    public XRequest(BaseActivity activity, String url) {
        this.activity = activity;
        this.url = encodeUrl(url);
        this.method = GET;
        this.params = null;
    }
    public XRequest(BaseActivity activity,String host, String url) {
        this.activity = activity;
        this.url = host + url;
        this.method = GET;
        this.params = null;
    }
    public XRequest(Context c, String url) {
        this.c = c;
        this.url = encodeUrl(url);
        this.method = GET;
        this.params = null;
    }

    public XRequest(BaseActivity activity, String url, String method, List<RequestBean> requestBean) {
        this.activity = activity;
        this.url = encodeUrl(url);
        this.method = method;
        this.requestBean = requestBean;
    }


    public XRequest(BaseActivity activity, String url, String method, Map<String, Object> params) {
        this.activity = activity;
        this.url = encodeUrl(url);
        this.method = method;
        this.params = params;
    }
    public XRequest(BaseActivity activity, String url, String method, Map<String, Object> params, String body) {
        this.activity = activity;
        this.url = encodeUrl(url);
        this.method = method;
        this.params = params;
        this.body   = body;
    }
    public XRequest(Context c, String url, String method, Map<String, Object> params) {
        this.c = c;
        this.url = encodeUrl(url);
        this.method = method;
        this.params = params;
    }
    public XRequest(AppCompatActivity activity, String url) {
        this.compatActivity = activity;
        this.url = encodeUrl(url);
        this.method = GET;
        this.params = null;
    }
    public XRequest(AppCompatActivity activity, String url, String method, List<RequestBean> requestBean) {
        this.compatActivity = activity;
        this.url = encodeUrl(url);
        this.method = method;
        this.requestBean = requestBean;
    }


    public XRequest(AppCompatActivity activity, String url, String method, Map<String, Object> params) {
        this.compatActivity = activity;
        this.url = encodeUrl(url);
        this.method = method;
        this.params = params;
    }

    public void execute() {
        try {
            if(GET.equals(method)){
                Redis redis = DbUtils.find(HTTPCACHE, url);
                if(redis != null){
                    if(params == null){
                        params = new HashMap<>();
                    }
                    params.put("md5", redis.getKeyword());
//                    keyword 存的是md5
//                    task.requestListener.onSuccess(task.activity, redis.getValue(), true);
                    cacheData = redis.getValue();
                }else{
                    cacheData = "";
                }
            }
        } catch (DbException e) {
            cacheData = "";
            e.printStackTrace();
        }
        HttpRequest.getInstance().execute(this);
    }

    public XRequest setOnRequestListener(HttpRequest.OnRequestListener l) {
        this.requestListener = l;
        return this;
    }

    public XRequest setBackstage(boolean backend) {
        this.backend = backend;
        return this;
    }
}
