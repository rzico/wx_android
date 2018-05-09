package com.rzico.weex.zhibo.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.rzico.weex.R;
import com.rzico.weex.activity.BaseActivity;
import com.rzico.weex.model.event.MessageBus;
import com.rzico.weex.model.info.BasePage;
import com.rzico.weex.model.info.NoticeInfo;
import com.rzico.weex.model.zhibo.LiveGiftBean;
import com.rzico.weex.model.zhibo.LiveRoomBean;
import com.rzico.weex.model.zhibo.UserBean;
import com.rzico.weex.module.AlbumModule;
import com.rzico.weex.module.JSCallBaskManager;
import com.rzico.weex.net.HttpRequest;
import com.rzico.weex.net.XRequest;
import com.rzico.weex.utils.SharedUtils;
import com.rzico.weex.utils.UploadToAli;
import com.rzico.weex.zhibo.activity.utils.BaseRoom;
import com.rzico.weex.zhibo.activity.utils.LiveRoom;
import com.rzico.weex.zhibo.adapter.ChatListAdapter;
import com.rzico.weex.zhibo.view.BeautySettingPannel;
import com.rzico.weex.zhibo.view.ChatListView;
import com.rzico.weex.zhibo.view.CircleImageView;
import com.rzico.weex.zhibo.view.GifView;
import com.rzico.weex.zhibo.view.MagicTextView;
import com.squareup.picasso.Picasso;
import com.taobao.weex.bridge.JSCallback;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMGroupSystemElem;
import com.tencent.imsdk.TIMGroupSystemElemType;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.group.TIMGroupManagerExt;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.yalantis.ucrop.model.AspectRatio;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.bean.MediaBean;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.rzico.weex.model.event.MessageBus.Type.ZHIBOCHAT;
import static com.rzico.weex.utils.photo.PhotoHandle.REQUEST_PHOTOHANDLER;
import static com.rzico.weex.zhibo.activity.utils.BaseRoom.ISFOLLOW;
import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;
import static com.yalantis.ucrop.UCrop.REQUEST_CROP;

public class OpenVideoActivity extends BaseActivity implements BeautySettingPannel.IOnBeautyParamsChangeListener {


    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private static final int UPDAT_WALL_TIME_TIMER_TASK = 10;
    public static final int INVISIBLE = 132;
    public static final int UPLOADSUCCESS = 200;
    public static final int UPLOADERROR = 201;
    private boolean mIsFlashOpened = false;
//initView
    private LinearLayout fengmian_ll01;//设置封面
    private LinearLayout ll_giftlist; //直播列表
    private RelativeLayout fengmian_ll02;
    private LiveRoom liveRoom;
    private EditText title_zhibo;//直播标题
    private TextView tiaoli_tv;//条款
    private CheckBox checkbox;//是否勾选条款
    private TextView zhibo_tv;//直播下一步
    private LinearLayout zhibo_ll;
    private TextView zhibo_start;
    private TextView live_time;//直播时间
    private TextView zb_time;//直播时长
    private TextView close;
    private VideoTimerTask mVideoTimerTask;//计时器
    private Timer mVideoTimer;
    private ChatListView chat_listview;

    private CircleImageView head_icon;

    private int                                             mBeautyLevel        = 5;
    private int                                             mWhiteningLevel     = 3;
    private int                                             mRuddyLevel         = 2;
    private int                                             mBeautyStyle        = TXLiveConstants.BEAUTY_STYLE_SMOOTH;

    private LinearLayout zhibo_jiesu_ll;//直播结束页面
    private TextView tv_fs;//结束 粉丝数
    private TextView tv_gz;//结束 关注数
    private TextView js_tv;//结束 返回首页
    private TextView gift_count;//礼物数量
    private TextView tv_nickname;//主播昵称

    private TextView room_count;//观看数量

    private Long roomCount = 1L;
    private Long giftCount = 0L;

    private boolean isPlay = false;//是否跳过直播页面

//    直播底部
    private TextView beauty_tv01;//美颜
    private BeautySettingPannel mBeautyPannelView;
    private TextView beauty_tv03;

    private TextView beauty_tv04;
    private TXCloudVideoView mCaptureView;//直播页面
    private ImageView img_fengmian;//封面

    private TextView fs_count;

    private ChatListAdapter chatListAdapter;
//
//   动画

    private TranslateAnimation livestartAnim;
    private boolean isUpdateHeadImg = false;//是否选择了图片
    private boolean mRecording = false;//是否开始直播了
    private Handler mHandler;
    private String frontcover = "";//封面地址

    private LiveGiftBean liveGiftBean;

    /**
     * 刷礼物
     */
    private LinearLayout llgiftcontent;
    /**
     * 动画相关
     */
    private NumAnim giftNumAnim;
    private TranslateAnimation inAnim;
    private TranslateAnimation outAnim;
    private Timer timer;

    private boolean isLubo = false;

    private GifView bigivgift;//送礼物时显示的最大数

    //当前用户信息
    private UserBean userBean;

    private String username = "";
    private String userpic  = "";


    private HashMap<String, Object> playParams = new HashMap<>();
    /**
     * 数据相关
     */
    private List<View> giftViewCollection = new ArrayList<View>();
//    private int[] liwu_gif = {R.raw.gg1,R.raw.gg2,R.raw.gg3,R.raw.gg4,R.raw.gg5,R.raw.gg6,R.raw.gg7,R.raw.gg8};
//    private int[] liwu_money = {1, 3, 5, 10, 20, 30, 50, 100};

    private String key;

    private String title;


    //弹幕
    private DanmakuView danmaku_view;
    private DanmakuContext danmakuContext;
    private boolean showDanmaku = false;

