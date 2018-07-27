package com.rzico.weex.push;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

import com.huawei.android.pushagent.api.PushEventReceiver;
import com.rzico.weex.WXApplication;

/**
 * Created by Administrator on 2018/7/20.
 */

public class HwPushMessageReceiver extends PushEventReceiver {
    private final String TAG = "HwPushMessageReceiver";
    private String mToken = "";

    @Override
    public void onToken(Context context, String token, Bundle extras){
        String belongId = extras.getString("belongId");
//        String content = "获取token和belongId成功，token = " + token + ",belongId = " + belongId;
//
        WXApplication.setToken(token);
//
//        Log.e(TAG, content);
    }

    @Override
    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
        try {
            String content = "收到一条Push消息： " + new String(msg, "UTF-8");
            System.out.println(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onEvent(Context context, Event event, Bundle extras) {
        if (Event.NOTIFICATION_OPENED.equals(event) || Event.NOTIFICATION_CLICK_BTN.equals(event)) {
            int notifyId = extras.getInt(BOUND_KEY.pushNotifyId, 0);
            if (0 != notifyId) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notifyId);
            }
            String content = "收到通知附加消息： " + extras.getString(BOUND_KEY.pushMsgKey);
            System.out.println(content);
        }
    }
}