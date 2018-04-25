package com.rzico.weex.module;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.rzico.weex.activity.BaseActivity;
import com.rzico.weex.activity.LivePlayerActivity;
import com.rzico.weex.model.LivePlayerBean;
import com.rzico.weex.model.event.MessageBus;
import com.rzico.weex.model.info.Message;
import com.rzico.weex.model.zhibo.LiveGiftBean;
import com.rzico.weex.net.HttpRequest;
import com.rzico.weex.net.XRequest;
import com.rzico.weex.utils.SharedUtils;
import com.rzico.weex.zhibo.activity.OpenVideoActivity;
import com.rzico.weex.zhibo.activity.PlayActivity;
import com.rzico.weex.zhibo.activity.utils.BaseRoom;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.group.TIMGroupManagerExt;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.rzico.weex.zhibo.activity.utils.BaseRoom.ISGAG;
import static com.rzico.weex.zhibo.activity.utils.BaseRoom.ISKICK;
import static com.rzico.weex.zhibo.activity.utils.BaseRoom.UNGAG;

/**
 * Created by Jinlesoft on 2018/2/1.
 */

public class LivePlayerModule extends WXModule {


    @JSMethod
    public void loadUrl(String url, String video, String method, JSCallback callback){
        LivePlayerBean livePlayerBean = new LivePlayerBean();
        livePlayerBean.setUrl(url);
        livePlayerBean.setVideo(video);
        livePlayerBean.setMethod(method);
        String key = System.currentTimeMillis() + "";
        JSCallBaskManager.put(key, callback);
        Intent intent = new Intent(mWXSDKInstance.getContext(), LivePlayerActivity.class);
        intent.putExtra("livePlayerParam",  livePlayerBean);
        intent.putExtra("key", key);
        mWXSDKInstance.getContext().startActivity(intent);
    }
    @JSMethod
    public void toPlayLiveRoom(final String id, final boolean play, final boolean record, final JSCallback callback){
        toPlayLiveRoom(id, play, record, "", "",callback);
    }

