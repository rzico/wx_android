package com.rzico.weex.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.rzico.weex.Constant;
import com.rzico.weex.R;
import com.rzico.weex.WXApplication;
import com.rzico.weex.activity.dialog.UpdateDialog;
import com.rzico.weex.db.XDB;
import com.rzico.weex.model.info.Launch;
import com.rzico.weex.model.info.MainUrl;
import com.rzico.weex.net.HttpRequest;
import com.rzico.weex.net.XRequest;
import com.rzico.weex.utils.AssetsCopyer;
import com.rzico.weex.utils.LoginUtils;
import com.rzico.weex.utils.PathUtils;
import com.rzico.weex.utils.SharedUtils;
import com.rzico.weex.utils.Utils;
import com.rzico.weex.utils.VersionManagementUtil;
import com.rzico.weex.utils.task.ZipExtractorTask;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserStatusListener;
import com.tencent.qcloud.presentation.business.InitBusiness;
import com.tencent.qcloud.presentation.event.FriendshipEvent;
import com.tencent.qcloud.presentation.event.GroupEvent;
import com.tencent.qcloud.presentation.event.MessageEvent;
import com.tencent.qcloud.presentation.event.RefreshEvent;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.rzico.weex.Constant.RESVERSION;
import static com.rzico.weex.Constant.imUserId;
import static com.rzico.weex.utils.task.ZipExtractorTask.ZIPSUCCESS;
import static com.tencent.open.utils.Global.getVersionCode;

public class SplashActivity extends BaseActivity {