    private BaseDanmakuParser parser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setStatusBarFullTransparent();
        setContentView(R.layout.camera_activity);
        EventBus.getDefault().register(this);
        key = getIntent().getStringExtra("key");
        initView();
        initLive();
    }

    private void initView() {
        mHandler = new MyHandler(this);
        fengmian_ll01 = (LinearLayout) findViewById(R.id.fengmian_ll01);
        fengmian_ll02 = (RelativeLayout) findViewById(R.id.fengmian_ll02);
        ll_giftlist = (LinearLayout) findViewById(R.id.ll_giftlist);

        img_fengmian = (ImageView) findViewById(R.id.img_fengmian);
        title_zhibo = (EditText) findViewById(R.id.title_zhibo);
        tiaoli_tv = (TextView) findViewById(R.id.tiaoli_tv);
        zhibo_tv=  (TextView) findViewById(R.id.zhibo_tv);
        zhibo_ll=  (LinearLayout) findViewById(R.id.zhibo_ll);
        zhibo_start= (TextView) findViewById(R.id.zhibo_start);
        checkbox= (CheckBox) findViewById(R.id.checkbox);
        close= (TextView) findViewById(R.id.close);
        live_time= (TextView) findViewById(R.id.live_time);
        zb_time= (TextView) findViewById(R.id.zb_time);
        zhibo_jiesu_ll= (LinearLayout) findViewById(R.id.zhibo_jiesu_ll);
        mCaptureView = (TXCloudVideoView) findViewById(R.id.video_view);
        chat_listview = (ChatListView) findViewById(R.id.chat_listview);
        llgiftcontent = (LinearLayout) findViewById(R.id.llgiftcontent);
        head_icon = (CircleImageView) findViewById(R.id.head_icon);
        room_count = (TextView)findViewById(R.id.room_count);
        gift_count = (TextView)findViewById(R.id.gift_count);
        tv_nickname = (TextView)findViewById(R.id.tv_nickname);

        danmaku_view = (DanmakuView) findViewById(R.id.danmaku_view);
        danmaku_view.enableDanmakuDrawingCache(true);
        danmaku_view.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                showDanmaku = true;
                danmaku_view.start();
//                generateSomeDanmaku();
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {

            }
        });
        danmakuContext = DanmakuContext.create();
        danmaku_view.prepare(parser, danmakuContext);

        fs_count = (TextView) findViewById(R.id.fs_count);
