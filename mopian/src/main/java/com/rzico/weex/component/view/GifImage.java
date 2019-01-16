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
package com.rzico.weex.component.view;

import android.content.Context;

import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.ui.action.BasicComponentData;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class GifImage extends WXComponent<GifImageView> {

    GifImageView gifview;

    Context context;

    public GifImage(WXSDKInstance instance, WXVContainer parent, BasicComponentData basicComponentData) {
        super(instance, parent, basicComponentData);
    }

    @Override
    protected GifImageView initComponentHostView(Context context) {

        gifview = new GifImageView(context);

        this.context = context;

        return gifview;

    }

    @WXComponentProp(name = "src")//该注解，则为weex中调用的方法名

    public void setSrc(String src) {

        try {

            GifDrawable gifFromAssets = new GifDrawable(context.getAssets(), src);

            gifview.setImageDrawable(gifFromAssets);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

}