package com.rzico.weex.zhibo.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.rzico.weex.model.LivePlayerBean;
import com.rzico.weex.model.event.MessageBus;
import com.rzico.weex.model.info.BaseEntity;
import com.rzico.weex.model.info.BasePage;
import com.rzico.weex.model.info.NoticeInfo;
import com.rzico.weex.model.zhibo.GameBean;
import com.rzico.weex.model.zhibo.LiveGiftBean;
import com.rzico.weex.model.zhibo.LiveRoomBean;
import com.rzico.weex.model.zhibo.SendGift;
import com.rzico.weex.model.zhibo.UserBean;
import com.rzico.weex.module.JSCallBaskManager;
import com.rzico.weex.net.HttpRequest;
import com.rzico.weex.net.XRequest;
import com.rzico.weex.utils.SharedUtils;
import com.rzico.weex.view.SlideSwitch;
import com.rzico.weex.zhibo.activity.utils.BaseRoom;
import com.rzico.weex.zhibo.activity.utils.LiveRoom;
import com.rzico.weex.zhibo.adapter.ChatListAdapter;
import com.rzico.weex.zhibo.view.BottomPanelFragment;
import com.rzico.weex.zhibo.view.ChatListView;
import com.rzico.weex.zhibo.view.CircleImageView;
import com.rzico.weex.zhibo.view.GifView;
import com.rzico.weex.zhibo.view.HeartLayout;
import com.rzico.weex.zhibo.view.InputPanel;
import com.rzico.weex.zhibo.view.MagicTextView;
import com.squareup.picasso.Picasso;
import com.taobao.weex.bridge.JSCallback;
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
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.finalteam.rxgalleryfinal.utils.DensityUtil;
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
import static com.rzico.weex.zhibo.activity.utils.BaseRoom.ISFOLLOW;
import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

public class PlayBfActivity extends BaseActivity {

    private final static  String tag = "PlayActivity";
    private TXCloudVideoView mView;
    private DanmakuView danmaku_view;
    private DanmakuContext danmakuContext;

    private BaseDanmakuParser parser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };

    private LiveRoom liveRoom;

    private BottomPanelFragment bottomPanel;
    private LinearLayout btnGift;//礼物
    private LinearLayout btnHeart;//点赞
    private HeartLayout mHeartLayout;//点赞控件
    private GifView bigivgift;//送礼物时显示的最大数
    RelativeLayout mInView;
    private TextView room_count;
    private TextView fs_count;
    private TextView gift_count;
    private TextView tv_danmu;
    private Long roomCount;
    private Long giftCount;

    private ImageView head_image;
    private Timer timer;
    boolean isRealFinish = false;

    public static double cashmoney = 0.0;//用户金币

//    private String headimg;//直播封面
//    private String roomNum;//直播房间号
//    private String liveUrl;//直播地址
//    private String anhorIma;//主播头像
//    private String roomname;//昵称
//    private String anhorid;//主播id

    private UserBean userBean;
    private UserBean zhuboBean;

    private CircleImageView head_icon;//头像
    private TextView room_name;//房间昵称
    private LinearLayout concern_ll;//关注

    public static final int GIFSHOW = 135;
    public static final int INVISIBLE = 132;
    private static final int CANDASHAN = 136;//控制 打赏按钮 1秒后才可以再打赏

    private ChatListView chat_listview;
    private ChatListAdapter chatListAdapter;
    private boolean showDanmaku;
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

    private LiveGiftBean liveGiftBean;
    /**
     * 数据相关
     */
    private List<View> giftViewCollection = new ArrayList<View>();

    private SlideSwitch ssBarrage;//是否显示弹幕
    private boolean isBarrage = false;

    //    用户信息
    private String username = "";
    private String userpic = "";
    //weex回调的 key
    private String key;



