package com.rzico.weex.module;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.rzico.weex.Constant;
import com.rzico.weex.WXApplication;
import com.rzico.weex.model.info.Message;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;
import com.ums.AppHelper;
import com.ums.upos.sdk.cardslot.CardInfoEntity;
import com.ums.upos.sdk.cardslot.CardSlotManager;
import com.ums.upos.sdk.cardslot.CardSlotTypeEnum;
import com.ums.upos.sdk.cardslot.CardTypeEnum;
import com.ums.upos.sdk.cardslot.OnCardInfoListener;
import com.ums.upos.sdk.exception.CallServiceException;
import com.ums.upos.sdk.exception.SdkException;
import com.ums.upos.sdk.scanner.OnScanListener;
import com.ums.upos.sdk.scanner.ScannerConfig;
import com.ums.upos.sdk.scanner.ScannerManager;
import com.ums.upos.sdk.system.BaseSystemManager;
import com.ums.upos.sdk.system.OnServiceStatusListener;

import org.json.JSONException;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jinlesoft on 2017/9/14.
 */

public class UposModule extends WXModule {

    private RxGalleryFinalCropListener listener = null;


    private static final class SimpleRxGalleryFinalHolder {
        private static final UposModule SIMPLE_RX_GALLERY_FINAL = new UposModule();
    }

    public static UposModule get() {
        return SimpleRxGalleryFinalHolder.SIMPLE_RX_GALLERY_FINAL;
    }


    public UposModule init(RxGalleryFinalCropListener listener) {
        this.listener = listener;
        return this;
    }

