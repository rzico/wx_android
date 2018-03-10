package com.rzico.weex;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.huawei.android.pushagent.PushManager;
import com.mob.MobSDK;
import com.rzico.weex.activity.MainActivity;
import com.rzico.weex.adapter.ImageAdapter;
import com.rzico.weex.component.MYWXWeb;
import com.rzico.weex.component.module.MYWXWebViewModule;
import com.rzico.weex.model.LivePlayerBean;
import com.rzico.weex.model.info.Message;
import com.rzico.weex.module.AudioModule;
import com.rzico.weex.module.LivePlayerModule;
import com.rzico.weex.module.MYModalUIModule;
import com.rzico.weex.module.PhoneModule;
import com.rzico.weex.module.PrintModule;
import com.rzico.weex.module.WXEventModule;
import com.rzico.weex.activity.BaseActivity;
import com.rzico.weex.adapter.WeexHttpAdapter;
import com.rzico.weex.adapter.WeexJSExceptionAdapter;
import com.rzico.weex.adapter.WeexUriAdapter;
import com.rzico.weex.module.AlbumModule;
import com.rzico.weex.net.HttpRequest;
import com.rzico.weex.net.XRequest;
import com.rzico.weex.utils.SharedUtils;
import com.rzico.weex.utils.Utils;
import com.rzico.weex.utils.chat.Foreground;
import com.taobao.weex.InitConfig;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.common.WXException;
import com.taobao.weex.ui.component.WXBasicComponentType;

import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMGroupEventListener;
import com.tencent.imsdk.TIMGroupReceiveMessageOpt;
import com.tencent.imsdk.TIMGroupTipsElem;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMLogListener;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMOfflinePushListener;
import com.tencent.imsdk.TIMOfflinePushNotification;
import com.tencent.imsdk.TIMOfflinePushSettings;
import com.tencent.imsdk.TIMRefreshListener;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserStatusListener;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.message.TIMConversationExt;
import com.tencent.qalsdk.sdk.MsfSdkUtils;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.xutils.x;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.tencent.qcloud.sdk.Constant.SDK_APPID;


public class WXApplication extends Application {

  private static Context context;

  private SharedPreferences SharedPre;

  private  static List<BaseActivity> activityList = new LinkedList<BaseActivity>();

  private final String tag = "mopian";
  //数据库管理类
  private static org.xutils.DbManager db;

  private static WXApplication instance;


  private Map<String, WXSDKInstance> wxsdkInstanceMap;

  private Handler loginHandler = null;

  public Map<String, WXSDKInstance> getWxsdkInstanceMap() {
    return wxsdkInstanceMap;
  }

  public void setWxsdkInstanceMap(Map<String, WXSDKInstance> wxsdkInstanceMap) {
    this.wxsdkInstanceMap = wxsdkInstanceMap;
  }

  public Handler getLoginHandler() {
    return loginHandler;
  }

  public void setLoginHandler(Handler loginHandler) {
    this.loginHandler = loginHandler;
  }

  public static WXApplication getInstance() {
    return instance;
  }

  public static  org.xutils.DbManager getDb(){ return db;}

  public static void setDb(org.xutils.DbManager db2){
    db = db2;
  }
  @Override
  public void onCreate() {
    super.onCreate();
    MultiDex.install(this);
    Foreground.init(this);
    context = getApplicationContext();
//    if(MsfSdkUtils.isMainProcess(this)) {
//      TIMManager.getInstance().setOfflinePushListener(new TIMOfflinePushListener() {
//        @Override
//        public void handleNotification(TIMOfflinePushNotification notification) {
//          if (notification.getGroupReceiveMsgOpt() == TIMGroupReceiveMessageOpt.ReceiveAndNotify){
//            //消息被设置为需要提醒
//              notification.doNotify(getApplicationContext(), R.mipmap.ic_launcher);
//          }
//        }
//      });
//    }

//    initDebugEnvironment(true, false, "DEBUG_SERVER_HOST");
    instance = this;
    init();
    initAlbum();
    initWeex();
//    initIM();
  }

