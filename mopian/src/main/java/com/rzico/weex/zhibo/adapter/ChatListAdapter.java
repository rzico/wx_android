package com.rzico.weex.zhibo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rzico.weex.R;
import com.rzico.weex.zhibo.activity.utils.BaseRoom;
import com.rzico.weex.zhibo.message.BaseMsgView;
import com.rzico.weex.zhibo.message.GifMsgView;
import com.rzico.weex.zhibo.message.NoticeMsgView;
import com.rzico.weex.zhibo.message.TextMsgView;
import com.rzico.weex.zhibo.view.utils.EmojiManager;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class ChatListAdapter extends BaseAdapter {

    private ArrayList<BaseRoom.UserInfo> msgList;
    private Context context;

    public ChatListAdapter(Context context) {
        msgList = new ArrayList<>();
        this.context = context;
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

        //new
        BaseRoom.UserInfo msgContent = msgList.get(position);
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.msg_text_view, null);
            viewHolder.username = (TextView) convertView.findViewById(R.id.username);
            viewHolder.msgText = (TextView) convertView.findViewById(R.id.msg_text);
            viewHolder.vip_tv= (TextView) convertView.findViewById(R.id.vip);
            convertView.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(msgContent.cmd.equals(BaseRoom.MessageType.CustomGifMsg.name())){

            viewHolder.username.setVisibility(VISIBLE);
            viewHolder.vip_tv.setVisibility(VISIBLE);
            viewHolder.username.setTextColor(context.getResources().getColor(R.color.gold));
            viewHolder.msgText.setTextColor(context.getResources().getColor(R.color.gold));

            viewHolder.username.setText(msgContent.nickName);
            viewHolder.msgText.setText(msgContent.text);
            if(msgContent.vip == null || msgContent.vip.equals("")){
                viewHolder.vip_tv.setVisibility(GONE);
            }else {
                viewHolder.vip_tv.setText(msgContent.vip);
            }
        }else if(msgContent.cmd.equals(BaseRoom.MessageType.CustomNoticeMsg.name())){
            viewHolder.msgText.setTextColor(context.getResources().getColor(R.color.gold));
            viewHolder.msgText.setText(msgContent.text);
            viewHolder.username.setText("");
            viewHolder.username.setVisibility(GONE);
            viewHolder.vip_tv.setVisibility(GONE);
            viewHolder.vip_tv.setText("");
        }else {
            viewHolder.username.setVisibility(VISIBLE);
            viewHolder.vip_tv.setVisibility(VISIBLE);
            viewHolder.username.setTextColor(context.getResources().getColor(R.color.liveusername));
            viewHolder.msgText.setTextColor(context.getResources().getColor(R.color.white));

            viewHolder.username.setText(msgContent.nickName);
            viewHolder.msgText.setText(EmojiManager.parse(msgContent.text, viewHolder.msgText.getTextSize()));
            if(msgContent.vip == null || msgContent.vip.equals("")){
                viewHolder.vip_tv.setVisibility(GONE);
            }else {
                viewHolder.vip_tv.setText(msgContent.vip);
            }

        }


        return convertView;
    }

    public void addMessage(BaseRoom.UserInfo userInfo) {
        msgList.add(userInfo);
    }

    class ViewHolder {
        private TextView username;
        private TextView msgText;
        private TextView vip_tv;

    }
}
