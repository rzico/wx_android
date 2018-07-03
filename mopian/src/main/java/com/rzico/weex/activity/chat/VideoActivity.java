package com.rzico.weex.activity.chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.rzico.weex.R;
import com.umeng.analytics.MobclickAgent;

public class VideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
