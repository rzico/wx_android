package com.rzico.weex.module;

import android.Manifest;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.google.zxing.activity.CaptureActivity;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.rzico.weex.Constant;
import com.rzico.weex.R;
import com.rzico.weex.WXApplication;
import com.rzico.weex.activity.BaseActivity;
import com.rzico.weex.activity.RichEditorAcitivity;
import com.rzico.weex.activity.RouterActivity;
import com.rzico.weex.activity.chat.ChatActivity;
import com.rzico.weex.db.DbUtils;
import com.rzico.weex.db.notidmanager.DbCacheBean;
import com.rzico.weex.model.event.MessageBus;
import com.rzico.weex.model.info.CacheSize;
import com.rzico.weex.model.info.Contact;
import com.rzico.weex.model.info.IMMessage;
import com.rzico.weex.model.info.Location;
import com.rzico.weex.model.info.Message;
import com.rzico.weex.model.info.REABean;
import com.rzico.weex.model.info.WxConfig;
import com.rzico.weex.net.HttpRequest;
import com.rzico.weex.net.XRequest;
import com.rzico.weex.oos.OssService;
import com.rzico.weex.oos.PauseableUploadTask;
import com.rzico.weex.oos.STSGetter;
import com.rzico.weex.utils.ContactUtils;
import com.rzico.weex.utils.DateUtils;
import com.rzico.weex.utils.DeleteFileUtil;
import com.rzico.weex.utils.PathUtils;
import com.rzico.weex.utils.RSAUtils;
import com.rzico.weex.utils.SharedUtils;
import com.rzico.weex.utils.Utils;
import com.rzico.weex.utils.chat.MessageFactory;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;
import com.taobao.weex.common.WXModuleAnno;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.message.TIMConversationExt;
import com.tencent.imsdk.ext.message.TIMManagerExt;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yixiang.mopian.constant.AllConstant;
import com.rzico.assistant.wxapi.WXEntryActivity;

import net.bither.util.NativeUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;


public class WXEventModule extends WXModule {


    @JSMethod
    public void logout(final JSCallback callback){
        new XRequest(getActivity(), "/weex/login/logout.jhtml", XRequest.POST, new HashMap<String, Object>()).setOnRequestListener(new HttpRequest.OnRequestListener() {
            @Override
            public void onSuccess(BaseActivity activity, String result, String type) {
                if(!TIMManager.getInstance().getLoginUser().equals("")){
                    TIMManager.getInstance().logout(new TIMCallBack() {
                        @Override
                        public void onError(int code, String desc) {
                            Message message = new Message().error(desc);
                            callback.invoke(message);
                        }
                        @Override
                        public void onSuccess() {
                            //登出成功
                            Constant.loginState = false;
                            Constant.userId = 0;
                            Constant.imUserId = "";
                            SharedUtils.saveLoginId(Constant.userId);
                            SharedUtils.saveImId(Constant.imUserId);
                            Message message = new Message().success("登出成功");
                            callback.invoke(message);
                            EventBus.getDefault().post(new MessageBus(MessageBus.Type.LOGOUT));
                        }
                    });
                }else{
                    //登出成功
                    Constant.loginState = false;
                    Constant.userId = 0;
                    Constant.imUserId = "";
                    SharedUtils.saveLoginId(Constant.userId);
                    SharedUtils.saveImId(Constant.imUserId);
                    Message message = new Message().success("登出成功");
                    callback.invoke(message);
                    EventBus.getDefault().post(new MessageBus(MessageBus.Type.LOGOUT));
                }

            }

            @Override
            public void onFail(BaseActivity activity, String cacheData, int code) {
                Message message = new Message().error();
                callback.invoke(message);
            }
        }).execute();

    }

    @JSMethod
    public void closeURL() {
        closeURL(null);
    }

//    @JSMethod(uiThread = false)
//    public long getUserId(){
//        return Constant.userId;
//    }

    @JSMethod(uiThread = false)
    public long getUId(){
        return SharedUtils.readLoginId();
    }
    @JSMethod(uiThread = false)
    public String getUserId(){
        return SharedUtils.readImId();
    }
    /**
     * 关闭页面并且返回值 根据key区分
     *
     * @param data
     */
    @JSMethod
    public void closeURL(Message data) {
        String key = getActivity().getIntent().getStringExtra("key");
        if (data != null && JSCallBaskManager.get(key) != null) {
            JSCallBaskManager.get(key).invoke(data);
            JSCallBaskManager.remove(key);
        } else if (JSCallBaskManager.get(key) != null) {
            Message message = new Message();
            message.setType("error");
            message.setContent("error");
            message.setData("");
            JSCallBaskManager.get(key).invoke(message);
            JSCallBaskManager.remove(key);
        }
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
//        getActivity().overridePendingTransition(0, 0);
    }

    @JSMethod
    public void router(String url) {
        Intent intent = new Intent();
        Uri uri = Uri.parse(url);
        String scheme = uri.getScheme();
        if (TextUtils.equals("tel", scheme)) {

        } else if (TextUtils.equals("sms", scheme)) {

        } else if (TextUtils.equals("mailto", scheme)) {

        } else if (TextUtils.equals("http", scheme) ||
                TextUtils.equals("https",
                        scheme)) {
            intent.putExtra("isLocal", "false");
            intent.setClass(getActivity(), RouterActivity.class);
        } else if (TextUtils.equals("file", scheme)) {
            intent.putExtra("isLocal", "true");
            intent.setClass(getActivity(), RouterActivity.class);
        } else {
            intent.setClass(getActivity(), RouterActivity.class);
            uri = Uri.parse(new StringBuilder("http:").append(url).toString());
        }
        intent.setData(uri);
        mWXSDKInstance.getContext().startActivity(intent);
    }
    @JSMethod
    public void closeRouter(){
        getActivity().finish();
    }


