package com.rzico.weex;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import static com.rzico.weex.Constant.resURL;

/**
 * Created by Jinlesoft on 2017/9/18.
 */

public class WXActivityManager {


    //weex路径
    //  登录页面
    public static final String login = "filt://view/index.js";
    public static final String test = "http://192.168.1.108:8081/cover.weex.js?hot-reload_controller=1&_wx_tpl=http://192.168.1.108:8081/cover.weex.js";




    public static void pageTo(Activity activity, String url) {

        //        String url = "file://assets/edit.js";
        if (!TextUtils.isEmpty(url)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String scheme = Uri.parse(url).getScheme();
            StringBuilder builder = new StringBuilder();
            if (TextUtils.equals("file", scheme)) {
                intent.putExtra("isLocal", "true");
            } else if (!TextUtils.equals("http", scheme) && !TextUtils.equals("https", scheme)) {
                builder.append("http:");
            }
            builder.append(url);

            Uri uri = Uri.parse(builder.toString());
            intent.setData(uri);
            intent.addCategory("com.taobao.android.intent.category.WEEX");
            intent.setPackage(activity.getPackageName());
            activity.startActivity(intent);
        }
    }
}
