package com.rzico.weex.module;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.rzico.weex.activity.BaseActivity;
import com.rzico.weex.model.info.Message;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

/**
 * Created by Jinlesoft on 2017/11/30.
 */

public class PhoneModule extends WXModule {


    public BaseActivity getActivity() {
        return (BaseActivity) mWXSDKInstance.getContext();
    }

    @JSMethod
    public void tel(final String number, final JSCallback callback) {
        Dexter.withActivity(getActivity()).withPermission(android.Manifest.permission.CALL_PHONE)
                .withListener(new com.karumi.dexter.listener.single.PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        //用intent启动拨打电话
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Message message = new Message().success("");
                        callback.invoke(message);
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        getActivity().startActivity(intent);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Message message = new Message().error();
                        callback.invoke(message);
                        getActivity().showDeniedDialog("需要拨号权限，否者无法拨打电话。");
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
                                                                   PermissionToken token) {
                        //用户不允许权限，向用户解释权限左右
                        token.continuePermissionRequest();
                    }
                }).check();
    }
    @JSMethod
    public void sms(final String phone, final String centent, final JSCallback callback){
        Dexter.withActivity(getActivity()).withPermission(android.Manifest.permission.SEND_SMS)
                .withListener(new com.karumi.dexter.listener.single.PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+phone));
                        intent.putExtra("sms_body", centent);
                        getActivity().startActivity(intent);
                        Message message = new Message().success("");
                        callback.invoke(message);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Message message = new Message().error();
                        callback.invoke(message);
                        getActivity().showDeniedDialog("需要短信权限，否者无法发送短信。");
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
