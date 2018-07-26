package com.tencent.qcloud.ui;

import android.content.Context;



public final class DensityUtil {

    private static float density = -1F;
    private static int widthPixels = -1;
    private static int heightPixels = -1;

    private DensityUtil() {
    }

    public static float getDensity(Context context) {
        if (density <= 0F) {
            density = context.getResources().getDisplayMetrics().density;
        }
        return density;
    }

    public static int dip2px(float dpValue, Context context) {
        return (int) (dpValue * getDensity(context) + 0.5f);
    }

    public static int px2dip(float pxValue, Context context) {
        return (int) (pxValue / getDensity(context) + 0.5f);
    }


}
