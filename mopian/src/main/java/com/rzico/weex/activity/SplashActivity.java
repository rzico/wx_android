package com.rzico.weex.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
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
import com.karumi.dexter.listener.single.PermissionListener;
import com.rzico.weex.Constant;
import com.rzico.weex.R;
import com.rzico.weex.WXApplication;
import com.rzico.weex.activity.dialog.UpdateDialog;
import com.rzico.weex.db.XDB;
import com.rzico.weex.model.TabBar;
import com.rzico.weex.model.info.Launch;
import com.rzico.weex.model.info.MainUrl;
import com.rzico.weex.module.JSCallBaskManager;
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


import org.greenrobot.eventbus.EventBus;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rzico.weex.Constant.imUserId;
import static com.rzico.weex.utils.task.ZipExtractorTask.ZIPSUCCESS;
import static com.tencent.open.utils.Global.getVersionCode;
import static com.rzico.weex.constant.AllConstant.isClearAll;

public class SplashActivity extends BaseActivity {

    private ProgressBar progress;
    private TextView textview;
    private static final String TAG = "SplashActivity";
    private Handler mHandler = null;

    private String writeResVersion = Constant.resVerison;//默认是 app的资源包

    private WXApplication wxApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 放在setContentView()之前运行
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
//        toCheckPermission();
        wxApplication = (WXApplication) this.getApplicationContext();
        setContentView(R.layout.activity_splash);
        mHandler = new MyHandler(this);
        isClearAll = 0;
        progress = (ProgressBar) findViewById(R.id.progress);
        progress.setVisibility(View.GONE);
        progress.setProgress(0);
        //清除状态栏
        clearNotification();
        //读取 userid
        Constant.userId = SharedUtils.readLoginId();
        //初始化im
        initIM();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * 清楚所有通知栏通知
     */
    private void clearNotification() {
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
//        MiPushClient.clearNotification(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constant.isSetting) {
            Constant.isSetting = false;
            Dexter.withActivity(SplashActivity.this).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            //初始化数据库
                            initDb();
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
    }

    private void initDb() {
        //初始化数据库
        org.xutils.DbManager.DaoConfig daoConfig = XDB.getDaoConfig();
        WXApplication.setDb(x.getDb(daoConfig));
    }