    @JSMethod
    public void toPlayLiveRoom(final String id, final boolean play, final boolean record,final String title,final String frontcover, final JSCallback callback){
        Dexter.withActivity( (BaseActivity) mWXSDKInstance.getContext()).withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Dexter.withActivity( (BaseActivity) mWXSDKInstance.getContext()).withPermission(Manifest.permission.RECORD_AUDIO)
                                .withListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted(PermissionGrantedResponse response) {
                                        Intent intent = new Intent(mWXSDKInstance.getContext(), OpenVideoActivity.class);
                                        String key = String.valueOf(System.currentTimeMillis());
                                        JSCallBaskManager.put(key, callback);
                                        intent.putExtra("liveId", id);
                                        intent.putExtra("isPlay", play);
                                        intent.putExtra("key", key);
                                        intent.putExtra("record", record);
                                        intent.putExtra("title", title);
                                        intent.putExtra("frontcover", frontcover);
                                        mWXSDKInstance.getContext().startActivity(intent);
                                    }

                                    @Override
                                    public void onPermissionDenied(PermissionDeniedResponse response) {
                                        ((BaseActivity)mWXSDKInstance.getContext()).showDeniedDialog("需要相机和麦克风权限,才能开始直播");
                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
                                                                                   PermissionToken token) {
                                        //用户不允许权限，向用户解释权限左右
                                        token.continuePermissionRequest();
                                    }
                                }).check();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        ((BaseActivity)mWXSDKInstance.getContext()).showDeniedDialog("需要相机和麦克风权限,才能开始直播");
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
                                                                   PermissionToken token) {
                        //用户不允许权限，向用户解释权限左右
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public BaseActivity getActivity(){
        return (BaseActivity) mWXSDKInstance.getContext();
    }
    @JSMethod
    public void toLookLiveRoom(final String id,JSCallback callback){
//        HashMap<String, Object> params = new HashMap<>();
//        params.put("id", "57");
//        params.put("lat", "");
//        params.put("lng", "");
//        //进入直播间
//        new XRequest(getActivity(), "/weex/user/view.jhtml", XRequest.GET, params).setOnRequestListener(new HttpRequest.OnRequestListener() {
//            @Override
//            public void onSuccess(BaseActivity activity, String result, String type) {
        String key = String.valueOf(System.currentTimeMillis());
        JSCallBaskManager.put(key, callback);

        Intent intent = new Intent(mWXSDKInstance.getContext(), PlayActivity.class);
        intent.putExtra("liveId", id);
        intent.putExtra("key", key);
        getActivity().startActivity(intent);
//            }
//
//            @Override
//            public void onFail(BaseActivity activity, String cacheData, int code) {
//                getActivity().showToast("进入房间失败");
//            }
//        }).execute();

    }
    /**
     * 获取指定的群成员的群内信息
     */
    @JSMethod
    public void getGroupUserInfo(String id, String groupId, final JSCallback callback){

        List<String> userIds = new ArrayList<>();
        userIds.add(id);
        TIMGroupManagerExt.getInstance().getGroupMembersInfo(groupId, userIds, new TIMValueCallBack<List<TIMGroupMemberInfo>>() {
            @Override
            public void onError(int i, String s) {
                Message message = new Message().error();
                callback.invoke(message);
            }

            @Override
            public void onSuccess(List<TIMGroupMemberInfo> timGroupMemberInfos) {
                Message message = new Message().success(timGroupMemberInfos.get(0));
                callback.invoke(message);
            }
        });
    }


//    @JSMethod
//    public void setAdamin(String id,String groupId,boolean set, final JSCallback callback){
//
//    }

    @JSMethod
        public void getGag(String userId, String groupId, final JSCallback callback){
        List<String> userIds = new ArrayList<>();
        userIds.add(userId);
        TIMGroupManagerExt.getInstance().getGroupMembersInfo(groupId, userIds, new TIMValueCallBack<List<TIMGroupMemberInfo>>() {
            @Override
            public void onError(int i, String s) {
                callback.invoke(new Message().error("获取失败"));
            }

            @Override
            public void onSuccess(List<TIMGroupMemberInfo> timGroupMemberInfos) {
                long s = timGroupMemberInfos.get(0).getSilenceSeconds();
                if(s > 100 && s < System.currentTimeMillis()){
                    callback.invoke(new Message().success(true));//被禁言了
                }else {
                    callback.invoke(new Message().success(false));//没被禁言
                }
            }

        });
    }
    /**
     *
     * @param userId
     * @param groupId
     * @param callback
     */
    @JSMethod
    public void toGag(final String userId, final String nickName, String groupId, String time, final JSCallback callback){
        //禁言 100 秒
        BaseRoom.UserInfo userInfo = new BaseRoom.UserInfo();
        userInfo.id = SharedUtils.imIdToId(userId);
        userInfo.imid = userId;
        userInfo.time = time;
        userInfo.nickName = nickName;
        if(Long.parseLong(time) == 1){
            userInfo.text = UNGAG;
        }else{
            userInfo.text = ISGAG;
        }
        EventBus.getDefault().post(new MessageBus(MessageBus.Type.SENDGAG, userInfo));
        callback.invoke(new Message().success("发送成功"));

    }

    @JSMethod
    public void toKick(String userId, final String nickName, final JSCallback callback){
        BaseRoom.UserInfo userInfo = new BaseRoom.UserInfo();
        userInfo.id   = SharedUtils.imIdToId(userId);
        userInfo.imid = userId;
        userInfo.nickName = nickName;
        userInfo.text = ISKICK;
        EventBus.getDefault().post(new MessageBus(MessageBus.Type.SENDKICK, userInfo));
        Message message = new Message().success("踢出成功");
        callback.invoke(message);
    }
}
