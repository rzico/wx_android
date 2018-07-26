package com.rzico.weex.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.OnMatrixChangedListener;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.OnSingleFlingListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.rzico.weex.R;
import com.rzico.weex.utils.AntiShake;
import com.rzico.weex.utils.BarTextColorUtils;
import com.rzico.weex.utils.PathUtils;
import com.rzico.weex.utils.PhotoUtils;
import com.rzico.weex.utils.UriToPathUtil;
import com.rzico.weex.utils.photo.PhotoHandle;
import com.umeng.analytics.MobclickAgent;
import com.yalantis.ucrop.*;
import com.yalantis.ucrop.callback.BitmapLoadCallback;
import com.yalantis.ucrop.model.ExifInfo;
import com.yalantis.ucrop.util.BitmapLoadUtils;
import com.rzico.weex.constant.AllConstant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.bean.MediaBean;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;
import cn.finalteam.rxgalleryfinal.utils.FoucedStateListDrawable;
import cn.finalteam.rxgalleryfinal.view.ImageButtonWithText;
import cn.hzw.graffiti.GraffitiActivity;
import cn.hzw.graffiti.GraffitiParams;

import com.yalantis.ucrop.view.TopView;

public class PhotoHandleActivity extends AppCompatActivity {

    private PhotoView mPhotoView;
    static final String PHOTO_TAP_TOAST_STRING = "Photo Tap! X: %.2f %% Y:%.2f %% ID: %d";
    static final String SCALE_TOAST_STRING = "Scaled to: %.2ff";
    static final String FLING_LOG_STRING = "Fling velocityX: %.2f, velocityY: %.2f";
    private Toast mCurrentToast;
    private static final int PUZZLE = 101;
    private static final int FILTER = 102;
    public static final int GRAFFITI = 103;

    private Bitmap nowBitmap = null;

    private AntiShake antiShake = new AntiShake();
    private boolean mShowBottomControls;
    private boolean isHandle = false;
    private TopView topView;

    //颜色
    private int mToolbarColor;
    private int mStatusBarColor;
    private int mActiveWidgetColor;
    private int mToolbarWidgetColor;
    @DrawableRes
    private int mToolbarCancelDrawable;

    //  功能按钮图片
    private ViewGroup mWrapperStateAspectRatio, mWrapperPhotoChange, mWrapperStateFilter, mWrapperDoodle;
    //  mLayoutPhotoChange 原先的放大缩小功能被改成更换图片
    private ViewGroup mLayoutAspectRatio, mLayoutRotate, mLayoutFilter;

