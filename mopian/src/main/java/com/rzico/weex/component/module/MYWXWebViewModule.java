package com.rzico.weex.component.module;

import android.Manifest;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.rzico.weex.WXApplication;
import com.rzico.weex.activity.BaseActivity;
import com.rzico.weex.component.MYWXWeb;
import com.rzico.weex.db.XDB;
import com.rzico.weex.db.bean.Redis;
import com.rzico.weex.model.info.Message;
import com.taobao.weex.WXSDKManager;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXWeb;
import com.taobao.weex.ui.module.WXWebViewModule;

import org.xutils.ex.DbException;
import org.xutils.x;

/**
 * Created by Jinlesoft on 2017/10/11.
 */

public class MYWXWebViewModule extends WXModule {

    private enum Action {
        reload,
        goBack,
        goForward
    }
    public BaseActivity getActivity(){
        return (BaseActivity) mWXSDKInstance.getContext();
    }

    @JSMethod(uiThread = true)
    public void goBack(String ref) {
        action(MYWXWebViewModule.Action.goBack, ref);
    }

    @JSMethod(uiThread = true)
    public void goForward(String ref) {
        action(MYWXWebViewModule.Action.goForward, ref);
    }

    @JSMethod(uiThread = true)
    public void reload(String ref) {
        action(MYWXWebViewModule.Action.reload, ref);
    }
    @JSMethod
    public void getLongImage(final String ref, final JSCallback callback){
        Dexter.withActivity(getActivity()).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new com.karumi.dexter.listener.single.PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        //初始化数据库
                        WXComponent webComponent =
                                WXSDKManager.getInstance()
                                        .getWXRenderManager()
                                        .getWXComponent(mWXSDKInstance.getInstanceId(), ref);
                        if(webComponent instanceof MYWXWeb) {
                            ((MYWXWeb) webComponent).getLongImage(callback);
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        getActivity().showDeniedDialog("需要存储权限");
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
                                                                   PermissionToken token) {
                        //用户不允许权限，向用户解释权限左右
                        token.continuePermissionRequest();

                        Message message = new Message();
                        message.setType("error");
                        message.setContent("保存失败");
                        message.setData("");
                        callback.invoke(message);
                    }
                }).check();

    }

    private void action(MYWXWebViewModule.Action action, String ref) {

        WXComponent webComponent =
                WXSDKManager.getInstance()
                        .getWXRenderManager()
                        .getWXComponent(mWXSDKInstance.getInstanceId(), ref);
        if(webComponent instanceof MYWXWeb) {
            ((MYWXWeb) webComponent).setAction(action.name());
        }
    }

}
