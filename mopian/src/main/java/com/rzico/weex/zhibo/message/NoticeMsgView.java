package com.rzico.weex.zhibo.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.rzico.weex.R;
import com.rzico.weex.zhibo.activity.utils.BaseRoom;

/**
 * Created by Jinlesoft on 2018/4/18.
 */

public class NoticeMsgView extends  BaseMsgView {


    private TextView msgText;
    public NoticeMsgView(Context context) {
        super(context);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.msg_notice_view, this);
        msgText = (TextView) view.findViewById(R.id.msg_text);
    }

    @Override
    public void setContent(BaseRoom.UserInfo userInfo) {
        msgText.setText(userInfo.text);
    }
}
