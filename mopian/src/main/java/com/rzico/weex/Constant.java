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

    //这里的WEEX_CATEGORY 需要与配置文件中的意图配置统一： <category android:name="com.taobao.android.intent.category.FARMER" />
    public static final String WEEX_CATEGORY = "com.taobao.android.intent.category.FARMER";

    public static final String wxURL = "http://cdnx.ahxinying.cn/farmer/app/";

    public static final String resURL = "http://cdnx.ahxinying.cn/";

    //    云店wxkey
    public static final String wxAppId = "wx6cc49384f155ec65";
    public static final String wxAppSecret = "70dc6d0d3a4fbf6a080bbdf3a8e46172";

    //    云店shareSDK
    public static final String shareAppId = "28dfe7bd78cdc";
    public static final String shareAppSecret = "33694f59b0d8fea28304d06bb410838f";

    //    推送
    public static final String mipushAppId = "2882303761517895577";
    public static final String mipushAppKey = "5941789561577";
    public static final long mipushbussid = 4609;
    public static final String huaweiAppId = "100502985";
    public static final String huaweiAppSecret = "879fd05362da8b14ba167c855326fed3";
    public static final long huaweibussid = 4610;
    public static final String mzpushAppId = "3231035";
    public static final String mzpushAppKey = "5d1ec357a6764f768486d7caaaa176dd";
    public static final long mzpushbussid = 4611;

    public static final String appId = "";

//    public static Handler loginHandler = null;

    //菜单路径
    public static  String index1 = "view/home/home";
    public static  String index2 = "view/messagecenter/messagecenter";
    public static  String index3 = "view/myMessage/myMessage";
    public static  String index4 = "view/mine/mine.js";
    public static  String center = "view/newarticle/newarticle.js";


    public static boolean isShowBottom = true;
    //   用户id
    public static long userId = 0;
    //   im的用户id
    public static String imUserId = "";


    public static String app = AllConstant.PACKNAME;
    public static final String endpoint = "http://oss-cn-shanghai.aliyuncs.com";
    public static final String bucket = "ahxinying";
    public static final  String upLoadImages = "upload/images/";
    public static final  String upLoadVideos = "upload/videos/";

    public static String key = "";//验证header头
    public static String updateResUrl = "";
    public static String updateAppUrl = "";
    public static boolean loginState = false;
    public static boolean unLinelogin = false;
    //当前程序的资源包版本
    public static String resVerison = "1.5.3";
    //服务器资源包版本
    public static String netResVerison = "1.5.3";
    public static String appVerison = "";

//    public static Map<String, WXSDKInstance> wxsdkInstanceMap;

    //生产环境服务器地址
    public static final String SERVER = "https://www.jdhone.com/";
    //public static final String SERVER = "https://www.ahxinying.cn/";
    public static final String SCENE_NAME = "farmer";
    public static final String PUBLIC_KEY =  "farmer/common/public_key.jhtml";
    public static final String helperUrl = SERVER;
    public static boolean isLoginAcitivity = false;

    public static boolean isSetting = true; //设置权限

    public static final int REQ_QR_CODE = 11002; // // 打开扫描界面请求码
    public static final String INTENT_EXTRA_KEY_QR_SCAN = "qr_scan_result";

}
