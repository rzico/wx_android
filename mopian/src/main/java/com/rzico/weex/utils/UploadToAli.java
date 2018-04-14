package com.rzico.weex.utils;

import android.app.Activity;
import android.content.Context;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.google.gson.Gson;
import com.rzico.weex.Constant;
import com.rzico.weex.activity.BaseActivity;
import com.rzico.weex.model.info.Message;
import com.rzico.weex.net.HttpRequest;
import com.rzico.weex.net.XRequest;
import com.rzico.weex.oos.OssService;
import com.rzico.weex.oos.PauseableUploadTask;
import com.rzico.weex.oos.STSGetter;
import com.taobao.weex.bridge.JSCallback;
import com.tencent.imsdk.TIMManager;
import com.yixiang.mopian.constant.AllConstant;

import net.bither.util.NativeUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Jinlesoft on 2018/4/9.
 */

public class UploadToAli {

    private Context context;
    public static UploadToAli uploadToAli = null;
    public UploadToAli(Context context){
        this.context = context;
    }

    public static UploadToAli getInstance(Context context){
        if(uploadToAli == null){
            return uploadToAli = new UploadToAli(context);
        }else {
            return uploadToAli;
        }
    }

    public void upload(final String filePath, final JSCallback callback, final JSCallback progressCallback) {
        String cachefileName = "";
        if(filePath.endsWith("jpg") || filePath.endsWith("bmp") || filePath.endsWith("png") || filePath.endsWith("jpeg")){
            //在这里压缩 把压缩完的地址 放 filepath 里面
            cachefileName = AllConstant.getDiskCachePath((BaseActivity) context) +"/"+ System.currentTimeMillis() + ".jpg";
            NativeUtil.compressBitmap(filePath, cachefileName);
        }else{
            cachefileName = filePath;
        }
        final String finalCacheFileName = cachefileName;
        final String stsData = SharedUtils.read("stsData");
        boolean error = true;//解析出错 或者 超时就失败 就请求sts
        if (stsData != null && !stsData.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(stsData);
                if(jsonObject!=null){
                    //对比查看sts有没有过期
                    error = DateUtils.isDateOneBigger(new Date(), jsonObject.getString("Expiration"), DateUtils.DATE_FORMAT_3);
                }
                if (!error) {
                    //取本地缓存不用去服务器取
                    uploadFile(stsData, (BaseActivity) context, finalCacheFileName, callback, progressCallback);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                error = true;
            }
        }

        if (error) {
            new XRequest((BaseActivity) context, "weex/member/oss/sts.jhtml").setOnRequestListener(new HttpRequest.OnRequestListener() {
                @Override
                public void onSuccess(BaseActivity activity, String result, String type) {
                    Message entity = new Gson().fromJson(result, Message.class);
                    String data = new Gson().toJson(entity.getData());
                    SharedUtils.save("stsData", data);
                    uploadFile(data, (BaseActivity) context, finalCacheFileName, callback, progressCallback);
                }

                @Override
                public void onFail(BaseActivity activity, String cacheData, int code) {
                    Message message = new Message().error("获取sts失败");
                    callback.invoke(message);
                }
            }).execute();

        }
    }

    private WeakReference<PauseableUploadTask> task;
    //上传文件
    public void uploadFile(String stsData, BaseActivity activity, String filePath, JSCallback callback, JSCallback progressCallback) {
        try{
            OSSCredentialProvider credentialProvider = new STSGetter(stsData);
            OSS oss = new OSSClient(activity, Constant.endpoint, credentialProvider);
            OssService ossService = new OssService(oss, Constant.bucket);
            Date nowTime = new Date();
            SimpleDateFormat time = new SimpleDateFormat("yyyy/MM/dd");
            String [] text = filePath.split("/");
            String [] houzui = text[text.length - 1].split("\\.");
            String imagePath = Constant.upLoadImages + time.format(nowTime) + "/" + UUID.randomUUID().toString() + "." + houzui[houzui.length - 1];
//        ossService.asyncPutImage(imagePath, filePath, callback, progressCallback);
//        if ((task == null) || (task.get() == null)){
//            Log.d("MultiPartUpload", "Start");
            PauseableUploadTask pauseableUploadTask = ossService.asyncMultiPartUpload(imagePath, filePath, callback, progressCallback);
            if(pauseableUploadTask != null){
                task = new WeakReference<>(pauseableUploadTask);
            }else {
                callback.invoke(new Message().error("图片地址不合法"));
            }
        } catch (Exception e){
            callback.invoke(new Message().error("图片地址不合法"));
        }

    }    /**获取库Phon表字段**/
}
