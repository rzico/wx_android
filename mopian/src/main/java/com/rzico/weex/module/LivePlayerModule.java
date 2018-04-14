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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public void toPlayLiveRoom(){
        Dexter.withActivity( (BaseActivity) mWXSDKInstance.getContext()).withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Dexter.withActivity( (BaseActivity) mWXSDKInstance.getContext()).withPermission(Manifest.permission.RECORD_AUDIO)
                                .withListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted(PermissionGrantedResponse response) {
                                        Intent intent = new Intent(mWXSDKInstance.getContext(), OpenVideoActivity.class);
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

    /**
     *
     * @param id
     * @param groupId
     * @param callback
     */
    @JSMethod
    public void toGag(String id, String groupId, final JSCallback callback){
        //禁言 100 秒
        TIMGroupManagerExt.ModifyMemberInfoParam param = new TIMGroupManagerExt.ModifyMemberInfoParam(groupId, id);
        param.setSilence(60 * 60 * 24);//经验24小时

        TIMGroupManagerExt.getInstance().modifyMemberInfo(param, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                Log.e("live", "modifyMemberInfo failed, code:" + code + "|msg: " + desc);
                Message message = new Message().error();
                callback.invoke(message);
            }

            @Override
            public void onSuccess() {
                Log.d("live", "modifyMemberInfo succ");
                Message message = new Message().success("禁言成功");
                callback.invoke(message);
            }
        });
    }

    public BaseActivity getActivity(){
        return (BaseActivity) mWXSDKInstance.getContext();
    }
    @JSMethod
    public void toLookLiveRoom(final String id){
//        HashMap<String, Object> params = new HashMap<>();
//        params.put("id", "57");
//        params.put("lat", "");
//        params.put("lng", "");
//        //进入直播间
//        new XRequest(getActivity(), "/weex/user/view.jhtml", XRequest.GET, params).setOnRequestListener(new HttpRequest.OnRequestListener() {
//            @Override
//            public void onSuccess(BaseActivity activity, String result, String type) {

                Intent intent = new Intent(mWXSDKInstance.getContext(), PlayActivity.class);
                intent.putExtra("id", id);
                getActivity().startActivity(intent);
//            }
//
//            @Override
//            public void onFail(BaseActivity activity, String cacheData, int code) {
//                getActivity().showToast("进入房间失败");
//            }
//        }).execute();

    }
}