//    游戏定义

    private View member_bottom_layout, heart_layout;
    private FrameLayout game_frame;
    private TXCloudVideoView gameCaptureView;
    private WebView gameWebView;
    private boolean mIsPlaying;
    private TXLivePlayer mLivePlayer = null;
    private static final int  CACHE_STRATEGY_FAST  = 1;  //极速
    private static final int  CACHE_STRATEGY_SMOOTH = 2;  //流畅
    private static final int  CACHE_STRATEGY_AUTO = 3;  //自动

    private static final float  CACHE_TIME_FAST = 1.0f;
    private static final float  CACHE_TIME_SMOOTH = 5.0f;

    public static final int ACTIVITY_TYPE_PUBLISH      = 1;
    public static final int ACTIVITY_TYPE_LIVE_PLAY    = 2;
    public static final int ACTIVITY_TYPE_VOD_PLAY     = 3;
    public static final int ACTIVITY_TYPE_LINK_MIC     = 4;
    public static final int ACTIVITY_TYPE_REALTIME_PLAY = 5;

    private int              mCacheStrategy = 0;

    LivePlayerBean livePlayerBean;

    private int              mCurrentRenderMode;
    private int              mCurrentRenderRotation;
    private boolean  mHWDecode   = false;

    private int              mPlayType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
    private TXLivePlayConfig mPlayConfig;
    private long             mStartPlayTS = 0;
    protected int            mActivityType;
    private String gameUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStatusBarFullTransparent();
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        liveRoom = new LiveRoom(this);
        setContentView(R.layout.activity_play_new);
        EventBus.getDefault().register(this);
        //mPlayerView 即 step1 中添加的界面 viewRENDER_MODE_FILL_SCREEN
        key = getIntent().getStringExtra("key");
        initView();
        initData();
        clearTiming();

    }
    private void initData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id" , getIntent().getStringExtra("liveId"));
        params.put("lat", "");
        params.put("lng", "");
        //进入直播间
        new XRequest(PlayBfActivity.this, "weex/live/into.jhtml", XRequest.POST, params).setOnRequestListener(new HttpRequest.OnRequestListener() {
            @Override
            public void onSuccess(BaseActivity activity, String result, String type) {
                LiveRoomBean data = new Gson().fromJson(result, LiveRoomBean.class);
                if(data != null){
                    liveRoom.setLiveRoomBean(data);
                    //获取用户信息
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("id", data.getData().getLiveMemberId());
                    //获取用户信息
                    new XRequest(PlayBfActivity.this, "weex/user/view.jhtml", XRequest.GET, params).setOnRequestListener(new HttpRequest.OnRequestListener() {
                        @Override
                        public void onSuccess(BaseActivity activity, String result, String type) {

                            UserBean data = new Gson().fromJson(result, UserBean.class);
                            if(data!=null){
                                zhuboBean = data;
                                fs_count.setText("粉丝" + zhuboBean.getData().getFans());

                                String roomName = zhuboBean.getData().getNickName();
                                if(roomName.length() > 4){
                                    roomName = roomName.substring(0, 4) + "...";
                                }
                                room_name.setText(roomName);
                            }else {
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
                    //这里请求获取公告：
                    new XRequest(PlayBfActivity.this, "weex/live/notice/list.jhtml", XRequest.GET,new HashMap<String, Object>()).setOnRequestListener(new HttpRequest.OnRequestListener() {
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
                        }

                        @Override
                        public void onFail(BaseActivity activity, String cacheData, int code) {

                            //如果服务器返回错误
                            BaseRoom.UserInfo userInfo = new BaseRoom.UserInfo();
                            userInfo.text = "系统消息：" + "倡导绿色直播，封面和直播内容涉及色情、低俗、暴力、引诱、暴露等都将被封停账号，同时禁止直播闹事，集会。文明直播，从我做起【网警24小时在线巡查】安全提示：若涉及本系统以外的交易操作，请一定要先核实对方身份，谨防受骗！";
                            userInfo.cmd  = BaseRoom.MessageType.CustomNoticeMsg.name();//推送消息
                            chatListAdapter.addMessage(userInfo);
                            chatListAdapter.notifyDataSetChanged();
                        }
                    }).execute();

                    //判断是否关注了
                    setGuanzu(data.getData().getFollow());
                    roomCount = data.getData().getViewerCount();
                    giftCount = data.getData().getGift();
                    //显示在线人数
//                    if(roomCount > 0){//因为自己已经加入了
//                        room_count.setText(人气值:--roomCount + "人" );
//                    }else{
                        room_count.setText("人气值:" + formatLooker(roomCount));
//                    }
                    room_count.setVisibility(VISIBLE);
                    gift_count.setText("炭币" + giftCount);
                    gift_count.setVisibility(VISIBLE);
//                    if(data.getData().getFrontcover() !=null && !data.getData().getFrontcover().equals("")){
//                        Picasso.with(PlayActivity.this).load(data.getData().getFrontcover()).into(head_image);
//                    }else {
//                        Picasso.with(PlayActivity.this).load("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3994969733,336727888&fm=27&gp=0.jpg").into(head_image);
//                    }
//                    head_image.setVisibility(VISIBLE);
                    //设置房间名称和 主播昵称信息
                    Picasso.with(PlayBfActivity.this).load(data.getData().getHeadpic()).into(head_icon);


                    liveRoom.enterRoom(data.getData().getLiveId() + "", mView, new LiveRoom.EnterRoomCallback() {
                        @Override
                        public void onError(int errCode, String errInfo) {
//                            head_image.setVisibility(View.GONE);
                            Log.e(tag, errInfo);
                            showToast("加入房间失败");
                            finish();
                        }

                        @Override
                        public void onSuccess() {

                            TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
                                @Override
                                public void onError(int i, String s) {

                                }

                                @Override
                                public void onSuccess(TIMUserProfile timUserProfile) {

                                    liveRoom.sendGroupTextMessage(timUserProfile.getNickName(), timUserProfile.getFaceUrl(), "加入房间", new BaseRoom.MessageCallback() {
                                        @Override
                                        public void onError(int code, String errInfo) {
//                                            head_image.setVisibility(View.GONE);
                                            if(code == 10017){
                                                showToast("您已被主播禁言");
                                            }
                                        }

                                        @Override
                                        public void onSuccess(Object... args) {
//                                            head_image.setVisibility(View.GONE);

                                        }
                                    });
                                }
                            });
                        }
                    });
                }

            }

            @Override
            public void onFail(BaseActivity activity, String cacheData, int code) {
                showToast("获取房间信息失败");
                finish();
            }
        }).execute();
        //获取用户信息
        HashMap<String, Object> params2 = new HashMap<>();
        params2.put("id", SharedUtils.readLoginId());
        //获取用户信息
        new XRequest(PlayBfActivity.this, "/weex/user/view.jhtml", XRequest.GET, params2).setOnRequestListener(new HttpRequest.OnRequestListener() {
            @Override
            public void onSuccess(BaseActivity activity, String result, String type) {

                UserBean data = new Gson().fromJson(result, UserBean.class);
                if(data!=null){
                    userBean = data;
                    liveRoom.setUserBean(userBean);
                    cashmoney = data.getData().getBalance();

                }else {
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

//        获取礼物信息
        new XRequest(PlayBfActivity.this, "/weex/live/gift/list.jhtml", XRequest.GET, new HashMap<String, Object>()).setOnRequestListener(new HttpRequest.OnRequestListener() {
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

    private void initView() {

        setStatusBarFullTransparent();
        mView = (TXCloudVideoView) findViewById(R.id.video_view);
        //评论
        bottomPanel = (BottomPanelFragment) getSupportFragmentManager().findFragmentById(R.id.bottom_bar);
        btnGift = (LinearLayout) bottomPanel.getView().findViewById(R.id.member_menu_more);
        btnHeart = (LinearLayout) bottomPanel.getView().findViewById(R.id.member_fullscreen_btn);
        mInView = (RelativeLayout) findViewById(R.id.re_ll);
        mHeartLayout = (HeartLayout) findViewById(R.id.heart_layout);
        head_image = (ImageView) findViewById(R.id.head_image);//直播封面
        bigivgift = (GifView) findViewById(R.id.bigivgift);
        llgiftcontent = (LinearLayout) findViewById(R.id.llgiftcontent);
        chat_listview = (ChatListView) findViewById(R.id.chat_listview);
        ssBarrage =(SlideSwitch) findViewById(R.id.ssBarrage);

        head_icon = (CircleImageView) findViewById(R.id.head_icon);
        room_name = (TextView) findViewById(R.id.room_name);
        concern_ll = (LinearLayout) findViewById(R.id.concern_ll);
        room_count = (TextView) findViewById(R.id.room_count);
        fs_count   = (TextView) findViewById(R.id.fs_count);
        gift_count = (TextView) findViewById(R.id.gift_count);
        tv_danmu  = (TextView) findViewById(R.id.tv_danmu);


        //直播定义
        game_frame = (FrameLayout) findViewById(R.id.game_frame);

        member_bottom_layout = (View)findViewById(R.id.member_bottom_layout);


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


        chatListAdapter = new ChatListAdapter();
        chat_listview.setAdapter(chatListAdapter);

        //动画

        inAnim = (TranslateAnimation) AnimationUtils.loadAnimation(this, R.anim.gift_in);
        outAnim = (TranslateAnimation) AnimationUtils.loadAnimation(this, R.anim.gift_out);
        giftNumAnim = new NumAnim();

        ssBarrage.setChecked(false);
        //是否发送弹幕监听
        ssBarrage.setOnChangedListener(new SlideSwitch.OnChangedListener() {
            @Override
            public void OnChanged(boolean checkState) {
                isBarrage = checkState;
                if(isBarrage){
                    tv_danmu.setTextColor(getResources().getColor(R.color.danmu));
                }else {
                    tv_danmu.setTextColor(getResources().getColor(R.color.text_dark));
                }
            }
        });
        //点击左上角头像
        head_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(liveRoom != null && liveRoom.getLiveRoomBean() != null){
                    liveRoom.showUserInfo(PlayBfActivity.this, liveRoom.getLiveRoomBean().getData().getLiveMemberId(), true);
                }
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
                    liveRoom.showUserInfo(PlayBfActivity.this, pid, true);
                }
            }
        });
        //关注主播
        concern_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> params = new HashMap<>();
                params.put("authorId", liveRoom.getLiveRoomBean().getData().getLiveMemberId());

                if(liveRoom.getLiveRoomBean().getData().getFollow()){
                    new XRequest(PlayBfActivity.this, "/weex/member/follow/delete.jhtml", XRequest.POST,params).setOnRequestListener(new HttpRequest.OnRequestListener() {
                        @Override
                        public void onSuccess(BaseActivity activity, String result, String type) {
                            BaseEntity data = new Gson().fromJson(result, BaseEntity.class);
                            if(data!=null && data.getType().equals("success")){
                                //取消关注成功
                                liveRoom.getLiveRoomBean().getData().setFollow(false);
                                showToast("取消关注成功");
                                liveRoom.sendGroupFollowMessage(username, userpic, BaseRoom.UNFOLLOW, null);
                                setGuanzu(false);
                            }
                        }

                        @Override
                        public void onFail(BaseActivity activity, String cacheData, int code) {
                            showToast("取消关注失败");
                        }
                    }).execute();
                }else{
                    //关注
                    new XRequest(PlayBfActivity.this, "/weex/member/follow/add.jhtml", XRequest.POST,params).setOnRequestListener(new HttpRequest.OnRequestListener() {
                        @Override
                        public void onSuccess(BaseActivity activity, String result, String type) {
                            BaseEntity data = new Gson().fromJson(result, BaseEntity.class);
                            if(data!=null && data.getType().equals("success")){
                                //关注成功
                                liveRoom.getLiveRoomBean().getData().setFollow(true);
                                setGuanzu(true);
                                liveRoom.sendGroupFollowMessage(username, userpic, BaseRoom.ISFOLLOW, null);
                                showToast("关注成功");
                            }
                        }

                        @Override
                        public void onFail(BaseActivity activity, String cacheData, int code) {
                            showToast("关注失败");
                        }
                    }).execute();
                }
            }
        });
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {
                //获取信息失败
                showToast("获取用户信息失败");
                finish();
            }

            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {
                if(timUserProfile != null){
                    username = timUserProfile.getNickName();
                    userpic  = timUserProfile.getFaceUrl();

                }
            }
        });
        //发消息
        bottomPanel.setInputPanelListener(new InputPanel.InputPanelListener() {
            @Override
            public void onSendClick(final String text) {

                if(!isBarrage){//不是发弹幕
                    liveRoom.sendGroupTextMessage(username, userpic, text, new BaseRoom.MessageCallback() {
                        @Override
                        public void onError(int code, String errInfo) {
                            if(code == 10017){
                                showToast("您已被主播禁言");
                            }
                        }
                        @Override
                        public void onSuccess(Object... args) {
                        }
                    });
                }else {
                    //发送弹幕
                    //=========================这里需要调用张总接口成功再调用
                    if(showDanmaku){
                        HashMap<String, Object> params = new HashMap<>();
                        params.put("liveId", liveRoom.getLiveRoomBean().getData().getLiveId());
                        new XRequest(PlayBfActivity.this, "weex/live/gift/barrage.jhtml", XRequest.POST, params).setOnRequestListener(new HttpRequest.OnRequestListener() {
                            @Override
                            public void onSuccess(BaseActivity activity, String result, String type) {
                                SendGift data = new Gson().fromJson(result, SendGift.class);
                                if(data.getType().equals("success")){

                                    //发送弹幕就扣除金币
                                    cashmoney = cashmoney - 1;
                                    jifen_tv.setText(cashmoney + "");
                                    liveRoom.sendGroupBarrageMessage(username, userpic, text, new BaseRoom.MessageCallback() {
                                        @Override
                                        public void onError(int code, String errInfo) {
                                            if(code == 10017){
                                                showToast("您已被主播禁言");
                                            }
                                        }

                                        @Override
                                        public void onSuccess(Object... args) {
                                        }
                                    });
                                }else{
                                    showToast("您的金币余额不足！弹幕发送失败！");
                                }

                            }

                            @Override
                            public void onFail(BaseActivity activity, String cacheData, int code) {
                                showToast("发送弹幕失败");
                            }
                        }).execute();
                    }
                }


            }
        });
        //发礼物
        btnGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SongCouponWindow();
            }
        });
        //点赞
        btnHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHeartLayout.addFavor();
                //点赞
            }
        });
        //
        mInView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomPanel.onBackAction();
            }
        });
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomPanel.onBackAction();

