package com.rzico.weex.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.rzico.weex.activity.BaseActivity;
import com.rzico.weex.net.HttpRequest;
import com.rzico.weex.net.XRequest;
import com.rzico.weex.utils.SharedUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class LocationService extends Service {

    private static final String TAG = "LocationService";

    //声明AMapLocationClient类对象
    AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("===========开始=====================");
        netThread.start();
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        mLocationOption.setOnceLocationLatest(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getPosition();
        return super.onStartCommand(intent, flags, startId);

    }

    Handler netHandler = null;

    /**
     * 收发网络数据的线程
     */
    Thread netThread = new Thread() {
        @Override
        public void run() {
            Looper.prepare();
            netHandler = new Handler() {
                public void dispatchMessage(Message msg) {
                    System.out.println("===========线程 =====================");
                    Bundle data = msg.getData();
                    switch (msg.what) {
                        case 0x1: //发送位置
//                            String macstr = getMac();
                            String longitude = data.getString("longitude");
                            String latitude = data.getString("latitude");
//                            String timestr = data.getString("timestr");
                            updatePosition(latitude,longitude);
                            break;

                    }
                }

                ;
            };
            Looper.loop();
        }
    };

    public void getPosition() {
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {

        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            System.out.println("===========回调 =====================");
            if (amapLocation == null) {
                return;
            }
            if (amapLocation.getErrorCode() != 0) {
                return;
            }

            Double longitude = amapLocation.getLongitude();//获取经度
            Double latitude = amapLocation.getLatitude();//获取纬度
            String longitudestr = String.valueOf(longitude);
            String latitudestr = String.valueOf(latitude);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(amapLocation.getTime());
            String timestr = df.format(date);
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("longitude", longitudestr);
            data.putString("latitude", latitudestr);
            data.putString("timestr", timestr);
            msg.setData(data);
            msg.what = 0x1;
            netHandler.sendMessage(msg);
        }

    };

    public void updatePosition(String lat, String lng) {
        System.out.println("更新位置");

       //如何读用户id  如何没有登录，不上报
       //调服务器上传  /lbs/localtion.jhtml?memberId=&lat=&lng=  POST
       String memberId = SharedUtils.readImId();
       if (memberId!=null && !"".equals(memberId)) {
           //登出
           new XRequest(null, "/lbs/location.jhtml?memberId="+memberId+"&lat="+lat+"&lng="+lng, XRequest.POST, new HashMap<String, Object>()).setOnRequestListener(new HttpRequest.OnRequestListener() {
               @Override
               public void onSuccess(BaseActivity activity, String result, String type) {
                   System.out.println("更新位置成功");
               }
               @Override
               public void onFail(BaseActivity activity, String cacheData, int code) {
              }
           }).execute();
       }

    }


}