package com.yalantis.ucrop.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.media.Image;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.finalteam.rxgalleryfinal.R;

public class ImageButtonWithText extends LinearLayout {
    public ImageView imageView;
    public TextView textView;

    public ImageButtonWithText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageButtonWithText);
        /*
        * 在attrs.xml添加属性：
        *   <declare-styleable name="ImageButtonWithText">
             <attr name="picture" format="reference"/>
            </declare-styleable>
        * */
        int picture_id = a.getResourceId(R.styleable.ImageButtonWithText_picture, -1);
        /**
         * Recycle the TypedArray, to be re-used by a later caller. After calling
         * this function you must not ever touch the typed array again.
         */
        a.recycle();
        imageView = new ImageView(context, attrs);
        imageView.setPadding(0, 0, 0, 0);
        /**
         * Sets a drawable as the content of this ImageView.
         * This does Bitmap reading and decoding on the UI
         * thread, which can cause a latency hiccup.  If that's a concern,
         * consider using setImageDrawable(android.graphics.drawable.Drawable) or
         * setImageBitmap(android.graphics.Bitmap) instead.
         * 直接在UI线程读取和解码Bitmap，可能会存在潜在的性能问题
         * 可以考虑使用 setImageDrawable(android.graphics.drawable.Drawable)
         * 或者setImageBitmap(android.graphics.Bitmap) 代替
         */
        imageView.setImageResource(picture_id);
//        imageView.setBackgroundColor(getResources().getColor(R.color.wxColor));
        textView = new TextView(context, attrs);
//        textView.setBackgroundColor(getResources().getColor(R.color.wxColor));
        /**
         * Sets the horizontal alignment of the text and the
         * vertical gravity that will be used when there is extra space
         * in the TextView beyond what is required for the text itself.
         */
        //水平居中
//        textView.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
        textView.setPadding(0, 0, 0, 0);
//        setClickable(true);
        setFocusable(true);
        setOrientation(LinearLayout.VERTICAL);
        setClickable(false);//不被点击
        addView(imageView);
        addView(textView);
    }

    public ImageView getImageView(){
        return imageView;
    }
    public TextView getTextView(){
        return textView;
    }

    public void setText(int resId) {
        textView.setText(resId);
    }

    public void setText(CharSequence buttonText) {
        textView.setText(buttonText);
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
    }

    public void setTextSize(float size){
        textView.setTextSize(size);
    }
}