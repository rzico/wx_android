package com.rzico.weex;

import com.rzico.weex.constant.AllConstant;

/**
 * Created by shiweiwei on 2015/11/2.
 * http://helper.tiaohuo.com
 * http://www.ruishangquan.com/
 * http://192.168.0.63:8080/tiaohuo/
 */
public class Constant {

    //这里的WEEX_CATEGORY 需要与配置文件中的意图配置统一： <category android:name="com.taobao.android.intent.category.MOPIAN" />
    public static final String WEEX_CATEGORY = "com.taobao.android.intent.category.MOPIAN";

    public static final String wxURL = "http://cdnx.1xx.me/weex/app/";

//    public static final String resURL = "http://cdnx.1xx.me/";
    public static final String resURL = "http://cdnx.rzico.com/";

    //    云店wxkey
//    public static final String wxAppId = "wx490857e2baff7cfd";
//    public static final String wxAppSecret = "46acecbfa148ca89d443d38b0ce7c865";
    //    魔篇wxkey
    public static final String wxAppId = "wxe9044e4a3a478046";
    public static final String wxAppSecret = "e1a6bffb5ad1eb7ffa2f442032df2d78";

    //    云店shareSDK
//    public static final String shareAppId = "1d927fd47d636";
//    public static final String shareAppSecret = "277f7181f07499357cfa33531fe818ab";
    //    魔篇shareSDK
    public static final String shareAppId = "22a31844e236a";
    public static final String shareAppSecret = "1b8e2c508eac21106cc6cc2025acda29";

//    public static Handler loginHandler = null;

    //菜单路径
    public static  String index1 = "view/home/index.js";
    public static  String index2 = "view/friend/list.js";
    public static  String index3 = "view/message/list.js";
    public static  String index4 = "view/member/index.js";
    public static  String center = "view/member/editor/editor.js";

    //   用户id
    public static long userId = 0;
    //   im的用户id
    public static String imUserId = "";


    public static String app = AllConstant.PACKNAME;
//  public static final String endpoint = "http://oss-cn-shenzhen.aliyuncs.com";
//  public static final String bucket = "rzico";
    public static final String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
    public static final String bucket = "rzico-weex";
//    public static final String bucket = "mopian";
    public static final  String upLoadImages = "upload/images/";
    public static final  String upLoadVideos = "upload/videos/";

    public static String key = "";//验证header头
    public static String updateResUrl = "";
    public static String updateAppUrl = "";
    public static boolean loginState = false;
    public static boolean unLinelogin = false;

    //当前程序的资源包版本
    public static String resVerison = "0.0.0";
    //服务器资源包版本
    public static String netResVerison = "1.0.0";
    public static String appVerison = "";

//    public static Map<String, WXSDKInstance> wxsdkInstanceMap;

    //生产环境服务器地址
//    public static final String SERVER = "http://weex.rzico.com:8088/";
//    public static final String SERVER = "http://mopian.1xx.me/";
    public static final String SERVER = "http://dev.rzico.com/";
//    public static final String SERVER = "http://dev.rzico.com/nihtan/";
//    public static final String SERVER = "http://192.168.2.110:8088/";
    public static final String PUBLIC_KEY =  "weex/common/public_key.jhtml";
    public static final String helperUrl = SERVER;
    public static boolean isLoginAcitivity = false;

    public static boolean isSetting = true; //设置权限

    public static final int REQ_QR_CODE = 11002; // // 打开扫描界面请求码
    public static final String INTENT_EXTRA_KEY_QR_SCAN = "qr_scan_result";


//    public static String pushUrl = "rtmp://10714.livepush.myqcloud.com/live/10714_jinle?bizid=10714&txSecret=c1edb88add2b8cd00a758699ef71527a&txTime=5AE73D7F";

//    public static String playUrl_rtmp = "rtmp://10714.liveplay.myqcloud.com/live/10714_jinle";
//    public static String playUrl_flv = "http://10714.liveplay.myqcloud.com/live/10714_7b3ccbf5f9.flv";
//    public static String playUrl_hls = "http://10714.liveplay.myqcloud.com/live/10714_7b3ccbf5f9.m3u8";

//    public static String groupId = "10000001";

}