//                liveRoom.sendGroupTextMessage("陈金乐", "", "主播好帅啊！", new BaseRoom.MessageCallback() {
//                    @Override
//                    public void onError(int code, String errInfo) {
//
//                    }
//
//                    @Override
//                    public void onSuccess(Object... args) {
//                    }
//                });
            }
        });
        danmaku_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomPanel.onBackAction();
//                liveRoom.sendGroupGapMessage(150L, "郭书智", BaseRoom.ISGAG, null);
            }
        });
    }

    public void setGuanzu(boolean state){
        if(state){//已关注
//            concern_ll.setVisibility(GONE);
//            concern_ll.setEnabled(false);
//            concern_ll.setFocusable(false);
            concern_ll.setBackground(getResources().getDrawable(R.drawable.gz_no));
        }else {
//            concern_ll.setVisibility(VISIBLE);
//            concern_ll.setEnabled(true);
//            concern_ll.setFocusable(true);
            concern_ll.setBackground(getResources().getDrawable(R.drawable.gz_yes));
        }
    }
    /**
     * 送礼物
     */
    private PopupWindow songCouponview;
    private View songview;
    private RelativeLayout lw01;
    private RelativeLayout lw02;
    private RelativeLayout lw03;
    private RelativeLayout lw04;
    private RelativeLayout lw05;
    private RelativeLayout lw06;
    private RelativeLayout lw07;
    private RelativeLayout lw08;
    private LinearLayout lw_ll;

    private Button shopping_bt_subtract;//减
    private Button shopping_bt_add;//加
    private TextView counttv;

    private ImageView iv_lw1,iv_lw2,iv_lw3,iv_lw4,iv_lw5,iv_lw6,iv_lw7,iv_lw8;
    private TextView tv_1,tv_2,tv_3,tv_4,tv_5,tv_6,tv_7,tv_8;//礼物名称
    private TextView tv1,tv2,tv3,tv4,tv5,tv6,tv7,tv8;//礼物金币额度

    private List<ImageView> lws;
    private List<TextView> lwNames;
    private List<TextView> lwMoneys;
    private TextView dashan;
    private TextView jifen_tv;
    private boolean ischeck = false;
    private int gifid = 0;
    int count = 1;
    int servecount = 0;//服务器上传的count

    @SuppressLint("WrongConstant")
    private void SongCouponWindow() {
        if(liveGiftBean == null) return;
        if (songCouponview == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.live_song, null);
            songCouponview = new PopupWindow(view,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, true);
            songCouponview.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            songCouponview.setFocusable(true);
            // 设置点击其他地方就消失
            songCouponview.setOutsideTouchable(true);
            songCouponview.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
            songCouponview.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            lw_ll = (LinearLayout) view.findViewById(R.id.lw_ll);
            lw01 = (RelativeLayout) view.findViewById(R.id.lw1);
            lw02 = (RelativeLayout) view.findViewById(R.id.lw2);
            lw03 = (RelativeLayout) view.findViewById(R.id.lw3);
            lw04 = (RelativeLayout) view.findViewById(R.id.lw4);
            lw05 = (RelativeLayout) view.findViewById(R.id.lw5);
            lw06 = (RelativeLayout) view.findViewById(R.id.lw6);
            lw07 = (RelativeLayout) view.findViewById(R.id.lw7);
            lw08 = (RelativeLayout) view.findViewById(R.id.lw8);

            lws = new ArrayList<>();
            lwNames = new ArrayList<>();
            lwMoneys = new ArrayList<>();
            iv_lw1 = (ImageView) view.findViewById(R.id.iv_lw1);
            iv_lw2 = (ImageView) view.findViewById(R.id.iv_lw2);
            iv_lw3 = (ImageView) view.findViewById(R.id.iv_lw3);
            iv_lw4 = (ImageView) view.findViewById(R.id.iv_lw4);
            iv_lw5 = (ImageView) view.findViewById(R.id.iv_lw5);
            iv_lw6 = (ImageView) view.findViewById(R.id.iv_lw6);
            iv_lw7 = (ImageView) view.findViewById(R.id.iv_lw7);
            iv_lw8 = (ImageView) view.findViewById(R.id.iv_lw8);
            tv_1 = (TextView)  view.findViewById(R.id.tv_1);
            tv_2 = (TextView)  view.findViewById(R.id.tv_2);
            tv_3 = (TextView)  view.findViewById(R.id.tv_3);
            tv_4 = (TextView)  view.findViewById(R.id.tv_4);
            tv_5 = (TextView)  view.findViewById(R.id.tv_5);
            tv_6 = (TextView)  view.findViewById(R.id.tv_6);
            tv_7 = (TextView)  view.findViewById(R.id.tv_7);
            tv_8 = (TextView)  view.findViewById(R.id.tv_8);
            tv1 = (TextView)  view.findViewById(R.id.tv1);
            tv2 = (TextView)  view.findViewById(R.id.tv2);
            tv3 = (TextView)  view.findViewById(R.id.tv3);
            tv4 = (TextView)  view.findViewById(R.id.tv4);
            tv5 = (TextView)  view.findViewById(R.id.tv5);
            tv6 = (TextView)  view.findViewById(R.id.tv6);
            tv7 = (TextView)  view.findViewById(R.id.tv7);
            tv8 = (TextView)  view.findViewById(R.id.tv8);
            lws.add(iv_lw1);
            lws.add(iv_lw2);
            lws.add(iv_lw3);
            lws.add(iv_lw4);
            lws.add(iv_lw5);
            lws.add(iv_lw6);
            lws.add(iv_lw7);
            lws.add(iv_lw8);
            lwNames.add(tv_1);
            lwNames.add(tv_2);
            lwNames.add(tv_3);
            lwNames.add(tv_4);
            lwNames.add(tv_5);
            lwNames.add(tv_6);
            lwNames.add(tv_7);
            lwNames.add(tv_8);
            lwMoneys.add(tv1);
            lwMoneys.add(tv2);
            lwMoneys.add(tv3);
            lwMoneys.add(tv4);
            lwMoneys.add(tv5);
            lwMoneys.add(tv6);
            lwMoneys.add(tv7);
            lwMoneys.add(tv8);
            //设置礼物图片
            List<LiveGiftBean.data.datagif> datagifs = liveGiftBean.getData().getData();
            int len = datagifs.size() > 8 ? 8 : datagifs.size();
            for (int i = 0; i <  len; i++){
                Picasso.with(PlayBfActivity.this).load(datagifs.get(i).getThumbnail()).into(lws.get(i));
                final int now = i;
                lwNames.get(i).setText(datagifs.get(i).getName());
                lwMoneys.get(i).setText(datagifs.get(i).getPrice() + "金币");
//                Picasso.with(PlayActivity.this).load(datagifs.get(i).getThumbnail()).into(new Target() {
//                    @Override
//                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                        lws.get(now).setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
//                    }
//
//                    @Override
//                    public void onBitmapFailed(Drawable errorDrawable) {
//                        System.out.println();
//                    }
//
//                    @Override
//                    public void onPrepareLoad(Drawable placeHolderDrawable) {
//                        System.out.println();
//
//                    }
//                });
            }

            dashan = (TextView) view.findViewById(R.id.dashan);
            jifen_tv = (TextView) view.findViewById(R.id.jifen_tv);
            jifen_tv.setText(cashmoney + "");
            shopping_bt_subtract = (Button) view.findViewById(R.id.shopping_bt_subtract);
            shopping_bt_add = (Button) view.findViewById(R.id.shopping_bt_add);
            counttv = (TextView) view.findViewById(R.id.shopping_tv_number);

            songview = (View) view.findViewById(R.id.other);

            songlistener();
        }
        if (songCouponview.isShowing()) {
            lw_ll.startAnimation(AnimationUtils.loadAnimation(PlayBfActivity.this, R.anim.out_from_bottom));
            songCouponview.dismiss();
        } else {
            jifen_tv.setText(cashmoney + "");
            counttv.setText("1");
            gifid = 0;
            lw_ll.startAnimation(AnimationUtils.loadAnimation(PlayBfActivity.this, R.anim.in_from_bottom));
            songCouponview.showAtLocation(mInView, Gravity.CENTER, 0, 0);
        }
    }

    public void setview() {
        ischeck = true;
        lw01.setBackground(getResources().getDrawable(R.drawable.icon_bg2));
        lw02.setBackground(getResources().getDrawable(R.drawable.icon_bg2));
        lw03.setBackground(getResources().getDrawable(R.drawable.icon_bg2));
        lw04.setBackground(getResources().getDrawable(R.drawable.icon_bg2));
        lw05.setBackground(getResources().getDrawable(R.drawable.icon_bg2));
        lw06.setBackground(getResources().getDrawable(R.drawable.icon_bg2));
        lw07.setBackground(getResources().getDrawable(R.drawable.icon_bg2));
        lw08.setBackground(getResources().getDrawable(R.drawable.icon_bg2));
        dashan.setTextColor(getResources().getColor(R.color.white));
        dashan.setBackground(getResources().getDrawable(R.drawable.icon_bg10));
    }

    public Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case CANDASHAN:
                    dashan.setEnabled(true);
                    dashan.setFocusable(true);
                    break;
                case GIFSHOW://礼物大图显示
                    dashan.setEnabled(false);
                    dashan.setFocusable(false);
                    Message message = mHandler.obtainMessage();
                    message.what = CANDASHAN;

                    mHandler.sendMessageDelayed(message, 1000);