    @JSMethod
    public void openURL(String url, JSCallback jsCallback) {
        try {
//            Toast.makeText(getContext(), "url:" + url , Toast.LENGTH_SHORT).show();
            //为了判断不触发两次
//            if(oldDate == 0){
//                oldDate = System.currentTimeMillis();
//            }else{
//                if(System.currentTimeMillis() - oldDate < 1000){
//                    oldDate = 0;
//                    return;
//                }
//            }
            String key = String.valueOf(System.currentTimeMillis());
            if (jsCallback != null) {//如果有传入回调的话
                JSCallBaskManager.put(key, jsCallback);
            }
            if (TextUtils.isEmpty(url)) {
                return;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(url);
            String scheme = uri.getScheme();
            if (TextUtils.equals("tel", scheme)) {

            } else if (TextUtils.equals("sms", scheme)) {

            } else if (TextUtils.equals("mailto", scheme)) {

            } else if (TextUtils.equals("http", scheme) ||
                    TextUtils.equals("https",
                            scheme)) {
                intent.putExtra("isLocal", "false");
                intent.putExtra("key", key);
                intent.addCategory(Constant.WEEX_CATEGORY);
            } else if (TextUtils.equals("file", scheme)) {
                intent.putExtra("isLocal", "true");
                intent.putExtra("key", key);
                intent.addCategory(Constant.WEEX_CATEGORY);
            } else {
                intent.addCategory(Constant.WEEX_CATEGORY);
                uri = Uri.parse(new StringBuilder("http:").append(url).toString());
            }
            intent.setData(uri);
            mWXSDKInstance.getContext().startActivity(intent);
        }finally {
        }
    }

    @WXModuleAnno(moduleMethod = true, runOnUIThread = true)
    public void openURL(String url) {
        openURL(url, null);
    }


    public Context getContext() {
        if(mWXSDKInstance == null){
            return WXApplication.getContext();
        }else{
            return mWXSDKInstance.getContext();
        }
    }

    public com.rzico.weex.activity.BaseActivity getActivity() {
        if(mWXSDKInstance == null){
            return WXApplication.getActivity();
        }else{
            return (com.rzico.weex.activity.BaseActivity) mWXSDKInstance.getContext();
        }
    }

    //    微信验证登录回调方法
    private RxWeiXinAuthFinalHolderListener rxWeiXinAuthFinalHolderListener = null;
    //    微信验证登录回调方法
    private RxWeiXinAuthFinalHolderListener rxScanfinalHolderListener = null;

    private static final class RxNativeFinalHolder {
        private static final WXEventModule RX_NATIVE_FINAL = new WXEventModule();
    }

    public static WXEventModule get() {
        return RxNativeFinalHolder.RX_NATIVE_FINAL;
    }

    public WXEventModule init(RxWeiXinAuthFinalHolderListener listener) {
        this.rxWeiXinAuthFinalHolderListener = listener;
        return this;
    }
    public WXEventModule initScan(RxWeiXinAuthFinalHolderListener listener) {
        this.rxScanfinalHolderListener = listener;
        return this;
    }

    /**
     * 打印文本
     *
     * @param data
     */
    @JSMethod
    public void toast(String data) {
        Toast.makeText(getContext(), data, Toast.LENGTH_SHORT).show();
    }

    /**
     * 获取系统信息
     *
     * @param callback
     */
    @JSMethod(uiThread = false)
    public Message wxConfig(JSCallback callback) {
        WxConfig wxConfig = new WxConfig();
        String color = Integer.toHexString(getContext().getResources().getColor(R.color.wxColor));
        if (color.length() > 6) {
            color = color.substring(color.length() - 6);
        }
        wxConfig.setColor("#" + color);
        Message message = new Message();
        message.setType("success");
        message.setContent("获取成功");
        message.setData(wxConfig);
//        callback.invoke(message);
        return message;
    }

    /**
     * 登录成功调用
     */

//    @JSMethod
//    public void activityResult(){
//        getActivity().setResult(Activity.RESULT_OK);
//        getActivity().finish();
//    }
//    /**
//     * 关闭页面调用
//     */
//    @JSMethod
//    public void activityCancel(){
//        getActivity().setResult(Activity.RESULT_CANCELED);
//        getActivity().finish();
//    }
    /**
     * 微信登录验证
     *
     * @param callback
     */

    @JSMethod
    public void wxAuth(final JSCallback callback) {

        final com.rzico.weex.activity.BaseActivity activity = getActivity();
        WXEventModule.get().init(new RxWeiXinAuthFinalHolderListener() {
            @Override
            public void userOk(String code) {
                Message message = new Message();
                message.setType("success");
                message.setContent("用户授权成功");
                message.setData(code);
                callback.invoke(message);
            }

            @Override
            public void userCancel() {
                Message message = new Message();
                message.setType("error");
                message.setContent("用户取消");
                callback.invoke(message);
//                activity.setResult(Activity.RESULT_CANCELED);
            }

            @Override
            public void authDenied() {
                Message message = new Message();
                message.setType("error");
                message.setContent("发送被拒绝");
                callback.invoke(message);
//                activity.setResult(Activity.RESULT_CANCELED);
            }
        }).sendWxAuth(activity);
    }

    /**
     * {"partnerid":"","prepayid":"","package":"","noncestr":"","timestamp":unsignedInt,"sign":""}
     * @param option
     * @param callback
     */
    @JSMethod
    public void wxAppPay(String option, JSCallback callback){

        sendWxAppPay(option, getActivity());
    }

    public void sendWxAppPay(String option, Activity activity){
        String partnerid = "";
        String prepayid = "";
        String packagea = "";
        String noncestr = "";
        String timestamp = "";
        String sign = "";
        try {
            option = URLDecoder.decode(option, "utf-8");
            com.alibaba.fastjson.JSONObject jsObj = JSON.parseObject(option);
            if (jsObj.containsKey("partnerid")) {
                partnerid = jsObj.getString("partnerid");
            }
            if (jsObj.containsKey("prepayid")) {
                prepayid = jsObj.getString("prepayid");

            }
            if (jsObj.containsKey("package")) {
                packagea = jsObj.getString("package");
            }
            if (jsObj.containsKey("noncestr")) {
                noncestr = jsObj.getString("noncestr");
            }
            if (jsObj.containsKey("timestamp")) {
                timestamp = jsObj.getString("timestamp");
            }
            if (jsObj.containsKey("sign")) {
                sign = jsObj.getString("sign");
            }

            IWXAPI mApi = WXAPIFactory.createWXAPI(activity, WXEntryActivity.WEIXIN_APP_ID, true);
            mApi.registerApp(WXEntryActivity.WEIXIN_APP_ID);
            if (mApi != null && mApi.isWXAppInstalled()) {
                PayReq request = new PayReq();
                request.appId = WXEntryActivity.WEIXIN_APP_ID;
                request.partnerId = partnerid;
                request.prepayId = prepayid;
                request.packageValue = packagea;
                request.nonceStr = noncestr;
                request.timeStamp = timestamp;
                request.sign = sign;
                mApi.sendReq(request);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    @JSMethod
    public void wxPay(String url, final JSCallback callback) {
        final Map map = new HashMap();
        map.put("Referer", Constant.SERVER);
        WebView webView = new WebView(getActivity());
        WebSettings mWebSettings = webView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        webView.loadUrl(url, map);
        webView.setWebViewClient(new WebViewClient() {
                                     @Override
                                     public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                         view.loadUrl(url, map);
                                         // 如下方案可在非微信内部WebView的H5页面中调出微信支付
                                         if (url.startsWith("weixin://wap/pay?")) {
                                             Intent intent = new Intent();
                                             intent.setAction(Intent.ACTION_VIEW);
                                             intent.setData(Uri.parse(url));
                                             getActivity().startActivity(intent);
                                             Message message = new Message();
                                             message.setType("success");
                                             callback.invoke(message);
                                         } else {
                                             Message message = new Message();
                                             message.setType("error");
                                             callback.invoke(message);
                                         }
                                         return true;
                                     }
                                 }

        );
    }

    /**
     * 发送验证
     *
     * @param activity
     */
    public void sendWxAuth(Activity activity) {
        IWXAPI mApi = WXAPIFactory.createWXAPI(activity, WXEntryActivity.WEIXIN_APP_ID, true);
        mApi.registerApp(WXEntryActivity.WEIXIN_APP_ID);
        if (mApi != null && mApi.isWXAppInstalled()) {
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_sdk_demo_test_neng";
            mApi.sendReq(req);

        } else
            Toast.makeText(activity, "用户未安装微信", Toast.LENGTH_SHORT).show();

    }

    /**
     * 改变标题拦颜色 ture 为 暗色
     *
     * @param isDark
     */
    @JSMethod
    public void changeWindowsBar(boolean isDark) {
//        BarTextColorUtils.StatusBarLightMode(getActivity(), isDark);
    }

    /*这里是老土的接受返回信息*/
    public void onWinXinAuthResult(BaseResp resp) {
        if (rxWeiXinAuthFinalHolderListener != null) {
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    //发送成功
                    try {
                        SendAuth.Resp sendResp = (SendAuth.Resp) resp;
                        if (sendResp != null) {
                            String code = sendResp.code;
                            rxWeiXinAuthFinalHolderListener.userOk(code);
                        }
                    } catch (Exception e) {
                    }
                    Log.i("leon", "发送成功");
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    rxWeiXinAuthFinalHolderListener.userCancel();
                    //发送取消
                    Log.i("leon", "发送取消");
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    rxWeiXinAuthFinalHolderListener.authDenied();
                    Log.i("leon", "发送被拒绝");
                    //发送被拒绝
                    break;
                default:
                    //发送返回
                    break;
            }
        }
    }

    /**
     * @param data
     * @param callback
     */
    @JSMethod
    public void encrypt(final String data, final JSCallback callback) {

        new XRequest(getActivity(), Constant.PUBLIC_KEY).setOnRequestListener(new HttpRequest.OnRequestListener() {
            @Override
            public void onSuccess(BaseActivity activity, String result, String type) {
                try {
                    REABean entity = new Gson().fromJson(result, REABean.class);
                    if (entity != null) {
                        PublicKey publicKey = RSAUtils.getPublicKey(Base64.decode(entity.getData().getModulus(), Base64.URL_SAFE | Base64.NO_WRAP), Base64.decode(entity.getData().getExponent(), Base64.URL_SAFE | Base64.NO_WRAP));
                        String afterencrypt = RSAUtils.encrypt(publicKey, data);
                        String safeBase64Str = afterencrypt.replace('+', '-');
                        safeBase64Str = safeBase64Str.replace('/', '_');
//            afterencrypt = afterencrypt.replaceAll("/")
//                        System.out.println("key加密后:" + afterencrypt);
                        Message message = new Message();
                        message.setType("success");
                        message.setContent("加密成功");
                        message.setData(safeBase64Str);
                        callback.invoke(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = new Message();
                    message.setType("error");
                    message.setContent("加密失败");
                    callback.invoke(message);
                }
            }

            @Override
            public void onFail(BaseActivity activity, String cacheData, int code) {
                Message message = new Message();
                message.setType("error");
                message.setContent("获取公钥失败");
                callback.invoke(message);
            }
        }).execute();

    }


    /**
     *  option{
     *      type,
     *      key,
     *      value,
     *      keyword,
     *      sort
     *  }
     * @param option
     * @param callback
     */
    @JSMethod
    public void save(String option, JSCallback callback) {

        String type = "";
        String key = "";
        String value = "";
        String keyword = "N";
        String sort = "";
        Message message = new Message();
        if(option==null || option.equals("")) return;
        option = option.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
        try {
            option = URLDecoder.decode(option, "utf-8");
            com.alibaba.fastjson.JSONObject jsObj = JSON.parseObject(option);
            if (jsObj.containsKey("type")) {
                type = jsObj.getString("type");
            }
            if (jsObj.containsKey("keyword")) {
                keyword = jsObj.getString("keyword");
                if(keyword != null && keyword.equals("")){
                    keyword = "N";
                }
            }
            if (jsObj.containsKey("key")) {
                key = jsObj.getString("key");
            }
            if (jsObj.containsKey("value")) {
                value = jsObj.getString("value");
            }
            if (jsObj.containsKey("sort")) {
                sort = jsObj.getString("sort");
            }
            DbCacheBean dbCacheBean = new DbCacheBean();
            dbCacheBean.setDoType(DbCacheBean.Type.SAVE);
            dbCacheBean.setType(type);
            dbCacheBean.setKeyword(keyword);
            dbCacheBean.setKey(key);
            dbCacheBean.setValue(value);
            dbCacheBean.setSort(sort);
            dbCacheBean.setJsCallback(callback);
//            判断是否登录
            if(!DbUtils.checkLogin()){
                DbUtils.handleNotLogin(dbCacheBean);
            }else {
                //处理
                DbUtils.doSqlite(dbCacheBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            message.setType("error");
            message.setContent("保存失败");
            message.setData(e.toString());
            callback.invoke(message);
        }
    }

    /**
     *  option{
     *      type,
     *      key
     *  }
     * @param option
     * @param callback
     */
    @JSMethod
    public void find(String option, JSCallback callback) {
        String type = "";
        String key = "";
        Message message = new Message();
        try {
            if(option==null || option.equals("")) return;
            option = option.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            option = URLDecoder.decode(option, "utf-8");
            com.alibaba.fastjson.JSONObject jsObj = JSON.parseObject(option);
            if (jsObj.containsKey("type")) {
                type = jsObj.getString("type");
            }
            if (jsObj.containsKey("key")) {
                key = jsObj.getString("key");
            }
            DbCacheBean dbCacheBean = new DbCacheBean();
            dbCacheBean.setDoType(DbCacheBean.Type.FIND);
            dbCacheBean.setJsCallback(callback);
            dbCacheBean.setType(type);
            dbCacheBean.setKey(key);
            if(!type.equals(XRequest.HTTPCACHE) && !DbUtils.checkLogin()){
                DbUtils.handleNotLogin(dbCacheBean);
                return;
            }else {
                DbUtils.doSqlite(dbCacheBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            message.setType("error");
            message.setContent("读取失败");
            message.setData("");
        }
        callback.invoke(message);
    }


    /**
     * FindListOption
     * <p>
     * type
     * keyword
     * orderBy
     * page
     * pageSize
     */
    @JSMethod
    public void findList(String option, JSCallback callback) {
        String type = "";
        String keyword = "";
        String orderBy = "desc";
        //两个值均为0的时候返回所有的type
        int current = 0;
        int pageSize = 10;
        Message message = new Message();
        if(option==null || option.equals("")) return;
        option = option.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
        try {
            option = URLDecoder.decode(option, "utf-8");
            com.alibaba.fastjson.JSONObject jsObj = JSON.parseObject(option);
            if (jsObj.containsKey("type")) {
                type = jsObj.getString("type");
            }
            if (jsObj.containsKey("keyword")) {
                keyword = jsObj.getString("keyword");
            }
            if (jsObj.containsKey("current")) {
                current = jsObj.getInteger("current");
            }
            if (jsObj.containsKey("pageSize")) {
                pageSize = jsObj.getInteger("pageSize");
            }
            if (jsObj.containsKey("orderBy")) {
                orderBy = jsObj.getString("orderBy");
            }
            DbCacheBean dbCacheBean = new DbCacheBean();
            dbCacheBean.setDoType(DbCacheBean.Type.FINDLIST);
            dbCacheBean.setJsCallback(callback);
            dbCacheBean.setType(type);
            dbCacheBean.setKeyword(keyword);
            dbCacheBean.setCurrent(current);
            dbCacheBean.setPageSize(current + pageSize);
            dbCacheBean.setOrderBy(orderBy);
            if(!DbUtils.checkLogin()){
                DbUtils.handleNotLogin(dbCacheBean);
                return;
            }else {
                DbUtils.doSqlite(dbCacheBean);
            }

        } catch (Exception e) {
            e.printStackTrace();
            message.setError("读取失败");
        }


        callback.invoke(message);

    }
    @JSMethod
    public void deleteAll(String option, JSCallback callback){
        Message message = new Message();
        String type = "";
        try {
            if(option == null || option.equals("")) return;
            option = option.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            option = URLDecoder.decode(option, "utf-8");
            com.alibaba.fastjson.JSONObject jsObj = JSON.parseObject(option);
            if (jsObj.containsKey("type")) {
                type = jsObj.getString("type");
            }

            DbCacheBean dbCacheBean = new DbCacheBean();
            dbCacheBean.setDoType(DbCacheBean.Type.DELETEALL);
            dbCacheBean.setJsCallback(callback);
            dbCacheBean.setType(type);

            if(!DbUtils.checkLogin()){
                DbUtils.handleNotLogin(dbCacheBean);
                return;
            }else {
                DbUtils.doSqlite(dbCacheBean);
            }

        } catch (Exception e) {
            e.printStackTrace();
            message.setType("error");
            message.setContent("删除失败");
            message.setData(null);
        }
        callback.invoke(message);
    }

    @JSMethod
    public void delete(String option, JSCallback callback) {
        Message message = new Message();
        String type = "";
        String key = "";
        try {
            if(option==null || option.equals("")) return;
            option = option.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            option = URLDecoder.decode(option, "utf-8");
            com.alibaba.fastjson.JSONObject jsObj = JSON.parseObject(option);
            if (jsObj.containsKey("type")) {
                type = jsObj.getString("type");
            }
            if (jsObj.containsKey("key")) {
                key = jsObj.getString("key");
            }
            DbCacheBean dbCacheBean = new DbCacheBean();
            dbCacheBean.setDoType(DbCacheBean.Type.DELETE);
            dbCacheBean.setJsCallback(callback);
            dbCacheBean.setType(type);
            dbCacheBean.setKey(key);

            if(!DbUtils.checkLogin()){
                DbUtils.handleNotLogin(dbCacheBean);
                return;
            }else {
                DbUtils.doSqlite(dbCacheBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            message.setType("error");
            message.setContent("删除失败");
            message.setData(null);
        }
        callback.invoke(message);
    }
//    获取未读消息

    @JSMethod
    public void getUnReadMessage(){
        EventBus.getDefault().post(new MessageBus(MessageBus.Type.RECEIVEMSG));

        List<TIMConversation> list = TIMManagerExt.getInstance().getConversationList();
        List<String> userIds = new ArrayList<>();

        final List<com.rzico.weex.model.chat.Message> unReadUserMessages = new ArrayList<>();//未读消息
        final List<Long> unReadNumber = new ArrayList<>();//未读消息

        long unRead = 0;
        for (TIMConversation item :list){
            if(item.getPeer().equals("")) continue;
            TIMConversationExt conExt = new TIMConversationExt(item);
            if(conExt.getUnreadMessageNum() > 0){
                List<TIMMessage> messages = conExt.getLastMsgs(1);
                if(messages !=null && messages.size() > 0){
                    final com.rzico.weex.model.chat.Message message = MessageFactory.getMessage(messages.get(0));
                    unReadUserMessages.add(message);
                    userIds.add(item.getPeer());
                    unReadNumber.add(conExt.getUnreadMessageNum());
                }
            }
        }
        if (userIds.size() > 0){
            //获取好友资料
            TIMFriendshipManager.getInstance().getUsersProfile(userIds, new TIMValueCallBack<List<TIMUserProfile>>(){
                @Override
                public void onError(int code, String desc){
                }

                @Override
                public void onSuccess(List<TIMUserProfile> result){
                    int len = result.size();
                    for (int i = 0; i < len; i++){
                        if(result!=null && result.size() > 0) {
                            TIMUserProfile user = result.get(i);
                            com.rzico.weex.model.chat.Message message = unReadUserMessages.get(i);
                            com.rzico.weex.model.info.Message onMessage = new com.rzico.weex.model.info.Message();
                            onMessage.setType("success");
                            onMessage.setContent("您有一条新消息");
                            IMMessage imMessage = new IMMessage();
//
                            imMessage.setUnRead(unReadNumber.get(i));
                            imMessage.setContent(message.getSummary());
                            imMessage.setId(message.getSender());
                            imMessage.setLogo(user.getFaceUrl());
                            imMessage.setNickName(user.getNickName());
                            imMessage.setCreateDate(message.getMessage().timestamp());
                            onMessage.setData(imMessage);
                            Map<String, Object> params = new HashMap<>();
                            params.put("data", onMessage);
                            EventBus.getDefault().post(new MessageBus(MessageBus.Type.GLOBAL, "onMessage", params));

                        }
                    }
                }
            });
        }
    }

    @JSMethod(uiThread = false)
    public boolean haveTop(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            return true;
        }else {
            return false;//顶部状态栏顶不上去
        }
    }

    @JSMethod
    public void openEditor(String data, JSCallback jsCallback) {//data 可以为空
        String key = String.valueOf(System.currentTimeMillis());
        if (jsCallback != null) {//如果有传入回调的话
            JSCallBaskManager.put(key, jsCallback);
        }
        Intent intent = new Intent();//
        intent.setClass(getActivity(), RichEditorAcitivity.class);
        intent.putExtra("key", key);
        intent.putExtra("data", data == null ? "" : data);
        getActivity().startActivity(intent);
    }

    /**
     * 上传文件
     *
     * @param filePath         文件路径不带 file://
     * @param callback         回调
     * @param progressCallback 进度条回调
     */
    @JSMethod
    public void upload(final String filePath, final JSCallback callback, final JSCallback progressCallback) {

        String cachefileName = "";
        if(filePath.endsWith("jpg") || filePath.endsWith("bmp") || filePath.endsWith("png") || filePath.endsWith("jpeg")){
            //在这里压缩 把压缩完的地址 放 filepath 里面
            cachefileName = AllConstant.getDiskCachePath(getActivity()) +"/"+ System.currentTimeMillis() + ".jpg";
            NativeUtil.compressBitmap(filePath, cachefileName);
        }else{
            cachefileName = filePath;
        }
        final String finalCacheFileName = cachefileName;
        final String stsData = SharedUtils.read("stsData");
        boolean error = true;//解析出错 或者 超时就失败 就请求sts
        if (stsData != null && !stsData.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(stsData);
                if(jsonObject!=null){
                    //对比查看sts有没有过期
                    error = DateUtils.isDateOneBigger(new Date(), jsonObject.getString("Expiration"), DateUtils.DATE_FORMAT_3);
                }
                if (!error) {
                    //取本地缓存不用去服务器取
                    uploadFile(stsData, getActivity(), finalCacheFileName, callback, progressCallback);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                error = true;
            }
        }

        if (error) {
            new XRequest(getActivity(), "weex/member/oss/sts.jhtml").setOnRequestListener(new HttpRequest.OnRequestListener() {
                @Override
                public void onSuccess(BaseActivity activity, String result, String type) {
                    Message entity = new Gson().fromJson(result, Message.class);
                    String data = new Gson().toJson(entity.getData());
                    SharedUtils.save("stsData", data);
                    uploadFile(data, getActivity(), finalCacheFileName, callback, progressCallback);
                }

                @Override
                public void onFail(BaseActivity activity, String cacheData, int code) {
                    Message message = new Message().error("获取sts失败");
                    callback.invoke(message);
                }
            }).execute();

        }
    }
    @JSMethod
    public void navToChat(String userId){
        ChatActivity.navToChat(getActivity(), userId, TIMConversationType.C2C);
    }
    @JSMethod
    public void scan(final JSCallback callback){
        WXEventModule.get().initScan(new RxWeiXinAuthFinalHolderListener() {
            @Override
            public void userOk(String code) {
                Message message = new Message().success(code);
                callback.invoke(message);
            }

            @Override
            public void userCancel() {
                Message message = new Message().error();
                callback.invoke(message);
            }

            @Override
            public void authDenied() {

            }
        }).scanHandler(getActivity());
    }


    public void scanHandler(final BaseActivity activity){
        Dexter.withActivity(activity).withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

//                        IntentIntegrator integrator = new IntentIntegrator(activity);
//                        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
//                        integrator.setPrompt("Scan a barcode");
//                        //integrator.setCameraId(0);  // Use a specific camera of the device
//                        integrator.setBeepEnabled(true);
//                        integrator.setOrientationLocked(false);
//                        integrator.setBarcodeImageEnabled(true);
//                        integrator.setPrompt(activity.getString(R.string.capture_qrcode_prompt));
//                        integrator.initiateScan();

                        // 二维码扫码
                        Intent intent = new Intent(activity, CaptureActivity.class);
                        activity.startActivityForResult(intent, Constant.REQ_QR_CODE);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        activity.showDeniedDialog("此功能需要调用照相机");
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
                                                                   PermissionToken token) {
                        //用户不允许权限，向用户解释权限左右
                        token.continuePermissionRequest();
                    }
                }).check();

    }
    private WeakReference<PauseableUploadTask> task;
    //上传文件
    public void uploadFile(String stsData, BaseActivity activity, String filePath, JSCallback callback, JSCallback progressCallback) {
        try{
            OSSCredentialProvider credentialProvider = new STSGetter(stsData);
            OSS oss = new OSSClient(activity, Constant.endpoint, credentialProvider);
            OssService ossService = new OssService(oss, Constant.bucket);
            Date nowTime = new Date();
            SimpleDateFormat time = new SimpleDateFormat("yyyy/MM/dd");
            String [] text = filePath.split("/");
            String [] houzui = text[text.length - 1].split("\\.");
            String imagePath = Constant.upLoadImages + time.format(nowTime) + "/" + UUID.randomUUID().toString() + "." + houzui[houzui.length - 1];
//        ossService.asyncPutImage(imagePath, filePath, callback, progressCallback);
//        if ((task == null) || (task.get() == null)){
//            Log.d("MultiPartUpload", "Start");
            PauseableUploadTask pauseableUploadTask = ossService.asyncMultiPartUpload(imagePath, filePath, callback, progressCallback);
            if(pauseableUploadTask != null){
                task = new WeakReference<>(pauseableUploadTask);
            }else {
                callback.invoke(new Message().error("图片地址不合法"));
            }
        } catch (Exception e){
            callback.invoke(new Message().error("图片地址不合法"));
        }

    }    /**获取库Phon表字段**/
    private static final String[] PHONES_PROJECTION = new String[] {
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY, ContactsContract.CommonDataKinds.Photo.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.DATA1 };

    /**联系人显示名称**/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
    /**电话号码**/
    private static final int PHONES_NUMBER_INDEX = 1;

    /**
     *option={
     * page
     * pageSize
     * }
     * @param option
     * @param callback
     */
    @JSMethod
    public void getContactList(String option, final JSCallback callback){

        int current = 0;
        int pageSize = 10;
        try {
            option = URLDecoder.decode(option, "utf-8");
            com.alibaba.fastjson.JSONObject jsObj = JSON.parseObject(option);
            if (jsObj.containsKey("current")) {
                current = jsObj.getInteger("current");
            }
            if (jsObj.containsKey("pageSize")) {
                pageSize = jsObj.getInteger("pageSize");
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }

        final int finalCurrent = current;
        final int finalPageSize = pageSize;
        Dexter.withActivity(getActivity()).withPermission(Manifest.permission.READ_CONTACTS)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        List<Contact> contacts = ContactUtils.getContact(getContext());
                        int length = contacts.size();
                        if( length > 0 ){
                            List<Contact> orderFinshlist = new ArrayList<>();
                            int i;
                            int sCurrent = finalCurrent > length ? length : finalCurrent;
                            int sPageSize= (finalCurrent + finalPageSize) > length ? length : (finalCurrent + finalPageSize);

                            for (i = sCurrent; i < sPageSize; i++){
                                orderFinshlist.add(contacts.get(i));
                            }
                            Message message = new Message().success(orderFinshlist);
                            callback.invoke(message);
                        }
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        getActivity().showDeniedDialog("此功能需要通讯录权限");
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
                                                                   PermissionToken token) {

                        //用户不允许权限，向用户解释权限左右
                        Message message = new Message().error("用户拒绝通讯录权限");
                        callback.invoke(message);
                        token.continuePermissionRequest();
                    }
                }).check();

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(data!=null){
            Bundle bundle = data.getExtras();
            if (bundle != null && rxScanfinalHolderListener!= null) {
                String scanResult = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN);
                if (scanResult != null && !scanResult.equals("")) {
                    rxScanfinalHolderListener.userOk(scanResult);
                } else {
                    rxScanfinalHolderListener.userCancel();
                }
            }
        }
    }


    /**
     *
     * @param option
     *  String title = "";   // 这个是分享的标题
    String text = ""; //这个是分享的介绍
    String imagePath = "";//这里图片可能是路径 可能是url path 不必加file
    String imageUrl = "";
    String url = "";// 这个是分享的url
    String type = [appMessage,timeline,favorite] //分别是 微信好友、 微信朋友圈、 微信收藏
    copyHref 复制连接 browser 打开浏览器
     * @param callback
     */
    @JSMethod
    public void share(String option, final JSCallback callback){
        String title = "";
        String text = "";
        String imagePath = "";
        String imageUrl = "";
        String url = "";
        String type = "Wechat";

        try {
            option = URLDecoder.decode(option, "utf-8");
            com.alibaba.fastjson.JSONObject jsObj = JSON.parseObject(option);
            if (jsObj.containsKey("title")) {
                title = jsObj.getString("title");
            }
            if (jsObj.containsKey("text")) {
                text = jsObj.getString("text");
            }
            if (jsObj.containsKey("imagePath")) {
                imagePath = jsObj.getString("imagePath");
            }
            if (jsObj.containsKey("imageUrl")) {
                imageUrl = jsObj.getString("imageUrl");
            }
            if (jsObj.containsKey("url")) {
                url = jsObj.getString("url");
            }
            if (jsObj.containsKey("type")) {
                type = jsObj.getString("type");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Platform.ShareParams params =  new Platform.ShareParams();
        params.setShareType(Platform.SHARE_WEBPAGE);//一般都是网页 如果要别的再另外加
        if(!imagePath.equals("")){
            params.setImagePath(imagePath);
        }else if(!imageUrl.equals("")){
            params.setImageUrl(imageUrl);
        }
        params.setText(text);
        params.setTitle(title);
        params.setUrl(url);
        Platform platform = null;
        if(type.equals("favorite")){
            platform = ShareSDK.getPlatform(WechatFavorite.NAME);
        }else if(type.equals("timeline")){
            platform = ShareSDK.getPlatform(WechatMoments.NAME);
        }else if(type.equals("appMessage")){
            platform = ShareSDK.getPlatform(Wechat.NAME);
        }else if(type.equals("copyHref")){
            // 得到剪贴板管理器
            ClipboardManager cmb = (ClipboardManager)getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(title.trim() + "\r\n" + text.trim() + "\r\n" +url.trim());
            Message message = new Message().success("");
            callback.invoke(message);
        }else if(type.equals("browser")){
            //分享到浏览器
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            getActivity().startActivity(intent);

            Message message = new Message().success("");
            callback.invoke(message);
        }
        if(platform!=null){
            platform.setPlatformActionListener(new PlatformActionListener() {
                @Override
                public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                    Message message = new Message().success("分享成功");
                    callback.invoke(message);
                }

                @Override
                public void onError(Platform platform, int i, Throwable throwable) {
                    Message message = new Message().error("分享出错");
                    callback.invoke(message);
                }
                @Override
                public void onCancel(Platform platform, int i) {
                    Message message = new Message().error("用户取消");
                    callback.invoke(message);
                }
            });
            platform.share(params);
        }
    }


    @JSMethod(uiThread = false)
    public boolean deleteConversation(String peer){
        TIMConversation conversation = TIMManager.getInstance().getConversation(
                TIMConversationType.C2C,    //会话类型：单聊
                peer);                      //会话对方用户帐号
//获取会话扩展实例
        TIMConversationExt conExt = new TIMConversationExt(conversation);
        //将此会话的所有消息标记为已读
        conExt.setReadMessage(null, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
            }

            @Override
            public void onSuccess() {
            }
        });
        return TIMManagerExt.getInstance().deleteConversationAndLocalMsgs(TIMConversationType.C2C, peer);
    }

    /*
     标记已读
     */
    @JSMethod
    public void setReadMessage(String peer, final JSCallback callback){
        TIMConversation conversation = TIMManager.getInstance().getConversation(
                TIMConversationType.C2C,    //会话类型：单聊
                peer);                      //会话对方用户帐号
//获取会话扩展实例
        TIMConversationExt conExt = new TIMConversationExt(conversation);
        //将此会话的所有消息标记为已读
        conExt.setReadMessage(null, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                Message message = new Message().error("");
                callback.invoke(message);
            }

            @Override
            public void onSuccess() {
                Message message = new Message().success("");
                callback.invoke(message);
            }
        });
    }

