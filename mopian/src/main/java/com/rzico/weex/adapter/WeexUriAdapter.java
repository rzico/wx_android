package com.rzico.weex.adapter;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.rzico.weex.utils.PathUtils;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.adapter.URIAdapter;

/**
 * Created by Jinlesoft on 2017/9/25.
 */

public class WeexUriAdapter implements URIAdapter {
    @NonNull
    @Override
    public Uri rewrite(WXSDKInstance instance, String type, Uri uri) {
        //file://resources/fonts/iconfont.ttf
        String path = uri.toString();
        if(path.startsWith("file://") && path.endsWith(".ttf")){//这里处理了字体路径
            path = "file://" + path.replace( "file://", PathUtils.getResPath());
            uri = Uri.parse(path);
        }

        return uri;
    }
}
