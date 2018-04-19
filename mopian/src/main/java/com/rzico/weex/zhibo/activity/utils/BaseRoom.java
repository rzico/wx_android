package com.rzico.weex.zhibo.activity.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rzico.weex.Constant;
import com.rzico.weex.R;
import com.rzico.weex.activity.BaseActivity;
import com.rzico.weex.model.event.MessageBus;
import com.rzico.weex.model.info.Message;
import com.rzico.weex.model.zhibo.LiveRoomBean;
import com.rzico.weex.module.JSCallBaskManager;
import com.rzico.weex.net.HttpRequest;
import com.rzico.weex.net.XRequest;
import com.rzico.weex.utils.SharedUtils;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMGroupManager;
import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMGroupMemberRoleType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.group.TIMGroupManagerExt;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.ui.widget.DanmakuView;

/**
 * Created by dennyfeng on 2017/12/8.
 */

public abstract class BaseRoom {

    public static String ROOM_SERVICE_DOMAIN = "https://room.qcloud.com/weapp/";;

    private static String tag = "BaseRoom";


    public final static  String ISFOLLOW = "关注了主播";
    public final static  String UNFOLLOW = "取消关注主播";
    public final static  String ISKICK   = "被主播踢出房间";
    public final static String  ISGAG    = "被禁言了";
    public final static String  UNGAG    = "被解除禁言了";

    protected Context mContext;
    protected Handler mHandler;
    private LiveRoomBean liveRoomBean;

    protected long                          mAppID;
    protected SelfAccountInfo               mSelfAccountInfo;
    protected String mCurrRoomID;

    protected TXLivePusher mTXLivePusher;
    protected TXLivePushListenerImpl        mTXLivePushListener;

    protected HeartBeatThread               mHeartBeatThread;       //心跳

    protected HashMap<String, PlayerItem> mPlayers                = new LinkedHashMap<>();



    public BaseRoom(Context context) {
        mContext = context.getApplicationContext();
        mHandler = new Handler(mContext.getMainLooper());

        mHeartBeatThread = new HeartBeatThread();
        //监听直播聊天消息

    }

    public LiveRoomBean getLiveRoomBean() {
        return liveRoomBean;
    }

    public void setLiveRoomBean(LiveRoomBean liveRoomBean) {
        this.liveRoomBean = liveRoomBean;
    }

    public void switchCamera() {
        if (mTXLivePusher != null) {
            mTXLivePusher.switchCamera();
        }
    }

    public boolean turnOnFlashLight(boolean enable) {
        if (mTXLivePusher != null) {
            return mTXLivePusher.turnOnFlashLight(enable);
        }
        return false;
    }