//                    进行送礼物接口
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("id", liveGiftBean.getData().getData().get(gifid - 1 >= 0 ? gifid -1 : 0).getId());
                    params.put("liveId", liveRoom.getLiveRoomBean().getData().getLiveId());
                    new XRequest(PlayBfActivity.this, "weex/live/gift/submit.jhtml", XRequest.POST, params).setOnRequestListener(new HttpRequest.OnRequestListener() {
                        @Override
                        public void onSuccess(BaseActivity activity, String result, String type) {
                            SendGift data = new Gson().fromJson(result, SendGift.class);
                            if(data.getType().equals("success")){
//                                sendgif();

                                List<LiveGiftBean.data.datagif> giftdatas = liveGiftBean.getData().getData();
                                int len = giftdatas.size();
                                int i = gifid;
                                i = i > len ? len : i;
                                String sendText = "送了【"+ giftdatas.get(i > 0 ? i - 1 : 0).getName() +"】";
                                liveRoom.sendGroupGifMessage(username, userpic, sendText, new BaseRoom.MessageCallback() {
                                    @Override
                                    public void onError(int code, String errInfo) {

                                    }

                                    @Override
                                    public void onSuccess(Object... args) {

                                    }
                                });
                                cashmoney = cashmoney - data.getData();
                                jifen_tv.setText(cashmoney + "");
                            }else{
                                showToast(data.getContent());
                            }
                        }

                        @Override
                        public void onFail(BaseActivity activity, String cacheData, int code) {
                            showToast("赠送失败");
                        }
                    }).execute();
                    break;
                case INVISIBLE:
                    bigivgift.setPaused(true);
                    bigivgift.setVisibility(GONE);
                    break;
            }
            return false;
        }
    });
    private void songlistener() {
        //减
        shopping_bt_subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = Integer.parseInt(counttv.getText().toString());
                if (count >= 2) {
                    count--;
                    counttv.setText(count + "");
                }
            }
        });
        //加
        shopping_bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = Integer.parseInt(counttv.getText().toString());
                if (count < 6) {
                    count++;
                    counttv.setText(count + "");
                } else {
                    Toast.makeText(PlayBfActivity.this, "一次最多送出6个礼物", Toast.LENGTH_LONG).show();
                }
            }
        });
        songview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gifid = 0;
                ischeck = false;
                setview();
                SongCouponWindow();
            }
        });
        lw01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gifid = 1;
                setview();
                lw01.setBackground(getResources().getDrawable(R.drawable.icon_bg20));
            }
        });
        lw02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gifid = 2;
                setview();
                lw02.setBackground(getResources().getDrawable(R.drawable.icon_bg20));
            }
        });
        lw03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gifid = 3;
                setview();
                lw03.setBackground(getResources().getDrawable(R.drawable.icon_bg20));
            }
        });
        lw04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gifid = 4;
                setview();
                lw04.setBackground(getResources().getDrawable(R.drawable.icon_bg20));
            }
        });
        lw05.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gifid = 5;
                setview();
                lw05.setBackground(getResources().getDrawable(R.drawable.icon_bg20));
            }
        });
        lw06.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gifid = 6;
                setview();
                lw06.setBackground(getResources().getDrawable(R.drawable.icon_bg20));
            }
        });
        lw07.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gifid = 7;
                setview();
                lw07.setBackground(getResources().getDrawable(R.drawable.icon_bg20));

            }
        });
        lw08.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gifid = 8;
                setview();
                lw08.setBackground(getResources().getDrawable(R.drawable.icon_bg20));
            }
        });
        dashan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ischeck) {
                    if (!liveRoom.getLiveRoomBean().getData().getLiveId().equals("")) {
                        count = Integer.parseInt(counttv.getText().toString());
                        if (gifid == 1) {
                            if (cashmoney >= 1 * count) {
                                for (int i = 0; i < count; i++) {
                                    Message message = mHandler.obtainMessage();
                                    message.what = GIFSHOW;
                                    mHandler.sendMessageDelayed(message, 500);
//                                    sendgif();
                                }
                            } else
                                Toast.makeText(PlayBfActivity.this, "您的金币余额不足，请充值", Toast.LENGTH_LONG).show();
                        } else if (gifid == 2) {
                            if (cashmoney >= 2 * count) {
                                for (int i = 0; i < count; i++) {
                                    Message message = mHandler.obtainMessage();
                                    message.what = GIFSHOW;
                                    mHandler.sendMessageDelayed(message, 500);
//                                    sendgif();
                                }
                            } else
                                Toast.makeText(PlayBfActivity.this, "您的金币余额不足，请充值", Toast.LENGTH_LONG).show();
                        } else if (gifid == 3) {
                            if (cashmoney >= 5 * count) {
                                for (int i = 0; i < count; i++) {
                                    Message message = mHandler.obtainMessage();
                                    message.what = GIFSHOW;
                                    mHandler.sendMessageDelayed(message, 500);
//                                    sendgif();
                                }
                            } else
                                Toast.makeText(PlayBfActivity.this, "您的金币余额不足，请充值", Toast.LENGTH_LONG).show();
                        } else if (gifid == 4) {
                            if (cashmoney >= 10 * count) {
                                for (int i = 0; i < count; i++) {
                                    Message message = mHandler.obtainMessage();
                                    message.what = GIFSHOW;
                                    mHandler.sendMessageDelayed(message, 500);
//                                    sendgif();
                                }
                            } else
                                Toast.makeText(PlayBfActivity.this, "您的金币余额不足，请充值", Toast.LENGTH_LONG).show();
                        } else if (gifid == 5) {
                            if (cashmoney >= 20 * count) {
                                for (int i = 0; i < count; i++) {
                                    Message message = mHandler.obtainMessage();
                                    message.what = GIFSHOW;
                                    mHandler.sendMessageDelayed(message, 500);
//                                    sendgif();
                                }
                            } else
                                Toast.makeText(PlayBfActivity.this, "您的金币余额不足，请充值", Toast.LENGTH_LONG).show();
                        } else if (gifid == 6) {
                            if (cashmoney >= 30 * count) {
                                for (int i = 0; i < count; i++) {
                                    Message message = mHandler.obtainMessage();
                                    message.what = GIFSHOW;
                                    mHandler.sendMessageDelayed(message, 500);
//                                    sendgif();
                                }
                            } else
                                Toast.makeText(PlayBfActivity.this, "您的金币余额不足，请充值", Toast.LENGTH_LONG).show();
                        } else if (gifid == 7) {
                            if (cashmoney >= 50 * count) {
                                for (int i = 0; i < count; i++) {
                                    Message message = mHandler.obtainMessage();
                                    message.what = GIFSHOW;
                                    mHandler.sendMessageDelayed(message, 500);
//                                    sendgif();
                                }
                            } else
                                Toast.makeText(PlayBfActivity.this, "您的金币余额不足，请充值", Toast.LENGTH_LONG).show();
                        } else if (gifid == 8) {
                            if (cashmoney >= 100 * count) {
                                for (int i = 0; i < count; i++) {
                                    Message message = mHandler.obtainMessage();
                                    message.what = GIFSHOW;
                                    mHandler.sendMessageDelayed(message, 500);
//                                    sendgif();
                                }
                            } else
                                Toast.makeText(PlayBfActivity.this, "您的金币余额不足，请充值", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(PlayBfActivity.this, "您还未选择礼物", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

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
                                new AlertView("系统消息", "该直播间解散了！", null, new String[]{"返回"}, null, PlayBfActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(Object o, int position) {
                                        finish();
                                    }
                                }).show();
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
                                        room_count.setText("人气值:" + formatLooker(roomCount++));
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


                                    List<LiveGiftBean.data.datagif> gifts = liveGiftBean.getData().getData();
                                    LiveGiftBean.data.datagif nowGif = null;
                                    for(LiveGiftBean.data.datagif item: gifts){
                                        if(text.contains(item.getName())){
                                            nowGif = item;
                                        }
                                    }


                                    //4秒后删除动画
                                    Message message2 = mHandler.obtainMessage();
                                    message2.what = INVISIBLE;
                                    mHandler.sendMessageDelayed(message2, 4000);
                                    showGift(nowGif.getId() + "", nowGif, userInfo.headPic, userInfo.nickName);
//                                    giftCount = giftCount + liwu_money[gifType - 1 < 0 ? 0 : gifType - 1];
                                    giftCount = giftCount + nowGif.getPrice();
                                    gift_count.setText("炭币" + giftCount);
                                    //播放礼物动画
                                    bigivgift.setMovieNet(nowGif.getAnimation());

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
                                        zhuboBean.getData().setFans(zhuboBean.getData().getFans() + 1);
                                        fs_count.setText("粉丝" + zhuboBean.getData().getFans());
                                        chatListAdapter.addMessage(userInfo);
                                        chatListAdapter.notifyDataSetChanged();
                                    }else {
                                        zhuboBean.getData().setFans(zhuboBean.getData().getFans() - 1);
                                        fs_count.setText("粉丝" + zhuboBean.getData().getFans());
                                    }
                                }
                            }else if(commonJson.cmd.equalsIgnoreCase(BaseRoom.MessageType.CustomKickMsg.name())){
                                //被踢了
                                ++i;
                                BaseRoom.UserInfo userInfo = new Gson().fromJson(new Gson().toJson(commonJson.data), BaseRoom.UserInfo.class);
                                if (userInfo != null && i < message.getElementCount()) {
                                    TIMElem nextElement = message.getElement(i);
                                    TIMTextElem textElem = (TIMTextElem) nextElement;
                                    String text = textElem.getText();//信息
                                    userInfo.text = text;
                                    chatListAdapter.addMessage(userInfo);
                                    chatListAdapter.notifyDataSetChanged();

                                    if(userInfo.imid.equals(SharedUtils.readImId())){//说明是自己
                                        new AlertView("系统消息", "您被主播踢出房间！", null, new String[]{"返回"}, null, PlayBfActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
                                            @Override
                                            public void onItemClick(Object o, int position) {
                                                finish();
                                            }
                                        }).show();
                                    }
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
                            }else if(commonJson.cmd.equalsIgnoreCase(BaseRoom.MessageType.CustomGameMsg.name())){
                                    //开始游戏操作 如果已经有游戏地址了 就不做这操作以免重复打开游戏
                                    if(gameUrl == null || gameUrl.equals("")){

                                        BaseRoom.UserInfo userInfo = new Gson().fromJson(new Gson().toJson(commonJson.data), BaseRoom.UserInfo.class);
                                        if(userInfo.type.equalsIgnoreCase("load")){
                                               gameUrl = userInfo.text;
                                            openGame();
//                                            Toast.makeText(PlayActivity.this, "开始游戏界面", Toast.LENGTH_SHORT).show();
                                        }
                                    }else {
                                        BaseRoom.UserInfo userInfo = new Gson().fromJson(new Gson().toJson(commonJson.data), BaseRoom.UserInfo.class);
                                        if(userInfo.type.equalsIgnoreCase("load") && !gameUrl.equals(userInfo.text)){//如果地址变了
                                            exitGame();
                                            gameUrl = userInfo.text;
                                            openGame();
//                                            Toast.makeText(PlayActivity.this, "开始游戏界面", Toast.LENGTH_SHORT).show();
                                        }else if(userInfo.type.equalsIgnoreCase("exit")){
                                            gameUrl = "";
                                            exitGame();
//                                            Toast.makeText(PlayActivity.this, "退出游戏界面", Toast.LENGTH_SHORT).show();
                                        }
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

    public void onClick(View view){
        switch (view.getId()){

            case R.id.btn_back:
                liveRoom.exitRoom(null);
                finish();
                break;
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
     * 显示礼物的方法
     */
    HashMap<String, Long> map = new HashMap<String, Long>();

    private void showGift(String gifid,LiveGiftBean.data.datagif datagif, String head, String usernmae) {
        View giftView = llgiftcontent.findViewWithTag(usernmae);

        String sendText = "";
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
                Picasso.with(PlayBfActivity.this).load(head).memoryPolicy(NO_CACHE, NO_STORE).error(R.drawable.defaltshop).into(crvheadimage);
            } else {
                crvheadimage.setBackground(getResources().getDrawable(R.drawable.defaltshop));
            }

            final MagicTextView giftNum = (MagicTextView) giftView.findViewById(R.id.giftNum);/*找到数量控件*/
            GifView gifView = (GifView) giftView.findViewById(R.id.ivgift);
//            List<LiveGiftBean.data.datagif> giftdatas = liveGiftBean.getData().getData();
//            int len = giftdatas.size();
//            i = i > len ? len : i;
            gifView.setMovieNet(datagif.getAnimation());
            sendText = "送了【"+ datagif.getName() +"】";

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
                TextView textView = (TextView) giftView.findViewById(R.id.giftype);/*找到头像控件*/
//                liveRoom.sendGroupGifMessage(usernmae, head, textView.getText().toString(), new BaseRoom.MessageCallback() {
//                    @Override
//                    public void onError(int code, String errInfo) {
//
//                    }
//
//                    @Override
//                    public void onSuccess(Object... args) {
//                    }
//                });
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
    @Override
    public void onPause() {
        super.onPause();
        liveRoom.switchToBackground();
    }
    @Override
    protected void onResume() {
        super.onResume();
// 继续
        liveRoom.switchToForeground();
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
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX", 1.3f, 1.0f);
            ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleY", 1.3f, 1.0f);
            AnimatorSet animSet = new AnimatorSet();
            lastAnimator = animSet;
            animSet.setDuration(200);
            animSet.setInterpolator(new OvershootInterpolator());
            animSet.playTogether(anim1, anim2);
            animSet.start();
        }
    }
    /**
     * 添加礼物view,(考虑垃圾回收)
     */
    private View addGiftView() {
        View view = null;
        if (giftViewCollection.size() <= 0) {
            /*如果垃圾回收中没有view,则生成一个*/
            view = LayoutInflater.from(PlayBfActivity.this).inflate(R.layout.item_gift, null);
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
    @Override
    public void onBackPressed() {
        if (!bottomPanel.onBackAction()) {
            exitRoom();
            finish();
            return;
        }
//        else if(ismove==2){
//            MoveActivity();
//        }

    }
    public void exitRoom() {

        liveRoom.exitRoom(null);
        mView.onDestroy();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        //关闭游戏
        if (mLivePlayer != null) {
            mLivePlayer.stopPlay(true);
            mLivePlayer = null;
        }
        if (gameCaptureView != null){
            gameCaptureView.onDestroy();
            gameCaptureView = null;
        }
        mPlayConfig = null;
        if(gameWebView !=null){
            gameWebView.removeAllViews();
            gameWebView.destroy();
        }

        showDanmaku = false;
        if (danmaku_view != null) {
            danmaku_view.release();
            danmaku_view = null;
        }
        JSCallback jsCallback = JSCallBaskManager.get(key);
        if(jsCallback != null){
            jsCallback.invoke(new com.rzico.weex.model.info.Message().success(liveRoom.getLiveRoomBean()));
            JSCallBaskManager.remove(key);
        }
        exitRoom();

        if(liveRoom.getLiveRoomBean()==null){

        }else {
            HashMap<String, Object> params = new HashMap<>();
            params.put("id", liveRoom.getLiveRoomBean().getData().getLiveId());
            new XRequest((BaseActivity) mContext, "/weex/live/quit.jhtml", XRequest.POST, params).setOnRequestListener(new HttpRequest.OnRequestListener() {
                @Override
                public void onSuccess(BaseActivity activity, String result, String type) {
                    Log.e("live", result);
                }

                @Override
                public void onFail(BaseActivity activity, String cacheData, int code) {
                    Log.e("live", code + "");
                }
            }).execute();
        }

    }


//    游戏方法定义开始

    private void initGame() {

//        if(ll_game == null){

//        }

        gameCaptureView = (TXCloudVideoView) findViewById(R.id.game_video_view);
        gameWebView = (WebView) findViewById(R.id.gameWebView);

        mCurrentRenderMode     = TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION;
        mCurrentRenderRotation = TXLiveConstants.RENDER_ROTATION_PORTRAIT;

        if (mLivePlayer == null){
            mLivePlayer = new TXLivePlayer(this);
        }

        gameCaptureView.setBackgroundColor(getResources().getColor(R.color.black));
        gameCaptureView.setLogMargin(12, 12, 110, 60);
        gameCaptureView.showLog(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            gameWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else { gameWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        WebSettings set = gameWebView.getSettings();
        set.setLoadWithOverviewMode(true);
        set.setUseWideViewPort(true);
        set.setDomStorageEnabled(true);
        set.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        set.setTextZoom(100);
        gameWebView.getSettings().setJavaScriptEnabled(true);
        gameWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        gameWebView.getSettings().setSupportMultipleWindows(true);
        gameWebView.setWebChromeClient(new WebChromeClient());
        gameWebView.clearCache(true);
        gameWebView.getBackground().setAlpha(0);
        gameWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view,String url) {
                if(gameWebView !=null ){

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        gameWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    } else { gameWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    }
                    gameWebView.setBackgroundColor(Color.TRANSPARENT);
                }

//                LivePlayerBean livePlayerBean = (LivePlayerBean) getIntent().getSerializableExtra("livePlayerParam");


                //开始播放
                if (livePlayerBean != null && livePlayerBean.getVideo()!=null && !"".equals(livePlayerBean.getVideo())) {
                    mIsPlaying = startPlay(livePlayerBean.getVideo());
                } else {
                    mIsPlaying = false;
                }


            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                boolean canHerf = true;
                System.out.print(url);

                if(url.length()<35 && url.contains("/home")){
                    //关闭当前页面
//                    finish();
                    canHerf = true;
                } else if(url.startsWith("http")){
                    return false;
                } else if(url.startsWith("https")){
                    return false;
                } else
                if(url.startsWith("nihvolbutton://bluefrog")){
                    canHerf = false;

                }

                return canHerf;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            }
        });


        mActivityType = getIntent().getIntExtra("PLAY_TYPE", ACTIVITY_TYPE_LIVE_PLAY);

        mPlayConfig = new TXLivePlayConfig();
//        checkPublishPermission();
        mCurrentRenderMode = TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN;
        mLivePlayer.setRenderMode(mCurrentRenderMode);
    }

    private boolean startPlay(String url) {
//        String playUrl = "rtmp://47.75.7.152:1935/HKRepeaterBC01/video";
        String playUrl = url;

        if (!checkPlayUrl(playUrl)) {
            return false;
        }

//        mRootView.setBackgroundColor(0xff000000);

        mLivePlayer.setPlayerView(gameCaptureView);

        mLivePlayer.setPlayListener(new ITXLivePlayListener() {
            @Override
            public void onPlayEvent(int event, Bundle param) {
                String playEventLog = "receive event: " + event + ", " + param.getString(TXLiveConstants.EVT_DESCRIPTION);
                Log.d("OpenVideoActivity", playEventLog);

                if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
//                    stopLoadingAnimation();
                    Log.d("AutoMonitor", "PlayFirstRender,cost=" +(System.currentTimeMillis()-mStartPlayTS));
                } else if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT || event == TXLiveConstants.PLAY_EVT_PLAY_END) {
                    stopPlay();
                } else if (event == TXLiveConstants.PLAY_EVT_PLAY_LOADING){
//                    startLoadingAnimation();
                } else if (event == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {
//                    stopLoadingAnimation();
                } else if (event == TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION) {

                } else if (event == TXLiveConstants.PLAY_EVT_CHANGE_ROTATION) {
                    return;
                }

                if (event < 0) {
                    Toast.makeText(getApplicationContext(), param.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNetStatus(Bundle status) {
                String str = getNetStatusString(status);
                Log.d("OpenVideoActivity", "Current status, CPU:"+status.getString(TXLiveConstants.NET_STATUS_CPU_USAGE)+
                        ", RES:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH)+"*"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT)+
                        ", SPD:"+status.getInt(TXLiveConstants.NET_STATUS_NET_SPEED)+"Kbps"+
                        ", FPS:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_FPS)+
                        ", ARA:"+status.getInt(TXLiveConstants.NET_STATUS_AUDIO_BITRATE)+"Kbps"+
                        ", VRA:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_BITRATE)+"Kbps");
            }
        });
        // 硬件加速在1080p解码场景下效果显著，但细节之处并不如想象的那么美好：
        // (1) 只有 4.3 以上android系统才支持
        // (2) 兼容性我们目前还仅过了小米华为等常见机型，故这里的返回值您先不要太当真
        mLivePlayer.enableHardwareDecode(mHWDecode);
        mLivePlayer.setRenderRotation(mCurrentRenderRotation);
        mLivePlayer.setRenderMode(mCurrentRenderMode);
        //设置播放器缓存策略
        //这里将播放器的策略设置为自动调整，调整的范围设定为1到4s，您也可以通过setCacheTime将播放器策略设置为采用
        //固定缓存时间。如果您什么都不调用，播放器将采用默认的策略（默认策略为自动调整，调整范围为1到4s）
        //mLivePlayer.setCacheTime(5);

        mLivePlayer.setConfig(mPlayConfig);

        int result = mLivePlayer.startPlay(playUrl,mPlayType); // result返回值：0 success;  -1 empty url; -2 invalid url; -3 invalid playType;
        if (result != 0) {
//            mRootView.setBackgroundResource(R.drawable.main_bkg);
            return false;
        }

        Log.w("video render","timetrack start play");



        mStartPlayTS = System.currentTimeMillis();

        return true;
    }
    private boolean checkPlayUrl(final String playUrl) {
        if (TextUtils.isEmpty(playUrl) || (!playUrl.startsWith("http://") && !playUrl.startsWith("https://") && !playUrl.startsWith("rtmp://")  && !playUrl.startsWith("/"))) {
            Toast.makeText(getApplicationContext(), "播放地址不合法，直播目前仅支持rtmp,flv播放方式!", Toast.LENGTH_SHORT).show();
            return false;
        }

        switch (mActivityType) {
            case ACTIVITY_TYPE_LIVE_PLAY:
            {
                if (playUrl.startsWith("rtmp://")) {
                    mPlayType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
                } else if ((playUrl.startsWith("http://") || playUrl.startsWith("https://"))&& playUrl.contains(".flv")) {
                    mPlayType = TXLivePlayer.PLAY_TYPE_LIVE_FLV;
                } else {
                    Toast.makeText(getApplicationContext(), "播放地址不合法，直播目前仅支持rtmp,flv播放方式!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            break;
            case ACTIVITY_TYPE_REALTIME_PLAY:
                mPlayType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP_ACC;
                break;
            default:
                Toast.makeText(getApplicationContext(), "播放地址不合法，目前仅支持rtmp,flv播放方式!", Toast.LENGTH_SHORT).show();
                return false;
        }
        return true;
    }
    private  void stopPlay() {
//        mRootView.setBackgroundResource(R.drawable.main_bkg);

        //关闭游戏
        if (mLivePlayer != null) {
            mLivePlayer.stopPlay(true);
            mLivePlayer = null;
        }
        if (gameCaptureView != null){
            gameCaptureView.onDestroy();
            gameCaptureView = null;
        }
        mPlayConfig = null;
        if(gameWebView!=null){
            gameWebView.loadUrl("about:blank");
            gameWebView.removeAllViews();
        }



        downAllView();
        mIsPlaying = false;
    }

    private void openGame() {
        new XRequest(PlayBfActivity.this, gameUrl, XRequest.GET, new HashMap<String, Object>()).setOnRequestListener(new HttpRequest.OnRequestListener() {
            @Override
            public void onSuccess(BaseActivity activity, String result, String type) {
                GameBean gameBean = new Gson().fromJson(result, GameBean.class);
                //连接直播以及游戏页面
                if(livePlayerBean == null && gameBean != null){
                    game_frame.setVisibility(VISIBLE);
                    livePlayerBean = new LivePlayerBean();
                    livePlayerBean.setUrl(gameBean.getData().getUrl());
                    livePlayerBean.setVideo(gameBean.getData().getVideo());
                    initGame();
                    gameWebView.postUrl(livePlayerBean.getUrl(), null);
                    upAllView();
                }
            }

            @Override
            public void onFail(BaseActivity activity, String cacheData, int code) {

            }
        }).execute();
    }

    public void exitGame(){
                //退出游戏界面
                gameUrl = null;
                livePlayerBean = null;
                game_frame.setVisibility(GONE);
                stopPlay();

    }


    protected String getNetStatusString(Bundle status) {
        String str = String.format("%-14s %-14s %-12s\n%-8s %-8s %-8s %-8s\n%-14s %-14s\n%-14s %-14s",
                "CPU:"+status.getString(TXLiveConstants.NET_STATUS_CPU_USAGE),
                "RES:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH)+"*"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT),
                "SPD:"+status.getInt(TXLiveConstants.NET_STATUS_NET_SPEED)+"Kbps",
                "JIT:"+status.getInt(TXLiveConstants.NET_STATUS_NET_JITTER),
                "FPS:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_FPS),
                "GOP:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_GOP)+"s",
                "ARA:"+status.getInt(TXLiveConstants.NET_STATUS_AUDIO_BITRATE)+"Kbps",
                "QUE:"+status.getInt(TXLiveConstants.NET_STATUS_CODEC_CACHE)
                        +"|"+status.getInt(TXLiveConstants.NET_STATUS_CACHE_SIZE)
                        +","+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_CACHE_SIZE)
                        +","+status.getInt(TXLiveConstants.NET_STATUS_V_DEC_CACHE_SIZE)
                        +"|"+status.getInt(TXLiveConstants.NET_STATUS_AV_RECV_INTERVAL)
                        +","+status.getInt(TXLiveConstants.NET_STATUS_AV_PLAY_INTERVAL)
                        +","+String.format("%.1f", status.getFloat(TXLiveConstants.NET_STATUS_AUDIO_PLAY_SPEED)).toString(),
                "VRA:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_BITRATE)+"Kbps",
                "SVR:"+status.getString(TXLiveConstants.NET_STATUS_SERVER_IP),
                "AUDIO:"+status.getString(TXLiveConstants.NET_STATUS_AUDIO_INFO));
        return str;
    }

    //开始游戏时将控件上移


    private  boolean isUpAll = false;
    public void upAllView(){
        if(!isUpAll){
            upView(chat_listview);
            upView(bottomPanel.getView());
            upView(mHeartLayout);
//            upView(zhibo_bottom_layout);
            isUpAll = true;
        }
    }
    public void downAllView(){
        if(isUpAll){
            downView(chat_listview);
            downView(bottomPanel.getView());
            downView(mHeartLayout);
//            downView(zhibo_bottom_layout);
            isUpAll = false;
        }
    }
    //结束游戏时将控件下移

//    控件上移方法

    public void upView(View view){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.bottomMargin += DensityUtil.dip2px(PlayBfActivity.this, 230);;
        view.setLayoutParams(params);
    }

    public void downView(View view){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.bottomMargin -= DensityUtil.dip2px(PlayBfActivity.this, 230);;
        view.setLayoutParams(params);
    }
}
