package cn.finalteam.rxgalleryfinal.ui.activity;

import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.yalantis.ucrop.view.TopView;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.rxgalleryfinal.R;
import cn.finalteam.rxgalleryfinal.bean.MediaBean;
import cn.finalteam.rxgalleryfinal.rxbus.RxBus;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.BaseResultEvent;
import cn.finalteam.rxgalleryfinal.rxbus.event.CloseRxMediaGridPageEvent;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageMultipleResultEvent;
import cn.finalteam.rxgalleryfinal.rxbus.event.MediaCheckChangeEvent;
import cn.finalteam.rxgalleryfinal.rxbus.event.MediaViewPagerChangedEvent;
import cn.finalteam.rxgalleryfinal.rxbus.event.OpenMediaPageFragmentEvent;
import cn.finalteam.rxgalleryfinal.rxbus.event.OpenMediaPreviewFragmentEvent;
import cn.finalteam.rxgalleryfinal.rxbus.event.RequestStorageReadAccessPermissionEvent;
import cn.finalteam.rxgalleryfinal.rxjob.RxJob;
import cn.finalteam.rxgalleryfinal.ui.fragment.MediaGridFragment;
import cn.finalteam.rxgalleryfinal.ui.fragment.MediaPageFragment;
import cn.finalteam.rxgalleryfinal.ui.fragment.MediaPreviewFragment;
import cn.finalteam.rxgalleryfinal.utils.Logger;
import cn.finalteam.rxgalleryfinal.utils.OsCompat;
import cn.finalteam.rxgalleryfinal.utils.ThemeUtils;
import cn.finalteam.rxgalleryfinal.view.ActivityFragmentView;
import io.reactivex.disposables.Disposable;

/**
 * Desction:
 * Author:pengjianbo  Dujinyang
 * Date:16/5/7 上午10:01
 */
public class MediaActivity extends BaseActivity implements ActivityFragmentView {

    public static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    public static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 102;
    public static final int REQUEST_CAMERA_ACCESS_PERMISSION = 103;

    private static final String EXTRA_CHECKED_LIST = EXTRA_PREFIX + ".CheckedList";
    private static final String EXTRA_SELECTED_INDEX = EXTRA_PREFIX + ".SelectedIndex";
    private static final String EXTRA_PAGE_MEDIA_LIST = EXTRA_PREFIX + ".PageMediaList";
    private static final String EXTRA_PAGE_POSITION = EXTRA_PREFIX + ".PagePosition";
    private static final String EXTRA_PREVIEW_POSITION = EXTRA_PREFIX + ".PreviewPosition";

    private MediaGridFragment mMediaGridFragment;
    private MediaPageFragment mMediaPageFragment;
    private MediaPreviewFragment mMediaPreviewFragment;

//    private View mToolbarDivider;

    private TopView topView;

    private ArrayList<MediaBean> mCheckedList;
    private int mSelectedIndex = 0;
    private ArrayList<MediaBean> mPageMediaList;
    private int mPagePosition;
    private int mPreviewPosition;
    private boolean isFinish = false;

    @Override
    public int getContentView() {
        return R.layout.gallery_activity_media;
    }

    private void initToolbar() {
        topView = (TopView) findViewById(R.id.toolbar);
        topView.setTitle("图片编辑");
        topView.setRightWord("完成");
        topView.setLeftIcon(cn.finalteam.rxgalleryfinal.R.drawable.gallery_ic_cross);
        topView.setOnLeftClickListener(new TopView.OnLeftClickListener() {
            @Override
            public void leftClick() {
                backAction();
            }
        });

    }
    @Override
    protected void onCreateOk(@Nullable Bundle savedInstanceState) {
        mMediaGridFragment = MediaGridFragment.newInstance(mConfiguration);
        if (!mConfiguration.isRadio()) {
            topView.setOnRightClickListener(new TopView.OnRightClickListener() {
                @Override
                public void rightClick() {
                    if (mMediaGridFragment != null && mMediaGridFragment.isShowRvBucketView()) {
                        mMediaGridFragment.hideRvBucketView();
                    } else {
                        if (mCheckedList != null && mCheckedList.size() > 0) {
                            BaseResultEvent event = new ImageMultipleResultEvent(mCheckedList);
                            RxBus.getDefault().post(event);
                            isFinish = true;
                            finish();
                        }
                    }
                }
            });
            topView.getRightView().setVisibility(View.VISIBLE);
        } else {
            //隐藏了 会把背景也隐藏了
            topView.getRightView().setText("");
        }
        mCheckedList = new ArrayList<>();
        List<MediaBean> selectedList = mConfiguration.getSelectedList();
        if (selectedList != null && selectedList.size() > 0) {
            mCheckedList.addAll(selectedList);
            if (mCheckedList.size() > 0) {
                String text = getResources().getString(R.string.gallery_over_button_text_checked, mCheckedList.size(), mConfiguration.getMaxSize());
                topView.getRightView().setText(text);
                topView.getRightView().setEnabled(true);
            } else {
                topView.getRightView().setText(R.string.gallery_over_button_text);
                topView.getRightView().setEnabled(false);
            }
        }

        showMediaGridFragment();
        subscribeEvent();
    }

