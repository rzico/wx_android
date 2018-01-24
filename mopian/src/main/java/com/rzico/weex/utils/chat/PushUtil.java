package com.rzico.weex.utils.chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.rzico.weex.Constant;
import com.rzico.weex.R;
import com.rzico.weex.WXApplication;
import com.rzico.weex.activity.AbsWeexActivity;
import com.rzico.weex.activity.MainActivity;
import com.rzico.weex.activity.chat.ChatActivity;
import com.rzico.weex.model.chat.CustomMessage;
import com.rzico.weex.model.chat.Message;
import com.rzico.weex.model.info.IMMessage;
import com.taobao.weex.WXSDKInstance;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMGroupReceiveMessageOpt;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.message.TIMConversationExt;
import com.tencent.qcloud.presentation.event.MessageEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;


/**
 * 在线消息通知展示
 */
public class PushUtil implements Observer {

    private static final String TAG = PushUtil.class.getSimpleName();

    private static int pushNum = 0;

    private final int pushId = 1;

    private static PushUtil instance = new PushUtil();

    private PushUtil() {
        MessageEvent.getInstance().addObserver(this);
    }

    public static PushUtil getInstance() {
        return instance;
    }


    private void PushNotify(TIMMessage msg, List<TIMUserProfile> result) {
        //系统消息，自己发的消息，程序在前台的时候不通知

        if (msg == null ||
//                Foreground.get().isForeground()||
                (msg.getConversation().getType() != TIMConversationType.Group &&
                        msg.getConversation().getType() != TIMConversationType.C2C) ||
                msg.isSelf() ||
                msg.getRecvFlag() == TIMGroupReceiveMessageOpt.ReceiveNotNotify ||
                MessageFactory.getMessage(msg) instanceof CustomMessage) return;

        String senderStr, contentStr;
        Message message = MessageFactory.getMessage(msg);
        TIMUserProfile userProfile = null;
        if (result != null && result.size() > 0) {
            userProfile = result.get(0);
        }
        if (message == null) return;
        senderStr = message.getSender();
        contentStr = message.getSummary();


        //获取会话扩展实例
        TIMConversation con = TIMManager.getInstance().getConversation(TIMConversationType.C2C, senderStr);
        TIMConversationExt conExt = new TIMConversationExt(con);
        if (conExt.getUnreadMessageNum() >= 1) {

            if (userProfile != null) {
                if (!userProfile.getNickName().equals("")) {
                    contentStr = "[" + conExt.getUnreadMessageNum() + "条]" + userProfile.getNickName() + ": " + contentStr;
                } else {
                    contentStr = "[" + conExt.getUnreadMessageNum() + "条]" + senderStr + ": " + contentStr;
                }
            }
        } else {
            //没有未读数 不推送
            return;
        }

        Log.d(TAG, "recv msg " + contentStr);
        NotificationManager mNotificationManager = (NotificationManager) WXApplication.getContext().getSystemService(WXApplication.getContext().NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(WXApplication.getContext());
        Intent notificationIntent = new Intent(WXApplication.getContext(), MainActivity.class);

        notificationIntent.putExtra("identify", senderStr);
        notificationIntent.putExtra("type", TIMConversationType.C2C);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
//                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(WXApplication.getContext(), 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (userProfile != null) {
            if (!userProfile.getNickName().equals("")) {
                mBuilder.setContentTitle(userProfile.getNickName());//设置通知栏标题
                mBuilder.setTicker(userProfile.getNickName() + ":" + contentStr); //通知首次出现在通知栏，带上升动画效果的
            } else {
                mBuilder.setContentTitle(userProfile.getIdentifier());//设置通知栏标题
                mBuilder.setTicker(userProfile.getIdentifier() + ":" + contentStr); //通知首次出现在通知栏，带上升动画效果的
            }
        }
        mBuilder.setContentText(contentStr)
                .setContentIntent(intent) //设置通知栏点击意图
//                .setNumber(++pushNum) //设置通知集合的数量
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                .setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON
        Notification notify = mBuilder.build();
        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(pushId, notify);
    }

    public static void resetPushNum() {
        pushNum = 0;
    }

    public void reset() {
        NotificationManager notificationManager = (NotificationManager) WXApplication.getContext().getSystemService(WXApplication.getContext().NOTIFICATION_SERVICE);
        notificationManager.cancel(pushId);
    }

