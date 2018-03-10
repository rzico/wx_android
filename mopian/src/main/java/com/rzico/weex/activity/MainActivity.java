package com.rzico.weex.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.huawei.android.pushagent.PushManager;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mob.MobSDK;
import com.rzico.weex.Constant;
import com.rzico.weex.R;
import com.rzico.weex.WXApplication;
import com.rzico.weex.activity.chat.ChatActivity;
import com.rzico.weex.adapter.WeexPageAdapter;
import com.rzico.weex.db.XDB;
import com.rzico.weex.model.LivePlayer2;
import com.rzico.weex.model.LivePlayerBean;
import com.rzico.weex.model.info.Location;
import com.rzico.weex.model.info.LoginBean;
import com.rzico.weex.model.info.Message;
import com.rzico.weex.module.AlbumModule;
import com.rzico.weex.module.JSCallBaskManager;
import com.rzico.weex.module.WXEventModule;
import com.rzico.weex.net.HttpRequest;
import com.rzico.weex.net.XRequest;
import com.rzico.weex.oos.OssService;
import com.rzico.weex.pageview.NoScrollPageView;
import com.rzico.weex.utils.AntiShake;
import com.rzico.weex.utils.AudioUtil;
import com.rzico.weex.utils.BarTextColorUtils;
import com.rzico.weex.utils.BluetoothUtil;
import com.rzico.weex.utils.ESCUtil;
import com.rzico.weex.utils.LoginUtils;
import com.rzico.weex.utils.PathUtils;
import com.rzico.weex.utils.Player;
import com.rzico.weex.utils.SharedUtils;
import com.rzico.weex.utils.chat.PushUtil;
import com.rzico.weex.utils.weex.constants.Constants;
import com.taobao.weex.IWXRenderListener;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXRenderStrategy;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserStatusListener;
import com.tencent.imsdk.ext.message.TIMConversationExt;
import com.tencent.imsdk.ext.message.TIMManagerExt;
import com.tencent.qcloud.presentation.business.InitBusiness;
import com.tencent.qcloud.presentation.event.FriendshipEvent;
import com.tencent.qcloud.presentation.event.GroupEvent;
import com.tencent.qcloud.presentation.event.MessageEvent;
import com.tencent.qcloud.presentation.event.RefreshEvent;

import org.xutils.x;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import static com.rzico.weex.Constant.imUserId;
import static com.yalantis.ucrop.UCrop.REQUEST_CROP;
import static com.yixiang.mopian.constant.AllConstant.isClearAll;


/**
 * Created by Jinlesoft on 2017/9/2.
 */

public class MainActivity extends BaseActivity implements View.OnClickListener, IWXRenderListener {

    private ImageView rgGroupHomeIm, rgGroupFriendIm, rgGroupMsgIm, rgGroupMyIm;
    private TextView rgGroupHomeTv, rgGroupFriendTv, rgGroupMsgTv, rgGroupMyTv;
    private TextView ivMessageDot;
    private LinearLayout rgGroupHome, rgGroupFriend, rgGroupMsg, rgGroupMy, rgGroupAdd;
    private final String TAG = "MainActivity";
    private final String HOME = "weex_home";
    private final String FRIEND = "weex_friend";
    private final String MSG = "weex_msg";
    private final String MY = "weex_my";


    private boolean canReload = true;
    //打开扫描界面请求码
    private int REQUEST_CODE = 0x01;
    //扫描成功返回码
    private int RESULT_OK = 0xA1;
    private PagerAdapter mWeexPageAdapter;

    private WXApplication wxApplication;
    private boolean isfirst = true;
    private boolean isfirstHandle = true;
    private int tab;

//    private FrameLayout mContainer;

    private NoScrollPageView mContainer;

    protected List<View> viewLists;


    private AntiShake antiShake = new AntiShake();

