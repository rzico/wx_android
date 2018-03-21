package com.rzico.weex.module;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rzico.weex.WXApplication;
import com.rzico.weex.activity.PuzzleActivity;
import com.rzico.weex.activity.TestActivity;
import com.rzico.weex.model.info.Message;
import com.rzico.weex.model.info.VideoBean;
import com.rzico.weex.utils.PathUtils;
import com.rzico.weex.utils.photo.PhotoHandle;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;
import com.yixiang.mopian.constant.AllConstant;

import org.xutils.common.util.FileUtil;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.RxGalleryFinalApi;
import cn.finalteam.rxgalleryfinal.bean.MediaBean;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageMultipleResultEvent;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;
import cn.finalteam.rxgalleryfinal.utils.MediaUtils;

import static com.yalantis.ucrop.UCrop.REQUEST_CROP;

/**
 * Created by Jinlesoft on 2017/9/14.
 */

public class AlbumModule extends WXModule {

    private RxGalleryFinalCropListener listener = null;


    private static final class SimpleRxGalleryFinalHolder {
        private static final AlbumModule SIMPLE_RX_GALLERY_FINAL = new AlbumModule();
    }

    public static AlbumModule get() {
        return SimpleRxGalleryFinalHolder.SIMPLE_RX_GALLERY_FINAL;
    }


    public AlbumModule init(RxGalleryFinalCropListener listener) {
        this.listener = listener;
        return this;
    }

    public Context getContext(){
        return mWXSDKInstance.getContext();
    }

    public Activity getActivity(){return (Activity) mWXSDKInstance.getContext();}

    @Override
    public void onActivityCreate() {
        super.onActivityCreate();
    }

