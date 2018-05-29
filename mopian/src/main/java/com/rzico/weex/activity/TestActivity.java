package com.rzico.weex.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.rzico.weex.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.taobao.weex.IWXRenderListener;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.common.WXRenderStrategy;
import com.taobao.weex.utils.WXFileUtils;

import java.io.File;

public class TestActivity extends AppCompatActivity {

    private Button back;
    private ImageView imageView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        back = (Button)findViewById(R.id.button);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestActivity.this.setResult(2);
                TestActivity.this.finish();
            }
        });
        File img = new File("/storage/emulated/0/DCIM/Camera/20180430_154130.jpg");
        File img2 = new File("/storage/emulated/0/Android/data/com.yixiang.mopian/cache/small_IMG_20180522120739.jpg");
        Picasso.with(WXEnvironment.getApplication())
                .load(img)
                .into(imageView2, new Callback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(TestActivity.this,"onSuccess",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(TestActivity.this,"onError",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