    public static final int LOGINSUCCESS = 99;
    public static final int LOGINERROR = 88;
    public static final int LOGOUT = 77;
    public static final int FORCEOFFLINE = 66;
    public static final int RECEIVEMSG = 55;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wxApplication = (WXApplication) this.getApplicationContext();
        Handler mHandler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                super.handleMessage(msg);
//                Toast.makeText(MainActivity.this, "请求登录结果：" + msg.what , Toast.LENGTH_SHORT).show();
                //预防屏幕抖屏
                if (isfirstHandle) {
                    isfirstHandle = false;
                    return;
                }
                if (msg.what == LOGINSUCCESS) {
                    if (canReload) {
                        destoryWeexInstance();
                        initWeexView();
                        setSelectTab(0);
                    }
                    canReload = true;//恢复默认
                } else if (msg.what == LOGOUT) {
//                    //注销登录
//                    //跳转到首页
                    destoryWeexInstance();
                    initWeexView();
                    setSelectTab(0);
                } else if (msg.what == FORCEOFFLINE) {
                    //被注销了
                    destoryWeexInstance();
                    initWeexView();
                    setSelectTab(0);
                    //弹窗
                    new AlertView("账号异常", "您的账号再另一台设备登录！", null, new String[]{"确定"}, null, MainActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }).show();
                } else if (msg.what == RECEIVEMSG) {
                    setUnRead();
                } else if (msg.what == LOGINERROR) {
                    //被注销了
                    destoryWeexInstance();
                    initWeexView();
                    setSelectTab(0);
                }
            }
        };
        wxApplication.setLoginHandler(mHandler);

        setContentView(R.layout.activity_main);
        //设置高亮标题
        initWeexView();
        initView();
        if (isClearAll == 1) {//说明 被清理掉内存了、 一些初始值 需要重新初始值
            isfirstHandle = false;
//            Toast.makeText(MainActivity.this, "1", Toast.LENGTH_SHORT).show();
            initIM();
            Dexter.withActivity(MainActivity.this).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            //初始化数据库
                            org.xutils.DbManager.DaoConfig daoConfig = XDB.getDaoConfig();
                            WXApplication.setDb(x.getDb(daoConfig));
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
        } else {
//            Toast.makeText(MainActivity.this, "0", Toast.LENGTH_SHORT).show();
        }
    }

    private void initIM() {
        //登录之前要初始化群和好友关系链缓存
        TIMUserConfig userConfig = new TIMUserConfig();
        userConfig.setUserStatusListener(new TIMUserStatusListener() {
            @Override
            public void onForceOffline() {
                //登出
                new XRequest(MainActivity.this, "/weex/login/logout.jhtml", XRequest.POST, new HashMap<String, Object>()).setOnRequestListener(new HttpRequest.OnRequestListener() {
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
                if (wxApplication.getLoginHandler() != null) {
                    wxApplication.getLoginHandler().sendEmptyMessage(MainActivity.FORCEOFFLINE);
                }
            }

            @Override
            public void onUserSigExpired() {
                //票据过期，需要重新登录
                LoginUtils.checkLogin(MainActivity.this, null, null);
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
                canReload = false;
                LoginUtils.checkLogin(MainActivity.this, null, null);
            }
        }
    }

    protected void destoryWeexInstance() {
        if (wxApplication.getWxsdkInstanceMap() != null) {
            for (String key : wxApplication.getWxsdkInstanceMap().keySet()) {
                wxApplication.getWxsdkInstanceMap().get(key).registerRenderListener(null);
                wxApplication.getWxsdkInstanceMap().get(key).destroy();
            }
            wxApplication.getWxsdkInstanceMap().clear();
            wxApplication.setWxsdkInstanceMap(null);
        }
    }


    public long getUnRead() {
        List<TIMConversation> list = TIMManagerExt.getInstance().getConversationList();
        long unRead = 0;
        for (TIMConversation item : list) {
            if (item.getPeer().equals("")) continue;
            TIMConversationExt conExt = new TIMConversationExt(item);
            unRead = unRead + conExt.getUnreadMessageNum();
        }
        return unRead;
    }

    /**
     * 判断小米推送是否已经初始化
     */
    private boolean shouldMiInit(BaseActivity activity) {
        ActivityManager am = ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }


//    @Override
//    protected boolean enableSliding() {
//        return false;
//    }

    /*初始化weexview*/
    private void initWeexView() {
        mContainer = (NoScrollPageView) findViewById(R.id.viewpager_content);
        final Map<String, WXSDKInstance> nowWxsdkInstanceMap = new HashMap<>();
        viewLists = new ArrayList<>();
        WXSDKInstance mWeexInstanceHome = new WXSDKInstance(this);
        mWeexInstanceHome.registerRenderListener(this);
        WXSDKInstance mWeexInstanceFriend = new WXSDKInstance(this);
        mWeexInstanceFriend.registerRenderListener(this);
        WXSDKInstance mWeexInstanceMsg = new WXSDKInstance(this);
        mWeexInstanceMsg.registerRenderListener(this);
        WXSDKInstance mWeexInstanceMy = new WXSDKInstance(this);
        mWeexInstanceMy.registerRenderListener(this);

        nowWxsdkInstanceMap.put(HOME, mWeexInstanceHome);
        nowWxsdkInstanceMap.put(FRIEND, mWeexInstanceFriend);
        nowWxsdkInstanceMap.put(MSG, mWeexInstanceMsg);
        nowWxsdkInstanceMap.put(MY, mWeexInstanceMy);
        wxApplication.setWxsdkInstanceMap(nowWxsdkInstanceMap);

        if (SharedUtils.readIndex1().startsWith("http://")) {//如果是网络url
            Map<String, Object> options = new HashMap<>();
            options.put(WXSDKInstance.BUNDLE_URL, SharedUtils.readIndex1());

            wxApplication.getWxsdkInstanceMap().get(HOME).renderByUrl(HOME, SharedUtils.readIndex1(), options, null, WXRenderStrategy.APPEND_ASYNC);
        } else {
            String url = SharedUtils.readIndex1();

            Map<String, Object> options = new HashMap<>();
            options.put(WXSDKInstance.BUNDLE_URL, url);
            wxApplication.getWxsdkInstanceMap().get(HOME).render(HOME, PathUtils.loadLocal(url, MainActivity.this), options, null, WXRenderStrategy.APPEND_ASYNC);
        }


        if (isfirst) {
            //验证登录
            LoginUtils.checkLogin(MainActivity.this, getIntent(), new LoginUtils.OnLoginListener() {
                @Override
                public void onSuccess(LoginBean loginBean) {
                    setUnRead();
                    if (SharedUtils.readLoginId() != 0) {
                        loadPage();
                    }
                    isfirst = false;
                }

                @Override
                public void onError(LoginUtils.LoginErrorType loginErrorType) {
                    isfirst = false;
                    if (SharedUtils.readLoginId() != 0) { //登录过就加载 没登录过不加载
                        loadPage();
                    }
                    //登录失败 等其他原因登录失败   则跳转登录 页面
//                    Intent intent = new Intent();
//                    intent.setClass(MainActivity.this, LoginActivity.class);
//                    startActivityForResult(intent, LoginActivity.LOGINCODE);
                }
            });
        } else {
//            if(Constant.loginState || Constant.unLinelogin){ //或者是离线登录
            setUnRead();

            if (SharedUtils.readLoginId() != 0) {
                loadPage();
            }
//            }
        }

        mWeexPageAdapter = new WeexPageAdapter(viewLists);
        mContainer.setAdapter(mWeexPageAdapter);
    }

    private void loadPage() {
        if (SharedUtils.readIndex2().startsWith("http://")) {//如果是网络url

            Map<String, Object> options = new HashMap<>();
            options.put(WXSDKInstance.BUNDLE_URL, SharedUtils.readIndex2());
            wxApplication.getWxsdkInstanceMap().get(FRIEND).renderByUrl(FRIEND, SharedUtils.readIndex2(), options, null, WXRenderStrategy.APPEND_ASYNC);
        } else {
            Map<String, Object> options = new HashMap<>();
            options.put(WXSDKInstance.BUNDLE_URL, SharedUtils.readIndex2());
            wxApplication.getWxsdkInstanceMap().get(FRIEND).render(FRIEND, PathUtils.loadLocal(SharedUtils.readIndex2(), MainActivity.this), options, null, WXRenderStrategy.APPEND_ASYNC);
        }
        if (SharedUtils.readIndex3().startsWith("http://")) {//如果是网络url

            Map<String, Object> options = new HashMap<>();
            options.put(WXSDKInstance.BUNDLE_URL, SharedUtils.readIndex3());
            wxApplication.getWxsdkInstanceMap().get(MSG).renderByUrl(MSG, SharedUtils.readIndex3(), options, null, WXRenderStrategy.APPEND_ASYNC);
        } else {
            Map<String, Object> options = new HashMap<>();
            options.put(WXSDKInstance.BUNDLE_URL, SharedUtils.readIndex3());
            wxApplication.getWxsdkInstanceMap().get(MSG).render(MSG, PathUtils.loadLocal(SharedUtils.readIndex3(), MainActivity.this), options, null, WXRenderStrategy.APPEND_ASYNC);
        }
        if (SharedUtils.readIndex4().startsWith("http://")) {//如果是网络url

            Map<String, Object> options = new HashMap<>();
            options.put(WXSDKInstance.BUNDLE_URL, SharedUtils.readIndex4());
            wxApplication.getWxsdkInstanceMap().get(MY).renderByUrl(MY, SharedUtils.readIndex4(), options, null, WXRenderStrategy.APPEND_ASYNC);
        } else {
            Map<String, Object> options = new HashMap<>();
            options.put(WXSDKInstance.BUNDLE_URL, SharedUtils.readIndex4());
            wxApplication.getWxsdkInstanceMap().get(MY).render(MY, PathUtils.loadLocal(SharedUtils.readIndex4(), MainActivity.this), options, null, WXRenderStrategy.APPEND_ASYNC);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);       //强制为横屏
        setUnRead();
        PushUtil.getInstance().reset();

    }

    public void setUnRead() {
        //未读数
        long unRead = getUnRead();
        if (unRead > 0) {
            ivMessageDot.setVisibility(View.VISIBLE);
            ivMessageDot.setText(String.valueOf(unRead));
            if (unRead > 99) {
                ivMessageDot.setText("···");
            }
        } else {
            ivMessageDot.setVisibility(View.INVISIBLE);
        }
    }

    public void initView() {

        rgGroupHomeIm = (ImageView) findViewById(R.id.rg_group_home_im);
        rgGroupFriendIm = (ImageView) findViewById(R.id.rg_group_vip_im);
        rgGroupMsgIm = (ImageView) findViewById(R.id.rg_group_huihua_im);
        rgGroupMyIm = (ImageView) findViewById(R.id.rg_group_me_im);
        ivMessageDot = (TextView) findViewById(R.id.iv_message_dot);
        rgGroupHomeTv = (TextView) findViewById(R.id.rg_group_home_tv);
        rgGroupFriendTv = (TextView) findViewById(R.id.rg_group_vip_tv);
        rgGroupMsgTv = (TextView) findViewById(R.id.rg_group_huihua_tv);
        rgGroupMyTv = (TextView) findViewById(R.id.rg_group_me_tv);

        rgGroupHome = (LinearLayout) findViewById(R.id.rg_group_home);
        rgGroupFriend = (LinearLayout) findViewById(R.id.rg_group_vip);
        rgGroupMsg = (LinearLayout) findViewById(R.id.rg_group_huihua);
        rgGroupMy = (LinearLayout) findViewById(R.id.rg_group_me);
        rgGroupAdd = (LinearLayout) findViewById(R.id.rg_group_yingxiao);


        rgGroupHomeIm.setImageResource(R.mipmap.ico_home);
        rgGroupFriendIm.setImageResource(R.mipmap.ico_friend);
        rgGroupMsgIm.setImageResource(R.mipmap.ico_msg);
        rgGroupMyIm.setImageResource(R.mipmap.ico_my);
        initEvent();
        //默认首页
        setSelectTab(0);
    }

    private void initEvent() {
        rgGroupHome.setOnClickListener(this);
        rgGroupFriend.setOnClickListener(this);
        rgGroupMsg.setOnClickListener(this);
        rgGroupAdd.setOnClickListener(this);
        rgGroupMy.setOnClickListener(this);
    }

    private void setBottonChange(int page) {
        switch (page) {
            case 0:
                BarTextColorUtils.StatusBarLightMode(MainActivity.this, false);
                rgGroupHomeIm.setImageResource(R.mipmap.ico_home_focus);
                rgGroupFriendIm.setImageResource(R.mipmap.ico_friend);
                rgGroupMsgIm.setImageResource(R.mipmap.ico_msg);
                rgGroupMyIm.setImageResource(R.mipmap.ico_my);

                rgGroupHomeTv.setTextColor(getResources().getColor(R.color.wxColor));
                rgGroupFriendTv.setTextColor(getResources().getColor(R.color.text_default));
                rgGroupMsgTv.setTextColor(getResources().getColor(R.color.text_default));
                rgGroupMyTv.setTextColor(getResources().getColor(R.color.text_default));
                break;
            case 1:
                rgGroupHomeIm.setImageResource(R.mipmap.ico_home);
                rgGroupFriendIm.setImageResource(R.mipmap.ico_friend_focus);
                rgGroupMsgIm.setImageResource(R.mipmap.ico_msg);
                rgGroupMyIm.setImageResource(R.mipmap.ico_my);

                rgGroupHomeTv.setTextColor(getResources().getColor(R.color.text_default));
                rgGroupFriendTv.setTextColor(getResources().getColor(R.color.wxColor));
                rgGroupMsgTv.setTextColor(getResources().getColor(R.color.text_default));
                rgGroupMyTv.setTextColor(getResources().getColor(R.color.text_default));
                break;
            case 2:
                rgGroupHomeIm.setImageResource(R.mipmap.ico_home);
                rgGroupFriendIm.setImageResource(R.mipmap.ico_friend);
                rgGroupMsgIm.setImageResource(R.mipmap.ico_msg_focus);
                rgGroupMyIm.setImageResource(R.mipmap.ico_my);

                rgGroupHomeTv.setTextColor(getResources().getColor(R.color.text_default));
                rgGroupFriendTv.setTextColor(getResources().getColor(R.color.text_default));
                rgGroupMsgTv.setTextColor(getResources().getColor(R.color.wxColor));
                rgGroupMyTv.setTextColor(getResources().getColor(R.color.text_default));
                break;
            case 3:
                rgGroupHomeIm.setImageResource(R.mipmap.ico_home);
                rgGroupFriendIm.setImageResource(R.mipmap.ico_friend);
                rgGroupMsgIm.setImageResource(R.mipmap.ico_msg);
                rgGroupMyIm.setImageResource(R.mipmap.ico_my_focus);

                rgGroupHomeTv.setTextColor(getResources().getColor(R.color.text_default));
                rgGroupFriendTv.setTextColor(getResources().getColor(R.color.text_default));
                rgGroupMsgTv.setTextColor(getResources().getColor(R.color.text_default));
                rgGroupMyTv.setTextColor(getResources().getColor(R.color.wxColor));
                break;
        }

    }

    @Override
    public void onClick(View view) {
        if (antiShake.check(view.getId())) return;//防抖动
        switch (view.getId()) {
            case R.id.rg_group_home:
//                new XRequest(MainActivity.this, "weex/member/nihtan/view.jhtml").setOnRequestListener(new HttpRequest.OnRequestListener() {
//                    @Override
//                    public void onSuccess(BaseActivity activity, String result, String type) {
//
//                        LivePlayer2 entity= new Gson().fromJson(result, LivePlayer2.class);
//                        String url = entity.getData().getUrl();
////                String url = "";
//                        String video = entity.getData().getVideo();
//                        String method = "POST";
//                        LivePlayerBean livePlayerBean = new LivePlayerBean();
//                        livePlayerBean.setUrl(url);
//                        livePlayerBean.setVideo(video);
//                        livePlayerBean.setMethod(method);
//                        String key = System.currentTimeMillis() + "";
//
//                        Intent intent2 = new Intent(MainActivity.this, LivePlayerActivity.class);
//                        intent2.putExtra("livePlayerParam",  livePlayerBean);
//                        intent2.putExtra("key", key);
//                        startActivity(intent2);
//                    }
//
//                    @Override
//                    public void onFail(BaseActivity activity, String cacheData, int code) {
//
//                    }
//                }).execute();


                setSelectTab(0);
                break;
            case R.id.rg_group_vip:
                setSelectTab(1);//如果登录 就直接切换
                break;
            case R.id.rg_group_huihua:
                setSelectTab(2);
                break;
            case R.id.rg_group_yingxiao:
//                Intent intent = new Intent(MainActivity.this, EditActivity.class);

                Intent intent = new Intent();
                Uri uri = Uri.parse("file://" + Constant.center);
                String scheme = uri.getScheme();
                if (TextUtils.equals("tel", scheme)) {

                } else if (TextUtils.equals("sms", scheme)) {

                } else if (TextUtils.equals("mailto", scheme)) {

                } else if (TextUtils.equals("http", scheme) ||
                        TextUtils.equals("https",
                                scheme)) {
                    intent.putExtra("isLocal", "false");
                    intent.setClass(MainActivity.this, RouterActivity.class);
                } else if (TextUtils.equals("file", scheme)) {
                    intent.putExtra("isLocal", "true");
                    intent.setClass(MainActivity.this, RouterActivity.class);
                } else {
                    intent.setClass(MainActivity.this, RouterActivity.class);
                    uri = Uri.parse(new StringBuilder("http:").append("file://" + Constant.center).toString());
                }
                intent.setData(uri);
                                startActivity(intent);
                break;
            case R.id.rg_group_me:
                setSelectTab(3);
                break;
        }
    }

    public void setSelectTab(int page) {
        if (page > 0 && SharedUtils.readLoginId() == 0) {//没有登录过
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, LoginActivity.LOGINCODE);
            return;
        }
//        if (page != 0) {
//            BarTextColorUtils.StatusBarLightMode(this, false);
//        } else {
//            BarTextColorUtils.StatusBarLightMode(this, true);
//        }

        mContainer.setCurrentItem(page);
        //设置底部图标
        setBottonChange(page);
    }

    private boolean haveSysMsg = false;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //该activity已经存在的时候被 new intent
        String identify = "";
        TIMConversationType type = null;
        if (intent != null) {
            identify = intent.getStringExtra("identify");
            type = (TIMConversationType) intent.getSerializableExtra("type");
            if (identify != null && !identify.equals("") && type != null) {
                if (identify.startsWith("gm")) {
//                    haveSysMsg = true;
                    setSelectTab(2);//跳转消息页面
                } else {
                    ChatActivity.navToChat(MainActivity.this, identify, TIMConversationType.C2C);
                }
            }
            String scheme = "";
            scheme = intent.getScheme();
            Uri uri = null;
            uri = intent.getData();
            //如果有scheme 代表伪链接
            if (uri != null && scheme != null && scheme.startsWith(getResources().getString(R.string.href_home))) {//用startswith是因为不确定是不是完全跟href_home一样 说不定后面还有参数
                String allUrl = uri.toString();
                String deleteScheme = allUrl.replace(getResources().getString(R.string.href_home) + "://", "");
                if (!deleteScheme.equals("") && deleteScheme.length() > 0 && deleteScheme.contains("?") && deleteScheme.contains("=")) {
                    String[] one = deleteScheme.split("\\?");//问号需要加双传意符
                    String[] two = one[1].split("=");
                    String url = "";
                    if (two != null && two.length == 2) {
                        if (one != null && one.length == 2) {
                            if (one[0].equals("article")) {
                                // 文章 file://view/article/preview.js?articleId=%@&publish=true
                                url = "file://view/article/preview.js?articleId=" + two[1] + "&publish=true";
                            } else if (one[0].equals("topic")) {
                                // 专栏 file://view/topic/author.js?id=%@
                                url = "file://view/topic/author.js?id=" + two[1];
                            }
                        }
                    }

                    if (!url.equals("")) {
                        Intent openScheme = new Intent();
                        String key = String.valueOf(System.currentTimeMillis());
                        if (url.startsWith("file://")) {
                            openScheme.putExtra("isLocal", "true");
                            openScheme.putExtra("key", key);
                            openScheme.addCategory(Constant.WEEX_CATEGORY);
                            openScheme.setData(Uri.parse(url));
                            startActivity(openScheme);
                        }
                    }
                }

            }
        }

    }

    @Override
    public void onViewCreated(WXSDKInstance instance, View view) {
        viewLists.add(view);
        if (mWeexPageAdapter != null) {
            mWeexPageAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRenderSuccess(WXSDKInstance instance, int width, int height) {

    }

    @Override
    public void onRefreshSuccess(WXSDKInstance instance, int width, int height) {

    }

    @Override
    public void onException(WXSDKInstance instance, String errCode, String msg) {
        //错误页面
//        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
//        viewLists.add(inflater.inflate(R.layout.error_notfind, null));
//        if(mWeexPageAdapter != null){
//            mWeexPageAdapter.notifyDataSetChanged();
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CROP) {//而代表请求裁剪
            AlbumModule.get().onActivityResult(requestCode, resultCode, data);
        } else {
            //扫描返回
            WXEventModule.get().onActivityResult(requestCode, resultCode, data);
        }
        //这里是跳转扫描的页面
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        if (result != null) {
//            if (result.getContents() == null) {
//                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
//            } else {
//                handleDecodeInternally(result.getContents());
//            }
//        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // Put up our own UI for how to handle the decoded contents.
    private void handleDecodeInternally(String code) {

        if (!TextUtils.isEmpty(code)) {
            Uri uri = Uri.parse(code);
            if (uri.getQueryParameterNames().contains("bundle")) {
                WXEnvironment.sDynamicMode = uri.getBooleanQueryParameter("debug", false);
                WXEnvironment.sDynamicUrl = uri.getQueryParameter("bundle");
                String tip = WXEnvironment.sDynamicMode ? "Has switched to Dynamic Mode" : "Has switched to Normal Mode";
                Toast.makeText(this, tip, Toast.LENGTH_SHORT).show();
                finish();
                return;
            } else if (uri.getQueryParameterNames().contains("_wx_devtool")) {
                WXEnvironment.sRemoteDebugProxyUrl = uri.getQueryParameter("_wx_devtool");
                WXEnvironment.sDebugServerConnectable = true;
                WXSDKEngine.reload();
                Toast.makeText(this, "devtool", Toast.LENGTH_SHORT).show();
                return;
            } else if (code.contains("_wx_debug")) {
                uri = Uri.parse(code);
                String debug_url = uri.getQueryParameter("_wx_debug");
                WXSDKEngine.switchDebugModel(true, debug_url);
                finish();
            } else {
                Toast.makeText(this, code, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Constants.ACTION_OPEN_URL);
                intent.setPackage(getPackageName());
                intent.setData(Uri.parse(code));
                startActivity(intent);
            }
        }
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
//            onBackPressed();
//            Intent home = new Intent(Intent.ACTION_MAIN);
//            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            home.addCategory(Intent.CATEGORY_HOME);
//            startActivity(home);
        }
    }
}
