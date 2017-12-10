package com.rzico.weex.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jinlesoft on 2017/9/8.
 */

public class WeexPageAdapter extends PagerAdapter {
    private List<View> mViews;

    public WeexPageAdapter(List<View> mViews){
        this.mViews = mViews;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // 销毁View
        container.removeView(mViews.get(position));
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // 初始化View
        View view = mViews.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
