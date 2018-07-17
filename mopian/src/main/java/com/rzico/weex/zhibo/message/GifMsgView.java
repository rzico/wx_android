package com.rzico.weex.zhibo.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.rzico.weex.R;
import com.rzico.weex.zhibo.activity.utils.BaseRoom;

/**
 * Created by Jinlesoft on 2018/4/8.
 */

public class GifMsgView extends BaseMsgView{

    private TextView username;
    private TextView msgText;
        private TextView vip_tv;
    public GifMsgView(Context context) {
        super(context);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.msg_text_view, this);
        username = (TextView) view.findViewById(R.id.username);
        msgText = (TextView) view.findViewById(R.id.msg_text);
        vip_tv= (TextView) view.findViewById(R.id.vip);
    }

    @Override
    public void setContent(BaseRoom.UserInfo userInfo) {
        username.setText(userInfo.nickName);
        msgText.setText(userInfo.text);
        if(userInfo.vip == null || userInfo.vip.equals("")){
            vip_tv.setVisibility(GONE);
        }else {
            vip_tv.setText(userInfo.vip);
        }
    }

}