    public void setMute(boolean isMute) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setMute(isMute);
        }
    }

    public boolean setBeautyFilter(int style, int beautyLevel, int whiteningLevel, int ruddyLevel) {
        if (mTXLivePusher != null) {
            return mTXLivePusher.setBeautyFilter(style, beautyLevel, whiteningLevel, ruddyLevel);
        }
        return false;
    }

    public boolean setZoom(int value) {
        if (mTXLivePusher != null) {
            return mTXLivePusher.setZoom(value);
        }
        return false;
    }

    public boolean setMirror(boolean enable) {
        if (mTXLivePusher != null) {
            return mTXLivePusher.setMirror(enable);
        }
        return false;
    }

    public void setExposureCompensation(float value) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setExposureCompensation(value);
        }
    }

    public boolean setMicVolume(float x) {
        if (mTXLivePusher != null) {
            return mTXLivePusher.setMicVolume(x);
        }
        return false;
    }

    public boolean setBGMVolume(float x) {
        if (mTXLivePusher != null) {
            return mTXLivePusher.setBGMVolume(x);
        }
        return false;
    }

    public void setBGMNofify(TXLivePusher.OnBGMNotify notify){
        if (mTXLivePusher != null) {
            mTXLivePusher.setBGMNofify(notify);
        }
    }

    public int getMusicDuration(String path) {
        if (mTXLivePusher != null) {
            return mTXLivePusher.getMusicDuration(path);
        }
        return 0;
    }

    public boolean playBGM(String path) {
        if (mTXLivePusher != null) {
            return mTXLivePusher.playBGM(path);
        }
        return false;
    }

    public boolean stopBGM() {
        if (mTXLivePusher != null) {
            return mTXLivePusher.stopBGM();
        }
        return false;
    }

    public boolean pauseBGM() {
        if (mTXLivePusher != null) {
            return mTXLivePusher.pauseBGM();
        }
        return false;
    }

    public boolean resumeBGM() {
        if (mTXLivePusher != null) {
            return mTXLivePusher.resumeBGM();
        }
        return false;
    }

    public void setFilter(Bitmap bmp) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setFilter(bmp);
        }
    }

    public void setMotionTmpl(String specialValue) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setMotionTmpl(specialValue);
        }
    }

    public boolean setGreenScreenFile(String file) {
        if (mTXLivePusher != null) {
            return mTXLivePusher.setGreenScreenFile(file);
        }
        return false;
    }

    public void setEyeScaleLevel(int level) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setEyeScaleLevel(level);
        }
    }

    public void setFaceSlimLevel(int level) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setFaceSlimLevel(level);
        }
    }

    public void setFaceVLevel(int level) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setFaceVLevel(level);
        }
    }

    public void setSpecialRatio(float ratio) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setSpecialRatio(ratio);
        }
    }

    public void setFaceShortLevel(int level) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setFaceShortLevel(level);
        }
    }

    public void setChinLevel(int scale) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setChinLevel(scale);
        }
    }

    public void setNoseSlimLevel(int scale) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setNoseSlimLevel(scale);
        }
    }

    public void setReverb(int reverbType) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setReverb(reverbType);
        }
    }

    public void setPauseImage(final @Nullable Bitmap bitmap) {
        if (mTXLivePusher != null) {
            TXLivePushConfig config = mTXLivePusher.getConfig();
            config.setPauseImg(bitmap);
            config.setPauseFlag(TXLiveConstants.PAUSE_FLAG_PAUSE_VIDEO | TXLiveConstants.PAUSE_FLAG_PAUSE_AUDIO);
            mTXLivePusher.setConfig(config);
        }
    }

    public void setPauseImage(final @IdRes int id){
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), id);
        if (mTXLivePusher != null) {
            TXLivePushConfig config = mTXLivePusher.getConfig();
            config.setPauseImg(bitmap);
            config.setPauseFlag(TXLiveConstants.PAUSE_FLAG_PAUSE_VIDEO | TXLiveConstants.PAUSE_FLAG_PAUSE_AUDIO);
            mTXLivePusher.setConfig(config);
        }
    }

    public void updateSelfUserInfo(String userName, String userAvatar) {
        if (mSelfAccountInfo != null) {
            mSelfAccountInfo.userName = userName;
            mSelfAccountInfo.userAvatar = userAvatar;
        }
    }

    public synchronized void startLocalPreview(final @NonNull TXCloudVideoView videoView) {
        invokeDebugLog("[BaseRoom] startLocalPreview");
        initLivePusher();
        if (mTXLivePusher != null) {
            videoView.setVisibility(View.VISIBLE);
            mTXLivePusher.startCameraPreview(videoView);
        }
    }

    public void switchToBackground(){
        invokeDebugLog("[BaseRoom] onPause");

        if (mTXLivePusher != null && mTXLivePusher.isPushing()) {
            mTXLivePusher.pausePusher();
        }

        synchronized (this) {
            for (Map.Entry<String, PlayerItem> entry : mPlayers.entrySet()) {
                entry.getValue().pause();
            }
        }
    }

    public void switchToForeground(){
        invokeDebugLog("[BaseRoom] onResume");

        if (mTXLivePusher != null && mTXLivePusher.isPushing()) {
            mTXLivePusher.resumePusher();
        }

        synchronized (this) {
            for (Map.Entry<String, PlayerItem> entry : mPlayers.entrySet()) {
                entry.getValue().resume();
            }
        }
    }

    public synchronized void stopLocalPreview() {
        if (mTXLivePusher != null) {
            mTXLivePusher.setPushListener(null);
            mTXLivePusher.stopCameraPreview(true);
            mTXLivePusher.stopPusher();
            mTXLivePusher = null;
        }

        unInitLivePusher();
    }
    protected void initLivePusher() {
        if (mTXLivePusher == null) {
            TXLivePushConfig config = new TXLivePushConfig();
//            config.setPauseFlag(TXLiveConstants.PAUSE_FLAG_PAUSE_VIDEO | TXLiveConstants.PAUSE_FLAG_PAUSE_AUDIO);
            config.setPauseFlag(10);
            config.setPauseImg(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.pause_publish));

            mTXLivePusher = new TXLivePusher(this.mContext);
            mTXLivePusher.setConfig(config);
            mTXLivePusher.setBeautyFilter(TXLiveConstants.BEAUTY_STYLE_SMOOTH, 5, 3, 2);

            mTXLivePushListener = new TXLivePushListenerImpl();
            mTXLivePusher.setPushListener(mTXLivePushListener);
        }
    }

    protected void unInitLivePusher() {
        if (mTXLivePusher != null) {
            mTXLivePushListener = null;
            mTXLivePusher.setPushListener(null);
            mTXLivePusher.stopCameraPreview(true);
            mTXLivePusher.stopPusher();
            mTXLivePusher = null;
        }
        destroyGroup(mCurrRoomID);

    }

    protected interface PusherStreamCallback {
        void onError(int errCode, String errInfo);
        void onSuccess();
    }

    protected void startPushStream(final String url, final int videoQuality, final PusherStreamCallback callback){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mTXLivePushListener != null) {
                    if (mTXLivePushListener.cameraEnable() == false) {
                        callback.onError(-1, "获取摄像头权限失败");
                        return;
                    }
                    if (mTXLivePushListener.micEnable() == false) {
                        callback.onError(-1, "获取麦克风权限失败");
                        return;
                    }
                }
                if (mTXLivePusher != null) {
                    invokeDebugLog("[BaseRoom] 开始推流 PushUrl = " + url);
                    mTXLivePushListener.setCallback(callback);
                    mTXLivePusher.setVideoQuality(videoQuality, false, false);
                    mTXLivePusher.startPusher(url);
                }
            }
        });
    }

    protected void startPushStream(final String url, final PusherStreamCallback callback){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mTXLivePushListener != null) {
                    if (mTXLivePushListener.cameraEnable() == false) {
                        callback.onError(-1, "获取摄像头权限失败");
                        return;
                    }
                    if (mTXLivePushListener.micEnable() == false) {
                        callback.onError(-1, "获取麦克风权限失败");
                        return;
                    }
                }
                if (mTXLivePusher != null) {
                    invokeDebugLog("[BaseRoom] 开始推流 PushUrl = " + url);
                    mTXLivePushListener.setCallback(callback);
                    mTXLivePusher.startPusher(url);
                }
            }
        });
    }

    /**
     * 退出IM群组
     * @param groupId  群ID
     * @param callback
     */
    public void quitGroup(final String groupId, final TIMCallBack callback){


        this.mHandler.post(new Runnable() {
            @Override
            public void run() {
                TIMGroupManager.getInstance().quitGroup(groupId, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        if (i == 10010) {
//                            printDebugLog("群 {%s} 已经解散了", groupId);
                            onSuccess();
                        }
                        else{
//                            printDebugLog("退出群 {%s} 失败： %s(%d)", groupId, s, i);
                            callback.onError(i, s);
                        }
                    }

                    @Override
                    public void onSuccess() {
//                        printDebugLog("退出群 {%s} 成功", groupId);

                        mCurrRoomID = groupId;
                        callback.onSuccess();
                    }
                });
            }
        });
    }
    /**
     * 销毁IM群组
     * @param groupId  群ID
     */
    private void destroyGroup(final String groupId){
        this.mHandler.post(new Runnable() {
            @Override
            public void run() {
                TIMGroupManager.getInstance().deleteGroup(groupId, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
//                        printDebugLog("解散群 {%s} 失败：%s(%d)", groupId, s, i);
                        Log.e("error", i + s);
                    }

                    @Override
                    public void onSuccess() {
//                        printDebugLog("解散群 {%s} 成功", groupId);
                        mCurrRoomID = groupId;
                    }
                });
            }
        });
    }
    protected interface CreateRoomCallback {
        void onError(int errCode, String errInfo);
        void onSuccess(String roomID);
    }


    protected void doCreateRoom(final String roomID, String roomInfo, final CreateRoomCallback callback){
        //这里应该向服务器申请看有没有群 有群就不创建

        //roomID 准备方群id 目前不是自定义群id
//创建公开群，且不自定义群 ID
        TIMGroupManager.CreateGroupParam param = new TIMGroupManager.CreateGroupParam("AVChatRoom", roomInfo);
//指定群简介
        param.setIntroduction(roomInfo);
//指定群公告
        param.setNotification("welcome to our group");
        param.setGroupId(roomID);
//创建群组
        TIMGroupManager.getInstance().createGroup(param, new TIMValueCallBack<String>() {
            @Override
            public void onError(int code, String desc) {
//                Log.d(tag, "create group failed. code: " + code + " errmsg: " + desc);
                if(code == 10025 || code == 10021){
                    //该错误是因为该用户已经创建了这个群号，暂时先返回旧的
                    callback.onSuccess(roomID);
                }else {
                    callback.onError(code, desc);
                }

            }

            @Override
            public void onSuccess(String s) {
                Log.d(tag, "create group succ, groupId:" + s);


                callback.onSuccess(s);
            }
        });
    }

    protected interface JoinGroupCallback {
        void onError(int errCode, String errInfo);
        void onSuccess();
    }

    protected void jionGroup(final String roomID, final JoinGroupCallback callback){
        mCurrRoomID = roomID;
            //这里是向IM请求加入群主
        TIMGroupManager.getInstance().applyJoinGroup(roomID, "some reason", new TIMCallBack() {

            @Override
            public void onError(int i, String s) {
                Log.i(tag, s);
                callback.onError(i,s);

            }

            @Override
            public void onSuccess() {
                Log.i(tag, "join group");
                callback.onSuccess();
            }
        });
    }

    protected interface AddPusherCallback {
        void onError(int errCode, String errInfo);
        void onSuccess();
    }

