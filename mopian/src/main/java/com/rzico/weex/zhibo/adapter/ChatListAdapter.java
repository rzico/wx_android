package com.rzico.weex.zhibo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.rzico.weex.zhibo.activity.utils.BaseRoom;
import com.rzico.weex.zhibo.message.BaseMsgView;
import com.rzico.weex.zhibo.message.GifMsgView;
import com.rzico.weex.zhibo.message.NoticeMsgView;
import com.rzico.weex.zhibo.message.TextMsgView;

import java.util.ArrayList;


public class ChatListAdapter extends BaseAdapter {

    private ArrayList<BaseRoom.UserInfo> msgList;

    public ChatListAdapter() {
        msgList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return msgList.size();
    }

    @Override
    public Object getItem(int position) {
        return msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseMsgView baseMsgView = (BaseMsgView) convertView;
        BaseRoom.UserInfo msgContent = msgList.get(position);
//        if(baseMsgView == null){
            if(msgContent.cmd.equals(BaseRoom.MessageType.CustomTextMsg.name())){
                baseMsgView = new TextMsgView(parent.getContext());
            } else if(msgContent.cmd.equals(BaseRoom.MessageType.CustomGifMsg.name())){
                baseMsgView = new GifMsgView(parent.getContext());
            }else if(msgContent.cmd.equals(BaseRoom.MessageType.CustomNoticeMsg.name())){
                baseMsgView = new NoticeMsgView(parent.getContext());
            }
//        }
        baseMsgView.setContent(msgContent);

        return baseMsgView;
    }

    public void addMessage(BaseRoom.UserInfo userInfo) {
        msgList.add(userInfo);
    }
}