    private void initIM() {
        //登录之前要初始化群和好友关系链缓存
        TIMUserConfig userConfig = new TIMUserConfig();
        userConfig.setUserStatusListener(new TIMUserStatusListener() {
            @Override
            public void onForceOffline() {
                //登出
                new XRequest(SplashActivity.this, Constant.path+"login/logout.jhtml", XRequest.POST, new HashMap<String, Object>()).setOnRequestListener(new HttpRequest.OnRequestListener() {
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
            private void offlineLoginOut() {
                //登出成功
                Constant.loginState = false;
                Constant.userId = 0;
                Constant.imUserId = "";
                SharedUtils.saveLoginId(Constant.userId);
                EventBus.getDefault().post(new com.rzico.weex.model.event.MessageBus(com.rzico.weex.model.event.MessageBus.Type.FORCEOFFLINE));

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
        if (!Utils.isApkDebugable(SplashActivity.this)) {
            showUplodeDialog(data);
        }else {
            updateRes();
        }

    }

    @Override
    public void onNetChange(int netMobile) {
        super.onNetChange(netMobile);
        boolean haveNet = isNetConnect();
//        Toast.makeText(getApplicationContext(), haveNet ? "有网络" : "无网络", Toast.LENGTH_SHORT).show();
        if (haveNet) {
//            destoryWeexInstance();
//            initWeexView();
//            setSelectTab(0);
            if (imUserId.equals("")) {

            }
        }
    }

    private void handlerFrist() {
        //首先判断本地资源包是不是初始化的
        //这里作判断处理
        new XRequest(SplashActivity.this, Constant.path+"common/resources.jhtml").setOnRequestListener(new HttpRequest.OnRequestListener() {
            @Override
            public void onSuccess(BaseActivity activity, String result, String type) {
                try {
                    Launch launch = new Gson().fromJson(result, Launch.class);
                    if (launch != null) {

                        Constant.updateResUrl = launch.getData().getResUrl();
                        Constant.updateAppUrl = launch.getData().getAppUrl();
                        Constant.netResVerison = launch.getData().getResVersion();
                        Constant.key = launch.getData().getKey();
                        Constant.appVerison = launch.getData().getAppVersion();

                        checkVision(launch.getData());

                        //http://cdn.rzico.com/weex/resources/release/res-0.0.1.zip
                    } else {
                        //这里是后台返回的数据 解析出错
                        //无论什么错误 都要能运行 就是把assests里面的数据拷贝到 data里面
//                        Toast.makeText(SplashActivity.this, "包解析出错", Toast.LENGTH_SHORT).show();
                        toNext();
                    }
                } catch (Exception e) {
                    Toast.makeText(SplashActivity.this, "程序出错", Toast.LENGTH_SHORT).show();
                    toNext();
                }
                //获取主页导航路由的路径  导航改成配置文件
//                new XRequest(SplashActivity.this, "weex/common/router.jhtml").setOnRequestListener(new HttpRequest.OnRequestListener() {
//                    @Override
//                    public void onSuccess(BaseActivity activity, String result, String type) {
//                        try {
//
//                            MainUrl mainUrl = new Gson().fromJson(result, MainUrl.class);
//                            Constant.index1 = handleUrl(mainUrl.getData().getTabnav().getHome());
//                            Constant.index2 = handleUrl(mainUrl.getData().getTabnav().getFriend());
//                            Constant.index3 = handleUrl(mainUrl.getData().getTabnav().getMessage());
//                            Constant.index4 = handleUrl(mainUrl.getData().getTabnav().getMember());
//
//                            Constant.center = handleUrl(mainUrl.getData().getTabnav().getAdd());
//
//                            SharedUtils.saveIndex1(Constant.index1);
//                            SharedUtils.saveIndex2(Constant.index2);
//                            SharedUtils.saveIndex3(Constant.index3);
//                            SharedUtils.saveIndex4(Constant.index4);
//                            SharedUtils.saveCenter(Constant.center);
//
//                        } catch (Exception e) {
//                            Toast.makeText(SplashActivity.this, "程序出错", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFail(BaseActivity activity, String cacheData, int code) {
//                        Toast.makeText(SplashActivity.this, "网络加载失败，请检查网络", Toast.LENGTH_SHORT).show();
//                    }
//                }).execute();
            }

            @Override
            public void onFail(BaseActivity activity, String cacheData, int code) {
                Toast.makeText(SplashActivity.this, "网络加载失败，请检查网络", Toast.LENGTH_SHORT).show();
                copylocalfile();
            }
        }).execute();


    }

    private String handleUrl(String url) {

        if (url != null && !url.equals("")) {
            url = url.contains("file:/") ? url.replace("file:/", "") : url;
//          return  url.contains("?") ? url.substring(0 ,url.indexOf("?")) : url;
            return url;
        } else {
            return url;
        }
    }

    private boolean haveUpdate(File file) {
        String[] fileData = file.list();
        for (String string : fileData) {
            if (string.contains("update")) return true;
        }
        return false;
    }

    /**
     * 如果网路请求或下载失败 就讲assets的东西拷贝到项目目录下
     */
    private void copylocalfile() {
        writeResVersion = Constant.resVerison;
        File file = new File(PathUtils.getResPath(SplashActivity.this));//检查有没有资源文件
        if (file.exists() && file.isDirectory()) {
            if (file.list().length > 0 && haveUpdate(file)) {
                //如果有文件 就判断 本地的版本 和  app的版本号
                try {
                    if(Utils.compareVersion(SharedUtils.readResVersion(), Constant.resVerison) >= 0){
                        toNext();
                    }else {
                        //如果app的版本号大 就再做解压

                        AssetsCopyer.releaseAssets(SplashActivity.this, "update.zip", PathUtils.getResPath(SplashActivity.this));
                        //将其解压
                        ZipExtractorTask task = new ZipExtractorTask(PathUtils.getResPath(SplashActivity.this) + "update.zip", PathUtils.getResPath(SplashActivity.this), mContext, true, mHandler);
                        task.execute();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    toNext();
                }
            } else {
                //没文件夹就将asstes的项目拷贝
                AssetsCopyer.releaseAssets(SplashActivity.this, "update.zip", PathUtils.getResPath(SplashActivity.this));
                //将其解压
                ZipExtractorTask task = new ZipExtractorTask(PathUtils.getResPath(SplashActivity.this) + "update.zip", PathUtils.getResPath(SplashActivity.this), mContext, true, mHandler);
                task.execute();
            }
        } else {
            //创建目录
            file.mkdirs();
            //没文件夹就将asstes的项目拷贝
            AssetsCopyer.releaseAssets(SplashActivity.this, "update.zip", PathUtils.getResPath(SplashActivity.this));
            //将其解压
            ZipExtractorTask task = new ZipExtractorTask(PathUtils.getResPath(SplashActivity.this) + "update.zip", PathUtils.getResPath(SplashActivity.this), mContext, true, mHandler);
            task.execute();
        }
    }

    private void downloadFile(final String url, String path) {
        //设置请求参数
        RequestParams requestParams = new RequestParams(url);
        requestParams.setSaveFilePath(path);//这里的path 是直接要给 保存的文件路径 包括文件名后缀名
        x.http().get(requestParams, new Callback.ProgressCallback<File>() {

            @Override
            public void onSuccess(File result) {
                //解压zip
                ZipExtractorTask task = new ZipExtractorTask(PathUtils.getResPath(SplashActivity.this) + "update_net.zip", PathUtils.getResPath(SplashActivity.this), mContext, true, mHandler);
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
                copylocalfile();
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
                progress.setMax((int) total);
                progress.setProgress((int) current);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void toNext() {
        //准备跳转
        SharedUtils.saveResVersion(writeResVersion);

        if(WXApplication.getAppInfo() == null){
           finish();
            return;
        }
        List<TabBar> tabBars = WXApplication.getAppInfo().getTabBar();
        if(tabBars == null || tabBars.size() < 1){

           String  url = WXApplication.getAppInfo().getIndex();
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
                intent.addCategory(Constant.WEEX_CATEGORY);
            } else if (TextUtils.equals("file", scheme)) {
                intent.putExtra("isLocal", "true");
                intent.addCategory(Constant.WEEX_CATEGORY);
            } else {
                intent.addCategory(Constant.WEEX_CATEGORY);
                uri = Uri.parse(new StringBuilder("http:").append(url).toString());
            }
            intent.setData(uri);
            startActivity(intent);
            finish();
        }else {
            Intent intent = new Intent();
            intent.setClass(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }
    static class MyHandler extends Handler{
        WeakReference<SplashActivity> mActivity;
        public MyHandler(SplashActivity activity) {
            mActivity = new WeakReference<SplashActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            SplashActivity mainActivity = mActivity.get();
            if (msg.what == ZIPSUCCESS) {
                mainActivity.toNext();
            } else {
                Toast.makeText(mainActivity, "解压网络资源包失败", Toast.LENGTH_SHORT).show();
//                mainActivity.finish();
                mainActivity.copylocalfile();
            }

        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void showUplodeDialog(final Launch.data versionInfo) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
        // String msg = "\n更新版本为" + version + "\n" + " [更新内容] " + "\n" + updateInfo + "\n是否立即更新?";
        dialog.setTitle("升级");
        final String currentVersion = VersionManagementUtil.getVersion(mContext);
        String appVersion = versionInfo.getAppVersion();
        final String minVersion = versionInfo.getMinVersion();
        if (VersionManagementUtil.VersionComparison(appVersion, currentVersion) == 1) {
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
                    if (VersionManagementUtil.VersionComparison(minVersion, currentVersion) == 1) {//如果低于最低版本 则不让登录
                        WXApplication.exit();
                    }else{
                        updateRes();
                        dialogInterface.dismiss();
                    }

                }
            });
            dialog.setCancelable(false);
            dialog.show();
        } else {
            updateRes();
        }
    }

    private void updateRes() {
        //这里注释掉是为了不用再校验版本
        String nowResVersion = SharedUtils.readResVersion();//现在的资源包版本
        String appResVersion = Constant.resVerison;//app自带资源包版本
        String netResVersion = Constant.netResVerison;//网络的资源包版本
        //  == null 或者 =="" 表示第一次使用， 否者是第二次使用 就判断版本号
        //  if(nowVersion.equals("")){
        //      nowVersion = Constant.resVerison;//设置默认的值
        //  }
        try {
//            为了阿轲测试注释
            if (Utils.isApkDebugable(SplashActivity.this)) {
                downloadFile("http://cdn.rzico.com/weex/resources/release/res-0.0.1.zip" + "?t=" + System.currentTimeMillis(), PathUtils.getResPath(SplashActivity.this) + "update_net.zip");
//                downloadFile("http://cdn.rzico.com/weex/release/res-1.0.1.zip" + "?t=" + System.currentTimeMillis(), PathUtils.getResPath(SplashActivity.this) + "update.zip");
//                toNext();
            } else {
                if(Utils.compareVersion(netResVersion, appResVersion) > 0 && Utils.compareVersion(netResVersion, nowResVersion) > 0){
                    writeResVersion = netResVersion;
                    downloadFile(Constant.updateResUrl + "?t=" + System.currentTimeMillis(), PathUtils.getResPath(SplashActivity.this) + "update_net.zip");
                }else  if(nowResVersion.equals("0.0.0") || (Utils.compareVersion(appResVersion, nowResVersion) > 0 && Utils.compareVersion(appResVersion, netResVersion) > 0)){
                    //如果是app自带的版本好 是最大的 就压缩本地的
                    writeResVersion = appResVersion;
                    copylocalfile();
                } else {
                    //如果现在的资源包版本 是最大的 就什么事情都不做 直接跳转页面
                    writeResVersion = nowResVersion;
                    toNext();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            toNext();
        }
    }

}
