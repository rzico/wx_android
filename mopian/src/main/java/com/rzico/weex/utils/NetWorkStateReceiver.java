package com.rzico.weex.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.rzico.weex.activity.BaseActivity;

/**
 * Created by Jinlesoft on 2017/12/2.
 */

public class NetWorkStateReceiver extends BroadcastReceiver {


    public NetEvevt evevt = BaseActivity.evevt;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        // 如果相等的话就说明网络状态发生了变化
        if (evevt!=null && context!= null && intent!=null && intent.getAction()!=null && intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            int netWorkState = NetUtil.getNetWorkState(context);
            // 接口回调传过去状态的类型
            evevt.onNetChange(netWorkState);
        }
        if(context!=null){
//            Toast.makeText(context, "evevt:" + (evevt == null ? "存在" : "不存在"), 0).show();    时时刻刻提醒自己 全局变量在对象被销毁时会恢复默认值 使用是需考虑到 慎用！
        }
    }

    // 自定义接口
    public interface NetEvevt {
        public void onNetChange(int netMobile);
    }
}