//        gz_count = (TextView) findViewById(R.id.gz_count);

        chatListAdapter = new ChatListAdapter();
        chat_listview.setAdapter(chatListAdapter);

        bigivgift = (GifView) findViewById(R.id.bigivgift);
        tv_fs= (TextView) findViewById(R.id.tv_fs);
        tv_gz=  (TextView)findViewById(R.id.tv_gz);
        js_tv= (TextView) findViewById(R.id.js_tv);


        beauty_tv01 = (TextView) findViewById(R.id.beauty_tv01);
        beauty_tv03 =  (TextView)findViewById(R.id.beauty_tv03);
        beauty_tv04 = (TextView) findViewById(R.id.beauty_tv04);
        //直播时间
        mVideoTimer = new Timer(true);
        mVideoTimerTask = new VideoTimerTask();
        //动画
        livestartAnim = (TranslateAnimation) AnimationUtils.loadAnimation(this, R.anim.out_from_bottom);

        inAnim = (TranslateAnimation) AnimationUtils.loadAnimation(this, R.anim.gift_in);
        outAnim = (TranslateAnimation) AnimationUtils.loadAnimation(this, R.anim.gift_out);
        giftNumAnim = new NumAnim();
        clearTiming();

        ll_giftlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveRoom.showGiftList(OpenVideoActivity.this, liveRoom.getLiveRoomBean().getData().getLiveId());
            }
        });
        //点击获取用户信息
        chat_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(((BaseRoom.UserInfo)chatListAdapter.getItem(position)).id == null) return;
                Long pid = ((BaseRoom.UserInfo)chatListAdapter.getItem(position)).id;
                if(pid == SharedUtils.readLoginId()) return;//如果是自己就不获取了
                if(pid!=null && pid != 0){
                    liveRoom.showUserInfo(OpenVideoActivity.this, pid, false);
                }
            }
        });
        //初始化昵称头像
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {
                Log.e("live", s);
            }

            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {
                username = timUserProfile.getNickName();
                userpic  = timUserProfile.getFaceUrl();
                if(username == null && username.equals("")){
                    username = "未设置";
                }
                if(userpic != null && !userpic.equals("")){
                    Picasso.with(OpenVideoActivity.this).load(userpic).into(head_icon);
                }
            }
        });

        //获取用户信息
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", SharedUtils.readLoginId());
        //获取用户信息
        new XRequest(OpenVideoActivity.this, "/weex/user/view.jhtml", XRequest.GET, params).setOnRequestListener(new HttpRequest.OnRequestListener() {
            @Override
            public void onSuccess(BaseActivity activity, String result, String type) {
                UserBean data = new Gson().fromJson(result, UserBean.class);
                if(data != null){
                    userBean = data;
                    fs_count.setText(data.getData().getFans() + "");
//                    gz_count.setText(data.getData().getFollow() + "");
                    String nickName =  data.getData().getNickName().length() > 4 ? data.getData().getNickName().substring(0, 4) + "..." : data.getData().getNickName();
                    tv_nickname.setText(nickName);
                    tv_fs.setText(data.getData().getFans() + "");
                    tv_gz.setText(data.getData().getFollow() + "");
                }else{
                    showToast("获取用户信息失败");
                    finish();
                }
            }

            @Override
            public void onFail(BaseActivity activity, String cacheData, int code) {
                showToast("获取用户信息失败");
                finish();
            }
        }).execute();

        //美颜p图部分
        mBeautyPannelView =  (BeautySettingPannel) findViewById(R.id.layoutFaceBeauty);
        mBeautyPannelView.setBeautyParamsChangeListener(this);


        beauty_tv03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(beauty_tv04.getVisibility()== View.VISIBLE)
                    beauty_tv04.setVisibility(View.GONE);
                else
                    beauty_tv04.setVisibility(View.VISIBLE);

                liveRoom.switchCamera();
            }
        });
        beauty_tv04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsFlashOpened) {
                    mIsFlashOpened = false;

                } else {
                    mIsFlashOpened = true;
                }
                liveRoom.turnOnFlashLight(mIsFlashOpened);
            }
        });


        mCaptureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //隐藏美颜编辑框
                mBeautyPannelView.setVisibility(View.INVISIBLE);
            }
        });
        fengmian_ll01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击设置封面
                startSelect();
            }
        });
        tiaoli_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //打开条款

            }
        });
        //条款单选框
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkbox.isChecked()){
                    zhibo_tv.setBackground(getResources().getDrawable(R.drawable.icon_bg14));
                    zhibo_tv.setTextColor(getResources().getColor(R.color.white));
                }else {
                    zhibo_tv.setBackground(getResources().getDrawable(R.drawable.icon_bg17));
                    zhibo_tv.setTextColor(getResources().getColor(R.color.app_back2));
                }
            }
        });
        isPlay = getIntent().getBooleanExtra("isPlay", false);
        //
        //默认跳过 测试用 到时候要删掉
        if(isPlay){
            //如果是跳过 直接开播的话
            zhibo_ll.setVisibility(GONE);
            zhibo_start.setVisibility(VISIBLE);
        }
        //单击下一步直播

        zhibo_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkbox.isChecked()) {
                    if (!title_zhibo.getText().toString().equals("") && isUpdateHeadImg) {
                        title = title_zhibo.getText().toString();
                        playParams.put("title", title);
                        playParams.put("frontcover", frontcover);
                        playParams.put("location", "");

                              zhibo_ll.startAnimation(livestartAnim);/*开始执行显示礼物的动画*/
                              livestartAnim.setAnimationListener(new Animation.AnimationListener() {/*显示动画的监听*/
                                  @Override
                                  public void onAnimationStart(Animation animation) {
                                  }

                                  @Override
                                  public void onAnimationEnd(Animation animation) {
                                      zhibo_ll.setVisibility(GONE);
                                      zhibo_start.setVisibility(VISIBLE);
                                  }

                                  @Override
                                  public void onAnimationRepeat(Animation animation) {
                                  }
                              });
                    } else if (title_zhibo.getText().toString().equals("")) {
                        Toast.makeText(OpenVideoActivity.this, "请输入直播标题", Toast.LENGTH_SHORT).show();
                    } else if (!isUpdateHeadImg) {
                        Toast.makeText(OpenVideoActivity.this, "请选择直播封面", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //开始直播

        zhibo_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //服务器请求直播
                playParams.put("id", getIntent().getStringExtra("liveId"));
                playParams.put("lat", "");
                playParams.put("lng", "");
                playParams.put("title", title);
                playParams.put("frontcover", frontcover);
                playParams.put("record", getIntent().getBooleanArrayExtra("record"));
//                Constant.groupId = liveRoomBean.getData().getLiveId().toString();
                new XRequest(OpenVideoActivity.this, "/weex/live/play.jhtml", XRequest.POST, playParams).setOnRequestListener(new HttpRequest.OnRequestListener() {
                    @Override
                    public void onSuccess(BaseActivity activity, String result, String type) {
                        LiveRoomBean data = new Gson().fromJson(result, LiveRoomBean.class);
                        if(data != null){
                            liveRoom.setLiveRoomBean(data);
                            //这里请求获取公告：
                            new XRequest(OpenVideoActivity.this, "weex/live/notice/list.jhtml", XRequest.GET,new HashMap<String, Object>()).setOnRequestListener(new HttpRequest.OnRequestListener() {
                                @Override
                                public void onSuccess(BaseActivity activity, String result, String type) {
                                    BasePage notiveInfo = new Gson().fromJson(result, BasePage.class);
//
                                    if(notiveInfo != null && notiveInfo.getType().equals("success") && notiveInfo.getData() != null && notiveInfo.getData().getData() != null && notiveInfo.getData().getData().size() > 0){
                                        int len = notiveInfo.getData().getData().size();
                                        List<NoticeInfo> noticeInfos = notiveInfo.getData().getData();
                                        for (int i = 0 ; i < len ; i++){
                                            if(noticeInfos.get(i).getType().equals("live")){//如果是系统公告
                                                BaseRoom.UserInfo userInfo = new BaseRoom.UserInfo();
                                                userInfo.text = "系统消息：" + noticeInfos.get(i).getTitle();
                                                userInfo.cmd  = BaseRoom.MessageType.CustomNoticeMsg.name();//推送消息
                                                chatListAdapter.addMessage(userInfo);
                                                chatListAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }else{
                                        //如果服务器返回错误
                                        BaseRoom.UserInfo userInfo = new BaseRoom.UserInfo();
                                        userInfo.text = "系统消息：" + "倡导绿色直播，封面和直播内容涉及色情、低俗、暴力、引诱、暴露等都将被封停账号，同时禁止直播闹事，集会。文明直播，从我做起【网警24小时在线巡查】安全提示：若涉及本系统以外的交易操作，请一定要先核实对方身份，谨防受骗！";
                                        userInfo.cmd  = BaseRoom.MessageType.CustomNoticeMsg.name();//推送消息
                                        chatListAdapter.addMessage(userInfo);
                                        chatListAdapter.notifyDataSetChanged();
                                    }
                                    //开始直播
                                    SharedUtils.saveLiveState(true);
                                }

                                @Override
                                public void onFail(BaseActivity activity, String cacheData, int code) {

                                }
                            }).execute();
                            ll_giftlist.setVisibility(VISIBLE);
                            //显示人数
                            roomCount = data.getData().getViewerCount();
                            room_count.setText("在线:" + formatLooker(roomCount));
                            room_count.setVisibility(VISIBLE);
                            giftCount = data.getData().getGift();
                            gift_count.setText("印票" + giftCount);
                            gift_count.setVisibility(VISIBLE);
                        //创建房间
                        liveRoom.createRoom(data.getData().getLiveId() + "", data.getData().getTitle(), new LiveRoom.CreateRoomCallback() {
                            @Override
                            public void onError(int errCode, String errInfo) {
                                showToast("参数错误，无法开始直播");
                                finish();
                            }

                            @Override
                            public void onSuccess(String roomID) {

                                //开始直播
                                mRecording = true;
                                //开启直播时间
                                mVideoTimer.schedule(mVideoTimerTask, 1000, 1000);
                                zhibo_start.setVisibility(GONE);
                            }
                        });
                        }else {
                            showToast("参数错误，无法开始直播");
                            finish();
                        }
                    }
                    @Override
                    public void onFail(BaseActivity activity, String cacheData, int code) {
                        showToast("参数错误，无法开始直播");
                        finish();
                    }
                }).execute();
                zhibo_start.setEnabled(false);
                zhibo_start.setFocusable(false);
            }
        });

//        关闭直播
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mRecording){
                    finish();
                }else{
                    if (!isPlay){
                        //获取用户信息
                        HashMap<String, Object> params = new HashMap<>();
                        params.put("id", SharedUtils.readLoginId());
                        //获取用户信息
                        new XRequest(OpenVideoActivity.this, "weex/user/view.jhtml", XRequest.GET, params).setOnRequestListener(new HttpRequest.OnRequestListener() {
                            @Override
                            public void onSuccess(BaseActivity activity, String result, String type) {
                                UserBean data = new Gson().fromJson(result, UserBean.class);
                                if(data != null){
                                    userBean = data;
                                    fs_count.setText(data.getData().getFans() + "");
//                                    gz_count.setText(data.getData().getFollow() + "");
                                    tv_fs.setText(data.getData().getFans() + "");
                                    tv_gz.setText(data.getData().getFollow() + "");
                                }
                                liveRoom.stopLocalPreview();
                                //关闭计时器
                                mVideoTimer.cancel();
                                zhibo_jiesu_ll.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onFail(BaseActivity activity, String cacheData, int code) {
                                liveRoom.stopLocalPreview();
                                //关闭计时器
                                mVideoTimer.cancel();
                                zhibo_jiesu_ll.setVisibility(View.VISIBLE);
                            }
                        }).execute();
                    }else{
                        //结束跳过 play
                       finish();
                    }
                }
            }
        });
        js_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        beauty_tv01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBeautyPannelView.setVisibility(mBeautyPannelView.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
            }
        });
    }

    /**
     * 向聊天界面推送消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void groupMessage(MessageBus messageBus){
        if(messageBus.getMessageType() == ZHIBOCHAT){
            TIMMessage message = (TIMMessage) messageBus.getMessage();
            if(message.getConversation().getType() == TIMConversationType.Group && !message.getConversation().getPeer().equals(liveRoom.getLiveRoomBean().getData().getLiveId().toString())) return;
            //处理消息类型
            long len = message.getElementCount();
            for (int i = 0; i < len; i++) {
                TIMElem element = message.getElement(i);

                switch (element.getType()){
                    case GroupSystem:
                        TIMGroupSystemElemType systemElemType = ((TIMGroupSystemElem) element).getSubtype();
                        switch (systemElemType) {

                            case TIM_GROUP_SYSTEM_DELETE_GROUP_TYPE: {
                                //房间被删了
                                EventBus.getDefault().post(new MessageBus(MessageBus.Type.ZHIBOCLOSE));
                                return;
                            }
                        }
                        break;
                    case Custom:
                        byte[] userData = ((TIMCustomElem) element).getData();
                        if (userData == null || userData.length == 0){
                            return;
                        }

                        String data = new String(userData);
                        try {
                            BaseRoom.CommonJson<Object> commonJson =  new Gson().fromJson(data, new TypeToken<BaseRoom.CommonJson<Object>>(){}.getType());

                                if (commonJson.cmd.equalsIgnoreCase(BaseRoom.MessageType.CustomTextMsg.name())) {
                                    ++i;
                                    BaseRoom.UserInfo userInfo = new Gson().fromJson(new Gson().toJson(commonJson.data), BaseRoom.UserInfo.class);
                                    if (userInfo != null && i < message.getElementCount()) {
                                        TIMElem nextElement = message.getElement(i);
                                        TIMTextElem textElem = (TIMTextElem) nextElement;
                                        String text = textElem.getText();//信息
                                        userInfo.text = text;
                                        if(text.contains("加入房间") && room_count != null){
                                            room_count.setText("在线:" + formatLooker(++roomCount));
                                        }
                                        chatListAdapter.addMessage(userInfo);
                                        chatListAdapter.notifyDataSetChanged();
                                    }
                                }else if(commonJson.cmd.equalsIgnoreCase(BaseRoom.MessageType.CustomGifMsg.name())){

                                    ++i;
                                    BaseRoom.UserInfo userInfo = new Gson().fromJson(new Gson().toJson(commonJson.data), BaseRoom.UserInfo.class);
                                    if (userInfo != null && i < message.getElementCount()) {
                                        TIMElem nextElement = message.getElement(i);
                                        TIMTextElem textElem = (TIMTextElem) nextElement;
                                        String text = textElem.getText();//信息
                                        userInfo.text = text;
                                        chatListAdapter.addMessage(userInfo);
                                        chatListAdapter.notifyDataSetChanged();

                                        //播放动画
//                                        int gifType = 0;
//                                        //播放GIF动画
//                                        if(text.contains("送了【666】")){
//                                            gifType = 1;
//                                        }else if(text.contains("送了【棒棒糖】")){
//                                            gifType = 2;
//                                        }else if(text.contains("送了【爱心】")){
//                                            gifType = 3;
//                                        }else if(text.contains("送了【玫瑰】")){
//                                            gifType = 4;
//                                        }else if(text.contains("送了【么么哒】")){
//                                            gifType = 5;
//                                        }else if(text.contains("送了【萌萌哒】")){
//                                            gifType = 6;
//                                        }else if(text.contains("送了【甜甜圈】")){
//                                            gifType = 7;
//                                        }else if(text.contains("送了【女神称号】")){
//                                            gifType = 8;
//                                        }

                                        List<LiveGiftBean.data.datagif> gifts = liveGiftBean.getData().getData();
                                        LiveGiftBean.data.datagif nowGif = null;
                                        for(LiveGiftBean.data.datagif item: gifts){
                                            if(text.contains(item.getName())){
                                                nowGif = item;
                                            }
                                        }
                                        //这里需要请求接口赠送礼物 请求成功了以后 开始动画

                                        //4秒后删除动画
                                        Message message2 = mHandler.obtainMessage();
                                        message2.what = INVISIBLE;
                                        mHandler.sendMessageDelayed(message2, 4000);
                                        showGift(nowGif.getId() + "", nowGif, userInfo.headPic, userInfo.nickName);
                                        giftCount = giftCount + nowGif.getPrice();
                                        gift_count.setText("印票" + giftCount);
                                        //播放礼物动画
                                        bigivgift.setMovieNet(nowGif.getAnimation());
//                                        if (bigivgift.getVisibility() == VISIBLE) {
//                                            bigivgift.setPaused(true);
////                                            bigivgift.setMovieResource(liwu_gif[gifType - 1]);
//                                            bigivgift.setMovieNet(nowGif.getAnimation());
//                                            bigivgift.setPaused(false);
//                                        } else {
//                                            bigivgift.setVisibility(VISIBLE);
//                                            bigivgift.setMovieNet(nowGif.getAnimation());
////                                            bigivgift.setMovieResource(liwu_gif[gifType - 1]);
//                                            bigivgift.setPaused(false);
//                                        }
                                    }
                                }else if(commonJson.cmd.equalsIgnoreCase(BaseRoom.MessageType.CustomFollowMsg.name())){
                                    //被关注了
                                    ++i;
                                    BaseRoom.UserInfo userInfo = new Gson().fromJson(new Gson().toJson(commonJson.data), BaseRoom.UserInfo.class);
                                    if (userInfo != null && i < message.getElementCount()) {
                                        TIMElem nextElement = message.getElement(i);
                                        TIMTextElem textElem = (TIMTextElem) nextElement;
                                        String text = textElem.getText();//信息
                                        userInfo.text = text;
                                        if(text.contains(ISFOLLOW)){
                                            userBean.getData().setFollow(userBean.getData().getFollow() + 1);
                                            fs_count.setText(userBean.getData().getFollow() + "");
                                            tv_fs.setText(userBean.getData().getFollow() + "");
                                            chatListAdapter.addMessage(userInfo);
                                            chatListAdapter.notifyDataSetChanged();
                                        }else{
                                            userBean.getData().setFollow(userBean.getData().getFollow() - 1);
                                            fs_count.setText(userBean.getData().getFollow() + "");
                                            tv_fs.setText(userBean.getData().getFollow() + "");
                                        }
                                    }
                                }else if(commonJson.cmd.equalsIgnoreCase(BaseRoom.MessageType.CustomKickMsg.name())){
                                    //有人被提了
                                    ++i;
                                    BaseRoom.UserInfo userInfo = new Gson().fromJson(new Gson().toJson(commonJson.data), BaseRoom.UserInfo.class);
                                    if (userInfo != null && i < message.getElementCount()) {
                                        TIMElem nextElement = message.getElement(i);
                                        TIMTextElem textElem = (TIMTextElem) nextElement;
                                        String text = textElem.getText();//信息
                                        userInfo.text = text;
                                        chatListAdapter.addMessage(userInfo);
                                        chatListAdapter.notifyDataSetChanged();
                                    }
                                }else if(commonJson.cmd.equalsIgnoreCase(BaseRoom.MessageType.CustomGagMsg.name())){
                                    ++i;
                                    BaseRoom.UserInfo userInfo = new Gson().fromJson(new Gson().toJson(commonJson.data), BaseRoom.UserInfo.class);
                                    if (userInfo != null && i < message.getElementCount()) {
                                        TIMElem nextElement = message.getElement(i);
                                        TIMTextElem textElem = (TIMTextElem) nextElement;
                                        String text = textElem.getText();//信息
                                        userInfo.text = text;
                                        chatListAdapter.addMessage(userInfo);
                                        chatListAdapter.notifyDataSetChanged();

                                        //禁言操作
                                        TIMGroupManagerExt.ModifyMemberInfoParam param = new TIMGroupManagerExt.ModifyMemberInfoParam(liveRoom.getLiveRoomBean().getData().getLiveId().toString(), userInfo.imid);
                                        param.setSilence(Long.parseLong(userInfo.time));//禁言24小时

                                        TIMGroupManagerExt.getInstance().modifyMemberInfo(param, new TIMCallBack() {
                                            @Override
                                            public void onError(int code, String desc) {
                                                Log.e("live", "modifyMemberInfo failed, code:" + code + "|msg: " + desc);
                                                com.rzico.weex.model.info.Message message = new com.rzico.weex.model.info.Message().error();
//                                                callback.invoke(message);
                                            }

                                            @Override
                                            public void onSuccess() {
                                                Log.d("live", "modifyMemberInfo succ");
                                                com.rzico.weex.model.info.Message message = new com.rzico.weex.model.info.Message().success("禁言成功");
//                                                callback.invoke(message);
                                            }
                                        });
                                    }
                                }else if(commonJson.cmd.equalsIgnoreCase(BaseRoom.MessageType.CustomBarrageMsg.name())){
                                    ++i;
                                    BaseRoom.UserInfo userInfo = new Gson().fromJson(new Gson().toJson(commonJson.data), BaseRoom.UserInfo.class);
                                    if (userInfo != null && i < message.getElementCount()) {
                                        TIMElem nextElement = message.getElement(i);
                                        TIMTextElem textElem = (TIMTextElem) nextElement;
                                        String text = textElem.getText();//信息
                                        userInfo.text = text;

                                        chatListAdapter.addMessage(userInfo);
                                        chatListAdapter.notifyDataSetChanged();
                                        liveRoom.addDanmaku(danmaku_view, danmakuContext,userInfo.nickName + ":" + userInfo.text , userInfo.id == SharedUtils.readLoginId());
                                    }
                                }

                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }

        }else if(messageBus.getMessageType() == MessageBus.Type.SENDGAG){
            BaseRoom.UserInfo userInfo = (BaseRoom.UserInfo) messageBus.getMessage();
            liveRoom.sendGroupGapMessage(userInfo, null);

        }else if(messageBus.getMessageType() == MessageBus.Type.SENDKICK){
            BaseRoom.UserInfo userInfo = (BaseRoom.UserInfo) messageBus.getMessage();
            liveRoom.sendGroupKickMessage(userInfo, null);
        }

    }


    /**
     * 格式化人数数据
     * @param count
     * @return
     */
    public String formatLooker(Long count){
        double dd;
        if(count > 1000){
            dd = count * 1.0 / 1000;
            return dd + "k";
        } else if(count > 10000){
            dd = count * 1.0 / 10000;
            return dd + "w";
        }else{
            return count + "";
        }
    }
    /**
     * 时间格式化
     */
    private long mSecond = 0;
    private String formatTime;
    public  void updateWallTime() {
        String hs, ms, ss;

        long h, m, s;
        h = mSecond / 3600;
        m = (mSecond % 3600) / 60;
        s = (mSecond % 3600) % 60;
        if (h < 10) {
            hs = "0" + h;
        } else {
            hs = "" + h;
        }

        if (m < 10) {
            ms = "0" + m;
        } else {
            ms = "" + m;
        }

        if (s < 10) {
            ss = "0" + s;
        } else {
            ss = "" + s;
        }
        if (hs.equals("00")) {
            formatTime = ms + ":" + ss;
        } else {
            formatTime = hs + ":" + ms + ":" + ss;
        }

        zb_time.setText("直播时长："+formatTime);
        live_time.setText(formatTime);
    }

    @Override
    public void onBeautyParamsChange(BeautySettingPannel.BeautyParams params, int key) {
        switch (key) {
            case BeautySettingPannel.BEAUTYPARAM_EXPOSURE:
                if (liveRoom != null) {
                    liveRoom.setExposureCompensation(params.mExposure);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_BEAUTY:
                mBeautyLevel = params.mBeautyLevel;
                if (liveRoom != null) {
                    liveRoom.setBeautyFilter(mBeautyStyle, mBeautyLevel, mWhiteningLevel, mRuddyLevel);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_WHITE:
                mWhiteningLevel = params.mWhiteLevel;
                if (liveRoom != null) {
                    liveRoom.setBeautyFilter(mBeautyStyle, mBeautyLevel, mWhiteningLevel, mRuddyLevel);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_BIG_EYE:
                if (liveRoom != null) {
                    liveRoom.setEyeScaleLevel(params.mBigEyeLevel);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_FACE_LIFT:
                if (liveRoom != null) {
                    liveRoom.setFaceSlimLevel(params.mFaceSlimLevel);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_FILTER:
                if (liveRoom != null) {
                    liveRoom.setFilter(params.mFilterBmp);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_GREEN:
                if (liveRoom != null) {
                    liveRoom.setGreenScreenFile(params.mGreenFile);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_MOTION_TMPL:
                if (liveRoom != null) {
                    liveRoom.setMotionTmpl(params.mMotionTmplPath);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_RUDDY:
                mRuddyLevel = params.mRuddyLevel;
                if (liveRoom != null) {
                    liveRoom.setBeautyFilter(mBeautyStyle, mBeautyLevel, mWhiteningLevel, mRuddyLevel);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_BEAUTY_STYLE:
                mBeautyStyle = params.mBeautyStyle;
                if (liveRoom != null) {
                    liveRoom.setBeautyFilter(mBeautyStyle, mBeautyLevel, mWhiteningLevel, mRuddyLevel);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_FACEV:
                if (liveRoom != null) {
                    liveRoom.setFaceVLevel(params.mFaceVLevel);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_FACESHORT:
                if (liveRoom != null) {
                    liveRoom.setFaceShortLevel(params.mFaceShortLevel);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_CHINSLIME:
                if (liveRoom != null) {
                    liveRoom.setChinLevel(params.mChinSlimLevel);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_NOSESCALE:
                if (liveRoom != null) {
                    liveRoom.setNoseSlimLevel(params.mNoseScaleLevel);
                }
                break;
            case BeautySettingPannel.BEAUTYPARAM_FILTER_MIX_LEVEL:
                if (liveRoom != null) {
                    liveRoom.setSpecialRatio(params.mFilterMixLevel/10.f);
                }
                break;
        }
    }

    /**
     * 记时器
     */
    private class VideoTimerTask extends TimerTask {
        public void run() {
            ++mSecond;
            mHandler.sendEmptyMessage(UPDAT_WALL_TIME_TIMER_TASK);
        }
    }
    private void initLive() {
        liveRoom = new LiveRoom(OpenVideoActivity.this);
        liveRoom.startLocalPreview(mCaptureView);
        if(getIntent().getStringExtra("title") == null || getIntent().getStringExtra("title").equals("")){
            title = getIntent().getStringExtra("title");
        }else{
            title = "没有标题";
        }
        if(getIntent().getStringArrayExtra("frontcover") == null || getIntent().getStringArrayExtra("frontcover").equals("")){
               frontcover = "";
        }else {
            frontcover = getIntent().getStringExtra("frontcover");
        }

        //        获取礼物信息
        new XRequest(OpenVideoActivity.this, "/weex/live/gift/list.jhtml", XRequest.GET, new HashMap<String, Object>()).setOnRequestListener(new HttpRequest.OnRequestListener() {
            @Override
            public void onSuccess(BaseActivity activity, String result, String type) {
                LiveGiftBean data = new Gson().fromJson(result, LiveGiftBean.class);
                liveGiftBean = data;
            }

            @Override
            public void onFail(BaseActivity activity, String cacheData, int code) {
                showToast("获取礼物信息失败");
                finish();
            }
        }).execute();
    }
    /**
     * 显示礼物的方法
     */
    HashMap<String, Long> map = new HashMap<String, Long>();

    private void showGift(String gifid, LiveGiftBean.data.datagif datagif, String head, String usernmae) {
        View giftView = llgiftcontent.findViewWithTag(usernmae);

        if (giftView == null) {/*该用户不在礼物显示列表*/
            map.put("username", datagif.getId());

            if (llgiftcontent.getChildCount() > 3) {/*如果正在显示的礼物的个数超过两个，那么就移除最后一次更新时间比较长的*/
                View giftView1 = llgiftcontent.getChildAt(0);
                CircleImageView picTv1 = (CircleImageView) giftView1.findViewById(R.id.crvheadimage);
                long lastTime1 = (Long) picTv1.getTag();
                View giftView2 = llgiftcontent.getChildAt(1);
                CircleImageView picTv2 = (CircleImageView) giftView2.findViewById(R.id.crvheadimage);
                long lastTime2 = (Long) picTv2.getTag();
                View giftView3 = llgiftcontent.getChildAt(2);
                CircleImageView picTv3 = (CircleImageView) giftView3.findViewById(R.id.crvheadimage);
                long lastTime3 = (Long) picTv3.getTag();
//                View giftView4 = llgiftcontent.getChildAt(3);
//                CircleImageView picTv4 = (CircleImageView) giftView4.findViewById(R.id.crvheadimage);
//                long lastTime4 = (Long) picTv4.getTag();
                Long[] time = {lastTime1, lastTime2, lastTime3/*,lastTime4*/};
                int minnum = 1;
                for (int j = 0; j < time.length; j++) {
                    if (time[j] < minnum) {
                        minnum = j;
                    }
                }
                removeGiftView(minnum);

//                if (lastTime1 > lastTime2) {/*如果第二个View显示的时间比较长*/
//                    removeGiftView(1);
//                } else {/*如果第一个View显示的时间长*/
//                    removeGiftView(0);
//                }
            }

            giftView = addGiftView();/*获取礼物的View的布局*/
            giftView.setTag(usernmae);/*设置view标识*/
            TextView username = (TextView) giftView.findViewById(R.id.username);
            username.setText(usernmae);
            TextView giftype = (TextView) giftView.findViewById(R.id.giftype);

            CircleImageView crvheadimage = (CircleImageView) giftView.findViewById(R.id.crvheadimage);
            if (!head.equals("")) {
                Picasso.with(OpenVideoActivity.this).load(head).memoryPolicy(NO_CACHE, NO_STORE).error(R.drawable.defaltshop).into(crvheadimage);
            } else {
                crvheadimage.setBackground(getResources().getDrawable(R.drawable.defaltshop));
            }

            final MagicTextView giftNum = (MagicTextView) giftView.findViewById(R.id.giftNum);/*找到数量控件*/
            GifView gifView = (GifView) giftView.findViewById(R.id.ivgift);
            // 设置背景gif图片资源
            String sendText = "";
            sendText = "送了【" + datagif.getName() +"】";
            gifView.setMovieNet(datagif.getAnimation());
//            if (i == 1) {
//                gifView.setMovieResource(R.raw.gg1);
////                giftype.setText("送了【666】");
//                sendText = "送了【666】";
//            } else if (i == 2) {
//                gifView.setMovieResource(R.raw.gg2);
////                giftype.setText("送了【棒棒糖】");
//                sendText = "送了【棒棒糖】";
//            } else if (i == 3) {
//                gifView.setMovieResource(R.raw.gg3);
////                giftype.setText("送了【爱心】");
//                sendText = "送了【爱心】";
//            } else if (i == 4) {
//                gifView.setMovieResource(R.raw.gg4);
////                giftype.setText("送了【玫瑰】");
//                sendText = "送了【玫瑰】";
//            } else if (i == 5) {
//                gifView.setMovieResource(R.raw.gg5);
////                giftype.setText("送了【么么哒】");
//                sendText = "送了【么么哒】";
//            } else if (i == 6) {
//                gifView.setMovieResource(R.raw.gg6);
////                giftype.setText("送了【萌萌哒】");
//                sendText = "送了【萌萌哒】";
//            } else if (i == 7) {
//                gifView.setMovieResource(R.raw.gg7);
////                giftype.setText("送了【甜甜圈】");
//                sendText = "送了【甜甜圈】";
//            } else if (i == 8) {
//                gifView.setMovieResource(R.raw.gg8);
////                giftype.setText("送了【女神称号】");
//                sendText = "送了【女神称号】";
//            }
            giftype.setText(sendText);

            giftNum.setText("x1");/*设置礼物数量*/
            crvheadimage.setTag(System.currentTimeMillis());/*设置时间标记*/
            giftNum.setTag(1);/*给数量控件设置标记*/

            llgiftcontent.addView(giftView);/*将礼物的View添加到礼物的ViewGroup中*/
            llgiftcontent.invalidate();/*刷新该view*/
            giftView.startAnimation(inAnim);/*开始执行显示礼物的动画*/
            inAnim.setAnimationListener(new Animation.AnimationListener() {/*显示动画的监听*/
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    giftNumAnim.start(giftNum);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        } else {/*该用户在礼物显示列表*/
            if (map.get("username") == datagif.getId()) {
                CircleImageView crvheadimage = (CircleImageView) giftView.findViewById(R.id.crvheadimage);/*找到头像控件*/
                MagicTextView giftNum = (MagicTextView) giftView.findViewById(R.id.giftNum);/*找到数量控件*/
                int showNum = (Integer) giftNum.getTag() + 1;
                giftNum.setText("x" + showNum);
                giftNum.setTag(showNum);
                crvheadimage.setTag(System.currentTimeMillis());
                giftNumAnim.start(giftNum);
            } else {
                int index = 0;
                map.put("username", datagif.getId());
                for (int k = 0; k < llgiftcontent.getChildCount(); k++) {
                    if (llgiftcontent.getChildAt(k) == giftView)
                        index = k;
                }
                giftView = null;
                removeGiftView(index, gifid, datagif, head, usernmae);
            }
        }
    }
    /**
     * 删除礼物view
     */
    private void removeGiftView(final int index) {
        final View removeView = llgiftcontent.getChildAt(index);
        final GifView gifView = (GifView) removeView.findViewById(R.id.ivgift);
        outAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                gifView.setPaused(true);
                llgiftcontent.removeViewAt(index);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeView.startAnimation(outAnim);
            }
        });
    }

    /**
     * 删除礼物view2
     */
    private void removeGiftView(final int index, final String gifid, final LiveGiftBean.data.datagif datagif, final String headimg, final String usernmae) {
        final View removeView = llgiftcontent.getChildAt(index);
        final GifView gifView = (GifView) removeView.findViewById(R.id.ivgift);
        llgiftcontent.removeViewAt(index);
        showGift(gifid, datagif, headimg, usernmae);
        outAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                gifView.setPaused(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeView.startAnimation(outAnim);
            }
        });
    }
    /**
     * 添加礼物view,(考虑垃圾回收)
     */
    private View addGiftView() {
        View view = null;
        if (giftViewCollection.size() <= 0) {
            /*如果垃圾回收中没有view,则生成一个*/
            view = LayoutInflater.from(OpenVideoActivity.this).inflate(R.layout.item_gift, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.topMargin = 10;
            view.setLayoutParams(lp);
            llgiftcontent.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View view) {
                }

                @Override
                public void onViewDetachedFromWindow(View view) {
                    giftViewCollection.add(view);
                }
            });
        } else {
            view = giftViewCollection.get(0);
            giftViewCollection.remove(view);
        }
        return view;
    }
    /**
     * 定时清除礼物
     */
    private void clearTiming() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                int count = llgiftcontent.getChildCount();
                for (int i = 0; i < count; i++) {
                    View view = llgiftcontent.getChildAt(i);
                    CircleImageView crvheadimage = (CircleImageView) view.findViewById(R.id.crvheadimage);
                    long nowtime = System.currentTimeMillis();
                    long upTime = (Long) crvheadimage.getTag();
                    if ((nowtime - upTime) >= 10000) {
                        removeGiftView(i);
                        return;
                    }
                }
            }
        };
        timer = new Timer();
        timer.schedule(task, 0, 3000);
    }
    String pathimage;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == REQUEST_CROP){//而代表请求裁剪
            AlbumModule.get().onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @SuppressLint("HandlerLeak")
    class MyHandler extends Handler {
        WeakReference<OpenVideoActivity> mActivity;
        public MyHandler(OpenVideoActivity activity) {
            mActivity = new WeakReference<OpenVideoActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            OpenVideoActivity activity = mActivity.get();

            switch (msg.what){
                case 1:

                    break;
                case UPDAT_WALL_TIME_TIMER_TASK:
                    //更新直播时间
                    updateWallTime();
                    break;

                case INVISIBLE:
                    bigivgift.setPaused(true);
                    bigivgift.setVisibility(GONE);
                    break;
                case UPLOADSUCCESS:
                    showToast("上传成功");
                    com.rzico.weex.model.info.Message message = (com.rzico.weex.model.info.Message)msg.obj;
                    fengmian_ll01.setVisibility(View.GONE);
                    fengmian_ll02.setVisibility(View.VISIBLE);
                    isUpdateHeadImg = true;
                    frontcover = (String) message.getData();
                    Picasso.with(OpenVideoActivity.this).load(frontcover).into(img_fengmian);
                    break;
                case UPLOADERROR:
                    showToast("上传错误");
                    break;
            }

        }

    }
    // activity 的 onStop 生命周期函数
    @Override
    public void onResume() {
        super.onResume();
        mCaptureView.onResume();     // mCaptureView 是摄像头的图像渲染view
        liveRoom.switchToForeground();
    }

    @Override
    protected void onPause() {
        super.onPause();
        liveRoom.switchToBackground();
        mCaptureView.onPause();
    }

    // activity 的 onStop 生命周期函数
    @Override
    public void onStop(){
        super.onStop();
        liveRoom.switchToBackground();
        mCaptureView.onPause();  // mCaptureView 是摄像头的图像渲染view
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        //停止直播
        SharedUtils.saveLiveState(false);
        showDanmaku = false;
        if (danmaku_view != null) {
            danmaku_view.release();
            danmaku_view = null;
        }
        liveRoom.stopLocalPreview();
        if(mRecording){
            HashMap<String, Object> params = new HashMap<>();
            params.put("id", liveRoom.getLiveRoomBean().getData().getLiveId());
            new XRequest(OpenVideoActivity.this, "/weex/live/stop.jhtml", XRequest.POST, params).setOnRequestListener(new HttpRequest.OnRequestListener() {
                @Override
                public void onSuccess(BaseActivity activity, String result, String type) {
                    Log.e("live", result);
                }

                @Override
                public void onFail(BaseActivity activity, String cacheData, int code) {
                    Log.e("live", cacheData);
                }
            }).execute();
        }
        JSCallback jsCallback = JSCallBaskManager.get(key);
        if(jsCallback != null){
            jsCallback.invoke(new com.rzico.weex.model.info.Message().success(liveRoom.getLiveRoomBean()));
            JSCallBaskManager.remove(key);
        }
    }

    //单选相册
    private void startSelect() {
        RxGalleryFinal
                .with(OpenVideoActivity.this)
                .image()
                .radio()
//                .cropAspectRatioOptions(0, new AspectRatio(10 + ":" + 7, 10, 7))
//                .crop()
                .imageLoader(ImageLoaderType.PICASSO)
                .subscribe(new RxBusResultDisposable<ImageRadioResultEvent>() {
                    @Override
                    protected void onEvent(final ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                        if (imageRadioResultEvent.getResult() == null){
                            //用户取消
                            return;
                        }
                        final MediaBean item = imageRadioResultEvent.getResult();
                        if((item.getThumbnailSmallPath() == null || item.getThumbnailSmallPath().equals("")) &&item.getOriginalPath()!=null){
                            item.setThumbnailSmallPath(item.getOriginalPath());
                        }
                        cropHeadImg(imageRadioResultEvent.getResult().getOriginalPath(), 10, 7);

//                        Toast.makeText(getContext(), "选中了图片路径：" + imageRadioResultEvent.getResult().getOriginalPath(), Toast.LENGTH_SHORT).show();
                    }
                })
                .openGallery();
    }

    public void cropHeadImg(String imagePath,int width, int height){
        if(imagePath == null || imagePath.equals("")){
            com.rzico.weex.model.info.Message message = new com.rzico.weex.model.info.Message().error("图片地址不合法");
            return;
        }
        //调用当前文件下的接口 并且实现它回调给 callback
        AspectRatio aspectRatio = new AspectRatio(width + ":" + height, width,height);
        AlbumModule.get().init(new AlbumModule.RxGalleryFinalCropListener() {
            @NonNull
            @Override
            public Activity getSimpleActivity() {
                return OpenVideoActivity.this;
            }

            @Override
            public void onCropCancel() {
                com.rzico.weex.model.info.Message message = new com.rzico.weex.model.info.Message();
                message.setType("cancel");
                message.setContent("用户取消");
                message.setData(null);
            }

            @Override
            public void onCropSuccess(@Nullable Uri uri) {
                com.rzico.weex.model.info.Message message = new com.rzico.weex.model.info.Message();
                message.setType("success");
                message.setContent("裁剪成功");
                MediaBean mediaBean = new MediaBean();
//                以下是要经过处理的 目前暂时传一样的
                mediaBean.setThumbnailBigPath(uri.getPath());
                mediaBean.setThumbnailSmallPath(uri.getPath());
                mediaBean.setOriginalPath(uri.getPath());
                message.setData(mediaBean);

                //选完图片 上传图片
                UploadToAli.getInstance(OpenVideoActivity.this).upload(uri.getPath(), new JSCallback() {
                    @Override
                    public void invoke(Object data) {
                        com.rzico.weex.model.info.Message message = (com.rzico.weex.model.info.Message)data;
                        if(message.getType().equals("success")){
                            Message msg = new Message();
                            msg.what = UPLOADSUCCESS;
                            msg.obj = message;
                            mHandler.sendMessage(msg);
                        }else{
                            mHandler.sendEmptyMessage(UPLOADERROR);
                        }
                    }

                    @Override
                    public void invokeAndKeepAlive(Object data) {

                    }
                }, null);
            }

            @Override
            public void onCropError(@NonNull String errorMessage) {
                com.rzico.weex.model.info.Message message = new com.rzico.weex.model.info.Message();
                message.setType("error");
                message.setContent("用户取消");
                message.setData(errorMessage);
            }
        }).openCrapActivity(imagePath, aspectRatio);
    }

    /**
     * 数字放大动画
     */
    public class NumAnim {
        private Animator lastAnimator = null;
        public void start(View view) {
            if (lastAnimator != null) {
                lastAnimator.removeAllListeners();
                lastAnimator.end();
                lastAnimator.cancel();
            }
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX",1.3f, 1.0f);
            ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleY",1.3f, 1.0f);
            AnimatorSet animSet = new AnimatorSet();
            lastAnimator = animSet;
            animSet.setDuration(200);
            animSet.setInterpolator(new OvershootInterpolator());
            animSet.playTogether(anim1, anim2);
            animSet.start();
        }
    }

    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if(!mRecording){
                finish();
            }else{
                exit();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    private AlertView exitAlert;
    public void exit() {

        //房间被删了
        exitAlert = new AlertView("温馨提示", "是否确定关闭直播间！", "取消", new String[]{"确定"}, null, OpenVideoActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                if(position == 0){
                 finish();
                }else{
                    if(exitAlert!=null){
                        exitAlert.dismiss();
                    }
                }
            }
        });
        exitAlert.show();
    }

}
