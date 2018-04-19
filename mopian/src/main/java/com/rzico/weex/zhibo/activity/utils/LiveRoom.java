package com.rzico.weex.zhibo.activity.utils;

/**
 * Created by Jinlesoft on 2018/3/29.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.rzico.weex.Constant;
import com.rzico.weex.model.event.MessageBus;
import com.rzico.weex.zhibo.view.utils.EmojiManager;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMGroupManager;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.ugc.TXRecordCommon;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;


public class LiveRoom extends BaseRoom {

    private static final int                LIVEROOM_ROLE_NONE      = 0;
    private static final int                LIVEROOM_ROLE_PUSHER    = 1;
    private static final int                LIVEROOM_ROLE_PLAYER    = 2;

    private TXLivePlayer mTXLivePlayer;
    private boolean                         mBackground             = false;

    private TXLivePlayConfig mTXLivePlayConfig;

    private Runnable mJoinPusherTimeoutTask;

    private static final int                LIVEROOM_CAMERA_PREVIEW    = 0;
    private int                             mPreviewType               = LIVEROOM_CAMERA_PREVIEW;
    private int                             mSelfRoleType           = LIVEROOM_ROLE_NONE;

    /**
     * LiveRoom 直播房间
     */
    public LiveRoom(Context context) {
        super(context);
        mTXLivePlayConfig = new TXLivePlayConfig();
        mTXLivePlayer = new TXLivePlayer(context);
        EmojiManager.init(context);

        mTXLivePlayConfig.setAutoAdjustCacheTime(true);
        mTXLivePlayConfig.setMaxAutoAdjustCacheTime(1);
        mTXLivePlayConfig.setMinAutoAdjustCacheTime(5);
        mTXLivePlayer.setConfig(mTXLivePlayConfig);
        mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        mTXLivePlayer.setPlayListener(new ITXLivePlayListener() {
            @Override
            public void onPlayEvent(int event, Bundle param) {
                if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {
                } else if (event == TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION) {
                    int width = param.getInt(TXLiveConstants.EVT_PARAM1, 0);
                    int height = param.getInt(TXLiveConstants.EVT_PARAM2, 0);
                    if (width > 0 && height > 0) {
                        float ratio = (float) height / width;
                        //pc上混流后的宽高比为4:5，这种情况下填充模式会把左右的小主播窗口截掉一部分，用适应模式比较合适
                        if (ratio > 1.3f) {
                            mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
                        } else {
                            mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
                        }
                    }
                }
            }

            @Override
            public void onNetStatus(Bundle status) {

            }
        });
    }



    /**
     * 启动摄像头预览
     * @param videoView 摄像头预览组件
     */
    public synchronized void startLocalPreview(final @NonNull TXCloudVideoView videoView) {
        super.startLocalPreview(videoView);
        mPreviewType = LIVEROOM_CAMERA_PREVIEW;
    }

    /**
     * LiveRoom 创建房间Callback
     */
    public interface CreateRoomCallback {
        void onError(int errCode, String errInfo);
        void onSuccess(String name);
    }

    /**
     * 停止摄像头预览
     */
    public synchronized void stopLocalPreview() {
        super.stopLocalPreview();
    }
    /**
     * 创建房间
     * @param roomInfo  房间信息
     * @param callback        房间创建完成的回调，里面会携带roomID
     */
    public void createRoom(final String roomID, final String roomInfo, final CreateRoomCallback callback) {
        //暂时写这个
        startPushStream(getLiveRoomBean().getData().getPushUrl(), TXLiveConstants.VIDEO_QUALITY_HIGH_DEFINITION, new PusherStreamCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                callback.onError(errCode, errInfo);
            }

            @Override
            public void onSuccess() {
                //推流过程中，可能会重复收到PUSH_EVT_PUSH_BEGIN事件，onSuccess可能会被回调多次，如果已经创建的房间，直接返回
                if (mCurrRoomID != null && mCurrRoomID.length() > 0) {
                    return;
                }

                if (mTXLivePusher != null) {
                    TXLivePushConfig config = mTXLivePusher.getConfig();
                    config.setVideoEncodeGop(5);
                    mTXLivePusher.setConfig(config);
                }

                mBackground = false;
                //4.推流成功，请求CGI:create_room，获取roomID、roomSig
                doCreateRoom(roomID, roomInfo, new BaseRoom.CreateRoomCallback() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        callback.onError(errCode, errInfo);
                    }

                    @Override
                    public void onSuccess(final String newRoomID) {
                        //创建房间成功
//                        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
//                            @Override
//                            public void onError(int i, String s) {
//
//                            }
//
//                            @Override
//                            public void onSuccess(TIMUserProfile timUserProfile) {

                                sendGroupTextMessage(getLiveRoomBean().getData().getNickname(), getLiveRoomBean().getData().getHeadpic(), "创建房间成功！", new MessageCallback() {
                                    @Override
                                    public void onError(int code, String errInfo) {

                                        Log.e("error", code + errInfo);
                                    }

                                    @Override
                                    public void onSuccess(Object... args) {

                                    }
                                });
//                            }
//                        });
                        //5.请求CGI：add_pusher，加入房间
                        mCurrRoomID = newRoomID;

                        //6.调用IM的joinGroup，加入群组
                        jionGroup(newRoomID, new JoinGroupCallback() {
                            @Override
                            public void onError(int errCode, String errInfo) {
                                callback.onError(errCode, errInfo);
                            }

                            @Override
                            public void onSuccess() {
                                callback.onSuccess(newRoomID);
                            }
                        });
//                        addPusher(newRoomID, , new AddPusherCallback() {
//                            @Override
//                            public void onError(int errCode, String errInfo) {
//                                callback.onError(errCode, errInfo);
//                            }
//
//                            @Override
//                            public void onSuccess() {
//
//                            }
//                        });
                    }
                });
            }
        });
    }


    /**
     * 从前台切换到后台，关闭采集摄像头数据，推送默认图片
     */
    public void switchToBackground(){
        super.switchToBackground();
        mBackground = true;
//        if (mSelfRoleType == LIVEROOM_ROLE_PLAYER) {
//            if (mCurrRoomID != null && mCurrRoomID.length() > 0) {
//                mTXLivePlayer.stopPlay(true);
//            }
//        }
    }

    /**
     * 由后台切换到前台，开启摄像头数据采集
     */
    public void switchToForeground(){
        super.switchToForeground();
        mBackground = false;
//        if (mSelfRoleType == LIVEROOM_ROLE_PLAYER) {
//
//        }
//
//        if (mTXLivePusher != null && mTXLivePusher.isPushing()) {
//        }
    }

    /**
     * LiveRoom 进入房间Callback
     */
    public interface EnterRoomCallback{
        void onError(int errCode, String errInfo);
        void onSuccess();
    }


    /**
     * LiveRoom 进入房间
     * @param roomID    房间号
     * @param cb        进入房间完成的回调
     */
    public void enterRoom(@NonNull final String roomID, @NonNull final TXCloudVideoView videoView, final EnterRoomCallback cb) {
        mSelfRoleType = LIVEROOM_ROLE_PLAYER;
        mCurrRoomID   = roomID;

        final MainCallback<EnterRoomCallback, Object> callback = new MainCallback<EnterRoomCallback, Object>(cb);

        // 调用IM的joinGroup
        jionGroup(roomID, new JoinGroupCallback() {
            @Override
            public void onError(int code, String errInfo) {
                callback.onError(code, errInfo);
            }

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        String mixedPlayUrl = getMixedPlayUrlByRoomID(roomID);
                        String mixedPlayUrl = getLiveRoomBean().getData().getPlayUrl();
                        if (mixedPlayUrl != null && mixedPlayUrl.length() > 0) {
                            int playType = getPlayType(mixedPlayUrl);
                            mTXLivePlayer.setPlayerView(videoView);
                            mTXLivePlayer.startPlay(mixedPlayUrl, playType);

                            //这里告诉服务器加入房间了
                            callback.onSuccess();
                        }
                        else {
                            callback.onError(-1, "房间不存在");
                        }
                    }
                });
            }
        });
    }

    /**
     * LiveRoom 离开房间Callback
     */
    public interface ExitRoomCallback{
        void onError(int errCode, String errInfo);
        void onSuccess();
    }
    /**
     * 离开房间
     * @param callback 离开房间完成的回调
     */
    public void exitRoom(final ExitRoomCallback callback) {
        final MainCallback cb = new MainCallback<ExitRoomCallback, Object>(callback);

        //1. 结束心跳
        mHeartBeatThread.stopHeartbeat();

        //2. 调用IM的quitGroup
        quitGroup(mCurrRoomID, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {

                if(callback!=null)
                callback.onError(i, s);
            }

            @Override
            public void onSuccess() {
                if(callback!=null)
                callback.onSuccess();
            }
        });


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //3. 结束本地推流
                if (mPreviewType == LIVEROOM_CAMERA_PREVIEW) {
                    stopLocalPreview();
                } else {
                    stopScreenCapture();
                }

                //4. 结束所有加速流的播放
                cleanPlayers();

                //5. 结束普通流播放
                if (mTXLivePlayer != null) {
                    mTXLivePlayer.stopPlay(true);
                    mTXLivePlayer.setPlayerView(null);
                }
            }
        });

        //6. 退出房间：请求CGI:delete_pusher，把自己从房间成员列表里删除 ===============


        mSelfRoleType = LIVEROOM_ROLE_NONE;
        mCurrRoomID   = "";

        cb.onSuccess();
    }

    /**
     * 结束录屏.
     *
     */
    public synchronized void stopScreenCapture() {
        if (mTXLivePusher != null) {
            mTXLivePusher.setPushListener(null);
            mTXLivePusher.stopScreenCapture();
            mTXLivePusher.stopPusher();
            mTXLivePusher = null;
        }
    }

    public boolean turnOnFlashLight(boolean enable){
        return super.turnOnFlashLight(enable);
    }
    /**
     * 切换摄像头
     */
    public void switchCamera() {
        super.switchCamera();
    }

    /**
     * 静音设置
     * @param isMute 静音变量, true 表示静音， 否则 false
     */
    public void setMute(boolean isMute) {
        super.setMute(isMute);
    }

    /**
     * 设置美颜效果.
     * @param style          美颜风格.三种美颜风格：0 ：光滑  1：自然  2：朦胧
     * @param beautyLevel    美颜等级.美颜等级即 beautyLevel 取值为0-9.取值为0时代表关闭美颜效果.默认值:0,即关闭美颜效果.
     * @param whiteningLevel 美白等级.美白等级即 whiteningLevel 取值为0-9.取值为0时代表关闭美白效果.默认值:0,即关闭美白效果.
     * @param ruddyLevel     红润等级.美白等级即 ruddyLevel 取值为0-9.取值为0时代表关闭美白效果.默认值:0,即关闭美白效果.
     * @return               是否成功设置美白和美颜效果. true:设置成功. false:设置失败.
     */
    public boolean setBeautyFilter(int style, int beautyLevel, int whiteningLevel, int ruddyLevel) {
        return super.setBeautyFilter(style, beautyLevel, whiteningLevel, ruddyLevel);
    }

    /**
     * 调整摄像头焦距
     * @param  value 焦距，取值 0~getMaxZoom();
     * @return  true : 成功 false : 失败
     */
    public boolean setZoom(int value) {
        return super.setZoom(value);
    }

    /**
     * 设置播放端水平镜像与否(tips：推流端前置摄像头默认看到的是镜像画面，后置摄像头默认看到的是非镜像画面)
     * @param enable true:播放端看到的是镜像画面,false:播放端看到的是镜像画面
     */
    public boolean setMirror(boolean enable) {
        return super.setMirror(enable);
    }

    /**
     * 调整曝光
     * @param value 曝光比例，表示该手机支持最大曝光调整值的比例，取值范围从-1到1。
     *              负数表示调低曝光，-1是最小值，对应getMinExposureCompensation。
     *              正数表示调高曝光，1是最大值，对应getMaxExposureCompensation。
     *              0表示不调整曝光
     */
    public void setExposureCompensation(float value) {
        super.setExposureCompensation(value);
    }

    /**
     * 设置麦克风的音量大小.
     * <p>该接口用于混音处理,比如将背景音乐与麦克风采集到的声音混合后播放.
     * @param x: 音量大小,1为正常音量,建议值为0~2,如果需要调大音量可以设置更大的值.
     * @return 是否成功设置麦克风的音量大小. true:设置麦克风的音量成功. false:设置麦克风的音量失败.
     */
    public boolean setMicVolume(float x) {
        return super.setMicVolume(x);
    }

    /**
     * 设置背景音乐的音量大小.
     * <p>该接口用于混音处理,比如将背景音乐与麦克风采集到的声音混合后播放.
     * @param x 音量大小,1为正常音量,建议值为0~2,如果需要调大背景音量可以设置更大的值.
     * @return  是否成功设置背景音乐的音量大小. true:设置背景音的音量成功. false:设置背景音的音量失败.
     */
    public boolean setBGMVolume(float x) {
        return super.setBGMVolume(x);
    }

    public void setBGMNofify(TXLivePusher.OnBGMNotify notify){
        super.setBGMNofify(notify);
    }

    /**
     * 获取音乐文件时长.
     *
     * @param path
     *          音乐文件路径
     *          path == null 获取当前播放歌曲时长
     *          path != null 获取path路径歌曲时长
     * @return
     *        音乐文件时长,单位ms.
     */
    public int getMusicDuration(String path) {
        return super.getMusicDuration(path);
    }

    /**
     * 播放背景音乐.
     * <p>该接口用于混音处理,比如将背景音乐与麦克风采集到的声音混合后播放.
     *
     * @param path
     *          背景音乐文件路径.
     * @return
     *      是否成功播放背景音乐. true:播放成功. false:播放失败.
     */
    public boolean playBGM(String path) {
        if (mTXLivePusher != null) {
            return mTXLivePusher.playBGM(path);
        }
        return false;
    }

    /**
     * 停止播放背景音乐.
     *
     * <p>该接口用于混音处理,比如将背景音乐与麦克风采集到的声音混合后播放.
     *
     * @return
     *      是否成功停止播放背景音乐. true:停止播放成功. false:停止播放失败.
     */
    public boolean stopBGM() {
        return super.stopBGM();
    }

    /**
     * 暂停播放背景音乐.
     *
     * <p>该接口用于混音处理,比如将背景音乐与麦克风采集到的声音混合后播放.
     *
     * @return
     *      是否成功暂停播放背景音乐. true:暂停播放成功. false:暂停播放失败.
     */
    public boolean pauseBGM() {
        return super.pauseBGM();
    }

    /**
     * 恢复播放背景音乐.
     *
     * <p>该接口用于混音处理,比如将背景音乐与麦克风采集到的声音混合后播放.
     *
     * @return
     *      是否成功恢复播放背景音乐. true:恢复播放成功. false:恢复播放失败.
     */
    public boolean resumeBGM() {
        return super.resumeBGM();
    }

    /**
     * setFilterImage 设置指定素材滤镜特效
     * @param bmp: 指定素材，即颜色查找表图片。注意：一定要用png图片格式！！！
     *           demo用到的滤镜查找表图片位于RTMPAndroidDemo/app/src/main/res/drawable-xxhdpi/目录下。
     */
    public void setFilter(Bitmap bmp) {
        super.setFilter(bmp);
    }

    /**
     * 设置动效贴图文件位置
     * @param specialValue
     */
    public void setMotionTmpl(String specialValue) {
        super.setMotionTmpl(specialValue);
    }

    /**
     * 设置绿幕文件:目前图片支持jpg/png，视频支持mp4/3gp等Android系统支持的格式
     * API要求18
     * @param file ：绿幕文件位置，支持两种方式：
     *             1.资源文件放在assets目录，path直接取文件名
     *             2.path取文件绝对路径
     * @return false:调用失败
     *         true:调用成功
     */
    public boolean setGreenScreenFile(String file) {
        return super.setGreenScreenFile(file);
    }

    /**
     * 设置大眼效果.
     * @param level
     *          大眼等级取值为0-9.取值为0时代表关闭美颜效果.默认值:0
     */
    public void setEyeScaleLevel(int level) {
        if (mTXLivePusher != null) {
            mTXLivePusher.setEyeScaleLevel(level);
        }
    }

    /**
     * 设置瘦脸效果.
     * @param level
     *          瘦脸等级取值为0-9.取值为0时代表关闭美颜效果.默认值:0
     */
    public void setFaceSlimLevel(int level) {
        super.setFaceSlimLevel(level);
    }

    /**
     * V 脸
     * @param level
     */
    public void setFaceVLevel(int level) {
        super.setFaceVLevel(level);
    }

    /**
     * setSpecialRatio 设置滤镜效果程度
     * @param ratio: 从0到1，越大滤镜效果越明显，默认取值0.5
     */
    public void setSpecialRatio(float ratio) {
        super.setSpecialRatio(ratio);
    }

    /**
     * 缩脸
     * @param level
     */
    public void setFaceShortLevel(int level) {
        super.setFaceShortLevel(level);
    }

    /**
     * 下巴
     * @param scale
     */
    public void setChinLevel(int scale) {
        super.setChinLevel(scale);
    }

    /**
     * 小鼻
     * @param scale
     */
    public void setNoseSlimLevel(int scale) {
        super.setNoseSlimLevel(scale);
    }


    /**
     * 混响
     * @param reverbType
     */
    public void setReverb(int reverbType) {
        super.setReverb(reverbType);
    }

    /**
     * 设置从前台切换到后台，推送的图片
     * @param bitmap
     */
    public void setPauseImage(final @Nullable Bitmap bitmap) {
        super.setPauseImage(bitmap);
    }

    /**
     * 从前台切换到后台，关闭采集摄像头数据
     * @param id 设置默认显示图片的资源文件
     */
    public void setPauseImage(final @IdRes int id){
        super.setPauseImage(id);
    }

    /**
     * 录制回调接口，需要在启动播放后设置才生效
     *
     * @param listener
     *         录制回调接口.
     */
    public void setVideoRecordListener(TXRecordCommon.ITXVideoRecordListener listener) {
        if (mTXLivePlayer != null ) {
            mTXLivePlayer.setVideoRecordListener(listener);
        }
    }

    /**
     * 启动视频录制
     *
     * @param recordType
     * @return 0表示成功，非0表示失败
     */
    public int startRecord(int recordType) {
        if (mTXLivePlayer != null ) {
            return mTXLivePlayer.startRecord(recordType);
        }

        return -1;
    }

    /**
     * 停止视频录制.
     *
     * @return 0表示成功，非0表示失败 .
     */
    public int stopRecord() {
        if (mTXLivePlayer != null) {
            return mTXLivePlayer.stopRecord();
        }
        return -1;
    }

    /**
     * 更新自己的用户信息
     * @param userName      昵称
     * @param userAvatar    头像
     */
    public void updateSelfUserInfo(String userName, String userAvatar) {
        super.updateSelfUserInfo(userName, userAvatar);
    }

    @Override
    protected void invokeDebugLog(String log) {

    }

    @Override
    protected void invokeError(int errorCode, String errorMessage) {

    }

 private class JoinPusherRequest {
        public String type;
        public String roomID;
        public String userID;
        public String userName;
        public String userAvatar;
    }

    private class JoinPusherResponse {
        public String type;
        public String roomID;
        public String result;
        public String message;
    }

    private class KickoutResponse {
        public String type;
        public String roomID;
    }
}
