package com.rzico.weex.adapter;

import com.rzico.weex.WXApplication;
import com.rzico.weex.activity.BaseActivity;
import com.rzico.weex.model.info.Message;
import com.rzico.weex.net.HttpRequest;
import com.rzico.weex.net.XRequest;
import com.rzico.weex.net.xResponse;
import com.rzico.weex.utils.PhoneUtil;
import com.taobao.weex.adapter.DefaultWXHttpAdapter;
import com.taobao.weex.adapter.IWXHttpAdapter;
import com.taobao.weex.common.WXRequest;
import com.taobao.weex.common.WXResponse;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jinlesoft on 2017/9/8.
 */

public class WeexHttpAdapter  implements IWXHttpAdapter {

    @Override
    public void sendRequest(WXRequest request, final OnHttpListener listener) {
        String method = "";
        if(request.method == null || request.method.equals("")){
            method = "GET";
        }else{
            method = request.method;
        }
        if(request.url.startsWith("http")){
            DefaultWXHttpAdapter defaultWXHttpAdapter = new DefaultWXHttpAdapter();
            defaultWXHttpAdapter.sendRequest(request, listener);
        }else{
            new XRequest(WXApplication.getActivity(), request.url, method.toUpperCase(),bindMap(request.paramMap, request.body))
                    .setOnRequestListener(new HttpRequest.OnRequestListener() {
                        @Override
                        public void onSuccess(BaseActivity activity, String result, String type) {
                            WXResponse wxResponse = new WXResponse();
                            if(type.equals("success")){
                                wxResponse.statusCode = "200";
//                            }else if(){
//                                wxResponse.statusCode = "304";
                            }else {//不是以上两个 就是 error type传的就是错误信息
                                wxResponse.statusCode = "304";
                                wxResponse.errorMsg   = type;
                            }
                            wxResponse.originalData = result.getBytes();
                            listener.onHttpFinish(wxResponse);
                        }
                        @Override
                        public void onFail(BaseActivity activity, String cacheData, int code) {
                            WXResponse wxResponse = new WXResponse();
                            if( code==2 && cacheData.equals("")){// 2 为post 请求失败
                                wxResponse.statusCode = "404";
                            }else{
                                wxResponse.statusCode = "304";
                            }
                            wxResponse.errorMsg = "网络不稳定";
                            wxResponse.originalData = cacheData.getBytes();
                            listener.onHttpFinish(wxResponse);
                        }
                    }).execute();
        }
    }


    public Map<String , Object> bindMap(Map<String , String> map, String body){
        Map<String , Object> data = new HashMap<>();
        if(map != null){
            data.putAll(map);
        }
        if(body!=null && !body.equals("")){
            data.put("body", body);
        }
        return data;
    }

}
