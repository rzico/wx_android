package com.rzico.weex.zhibo.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.rzico.weex.R;
import com.rzico.weex.model.chat.TextMessage;
import com.rzico.weex.zhibo.activity.utils.BaseRoom;
import com.rzico.weex.zhibo.view.utils.EmojiManager;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMTextElem;

public class TextMsgView extends BaseMsgView {

    private TextView username;
    private TextView msgText;
    private TextView vip_tv;
    public TextMsgView(Context context) {
        super(context);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.msg_text_view, this);
        username = (TextView) view.findViewById(R.id.username);
        msgText = (TextView) view.findViewById(R.id.msg_text);
        vip_tv= (TextView) view.findViewById(R.id.vip);
    }

    @Override
    public void setContent(BaseRoom.UserInfo userInfo) {
        username.setText(userInfo.nickName);
        msgText.setText(EmojiManager.parse(userInfo.text, msgText.getTextSize()));
        if(userInfo.vip == null || userInfo.vip.equals("")){
            vip_tv.setVisibility(GONE);
        }else {
            vip_tv.setText(userInfo.vip);
        }
    }

}
