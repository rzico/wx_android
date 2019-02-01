package com.rzico.weex.view;

import java.util.ArrayList;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rzico.weex.Constant;
import com.rzico.weex.R;
import com.rzico.weex.WXApplication;
import com.rzico.weex.utils.SharedUtils;
import com.rzico.weex.utils.Utils;


public class NavigationView extends LinearLayout {


    private int image_width;
    private int image_height;
    private float text_size;
    private TextView dotText;
    private Context context;
    /**
     * 选中图片数组
     */
    private int[] selectedImage;
    /**
     * 未选中图片数组
     */
    private int[] unSelectedImage;


    private ArrayList<TextView> textViews = new ArrayList<TextView>();


    private ArrayList<ImageView> imageViews = new ArrayList<ImageView>();


    public OnItemClickListener onItemClickListener;


    public NavigationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public NavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NavigationView);
        image_height = typedArray.getInteger(R.styleable.NavigationView_image_height, 23);
        image_width = typedArray.getInteger(R.styleable.NavigationView_image_width, 23);
//        text_size = typedArray.getDimension(R.styleable.NavigationView_text_size, 12);
        text_size = 12;
        dotText = new TextView(context);
        LinearLayout.LayoutParams textDotLp = new LinearLayout.LayoutParams(Utils.dp2px(context, 20), Utils.dp2px(context, 20));
        dotText.setTextColor(Color.WHITE);
        dotText.setGravity(Gravity.CENTER);
        dotText.setBackground(getResources().getDrawable(R.drawable.dot_message));
        dotText.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        dotText.setVisibility(INVISIBLE);
        dotText.setLayoutParams(textDotLp);
        typedArray.recycle();
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    /**
     * 动态添加布局
     *
     * @param titles          导航标题
     * @param selectedImage   选中时的图片
     * @param unSelectedImage 未选中时的图片
     * @param screenWidth     屏幕的宽度
     * @param mHeight         控件自身高度
     * @param context
     */
    @SuppressWarnings("deprecation")
    public void setLayout(String[] titles, int[] selectedImage, int[] unSelectedImage, int screenWidth, int mHeight,
                          Context context) {
        this.context = context;
        this.selectedImage = selectedImage;
        this.unSelectedImage = unSelectedImage;
        setOrientation(LinearLayout.HORIZONTAL);
        if (titles != null && titles.length != 0) {
            int widthScale = screenWidth / titles.length;
            if(!WXApplication.getAppInfo().getStyle().equals("both")) {
                image_width = image_width + 12;
                image_height = image_height + 12;
            }
            for (int i = 0; i < titles.length; i++) {
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setBackgroundColor(Color.parseColor(WXApplication.getAppInfo().getBackgroundColor()));
                layout.setGravity(Gravity.CENTER);

//                LinearLayout.LayoutParams Lp_v = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dp2px(context, 1));
//                View v = new View(context);
//                v.setBackgroundColor(Color.parseColor(WXApplication.getAppInfo().getBorderStyle()));
//                v.setLayoutParams(Lp_v);

                LinearLayout.LayoutParams layoutLp = new LinearLayout.LayoutParams(widthScale,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                layoutLp.gravity = Gravity.CENTER;
                layout.setLayoutParams(layoutLp);


                ImageView image = new ImageView(context);

                LinearLayout.LayoutParams imageLp = new LinearLayout.LayoutParams(Utils.dp2px(context, image_width), Utils.dp2px(context, image_height));
                imageLp.gravity = Gravity.CENTER_HORIZONTAL;
                imageLp.topMargin = 5;
                image.setImageDrawable(context.getResources().getDrawable(unSelectedImage[i]));
                image.setLayoutParams(imageLp);

                TextView tv_title = new TextView(context);
                LinearLayout.LayoutParams textLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                tv_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_size);
                tv_title.setText(titles[i]);
                tv_title.setLayoutParams(textLp);

                /**创建 dot布局
                 <FrameLayout
                android:layout_width="wrap_content"
                android:layout_height="wrap_content">

                <LinearLayout
                android:layout_width="45dp"
                android:layout_height="25dp"
                android:orientation="vertical"
                android:gravity="bottom|center">
                <ImageView
                android:id="@+id/rg_group_huihua_im"
                android:layout_width="23dp"
                android:layout_height="23dp"
                android:layout_gravity="center_horizontal" />
                </LinearLayout>
                <TextView
                android:id="@+id/iv_message_dot"
                android:layout_width="20dp"
                android:layout_height="20dp"
                android:layout_gravity="right|top"
                android:textSize="12sp"
                android:textColor="@color/white"
                android:gravity="center"
                android:background="@drawable/dot_message"
                android:visibility="invisible"/>
            </FrameLayout>**/
                FrameLayout frameLayout = new FrameLayout(context);
                LinearLayout.LayoutParams textFl = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                frameLayout.setLayoutParams(textFl);

                LinearLayout linearLayout = new LinearLayout(context);
                LinearLayout.LayoutParams textLl = new LinearLayout.LayoutParams(Utils.dp2px(context, 45), Utils.dp2px(context, 25));

                if(!WXApplication.getAppInfo().getStyle().equals("both")) {
                    linearLayout.setGravity(Gravity.CENTER);
                    linearLayout.setOrientation(VERTICAL);
                } else {
                    linearLayout.setGravity(Gravity.BOTTOM | Gravity.CENTER);
                    linearLayout.setOrientation(VERTICAL);
                }

                linearLayout.setLayoutParams(textLl);

                LinearLayout linearLayout2 = new LinearLayout(context);
                LinearLayout.LayoutParams textLl2 = new LinearLayout.LayoutParams(Utils.dp2px(context, 40), Utils.dp2px(context, 25));
                linearLayout2.setGravity(Gravity.TOP | Gravity.END);
                linearLayout2.setOrientation(VERTICAL);
                linearLayout2.setLayoutParams(textLl2);

                if(WXApplication.getAppInfo().getStyle().equals("both")){
                    if (WXApplication.getAppInfo().getDot() != i){
                        layout.addView(image);
                    }else {
                        linearLayout.addView(image);
                        linearLayout2.addView(dotText);
                        frameLayout.addView(linearLayout);
                        frameLayout.addView(linearLayout2);
                        layout.addView(frameLayout);
                    }
                    layout.addView(tv_title);
                }else {
                    if (WXApplication.getAppInfo().getDot() != i){
                        layout.addView(image);
                    }else {
                        linearLayout.addView(image);
                        linearLayout2.addView(dotText);
                        frameLayout.addView(linearLayout);
                        frameLayout.addView(linearLayout2);
                        layout.addView(frameLayout);
                    }
                }

                layout.setOnClickListener(new OnClickListener() {


                    @Override
                    public void onClick(View v) {
                        int position = (Integer) v.getTag();
                        if (WXApplication.getAppInfo().getTabBar().get(position).isRequireAuth() && (SharedUtils.readLoginId() == 0 || !Constant.loginState )) {//没有登录过

                        }else {
                            setColorLing(position);
                        }
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(position);
                        }
                    }
                });


                layout.setTag(i);
                addView(layout, widthScale, mHeight);
                imageViews.add(image);
                textViews.add(tv_title);
            }
        }
    }


    /**
     * 底部导航点击接口回调
     */
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    /**
     * 设置文本和图片为亮色
     *
     * @param position
     */
    public void setColorLing(int position) {
        setColorDark();
        for (int i = 0; i < textViews.size(); i++) {
            if (position == i) {
                textViews.get(i).setTextColor(Color.parseColor(WXApplication.getAppInfo().getSelectedColor()));
                imageViews.get(i).setImageDrawable(context.getResources().getDrawable(selectedImage[i]));
            }
        }
    }

    public TextView getDotTextView(){
        return dotText;
    }



    /**
     * 设置文本和图片为暗色
     */
    public void setColorDark() {
        for (int i = 0; i < textViews.size(); i++) {
            textViews.get(i).setTextColor(Color.parseColor(WXApplication.getAppInfo().getColor()));
            imageViews.get(i).setImageDrawable(context.getResources().getDrawable(unSelectedImage[i]));
        }
    }


}