    /**
     * 用户不允许权限，向用户说明权限的重要性，并支持用户去设置中开启权限
     */
    public void showDeniedDialog(String title) {
        new android.app.AlertDialog.Builder(getActivity()).setTitle(title)
                .setMessage("请允许使用该权限,拒绝将无法使用此功能")
                .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //TiaohuoApplication.exit();
                    }
                })
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                        intent.putExtra("cmp", "com.android.settings/.applications.InstalledAppDetails");
                        intent.addCategory("android.intent.category.DEFAULT");
                        getActivity().startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @JSMethod
    public void getLocation(final JSCallback callback){

        Dexter.withActivity(getActivity()).withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new com.karumi.dexter.listener.single.PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        AMapLocationClient mLocationClient = null;
                        mLocationClient = new AMapLocationClient(getContext());
                        mLocationClient.setLocationListener(new AMapLocationListener() {
                            @Override
                            public void onLocationChanged(AMapLocation aMapLocation) {
                                if (aMapLocation != null) {
                                    if (aMapLocation.getErrorCode() == 0) {
                                        //解析定位结果
                                        Message message = new Message();
                                        message.setType("success");
                                        message.setContent("定位成功");
                                        Location location = new Location();
                                        location.setAddress(aMapLocation.getAddress());
                                        location.setCity(aMapLocation.getCity());
                                        location.setCityCode(aMapLocation.getCityCode());
                                        location.setCountry(aMapLocation.getCountry());
                                        location.setDistrict(aMapLocation.getDistrict());
                                        location.setLat(aMapLocation.getLatitude());
                                        location.setLng(aMapLocation.getLongitude());
                                        location.setStreet(aMapLocation.getStreet());
                                        location.setProvince(aMapLocation.getProvince());
                                        message.setData(location);
                                        callback.invoke(message);
                                    }else{
                                        Message message = new Message().error(aMapLocation.getErrorInfo());
                                        callback.invoke(message);
                                    }
                                }else{
                                    Message message = new Message().error("获取失败");
                                }
                            }
                        });
                        mLocationClient.startLocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        showDeniedDialog("需要定位权限");
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
                                                                   PermissionToken token) {
                        //用户不允许权限，向用户解释权限左右
                        token.continuePermissionRequest();
                    }
                }).check();
    }


    //    IWXStorageAdapter mStorageAdapter;
