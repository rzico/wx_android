package cn.finalteam.rxgalleryfinal.utils;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import cn.finalteam.rxgalleryfinal.R;

/**
 * Hack class to properly support state drawable back to Android 1.6
 * 点击 改变图标颜色类
 */
public class FoucedStateListDrawable extends StateListDrawable {

    private int mSelectionColor;
    private TextView textView;

    public FoucedStateListDrawable(Drawable drawable, int selectionColor) {
        super();
        this.mSelectionColor = selectionColor;
        addState(new int[]{android.R.attr.state_pressed}, drawable);
        addState(new int[]{}, drawable);
    }
    public FoucedStateListDrawable(Drawable drawable, TextView textView, int selectionColor) {
        super();
        this.mSelectionColor = selectionColor;
        this.textView = textView;
        addState(new int[]{android.R.attr.state_pressed}, drawable);
        addState(new int[]{}, drawable);
    }

    @Override
    protected boolean onStateChange(int[] states) {
        boolean isStatePressedInArray = false;
        for (int state : states) {
            if (state == android.R.attr.state_pressed) {
                isStatePressedInArray = true;
            }
        }
        if (isStatePressedInArray) {
            super.setColorFilter(mSelectionColor, PorterDuff.Mode.SRC_ATOP);
            if(textView!=null){
                textView.setTextColor(mSelectionColor);
            }
        } else {
            super.clearColorFilter();
            if(textView!=null) {
                textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.textColor));
            }
        }
        return super.onStateChange(states);
    }

    @Override
    public boolean isStateful() {
        return true;
    }

}
