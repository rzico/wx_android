/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.rzico.weex.adapter;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.rzico.weex.WXApplication;
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
        if(path.startsWith("file://") && !path.startsWith("file:///android_asset") && path.endsWith(".ttf")){//这里处理了字体路径
            path = "file://" + path.replace( "file://", PathUtils.getResPath(WXApplication.getActivity()));
            uri = Uri.parse(path);
        }

        return uri;
    }

    @NonNull
    @Override
    public Uri rewrite(String bundleURL, String type, Uri uri) {
        String path = uri.toString();
        if(path.startsWith("file://") && !path.startsWith("file:///android_asset") && path.endsWith(".ttf")){//这里处理了字体路径
            path = "file://" + path.replace( "file://", PathUtils.getResPath(WXApplication.getActivity()));
            uri = Uri.parse(path);
        }

        return uri;
    }
}
