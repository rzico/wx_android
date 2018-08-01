package com.rzico.weex.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.CompoundButton;


import com.google.gson.Gson;
import com.huawei.android.pushagent.api.PushManager;
import com.meizu.cloud.pushsdk.util.MzSystemUtils;
import com.rzico.weex.Constant;
import com.rzico.weex.R;
import com.rzico.weex.WXApplication;
import com.rzico.weex.activity.BaseActivity;
import com.rzico.weex.activity.LoginActivity;
import com.rzico.weex.activity.chat.ChatActivity;
import com.rzico.weex.db.DbUtils;
import com.rzico.weex.model.event.MessageBus;
import com.rzico.weex.model.info.LoginBean;
import com.rzico.weex.net.HttpRequest;
import com.rzico.weex.net.XRequest;
import com.rzico.weex.utils.chat.PushUtil;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;

import com.tencent.imsdk.TIMOfflinePushSettings;
import com.tencent.imsdk.TIMOfflinePushToken;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.qcloud.presentation.event.MessageEvent;
import com.tencent.qcloud.ui.LineControllerView;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.tencent.open.utils.Global.getPackageName;

/**
 * Created by Jinlesoft on 2017/10/18.
 */

public class LoginUtils  {

    public interface OnLoginListener {
        void onSuccess(LoginBean loginBean);
        void onError(LoginErrorType loginErrorType);
    }
    public enum  LoginErrorType{
        UNLOGIN,
        IMLOGINERROR,
        LOGINERROR
    }
    private static final String tag = "weex_loginutils";
    public static void checkLogin(BaseActivity activity,final Intent intent,final OnLoginListener listener) {
        new XRequest(activity, "/weex/login/isAuthenticated.jhtml", XRequest.POST, new HashMap<String, Object>()).setOnRequestListener(new HttpRequest.OnRequestListener() {
            @Override
            public void onSuccess(final BaseActivity activity, String result, String type) {
                final LoginBean loginBean = new Gson().fromJson(result, LoginBean.class);
                if(loginBean != null){
                    if(!loginBean.getData().isLoginStatus()){
                        //如果是空的话就是直接 跳转登录页面 不做登录回调处理 用于 网络请求那边回调
                        if(listener !=null ){
                            listener.onError(LoginErrorType.UNLOGIN);//登录失败
                        }else {
                            Intent intent = new Intent(activity, LoginActivity.class);
                            activity.startActivity(intent);
                        }
                        loginError();
                    }else{
                        Constant.userId = loginBean.getData().getUid();
                        SharedUtils.saveLoginId(Constant.userId);
                        // identifier为用户名，userSig 为用户登录凭证
                        TIMManager.getInstance().login(loginBean.getData().getUserId(), loginBean.getData().getUserSig(), new TIMCallBack() {
                            @Override
                            public void onError(int code, String desc) {
                                //错误码code和错误描述desc，可用于定位请求失败原因
                                //错误码code列表请参见错误码表
                                Log.d("weex", "login failed. code: " + code + " errmsg: " + desc);
                                    if(listener != null) {
                                    listener.onError(LoginErrorType.IMLOGINERROR);//登录失败
                                }
                                loginError();

                            }
                            @Override
                            public void onSuccess() {
                                // 登录成功 需要传回来登录的loginId
//                                SharedUtils.saveLoginId(Constant.userId);
//                                Constant.loginState = true;
//                                Constant.userId = loginBean.getData().getUid();
                                Constant.imUserId = loginBean.getData().getUserId();
                                SharedUtils.saveImId(Constant.imUserId);
                                DbUtils.reDoSql();
                                Log.d("weex", "login succ");
//                                发起聊天
                                String identify = "";
                                TIMConversationType type;
                                //如果传参 就是 程序没打开 而被唤醒后
                                if(intent!=null){
                                    identify = intent.getStringExtra("identify");
                                    type = (TIMConversationType) intent.getSerializableExtra("type");
                                    if(identify !=null && !identify.equals("") && type != null){
                                        ChatActivity.navToChat(activity, identify, TIMConversationType.C2C);
                                    }
                                }
                                //初始化程序后台后消息推送
                                PushUtil.getInstance();
                                //初始化消息监听
                                MessageEvent.getInstance();
                                String deviceMan = android.os.Build.MANUFACTURER;
                                //注册小米和华为推送
                                if (deviceMan.equals("Xiaomi")){
//                                    System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                                    MiPushClient.registerPush(WXApplication.getContext(), Constant.mipushAppId, Constant.mipushAppKey);
                                }else if (deviceMan.equals("HUAWEI")){
                                    com.huawei.android.pushagent.PushManager.requestToken(activity);
                                }

                                //魅族推送只适用于Flyme系统,因此可以先行判断是否为魅族机型，再进行订阅，避免在其他机型上出现兼容性问题
//                                if(MzSystemUtils.isBrandMeizu(WXApplication.getContext())){
//                                    com.meizu.cloud.pushsdk.PushManager.register(activity, "112662", "3aaf89f8e13f43d2a4f97a703c6f65b3");
//                                }

//                                c2cMusic();
                                if(listener!=null){
                                    listener.onSuccess(loginBean);
                                }
                                loginSuccess();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFail(BaseActivity activity, String cacheData, int code) {
                if(listener!=null){
                    listener.onError(LoginErrorType.LOGINERROR);//登录失败
                }
                loginError();
//                Toast.makeText(activity, code, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }


    /**
     * 判断小米推送是否已经初始化
     */
//    private static boolean shouldMiInit() {
//        ActivityManager am = ((ActivityManager) WXApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE));
//        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
//        String mainProcessName = getPackageName();
//        int myPid = android.os.Process.myPid();
//        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
//            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//
    public static void loginSuccess(){
        Constant.loginState = true;
        SharedUtils.saveLoginId(Constant.userId);
        EventBus.getDefault().post(new MessageBus(MessageBus.Type.LOGINSUCCESS));
    }
    public static void loginError(){
        Constant.loginState = false;
        //登录失败现在改成
        Constant.userId = SharedUtils.readLoginId();
        //如果是离线登录的话
        //unLinelogin是判断是否是离线登录
        if(SharedUtils.readLoginId()!=0){
            Constant.unLinelogin = true;
        }else {
            Constant.unLinelogin = false;
        }

        EventBus.getDefault().post(new MessageBus(MessageBus.Type.LOGINERROR));
    }

    public static void c2cMusic() {
        final Uri notifyMusic = Uri.parse("android.resource://"+WXApplication.getInstance().getPackageName()+"/" + R.raw.h0);
        TIMManager.getInstance().getOfflinePushSettings(new TIMValueCallBack<TIMOfflinePushSettings>() {
            @Override
            public void onError(int i, String s) {
//                Log.e(TAG, "get offline push setting error " + s);
            }
            @Override
            public void onSuccess(TIMOfflinePushSettings timOfflinePushSettings) {
                TIMOfflinePushSettings settings = timOfflinePushSettings;

                        settings.setC2cMsgRemindSound(notifyMusic);
                        TIMManager.getInstance().setOfflinePushSettings(settings);

            }
        });

    }
}
