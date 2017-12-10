package com.rzico.weex.module;

import com.rzico.weex.utils.Player;
import com.taobao.weex.annotation.JSMethod;
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
}
