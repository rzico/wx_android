package com.rzico.weex.module;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.rzico.weex.WXApplication;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.common.WXModule;

import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jinlesoft on 2018/7/13.
 */

public class MapModule extends WXModule {

    public com.rzico.weex.activity.BaseActivity getActivity() {
        if(mWXSDKInstance == null){
            return WXApplication.getActivity();
        }else{
            return (com.rzico.weex.activity.BaseActivity) mWXSDKInstance.getContext();
        }
    }

    @JSMethod
    public void startNaviGao(String options) {

        String slat = "";
        String slon = "";
        String dlat = "";
        String dlon = "";
        int type = 2;// 1 公交 2 驾车 4步行
        if(options==null || options.equals("")) return;
        try {
            options = URLDecoder.decode(options, "utf-8");
            com.alibaba.fastjson.JSONObject jsObj = JSON.parseObject(options);
            if (jsObj.containsKey("type")) {
                type = jsObj.getInteger("type");
            }
            if (jsObj.containsKey("slat")) {
                slat = jsObj.getString("slat");
            }
            if (jsObj.containsKey("slon")) {
                slon = jsObj.getString("slon");
            }
            if (jsObj.containsKey("dlat")) {
                dlat = jsObj.getString("dlat");
            }
            if (jsObj.containsKey("dlon")) {
                dlon = jsObj.getString("dlon");
            }
            if (isAvilible(getActivity(), "com.autonavi.minimap")) {

                String locationStr = "androidamap://route?sourceApplication="
                        + "rzico"
                        +
                        "&slat="
                        + slat
                        + "&slon="
                        + slon
                        +
                        "&sname="
                        + "起点"
                        + "&dlat="
                        + dlat
                        + "&dlon="
                        + dlon
                        + "&dname="
                        + "终点"
                        + "&dev=0"
                        + "&m=0"
                        + "&t="
                        + type
                        + "&showType=1";
                Intent intent = new Intent("android.intent.action.VIEW",
                        android.net.Uri.parse(/*stringBuffer.toString()*/locationStr));
                intent.setPackage("com.autonavi.minimap");
                getActivity().startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "您尚未安装高德地图或地图版本过低", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(getActivity(), "传入参数异常", Toast.LENGTH_SHORT).show();
        }


    }

    //验证各种导航地图是否安装
    public static boolean isAvilible(Context context, String packageName) {
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }
}