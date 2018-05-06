package com.rzico.weex.zhibo.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.AdapterView;
import android.widget.Button;
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
import com.rzico.weex.model.event.MessageBus;
import com.rzico.weex.model.info.BaseEntity;
import com.rzico.weex.model.info.BasePage;
import com.rzico.weex.model.info.NoticeInfo;
import com.rzico.weex.model.zhibo.LiveGiftBean;
import com.rzico.weex.model.zhibo.LiveRoomBean;
import com.rzico.weex.model.zhibo.NoticeBean;
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
import com.rzico.weex.zhibo.view.HeartLayout;
import com.rzico.weex.zhibo.view.InputPanel;
import com.rzico.weex.zhibo.view.MagicTextView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
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
import com.tencent.rtmp.ui.TXCloudVideoView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.rzico.weex.zhibo.view.GifView;

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

public class PlayActivity extends BaseActivity {

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
        new XRequest(PlayActivity.this, "weex/live/into.jhtml", XRequest.POST, params).setOnRequestListener(new HttpRequest.OnRequestListener() {
            @Override
            public void onSuccess(BaseActivity activity, String result, String type) {
                LiveRoomBean data = new Gson().fromJson(result, LiveRoomBean.class);
                if(data != null){
                    liveRoom.setLiveRoomBean(data);
                    //获取用户信息
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("id", data.getData().getLiveMemberId());
                    //获取用户信息
                    new XRequest(PlayActivity.this, "weex/user/view.jhtml", XRequest.GET, params).setOnRequestListener(new HttpRequest.OnRequestListener() {
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
                    new XRequest(PlayActivity.this, "weex/live/notice/list.jhtml", XRequest.GET,new HashMap<String, Object>()).setOnRequestListener(new HttpRequest.OnRequestListener() {
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
//                        room_count.setText(在线:--roomCount + "人" );
//                    }else{
                        room_count.setText("在线:" + formatLooker(roomCount));
//                    }
                    room_count.setVisibility(VISIBLE);
                    gift_count.setText("印票" + giftCount);
                    gift_count.setVisibility(VISIBLE);
//                    if(data.getData().getFrontcover() !=null && !data.getData().getFrontcover().equals("")){
//                        Picasso.with(PlayActivity.this).load(data.getData().getFrontcover()).into(head_image);
//                    }else {
//                        Picasso.with(PlayActivity.this).load("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3994969733,336727888&fm=27&gp=0.jpg").into(head_image);
//                    }
//                    head_image.setVisibility(VISIBLE);
                    //设置房间名称和 主播昵称信息
                    Picasso.with(PlayActivity.this).load(data.getData().getHeadpic()).into(head_icon);


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
        new XRequest(PlayActivity.this, "/weex/user/view.jhtml", XRequest.GET, params2).setOnRequestListener(new HttpRequest.OnRequestListener() {
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
        new XRequest(PlayActivity.this, "/weex/live/gift/list.jhtml", XRequest.GET, new HashMap<String, Object>()).setOnRequestListener(new HttpRequest.OnRequestListener() {
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
                    liveRoom.showUserInfo(PlayActivity.this, liveRoom.getLiveRoomBean().getData().getLiveMemberId(), true);
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
                    liveRoom.showUserInfo(PlayActivity.this, pid, true);
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
                    new XRequest(PlayActivity.this, "/weex/member/follow/delete.jhtml", XRequest.POST,params).setOnRequestListener(new HttpRequest.OnRequestListener() {
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
                    new XRequest(PlayActivity.this, "/weex/member/follow/add.jhtml", XRequest.POST,params).setOnRequestListener(new HttpRequest.OnRequestListener() {
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
                        new XRequest(PlayActivity.this, "weex/live/gift/barrage.jhtml", XRequest.POST, params).setOnRequestListener(new HttpRequest.OnRequestListener() {
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
                Picasso.with(PlayActivity.this).load(datagifs.get(i).getThumbnail()).into(lws.get(i));
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
            lw_ll.startAnimation(AnimationUtils.loadAnimation(PlayActivity.this, R.anim.out_from_bottom));
            songCouponview.dismiss();
        } else {
            jifen_tv.setText(cashmoney + "");
            counttv.setText("1");
            gifid = 0;
            lw_ll.startAnimation(AnimationUtils.loadAnimation(PlayActivity.this, R.anim.in_from_bottom));
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
                    new XRequest(PlayActivity.this, "weex/live/gift/submit.jhtml", XRequest.POST, params).setOnRequestListener(new HttpRequest.OnRequestListener() {
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
                    Toast.makeText(PlayActivity.this, "一次最多送出6个礼物", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(PlayActivity.this, "您的金币余额不足，请充值", Toast.LENGTH_LONG).show();
                        } else if (gifid == 2) {
                            if (cashmoney >= 2 * count) {
                                for (int i = 0; i < count; i++) {
                                    Message message = mHandler.obtainMessage();
                                    message.what = GIFSHOW;
                                    mHandler.sendMessageDelayed(message, 500);
//                                    sendgif();
                                }
                            } else
                                Toast.makeText(PlayActivity.this, "您的金币余额不足，请充值", Toast.LENGTH_LONG).show();
                        } else if (gifid == 3) {
                            if (cashmoney >= 5 * count) {
                                for (int i = 0; i < count; i++) {
                                    Message message = mHandler.obtainMessage();
                                    message.what = GIFSHOW;
                                    mHandler.sendMessageDelayed(message, 500);
//                                    sendgif();
                                }
                            } else
                                Toast.makeText(PlayActivity.this, "您的金币余额不足，请充值", Toast.LENGTH_LONG).show();
                        } else if (gifid == 4) {
                            if (cashmoney >= 10 * count) {
                                for (int i = 0; i < count; i++) {
                                    Message message = mHandler.obtainMessage();
                                    message.what = GIFSHOW;
                                    mHandler.sendMessageDelayed(message, 500);
//                                    sendgif();
                                }
                            } else
                                Toast.makeText(PlayActivity.this, "您的金币余额不足，请充值", Toast.LENGTH_LONG).show();
                        } else if (gifid == 5) {
                            if (cashmoney >= 20 * count) {
                                for (int i = 0; i < count; i++) {
                                    Message message = mHandler.obtainMessage();
                                    message.what = GIFSHOW;
                                    mHandler.sendMessageDelayed(message, 500);
//                                    sendgif();
                                }
                            } else
                                Toast.makeText(PlayActivity.this, "您的金币余额不足，请充值", Toast.LENGTH_LONG).show();
                        } else if (gifid == 6) {
                            if (cashmoney >= 30 * count) {
                                for (int i = 0; i < count; i++) {
                                    Message message = mHandler.obtainMessage();
                                    message.what = GIFSHOW;
                                    mHandler.sendMessageDelayed(message, 500);
//                                    sendgif();
                                }
                            } else
                                Toast.makeText(PlayActivity.this, "您的金币余额不足，请充值", Toast.LENGTH_LONG).show();
                        } else if (gifid == 7) {
                            if (cashmoney >= 50 * count) {
                                for (int i = 0; i < count; i++) {
                                    Message message = mHandler.obtainMessage();
                                    message.what = GIFSHOW;
                                    mHandler.sendMessageDelayed(message, 500);
//                                    sendgif();
                                }
                            } else
                                Toast.makeText(PlayActivity.this, "您的金币余额不足，请充值", Toast.LENGTH_LONG).show();
                        } else if (gifid == 8) {
                            if (cashmoney >= 100 * count) {
                                for (int i = 0; i < count; i++) {
                                    Message message = mHandler.obtainMessage();
                                    message.what = GIFSHOW;
                                    mHandler.sendMessageDelayed(message, 500);
//                                    sendgif();
                                }
                            } else
                                Toast.makeText(PlayActivity.this, "您的金币余额不足，请充值", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(PlayActivity.this, "您还未选择礼物", Toast.LENGTH_LONG).show();
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
                                new AlertView("系统消息", "该直播间解散了！", null, new String[]{"返回"}, null, PlayActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
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
//                                    int gifType = 0;
//                                    //播放GIF动画
//                                    if(text.contains("送了【666】")){
//                                        gifType = 1;
//                                    }else if(text.contains("送了【棒棒糖】")){
//                                        gifType = 2;
//                                    }else if(text.contains("送了【爱心】")){
//                                        gifType = 3;
//                                    }else if(text.contains("送了【玫瑰】")){
//                                        gifType = 4;
//                                    }else if(text.contains("送了【么么哒】")){
//                                        gifType = 5;
//                                    }else if(text.contains("送了【萌萌哒】")){
//                                        gifType = 6;
//                                    }else if(text.contains("送了【甜甜圈】")){
//                                        gifType = 7;
//                                    }else if(text.contains("送了【女神称号】")){
//                                        gifType = 8;
//                                    }
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
//                                    giftCount = giftCount + liwu_money[gifType - 1 < 0 ? 0 : gifType - 1];
                                    giftCount = giftCount + nowGif.getPrice();
                                    gift_count.setText("印票" + giftCount);
                                    //播放礼物动画
                                        bigivgift.setMovieNet(nowGif.getAnimation());
//                                    if (bigivgift.getVisibility() == VISIBLE) {
//                                        bigivgift.setPaused(true);
////                                        bigivgift.setMovieResource(liwu_gif[gifType - 1]);
//                                        bigivgift.setMovieNet(nowGif.getAnimation());
//                                        bigivgift.setPaused(false);
//                                    } else {
//                                        bigivgift.setVisibility(VISIBLE);
////                                        bigivgift.setMovieResource(liwu_gif[gifType - 1]);
//                                        bigivgift.setMovieNet(nowGif.getAnimation());
//                                        bigivgift.setPaused(false);
//                                    }
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
                                        new AlertView("系统消息", "您被主播踢出房间！", null, new String[]{"返回"}, null, PlayActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
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





//    private int[] liwu_gif = {R.raw.gg1,R.raw.gg2,R.raw.gg3,R.raw.gg4,R.raw.gg5,R.raw.gg6,R.raw.gg7,R.raw.gg8};
//    private int[] liwu_money = {1, 3, 5, 10, 20, 30, 50, 100};
//    public void sendgif(){
//
//        //这里需要请求接口赠送礼物 请求成功了以后 开始动画
//
//        //4秒后删除动画
//        Message message = mHandler.obtainMessage();
//        message.what = INVISIBLE;
//        mHandler.sendMessageDelayed(message, 4000);
//        showGift(gifid + "", gifid, userpic, username);
//
//        //播放礼物动画
//        if (bigivgift.getVisibility() == VISIBLE) {
//            bigivgift.setPaused(true);
//            bigivgift.setMovieResource(liwu_gif[gifid - 1]);
//            bigivgift.setPaused(false);
//        } else {
//            bigivgift.setVisibility(VISIBLE);
//            bigivgift.setMovieResource(liwu_gif[gifid - 1]);
//            bigivgift.setPaused(false);
//        }
//
//    }

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
                Picasso.with(PlayActivity.this).load(head).memoryPolicy(NO_CACHE, NO_STORE).error(R.drawable.defaltshop).into(crvheadimage);
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
            view = LayoutInflater.from(PlayActivity.this).inflate(R.layout.item_gift, null);
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
}
