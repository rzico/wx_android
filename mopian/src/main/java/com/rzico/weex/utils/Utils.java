package com.rzico.weex.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.rzico.weex.Constant;
import com.rzico.weex.activity.BaseActivity;
//
//import org.apache.http.util.EncodingUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

/**
 * Created by linhuasen on 15/11/26.
 */
public class Utils {


//    public static void showMarketPrice(double price, double market, TextView tv) {
//        tv.setVisibility(price < market ? View.VISIBLE : View.GONE);
//        tv.setText("￥" + IntFormat(market));
//        tv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
//    }



    public static boolean isApkDebugable(Context context) {
        try {
            ApplicationInfo info= context.getApplicationInfo();
            return (info.flags&ApplicationInfo.FLAG_DEBUGGABLE)!=0;
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * 加载本地图片
     *
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void loadImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .centerCrop()
                .crossFade(500)
                .into(imageView);
    }

    public static void loadImage(Context context, String url, ImageView imageView, int mipmapId) {
        Glide.with(context)
                .load(url)
                .centerCrop()
                .placeholder(mipmapId)
                .crossFade(500)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void loadRoundImage(Context context, String url, ImageView imageView, int mipmapId) {
        Glide.with(context)
                .load(url)
                .placeholder(mipmapId)
                .transform(new GlideRoundTransform(context, 5))
                .crossFade(500)
                .into(imageView);
    }

    public static void loadRoundImage(Context context, String url, ImageView imageView, int mipmapId,int round) {
        Glide.with(context)
                .load(url)
                .placeholder(mipmapId)
                .transform(new GlideRoundTransform(context, round))
                .crossFade(500)
                .into(imageView);
    }


    public static void loadImage(Context context, String url, ImageView imageView, boolean isSource) {
        Glide.with(context)
                .load(url)
                .centerCrop()
//                .placeholder(R.mipmap.no_picture)
                .diskCacheStrategy(isSource ? DiskCacheStrategy.SOURCE : DiskCacheStrategy.NONE)
                .crossFade(500)
                .into(imageView);
    }

    public static void loadLocalImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .centerCrop()
//                .placeholder(R.mipmap.no_picture)
                .crossFade(500)
                .into(imageView);
    }

    /**
     * 隐藏键盘
     *
     * @param v
     */
    public static void hideSoft(Context context, View v) {
        InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static void changeFragment(FragmentActivity activity, int container, Fragment fragment, String TAG, boolean isAnim) {

        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();

        if (isAnim) {
//            ft.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out);
        }

        ft.replace(container, fragment, TAG);
        ft.addToBackStack(TAG);
        ft.commitAllowingStateLoss();
    }

    public static void performBackFragmet(FragmentActivity activity, int container, Fragment fragment, String TAG, boolean isAnim) {

        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();

        if (isAnim) {
//            ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_right_out, R.anim.slide_right_in, R.anim.slide_right_in);
        }

        ft.replace(container, fragment, TAG);
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }




    public static int toInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return -1;
        }
    }

