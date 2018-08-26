package com.rzico.weex.component.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.rzico.weex.Constant;
import com.rzico.weex.R;
import com.rzico.weex.model.event.MessageBus;
import com.rzico.weex.utils.MD5Utils;
import com.rzico.weex.utils.PhoneUtil;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.http.WXHttpUtil;
import com.taobao.weex.ui.view.IWebView;
import com.taobao.weex.utils.WXLogUtils;

import org.greenrobot.eventbus.EventBus;
import org.xutils.http.cookie.DbCookieStore;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.finalteam.rxgalleryfinal.utils.DensityUtil;

import static com.rzico.weex.Constant.SERVER;
import static com.rzico.weex.Constant.key;

/**
 * Created by Jinlesoft on 2017/10/11.
 */

public class MYWXWebView implements IWebView {
    private Context mContext;
    private WebView mWebView;
//    private ProgressBar mProgressBar;
    private ProgressView mProgressBar;
    private boolean mShowLoading = true;

    private OnErrorListener mOnErrorListener;
    private OnPageListener mOnPageListener;

    private Map extraHeaders = new HashMap();
    public MYWXWebView(Context context) {
        mContext = context;
        extraHeaders.put("Referer",SERVER);
        extraHeaders.put(WXHttpUtil.KEY_USER_AGENT, WXHttpUtil.assembleUserAgent(mContext, WXEnvironment.getConfig())+ "weex");
        String uid= PhoneUtil.getDeviceId(mContext);
        String app= Constant.app;
        String tsp = String.valueOf(System.currentTimeMillis());

        String tkn = MD5Utils.getMD5Str(uid + app + tsp + key);

        extraHeaders.put("x-uid", uid);//设备号
        extraHeaders.put("x-app", app);//包名
        extraHeaders.put("x-tsp", tsp);//时间戳
        extraHeaders.put("x-tkn", tkn);//令牌

    }

    /**
     * 给WebView同步Cookie
     *
     * @param context 上下文
     * @param url     可以使用[domain][host]
     */
    private void syncCookie(Context context, String url) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();// 移除旧的[可以省略]
        DbCookieStore instance = DbCookieStore.INSTANCE;
        List<HttpCookie> cookies = instance.getCookies();// 获取Cookie[可以是其他的方式获取]
        for (int i = 0; i < cookies.size(); i++) {
            HttpCookie cookie = cookies.get(i);
            String value = cookie.getName() + "=" + cookie.getValue();
            cookieManager.setCookie(url, value);
        }
        CookieSyncManager.getInstance().sync();// To get instant sync instead of waiting for the timer to trigger, the host can call this.
    }

    @Override
    public View getView() {
        FrameLayout root = new FrameLayout(mContext);
        root.setBackgroundColor(Color.WHITE);

        if(mWebView == null){
            mWebView = new WebView(mContext);//mContext.getApplicationContext();
        }
        FrameLayout.LayoutParams wvLayoutParams =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);
        wvLayoutParams.gravity = Gravity.CENTER;



        mWebView.setLayoutParams(wvLayoutParams);
        root.addView(mWebView);
        initWebView(mWebView);

