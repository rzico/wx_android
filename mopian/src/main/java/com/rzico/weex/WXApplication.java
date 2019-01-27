package com.rzico.weex;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;

import com.google.gson.Gson;
import com.rzico.weex.adapter.ImageAdapter;
import com.rzico.weex.component.amap.component.WXMapMarkerComponent;
import com.rzico.weex.component.amap.component.WXMapViewComponent;
import com.rzico.weex.component.amap.module.WXMapModule;

import com.rzico.weex.component.view.WXImage;
import com.rzico.weex.db.DbUtils;
import com.rzico.weex.db.notidmanager.DbCacheBean;
import com.rzico.weex.model.APPINFO;
import com.rzico.weex.module.AudioModule;
import com.rzico.weex.module.LivePlayerModule;
import com.rzico.weex.module.MapModule;
import com.rzico.weex.module.UposModule;
import com.rzico.weex.module.PhoneModule;
import com.rzico.weex.module.PrintModule;
import com.rzico.weex.module.WXEventModule;
import com.rzico.weex.activity.BaseActivity;
import com.rzico.weex.adapter.WeexHttpAdapter;
import com.rzico.weex.adapter.WeexJSExceptionAdapter;
import com.rzico.weex.adapter.WeexUriAdapter;
import com.rzico.weex.module.AlbumModule;
import com.rzico.weex.net.XRequest;
import com.rzico.weex.utils.AssetsCopyer;
import com.rzico.weex.utils.PhoneUtil;
import com.rzico.weex.utils.chat.Foreground;
import com.taobao.weex.InitConfig;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.common.WXException;
import com.taobao.weex.ui.SimpleComponentHolder;
import com.taobao.weex.ui.component.WXBasicComponentType;

import com.tencent.imsdk.TIMGroupReceiveMessageOpt;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMOfflinePushListener;
import com.tencent.imsdk.TIMOfflinePushNotification;
import com.tencent.qalsdk.sdk.MsfSdkUtils;

import org.xutils.x;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class WXApplication extends Application {

  private static Context context;


  private  static List<BaseActivity> activityList = new LinkedList<BaseActivity>();

  private final String tag = "yundian";

  private static final String CACHE_NAME = "cache_path";

  private static APPINFO appinfo;

  //数据库管理类
  private static org.xutils.DbManager db;

  private static WXApplication instance;

  //用户ID
  private static String uid = "";

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
    com.tencent.qcloud.ui.EmojiManager.init(this);

//    UMConfigure.init(getContext(), "5b3b22b18f4a9d7720000156","Android", UMConfigure.DEVICE_TYPE_PHONE, null);
//    UMConfigure.setLogEnabled(true);
//    MobclickAgent.setScenarioType(getContext(), MobclickAgent.EScenarioType.E_UM_NORMAL);

    context = getApplicationContext();
    if(MsfSdkUtils.isMainProcess(this)) {
      TIMManager.getInstance().setOfflinePushListener(new TIMOfflinePushListener() {
        @Override
        public void handleNotification(TIMOfflinePushNotification notification) {
          if (notification.getGroupReceiveMsgOpt() == TIMGroupReceiveMessageOpt.ReceiveAndNotify){
            //消息被设置为需要提醒
            notification.doNotify(getApplicationContext(), R.mipmap.ic_launcher);
          }
        }
      });
    }

    instance = this;
    init();
    initAlbum();
    initWeex();
    getUid();

  }

  public static String getUid() {
    if(uid == null || uid.equals("")){
      uid = PhoneUtil.getDeviceId(context);
      return uid;
    }else{
      return uid;
    }
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
      WXSDKEngine.registerComponent("weex-amap", WXMapViewComponent.class);
      WXSDKEngine.registerComponent("weex-amap-marker", WXMapMarkerComponent.class);
      WXSDKEngine.registerComponent(
              new SimpleComponentHolder(
                      WXImage.class,
                      new WXImage.Creator()
              ),
              false,
              WXBasicComponentType.IMAGE,
              WXBasicComponentType.IMG
      );

      WXSDKEngine.registerModule("event", WXEventModule.class);
      WXSDKEngine.registerModule("map", MapModule.class);
      WXSDKEngine.registerModule("audio", AudioModule.class);
      WXSDKEngine.registerModule("album", AlbumModule.class);
      WXSDKEngine.registerModule("print", PrintModule.class);
      WXSDKEngine.registerModule("phone", PhoneModule.class);
      WXSDKEngine.registerModule("upos", UposModule.class);
      WXSDKEngine.registerModule("livePlayer", LivePlayerModule.class);
      WXSDKEngine.registerModule("amap", WXMapModule.class);
    } catch (WXException e) {
      e.printStackTrace();
    }

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
        getAppInfo();
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

    public static APPINFO getAppInfo() {
        if (appinfo == null) {
            //解析文件
            String jsonData = AssetsCopyer.getJson("app.json", getContext());
            appinfo = new Gson().fromJson(jsonData, APPINFO.class);
            return appinfo;
        } else {
            return appinfo;
        }
    }
}
