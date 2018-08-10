package com.rzico.weex.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.OnMatrixChangedListener;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.OnSingleFlingListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.rzico.weex.R;
import com.rzico.weex.model.info.Message;
import com.rzico.weex.module.JSCallBaskManager;
import com.rzico.weex.utils.AntiShake;
import com.rzico.weex.utils.BarTextColorUtils;
import com.rzico.weex.utils.PathUtils;
import com.rzico.weex.utils.PhotoUtils;
import com.rzico.weex.utils.photo.PhotoHandle;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.callback.BitmapLoadCallback;
import com.yalantis.ucrop.model.AspectRatio;
import com.yalantis.ucrop.model.ExifInfo;
import com.yalantis.ucrop.util.BitmapLoadUtils;
import cn.finalteam.rxgalleryfinal.utils.FoucedStateListDrawable;
import cn.finalteam.rxgalleryfinal.utils.GPUImageUtil;
import com.yalantis.ucrop.view.CropImageView;
import cn.finalteam.rxgalleryfinal.view.ImageButtonWithText;
import com.yalantis.ucrop.view.TopView;
import com.yalantis.ucrop.view.widget.AspectRatioTextView;
import com.rzico.weex.constant.AllConstant;

import org.xutils.common.util.FileUtil;

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
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

public class FilterActivity extends AppCompatActivity {

    public static final int SUCCESS = 1;
    public static final int ERROR = 0;

    private PhotoView mPhotoView;
//    static final String PHOTO_TAP_TOAST_STRING = "Photo Tap! X: %.2f %% Y:%.2f %% ID: %d";
    static final String SCALE_TOAST_STRING = "Scaled to: %.2ff";
//    static final String FLING_LOG_STRING = "Fling velocityX: %.2f, velocityY: %.2f";
    private Toast mCurrentToast;
    private static final int PUZZLE = 101;

    private TopView topView;

    private boolean mShowBottomControls;

    private AntiShake antiShake = new AntiShake();

    private Bitmap currentBitmap;
    @DrawableRes
    private int mToolbarCancelDrawable;

    private ViewGroup mLayoutFilter;