    @JSMethod
    public void openAlbumSingle(boolean isCrop, JSCallback callback){
        com.alibaba.fastjson.JSONObject jsonObject = new JSONObject();
        jsonObject.put("isCrop", isCrop);
        jsonObject.put("width", 1);
        jsonObject.put("height",1);
        openAlbumSingle(jsonObject.toJSONString(), callback);
    }
    @JSMethod
    public void openAlbumSingle(String option, final JSCallback callback){
        boolean getCrop = false;
        int width = 1;
        int height = 1;
        if(!(option.startsWith("{") && option.endsWith("}"))){
            if(option.equals("true")){
                getCrop = true;
            }else if(option.equals("false")){
                getCrop = false;
            }
        }else{
            try {
                com.alibaba.fastjson.JSONObject jsObj = JSON.parseObject(option);
                if(jsObj.containsKey("isCrop")){
                    getCrop = jsObj.getBoolean("isCrop");
                }
                if(jsObj.containsKey("width")){
                    width = jsObj.getInteger("width");
                }
                if(jsObj.containsKey("height")){
                    height = jsObj.getInteger("height");
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        final boolean isCrop = getCrop;
        final  int finalwidth = width;
        final  int finalheight = height;
        RxGalleryFinal
                .with(WXApplication.getActivity())
                .image()
                .radio()
                .cropAspectRatioOptions(0, new AspectRatio(width + ":" + height, width, height))
//                .crop()
                .imageLoader(ImageLoaderType.PICASSO)
                .subscribe(new RxBusResultDisposable<ImageRadioResultEvent>() {
                    @Override
                    protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                        if (imageRadioResultEvent.getResult() == null){
                            Message message = new Message();
                            message.setType("cancel");
                            message.setContent("用户取消");
                            message.setData(null);
                            callback.invoke(message);
                            return;
                        }
                        if(!isCrop){
                            Message message = new Message();
                            message.setType("success");
                            message.setContent("选择成功");
                            MediaBean item = imageRadioResultEvent.getResult();
                            if((item.getThumbnailSmallPath() == null || item.getThumbnailSmallPath().equals("")) &&item.getOriginalPath()!=null){
                                item.setThumbnailSmallPath(item.getOriginalPath());
                            }
                            message.setData(item);
                            callback.invoke(message);
                        }else{
                            cropHeadImg(imageRadioResultEvent.getResult().getOriginalPath(), finalwidth, finalheight, callback);
                        }
//                        Toast.makeText(getContext(), "选中了图片路径：" + imageRadioResultEvent.getResult().getOriginalPath(), Toast.LENGTH_SHORT).show();
                    }
                })
                .openGallery();
    }
    @JSMethod
    public void openVideo(final JSCallback callback){
        RxGalleryFinalApi
                .getInstance(getActivity())
//                .setVideoPreview(true)
                .setType(RxGalleryFinalApi.SelectRXType.TYPE_VIDEO, RxGalleryFinalApi.SelectRXType.TYPE_SELECT_RADIO)
                .setVDRadioResultEvent(new RxBusResultDisposable<ImageRadioResultEvent>() {
                    @Override
                    protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
//                        Toast.makeText(getContext(), imageRadioResultEvent.getResult().getOriginalPath(), Toast.LENGTH_SHORT).show();
                        if(imageRadioResultEvent.getResult() != null && imageRadioResultEvent.getResult().getOriginalPath()!=null){
                            MediaMetadataRetriever media = new MediaMetadataRetriever();
                            media.setDataSource(imageRadioResultEvent.getResult().getOriginalPath());

                            Bitmap bitmap = media.getFrameAtTime();
                            VideoBean videoBean = new VideoBean();
                            videoBean.setVideoPath(imageRadioResultEvent.getResult().getOriginalPath());
                            File imageFile = FileUtil.saveBitmapJPG(UUID.randomUUID().toString(),bitmap, AllConstant.getDiskCachePath(getActivity()));
                            videoBean.setCoverImagePath(imageFile.getPath());
                            Message message = new Message().success(videoBean);
                            callback.invoke(message);
                        }else {
                            Message message = new Message().error();
                            callback.invoke(message);
                        }
                    }
                })
                .open();
    }
    @JSMethod
    public void openAlbumMuti(final JSCallback callback){
//        回调不使用全局变量
        RxGalleryFinal rxGalleryFinal = RxGalleryFinal
                .with(WXApplication.getActivity())
                .image()
                .multiple();
        //重新进来的话， 不把以前的选上 则注释
//        if (list != null && !list.isEmpty()) {
//            rxGalleryFinal
//                    .selected(list);
//        }
        rxGalleryFinal.maxSize(100)
                .imageLoader(ImageLoaderType.PICASSO)
                .subscribe(new RxBusResultDisposable<ImageMultipleResultEvent>() {
                    @Override
                    protected void onEvent(ImageMultipleResultEvent imageMultipleResultEvent) throws Exception {
                        List<MediaBean> list = imageMultipleResultEvent.getResult();
                        if(list!=null && list.size() > 0) {
                            for (MediaBean item : list){
                                if((item.getThumbnailSmallPath() == null || item.getThumbnailSmallPath().equals("")) &&item.getOriginalPath()!=null){
                                    item.setThumbnailSmallPath(item.getOriginalPath());
                                }
                            }
                            Message message = new Message();
                            message.setType("success");
                            message.setContent("选择成功");
                            message.setData(list);
                            callback.invoke(message);
                        }else {
                            Message message = new Message();
                            message.setType("cancel");
                            message.setContent("用户取消");
                            message.setData(null);
                            callback.invoke(message);
                        }
                    }

                }).openGallery();
    }

    @JSMethod
    public void cropHeadImg(String imagePath,int width, int height, final JSCallback callback){
        //调用当前文件下的接口 并且实现它回调给 callback
        AspectRatio aspectRatio = new AspectRatio(width + ":" + height, width,height);
        AlbumModule.get().init(new RxGalleryFinalCropListener() {
            @NonNull
            @Override
            public Activity getSimpleActivity() {
                return getActivity();
            }

            @Override
            public void onCropCancel() {
                Message message = new Message();
                message.setType("cancel");
                message.setContent("用户取消");
                message.setData(null);
                callback.invoke(message);
            }

            @Override
            public void onCropSuccess(@Nullable Uri uri) {
                Message message = new Message();
                message.setType("success");
                message.setContent("裁剪成功");
                MediaBean mediaBean = new MediaBean();
//                以下是要经过处理的 目前暂时传一样的
                mediaBean.setThumbnailBigPath(uri.getPath());
                mediaBean.setThumbnailSmallPath(uri.getPath());
                mediaBean.setOriginalPath(uri.getPath());
                message.setData(mediaBean);
                callback.invoke(message);
            }

            @Override
            public void onCropError(@NonNull String errorMessage) {
                Message message = new Message();
                message.setType("error");
                message.setContent("用户取消");
                message.setData(errorMessage);
                callback.invoke(message);
            }
        }).openCrapActivity(imagePath, aspectRatio);
    }

    @JSMethod
    public void cropHeadImg(String imagePath, final JSCallback callback){
       cropHeadImg(imagePath, 1, 1, callback);
    }
    @JSMethod
    public void openCrop(String imagePath, final JSCallback callback){
        //调用当前文件下的接口 并且实现它回调给 callback
        AlbumModule.get().init(new RxGalleryFinalCropListener() {
            @NonNull
            @Override
            public Activity getSimpleActivity() {
                return getActivity();
            }

            @Override
            public void onCropCancel() {
                Message message = new Message();
                message.setType("cancel");
                message.setContent("用户取消");
                message.setData(null);
                callback.invoke(message);
            }

            @Override
            public void onCropSuccess(@Nullable Uri uri) {
                    Message message = new Message();
                    message.setType("success");
                    message.setContent("裁剪成功");
                    MediaBean mediaBean = new MediaBean();
//                  Toast.makeText(getContext(), uri.toString(), Toast.LENGTH_SHORT).show();
                if(uri.toString().startsWith("http://")){
                    String url = uri.toString();
                    mediaBean.setOriginalPath(url);
                    mediaBean.setThumbnailBigPath(url);
                    mediaBean.setThumbnailSmallPath(url);
                }else {
                    mediaBean.setThumbnailBigPath(uri.getPath());
                    mediaBean.setThumbnailSmallPath(uri.getPath());
                    mediaBean.setOriginalPath(uri.getPath());
                }
//                以下是要经过处理的 目前暂时传一样的
                    message.setData(mediaBean);
                    callback.invoke(message);
            }

            @Override
            public void onCropError(@NonNull String errorMessage) {

                    Message message = new Message();
                    message.setType("error");
                    message.setContent("用户取消");
                    message.setData(errorMessage);
                    callback.invoke(message);
            }
        }).openPhotoHandleActivity(imagePath);
    }

    /**
     * 处理图片
     * @param imagePath
     */
    public void openPhotoHandleActivity(String imagePath){
        Uri uri = null;
        //这里要处理传入的path 因为weex前端没有传入 file://
        if(imagePath.startsWith("/")){
            imagePath = "file://" + imagePath;
        }
//        if(!imagePath.startsWith("file://")){
//            uri = Uri.parse(imagePath);
//        }else{
//            uri = Uri.parse("file://" + imagePath);
//        }
        uri = Uri.parse(imagePath);
        PhotoHandle of = PhotoHandle.of(uri, Uri.fromFile(PathUtils.getDiskCacheDir(listener.getSimpleActivity())));
        of.start(listener.getSimpleActivity());
    }

    public void openCrapActivity(String imagePath, AspectRatio... aspectRatio){
        Uri uri = null;
        if(imagePath.startsWith("/")){
            imagePath = "file://" + imagePath;
        }
        uri = Uri.parse(imagePath);
        //这里要处理传入的path 因为weex前端没有传入 file://
//        if(!imagePath.startsWith("file://")){
//            uri = Uri.parse(imagePath);
//        }else{
//            uri = Uri.parse("file://" + imagePath);
//        }
        UCrop of = UCrop.of(uri, Uri.fromFile(PathUtils.getDiskCacheDir(listener.getSimpleActivity())));
//        for (AspectRatio item: aspectRatios) {
//            of.withAspectRatio(item.getAspectRatioX(),item.getAspectRatioY());//裁剪头像
//        }
        UCrop.Options option = new UCrop.Options();
        option.setAspectRatioOptions(0, aspectRatio);
        of.withOptions(option);
        of.start(listener.getSimpleActivity());

//        Intent intent = new Intent();
//        intent.setClass(listener.getSimpleActivity(), TestActivity.class);
//        intent.resolveActivity(listener.getSimpleActivity().getApplication().getPackageManager());
//        listener.getSimpleActivity().startActivityForResult(intent, 2);
    }

    /**
     *   {
     *   imageArray:
     *   [
     *   {
     *     path:""
     *   },
     *   {
     *     path:""
     *   }
     *   ]
     *   height:
     *   width:
     *   }
     * @param option
     * @param callback
     */
    @JSMethod
    public void openPuzzle(String option, final JSCallback callback){
        int imageCount = 1;
        JSONArray imageArray = null;
        int  height = 1000;
        int  width  = 500;

        List<MediaBean> mediaBeens = new ArrayList<>();
        try {
            option = URLDecoder.decode(option, "utf-8");
            com.alibaba.fastjson.JSONObject jsObj = JSON.parseObject(option);
            if (jsObj.containsKey("imageArray")) {
                imageArray = jsObj.getJSONArray("imageArray");
                for (int i = 0; i < imageArray.size() ; i++){
                    if (imageArray.getJSONObject(i).containsKey("path")) {
                        MediaBean mediaBean = new MediaBean();
                        String path = imageArray.getJSONObject(i).getString("path");
                        if(path.startsWith("file:/")){
                            path = path.replace("file:/", "");
                        }
                        mediaBean.setOriginalPath(path);
                        mediaBeens.add(mediaBean);
                    }
                }
            }
            if (jsObj.containsKey("imageCount")) {
                imageCount = jsObj.getInteger("imageCount");
            }
            if (jsObj.containsKey("height")) {
                height = jsObj.getInteger("height");
            }
            if (jsObj.containsKey("width")) {
                width = jsObj.getInteger("width");
            }
            String key = String.valueOf(System.currentTimeMillis());
            JSCallBaskManager.put(key, callback);
            Intent intent = new Intent();
            intent.putExtra("key", key);
            intent.putExtra("height", height);
            intent.putExtra("width", width);
            intent.putExtra("pics", (Serializable) mediaBeens);
            intent.setClass(getActivity(), PuzzleActivity.class);
            getActivity().startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(listener != null){
            switch (resultCode) {
                case Activity.RESULT_CANCELED:
                    listener.onCropCancel();
                    break;
                case UCrop.RESULT_ERROR:
                    if (data != null) {
                        Throwable cropError = UCrop.getError(data);
                        if (cropError != null) {
                            listener.onCropError(cropError.getMessage());
                        } else {
                            listener.onCropError("裁剪出现未知错误");
                        }
                    } else {
                        listener.onCropError("获取相册图片出现错误");
                    }
                    break;

                case Activity.RESULT_OK:
                    switch (requestCode) {
                        case PhotoHandle.REQUEST_PHOTOHANDLER:
                            listener.onCropSuccess(UCrop.getOutput(data));
                            break;
                        case REQUEST_CROP:
                            listener.onCropSuccess(UCrop.getOutput(data));
                            break;
                    }
                    break;
            }
        }

    }

    public interface RxGalleryFinalCropListener {

        @NonNull
        Activity getSimpleActivity();


        /**
         * 裁剪被取消
         */
        void onCropCancel();

        /**
         * 裁剪成功
         *
         * @param uri 裁剪的 Uri , 有可能会为Null
         */
        void onCropSuccess(@Nullable Uri uri);

        /**
         * 裁剪失败
         *
         * @param errorMessage 错误信息
         */
        void onCropError(@NonNull String errorMessage);

    }


}