    public Context getContext() {
        if(mWXSDKInstance == null){
            return WXApplication.getContext();
        }else{
            return mWXSDKInstance.getContext();
        }
    }
    public com.rzico.weex.activity.BaseActivity getActivity() {
        if(mWXSDKInstance == null){
            return WXApplication.getActivity();
        }else{
            return (com.rzico.weex.activity.BaseActivity) mWXSDKInstance.getContext();
        }
    }

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
        UposModule.get().init(new RxGalleryFinalCropListener() {
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
            public void onPaySuccess(Map<String,Object> data) {
                callback.invoke(new Message().success(data));
            }

            @Override
            public void onPrintSuccess(String data) {

            }

            @Override
            public void onPayError(@NonNull String errorMessage) {
                callback.invoke(new Message().error(errorMessage));
            }
        }).sendPay("公共资源", "退货",jsonObject);
    }
    /**
     * 预付卡支付
     * @param callback
     */
    @JSMethod
    public void cardPay(double amount, final JSCallback callback){

//            String transData = "{\"amt\":\"" + amount + "\", \"isNeedPrintReceipt\":\"false\", \"tradeType\":\"useScan\"}";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appId", Constant.appId);
        jsonObject.put("amt", String.valueOf(amount));
        UposModule.get().init(new RxGalleryFinalCropListener() {
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
            public void onPaySuccess(Map<String,Object> data) {
                callback.invoke(new Message().success(data));
            }

            @Override
            public void onPrintSuccess(String data) {

            }

            @Override
            public void onPayError(@NonNull String errorMessage) {
                callback.invoke(new Message().error(errorMessage));
            }
        }).sendPay("预付卡", "消费",jsonObject);
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
        UposModule.get().init(new RxGalleryFinalCropListener() {
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
            public void onPaySuccess(Map<String,Object> data) {
                callback.invoke(new Message().success(data));
            }

            @Override
            public void onPrintSuccess(String data) {

            }

            @Override
            public void onPayError(@NonNull String errorMessage) {
                callback.invoke(new Message().error(errorMessage));
            }
        }).sendPay("公共资源", "撤销",jsonObject);
    }

    //阿里支付
    @JSMethod
    public void aliPay(String extBillNo, String extOrderNo, double amount, final JSCallback callback){

//            String transData = "{\"amt\":\"" + amount + "\", \"isNeedPrintReceipt\":\"false\", \"tradeType\":\"useScan\"}";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amt", String.valueOf(amount));
        jsonObject.put("tradeType", "useScan");
        jsonObject.put("appId", Constant.appId);
        jsonObject.put("extBillNo", extBillNo);
        jsonObject.put("extOrderNo", extOrderNo);
        UposModule.get().init(new RxGalleryFinalCropListener() {
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
            public void onPaySuccess(Map<String,Object> data) {
                callback.invoke(new Message().success(data));
            }

            @Override
            public void onPrintSuccess(String data) {

            }

            @Override
            public void onPayError(@NonNull String errorMessage) {
                callback.invoke(new Message().error(errorMessage));
            }
        }).sendPay("POS 通", "POS通", jsonObject);
    }
    //微信支付
    @JSMethod
    public void weixinPay(String extBillNo, String extOrderNo, double amount, final JSCallback callback){

//            String transData = "{\"amt\":\"" + amount + "\", \"isNeedPrintReceipt\":\"false\", \"tradeType\":\"useScan\"}";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amt", String.valueOf(amount));
        jsonObject.put("tradeType", "useScan");
        jsonObject.put("appId", Constant.appId);
        jsonObject.put("extBillNo", extBillNo);
        jsonObject.put("extOrderNo", extOrderNo);
        UposModule.get().init(new RxGalleryFinalCropListener() {
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
            public void onPaySuccess(Map<String,Object> data) {
                callback.invoke(new Message().success(data));
            }

            @Override
            public void onPrintSuccess(String data) {

            }

            @Override
            public void onPayError(@NonNull String errorMessage) {
                callback.invoke(new Message().error(errorMessage));
            }
        }).sendPay("POS 通", "POS通", jsonObject);
    }

    @JSMethod
    public void bankPay(String extBillNo, String extOrderNo, double amount, String appName, final JSCallback callback){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amt", String.valueOf(amount));
        jsonObject.put("appId", Constant.appId);
        jsonObject.put("extBillNo", extBillNo);
        jsonObject.put("extOrderNo", extOrderNo);
        UposModule.get().init(new RxGalleryFinalCropListener() {
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
            public void onPaySuccess(Map<String,Object> data) {
                callback.invoke(new Message().success(data));
            }

            @Override
            public void onPrintSuccess(String data) {

            }

            @Override
            public void onPayError(@NonNull String errorMessage) {
                callback.invoke(new Message().error(errorMessage));
            }
        }).sendPay(appName, "消费",jsonObject);
    }


    @JSMethod
    public void select( String extOrderNo, double amount, final JSCallback callback){

        UposModule.get().init(new RxGalleryFinalCropListener() {
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
            public void onPaySuccess(Map<String,Object> data) {
                callback.invoke(new Message().success(data));
            }

            @Override
            public void onPrintSuccess(String data) {

            }

            @Override
            public void onPayError(@NonNull String errorMessage) {
                callback.invoke(new Message().error(errorMessage));
            }
        }).callQuery("POS 通", extOrderNo, amount);
    }

    @JSMethod
    public void print(String url, final JSCallback callback){
        UposModule.get().init(new RxGalleryFinalCropListener() {
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
            public void onPaySuccess(Map<String,Object> data) {

            }

            @Override
            public void onPrintSuccess(String data) {
                callback.invoke(new Message().success(data));
            }

            @Override
            public void onPayError(@NonNull String errorMessage) {
                callback.invoke(new Message().error(errorMessage));
            }
        }).printTest(url, callback);
    }
    @Override
    public void onActivityCreate() {
        super.onActivityCreate();
    }

    CardSlotManager cardSlotManager = null;

    private ScannerManager scannerManager;
    private Bundle bundle;
    private int scanner_type = 1;

    @JSMethod
    public void scan(final JSCallback callback){
        try{
        BaseSystemManager.getInstance().deviceServiceLogin(
                getActivity(), null, "99999998",
                new OnServiceStatusListener() {
                    @Override
                    public void onStatus(int arg0) {
                        if (0 == arg0 || 2 == arg0 || 100 == arg0) {
                            scannerManager = new ScannerManager();
                            bundle = new Bundle();
                            bundle.putInt(ScannerConfig.COMM_SCANNER_TYPE, scanner_type);
                            bundle.putBoolean(ScannerConfig.COMM_ISCONTINUOUS_SCAN, false);
                            try {
                                scannerManager.stopScan();
                                scannerManager.initScanner(bundle);
                                scannerManager.startScan(30000, new OnScanListener() {
                                    @Override
                                    public void onScanResult(int i, byte[] bytes) {
                                        //防止用户未扫描直接返回，导致bytes为空
                                        if (bytes != null && !bytes.equals("")) {
                                            callback.invoke(new Message().success(new String(bytes)));
                                        }
                                    }
                                });

                            } catch (SdkException e) {
                                callback.invoke(new Message().error());
                                e.printStackTrace();
                            } catch (CallServiceException e) {
                                callback.invoke(new Message().error());
                                e.printStackTrace();
                            }
                        }
                    }
                });
        } catch (SdkException e) {

        }

    }

    @JSMethod
    public void continuScan(final JSCallback callback){
        try{
            BaseSystemManager.getInstance().deviceServiceLogin(
                    getActivity(), null, "99999998",
                    new OnServiceStatusListener() {
                        @Override
                        public void onStatus(int arg0) {
                            if (0 == arg0 || 2 == arg0 || 100 == arg0) {
                                scannerManager = new ScannerManager();
                                bundle = new Bundle();
                                bundle.putInt(ScannerConfig.COMM_SCANNER_TYPE, scanner_type);
                                bundle.putBoolean(ScannerConfig.COMM_ISCONTINUOUS_SCAN, true);
                                try {
                                    scannerManager.stopScan();
                                    scannerManager.initScanner(bundle);
                                    scannerManager.startScan(0, new OnScanListener() {
                                        @Override
                                        public void onScanResult(int i, byte[] bytes) {
                                            //防止用户未扫描直接返回，导致bytes为空
                                            if (bytes != null && !bytes.equals("")) {
                                                callback.invokeAndKeepAlive(new Message().success(new String(bytes)));
                                            }
                                        }
                                    });

                                } catch (SdkException e) {
                                    callback.invokeAndKeepAlive(new Message().error());
                                    e.printStackTrace();
                                } catch (CallServiceException e) {
                                    callback.invokeAndKeepAlive(new Message().error());
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        } catch (SdkException e) {

        }

    }

    @JSMethod
    public void stopScan(JSCallback callback){
        if(scannerManager != null){
            try {
                scannerManager.stopScan();
                callback.invoke(new Message().success(""));
            } catch (SdkException e) {
                callback.invoke(new Message().error());
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (CallServiceException e) {
                callback.invoke(new Message().error());
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @JSMethod
    public void stopReadCard(){
        try {
            cardSlotManager.stopRead();
        } catch (SdkException e) {
            e.printStackTrace();
        } catch (CallServiceException e) {
            e.printStackTrace();
        }
    }
    @JSMethod
    public void readCard(final JSCallback callback) {
        try {
            BaseSystemManager.getInstance().deviceServiceLogin(
                    getActivity(), null, "99999998",
                    new OnServiceStatusListener() {
                        @Override
                        public void onStatus(int arg0) {
                            if (0 == arg0 || 2 == arg0 || 100 == arg0) {
                                seachCardInfo(callback);
                            }else {
                                callback.invoke(new Message().error("登陆失败"));
                            }
                        }
                    });
        } catch (SdkException e) {
            e.printStackTrace();
        }
//        show("开始寻卡\n");
    }

    public void seachCardInfo(final JSCallback callback){
        if(cardSlotManager == null){
            cardSlotManager = new CardSlotManager();
        }
        Set<CardSlotTypeEnum> slotTypes = new HashSet<CardSlotTypeEnum>();
        slotTypes.add(CardSlotTypeEnum.SWIPE);
        Set<CardTypeEnum> cardTypes = new HashSet<CardTypeEnum>();
        cardTypes.add(CardTypeEnum.MAG_CARD);
        int timeout = 0;

        try {
            Map<CardSlotTypeEnum, Bundle> options = new HashMap<CardSlotTypeEnum, Bundle>();
            Bundle bundle = new Bundle();
            options.put(CardSlotTypeEnum.SWIPE, bundle);
            cardSlotManager.setConfig(options);
            cardSlotManager.readCard(slotTypes, cardTypes, timeout,
                    new OnCardInfoListener() {

                        @Override
                        public void onCardInfo(int arg0, CardInfoEntity arg1) {
                            if (0 != arg0) {
                                try {
                                    cardSlotManager.stopRead();
                                        seachCardInfo(callback);
                                } catch (SdkException e) {
                                    callback.invoke(new Message().error());
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (CallServiceException e) {
                                    callback.invoke(new Message().error());
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            } else {
                                switch (arg1.getActuralEnterType()) {
                                    case MAG_CARD:
                                        Map<String, String> map = new HashMap<>();
                                        map.put("data1", arg1.getTk1());
                                        map.put("data2", arg1.getTk2());
                                        map.put("data3", arg1.getTk3());
                                        callback.invoke(new Message().success(map));
                                        break;
                                    default:
                                        break;
                                }
                                try {
                                    cardSlotManager.stopRead();
                                } catch (SdkException e) {
                                    callback.invoke(new Message().error());
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (CallServiceException e) {
                                    callback.invoke(new Message().error());
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, null);
        } catch (SdkException e) {
            callback.invoke(new Message().error());
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CallServiceException e) {
            callback.invoke(new Message().error());
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
                    Map<String, Object> map = AppHelper.filterTransResult(data);
//                    Map<>
//                    result.append(AppHelper.TRANS_APP_NAME + ":" +map.get(AppHelper.TRANS_APP_NAME));
//                    result.append(AppHelper.TRANS_BIZ_ID + ":" +map.get(AppHelper.TRANS_BIZ_ID));
//                    result.append(AppHelper.RESULT_CODE + ":" +map.get(AppHelper.RESULT_CODE));
//                    result.append(AppHelper.RESULT_MSG + ":" +map.get(AppHelper.RESULT_MSG));
//                    result.append(AppHelper.TRANS_DATA + ":" +map.get(AppHelper.TRANS_DATA));
//                    String result = new Gson().toJson(map);
                    if (null != map) {
                        if(listener != null){
                            if ("0".equals(map.get("resultCode"))) {
                                String tdJson = map.get("transData").toString();
                                Map<String, Object> transData ;
                                 transData = new Gson().fromJson(tdJson, Map.class);
//                                map.put("transData", transData);
                                listener.onPaySuccess(transData);
                            } else {
                                listener.onPayError(map.get("resultMsg").toString());
                            }

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
                            listener.onPrintSuccess(result.toString());
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

    /**
     * 获取网络图片
     * @param imageurl 图片网络地址
     * @return Bitmap 返回位图
     */
    public Bitmap GetImageInputStream(String imageurl){
        URL url;
        HttpURLConnection connection=null;
        Bitmap bitmap=null;
        try {
            url = new URL(imageurl);
            connection=(HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(6000); //超时设置
            connection.setDoInput(true);
            connection.setUseCaches(false); //设置不使用缓存
            InputStream inputStream=connection.getInputStream();
            bitmap= BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void printTest(final String url, JSCallback callback){
//        View view = getActivity().getWindow().getDecorView();
//        view.setDrawingCacheEnabled(true);
//        view.buildDrawingCache();
//        Bitmap bitmap = view.getDrawingCache();
        final Bitmap[] bitmap = {null};
        new Thread(new Runnable() {
            @Override
            public void run() {
                bitmap[0] = GetImageInputStream(url);
            }
        }).start();
        long waitTime = 300;
        while (bitmap[0] == null){
            try {
                Thread.sleep(waitTime);
                waitTime += waitTime;
                if(waitTime > 15000){
                    callback.invoke(new Message().error("图片请求超时"));
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(bitmap[0] == null){

            return;
        }

        String fname = "/sdcard/ddd.png";
        try {
            FileOutputStream out = new FileOutputStream(fname);
            bitmap[0].compress(Bitmap.CompressFormat.PNG, 100, out);
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
        } catch (Exception e) {
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
        void onPaySuccess(Map<String,Object> data);

        void onPrintSuccess(String data);

        /**
         * 裁剪失败
         *
         * @param errorMessage 错误信息
         */
        void onPayError(@NonNull String errorMessage);

    }


}