    private Uri inputUri, outputUri;
    private List<ViewGroup> mCropFilterViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarTextColorUtils.StatusBarLightMode(FilterActivity.this, R.color.wxColor);
        setContentView(R.layout.activity_filter);
        Intent intent = getIntent();
        initView(intent);
        initData(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initData(Intent intent) {
        inputUri = intent.getParcelableExtra(PhotoHandle.EXTRA_INPUT_URI);
        outputUri = intent.getParcelableExtra(PhotoHandle.EXTRA_OUTPUT_URI);

        if (inputUri != null && outputUri != null) {
            int maxBitmapSize = BitmapLoadUtils.calculateMaxBitmapSize(FilterActivity.this);
            BitmapLoadUtils.decodeBitmapInBackground(FilterActivity.this, inputUri, outputUri, maxBitmapSize, maxBitmapSize,
                    new BitmapLoadCallback() {

                        @Override
                        public void onBitmapLoaded(@NonNull Bitmap bitmap, @NonNull ExifInfo exifInfo, @NonNull String imageInputPath, @Nullable String imageOutputPath) {
                            mPhotoView.setImageBitmap(bitmap);
                            currentBitmap = ((BitmapDrawable)mPhotoView.getDrawable()).getBitmap();
                        }

                        @Override
                        public void onFailure(@NonNull Exception bitmapWorkerException) {
                            Log.e("PhotoHandle", "onFailure: setImageUri", bitmapWorkerException);

                        }
                    });
        }

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
    private void initView(Intent intent) {

        mPhotoView = (PhotoView) findViewById(R.id.iv_photo);
            mLayoutFilter = (ViewGroup) findViewById(R.id.layout_filter);
        mPhotoView.setOnMatrixChangeListener(new MatrixChangeListener());
        mPhotoView.setOnPhotoTapListener(new PhotoTapListener());
        mPhotoView.setOnSingleFlingListener(new SingleFlingListener());
        topView = (TopView)findViewById(R.id.toolbar);

//        mToolbarCancelDrawable = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_WIDGET_CANCEL_DRAWABLE, cn.finalteam.rxgalleryfinal.R.drawable.gallery_ic_cross);

        mShowBottomControls = !intent.getBooleanExtra(UCrop.Options.EXTRA_HIDE_BOTTOM_CONTROLS, false);
        if (mShowBottomControls) {
            setupFilterWidget(intent);
        }

        topView = (TopView) findViewById(R.id.toolbar);
        topView.setTitle("滤镜");
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

                if (antiShake.check(topView.getId())) return;//防抖动
                saveFilter();
            }
        });
    }

    protected void saveFilter() {
//        Bitmap bitmap = mPhotoView.getDrawingCache().copy(Bitmap.Config.RGB_565, true);
        Bitmap bitmap = ((BitmapDrawable)mPhotoView.getDrawable()).getBitmap();
        try {
            File file = FileUtil.saveBitmapJPG("MOPIAN" + System.currentTimeMillis(), bitmap, AllConstant.getDiskCachePath(FilterActivity.this));
            // 最后通知图库更新
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            sendBroadcast(intent);//这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦

            Intent reIntent = new Intent();
            reIntent.putExtra("path", file.getPath());
            setResult(Activity.RESULT_OK, reIntent);
            finish();
        } catch (IOException e) {
            e.printStackTrace();
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

        mCurrentToast = Toast.makeText(FilterActivity.this, text, Toast.LENGTH_SHORT);
        mCurrentToast.show();
    }
    /**
     * 设置滤镜小组件
     */
    private void setupFilterWidget(Intent intent) {
        LinearLayout wrapperFilterList = (LinearLayout) findViewById(R.id.layout_filter);
        FrameLayout wrapperFilter;
        AspectRatioTextView aspectRatioTextView;
        ArrayList<AspectRatio> filterRatioList = new ArrayList<>();

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.weight = 1;

        List<GPUImageFilter> gpuimageFilters = GPUImageUtil.getImageFilter();
        for (GPUImageFilter g : gpuimageFilters){

            wrapperFilter = (FrameLayout) getLayoutInflater().inflate(R.layout.ucrop_filter, null);
            wrapperFilter.setLayoutParams(lp);

            filterRatioList.add(new AspectRatio( GPUImageUtil.gpuImageStrings.get(filterRatioList.size()).toUpperCase(),
                    CropImageView.SOURCE_IMAGE_ASPECT_RATIO, CropImageView.SOURCE_IMAGE_ASPECT_RATIO));

//            imageView.setImageBitmap(GPUImageUtil.getGPUImageFromUri(UCropActivity.this,  intent.getParcelableExtra(UCrop.EXTRA_INPUT_URI), gpuimageFilters.indexOf(g)));
            aspectRatioTextView = ((AspectRatioTextView) wrapperFilter.getChildAt(0));
            aspectRatioTextView.setActiveColor(getResources().getColor(R.color.wxColor));
            aspectRatioTextView.setAspectRatio(filterRatioList.get(filterRatioList.size() - 1));
            wrapperFilterList.addView(wrapperFilter);

            mCropFilterViews.add(wrapperFilter);
        }
        mCropFilterViews.get(0).setSelected(true);
        for(int  i= 0; i < mCropFilterViews.size(); i++) {
            final int j = i;
            mCropFilterViews.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentBitmap != null){
                        mPhotoView.setImageBitmap(GPUImageUtil.getGPUImageFromUri(FilterActivity.this, currentBitmap, j));
                        if (!v.isSelected()) {
                            for (ViewGroup cropAspectRatioView : mCropFilterViews) {
                                cropAspectRatioView.setSelected(cropAspectRatioView == v);
                            }
                        }
                    }
                }
            });
        }
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
//            Log.d("PhotoView", String.format(FLING_LOG_STRING, velocityX, velocityY));
            return true;
        }
    }

    public void showToast(String data) {
        Toast.makeText(FilterActivity.this, data, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                        break;
                    case PUZZLE:
                        String path = data.getStringExtra("path");
                        if (path != null && !path.equals("")) {
                            Uri uri = Uri.parse("file://" + path);
                            inputUri = uri;
                            mPhotoView.setImageURI(inputUri);
                        }
                        break;
                }
                break;
        }
    }
}
