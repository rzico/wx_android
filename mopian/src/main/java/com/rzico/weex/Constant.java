package com.rzico.weex;

import android.os.Handler;

import com.taobao.weex.WXSDKInstance;
import com.rzico.weex.constant.AllConstant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shiweiwei on 2015/11/2.
 * http://helper.tiaohuo.com
 * http://www.ruishangquan.com/
 * http://192.168.0.63:8080/tiaohuo/
 */
public class Constant {

    //这里的WEEX_CATEGORY 需要与配置文件中的意图配置统一： <category android:name="com.taobao.android.intent.category.YUNDIAN" />
    public static final String WEEX_CATEGORY = "com.taobao.android.intent.category.PINE";

    public static final String wxURL = "http://cdnx.jdhone.com/weex/app/";

    public static final String resURL = "http://cdnx.jdhone.com/";

    //    云店wxkey
    public static final String wxAppId = "wx52f15c8b810a6195";
    public static final String wxAppSecret = "fa8b36c812125ccc5a642db3daddcd59";

    //    云店shareSDK
    public static final String shareAppId = "260ea801ce8a8";
    public static final String shareAppSecret = "33694f59b0d8fea28304d06bb410838f";

//    public static Handler loginHandler = null;

    //菜单路径
    public static  String index1 = "view/circle/circle.js";
    public static  String index2 = "view/mall/mall.js";
    public static  String index3 = "view/messagecenter/messagecenter.js";
    public static  String index4 = "view/mine/mine.js";
    public static  String center = "view/newarticle/newarticle.js";

    //   用户id
    public static long userId = 0;
    //   im的用户id
    public static String imUserId = "";


    public static String app = AllConstant.PACKNAME;
    public static final String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
    public static final String bucket = "jdh";
    public static final  String upLoadImages = "upload/images/";
    public static final  String upLoadVideos = "upload/videos/";

    public static String key = "";//验证header头
    public static String updateResUrl = "";
    public static String updateAppUrl = "";
    public static boolean loginState = false;
    public static boolean unLinelogin = false;
    //当前程序的资源包版本
    public static String resVerison = "1.3.5";
    //服务器资源包版本
    public static String netResVerison = "1.3.5";
    public static String appVerison = "";

//    public static Map<String, WXSDKInstance> wxsdkInstanceMap;

    //生产环境服务器地址
    public static final String SERVER = "http://120.26.160.202:8081/";
    public static final String PUBLIC_KEY =  "weex/common/public_key.jhtml";
    public static final String helperUrl = SERVER;
    public static boolean isLoginAcitivity = false;

    public static boolean isSetting = true; //设置权限

    public static final int REQ_QR_CODE = 11002; // // 打开扫描界面请求码
    public static final String INTENT_EXTRA_KEY_QR_SCAN = "qr_scan_result";

}
