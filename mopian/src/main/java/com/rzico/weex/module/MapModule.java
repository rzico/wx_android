package com.rzico.weex.module;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.rzico.weex.WXApplication;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.common.WXModule;

import java.net.URISyntaxException;
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
    public void startNaviGao(String lat, String lon) {
        if (isAvilible(getActivity(), "com.autonavi.minimap")) {
            try {
                //sourceApplication
                Intent intent = Intent.getIntent("androidamap://navi?sourceApplication=厦门睿商科技有限公司&poiname=我的目的地&lat=" + lat + "&lon=" + lon + "&dev=0");
                getActivity().startActivity(intent);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "您尚未安装高德地图或地图版本过低", Toast.LENGTH_SHORT).show();
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