    @Override
    public void findViews() {
//        mToolbarDivider = findViewById(R.id.toolbar_divider);
        initToolbar();
    }

    @Override
    protected void setTheme() {



        int statusBarColor = ThemeUtils.resolveColor(this, R.attr.gallery_color_statusbar, R.color.gallery_default_toolbar_bg);
        ThemeUtils.setStatusBarColor(statusBarColor, getWindow());

//        int dividerHeight = (int) ThemeUtils.resolveDimen(this, R.attr.gallery_toolbar_divider_height, R.dimen.gallery_default_toolbar_divider_height);
//        int dividerBottomMargin = (int) ThemeUtils.resolveDimen(this, R.attr.gallery_toolbar_bottom_margin, R.dimen.gallery_default_toolbar_bottom_margin);
//        LayoutParams dividerLP = new LayoutParams(LayoutParams.MATCH_PARENT, dividerHeight);
//        dividerLP.bottomMargin = dividerBottomMargin;
//        mToolbarDivider.setLayoutParams(dividerLP);

//        Drawable dividerDrawable = ThemeUtils.resolveDrawable(this, R.attr.gallery_toolbar_divider_bg, R.color.gallery_default_toolbar_divider_bg);
//        OsCompat.setBackgroundDrawableCompat(mToolbarDivider, dividerDrawable);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCheckedList != null) {
            outState.putParcelableArrayList(EXTRA_CHECKED_LIST, mCheckedList);
        }
        outState.putInt(EXTRA_SELECTED_INDEX, mSelectedIndex);
        if (mPageMediaList != null) {
            outState.putParcelableArrayList(EXTRA_PAGE_MEDIA_LIST, mPageMediaList);
        }
        outState.putInt(EXTRA_PAGE_POSITION, mPagePosition);
        outState.putInt(EXTRA_PREVIEW_POSITION, mPreviewPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        List<MediaBean> list = savedInstanceState.getParcelableArrayList(EXTRA_CHECKED_LIST);
        if (list != null && list.size() > 0) {
            mCheckedList.clear();
            mCheckedList.addAll(list);
        }
        mPageMediaList = savedInstanceState.getParcelableArrayList(EXTRA_PAGE_MEDIA_LIST);
        mPagePosition = savedInstanceState.getInt(EXTRA_PAGE_POSITION);
        mPreviewPosition = savedInstanceState.getInt(EXTRA_PREVIEW_POSITION);
        mSelectedIndex = savedInstanceState.getInt(EXTRA_SELECTED_INDEX);
        if (!mConfiguration.isRadio()) {
            switch (mSelectedIndex) {
                case 1:
                    showMediaPageFragment(mPageMediaList, mPagePosition);
                    break;
                case 2:
                    showMediaPreviewFragment();
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            backAction();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showMediaGridFragment() {
        mMediaPreviewFragment = null;
        mMediaPageFragment = null;
        mSelectedIndex = 0;

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mMediaGridFragment);
        if (mMediaPreviewFragment != null) {
            ft.hide(mMediaPreviewFragment);
        }
        if (mMediaPageFragment != null) {
            ft.hide(mMediaPageFragment);
        }
        ft.show(mMediaGridFragment)
                .commit();

        if (mConfiguration.isImage()) {
            topView.setTitle(R.string.gallery_media_grid_image_title);
        } else {
            topView.setTitle(R.string.gallery_media_grid_video_title);
        }
    }

    @Override
    public void showMediaPageFragment(ArrayList<MediaBean> list, int position) {
        mSelectedIndex = 1;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mMediaPageFragment = MediaPageFragment.newInstance(mConfiguration, list, position);
        ft.add(R.id.fragment_container, mMediaPageFragment);
        mMediaPreviewFragment = null;
        ft.hide(mMediaGridFragment);
        ft.show(mMediaPageFragment);
        ft.commit();

        String title = getString(R.string.gallery_page_title, position + 1, list.size());
        topView.setTitle(title);
    }

    @Override
    public void showMediaPreviewFragment() {
        mSelectedIndex = 2;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mMediaPreviewFragment = MediaPreviewFragment.newInstance(mConfiguration, mPreviewPosition);
        ft.add(R.id.fragment_container, mMediaPreviewFragment);
        mMediaPageFragment = null;
        ft.hide(mMediaGridFragment);
        ft.show(mMediaPreviewFragment);
        ft.commit();

        String title = getString(R.string.gallery_page_title, mPreviewPosition, mCheckedList.size());
        topView.setTitle(title);
    }

    private void subscribeEvent() {
        Disposable subscriptionOpenMediaPreviewEvent = RxBus.getDefault().toObservable(OpenMediaPreviewFragmentEvent.class)
                .map(mediaPreviewEvent -> mediaPreviewEvent)
                .subscribeWith(new RxBusDisposable<OpenMediaPreviewFragmentEvent>() {
                    @Override
                    protected void onEvent(OpenMediaPreviewFragmentEvent openMediaPreviewFragmentEvent) {
                        mPreviewPosition = 0;
                        showMediaPreviewFragment();
                    }
                });

        RxBus.getDefault().add(subscriptionOpenMediaPreviewEvent);

        Disposable subscriptionMediaCheckChangeEvent = RxBus.getDefault().toObservable(MediaCheckChangeEvent.class)
                .map(mediaCheckChangeEvent -> mediaCheckChangeEvent)
                .subscribeWith(new RxBusDisposable<MediaCheckChangeEvent>() {
                    @Override
                    protected void onEvent(MediaCheckChangeEvent mediaCheckChangeEvent) {
                        MediaBean mediaBean = mediaCheckChangeEvent.getMediaBean();
                        if (mCheckedList.contains(mediaBean)) {
                            mCheckedList.remove(mediaBean);
                        } else {
                            mCheckedList.add(mediaBean);
                        }

                        if (mCheckedList.size() > 0) {
                            String text = getResources().getString(R.string.gallery_over_button_text_checked, mCheckedList.size(), mConfiguration.getMaxSize());
                            topView.setTitle(text);
                            topView.getLeftView().setEnabled(true);
                        } else {
                            TextView textView = new TextView(MediaActivity.this);
                            topView.setTitle(R.string.gallery_over_button_text);
                            topView.getLeftView().setEnabled(false);
                        }
                    }
                });
        RxBus.getDefault().add(subscriptionMediaCheckChangeEvent);

        Disposable subscriptionMediaViewPagerChangedEvent = RxBus.getDefault().toObservable(MediaViewPagerChangedEvent.class)
                .map(mediaViewPagerChangedEvent -> mediaViewPagerChangedEvent)
                .subscribeWith(new RxBusDisposable<MediaViewPagerChangedEvent>() {
                    @Override
                    protected void onEvent(MediaViewPagerChangedEvent mediaPreviewViewPagerChangedEvent) {
                        int curIndex = mediaPreviewViewPagerChangedEvent.getCurIndex();
                        int totalSize = mediaPreviewViewPagerChangedEvent.getTotalSize();
                        if (mediaPreviewViewPagerChangedEvent.isPreview()) {
                            mPreviewPosition = curIndex;
                        } else {
                            mPagePosition = curIndex;
                        }
                        String title = getString(R.string.gallery_page_title, curIndex + 1, totalSize);
                        topView.setTitle(title);
                    }
                });
        RxBus.getDefault().add(subscriptionMediaViewPagerChangedEvent);

        Disposable subscriptionCloseRxMediaGridPageEvent = RxBus.getDefault().toObservable(CloseRxMediaGridPageEvent.class)
                .subscribeWith(new RxBusDisposable<CloseRxMediaGridPageEvent>() {
                    @Override
                    protected void onEvent(CloseRxMediaGridPageEvent closeRxMediaGridPageEvent) throws Exception {
                        finish();
                    }
                });
        RxBus.getDefault().add(subscriptionCloseRxMediaGridPageEvent);

        Disposable subscriptionOpenMediaPageFragmentEvent = RxBus.getDefault().toObservable(OpenMediaPageFragmentEvent.class)
                .subscribeWith(new RxBusDisposable<OpenMediaPageFragmentEvent>() {
                    @Override
                    protected void onEvent(OpenMediaPageFragmentEvent openMediaPageFragmentEvent) {
                        mPageMediaList = openMediaPageFragmentEvent.getMediaBeanList();
                        mPagePosition = openMediaPageFragmentEvent.getPosition();

                        showMediaPageFragment(mPageMediaList, mPagePosition);
                    }
                });
        RxBus.getDefault().add(subscriptionOpenMediaPageFragmentEvent);
    }

    public List<MediaBean> getCheckedList() {
        return mCheckedList;
    }

    private void backAction() {
        if (mMediaGridFragment != null && mMediaGridFragment.isShowRvBucketView()) {
            mMediaGridFragment.hideRvBucketView();
            return;
        }
        if ((mMediaPreviewFragment != null && mMediaPreviewFragment.isVisible())
                || (mMediaPageFragment != null && mMediaPageFragment.isVisible())) {
            showMediaGridFragment();
            return;
        }
        onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backAction();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //传空的表示 关闭了  如果是点击完成 就不要 返回空
        if(!isFinish){
            BaseResultEvent close = new ImageMultipleResultEvent(null);
            RxBus.getDefault().post(close);
        }
        RxBus.getDefault().removeAllStickyEvents();
        RxBus.getDefault().clear();
        RxJob.getDefault().clearJob();
    }

    private StateListDrawable createDefaultOverButtonBgDrawable() {
        int dp12 = (int) ThemeUtils.applyDimensionDp(this, 12.f);
        int dp8 = (int) ThemeUtils.applyDimensionDp(this, 8.f);
        float dp4 = ThemeUtils.applyDimensionDp(this, 4.f);
        float[] round = new float[]{dp4, dp4, dp4, dp4, dp4, dp4, dp4, dp4};
        ShapeDrawable pressedDrawable = new ShapeDrawable(new RoundRectShape(round, null, null));
        pressedDrawable.setPadding(dp12, dp8, dp12, dp8);
        int pressedColor = ThemeUtils.resolveColor(this, R.attr.gallery_toolbar_over_button_pressed_color, R.color.gallery_default_toolbar_bg);
        pressedDrawable.getPaint().setColor(pressedColor);

        int normalColor = ThemeUtils.resolveColor(this, R.attr.gallery_toolbar_over_button_normal_color, R.color.gallery_default_toolbar_bg);
        ShapeDrawable normalDrawable = new ShapeDrawable(new RoundRectShape(round, null, null));
        normalDrawable.setPadding(dp12, dp8, dp12, dp8);
        normalDrawable.getPaint().setColor(normalColor);

        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
        stateListDrawable.addState(new int[]{}, normalDrawable);

        return stateListDrawable;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Logger.i("onRequestPermissionsResult:requestCode=" + requestCode + " permissions=" + permissions[0]);
        switch (requestCode) {
            case REQUEST_STORAGE_READ_ACCESS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    RxBus.getDefault().post(new RequestStorageReadAccessPermissionEvent(true, RequestStorageReadAccessPermissionEvent.TYPE_WRITE));
                } else {
                    finish();
                }
                break;
            case REQUEST_STORAGE_WRITE_ACCESS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    RxBus.getDefault().post(new RequestStorageReadAccessPermissionEvent(true, RequestStorageReadAccessPermissionEvent.TYPE_WRITE));
                } else {
                    finish();
                }
                break;
            case REQUEST_CAMERA_ACCESS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    RxBus.getDefault().post(new RequestStorageReadAccessPermissionEvent(true, RequestStorageReadAccessPermissionEvent.TYPE_CAMERA));
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}
