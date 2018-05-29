package com.rzico.weex.module;

import com.rzico.weex.model.info.Message;
import com.rzico.weex.utils.Player;
import com.rzico.weex.utils.RecorderUtil;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

/**
 * Created by Jinlesoft on 2017/11/18.
 */

public class AudioModule extends WXModule {


    @JSMethod
    public void play(String url){
        Player.getInstance().stop();
        Player.getInstance().playUrl(url);
        Player.getInstance().play();
    }

    @JSMethod
    public void stop(){
        Player.getInstance().stop();
    }


    @JSMethod
    public void startRecording(JSCallback callback){
        RecorderUtil.getInstance().startRecording(callback);
    }

    @JSMethod
    public void stopRecording(JSCallback callback){
        RecorderUtil.getInstance().stopRecording(callback);
        Record record = new Record();
        record.setPath(RecorderUtil.getInstance().getFilePath());
        record.setTime(RecorderUtil.getInstance().getTimeInterval());
        Message message = new Message();
        message.setType("success");
        message.setContent("录音成功");
        message.setData(record);
        callback.invoke(message);
    }
    class Record{
        private String path;//录音保存的路径
        private long time;//录音时长

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }
    }

}
