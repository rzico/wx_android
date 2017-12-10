package com.rzico.weex.net;





import com.rzico.weex.json.BaseResponse;
import com.rzico.weex.json.ResponseParser;

import org.xutils.common.Callback;

/**
 * Created by linhuasen on 15/12/6.
 */
public abstract class xResponse implements Callback.CommonCallback<String>{

    @Override
    public void onSuccess(String result) {
        BaseResponse response = ResponseParser.toBaseResponse(result);
        if(response != null){
            if(response.getMessage().getType().equals("error")){
                if(response.getMessage().getContent().equals("session.invaild")){
                    invaild();
                }else{
                    success(result);
                }
            }else{
                success(result);
            }
        }else{
            success(result);
        }
    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        error(ex,isOnCallback);
    }

    @Override
    public void onCancelled(CancelledException cex) {

    }

    @Override
    public void onFinished() {
        finished();
    }


    public abstract void success(String result);

    public abstract void error(Throwable ex, boolean isOnCallback);

    /**
     * session 超时
     */
    public abstract void invaild();

    public abstract void finished();
}