  private ProgressBar progress;
  private TextView textview;
  private static final  String TAG = "SplashActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
    progress = (ProgressBar) findViewById(R.id.progress);
    progress.setVisibility(View.GONE);
    progress.setProgress(0);
    clearNotification();
    Constant.userId = SharedUtils.readLoginId();
    initDb();
    initIM();
    Dexter.withActivity(SplashActivity.this).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(new com.karumi.dexter.listener.single.PermissionListener() {
              @Override
              public void onPermissionGranted(PermissionGrantedResponse response) {
                handlerFrist();
                SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
                int loglvl = pref.getInt("loglvl", TIMLogLevel.DEBUG.ordinal());
                InitBusiness.start(getApplicationContext(), loglvl);
              }

              @Override
              public void onPermissionDenied(PermissionDeniedResponse response) {
                showDeniedDialog("需要存储权限,才能运行程序");
              }

              @Override
              public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
                                                             PermissionToken token) {
                //用户不允许权限，向用户解释权限左右
                token.continuePermissionRequest();
              }
            }).check();

  }

  private void initDb() {
    Dexter.withActivity(this).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(new com.karumi.dexter.listener.single.PermissionListener() {
              @Override
              public void onPermissionGranted(PermissionGrantedResponse response) {
                //初始化数据库
                org.xutils.DbManager.DaoConfig daoConfig = XDB.getDaoConfig();
                WXApplication.setDb(x.getDb(daoConfig));

              }

              @Override
              public void onPermissionDenied(PermissionDeniedResponse response) {
                showDeniedDialog("需要存储权限");
              }

              @Override
              public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
                                                             PermissionToken token) {
                //用户不允许权限，向用户解释权限左右
                token.continuePermissionRequest();
              }
            }).check();
  }

  private void initIM() {
    //登录之前要初始化群和好友关系链缓存
    TIMUserConfig userConfig = new TIMUserConfig();
    userConfig.setUserStatusListener(new TIMUserStatusListener() {
      @Override
      public void onForceOffline() {
        //登出
        new XRequest(SplashActivity.this, "/weex/login/logout.jhtml", XRequest.POST, new HashMap<String, Object>()).setOnRequestListener(new HttpRequest.OnRequestListener() {
          @Override
          public void onSuccess(BaseActivity activity, String result, String type) {
            TIMManager.getInstance().logout(new TIMCallBack() {
              @Override
              public void onError(int code, String desc) {
                offlineLoginOut();
              }
              @Override
              public void onSuccess() {
                offlineLoginOut();
              }
            });
          }

          @Override
          public void onFail(BaseActivity activity, String cacheData, int code) {
            offlineLoginOut();
          }
        }).execute();
      }

      //被踢了
      private void offlineLoginOut(){
        //登出成功
        Constant.loginState = false;
        Constant.userId = 0;
        Constant.imUserId = "";
        SharedUtils.saveLoginId(Constant.userId);
        if(Constant.loginHandler!=null){
          Constant.loginHandler.sendEmptyMessage(MainActivity.FORCEOFFLINE);
        }
      }

      @Override
      public void onUserSigExpired() {
        //票据过期，需要重新登录
          LoginUtils.checkLogin(SplashActivity.this, null, null);
      }
    })
            .setConnectionListener(new TIMConnListener() {
              @Override
              public void onConnected() {
                android.util.Log.i(TAG, "onConnected");
              }

              @Override
              public void onDisconnected(int code, String desc) {
                android.util.Log.i(TAG, "onDisconnected");
              }

              @Override
              public void onWifiNeedAuth(String name) {
                android.util.Log.i(TAG, "onWifiNeedAuth");
              }
            });

    //设置刷新监听
    RefreshEvent.getInstance().init(userConfig);
    userConfig = FriendshipEvent.getInstance().init(userConfig);
    userConfig = GroupEvent.getInstance().init(userConfig);
    userConfig = MessageEvent.getInstance().init(userConfig);
    TIMManager.getInstance().setUserConfig(userConfig);
  }

  private void checkVision(Launch.data data) {
    showUplodeDialog(data);

  }

  @Override
  public void onNetChange(int netMobile) {
    super.onNetChange(netMobile);
    boolean haveNet = isNetConnect();
//        Toast.makeText(getApplicationContext(), haveNet ? "有网络" : "无网络", Toast.LENGTH_SHORT).show();
    if(haveNet){
//            destoryWeexInstance();
//            initWeexView();
//            setSelectTab(0);
      if(imUserId.equals("")){
      }
    }
  }

  private void handlerFrist() {
    //这里作判断处理
    new XRequest(SplashActivity.this, "weex/common/resources.jhtml").setOnRequestListener(new HttpRequest.OnRequestListener() {
      @Override
      public void onSuccess(BaseActivity activity, String result, String type) {
        try{
          Launch  launch = new Gson().fromJson(result, Launch.class);
          if(launch != null){

            Constant.updateResUrl  = launch.getData().getResUrl();
            Constant.updateAppUrl  = launch.getData().getAppUrl();
            Constant.resVerison    = launch.getData().getResVersion();
            Constant.key        = launch.getData().getKey();
            launch.getData().getAppVersion();

            checkVision(launch.getData());

            //http://cdn.rzico.com/weex/resources/release/res-0.0.1.zip
          }else{
            //这里是后台返回的数据 解析出错
            //无论什么错误 都要能运行 就是把assests里面的数据拷贝到 data里面
            Toast.makeText(SplashActivity.this, "包解析出错", Toast.LENGTH_SHORT).show();
          }
        }catch (Exception e){
          Toast.makeText(SplashActivity.this, "程序出错", Toast.LENGTH_SHORT).show();
        }
      }

      @Override
      public void onFail(BaseActivity activity, String cacheData, int code) {
        Toast.makeText(SplashActivity.this, "网络加载失败，请检查网络", Toast.LENGTH_SHORT).show();
        copylocalfile();
      }
    }).execute();

    //获取主页导航路由的路径
    new XRequest(SplashActivity.this, "weex/common/router.jhtml").setOnRequestListener(new HttpRequest.OnRequestListener() {
      @Override
      public void onSuccess(BaseActivity activity, String result, String type) {
        try{

          MainUrl mainUrl = new Gson().fromJson(result, MainUrl.class);

          Constant.index1 = mainUrl.getData().getTabnav().getHome();
          Constant.index2 = mainUrl.getData().getTabnav().getFriend();
          Constant.index3 = mainUrl.getData().getTabnav().getMessage();
          Constant.index4 = mainUrl.getData().getTabnav().getMember();

          Constant.center = mainUrl.getData().getTabnav().getAdd();

        }catch (Exception e){
          Toast.makeText(SplashActivity.this, "程序出错", Toast.LENGTH_SHORT).show();
        }
      }
      @Override
      public void onFail(BaseActivity activity, String cacheData, int code) {
        Toast.makeText(SplashActivity.this, "网络加载失败，请检查网络", Toast.LENGTH_SHORT).show();
      }
    }).execute();
  }

  private boolean haveUpdate(File file){
    String[] fileData = file.list();
    for (String string : fileData){
      if(string.contains("update"))return true;
    }
    return false;
  }
  /**
   * 如果网路请求或下载失败 就讲assets的东西拷贝到项目目录下
   */
  private void copylocalfile(){
    File file = new File(PathUtils.getResPath());//检查有没有资源文件
    if (file.exists() && file.isDirectory()) {
      if(file.list().length > 0 && haveUpdate(file)) {
        //Not empty, do something here.
        toNext();
      }else{
        //没文件夹就将asstes的项目拷贝
        AssetsCopyer.releaseAssets(SplashActivity.this, "update.zip",PathUtils.getResPath());
        //将其解压
        ZipExtractorTask task = new ZipExtractorTask(PathUtils.getResPath() + "update.zip",PathUtils.getResPath() , mContext, true, mHandler);
        task.execute();
      }
    }else{
      //创建目录
      file.mkdirs();
      //没文件夹就将asstes的项目拷贝
      AssetsCopyer.releaseAssets(SplashActivity.this, "update.zip",PathUtils.getResPath());
      //将其解压
      ZipExtractorTask task = new ZipExtractorTask(PathUtils.getResPath() + "update.zip",PathUtils.getResPath() , mContext, true, mHandler);
      task.execute();
    }
  }
  private void downloadFile(final String url, String path) {
    //设置请求参数
    RequestParams requestParams = new RequestParams(url);
    requestParams.setSaveFilePath(path);//这里的path 是直接要给 保存的文件路径 包括文件名后缀名
    x.http().get(requestParams, new Callback.ProgressCallback<File>(){

      @Override
      public void onSuccess(File result) {
//        Toast.makeText(SplashActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
        SharedUtils.save(RESVERSION, Constant.resVerison);//写入现在下载完成的版本
        //解压zip
        ZipExtractorTask task = new ZipExtractorTask(PathUtils.getResPath() + "update.zip",PathUtils.getResPath() , mContext, true, mHandler);
        task.execute();
      }

      @Override
      public void onError(Throwable ex, boolean isOnCallback) {
        System.out.println("下载失败");
        copylocalfile();
      }

      @Override
      public void onCancelled(CancelledException cex) {
        System.out.println("下载取消");
      }

      @Override
      public void onFinished() {

      }

      @Override
      public void onWaiting() {

      }

      @Override
      public void onStarted() {
        progress.setVisibility(View.VISIBLE);
      }

      @Override
      public void onLoading(long total, long current, boolean isDownloading) {
        progress.setMax( ( int ) total );
        progress.setProgress( ( int ) current );
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

  }

  private void toNext(){
    //准备跳转
    Intent intent = new Intent();
    intent.setClass(SplashActivity.this, MainActivity.class);
    startActivity(intent);
    finish();

  }
  Handler mHandler = new Handler(){
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      if(msg.what == ZIPSUCCESS){
        toNext();
      }else{
        Toast.makeText(SplashActivity.this, "解压失败", Toast.LENGTH_SHORT).show();
      }
    }
  };


  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  /**
   * 清楚所有通知栏通知
   */
  private void clearNotification(){
    NotificationManager notificationManager = (NotificationManager) this
            .getSystemService(NOTIFICATION_SERVICE);
    notificationManager.cancelAll();
    MiPushClient.clearNotification(getApplicationContext());
  }

  private void showUplodeDialog(final Launch.data versionInfo) {
    final AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
    // String msg = "\n更新版本为" + version + "\n" + " [更新内容] " + "\n" + updateInfo + "\n是否立即更新?";
    dialog.setTitle("升级");
    final String currentVersion = VersionManagementUtil.getVersion(mContext);
    String appVersion = versionInfo.getAppVersion();
    final String minVersion = versionInfo.getMinVersion();
    if (VersionManagementUtil.VersionComparison(appVersion,currentVersion) == 1) {
      dialog.setMessage("有新版本了,如果不更新,有些优惠功能将无法使用,程序将退出");
      dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        new UpdateDialog(SplashActivity.this, versionInfo, new UpdateDialog
                .CloseListener() {
          @Override
          public void close() {
            showToast("close");
            updateRes();
          }
        }).show();
        dialogInterface.dismiss();
      }
      }).setNegativeButton("否", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        if (VersionManagementUtil.VersionComparison(minVersion,currentVersion) == 1) {//如果低于最低版本 则不让登录
          WXApplication.exit();
        }
        updateRes();
        dialogInterface.dismiss();

      }
    });
    dialog.setCancelable(false);
    dialog.show();
    }else {
      updateRes();
    }
  }
  private void updateRes(){
    //这里注释掉是为了不用再校验版本
//            String nowVersion = SharedUtils.read(RESVERSION);
    // == null 或者 =="" 表示第一次使用， 否者是第二次使用 就判断版本号
//            if(nowVersion == null || nowVersion.equals("") || Utils.compareVersion(Constant.resVersion, nowVersion) > 0){
    //下载weex资源
    downloadFile(Constant.updateResUrl + "?t=" + System.currentTimeMillis(), PathUtils.getResPath() + "update.zip");

//            }else{
//              toNext();
//            }
  }
}