    private Uri inputUri, outputUri;

    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarTextColorUtils.StatusBarLightMode(PhotoHandleActivity.this, R.color.wxColor);
        setContentView(R.layout.activity_photo_handle);
        Intent intent = getIntent();
        initView(intent);
        initData(intent);
    }

    private void initData(Intent intent) {
        inputUri = intent.getParcelableExtra(PhotoHandle.EXTRA_INPUT_URI);
        outputUri = intent.getParcelableExtra(PhotoHandle.EXTRA_OUTPUT_URI);

        if (inputUri != null && outputUri != null) {
            int maxBitmapSize = BitmapLoadUtils.calculateMaxBitmapSize(PhotoHandleActivity.this);
            BitmapLoadUtils.decodeBitmapInBackground(PhotoHandleActivity.this, inputUri, outputUri, maxBitmapSize, maxBitmapSize,
                    new BitmapLoadCallback() {

                        @Override
                        public void onBitmapLoaded(@NonNull Bitmap bitmap, @NonNull ExifInfo exifInfo, @NonNull String imageInputPath, @Nullable String imageOutputPath) {
                            nowBitmap = bitmap;
                            mPhotoView.setImageBitmap(bitmap);
                        }
                        @Override
                        public void onFailure(@NonNull Exception bitmapWorkerException) {
                            Log.e("PhotoHandle", "onFailure: setImageUri", bitmapWorkerException);
                            setResult(UCrop.RESULT_ERROR, new Intent()
                                    .putExtra(UCrop.EXTRA_OUTPUT_URI, inputUri));
                            finish();
                        }
                    });

        }

    }
    private void rotateLoop() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPhotoView.setRotationBy(1);
                rotateLoop();
            }
        }, 15);
    }
    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            // 读取uri所在的图片
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            Log.e("[Android]", e.getMessage());
            Log.e("[Android]", "目录为：" + uri);
            e.printStackTrace();
            return null;
        }
    }
    protected void savePhoto() {
        Bitmap bitmap = null;
        if(mPhotoView!=null){
            bitmap = ((BitmapDrawable)mPhotoView.getDrawable()).getBitmap();
        }
        try {
            String scheme = inputUri.getScheme();
            if((isHandle || scheme.startsWith("http")) && bitmap!=null){
                File file = PathUtils.saveBitmapJPG("MOPIAN" + System.currentTimeMillis(), bitmap);
                // 最后通知图库更新
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(file);
                intent.setData(uri);
                sendBroadcast(intent);//这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦

            setResult(Activity.RESULT_OK, new Intent()
                    .putExtra(UCrop.EXTRA_OUTPUT_URI, uri));
            }else{
                setResult(Activity.RESULT_OK, new Intent()
                        .putExtra(UCrop.EXTRA_OUTPUT_URI, inputUri));
            }
            finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void initView(Intent intent) {

        mPhotoView = (PhotoView) findViewById(R.id.iv_photo);

        mPhotoView.setOnMatrixChangeListener(new MatrixChangeListener());
        mPhotoView.setOnPhotoTapListener(new PhotoTapListener());
        mPhotoView.setOnSingleFlingListener(new SingleFlingListener());

//        init颜色

        mStatusBarColor = intent.getIntExtra(UCrop.Options.EXTRA_STATUS_BAR_COLOR, ContextCompat.getColor(this, cn.finalteam.rxgalleryfinal.R.color.ucrop_color_statusbar));
        mToolbarColor = intent.getIntExtra(UCrop.Options.EXTRA_TOOL_BAR_COLOR, ContextCompat.getColor(this, R.color.wxColor));
        mActiveWidgetColor = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_COLOR_WIDGET_ACTIVE, ContextCompat.getColor(this, R.color.wxColor));
        mToolbarWidgetColor = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_WIDGET_COLOR_TOOLBAR, ContextCompat.getColor(this, R.color.white));

//        mToolbarCancelDrawable = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_WIDGET_CANCEL_DRAWABLE, cn.finalteam.rxgalleryfinal.R.drawable.gallery_ic_cross);

        mShowBottomControls = !intent.getBooleanExtra(UCrop.Options.EXTRA_HIDE_BOTTOM_CONTROLS, false);
        if (mShowBottomControls) {

            //功能界面
//            ViewGroup photoBox = (ViewGroup) findViewById(R.id.photohandle_photobox);
//            View.inflate(this, R.layout.photohandle_controls, photoBox);
//
            mWrapperStateAspectRatio = (ViewGroup) findViewById(R.id.state_ucrop);
            mWrapperStateAspectRatio.setOnClickListener(mStateClickListener);
            mWrapperPhotoChange = (ViewGroup) findViewById(R.id.state_photo_change);
            mWrapperPhotoChange.setOnClickListener(mStateClickListener);
            mWrapperDoodle = (ViewGroup) findViewById(R.id.state_photo_doodle);
            mWrapperDoodle.setOnClickListener(mStateClickListener);
            mWrapperStateFilter = (ViewGroup) findViewById(R.id.state_filter);
            mWrapperStateFilter.setOnClickListener(mStateClickListener);

//            mLayoutFilter = (ViewGroup) findViewById(R.id.layout_filter);
            mLayoutAspectRatio = (ViewGroup) findViewById(R.id.layout_aspect_ratio);
            mLayoutRotate = (ViewGroup) findViewById(R.id.layout_rotate_wheel);
            setupStatesWrapper();
        }

        initToolbar();

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    private void initToolbar() {
        topView = (TopView) findViewById(R.id.toolbar);
        topView.setTitle("图片编辑");
        topView.setRightWord("完成");
        topView.setLeftIcon(cn.finalteam.rxgalleryfinal.R.drawable.gallery_ic_cross);
        topView.setOnLeftClickListener(new TopView.OnLeftClickListener() {
            @Override
            public void leftClick() {
                onBackPressed();
            }
        });
        topView.setOnRightClickListener(new TopView.OnRightClickListener() {
            @Override
            public void rightClick() {
                //保存当前图片
                if (antiShake.check(topView.getId())) return;//防抖动
                savePhoto();
            }
        });
    }


    /**
     * Use {@link #mActiveWidgetColor} for color filter
     * 设置选中和不选中颜色状态
     */
    private void setupStatesWrapper() {
        ImageButtonWithText photoChangeImageView = (ImageButtonWithText) findViewById(R.id.image_view_photo_change);
        ImageButtonWithText stateFilterImageView = (ImageButtonWithText) findViewById(R.id.image_view_state_filter);
        ImageButtonWithText stateUcropImageView = (ImageButtonWithText) findViewById(R.id.image_view_state_ucrop);
        ImageButtonWithText photoPuzzleImageView = (ImageButtonWithText) findViewById(R.id.image_view_photo_doodle);
//        photoPuzzleImageView.setFocusable(false);
//      photoPuzzleImageView.setClickable(false);
        photoChangeImageView.getImageView().setImageDrawable(new FoucedStateListDrawable(photoChangeImageView.getImageView().getDrawable(), photoChangeImageView.getTextView(), mActiveWidgetColor));
        stateFilterImageView.getImageView().setImageDrawable(new FoucedStateListDrawable(stateFilterImageView.getImageView().getDrawable(), stateFilterImageView.getTextView(), mActiveWidgetColor));
        stateUcropImageView.getImageView().setImageDrawable(new FoucedStateListDrawable(stateUcropImageView.getImageView().getDrawable(), stateUcropImageView.getTextView(), mActiveWidgetColor));
        photoPuzzleImageView.getImageView().setImageDrawable(new FoucedStateListDrawable(photoPuzzleImageView.getImageView().getDrawable(), photoPuzzleImageView.getTextView(), mActiveWidgetColor));
    }

    private final View.OnClickListener mStateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!v.isSelected()) {
                setWidgetState(v.getId());
            }
        }
    };

    /**
     * 根据id设置小组件即底部功能组件状态 即点击切完功能试图 --- 值得学习
     *
     * @param stateViewId
     */
    private void setWidgetState(@IdRes int stateViewId) {
        if (!mShowBottomControls) return;

        if (stateViewId == R.id.state_photo_change) {
            setAllowedGestures(0);
        } else if (stateViewId == R.id.state_photo_doodle) {
            setAllowedGestures(1);
        } else if (stateViewId == R.id.state_filter) {
            setAllowedGestures(2);//滤镜
        } else if (stateViewId == R.id.state_ucrop) {
            setAllowedGestures(3);//裁剪
        }
    }

    private void setAllowedGestures(final int tab) {
        //设置组件判断是裁剪还是旋转
        if (tab == 0) {
            //则是 更换照片
            RxGalleryFinal.with(PhotoHandleActivity.this).image().radio().imageLoader(ImageLoaderType.PICASSO)
                    .subscribe(new RxBusResultDisposable<ImageRadioResultEvent>() {
                        @Override
                        protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                            Uri uri = Uri.parse("file://" + imageRadioResultEvent.getResult().getOriginalPath());
                            inputUri = uri;
                            if (inputUri != null && outputUri != null) {
                                int maxBitmapSize = BitmapLoadUtils.calculateMaxBitmapSize(PhotoHandleActivity.this);
                                BitmapLoadUtils.decodeBitmapInBackground(PhotoHandleActivity.this, inputUri, outputUri, maxBitmapSize, maxBitmapSize,
                                        new BitmapLoadCallback() {

                                            @Override
                                            public void onBitmapLoaded(@NonNull Bitmap bitmap, @NonNull ExifInfo exifInfo, @NonNull String imageInputPath, @Nullable String imageOutputPath) {
                                                mPhotoView.setImageBitmap(bitmap);
                                            }
                                            @Override
                                            public void onFailure(@NonNull Exception bitmapWorkerException) {
                                                Log.e("PhotoHandle", "onFailure: setImageUri", bitmapWorkerException);
                                            }
                                        });

                            }
                        }
                    })
                    .openGallery();
        } else if (tab == 1) {
            //则是拼图 原先是拼图 现在变成涂鸦了
//            int height = 500;
//            int width = 1000;
//
//            List<MediaBean> mediaBeens = new ArrayList<>();
//            try {
//                MediaBean mediaBean = new MediaBean();
//                mediaBean.setOriginalPath(inputUri.getPath());
//                mediaBeens.add(mediaBean);
//                Intent intent = new Intent();
//                intent.putExtra("height", height);
//                intent.putExtra("width", width);
//                intent.putExtra("pics", (Serializable) mediaBeens);
//                intent.setClass(PhotoHandleActivity.this, cn.finalteam.rxgalleryfinal.PuzzleActivity.class);
//                startActivityForResult(intent, PUZZLE);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

//            涂鸦代码GRAFFITI


            if(nowBitmap != null){
                // 涂鸦参数
//                GraffitiParams params = new GraffitiParams();
//                ByteArrayOutputStream baos=new ByteArrayOutputStream();
//                nowBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                byte [] bitmapByte =baos.toByteArray();
//
//                params.mPaintSize = 20;
//                GraffitiActivity.startActivityForResult(PhotoHandleActivity.this, params,bitmapByte, GRAFFITI);
                String inputPath = UriToPathUtil.getRealFilePath(PhotoHandleActivity.this, inputUri);
                String outputPath = UriToPathUtil.getRealFilePath(PhotoHandleActivity.this, outputUri);
                GraffitiActivity.startActivityForResult(PhotoHandleActivity.this, inputPath, outputPath, true, GRAFFITI);
            }

        } else if (tab == 2) {
            //裁剪
            Intent intent = new Intent();
            intent.setClass(PhotoHandleActivity.this, FilterActivity.class);
            intent.putExtra(PhotoHandle.EXTRA_INPUT_URI, inputUri);
            intent.putExtra(PhotoHandle.EXTRA_OUTPUT_URI, outputUri);
            startActivityForResult(intent, FILTER);
        } else if (tab == 3) {
            UCrop of = UCrop.of(inputUri, outputUri);
            UCrop.Options option = new UCrop.Options();
            of.withOptions(option);
            of.start(PhotoHandleActivity.this);
        }
    }

    private class PhotoTapListener implements OnPhotoTapListener {

        @Override
        public void onPhotoTap(ImageView view, float x, float y) {
            float xPercentage = x * 100f;
            float yPercentage = y * 100f;

//            showToast(String.format(PHOTO_TAP_TOAST_STRING, xPercentage, yPercentage, view == null ? 0 : view.getId()));
        }
    }

    private void showToast(CharSequence text) {
        if (mCurrentToast != null) {
            mCurrentToast.cancel();
        }

        mCurrentToast = Toast.makeText(PhotoHandleActivity.this, text, Toast.LENGTH_SHORT);
        mCurrentToast.show();
    }

    private class MatrixChangeListener implements OnMatrixChangedListener {

        @Override
        public void onMatrixChanged(RectF rect) {
//            mCurrMatrixTv.setText(rect.toString());
//            showToast(rect.toString());
        }
    }

    private class SingleFlingListener implements OnSingleFlingListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d("PhotoView", String.format(FLING_LOG_STRING, velocityX, velocityY));
            return true;
        }
    }

    public void showToast(String data) {
        Toast.makeText(PhotoHandleActivity.this, data, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isHandle = true;
        switch (resultCode) {
            case Activity.RESULT_CANCELED:
                //取消裁剪
                break;
            case UCrop.RESULT_ERROR:
                //裁剪出错
                if (data != null) {
                    Throwable cropError = UCrop.getError(data);
                    if (cropError != null) {
                        showToast(cropError.getMessage());
                    } else {
                        showToast("裁剪出现未知错误");
                    }
                } else {
                    showToast("获取相册图片出现错误");
                }
                break;

            case Activity.RESULT_OK:
                switch (requestCode) {
                    case UCrop.REQUEST_CROP:
                        inputUri = UCrop.getOutput(data);
                        mPhotoView.setImageURI(inputUri);
                        //初始化裁剪路径============================================
                        outputUri =  Uri.fromFile(AllConstant.getDiskCacheDir(PhotoHandleActivity.this));
                        break;
                    case PUZZLE:

                    case FILTER:
                        String path = data.getStringExtra("path");
                        if (path != null && !path.equals("")) {
                            Uri uri = Uri.parse("file://" + path);
                            inputUri = uri;
                            mPhotoView.setImageURI(inputUri);
                        }
                        break;
                    case GRAFFITI:
                        String graffitiPath = data.getStringExtra(GraffitiActivity.KEY_IMAGE_PATH);
                        if (TextUtils.isEmpty(graffitiPath)) {
                            return;
                        }else {
                            Uri uri = Uri.parse("file://" + graffitiPath);
                            inputUri = uri;
                            mPhotoView.setImageURI(inputUri);
                        }
                        break;
                }
                break;
        }
    }
}
