package com.rzico.weex.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.rzico.weex.R;
import com.rzico.weex.db.DbUtils;
import com.rzico.weex.db.notidmanager.NotIdManager;
import com.rzico.weex.module.WXEventModule;
import com.rzico.weex.net.session.SessionOutManager;
import com.rzico.weex.net.session.TaskBean;
import com.rzico.weex.utils.PathUtils;
import com.rzico.weex.utils.Utils;
import com.taobao.weex.IWXRenderListener;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.common.WXRenderStrategy;

import java.util.HashMap;
import java.util.Map;

import static com.rzico.weex.Constant.isLoginAcitivity;

public class LoginActivity extends BaseActivity implements IWXRenderListener {

    WXSDKInstance mWXSDKInstance;

    public final static int LOGINCODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_weex);

        mWXSDKInstance = new WXSDKInstance(this);
        mWXSDKInstance.registerRenderListener(this);

        Map<String,Object> options=new HashMap<>();
        options.put(WXSDKInstance.BUNDLE_URL,"file://"+ "view/index.js");
        String data = PathUtils.loadLocal("view/index.js", LoginActivity.this);
        mWXSDKInstance.render("login", PathUtils.loadLocal("view/index.js", LoginActivity.this), null, null, WXRenderStrategy.APPEND_ASYNC);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onViewCreated(WXSDKInstance instance, View view) {
        setContentView(view);
    }

    @Override
    public void onRenderSuccess(WXSDKInstance instance, int width, int height) {

    }

    @Override
    public void onRefreshSuccess(WXSDKInstance instance, int width, int height) {

    }

    @Override
    public void onException(WXSDKInstance instance, String errCode, String msg) {

    }



    @Override
    protected void onStop() {
        super.onStop();
        if(mWXSDKInstance!=null){
            mWXSDKInstance.onActivityStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isLoginAcitivity = false;//这里是请求网络判断
        SessionOutManager.clear();
        DbUtils.reDoSql();
        if(mWXSDKInstance!=null){
            mWXSDKInstance.onActivityDestroy();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        WXEventModule.get().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(Activity.RESULT_CANCELED);
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
