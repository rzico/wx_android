package com.rzico.weex.adapter;

import android.widget.Toast;

import com.rzico.weex.WXApplication;
import com.rzico.weex.utils.Utils;
import com.taobao.weex.adapter.IWXJSExceptionAdapter;
import com.taobao.weex.common.WXJSExceptionInfo;

/**
 * Created by Jinlesoft on 2017/9/25.
 */

public class WeexJSExceptionAdapter implements IWXJSExceptionAdapter {
    @Override
    public void onJSException(WXJSExceptionInfo exception) {
        if(Utils.isApkDebugable(WXApplication.getActivity())){
            Toast.makeText(WXApplication.getActivity(), exception.getException().toString(), Toast.LENGTH_LONG).show();
        }
    }
}
