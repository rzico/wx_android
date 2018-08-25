package com.rzico.weex.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rzico.weex.R;
import com.rzico.weex.db.DbUtils;
import com.rzico.weex.module.AlbumModule;
import com.rzico.weex.module.PayModule;
import com.rzico.weex.module.WXEventModule;
import com.rzico.weex.net.session.SessionOutManager;
import com.rzico.weex.utils.PathUtils;
import com.rzico.weex.utils.weex.constants.Constants;
import com.taobao.weex.IWXRenderListener;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.common.WXRenderStrategy;
import com.ums.AppHelper;

import java.util.HashMap;
import java.util.Map;

import static com.rzico.weex.Constant.isLoginAcitivity;
import static com.rzico.weex.utils.photo.PhotoHandle.REQUEST_PHOTOHANDLER;
import static com.yalantis.ucrop.UCrop.REQUEST_CROP;

/**
 * Created by Jinlesoft on 2018/1/30.
 */

public class RouterActivity extends AbsWeexActivity{

    private ProgressBar mProgressBar;
    private TextView mTipView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxpage);

        setFitSystemWindow(false);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }else
            setStatusBarFullTransparent();
        mContainer = (ViewGroup) findViewById(R.id.container);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mTipView = (TextView) findViewById(R.id.index_tip);

        Uri uri = getIntent().getData();
        Bundle bundle = getIntent().getExtras();
        if (uri != null) {
            mUri = uri;
        }
        if(getIntent().getStringExtra("isLocal") != null){
            isLocalUrl = getIntent().getStringExtra("isLocal").equals("true");
        }

        if (bundle != null) {
            String bundleUrl = bundle.getString(Constants.PARAM_BUNDLE_URL);
            if (!TextUtils.isEmpty(bundleUrl)) {
                mUri = Uri.parse(bundleUrl);
            }
        }

        if (mUri == null) {
            Toast.makeText(this, "the uri is empty!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        loadUrl(getUrl(mUri));
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        Uri uri = intent.getData();
//        Bundle bundle = getIntent().getExtras();
//        if (uri != null) {
//            mUri = uri;
//        }
//        if(getIntent().getStringExtra("isLocal") != null){
//            isLocalUrl = getIntent().getStringExtra("isLocal").equals("true");
//        }
//
//        if (bundle != null) {
//            String bundleUrl = bundle.getString(Constants.PARAM_BUNDLE_URL);
//            if (!TextUtils.isEmpty(bundleUrl)) {
//                mUri = Uri.parse(bundleUrl);
//            }
//        }
//
//        if (mUri == null) {
//            Toast.makeText(this, "the uri is empty!", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
////        renderPage();
//        createWeexInstance();
//        setUrl(getUrl(mUri));
//        renderPage();
    }

    private String getUrl(Uri uri) {
        String url = uri.toString();
        String scheme = uri.getScheme();
        if (uri.isHierarchical()) {
            if (TextUtils.equals(scheme, "http") || TextUtils.equals(scheme, "https")) {
                String weexTpl = uri.getQueryParameter(Constants.WEEX_TPL_KEY);
                if (!TextUtils.isEmpty(weexTpl)) {
                    url = weexTpl;
                }
            }
        }
        return url;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_PHOTOHANDLER || requestCode == REQUEST_CROP) {//而代表请求裁剪
            AlbumModule.get().onActivityResult(requestCode, resultCode, data);
        }else if (AppHelper.TRANS_REQUEST_CODE == requestCode){
            PayModule.get().onActivityResult(requestCode, resultCode, data);
        }else {
            WXEventModule.get().onActivityResult(requestCode, resultCode, data);
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            setResult(Activity.RESULT_CANCELED);
//            finish();
//            return false;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

}
