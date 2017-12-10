package com.rzico.weex.utils.chat;

import com.rzico.weex.model.chat.CustomMessage;
import com.rzico.weex.model.chat.FileMessage;
import com.rzico.weex.model.chat.GroupTipMessage;
import com.rzico.weex.model.chat.ImageMessage;
import com.rzico.weex.model.chat.Message;
import com.rzico.weex.model.chat.TextMessage;
import com.rzico.weex.model.chat.UGCMessage;
import com.rzico.weex.model.chat.VideoMessage;
import com.rzico.weex.model.chat.VoiceMessage;
import com.tencent.imsdk.TIMMessage;

/**
 * 消息工厂
 */
public class MessageFactory {

    private MessageFactory() {}


    /**
     * 消息工厂方法
     */
    public static Message getMessage(TIMMessage message){
        switch (message.getElement(0).getType()){
            case Text:
            case Face:
                return new TextMessage(message);
            case Image:
                return new ImageMessage(message);
            case Sound:
                return new VoiceMessage(message);
            case Video:
                return new VideoMessage(message);
            case GroupTips:
                return new GroupTipMessage(message);
            case File:
                return new FileMessage(message);
            case Custom:
                return new CustomMessage(message);
            case UGC:
                return new UGCMessage(message);
            default:
                return null;
        }
    }



}
