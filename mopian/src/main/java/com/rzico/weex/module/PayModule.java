package com.rzico.weex.module;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rzico.weex.Constant;
import com.rzico.weex.WXApplication;
import com.rzico.weex.activity.PuzzleActivity;
import com.rzico.weex.constant.AllConstant;
import com.rzico.weex.model.info.Message;
import com.rzico.weex.model.info.VideoBean;
import com.rzico.weex.utils.PathUtils;
import com.rzico.weex.utils.photo.PhotoHandle;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;
import com.ums.AppHelper;
import com.ums.upos.sdk.utils.json.JSONUtils;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;

import org.json.JSONException;
import org.xutils.common.Callback;
import org.xutils.common.util.FileUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.RxGalleryFinalApi;
import cn.finalteam.rxgalleryfinal.bean.MediaBean;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageMultipleResultEvent;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;

import static com.yalantis.ucrop.UCrop.REQUEST_CROP;

/**
 * Created by Jinlesoft on 2017/9/14.
 */

public class PayModule extends WXModule {

    private RxGalleryFinalCropListener listener = null;


    private static final class SimpleRxGalleryFinalHolder {
        private static final PayModule SIMPLE_RX_GALLERY_FINAL = new PayModule();
    }

    public static PayModule get() {
        return SimpleRxGalleryFinalHolder.SIMPLE_RX_GALLERY_FINAL;
    }


    public PayModule init(RxGalleryFinalCropListener listener) {
        this.listener = listener;
        return this;
    }

    public Context getContext(){
        return mWXSDKInstance.getContext();
    }

    public Activity getActivity(){return (Activity) mWXSDKInstance.getContext();}

    /**
     * 退货
     * @param refNo
     * @param date
     * @param tradeYear
     * @param amount
     * @param callback
     */
    @JSMethod
    public void returnGoods(String refNo,String date,String tradeYear, double amount, final JSCallback callback){

//            String transData = "{\"amt\":\"" + amount + "\", \"isNeedPrintReceipt\":\"false\", \"tradeType\":\"useScan\"}";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amt", String.valueOf(amount));
        jsonObject.put("appId", Constant.appId);
        jsonObject.put("refNo", refNo);
        jsonObject.put("date", date);
        jsonObject.put("tradeYear", tradeYear);
        PayModule.get().init(new RxGalleryFinalCropListener() {
            @NonNull
            @Override
            public Activity getSimpleActivity() {
                return getActivity();
            }

            @Override
            public void onPayCancel() {
                callback.invoke(new Message().error("用户取消"));
            }

            @Override
            public void onPaySuccess(String data) {
                callback.invoke(new Message().success(data));
            }

            @Override
            public void onPayError(@NonNull String errorMessage) {
                callback.invoke(new Message().error(errorMessage));
            }
        }).sendPay("公共资源", "退货",jsonObject);
    }

    /**
     * 退货
     * @param orgTraceNo
     * @param callback
     */
    @JSMethod
    public void revoke(String orgTraceNo, final JSCallback callback){

//            String transData = "{\"amt\":\"" + amount + "\", \"isNeedPrintReceipt\":\"false\", \"tradeType\":\"useScan\"}";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appId", Constant.appId);
        jsonObject.put("orgTraceNo", orgTraceNo);
        PayModule.get().init(new RxGalleryFinalCropListener() {
            @NonNull
            @Override
            public Activity getSimpleActivity() {
                return getActivity();
            }

            @Override
            public void onPayCancel() {
                callback.invoke(new Message().error("用户取消"));
            }

            @Override
            public void onPaySuccess(String data) {
                callback.invoke(new Message().success(data));
            }

            @Override
            public void onPayError(@NonNull String errorMessage) {
                callback.invoke(new Message().error(errorMessage));
            }
        }).sendPay("公共资源", "撤销",jsonObject);
    }

    @JSMethod
    public void posPay(String extBillNo, String extOrderNo, double amount, final JSCallback callback){

//            String transData = "{\"amt\":\"" + amount + "\", \"isNeedPrintReceipt\":\"false\", \"tradeType\":\"useScan\"}";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amt", String.valueOf(amount));
        jsonObject.put("appId", Constant.appId);
        jsonObject.put("extBillNo", extBillNo);
        jsonObject.put("extOrderNo", extOrderNo);
        PayModule.get().init(new RxGalleryFinalCropListener() {
            @NonNull
            @Override
            public Activity getSimpleActivity() {
                return getActivity();
            }

            @Override
            public void onPayCancel() {
                callback.invoke(new Message().error("用户取消"));
            }

            @Override
            public void onPaySuccess(String data) {
                callback.invoke(new Message().success(data));
            }

            @Override
            public void onPayError(@NonNull String errorMessage) {
                callback.invoke(new Message().error(errorMessage));
            }
        }).sendPay("POS 通", "POS通", jsonObject);
    }

    @JSMethod
    public void pay(String extBillNo, String extOrderNo, double amount, final JSCallback callback){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amt", String.valueOf(amount));
        jsonObject.put("appId", Constant.appId);
        jsonObject.put("extBillNo", extBillNo);
        jsonObject.put("extOrderNo", extOrderNo);
        PayModule.get().init(new RxGalleryFinalCropListener() {
            @NonNull
            @Override
            public Activity getSimpleActivity() {
                return getActivity();
            }

            @Override
            public void onPayCancel() {
                callback.invoke(new Message().error("用户取消"));
            }

            @Override
            public void onPaySuccess(String data) {
                callback.invoke(new Message().success(data));
            }

            @Override
            public void onPayError(@NonNull String errorMessage) {
                callback.invoke(new Message().error(errorMessage));
            }
        }).sendPay("银行卡收款", "消费",jsonObject);
    }


