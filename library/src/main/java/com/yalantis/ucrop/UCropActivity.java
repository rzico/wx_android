package com.yalantis.ucrop;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yalantis.ucrop.callback.BitmapCropCallback;
import com.yalantis.ucrop.model.AspectRatio;
import com.yalantis.ucrop.util.FastBitmapDrawable;
import com.yalantis.ucrop.util.GPUImageUtil;
import com.yalantis.ucrop.util.SelectedStateListDrawable;
import com.yalantis.ucrop.view.CropImageView;
import com.yalantis.ucrop.view.GestureCropImageView;
import com.yalantis.ucrop.view.OverlayView;
import com.yalantis.ucrop.view.TopView;
import com.yalantis.ucrop.view.TransformImageView;
import com.yalantis.ucrop.view.UCropView;
import com.yalantis.ucrop.view.widget.AspectRatioTextView;
import com.yalantis.ucrop.view.widget.HorizontalProgressWheelView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import cn.finalteam.rxgalleryfinal.R;
import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.bean.MediaBean;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 */

@SuppressWarnings("ConstantConditions")
public class UCropActivity extends AppCompatActivity {

    public static final int DEFAULT_COMPRESS_QUALITY = 90;
    public static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;

    public static final int NONE = 0;
    public static final int SCALE = 1;
    public static final int ROTATE = 2;
    public static final int ALL = 3;

    private static final int PUZZLE = 101;

    private int currentFilter = 0;

