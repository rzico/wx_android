package com.rzico.weex.module;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.rzico.weex.WXApplication;
import com.rzico.weex.activity.BaseActivity;
import com.rzico.weex.activity.SplashActivity;
import com.rzico.weex.model.info.Message;
import com.rzico.weex.utils.PathUtils;
import com.rzico.weex.utils.Player;
import com.rzico.weex.utils.RecorderUtil;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

/**
 * Created by Jinlesoft on 2017/11/18.
 */

public class AudioModule extends WXModule {


    @JSMethod
    public void play(String url, JSCallback callback){
        if(url.startsWith("file://resource")){
            url =  url.replace("file://", PathUtils.getResPath(WXApplication.getActivity()));//读取资源路径
        }
        Player.getInstance().stop();
        Player.getInstance().playUrl(url, callback);
        Player.getInstance().play();
    }

    @JSMethod
    public void stop(){
        Player.getInstance().stop();
    }

    public com.rzico.weex.activity.BaseActivity getActivity() {
        if(mWXSDKInstance == null){
            return WXApplication.getActivity();
        }else{
            return (com.rzico.weex.activity.BaseActivity) mWXSDKInstance.getContext();
        }
    }

    @JSMethod
    public void startRecording(final JSCallback callback){
        Dexter.withActivity(getActivity()).withPermission(Manifest.permission.RECORD_AUDIO).withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        RecorderUtil.getInstance().startRecording(callback);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        showDeniedDialog("需要录音权限");

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        //用户不允许权限，向用户解释权限左右
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @JSMethod
    public void stopRecording(final JSCallback callback){
        Dexter.withActivity(WXApplication.getActivity()).withPermission(Manifest.permission.RECORD_AUDIO)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        RecorderUtil.getInstance().stopRecording(callback);
                        Record record = new Record();
                        record.setPath(RecorderUtil.getInstance().getFilePath());
                        record.setTime(RecorderUtil.getInstance().getTimeInterval());
                        Message message = new Message();
                        message.setType("success");
                        message.setContent("录音成功");
                        message.setData(record);
                        callback.invoke(message);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        showDeniedDialog("需要录音权限");

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        //用户不允许权限，向用户解释权限左右
                        token.continuePermissionRequest();
                    }
                }).check();
    }
    class Record{
        private String path;//录音保存的路径
        private long time;//录音时长

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }
    }

    /**
     * 用户不允许权限，向用户说明权限的重要性，并支持用户去设置中开启权限
     */
    public void showDeniedDialog(String title) {
        new android.app.AlertDialog.Builder(getActivity()).setTitle(title)
                .setMessage("请允许使用该权限,拒绝将无法使用此功能")
                .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //TiaohuoApplication.exit();
                    }
                })
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                        intent.putExtra("cmp", "com.android.settings/.applications.InstalledAppDetails");
                        intent.addCategory("android.intent.category.DEFAULT");
                        getActivity().startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .show();
    }

}