//        mProgressBar = new ProgressBar(mContext);
//        showProgressBar(false);
//        FrameLayout.LayoutParams pLayoutParams =
//                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
//                        FrameLayout.LayoutParams.WRAP_CONTENT);
//        mProgressBar.setLayoutParams(pLayoutParams);
//        pLayoutParams.gravity = Gravity.CENTER;
//        root.addView(mProgressBar);
        mProgressBar = new ProgressView(mContext);
        mProgressBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(mContext, 3)));
        mProgressBar.setColor(0xff62b900);
        mProgressBar.setProgress(10);
        //把进度条加到Webview中
        root.addView(mProgressBar);

        return root;
    }

    @Override
    public void destroy() {
        if (getWebView() != null) {
            getWebView().removeAllViews();
            getWebView().destroy();
            mWebView = null;
        }
    }

    @Override
    public void loadUrl(String url) {
//        Toast.makeText(WXApplication.getActivity(), "初始化耗时："+ (System.currentTimeMillis() - startLoad), Toast.LENGTH_SHORT).show();
//        startLoad = System.currentTimeMillis();
        if(getWebView() == null)
            return;
        syncCookie(mContext, url);
        getWebView().loadUrl(url, extraHeaders);
    }

    @Override
    public void reload() {
        if(getWebView() == null)
            return;
        getWebView().reload();
    }

    @Override
    public void goBack() {
        if(getWebView() == null)
            return;
        getWebView().goBack();
    }

    @Override
    public void goForward() {
        if(getWebView() == null)
            return;
        getWebView().goForward();
    }

    /*@Override
    public void setVisibility(int visibility) {
        if (mRootView != null) {
            mRootView.setVisibility(visibility);
        }
    }*/

    @Override
    public void setShowLoading(boolean shown) {
        mShowLoading = shown;
    }

    @Override
    public void setOnErrorListener(OnErrorListener listener) {
        mOnErrorListener = listener;
    }

    @Override
    public void setOnPageListener(OnPageListener listener) {
        mOnPageListener = listener;
    }

    private void showProgressBar(boolean shown) {
        if (mShowLoading) {
            mProgressBar.setVisibility(shown ? View.VISIBLE : View.GONE);
        }
    }

    private void showWebView(boolean shown) {
        mWebView.setVisibility(shown ? View.VISIBLE : View.INVISIBLE);
    }
    private long startLoad = 0;

    public @Nullable
    WebView getWebView() {
        //TODO: remove this, duplicate with getView semantically.
        return mWebView;
    }

    private void initWebView(WebView wv) {
//        startLoad = System.currentTimeMillis();
        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setDomStorageEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setBlockNetworkImage(true);

        if (Build.VERSION.SDK_INT >= 21) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // 修改ua使得web端正确判断
        String ua = settings.getUserAgentString();
        settings.setUserAgentString(ua+"; weex");

        wv.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                boolean canHerf = true;
                // 如下方案可在非微信内部WebView的H5页面中调出微信支付
                if(url.startsWith("weixin://wap/pay?")) {
                    view.loadUrl(url, extraHeaders);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    mContext.startActivity(intent);
                    canHerf = false;
                }else if(url.startsWith(mContext.getResources().getString(R.string.href_home))){//本app的伪协议、跳转

//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse(url));
//                    mContext.startActivity(intent);已经在程序里了 不需要跳转
                    //处理伪协议
                        String allUrl = url;
                        String deleteScheme = allUrl.replace(mContext.getResources().getString(R.string.href_home) + "://","");
                        if(!deleteScheme.equals("") && deleteScheme.length() > 0 && deleteScheme.contains("?") && deleteScheme.contains("=")){
                            String[] one = deleteScheme.split("\\?");//问号需要加双传意符
                            String[] two = one[1].split("=");

                            if(two !=null && two.length == 2){
                                if(one!=null && one.length == 2){
                                    if(one[0].equals("article")){
                                        // 文章 file://view/article/preview.js?articleId=%@&publish=true
                                        url = "file://view/articlePreview/articlePreview.js?articleId=" + two[1] + "&publish=true";
                                    }else if(one[0].equals("topic")){
                                        // 专栏 file://view/topic/index.js?id=%@
                                        url = "file://view/perHome/perHome.js?id=" + two[1];
                                    }
                                    else if(one[0].equals("buyGood")){
                                        //一键购
                                        Map<String, Object> params = new HashMap<>();
                                        params.put("goodId", two[1]);

                                        EventBus.getDefault().post(new MessageBus(MessageBus.Type.GLOBAL, "buyGood", params));
                                    }else if(one[0].equals("saveImage")){

                                        //文件下载
                                        Map<String, Object> params = new HashMap<>();
                                        params.put("url", two[1]);

                                        EventBus.getDefault().post(new MessageBus(MessageBus.Type.GLOBAL, "saveImage", params));
                                    }
                                }
                            }

                            if(!url.equals("")){
                                Intent openScheme = new Intent();
                                String key = String.valueOf(System.currentTimeMillis());
                                if (url.startsWith("file://")) {
                                    openScheme.putExtra("isLocal", "true");
                                    openScheme.putExtra("key", key);
                                    openScheme.addCategory(Constant.WEEX_CATEGORY);
                                    openScheme.setData(Uri.parse(url));
                                    mContext.startActivity(openScheme);
                                    canHerf = true;
                                }
                            }

                    }
                }else {
//                    view.loadUrl(url, extraHeaders);
                }
                WXLogUtils.v("tag", "onPageOverride " + url);
                return canHerf;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                WXLogUtils.v("tag", "onPageStarted " + url);
                if (mOnPageListener != null) {
                    mOnPageListener.onPageStart(url);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                WXLogUtils.v("tag", "onPageFinished " + url);

                view.getSettings().setBlockNetworkImage(false);
//                Toast.makeText(view.getContext(), "耗时："+ (System.currentTimeMillis() - startLoad), Toast.LENGTH_SHORT).show();
                if (mOnPageListener != null) {
                    mOnPageListener.onPageFinish(url, view.canGoBack(), view.canGoForward());
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (mOnErrorListener != null) {
                    //mOnErrorListener.onError("error", "page error code:" + error.getErrorCode() + ", desc:" + error.getDescription() + ", url:" + request.getUrl());
                    mOnErrorListener.onError("error", "page error");
                }
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError("error", "http error");
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError("error", "ssl error");
                }
            }

        });
        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                showWebView(newProgress == 100);
//                showProgressBar(newProgress != 100);
                if (newProgress == 100) {
                    //加载完毕进度条消失
                    mProgressBar.setVisibility(View.GONE);

//                    view.getSettings().setBlockNetworkImage(false);
                } else {
                    //更新进度
                    mProgressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);

                WXLogUtils.v("tag", "onPageProgressChanged " + newProgress);
            }

            @Override
            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                Log.d("webview", message + " -- From line " + lineNumber + " of " + sourceID);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (mOnPageListener != null) {
                    mOnPageListener.onReceivedTitle(view.getTitle());
                }
            }

        });
    }

}
