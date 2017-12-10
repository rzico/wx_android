package com.rzico.weex.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.rzico.weex.Constant;
import com.rzico.weex.R;
import com.rzico.weex.module.AlbumModule;
import com.rzico.weex.utils.BarTextColorUtils;
import com.rzico.weex.utils.PathUtils;
import com.taobao.weex.IWXRenderListener;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.common.WXRenderStrategy;

import java.util.HashMap;
import java.util.Map;

import static com.rzico.weex.utils.photo.PhotoHandle.REQUEST_PHOTOHANDLER;
import static com.yalantis.ucrop.UCrop.REQUEST_CROP;

public class EditActivity extends BaseActivity implements IWXRenderListener {

    WXSDKInstance mWXSDKInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarTextColorUtils.StatusBarLightMode(this, 0);
        setContentView(R.layout.activity_edit);
//        //新页面接收数据
//        Bundle bundle = this.getIntent().getExtras();
//        //接收name值
//        String url = bundle.getString("url");
//        if(url == null) finish();
        mWXSDKInstance = new WXSDKInstance(this);
        mWXSDKInstance.registerRenderListener(this);
        /**
         * WXSample 可以替换成自定义的字符串，针对埋点有效。
         * template 是.we transform 后的 js文件。
         * option 可以为空，或者通过option传入 js需要的参数。例如bundle js的地址等。
         * jsonInitData 可以为空。
         */
        Map<String,Object> options=new HashMap<>();
        options.put(WXSDKInstance.BUNDLE_URL,"file://" + Constant.center);
        mWXSDKInstance.render("edit", PathUtils.loadLocal(Constant.center), options, null, WXRenderStrategy.APPEND_ASYNC);
//      mWXSDKInstance.renderByUrl("edit", "http://192.168.1.107:8081/editor.weex.js?hot-reload_controller=1&_wx_tpl=http://192.168.1.107:8081/editor.weex.js", null, null, WXRenderStrategy.APPEND_ASYNC);

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
    public void onResume() {
        super.onResume();
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
        if(mWXSDKInstance!=null){
            mWXSDKInstance.onActivityDestroy();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_PHOTOHANDLER) {//而代表请求裁剪
            AlbumModule.get().onActivityResult(requestCode, resultCode, data);
        }
    }
}