//    protected void addPusher(final String roomID, final String pushURL, final AddPusherCallback callback) {
//        //这里要请求服务器 告诉服务器群id
//        callback.onSuccess();
//    }


    protected void cleanPlayers() {
        synchronized (this) {
            for (Map.Entry<String, PlayerItem> entry : mPlayers.entrySet()) {
                entry.getValue().destroy();
            }
            mPlayers.clear();
        }
    }

    protected void runOnUiThread(final Runnable runnable){
        if (mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    runnable.run();
                }
            });
        }
    }


    /**
     * 函数级公共Callback定义
     */
    public interface MessageCallback{
        void onError(int code, String errInfo);
        void onSuccess(Object ...args);
    }
    public enum MessageType{
        CustomNoticeMsg,//这个消息是每个用户一进去的推送消息 不需要发送
        CustomTextMsg,
        CustomGifMsg,
        CustomGagMsg,//经验
        CustomKickMsg,//踢人
        CustomFollowMsg,//关注啦
        CustomBarrageMsg,//弹幕
    }
    public void sendGroupGifMessage(final @NonNull String userName, final @NonNull String headPic, final @NonNull String text, final MessageCallback callback){
        UserInfo userInfo = new UserInfo();
        userInfo.nickName = userName;
        userInfo.headPic = headPic;
        userInfo.text = text;
        sendGroupMessage(userInfo, MessageType.CustomGifMsg, callback);
    }
    public void sendGroupTextMessage(final @NonNull String userName, final @NonNull String headPic, final @NonNull String text, final MessageCallback callback){
        UserInfo userInfo = new UserInfo();
        userInfo.nickName = userName;
        userInfo.headPic = headPic;
        userInfo.text = text;
        sendGroupMessage(userInfo, MessageType.CustomTextMsg, callback);
    }
    public void sendGroupGapMessage(UserInfo userInfo, final MessageCallback callback){
        sendGroupMessage(userInfo, MessageType.CustomGagMsg, callback);
    }
    public void sendGroupKickMessage(UserInfo userInfo, final MessageCallback callback){
        sendGroupMessage(userInfo, MessageType.CustomKickMsg, callback);
    }

    public void sendGroupFollowMessage(final @NonNull String userName, final @NonNull String headPic, final @NonNull String text, final MessageCallback callback){
        UserInfo userInfo = new UserInfo();
        userInfo.nickName = userName;
        userInfo.headPic = headPic;
        userInfo.text = text;
        sendGroupMessage(userInfo, MessageType.CustomFollowMsg, callback);
    }
    public void sendGroupBarrageMessage( final @NonNull String userName, final @NonNull String headPic, final @NonNull String text, final MessageCallback callback){
        UserInfo userInfo = new UserInfo();
        userInfo.nickName = userName;
        userInfo.headPic = headPic;
        userInfo.text = text;
        sendGroupMessage(userInfo, MessageType.CustomBarrageMsg, callback);
    }


    /**
     * 发送IM群文本消息
     * @param callback
     */
    public void sendGroupMessage(final UserInfo userInfo, final MessageType messageType, final MessageCallback callback){


        this.mHandler.post(new Runnable() {
            @Override
            public void run() {
                TIMMessage message = new TIMMessage();
                try {
                    CommonJson<UserInfo> txtHeadMsg = new CommonJson<UserInfo>();
                    txtHeadMsg.cmd = messageType.name();
                    txtHeadMsg.data = userInfo;
                    //一下可能是多余的
                    if(messageType == MessageType.CustomKickMsg && userInfo.id!=null && userInfo.id != 0L){
                        txtHeadMsg.data.id = userInfo.id;
                        txtHeadMsg.data.imid = SharedUtils.idToImId(userInfo.id);
                    }else if(messageType == MessageType.CustomGagMsg && userInfo.id!=null && userInfo.id != 0L){
                        txtHeadMsg.data.id = userInfo.id;
                        txtHeadMsg.data.imid = SharedUtils.idToImId(userInfo.id);
                    }else{//如果不是KICK和 GAG 就是不传对方的id
                        txtHeadMsg.data.id = SharedUtils.readLoginId();
                        txtHeadMsg.data.imid = SharedUtils.idToImId(txtHeadMsg.data.id);
                    }
//                    txtHeadMsg.data.nickName = userName;
//                    txtHeadMsg.data.headPic = headPic;
                    txtHeadMsg.data.cmd = messageType.name();
                    String strCmdMsg = new Gson().toJson(txtHeadMsg, new TypeToken<CommonJson<UserInfo>>(){}.getType());

                    TIMCustomElem customElem = new TIMCustomElem();
                    customElem.setData(strCmdMsg.getBytes("UTF-8"));

                    TIMTextElem textElem = new TIMTextElem();
                    textElem.setText(userInfo.text);

                    message.addElement(customElem);
                    message.addElement(textElem);
                }
                catch (Exception e) {
//                    printDebugLog("[sendGroupTextMessage] 发送群{%s}消息失败，组包异常", mGroupID);
                    if (callback != null) {
                        callback.onError(-1, "发送群消息失败");
                    }
                    return;
                }

                TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, mCurrRoomID);
                conversation.sendMessage(message, new TIMValueCallBack<TIMMessage>() {
                    @Override
                    public void onError(int i, String s) {
//                        printDebugLog("[sendGroupTextMessage] 发送群{%s}消息失败: %s(%d)", mGroupID, s, i);

                        if (callback != null)
                            callback.onError(i, s);
                    }

                    @Override
                    public void onSuccess(TIMMessage timMessage) {
//                        printDebugLog("[sendGroupTextMessage] 发送群消息成功");
                        EventBus.getDefault().post(new MessageBus(MessageBus.Type.ZHIBOCHAT, timMessage));
                        if (callback != null)
                            callback.onSuccess(timMessage);
                    }
                });
            }
        });
    }
    protected void runOnUiThreadDelay(final Runnable runnable, long delayMills){
        if (mHandler != null) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runnable.run();
                }
            }, delayMills);
        }
    }


    protected int getPlayType(String playUrl) {
        int playType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
        if (playUrl.startsWith("rtmp://")) {
            playType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
        } else if ((playUrl.startsWith("http://") || playUrl.startsWith("https://")) && playUrl.contains(".flv")) {
            playType = TXLivePlayer.PLAY_TYPE_LIVE_FLV;
        }
        return playType;
    }

    protected class SelfAccountInfo {
        public String userID;
        public String userName;
        public String userAvatar;
        public String userSig;
        public String accType;
        public long     sdkAppID;

        public SelfAccountInfo(String userID, String userName, String headPicUrl, String userSig, String accType, long sdkAppID) {
            this.userID = userID;
            this.userName = userName;
            this.userAvatar = headPicUrl;
            this.userSig = userSig;
            this.accType = accType;
            this.sdkAppID = sdkAppID;
        }
    }

    private  class PlayerItem {
        public TXCloudVideoView view;
        public TXLivePlayer player;

        public PlayerItem(TXCloudVideoView view, TXLivePlayer player) {
            this.view = view;
            this.player = player;
        }

        public void resume(){
            this.player.resume();
        }

        public void pause(){
            this.player.pause();
        }

        public void destroy(){
            this.player.stopPlay(true);
            this.view.onDestroy();
        }
    }
    /**
     * sp转px的方法。
     */
    public int sp2px(float spValue) {
        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
    /**
     * 向弹幕View中添加一条弹幕
     * @param content
     *          弹幕的具体内容
     * @param  withBorder
     *          弹幕是否有边框
     */
    public void addDanmaku(DanmakuView danmaku_view, DanmakuContext danmakuContext, String content, boolean withBorder) {
        BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        danmaku.text = content;
        danmaku.padding = 5;
        danmaku.textSize = sp2px(16);
        danmaku.textColor = 0xFFEF9A01;//金色
        danmaku.setTime(danmaku_view.getCurrentTime());
        if (withBorder) {
            danmaku.borderColor = Color.GREEN;
        }
        danmaku_view.addDanmaku(danmaku);
    }
    private void openUserInfo(final BaseActivity activity, Long userId, final boolean isUser){
        String url =  "file://view/live/host.js?id=" + userId + "&isUser=" + isUser + "&groupId=" + mCurrRoomID;
        String key = String.valueOf(System.currentTimeMillis());
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
        activity.startActivity(intent);
    }
    public void showGiftList(final BaseActivity activity, final Long liveId){

        String url =  "file://view/live/gifts.js?liveId=" + liveId ;
        String key = String.valueOf(System.currentTimeMillis());
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
        activity.startActivity(intent);
    }

    public void showUserInfo(final BaseActivity activity, final Long userId, final boolean isUser){
        if(userId == null){
            activity.showToast("获取用户信息失败");
            return;
        }

        if(isUser){
            List<String> userIds = new ArrayList<>();
            userIds.add("u" + (10200 + userId));
            TIMGroupManagerExt.getInstance().getGroupMembersInfo(mCurrRoomID, userIds, new TIMValueCallBack<List<TIMGroupMemberInfo>>() {
                @Override
                public void onError(int i, String s) {
                    openUserInfo(activity, userId, isUser);
                }

                @Override
                public void onSuccess(List<TIMGroupMemberInfo> timGroupMemberInfos) {
                    if(timGroupMemberInfos.size()>0){
                        long s = timGroupMemberInfos.get(0).getSilenceSeconds();
                        openUserInfo(activity, userId,  isUser);
                    }
                }

            });
        }else {
            openUserInfo(activity, userId, isUser);
        }
    }

    public static class CommonJson<T> {
        public String cmd;
        public T      data;
    }
    public static final class UserInfo {
        public Long id;//这个是用于查看用户个人信息的
        public String imid;//这个是禁言跟提出成员使用的
        public String nickName;
//        public String groupId;
        public String headPic;
        public String text;//发送的信息
        public String cmd;//消息类型
        public String time;//被禁言时长
    }
    protected class HeartBeatThread extends HandlerThread {
        private Handler handler;
        private String userID;
        private String roomID;
        private boolean running = false;

        public HeartBeatThread() {
            super("HeartBeatThread");
            this.start();
            handler = new Handler(this.getLooper());
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }

        public void setRoomID(String roomID) {
            this.roomID = roomID;
        }

        private Runnable heartBeatRunnable = new Runnable() {
            @Override
            public void run() {

            }
        };

        public boolean running() {
            return running;
        }

        public void startHeartbeat(){
            running = true;
            handler.postDelayed(heartBeatRunnable, 1000);
        }

        public void stopHeartbeat(){
            running = false;
            handler.removeCallbacks(heartBeatRunnable);
        }
    }

    private class TXLivePushListenerImpl implements ITXLivePushListener {
        private boolean mCameraEnable = true;
        private boolean mMicEnable = true;
        private PusherStreamCallback mCallback = null;

        public void setCallback(PusherStreamCallback callback) {
            mCallback = callback;
        }

        public boolean cameraEnable() {
            return mCameraEnable;
        }

        public boolean micEnable() {
            return mMicEnable;
        }

        @Override
        public void onPushEvent(int event, Bundle param) {
            if (event == TXLiveConstants.PUSH_EVT_PUSH_BEGIN) {
                invokeDebugLog("[BaseRoom] 推流成功");
                if (mCallback != null) {
                    mCallback.onSuccess();
                }
            } else if (event == TXLiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL) {
                mCameraEnable = false;
                invokeDebugLog("[BaseRoom] 推流失败：打开摄像头失败");
                if (mCallback != null) {
                    mCallback.onError(-1, "获取摄像头权限失败");
                }
                else {
                    invokeError(-1, "获取摄像头权限失败");
                }
            } else if (event == TXLiveConstants.PUSH_ERR_OPEN_MIC_FAIL) {
                mMicEnable = false;
                invokeDebugLog("[BaseRoom] 推流失败：打开麦克风失败");
                if (mCallback != null) {
                    mCallback.onError(-1, "获取麦克风权限失败");
                }
                else {
                    invokeError(-1, "获取麦克风权限失败");
                }
            } else if (event == TXLiveConstants.PUSH_ERR_NET_DISCONNECT) {
                invokeDebugLog("[BaseRoom] 推流失败：网络断开");
                invokeError(-1, "网络断开，推流失败");
            }
        }

        @Override
        public void onNetStatus(Bundle status) {

        }
    }

    protected class MainCallback<C, T> {

        private C callback;

        public MainCallback(C callback) {
            this.callback = callback;
        }

        public void onError(final int errCode, final String errInfo) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Method onError = callback.getClass().getMethod("onError", int.class, String.class);
                        onError.invoke(callback, errCode, errInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        public void onSuccess(final T obj) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Method onSuccess = callback.getClass().getMethod("onSuccess", obj.getClass());
                        onSuccess.invoke(callback, obj);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        public void onSuccess() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Method onSuccess = callback.getClass().getMethod("onSuccess");
                        onSuccess.invoke(callback);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    protected class CustomMessage{
        public String userName;
        public String userAvatar;
        public String cmd;
        public String msg;
    }


    protected abstract void invokeDebugLog(String log);

    protected abstract void invokeError(int errorCode, String errorMessage);
}
