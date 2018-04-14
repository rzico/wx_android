package com.rzico.weex.zhibo.message;

import android.content.Context;
import android.widget.RelativeLayout;

import com.rzico.weex.zhibo.activity.utils.BaseRoom;
import com.tencent.imsdk.TIMMessage;

/**
 * Created by Jinlesoft on 2018/3/31.
 */

public abstract class BaseMsgView extends RelativeLayout{
    public BaseMsgView(Context context) {
        super(context);
    }

    public abstract void setContent(BaseRoom.UserInfo userInfo);
}