    public static int toInt(String str, int def) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return def;
        }
    }

    public static double toDouble(String str, double err) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return err;
        }
    }

    public static String toIntString(double var) {
        DecimalFormat format = new DecimalFormat("0");
        String str = format.format(var);
        return "-0".equals(str) ? "0" : str;
    }

    public static void setCursorEnd(EditText edit) {
        CharSequence text = edit.getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
    }

    public static void call(Context context, String phoneNumber) {
//        Intent it = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
//            context.startActivity(it);
//        }
    }

    public static void changeFragment(FragmentActivity activity, int container, Class fragmentClass, String tag, Bundle args, boolean isAnim) {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
        boolean isFragmentExist = true;
        if (fragment == null) {
            try {
                isFragmentExist = false;
                fragment = (Fragment) fragmentClass.newInstance();
                fragment.setArguments(new Bundle());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (fragment.isAdded()) {
            return;
        }
        if (args != null && !args.isEmpty()) {
            fragment.getArguments().putAll(args);
        }
        if (isAnim) {
            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                    android.R.anim.fade_in, android.R.anim.fade_out);
        }

        if (isFragmentExist) {
            ft.replace(container, fragment);
        } else {
            ft.replace(container, fragment, tag);
        }
//        activity.getSupportFragmentManager().executePendingTransactions();
        ft.addToBackStack(tag);
        ft.commitAllowingStateLoss();
    }

    /**
     * 销毁所有Activity
     */
    public static void destoryActivity() {
//        Set<String> keySet = destoryMap.keySet();
//        for (String key : keySet) {
//            destoryMap.get(key).finish();
//        }

        for (Activity activity : destoryActivitys) {
            activity.finish();
        }
    }

    /**
     * 检查版本更新
     *
     * @return
     */

    public static boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public static String getVersionName(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.PERMISSION_GRANTED);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean hasPermission(BaseActivity activity, String permission) {

        if (canMakeSmores()) {

            return (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);

        }

        return true;
    }

    public static boolean checkConnection(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null == cm) {
            return false;
        }

        NetworkInfo info = cm.getActiveNetworkInfo();
        return null != info && info.isConnected()
                && (ConnectivityManager.TYPE_MOBILE == info.getType()
                || ConnectivityManager.TYPE_WIFI == info.getType());
    }

    public static void saveImage(Context context, View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "title", "description");
    }

    /**
     * 检测是否有emoji表情
     *
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }
    /**
     * 判断是否是Emoji
     *
     * @param codePoint
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
    }


    public static Bitmap createQRCode(String text, int w, int h) {
        try {

            if (TextUtils.isEmpty(text)) {
                return null;
            }

            new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, w, h);

            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = null;
            try {
                bitMatrix = new QRCodeWriter().encode(text,
                        BarcodeFormat.QR_CODE, w, h, hints);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * w + x] = 0xff000000;
                    } else {
                        pixels[y * w + x] = 0xffffffff;
                    }
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(w, h,
                    Bitmap.Config.ARGB_8888);

            bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static boolean isBankCard(String cardId) {
        char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return cardId.charAt(cardId.length() - 1) == bit;

    }

    private static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null
                || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            // 如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }

    /*验证手机号*/
    public static boolean isMobile(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }

        String regex = "1(3|4|5|7|8)[0-9]{9}";
        return str.matches(regex);
    }

    private static List<Activity> destoryActivitys = new ArrayList<>();

    /**
     * 添加到销毁队列
     *
     * @param activity 要销毁的activity
     */

    public static void addDestoryActivity(Activity activity) {
        destoryActivitys.add(activity);
    }

    /**
     * 保留两位小数
     *
     * @param param
     * @return
     */
    public static String doubleTo2Str(double param) {
        DecimalFormat dcmFmt = new DecimalFormat("0.00");
        return dcmFmt.format(param);

    }

    /**
     * 保留一位小数
     *
     * @param param
     * @return
     */
    public static String doubleTo1Str(double param) {
        DecimalFormat dcmFmt = new DecimalFormat("0.0");
        return dcmFmt.format(param);

    }

    /**
     * 取证
     *
     * @param param
     * @return
     */
    public static String doubleToStr(double param) {
        DecimalFormat dcmFmt = new DecimalFormat("0");
        return dcmFmt.format(param);
    }

    public static String floatToStr(float param){
        DecimalFormat dcmFmt = new DecimalFormat("0");
        return dcmFmt.format(param);
    }

    public static String getWeek(long timeStamp) {
        int mydate = 0;
        String week = null;
        Calendar cd = Calendar.getInstance();
        cd.setTime(new Date(timeStamp));
        mydate = cd.get(Calendar.DAY_OF_WEEK);
        // 获取指定日期转换成星期几
        if (mydate == 1) {
            week = "周日";
        } else if (mydate == 2) {
            week = "周一";
        } else if (mydate == 3) {
            week = "周二";
        } else if (mydate == 4) {
            week = "周三";
        } else if (mydate == 5) {
            week = "周四";
        } else if (mydate == 6) {
            week = "周五";
        } else if (mydate == 7) {
            week = "周六";
        }
        return week;
    }



    /**
     * dp2px
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px2dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static String getUuid(Context context) {

        TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return deviceUuid.toString().replaceAll("-", "");
    }

    public static String getMD5(String str) {
        if (str != null && !str.equals("")) {
            return MD5Utils.getMD5Str(str);
        }
        return str;
    }
    public static void setPricePoint(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }

        });

    }


    /**
     * 对TextView截取字段设置不同颜色
     *
     * @param context
     * @param message
     * @param end
     * @param tv
     * @param startColor
     * @param endColor
     */
    public static void SpannableString(Context context, String message, int end, TextView tv, int startColor, int endColor) {
        SpannableStringBuilder style = new SpannableStringBuilder(message);
        style.setSpan(new ForegroundColorSpan(context.getResources().getColor(startColor)), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ForegroundColorSpan(context.getResources().getColor(endColor)), end, message.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tv.setText(style);
    }

    public static double FormatPrice(double price) {
        BigDecimal b = new BigDecimal(price);
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String FormatPrice2String(double price) {
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(FormatPrice(price));
    }

    public static double FormatPrice(String price) {

        double priceNum = 0.00;
        if (price == null || price.length() == 0) {

        }
        else {
            priceNum = Double.parseDouble(price);
        }

        return FormatPrice(priceNum);
    }

    public static String FormatPrice2String(String price) {

        double priceNum = 0.00;
        if (price == null || price.length() == 0) {

        }
        else {
            priceNum = Double.parseDouble(price);
        }

        return FormatPrice2String(priceNum);
    }

    /**
     * 比较版本号的大小,前者大则返回一个正数,后者大返回一个负数,相等则返回0
     * @param version1
     * @param version2
     * @return
     */
    public static int compareVersion(String version1, String version2) throws Exception {
        if (version1 == null || version2 == null) {
            throw new Exception("compareVersion error:illegal params.");
        }
        String[] versionArray1 = version1.split("\\.");//注意此处为正则匹配，不能用"."；
        String[] versionArray2 = version2.split("\\.");
        int idx = 0;
        int minLength = Math.min(versionArray1.length, versionArray2.length);//取最小长度值
        int diff = 0;
        while (idx < minLength
                && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0//先比较长度
                && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {//再比较字符
            ++idx;
        }
        //如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
        diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
        return diff;
    }
}
