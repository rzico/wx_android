package com.rzico.weex.module;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.rzico.weex.Constant;
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

import org.json.JSONException;

import java.io.FileOutputStream;
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
     * 预付卡支付
     * @param callback
     */
    @JSMethod
    public void pcardPay(double amount, final JSCallback callback){

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
            public void onPaySuccess(String data) {
                callback.invoke(new Message().success(data));
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

    CardSlotManager cardSlotManager = null;

    private ScannerManager scannerManager;
    private Bundle bundle;
    private int scanner_type = 1;

    @JSMethod
    private void scan(final JSCallback callback){
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

    @JSMethod
    private void continuScan(final JSCallback callback){
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

    @JSMethod
    private void stopScan(JSCallback callback){
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
    private void searchCardInfo(final JSCallback callback) {
//        show("开始寻卡\n");
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
                                    searchCardInfo(callback);
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
                                        callback.invoke(new Message().success("磁道1：" + arg1.getTk1()
                                                + "\n" + "磁道2：" + arg1.getTk2()
                                                + "\n" + "磁道3：" + arg1.getTk3()+"\n"));
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