  public static void initWeex(){
    WXSDKEngine.addCustomOptions("appName", WXApplication.getInstance().getResources().getString(R.string.app_name));
    WXSDKEngine.addCustomOptions("appGroup", "WXApp");
    WXSDKEngine.initialize(WXApplication.getInstance(),
            new InitConfig.Builder()
                    .setImgAdapter(new ImageAdapter())
                    .setHttpAdapter(new WeexHttpAdapter())
                    .setURIAdapter(new WeexUriAdapter())
                    .setJSExceptionAdapter(new WeexJSExceptionAdapter())
                    .build()
    );
    try {
      WXSDKEngine.registerComponent(WXBasicComponentType.WEB, MYWXWeb.class);

      WXSDKEngine.registerModule("webview", MYWXWebViewModule.class, true);
      WXSDKEngine.registerModule("event", WXEventModule.class);
      WXSDKEngine.registerModule("audio", AudioModule.class);
      WXSDKEngine.registerModule("album", AlbumModule.class);
      WXSDKEngine.registerModule("modal", MYModalUIModule.class);
      WXSDKEngine.registerModule("print", PrintModule.class);
      WXSDKEngine.registerModule("phone", PhoneModule.class);
      WXSDKEngine.registerModule("livePlayer", LivePlayerModule.class);


    } catch (WXException e) {
      e.printStackTrace();
    }

  }
  private void initIM() {
    //初始化SDK基本配置
    TIMSdkConfig config = new TIMSdkConfig(SDK_APPID)
            .enableCrashReport(false)
            .enableLogPrint(true)
            .setLogLevel(TIMLogLevel.DEBUG)
            .setLogPath(Environment.getExternalStorageDirectory().getPath() + "/justfortest2/");

//初始化SDK
    TIMManager.getInstance().init(getApplicationContext(), config);
    //基本用户配置
    TIMUserConfig userConfig = new TIMUserConfig()
            //设置用户状态变更事件监听器
            .setUserStatusListener(new TIMUserStatusListener() {
              @Override
              public void onForceOffline() {
                //被其他终端踢下线
                Log.i(tag, "onForceOffline");

              }

              @Override
              public void onUserSigExpired() {
                //用户签名过期了，需要刷新userSig重新登录SDK
                Log.i(tag, "onUserSigExpired");
              }
            })
            //设置连接状态事件监听器
            .setConnectionListener(new TIMConnListener() {
              @Override
              public void onConnected() {
                Log.i(tag, "onConnected");
              }

              @Override
              public void onDisconnected(int code, String desc) {
                Log.i(tag, "onDisconnected");
              }

              @Override
              public void onWifiNeedAuth(String name) {
                Log.i(tag, "onWifiNeedAuth");
              }
            })
            //设置群组事件监听器
            .setGroupEventListener(new TIMGroupEventListener() {
              @Override
              public void onGroupTipsEvent(TIMGroupTipsElem elem) {
                Log.i(tag, "onGroupTipsEvent, type: " + elem.getTipsType());
              }
            })
            //设置会话刷新监听器
            .setRefreshListener(new TIMRefreshListener() {
              @Override
              public void onRefresh() {
                Log.i(tag, "onRefresh");
              }

              @Override
              public void onRefreshConversation(List<TIMConversation> conversations) {
                Log.i(tag, "onRefreshConversation, conversation size: " + conversations.size());
              }
            });
//将用户配置与通讯管理器进行绑定
    TIMManager.getInstance().setUserConfig(userConfig);


  }

  public static Context getContext() {
    return context;
  }


  public void initAlbum(){
    //设置主题

  }
  /**
   * @param enable enable remote debugger. valid only if host not to be "DEBUG_SERVER_HOST".
   *               true, you can launch a remote debugger and inspector both.
   *               false, you can  just launch a inspector.
   * @param host   the debug server host, must not be "DEBUG_SERVER_HOST", a ip address or domain will be OK.
   *               for example "127.0.0.1".
   */
  private void initDebugEnvironment(boolean connectable,boolean enable, String host) {
    if (!"DEBUG_SERVER_HOST".equals(host)) {
      WXEnvironment.sDebugServerConnectable = connectable;
      WXEnvironment.sRemoteDebugMode = enable;
      WXEnvironment.sRemoteDebugProxyUrl = "ws://" + host + ":8088/debugProxy/native";
    }
  }

  /**
   * 初始化常用对象
   */
  private void init() {

    //xUtil
      x.Ext.init(this);
    //x.Ext.setDebug(false);
      x.Ext.setDebug(BuildConfig.DEBUG);
    MobSDK.init(this, Constant.shareAppId,Constant.shareAppSecret);

  }

  public static BaseActivity getActivity(){
    return activityList.get(activityList.size() - 1);
  }

  //添加Activity 到容器中
  public static void addActivity(BaseActivity activity) {
    activityList.add(activity);
  }

  //遍历所有Activity 并finish
  public static void exit() {
    try {
      for (Activity activity : activityList) {
        if (activity != null)
          activity.finish();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      System.exit(0);
    }
  }

  public static void removeActivity() {
    try {
      for (Activity activity : activityList) {
        if (activity != null){
//          if (activity instanceof VerifyCodeLoginActivity){
//            activityList.remove(activity);
//          }
          activity.finish();
        }

      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // 保存字符串
  public void save(String name, String content) {
    SharedPre.edit().putString(name, content).commit();
  }

  // 读取字符串
  public String get(String name, String defaultName) {
    return SharedPre.getString(name, defaultName);
  }

  private String getAppName(int pID) {
    String processName = null;
    ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
    List l = am.getRunningAppProcesses();
    Iterator i = l.iterator();
    PackageManager pm = this.getPackageManager();
    while (i.hasNext()) {
      ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
      try {
        if (info.pid == pID) {
          processName = info.processName;
          return processName;
        }
      } catch (Exception e) {
      }
    }
    return processName;
  }
}
