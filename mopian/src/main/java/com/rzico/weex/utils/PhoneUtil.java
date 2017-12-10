package com.rzico.weex.utils;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import com.rzico.weex.Constant;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/5/4 0004.
 */

public class PhoneUtil {
    /**
     * 判断手机格式是否正确
     * @param mobiles
     * @return
     * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
     * 联通：130、131、132、152、155、156、185、186
     * 电信：133、153、180、189、（1349卫通）
     * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
     */
//    public static boolean isMobileNO(String mobiles) {
//        //"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
//        String telRegex = "[1][34578]\\d{9}" ;
//        if (TextUtils.isEmpty(mobiles))
//            return false ;
//        else return mobiles.matches( telRegex ) ;
//    }

    public static boolean isMobileNO(String mobiles) {
        if (TextUtils.isEmpty(mobiles)) return false;
        else {
            Pattern p = Pattern.compile("^(0|86|17951)?(13[0-9]|15[012356789]|17[0-9]|18[0-9]|14[57])[0-9]{8}$");
            Matcher m = p.matcher(mobiles);
            return m.matches();
        }
    }


    /**
     * 获取设备id
     *
     * @return
     */

    public static String getDeviceId(Context context) {
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure
                .ANDROID_ID);
        return android_id;
    }
/**
*
*@aurhor 尹平
*create at 2017/7/1 0001 14:08
*/
    public static String getxkey(Context context){
        String uid=getDeviceId(context);
        String packetName= Constant.app;
        String key="myjsy2014$$";
        String xkey = MD5.Md5(uid+packetName+key);
        return xkey;
    }
}
