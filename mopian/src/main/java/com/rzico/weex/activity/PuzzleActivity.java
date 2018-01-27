package com.rzico.weex.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rzico.weex.R;
import com.rzico.weex.model.info.Message;
import com.rzico.weex.module.JSCallBaskManager;
import com.rzico.weex.utils.BarTextColorUtils;
import com.yixiang.mopian.constant.AllConstant;

import org.xutils.common.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.bean.MediaBean;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageMultipleResultEvent;

/**
 * Created by dd on 16/1/13.
 * 拼图主界面
 */
public class PuzzleActivity extends cn.finalteam.rxgalleryfinal.PuzzleActivity implements View.OnClickListener {

    /**
     * 这里重装他 是为了处理一些weex用到的回调方法
     */
    private String key = "";//用于回调
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        key = getIntent().getStringExtra("key");
    }


    @Override
    protected void back() {
        Message message = new Message();
        message.setType("cancel");
        message.setContent("cancel");
        message.setData("");
        JSCallBaskManager.get(key).invoke(message);
        JSCallBaskManager.remove(key);
        super.back();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            back();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void savePuzzle() {
        buildDrawingCache(puzzleLL);
        puzzleLL.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        Bitmap bitmap = puzzleLL.getDrawingCache().copy(Bitmap.Config.RGB_565, true);
        Message message = new Message();
        try {
            File file = FileUtil.saveBitmapJPG("MOPIAN" + System.currentTimeMillis(), bitmap, AllConstant.getDiskCachePath(PuzzleActivity.this));
            message.setSuccess(file.getPath(), "保存成功");
            // 最后通知图库更新
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);intent.setData(uri);
            sendBroadcast(intent);//这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦
        } catch (IOException e) {
            message.setError("保存失败");
            e.printStackTrace();
        }
        if(JSCallBaskManager.get(key) != null){
            JSCallBaskManager.get(key).invoke(message);
        }
    }
}