//
//    private IWXStorageAdapter ability() {
//        if (mStorageAdapter != null) {
//            return mStorageAdapter;
//        }
//        mStorageAdapter = WXSDKEngine.getIWXStorageAdapter();
//        return mStorageAdapter;
//    }
    @JSMethod
    public void getCacheSize(JSCallback callback){
//        IWXStorageAdapter adapter = ability();
//        if (adapter == null) {
//            StorageResultHandler.handleNoHandlerError(callback);
//            return;
//        }
//        adapter.length(new IWXStorageAdapter.OnResultReceivedListener() {
//            @Override
//            public void onReceived(Map<String, Object> data) {
//                Map<String, Object> map = data;
//            }
//        });
//        暂时不做处理
        CacheSize cacheSize = new CacheSize();
        cacheSize.setCache("0M");
        cacheSize.setTim("0M");
        cacheSize.setWxstorage("0M");
        Message message = new Message().success(cacheSize);
        callback.invoke(message);
    }
    @JSMethod
    public void clearCache(JSCallback callback){
        String cachePath = PathUtils.getCachePath();
        boolean success = DeleteFileUtil.delete(cachePath);
        success = DeleteFileUtil.delete(AllConstant.getDiskCachePath(getActivity()));
        if(success){
            Message message = new Message().success("");
            callback.invoke(message);
        }else{
            Message message = new Message().error();
            callback.invoke(message);
        }
    }

    @JSMethod
    public void sendGlobalEvent(String eventKey, Message data){
        Map<String, Object> params = new HashMap<>();
        params.put("data", data);
        //推送前面4个页面

        EventBus.getDefault().post(new MessageBus(MessageBus.Type.GLOBAL, eventKey, params));
        //判断当前页面是不是weex页面
    }

    @JSMethod
    public void getss(){

    }
    @JSMethod
    public String md5(String data){
        return Utils.getMD5(data);
    }

    public interface RxWeiXinAuthFinalHolderListener {
        //        发送成功 获取到code
        void userOk(String code);
        //        用户取消了
        void userCancel();
        //        发送被拒绝了
        void authDenied();
    }




}