    @JSMethod
    public void select( String extOrderNo, double amount, final JSCallback callback){

        PayModule.get().init(new RxGalleryFinalCropListener() {
            @NonNull
            @Override
            public Activity getSimpleActivity() {
                return null;
            }

            @Override
            public void onPayCancel() {
                callback.invoke(new Message().error("用户取消"));
            }

            @Override
            public void onPaySuccess(String data) {
                callback.invoke(new Message().success(data));
            }

            @Override
            public void onPayError(@NonNull String errorMessage) {
                callback.invoke(new Message().error(errorMessage));
            }
        }).callQuery("POS 通", extOrderNo, amount);
    }

    @JSMethod
    public void print(final JSCallback callback){
        PayModule.get().init(new RxGalleryFinalCropListener() {
            @NonNull
            @Override
            public Activity getSimpleActivity() {
                return null;
            }

            @Override
            public void onPayCancel() {
                callback.invoke(new Message().error("用户取消"));
            }

            @Override
            public void onPaySuccess(String data) {
                callback.invoke(new Message().success(data));
            }

            @Override
            public void onPayError(@NonNull String errorMessage) {
                callback.invoke(new Message().error(errorMessage));
            }
        }).printTest();
    }
    @Override
    public void onActivityCreate() {
        super.onActivityCreate();
    }




    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK != resultCode) {
            if(listener != null){
                listener.onPayError("调用失败");
            }
            return;
        }
//        if (data == null || data.getExtras() == null
//                || data.getExtras().getString("result") == null) {
//            //走查询交易
//            if (isCallQuery) {
//                callQuery(requestCode);
//                isCallQuery = false;
//            }
//            return;
//        }

        if(AppHelper.TRANS_REQUEST_CODE == requestCode){

            if (Activity.RESULT_OK == resultCode) {
                if (null != data) {
                    StringBuilder result = new StringBuilder();
                    Map<String,String> map = AppHelper.filterTransResult(data);
                    result.append(AppHelper.TRANS_APP_NAME + ":" +map.get(AppHelper.TRANS_APP_NAME) + "\r\n");
                    result.append(AppHelper.TRANS_BIZ_ID + ":" +map.get(AppHelper.TRANS_BIZ_ID) + "\r\n");
                    result.append(AppHelper.RESULT_CODE + ":" +map.get(AppHelper.RESULT_CODE) + "\r\n");
                    result.append(AppHelper.RESULT_MSG + ":" +map.get(AppHelper.RESULT_MSG) + "\r\n");
                    result.append(AppHelper.TRANS_DATA + ":" +map.get(AppHelper.TRANS_DATA) + "\r\n");

                    if (null != result) {
                        if(listener != null){
                            listener.onPaySuccess(result.toString());
                        }
                    }
                }else{
                    if(listener != null){
                        listener.onPayError("Intent is null");
                    }
                }
            }else{
                if(listener != null){
                    listener.onPayError("resultCode is not RESULT_OK");
                }
            }
        } else if(AppHelper.PRINT_REQUEST_CODE == requestCode){
            if (Activity.RESULT_OK == resultCode) {
                if (null != data) {
                    StringBuilder result = new StringBuilder();
                    String printCode = data.getStringExtra("resultCode");
                    result.append("resultCode:" + printCode);
                    if (null != result) {
                        if(listener != null){
                            listener.onPaySuccess(result.toString());
                        }
                    }
                }else{
                    if(listener != null){
                        listener.onPayError("Intent is null");
                    }
                }
            }else{
                if(listener != null){
                    listener.onPayError("resultCode is not RESULT_OK");
                }
            }
        }
    }
    private void printTest(){
        View view = getActivity().getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        if(bitmap == null){

            return;
        }

        String fname = "/sdcard/ddd.png";
        try {
            FileOutputStream out = new FileOutputStream(fname);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        AppHelper.callPrint(getActivity(), fname);
    }

    public void sendPay(String title, String type, JSONObject jsonObject){
        try {
            String transApp = title;
            String transType = type;
//            HashMap<String, String> transMap = new HashMap<>();

            String transData = jsonObject.toJSONString();
            org.json.JSONObject json = null;
            json = new org.json.JSONObject(transData);
            AppHelper.callTrans(getActivity(), transApp, transType, json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 交易失败，走一次查询功能
    private void callQuery(String title, String orderNo, double amount){
        String transApp = title;
        String transData = "{\"amt\":\"" + amount + "\"}";
        String extOrderNo = orderNo;

        if (null == transApp || transApp.isEmpty()) {
            if(listener != null){
                listener.onPayError("应用名字不能为空");
            }
            return;
        }
        if (null == extOrderNo || extOrderNo.isEmpty()) {
            if(listener != null){
                listener.onPayError("外部订单号为空");
            }
        }
        try {
            org.json.JSONObject transDatas = new org.json.JSONObject(transData);
            transDatas.put("extOrderNo", extOrderNo);
            AppHelper.callTrans(getActivity(), transApp, "交易明细", transDatas);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface RxGalleryFinalCropListener {

        @NonNull
        Activity getSimpleActivity();

        /**
         * 用户
         */
        void onPayCancel();

        /**
         * 支付成功成功
         *
         * @param data 支付数据
         */
        void onPaySuccess(String data);

        /**
         * 裁剪失败
         *
         * @param errorMessage 错误信息
         */
        void onPayError(@NonNull String errorMessage);

    }


}
