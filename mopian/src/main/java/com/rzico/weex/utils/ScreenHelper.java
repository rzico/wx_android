package com.rzico.weex.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * 屏幕大小有关
 * 
 * @author <a href="http://www.xunhou.me" target="_blank">Kelvin</a>
 *
 */
public class ScreenHelper {

	//    * 获得状态栏的高度
	public static int getStatusHeight(Context context) {
		int statusHeight=0;
		if (statusHeight <= 0) {
			try {
				Class<?> clazz = Class.forName("com.android.internal.R$dimen");
				Object object = clazz.newInstance();
				int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
				statusHeight = context.getResources().getDimensionPixelSize(height);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return statusHeight;
	}
	/**
	 * 获取手机屏幕DisplayMetrics属性
	 * 
	 * @param
	 * @return
	 */
	public static DisplayMetrics getDisplayMetrics(Context context) {
		if (context == null) {
			return null;
		}
		DisplayMetrics metrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics;
	}

	/**
	 * 获取屏幕的大小
	 *
	 * @param context
	 * @return
	 */
	public static Screen getScreenPix(Context context) {
		DisplayMetrics dm = getDisplayMetrics(context);
		return new Screen(dm.widthPixels, dm.heightPixels);
	}

	/**
	 * 获取屏幕的宽
	 *
	 * @param context
	 * @return
	 */
	public static int getScreenWidthPix(Context context) {
		return getScreenPix(context).widthPixels;
	}

	/**
	 * 获取屏幕的高
	 *
	 * @param context
	 * @return
	 */
	public static int getScreenHeightPix(Context context) {
		return getScreenPix(context).heightPixels;
	}

	/**
	 * 获取当前宽和高
	 *
	 * @param
	 * @return
	 */
	public static String getWidthAndHeight(Context context) {
		return getScreenHeightPix(context) + "*" + getScreenWidthPix(context);
	}

	public static class Screen {
		public int widthPixels;
		public int heightPixels;

		public Screen() {
		}

		public Screen(int widthPixels, int heightPixels) {
			this.widthPixels = widthPixels;
			this.heightPixels = heightPixels;
		}

		@Override
		public String toString() {
			return "(" + widthPixels + "," + heightPixels + ")";
		}
	}
}