    /**
     * This method is called if the specified {@code Observable} object's
     * {@code notifyObservers} method is called (because the {@code Observable}
     * object has been updated.
     *
     * @param observable the {@link Observable} object.
     * @param data       the data passed to {@link Observable#notifyObservers(Object)}.
     */
    @Override
    public void update(Observable observable, Object data) {
        WXApplication wxApplication = (WXApplication) WXApplication.getContext();
        if(wxApplication == null) return;
        if (observable instanceof MessageEvent) {
            if (data instanceof TIMMessage) {
                final TIMMessage msg = (TIMMessage) data;
                List<String> users = new ArrayList<>();
                final com.rzico.weex.model.chat.Message message = MessageFactory.getMessage(msg);
                if (message != null && message.getSender() != null && !message.getSender().equals("")) {
                    users.add(message.getSender());
                    //推送主页更新消息
                    if ( wxApplication.getLoginHandler()!= null) {
                        wxApplication.getLoginHandler().sendEmptyMessage(MainActivity.RECEIVEMSG);
                    }
                }
                //获取好友资料
                TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>() {
                    @Override
                    public void onError(int code, String desc) {
                        Toast.makeText(WXApplication.getContext(), desc, Toast.LENGTH_SHORT).show();
                        if (!msg.isSelf()) {
                            handleToWeex(message, null);
                        }
                        PushNotify(msg, null);
                    }

                    @Override
                    public void onSuccess(List<TIMUserProfile> result) {
                        if (!msg.isSelf()) {
//                                Toast.makeText(WXApplication.getContext(), "comon", Toast.LENGTH_SHORT).show();
                            handleToWeex(message, result);
                        }
//                            Toast.makeText(WXApplication.getContext(), "qingqiule", Toast.LENGTH_SHORT).show();
                        PushNotify(msg, result);

                    }
                });
            }
        }
    }

    private void handleToWeex(com.rzico.weex.model.chat.Message message, List<TIMUserProfile> result) {
//        Toast.makeText(WXApplication.getContext(), "0", Toast.LENGTH_SHORT).show();
        WXApplication wxApplication = (WXApplication) WXApplication.getContext();
        if(wxApplication == null) return;
        if (!(message instanceof CustomMessage)) {//如果不是自定义消息则提示
//            Toast.makeText(WXApplication.getContext(), "1", Toast.LENGTH_SHORT).show();
            if (message == null) return;
            TIMConversation con = TIMManager.getInstance().getConversation(TIMConversationType.C2C, message.getSender());
            final TIMConversationExt conExt = new TIMConversationExt(con);
            System.out.println("unRead:" + conExt.getUnreadMessageNum());

//            Toast.makeText(WXApplication.getContext(), "2", Toast.LENGTH_SHORT).show();
            if (result != null && result.size() > 0) {
//                Toast.makeText(WXApplication.getContext(), "3", Toast.LENGTH_SHORT).show();
                TIMUserProfile user = result.get(0);
                com.rzico.weex.model.info.Message onMessage = new com.rzico.weex.model.info.Message();
                onMessage.setType("success");
//                MainActivity.getUnRead();
                onMessage.setContent("您有一条新消息");
                IMMessage imMessage = new IMMessage();
//
                if (conExt.getUnreadMessageNum() >= 1) {
                    imMessage.setUnRead(conExt.getUnreadMessageNum());
                } else {
                    imMessage.setUnRead(0);
                }
                imMessage.setContent(message.getSummary());
                imMessage.setId(message.getSender());
                imMessage.setLogo(user.getFaceUrl());
                imMessage.setNickName(user.getNickName());
                imMessage.setCreateDate(message.getMessage().timestamp());
                onMessage.setData(imMessage);
                Map<String, Object> params = new HashMap<>();
                params.put("data", onMessage);
//                Toast.makeText(WXApplication.getContext(), "4", Toast.LENGTH_SHORT).show();
                if (wxApplication.getWxsdkInstanceMap() != null) {
//                    Toast.makeText(WXApplication.getContext(), "5", Toast.LENGTH_SHORT).show();
                    for (String key : wxApplication.getWxsdkInstanceMap().keySet()) {
                        wxApplication.getWxsdkInstanceMap().get(key).fireGlobalEventCallback("onMessage", params);
                    }
                }
//                Toast.makeText(WXApplication.getContext(), "6", Toast.LENGTH_SHORT).show();
                //判断当前页面是不是weex页面
//                Toast.makeText(WXApplication.getContext(), "WXApplcation.getActivity()==" + WXApplication.getActivity() == null ? "null" : "nonull", Toast.LENGTH_SHORT).show();
                if (WXApplication.getActivity() instanceof AbsWeexActivity) {
                    ((AbsWeexActivity) WXApplication.getActivity()).getWXSDKInstance().fireGlobalEventCallback("onMessage", params);
                }
            } else {
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code列表请参见错误码表
                com.rzico.weex.model.info.Message onMessage = new com.rzico.weex.model.info.Message();
                onMessage.setType("success");
                onMessage.setContent("您有一条新消息");
                IMMessage imMessage = new IMMessage();
                imMessage.setContent(message.getSummary());
                imMessage.setId(message.getSender());
                imMessage.setLogo("");
                imMessage.setNickName("");
                imMessage.setUnRead(0);
                imMessage.setCreateDate(message.getMessage().timestamp());
                onMessage.setData(imMessage);
                Map<String, Object> params = new HashMap<>();
                params.put("data", onMessage);
                if (wxApplication.getWxsdkInstanceMap() != null) {
                    for (String key : wxApplication.getWxsdkInstanceMap().keySet()) {
                        wxApplication.getWxsdkInstanceMap().get(key).fireGlobalEventCallback("onMessage", params);
                    }
                }

                //判断当前页面是不是weex页面
                if (WXApplication.getActivity() instanceof AbsWeexActivity) {
                    ((AbsWeexActivity) WXApplication.getActivity()).getWXSDKInstance().fireGlobalEventCallback("onMessage", params);
                }
            }
        }
    }
}
