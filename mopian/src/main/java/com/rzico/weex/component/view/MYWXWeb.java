package com.rzico.weex.component.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.rzico.weex.Constant;
import com.rzico.weex.model.info.Message;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.adapter.URIAdapter;
import com.taobao.weex.annotation.Component;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.Constants;
import com.taobao.weex.dom.WXDomObject;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;
import com.taobao.weex.ui.view.IWebView;
import com.taobao.weex.utils.WXUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(lazyload = false)
/**
 * Created by Jinlesoft on 2017/10/11.
 */

public class MYWXWeb extends WXComponent {

    public static final String GO_BACK = "goBack";
    public static final String GO_FORWARD = "goForward";
    public static final String RELOAD = "reload";
    private Map extraHeaders = new HashMap();
    protected IWebView mWebView;

    @Deprecated
    public MYWXWeb(WXSDKInstance instance, WXDomObject dom, WXVContainer parent, String instanceId, boolean isLazy) {
        this(instance,dom,parent,isLazy);
    }

    public MYWXWeb(WXSDKInstance instance, WXDomObject dom, WXVContainer parent, boolean isLazy) {
        super(instance, dom, parent, isLazy);
        createWebView();

        extraHeaders.put("Referer", Constant.SERVER);
    }

    protected void  createWebView(){
        mWebView = new MYWXWebView(getContext());
    }

    @Override
    protected View initComponentHostView(@NonNull Context context) {
        mWebView.setOnErrorListener(new IWebView.OnErrorListener() {
            @Override
            public void onError(String type, Object message) {
                fireEvent(type, message);
            }
        });
        mWebView.setOnPageListener(new IWebView.OnPageListener() {
            @Override
            public void onReceivedTitle(String title) {
                if (getDomObject().getEvents().contains(Constants.Event.RECEIVEDTITLE)) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("title", title);
                    fireEvent(Constants.Event.RECEIVEDTITLE, params);
                }
            }

            @Override
            public void onPageStart(String url) {
                if ( getDomObject().getEvents().contains(Constants.Event.PAGESTART)) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("url", url);
                    fireEvent(Constants.Event.PAGESTART, params);
                }
            }

            @Override
            public void onPageFinish(String url, boolean canGoBack, boolean canGoForward) {
                if ( getDomObject().getEvents().contains(Constants.Event.PAGEFINISH)) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("url", url);
                    params.put("canGoBack", canGoBack);
                    params.put("canGoForward", canGoForward);
                    fireEvent(Constants.Event.PAGEFINISH, params);
                }
            }
        });
        return mWebView.getView();
    }

    @Override
    public void destroy() {
        super.destroy();
        getWebView().destroy();
    }

    @Override
    protected boolean setProperty(String key, Object param) {
        switch (key) {
            case Constants.Name.SHOW_LOADING:
                Boolean result = WXUtils.getBoolean(param,null);
                if (result != null)
                    setShowLoading(result);
                return true;
            case Constants.Name.SRC:
                String src = WXUtils.getString(param,null);
                if (src != null)
                    setUrl(src);
                return true;
        }
        return super.setProperty(key,param);
    }

    @WXComponentProp(name = Constants.Name.SHOW_LOADING)
    public void setShowLoading(boolean showLoading) {
        getWebView().setShowLoading(showLoading);
    }

    @WXComponentProp(name = Constants.Name.SRC)
    public void setUrl(String url) {
        if (TextUtils.isEmpty(url) || getHostView() == null) {
            return;
        }
        if (!TextUtils.isEmpty(url)) {
            loadUrl(getInstance().rewriteUri(Uri.parse(url), URIAdapter.WEB).toString());
        }
    }

    public void setAction(String action) {
        if (!TextUtils.isEmpty(action)) {
            if (action.equals(GO_BACK)) {
                goBack();
            } else if (action.equals(GO_FORWARD)) {
                goForward();
            } else if (action.equals(RELOAD)) {
                reload();
            }
        }
    }

    private void fireEvent(String type, Object message) {
        if (getDomObject().getEvents().contains(Constants.Event.ERROR)) {
            Map<String, Object> params = new HashMap<>();
            params.put("type", type);
            params.put("errorMsg", message);
            fireEvent(Constants.Event.ERROR, params);
        }
    }

    private void loadUrl(String url) {
        getWebView().loadUrl(url);
    }

    private void reload() {
        getWebView().reload();
    }


    public void getLongImage(final JSCallback callback){
        // WebView 生成长图，也就是超过一屏的图片，代码中的 longImage 就是最后生成的长图
        getWebView().getWebView().measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        getWebView().getWebView().layout(0, 0, getWebView().getWebView().getMeasuredWidth(), getWebView().getWebView().getMeasuredHeight());
        getWebView().getWebView().setDrawingCacheEnabled(true);
        getWebView().getWebView().buildDrawingCache();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            getWebView().getWebView().enableSlowWholeDocumentDraw();
        }
        getWebView().getWebView().postDelayed(new Runnable() {
            @Override
            public void run() {
                int width = getWebView().getWebView().getMeasuredWidth();
                int height= getWebView().getWebView().getMeasuredHeight();
                Bitmap longImage = Bitmap.createBitmap(width,
                        height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(longImage);  // 画布的宽高和 WebView 的网页保持一致
                Paint paint = new Paint();
                canvas.drawBitmap(longImage, 0, height, paint);
                getWebView().getWebView().draw(canvas);

                saveBitmap(longImage, callback);
                getWebView().getWebView().setDrawingCacheEnabled(false);
                getWebView().getWebView().destroyDrawingCache();
                getWebView().reload();
            }
        },300);
    }

    private void goForward() {
        getWebView().goForward();
    }

    private void goBack() {
        getWebView().goBack();
    }

    public IWebView getWebView() {
        return mWebView;
    }

    public void  saveBitmap(Bitmap bm, JSCallback callback) {
        Message message = new Message();
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "MOPIAN");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis()+".png";
//        fileName = fileName.substring(fileName.length()-18,fileName.length());
        File pictureFile = new File(appDir, fileName);
        if (pictureFile.exists()) {
            pictureFile.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(pictureFile);
            bm.compress(Bitmap.CompressFormat.PNG, 60, out);
//            Snackbar.make(findViewById(android.R.id.content), "保存图片成功"+pictureFile.getAbsolutePath(), Snackbar.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "保存图片成功"+pictureFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();

            out.flush();
            out.close();
            out = null;
            message.setType("success");
            message.setContent("保存图片成功");
            message.setData(pictureFile.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            message.setType("error");
            message.setContent("FileNotFoundException");
        } catch (IOException e) {
            e.printStackTrace();
            message.setType("error");
            message.setContent("IOException");
        } finally {
        }
        // 其次把文件插入到系统图库
//        try {
//            MediaStore.Images.Media.insertImage(getContext().getContentResolver(),
//                    pictureFile .getAbsolutePath(), fileName, null);
//        } catch (FileNotFoundException e) {
//            message.setType("error");
//            message.setContent("FileNotFoundException");
//            e.printStackTrace();
//        }
        // 最后通知图库更新
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(pictureFile);intent.setData(uri);
        getContext().sendBroadcast(intent);//这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦
        callback.invoke(message);
    }

}