    private Uri inputUri;
    private Uri outputUri;
    @IntDef({NONE, SCALE, ROTATE, ALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GestureTypes {

    }

    private static final String TAG = "UCropActivity";

    private static final int TABS_COUNT = 3;
    private static final int SCALE_WIDGET_SENSITIVITY_COEFFICIENT = 15000;
    private static final int ROTATE_WIDGET_SENSITIVITY_COEFFICIENT = 42;

    private String mToolbarTitle;

    // Enables dynamic coloring
    private int mToolbarColor;
    private int mStatusBarColor;
    private int mActiveWidgetColor;
    private int mToolbarWidgetColor;
    @ColorInt private int mRootViewBackgroundColor;
    @DrawableRes private int mToolbarCancelDrawable;
    @DrawableRes private int mToolbarCropDrawable;
    private int mLogoColor;

    private boolean mShowBottomControls;
    private boolean mShowLoader = true;

    private UCropView mUCropView;
    private GestureCropImageView mGestureCropImageView;
    private OverlayView mOverlayView;

    private InputStream inputStream;
//  功能按钮图片
    private ViewGroup mWrapperStateAspectRatio, mWrapperStateRotate, mWrapperPhotoChange, mWrapperStateFilter, mWrapperPhotoPuzzle;
//  功能面板
//  mLayoutPhotoChange 原先的放大缩小功能被改成更换图片
    private ViewGroup mLayoutAspectRatio, mLayoutRotate, mLayoutFilter;
    private List<ViewGroup> mCropAspectRatioViews = new ArrayList<>();
    private List<ViewGroup> mCropFilterViews = new ArrayList<>();
    private TextView mTextViewRotateAngle, mTextViewScalePercent;
    private View mBlockingView;

    private Bitmap.CompressFormat mCompressFormat = DEFAULT_COMPRESS_FORMAT;
    private int mCompressQuality = DEFAULT_COMPRESS_QUALITY;
    private int[] mAllowedGestures = new int[]{NONE, NONE, SCALE, SCALE};//前面两个1、换图 2、拼图
    //缩放SCALE
    //旋转ROTATE
//    private int[] mAllowedGestures = new int[]{SCALE, ROTATE, ALL};
//
//    public void reSet(){
//        mToolbarTitle = null;
//        mUCropView = null;
//        mGestureCropImageView = null;
//        mOverlayView = null;
//        mWrapperStateAspectRatio = null;
//        mWrapperStateRotate = null;
//        mWrapperPhotoChange = null;
//        mLayoutAspectRatio = null;
//        mLayoutRotate = null;
//        mCropAspectRatioViews = null;
//        mCropAspectRatioViews = new ArrayList<>();
//        mTextViewRotateAngle = null;
//        mBlockingView = null;
//        mTextViewScalePercent = null;
//        mCompressFormat = DEFAULT_COMPRESS_FORMAT;
//        mCompressQuality = DEFAULT_COMPRESS_QUALITY;
//    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ucrop_activity_photobox);

        final Intent intent = getIntent();

        setupViews(intent);

        setImageData(intent, null);

        setInitialState();

        addBlockingView();

    }

//    @Override
//    public boolean onCreateOptionsMenu(final Menu menu) {
//        getMenuInflater().inflate(R.menu.ucrop_menu_activity, menu);
//
//        // Change crop & loader menu icons color to match the rest of the UI colors
//
//        MenuItem menuItemLoader = menu.findItem(R.id.menu_loader);
//        Drawable menuItemLoaderIcon = menuItemLoader.getIcon();
//        if (menuItemLoaderIcon != null) {
//            try {
//                menuItemLoaderIcon.mutate();
//                menuItemLoaderIcon.setColorFilter(mToolbarWidgetColor, PorterDuff.Mode.SRC_ATOP);
//                menuItemLoader.setIcon(menuItemLoaderIcon);
//            } catch (IllegalStateException e) {
//                Log.i(TAG, String.format("%s - %s", e.getMessage(), getString(R.string.ucrop_mutate_exception_hint)));
//            }
//            ((Animatable) menuItemLoader.getIcon()).start();
//        }

//        MenuItem menuItemCrop = menu.findItem(R.id.menu_crop);
//        Drawable menuItemCropIcon = ContextCompat.getDrawable(this, mToolbarCropDrawable);
//        if (menuItemCropIcon != null) {
//            menuItemCropIcon.mutate();
//            menuItemCropIcon.setColorFilter(mToolbarWidgetColor, PorterDuff.Mode.SRC_ATOP);
//            menuItemCrop.setTitle("完成");
////            menuItemCrop.setIcon(menuItemCropIcon);
//        }

//        return true;
//    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        menu.findItem(R.id.menu_crop).setVisible(!mShowLoader);
//        menu.findItem(R.id.menu_loader).setVisible(mShowLoader);
//        return super.onPrepareOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
        //保存按钮
//        if (item.getItemId() == R.id.menu_crop) {
//            cropAndSaveImage();
//        } else if (item.getItemId() == android.R.id.home) {
//            onBackPressed();
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGestureCropImageView != null) {
            mGestureCropImageView.cancelAllAnimations();
        }
    }

    /**
     * This method extracts all data from the incoming intent and setups views properly.
     */
    private void setImageData(@NonNull Intent intent, Uri uri) {
        if(uri == null){
            inputUri = intent.getParcelableExtra(UCrop.EXTRA_INPUT_URI);
        }else {
            inputUri = uri;
        }

        outputUri = intent.getParcelableExtra(UCrop.EXTRA_OUTPUT_URI);
        processOptions(intent);

        if (inputUri != null && outputUri != null) {
            try {
                mGestureCropImageView.setImageUri(inputUri, outputUri);
            } catch (Exception e) {
                setResultError(e);
                finish();
            }
        } else {
            setResultError(new NullPointerException(getString(R.string.ucrop_error_input_data_is_absent)));
            finish();
        }
    }

    /**
     * This method extracts {@link UCrop.Options #optionsBundle} from incoming intent
     * and setups Activity, {@link OverlayView} and {@link CropImageView} properly.
     */
    @SuppressWarnings("deprecation")
    private void processOptions(@NonNull Intent intent) {
        // Bitmap compression options
        String compressionFormatName = intent.getStringExtra(UCrop.Options.EXTRA_COMPRESSION_FORMAT_NAME);
        Bitmap.CompressFormat compressFormat = null;
        if (!TextUtils.isEmpty(compressionFormatName)) {
            compressFormat = Bitmap.CompressFormat.valueOf(compressionFormatName);
        }
        mCompressFormat = (compressFormat == null) ? DEFAULT_COMPRESS_FORMAT : compressFormat;

        mCompressQuality = intent.getIntExtra(UCrop.Options.EXTRA_COMPRESSION_QUALITY, UCropActivity.DEFAULT_COMPRESS_QUALITY);


        // Gestures options
        int[] allowedGestures = intent.getIntArrayExtra(UCrop.Options.EXTRA_ALLOWED_GESTURES);
        if (allowedGestures != null && allowedGestures.length == TABS_COUNT) {
            mAllowedGestures = allowedGestures;
        }

        // Crop image view options
        mGestureCropImageView.setMaxBitmapSize(intent.getIntExtra(UCrop.Options.EXTRA_MAX_BITMAP_SIZE, CropImageView.DEFAULT_MAX_BITMAP_SIZE));
        mGestureCropImageView.setMaxScaleMultiplier(intent.getFloatExtra(UCrop.Options.EXTRA_MAX_SCALE_MULTIPLIER, CropImageView.DEFAULT_MAX_SCALE_MULTIPLIER));
        mGestureCropImageView.setImageToWrapCropBoundsAnimDuration(intent.getIntExtra(UCrop.Options.EXTRA_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION, CropImageView.DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION));

        // Overlay view options
        mOverlayView.setFreestyleCropEnabled(intent.getBooleanExtra(UCrop.Options.EXTRA_FREE_STYLE_CROP, OverlayView.DEFAULT_FREESTYLE_CROP_MODE != OverlayView.FREESTYLE_CROP_MODE_DISABLE));

        mOverlayView.setDimmedColor(intent.getIntExtra(UCrop.Options.EXTRA_DIMMED_LAYER_COLOR, getResources().getColor(R.color.ucrop_color_default_dimmed)));
        mOverlayView.setCircleDimmedLayer(intent.getBooleanExtra(UCrop.Options.EXTRA_CIRCLE_DIMMED_LAYER, OverlayView.DEFAULT_CIRCLE_DIMMED_LAYER));

        mOverlayView.setShowCropFrame(intent.getBooleanExtra(UCrop.Options.EXTRA_SHOW_CROP_FRAME, OverlayView.DEFAULT_SHOW_CROP_FRAME));
        mOverlayView.setCropFrameColor(intent.getIntExtra(UCrop.Options.EXTRA_CROP_FRAME_COLOR, getResources().getColor(R.color.ucrop_color_default_crop_frame)));
        mOverlayView.setCropFrameStrokeWidth(intent.getIntExtra(UCrop.Options.EXTRA_CROP_FRAME_STROKE_WIDTH, getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_frame_stoke_width)));

        mOverlayView.setShowCropGrid(intent.getBooleanExtra(UCrop.Options.EXTRA_SHOW_CROP_GRID, OverlayView.DEFAULT_SHOW_CROP_GRID));
        mOverlayView.setCropGridRowCount(intent.getIntExtra(UCrop.Options.EXTRA_CROP_GRID_ROW_COUNT, OverlayView.DEFAULT_CROP_GRID_ROW_COUNT));
        mOverlayView.setCropGridColumnCount(intent.getIntExtra(UCrop.Options.EXTRA_CROP_GRID_COLUMN_COUNT, OverlayView.DEFAULT_CROP_GRID_COLUMN_COUNT));
        mOverlayView.setCropGridColor(intent.getIntExtra(UCrop.Options.EXTRA_CROP_GRID_COLOR, getResources().getColor(R.color.ucrop_color_default_crop_grid)));
        mOverlayView.setCropGridStrokeWidth(intent.getIntExtra(UCrop.Options.EXTRA_CROP_GRID_STROKE_WIDTH, getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_grid_stoke_width)));

        // Aspect ratio options
        float aspectRatioX = intent.getFloatExtra(UCrop.EXTRA_ASPECT_RATIO_X, 0);
        float aspectRatioY = intent.getFloatExtra(UCrop.EXTRA_ASPECT_RATIO_Y, 0);

        int aspectRationSelectedByDefault = intent.getIntExtra(UCrop.Options.EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT, 0);
        ArrayList<AspectRatio> aspectRatioList = intent.getParcelableArrayListExtra(UCrop.Options.EXTRA_ASPECT_RATIO_OPTIONS);

        if (aspectRatioX > 0 && aspectRatioY > 0) {
            if (mWrapperStateAspectRatio != null) {
                mWrapperStateAspectRatio.setVisibility(View.GONE);
            }
            mGestureCropImageView.setTargetAspectRatio(aspectRatioX / aspectRatioY);
        } else if (aspectRatioList != null && aspectRationSelectedByDefault < aspectRatioList.size()) {
            mGestureCropImageView.setTargetAspectRatio(aspectRatioList.get(aspectRationSelectedByDefault).getAspectRatioX() /
                    aspectRatioList.get(aspectRationSelectedByDefault).getAspectRatioY());
        } else {
            mGestureCropImageView.setTargetAspectRatio(CropImageView.SOURCE_IMAGE_ASPECT_RATIO);
        }

        // Result bitmap max size options
        int maxSizeX = intent.getIntExtra(UCrop.EXTRA_MAX_SIZE_X, 0);
        int maxSizeY = intent.getIntExtra(UCrop.EXTRA_MAX_SIZE_Y, 0);

        if (maxSizeX > 0 && maxSizeY > 0) {
            mGestureCropImageView.setMaxResultImageSizeX(maxSizeX);
            mGestureCropImageView.setMaxResultImageSizeY(maxSizeY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(PUZZLE == requestCode){
            if(resultCode == PuzzleActivity.SUCCESS){
                String path = data.getStringExtra("path");
                if(path!=null&&!path.equals("")){
                    Uri uri = Uri.parse("file://" + path);
                    reSetImage(uri);
                }
            }else {
//           用户取消了

            }
        }
    }

    private void setupViews(@NonNull Intent intent) {

        mStatusBarColor = intent.getIntExtra(UCrop.Options.EXTRA_STATUS_BAR_COLOR, ContextCompat.getColor(this, R.color.gallery_default_toolbar_bg));
        mToolbarColor = intent.getIntExtra(UCrop.Options.EXTRA_TOOL_BAR_COLOR, ContextCompat.getColor(this, R.color.gallery_default_toolbar_bg));
        mActiveWidgetColor = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_COLOR_WIDGET_ACTIVE, ContextCompat.getColor(this, R.color.gallery_default_toolbar_bg));
        mToolbarWidgetColor = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_WIDGET_COLOR_TOOLBAR, ContextCompat.getColor(this, R.color.ucrop_color_toolbar_widget));

        mToolbarCancelDrawable = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_WIDGET_CANCEL_DRAWABLE, R.drawable.gallery_ic_cross);
        mToolbarCropDrawable = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_WIDGET_CROP_DRAWABLE, R.drawable.ucrop_ic_done);

        mToolbarTitle = intent.getStringExtra(UCrop.Options.EXTRA_UCROP_TITLE_TEXT_TOOLBAR);
        mToolbarTitle = mToolbarTitle != null ? mToolbarTitle : getResources().getString(R.string.ucrop_label_edit_photo);
        mLogoColor = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_LOGO_COLOR, ContextCompat.getColor(this, R.color.ucrop_color_default_logo));
        mShowBottomControls = !intent.getBooleanExtra(UCrop.Options.EXTRA_HIDE_BOTTOM_CONTROLS, false);
        mRootViewBackgroundColor = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_ROOT_VIEW_BACKGROUND_COLOR, ContextCompat.getColor(this, R.color.ucrop_color_crop_background));

        setupAppBar();
        initiateRootViews();

        if (mShowBottomControls) {
            //功能界面
            ViewGroup photoBox = (ViewGroup) findViewById(R.id.ucrop_photobox);
            View.inflate(this, R.layout.ucrop_controls, photoBox);

            mWrapperStateAspectRatio = (ViewGroup) findViewById(R.id.state_aspect_ratio);
            mWrapperStateAspectRatio.setOnClickListener(mStateClickListener);
            mWrapperStateRotate = (ViewGroup) findViewById(R.id.state_rotate);
            mWrapperStateRotate.setOnClickListener(mStateClickListener);
//            mWrapperPhotoChange = (ViewGroup) findViewById(R.id.state_photo_change);
//            mWrapperPhotoChange.setOnClickListener(mStateClickListener);
//            mWrapperPhotoPuzzle = (ViewGroup) findViewById(R.id.state_photo_puzzle);
//            mWrapperPhotoPuzzle.setOnClickListener(mStateClickListener);
//            mWrapperStateFilter = (ViewGroup) findViewById(R.id.state_filter);
//            mWrapperStateFilter.setOnClickListener(mStateClickListener);

//            mLayoutFilter = (ViewGroup) findViewById(R.id.layout_filter);
            mLayoutAspectRatio = (ViewGroup) findViewById(R.id.layout_aspect_ratio);
            mLayoutRotate = (ViewGroup) findViewById(R.id.layout_rotate_wheel);
//            原先是放大缩小 改成了 更换图片
//            mLayoutScale = (ViewGroup) findViewById(R.id.layout_photo_change);
//          这里是初始化功能页面
            setupAspectRatioWidget(intent);
//            setupFilterWidget(intent);
            setupRotateWidget();
//            setupScaleWidget();
            setupStatesWrapper();
        }
    }

    /**
     * Configures and styles both status bar and toolbar.
     */
    private void setupAppBar() {
        setStatusBarColor(mStatusBarColor);

        final TopView toolbar = (TopView) findViewById(R.id.toolbar);

        toolbar.setTitle("裁剪");
        toolbar.setRightWord("完成");
        toolbar.setLeftIcon(cn.finalteam.rxgalleryfinal.R.drawable.gallery_ic_cross);
        toolbar.setOnRightClickListener(new TopView.OnRightClickListener() {
            @Override
            public void rightClick() {
                cropAndSaveImage();
            }
        });

        // Set all of the Toolbar coloring
        toolbar.setBackgroundColor(mToolbarColor);
        toolbar.getLeftView().setTextColor(mToolbarWidgetColor);
//        toolbar.getLeftView().setText(mToolbarTitle);

        // Color buttons inside the Toolbar
        Drawable stateButtonDrawable = ContextCompat.getDrawable(this, mToolbarCancelDrawable).mutate();
        stateButtonDrawable.setColorFilter(mToolbarWidgetColor, PorterDuff.Mode.SRC_ATOP);

        toolbar.setOnLeftClickListener(new TopView.OnLeftClickListener() {
            @Override
            public void leftClick() {
                finish();
            }
        });

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void initiateRootViews() {
        mUCropView = (UCropView) findViewById(R.id.ucrop);
        mGestureCropImageView = mUCropView.getCropImageView();
        mOverlayView = mUCropView.getOverlayView();

        mGestureCropImageView.setTransformImageListener(mImageListener);

        ((ImageView) findViewById(R.id.image_view_logo)).setColorFilter(mLogoColor, PorterDuff.Mode.SRC_ATOP);

        findViewById(R.id.ucrop_frame).setBackgroundColor(mRootViewBackgroundColor);
    }

    private TransformImageView.TransformImageListener mImageListener = new TransformImageView.TransformImageListener() {
        @Override
        public void onRotate(float currentAngle) {
            setAngleText(currentAngle);
        }

        @Override
        public void onScale(float currentScale) {
            setScaleText(currentScale);
        }

        @Override
        public void onLoadComplete() {
            mUCropView.animate().alpha(1).setDuration(300).setInterpolator(new AccelerateInterpolator());
            mBlockingView.setClickable(false);
            mShowLoader = false;
            supportInvalidateOptionsMenu();
        }

        @Override
        public void onLoadFailure(@NonNull Exception e) {
            setResultError(e);
            finish();
        }
    };

    /**
     * Use {@link #mActiveWidgetColor} for color filter
     * 设置选中和不选中颜色状态
     */
    private void setupStatesWrapper() {
//        ImageView photoChangeImageView = (ImageView) findViewById(R.id.image_view_photo_change);
        ImageView stateRotateImageView = (ImageView) findViewById(R.id.image_view_state_rotate);
        ImageView stateAspectRatioImageView = (ImageView) findViewById(R.id.image_view_state_aspect_ratio);
//        ImageView stateFilterImageView = (ImageView) findViewById(R.id.image_view_state_filter);

//        photoChangeImageView.setImageDrawable(new SelectedStateListDrawable(photoChangeImageView.getDrawable(), mActiveWidgetColor));
        stateRotateImageView.setImageDrawable(new SelectedStateListDrawable(stateRotateImageView.getDrawable(), mActiveWidgetColor));
        stateAspectRatioImageView.setImageDrawable(new SelectedStateListDrawable(stateAspectRatioImageView.getDrawable(), mActiveWidgetColor));
//        stateFilterImageView.setImageDrawable(new SelectedStateListDrawable(stateFilterImageView.getDrawable(), mActiveWidgetColor));
    }


    /**
     * Sets status-bar color for L devices.
     * 设置状态栏颜色
     * @param color - status-bar color
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(color);
            }
        }
    }


    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    /**
     * 设置滤镜小组件
     */
    private void setupFilterWidget(Intent intent) {
//        LinearLayout wrapperFilterList = (LinearLayout) findViewById(R.id.layout_filter);
//        FrameLayout wrapperFilter;
//        AspectRatioTextView aspectRatioTextView;
//        ArrayList<AspectRatio> filterRatioList = new ArrayList<>();
//
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
//        lp.weight = 1;
//
//        List<GPUImageFilter> gpuimageFilters = GPUImageUtil.getImageFilter();
//        for (GPUImageFilter g : gpuimageFilters){
//
//            wrapperFilter = (FrameLayout) getLayoutInflater().inflate(R.layout.ucrop_filter, null);
//            wrapperFilter.setLayoutParams(lp);
//
//            filterRatioList.add(new AspectRatio( GPUImageUtil.gpuImageStrings.get(filterRatioList.size()).toUpperCase(),
//                    CropImageView.SOURCE_IMAGE_ASPECT_RATIO, CropImageView.SOURCE_IMAGE_ASPECT_RATIO));
//
////            imageView.setImageBitmap(GPUImageUtil.getGPUImageFromUri(UCropActivity.this,  intent.getParcelableExtra(UCrop.EXTRA_INPUT_URI), gpuimageFilters.indexOf(g)));
//            aspectRatioTextView = ((AspectRatioTextView) wrapperFilter.getChildAt(0));
//            aspectRatioTextView.setActiveColor(mActiveWidgetColor);
//            aspectRatioTextView.setAspectRatio(filterRatioList.get(filterRatioList.size() - 1));
//            wrapperFilterList.addView(wrapperFilter);
//
//            mCropFilterViews.add(wrapperFilter);
//        }
//        mCropFilterViews.get(0).setSelected(true);
//        for(int  i= 0; i < mCropFilterViews.size(); i++) {
//            int j = i;
//            mCropFilterViews.get(i).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mGestureCropImageView.setImageBitmap(GPUImageUtil.getGPUImageFromUri(UCropActivity.this, mGestureCropImageView.getCurrentBitmap(), j));
//                    currentFilter = j;
//                    if (!v.isSelected()) {
//                        for (ViewGroup cropAspectRatioView : mCropFilterViews) {
//                            cropAspectRatioView.setSelected(cropAspectRatioView == v);
//                        }
//                    }
//                }
//            });
//        }
    }
    /**
     * 设置裁剪的高宽比
     * @param intent
     */
    private void setupAspectRatioWidget(@NonNull Intent intent) {

        int aspectRationSelectedByDefault = intent.getIntExtra(UCrop.Options.EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT, 0);
        ArrayList<AspectRatio> aspectRatioList = intent.getParcelableArrayListExtra(UCrop.Options.EXTRA_ASPECT_RATIO_OPTIONS);

        if (aspectRatioList == null || aspectRatioList.isEmpty()) {
            aspectRationSelectedByDefault = 2;

            aspectRatioList = new ArrayList<>();
            aspectRatioList.add(new AspectRatio(null, 1, 1));
            aspectRatioList.add(new AspectRatio(null, 3, 4));
            aspectRatioList.add(new AspectRatio(getString(R.string.ucrop_label_original).toUpperCase(),
                    CropImageView.SOURCE_IMAGE_ASPECT_RATIO, CropImageView.SOURCE_IMAGE_ASPECT_RATIO));
            aspectRatioList.add(new AspectRatio(null, 3, 2));
            aspectRatioList.add(new AspectRatio(null, 16, 9));
        }

        LinearLayout wrapperAspectRatioList = (LinearLayout) findViewById(R.id.layout_aspect_ratio);

        FrameLayout wrapperAspectRatio;
        AspectRatioTextView aspectRatioTextView;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        for (AspectRatio aspectRatio : aspectRatioList) {
            wrapperAspectRatio = (FrameLayout) getLayoutInflater().inflate(R.layout.ucrop_aspect_ratio, null);
            wrapperAspectRatio.setLayoutParams(lp);
            aspectRatioTextView = ((AspectRatioTextView) wrapperAspectRatio.getChildAt(0));
            aspectRatioTextView.setActiveColor(mActiveWidgetColor);
            aspectRatioTextView.setAspectRatio(aspectRatio);
            wrapperAspectRatioList.addView(wrapperAspectRatio);
            mCropAspectRatioViews.add(wrapperAspectRatio);
        }

        mCropAspectRatioViews.get(aspectRationSelectedByDefault).setSelected(true);

        for (ViewGroup cropAspectRatioView : mCropAspectRatioViews) {
            cropAspectRatioView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGestureCropImageView.setTargetAspectRatio(
                            ((AspectRatioTextView) ((ViewGroup) v).getChildAt(0)).getAspectRatio(v.isSelected()));
                    mGestureCropImageView.setImageToWrapCropBounds();
                    if (!v.isSelected()) {
                        for (ViewGroup cropAspectRatioView : mCropAspectRatioViews) {
                            cropAspectRatioView.setSelected(cropAspectRatioView == v);
                        }
                    }
                }
            });
        }
        if(aspectRatioList.size() == 1){
            wrapperAspectRatioList.setVisibility(View.GONE);
        }
    }

    /**
     * 设置旋转组件
     */
    private void setupRotateWidget() {
        mTextViewRotateAngle = ((TextView) findViewById(R.id.text_view_rotate));
        ((HorizontalProgressWheelView) findViewById(R.id.rotate_scroll_wheel))
                .setScrollingListener(new HorizontalProgressWheelView.ScrollingListener() {
                    @Override
                    public void onScroll(float delta, float totalDistance) {
                        mGestureCropImageView.postRotate(delta / ROTATE_WIDGET_SENSITIVITY_COEFFICIENT);
                    }

                    @Override
                    public void onScrollEnd() {
                        mGestureCropImageView.setImageToWrapCropBounds();
                    }

                    @Override
                    public void onScrollStart() {
                        mGestureCropImageView.cancelAllAnimations();
                    }
                });

        ((HorizontalProgressWheelView) findViewById(R.id.rotate_scroll_wheel)).setMiddleLineColor(mActiveWidgetColor);


        findViewById(R.id.wrapper_reset_rotate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetRotation();
            }
        });
        findViewById(R.id.wrapper_rotate_by_angle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateByAngle(90);
            }
        });
    }

    /**
     * 设置放大缩小功能
     */
    private void setupScaleWidget() {
        mTextViewScalePercent = ((TextView) findViewById(R.id.text_view_scale));
        ((HorizontalProgressWheelView) findViewById(R.id.scale_scroll_wheel))
                .setScrollingListener(new HorizontalProgressWheelView.ScrollingListener() {
                    @Override
                    public void onScroll(float delta, float totalDistance) {
                        if (delta > 0) {
                            mGestureCropImageView.zoomInImage(mGestureCropImageView.getCurrentScale()
                                    + delta * ((mGestureCropImageView.getMaxScale() - mGestureCropImageView.getMinScale()) / SCALE_WIDGET_SENSITIVITY_COEFFICIENT));
                        } else {
                            mGestureCropImageView.zoomOutImage(mGestureCropImageView.getCurrentScale()
                                    + delta * ((mGestureCropImageView.getMaxScale() - mGestureCropImageView.getMinScale()) / SCALE_WIDGET_SENSITIVITY_COEFFICIENT));
                        }
                    }
                    @Override
                    public void onScrollEnd() {
                        mGestureCropImageView.setImageToWrapCropBounds();
                    }

                    @Override
                    public void onScrollStart() {
                        mGestureCropImageView.cancelAllAnimations();
                    }
                });
        ((HorizontalProgressWheelView) findViewById(R.id.scale_scroll_wheel)).setMiddleLineColor(mActiveWidgetColor);
    }

    private void setAngleText(float angle) {
        if (mTextViewRotateAngle != null) {
            mTextViewRotateAngle.setText(String.format(Locale.getDefault(), "%.1f°", angle));
        }
    }

    private void setScaleText(float scale) {
        if (mTextViewScalePercent != null) {
            mTextViewScalePercent.setText(String.format(Locale.getDefault(), "%d%%", (int) (scale * 100)));
        }
    }

    private void resetRotation() {
        mGestureCropImageView.postRotate(-mGestureCropImageView.getCurrentAngle());
        mGestureCropImageView.setImageToWrapCropBounds();
    }

    private void rotateByAngle(int angle) {
        mGestureCropImageView.postRotate(angle);
        mGestureCropImageView.setImageToWrapCropBounds();
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
     * 设置最初的状态 如 设置 裁剪是默认选中功能
     */
    private void setInitialState() {
        if (mShowBottomControls) {
            if (mWrapperStateAspectRatio.getVisibility() == View.VISIBLE) {
                setWidgetState(R.id.state_aspect_ratio);
            } else {
//                setWidgetState(R.id.state_photo_change);
            }
        } else {
            setAllowedGestures(0);
        }
    }

    /**
     * 根据id设置小组件即底部功能组件状态 即点击切完功能试图 --- 值得学习
     * @param stateViewId
     */
    private void setWidgetState(@IdRes int stateViewId) {
        if (!mShowBottomControls) return;
//
//        if (stateViewId != R.id.state_photo_change && stateViewId != R.id.state_photo_puzzle){//因为如果是点第一个 就不改变状态 因为第一个是选择图片 单击 不是选中
            mWrapperStateAspectRatio.setSelected(stateViewId == R.id.state_aspect_ratio);
            mWrapperStateRotate.setSelected(stateViewId == R.id.state_rotate);
//            mWrapperStateFilter.setSelected(stateViewId == R.id.state_filter);
//
//            mLayoutFilter.setVisibility(stateViewId == R.id.state_filter ? View.VISIBLE : View.GONE);
            mLayoutAspectRatio.setVisibility(stateViewId == R.id.state_aspect_ratio ? View.VISIBLE : View.GONE);
            mLayoutRotate.setVisibility(stateViewId == R.id.state_rotate ? View.VISIBLE : View.GONE);
//        }
//        mWrapperPhotoChange.setSelected(stateViewId == R.id.state_photo_change);
//        mLayoutPhotoChange.setVisibility(stateViewId == R.id.state_photo_change ? View.VISIBLE : View.GONE);

//        if (stateViewId == R.id.state_photo_change) {
//            setAllowedGestures(0);
//        } else if(stateViewId == R.id.state_photo_puzzle){
//            setAllowedGestures(1);
//        }
//        else if(stateViewId == R.id.state_filter){
//            setAllowedGestures(2);
//        }
         if (stateViewId == R.id.state_rotate) {
            setAllowedGestures(2);
        } else if (stateViewId == R.id.state_aspect_ratio){
            setAllowedGestures(2);
        }
    }


    private void reSetImage(Uri uri){
        mUCropView.resetCropImageView();
        final Intent intent = getIntent();
        initiateRootViews();
        setImageData(intent, uri);
        for (ViewGroup cropAspectRatioView : mCropAspectRatioViews) {
            cropAspectRatioView.setSelected(cropAspectRatioView == mCropAspectRatioViews.get(2));
        }
        setInitialState();
//                            mGestureCropImageView.zoomOutImage(0.5f);
//                            mGestureCropImageView.setImageToWrapCropBounds();
        myHandler.sendEmptyMessage(0);
    }

    private void setAllowedGestures(int tab) {
        //设置组件判断是裁剪还是旋转
        if(tab == 0){
            //则是 更换照片
            RxGalleryFinal.with(UCropActivity.this).image().radio().imageLoader(ImageLoaderType.PICASSO)
                    .subscribe(new RxBusResultDisposable<ImageRadioResultEvent>() {
                        @Override
                        protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) throws Exception {

                            Uri uri = Uri.parse("file://" + imageRadioResultEvent.getResult().getOriginalPath());
                            reSetImage(uri);


                        }
                    })
                    .openGallery();
         }else if(tab == 1){
            //则是拼图
            int  height = 500;
            int  width  = 1000;

            List<MediaBean> mediaBeens = new ArrayList<>();
            try {
                MediaBean mediaBean = new MediaBean();
                mediaBean.setOriginalPath(inputUri.getPath());
                mediaBeens.add(mediaBean);
                Intent intent = new Intent();
                intent.putExtra("height", height);
                intent.putExtra("width", width);
                intent.putExtra("pics", (Serializable) mediaBeens);
                intent.setClass(UCropActivity.this, PuzzleActivity.class);
                startActivityForResult(intent, PUZZLE);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            mGestureCropImageView.setScaleEnabled(mAllowedGestures[tab] == ALL || mAllowedGestures[tab] == SCALE);
            mGestureCropImageView.setRotateEnabled(mAllowedGestures[tab] == ALL || mAllowedGestures[tab] == ROTATE);
        }
    }
    Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    //调整更换图片完的位置
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mGestureCropImageView.zoomOutImage(0.5f);
                            mGestureCropImageView.setImageToWrapCropBounds(true);
                        }
                    }, 200);
                    break;
                case 1://这里弄设置完图片后执行  因为 设置图片是异步的  这里是裁剪图片
                    //裁剪和保存图片
                    mGestureCropImageView.cropAndSaveImage(mCompressFormat, mCompressQuality, new BitmapCropCallback() {

                        @Override
                        public void onBitmapCropped(@NonNull Uri resultUri, int offsetX, int offsetY, int imageWidth, int imageHeight) {
                            setResultUri(resultUri, mGestureCropImageView.getTargetAspectRatio(), offsetX, offsetY, imageWidth, imageHeight);
                            finish();
                        }

                        @Override
                        public void onCropFailure(@NonNull Throwable t) {
                            setResultError(t);
                            finish();
                        }
                    });
                    break;
            }
        }
    };
    /**
     * Adds view that covers everything below the Toolbar.添加视图涵盖下面的工具栏。
     * When it's clickable - user won't be able to click/touch anything below the Toolbar.
     * Need to block user input while loading and cropping an image.
     */
    private void addBlockingView() {
        if (mBlockingView == null) {
            mBlockingView = new View(this);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.addRule(RelativeLayout.BELOW, R.id.toolbar);
            mBlockingView.setLayoutParams(lp);
            mBlockingView.setClickable(true);
        }
        ((RelativeLayout) findViewById(R.id.ucrop_photobox)).addView(mBlockingView);
    }
   private String cacheUrl =  Environment.getExternalStorageDirectory().getPath() + "/" + "com.rzico.weex" + "/";
    private Uri saveImage(Bitmap bitmap) {
        try {
        //保存的缓存路径
            String path = cacheUrl + "cache/img/"+ UUID.randomUUID().toString() + ".jpg";
        File filePic = new File(path);
        if (!filePic.exists()) {
            filePic.getParentFile().mkdirs();
                filePic.createNewFile();
        }
            FileOutputStream fos = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return Uri.parse("file://" + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
 private Dialog progressDialog;
    protected void cropAndSaveImage() {
        mBlockingView.setClickable(true);
        mShowLoader = true;
        try {
            progressDialog = new Dialog(UCropActivity.this,R.style.progress_dialog);
            progressDialog.setContentView(R.layout.dialog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            TextView msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
            msg.setText("卖力加载中");
            progressDialog.show();
            mGestureCropImageView.setImageUri(inputUri, outputUri, myHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 设置返回uri
     * @param uri
     * @param resultAspectRatio
     * @param offsetX
     * @param offsetY
     * @param imageWidth
     * @param imageHeight
     */
    protected void setResultUri(Uri uri, float resultAspectRatio, int offsetX, int offsetY, int imageWidth, int imageHeight) {
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        setResult(RESULT_OK, new Intent()
                .putExtra(UCrop.EXTRA_OUTPUT_URI, uri)
                .putExtra(UCrop.EXTRA_OUTPUT_CROP_ASPECT_RATIO, resultAspectRatio)
                .putExtra(UCrop.EXTRA_OUTPUT_IMAGE_WIDTH, imageWidth)
                .putExtra(UCrop.EXTRA_OUTPUT_IMAGE_HEIGHT, imageHeight)
                .putExtra(UCrop.EXTRA_OUTPUT_OFFSET_X, offsetX)
                .putExtra(UCrop.EXTRA_OUTPUT_OFFSET_Y, offsetY)
        );
    }

    protected void setResultError(Throwable throwable) {
        setResult(UCrop.RESULT_ERROR, new Intent().putExtra(UCrop.EXTRA_ERROR, throwable));
    }

}
