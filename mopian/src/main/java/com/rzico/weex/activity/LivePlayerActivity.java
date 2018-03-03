package com.rzico.weex.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.rzico.weex.R;
import com.rzico.weex.model.LivePlayerBean;
import com.rzico.weex.module.JSCallBaskManager;
import com.rzico.weex.utils.AudioUtil;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.ugc.TXRecordCommon;

public class LivePlayerActivity extends Activity implements ITXLivePlayListener, View.OnClickListener{
    private static final String TAG = LivePlayerActivity.class.getSimpleName();

    private TXLivePlayer mLivePlayer = null;
    private boolean mIsPlaying;
    private TXCloudVideoView mPlayerView;
    private ImageView mLoadingView;
    private boolean          mHWDecode   = false;
    private LinearLayout mRootView;
    private WebView mWebView;


    private static final int  CACHE_STRATEGY_FAST  = 1;  //极速
    private static final int  CACHE_STRATEGY_SMOOTH = 2;  //流畅
    private static final int  CACHE_STRATEGY_AUTO = 3;  //自动

    private static final float  CACHE_TIME_FAST = 1.0f;
    private static final float  CACHE_TIME_SMOOTH = 5.0f;

    public static final int ACTIVITY_TYPE_PUBLISH      = 1;
    public static final int ACTIVITY_TYPE_LIVE_PLAY    = 2;
    public static final int ACTIVITY_TYPE_VOD_PLAY     = 3;
    public static final int ACTIVITY_TYPE_LINK_MIC     = 4;
    public static final int ACTIVITY_TYPE_REALTIME_PLAY = 5;

    private int              mCacheStrategy = 0;


    private int              mCurrentRenderMode;
    private int              mCurrentRenderRotation;

    private int              mPlayType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
    private TXLivePlayConfig mPlayConfig;
    private long             mStartPlayTS = 0;
    protected int            mActivityType;
    private boolean mRecordFlag = false;
    private boolean mCancelRecordFlag = false;
    private boolean mIsLogShow = false;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentRenderMode     = TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION;
        mCurrentRenderRotation = TXLiveConstants.RENDER_ROTATION_PORTRAIT;

        mActivityType = getIntent().getIntExtra("PLAY_TYPE", ACTIVITY_TYPE_LIVE_PLAY);

        mPlayConfig = new TXLivePlayConfig();

        setContentView();

//        LinearLayout backLL = (LinearLayout)findViewById(R.id.back_ll);
//        backLL.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                stopPlay();
//                finish();
//            }
//        });

        checkPublishPermission();


        LivePlayerBean livePlayerBean = (LivePlayerBean) getIntent().getSerializableExtra("livePlayerParam");

        //加载webview
        if(livePlayerBean != null){

            if(!livePlayerBean.getUrl().equals("")){
                if(livePlayerBean.getMethod().toUpperCase().equals("POST")){
                    mWebView.postUrl(livePlayerBean.getUrl(), null);
                }else {
//                    mWebView.loadUrl("http://dev.rzico.com/nihtan/api/play.jhtml?game=Dragon-Tiger&table=1&range=5-150");
                    mWebView.loadUrl(livePlayerBean.getUrl());
                }
            }

            //填充
            mCurrentRenderMode = TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN;
            mLivePlayer.setRenderMode(mCurrentRenderMode);

//            mCurrentRenderRotation = TXLiveConstants.RENDER_ROTATION_LANDSCAPE;
//            mLivePlayer.setRenderRotation(mCurrentRenderRotation);
            //开始播放
            mIsPlaying = startPlay(livePlayerBean.getVideo());
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LivePlayerBean livePlayerBean = (LivePlayerBean) intent.getSerializableExtra("livePlayerParam");

        //加载webview
        if(livePlayerBean != null){

            if(!livePlayerBean.getUrl().equals("")){
                if(livePlayerBean.getMethod().toUpperCase().equals("POST")){
                    mWebView.postUrl(livePlayerBean.getUrl(), null);
                }else {
                    mWebView.loadUrl(livePlayerBean.getUrl());
//                    mWebView.postUrl(livePlayerBean.getUrl(), null);
                }
            }

            //填充
            mCurrentRenderMode = TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN;
            mLivePlayer.setRenderMode(mCurrentRenderMode);

//            mCurrentRenderRotation = TXLiveConstants.RENDER_ROTATION_LANDSCAPE;
//            mLivePlayer.setRenderRotation(mCurrentRenderRotation);
            //开始播放
            mIsPlaying = startPlay(livePlayerBean.getVideo());
        }
    }

    private boolean checkPublishPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(this,
                        permissions.toArray(new String[0]),
                        100);
                return false;
            }
        }

        return true;
    }


    public void setContentView() {
        super.setContentView(R.layout.activity_play);

        mRootView = (LinearLayout) findViewById(R.id.root);
        if (mLivePlayer == null){
            mLivePlayer = new TXLivePlayer(this);
        }

        mPlayerView = (TXCloudVideoView) findViewById(R.id.video_view);
        mPlayerView.setLogMargin(12, 12, 110, 60);
        mPlayerView.showLog(false);
        mLoadingView = (ImageView) findViewById(R.id.loadingImageView);
        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.setWebChromeClient(new WebChromeClient());
//        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    boolean canHerf = true;
                    if(url.endsWith("game=true")){
                        //关闭当前页面
                        finish();
                        canHerf = false;
                    }else if(url.startsWith("http")){
                        view.loadUrl(url);
                        canHerf = true;
                    }
                    if(url.startsWith("nihvolbutton://bluefrog")){

                        String allUrl = url;
                        String deleteScheme = allUrl.replace("nihvolbutton://","");

                        if(!deleteScheme.equals("") && deleteScheme.length() > 0 && deleteScheme.contains("?") && deleteScheme.contains("=")){
                            String[] one = deleteScheme.split("\\?");//问号需要加双传意符
                            if(one.equals("bluefrog")){//如果是这个方法
                                String[] two = one[1].split("=");
                                AudioUtil audioUtil = AudioUtil.getInstance(LivePlayerActivity.this);
                                if(two[0].equals("volume")){//调整音量
                                    audioUtil.setMediaVolume(Integer.valueOf(two[1]));
                                }else if(two[0].equals("mute")){//控制有声音无声音
                                    audioUtil.setSpeakerStatus(two[1].equals("0")); //等于0则关闭无声 就是 开启扩音器
                                }
                                canHerf = false;
                            }

                        }

                    }

                    return canHerf;
            }
        });

        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.getSettings().setAppCacheEnabled(true);
//        //设置 缓存模式
//        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
//        // 开启 DOM storage API 功能
//        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        mWebView.setBackgroundColor(0); // 设置背景色
        mWebView.getBackground().setAlpha(0); // 设置填充透明度 范围：0-255
        mWebView.loadDataWithBaseURL(null, "加载中。。", "text/html", "utf-8",null);
        mWebView.setVisibility(View.VISIBLE); // 加载完之后进行设置显示，以免加载时初始化效果不好看
        mIsPlaying = false;




        this.setCacheStrategy(CACHE_STRATEGY_AUTO);


        View view = mPlayerView.getRootView();
        view.setOnClickListener(this);
    }



    @Override
	public void onDestroy() {
		super.onDestroy();
		if (mLivePlayer != null) {
            mLivePlayer.stopPlay(true);
            mLivePlayer = null;
        }
        if (mPlayerView != null){
            mPlayerView.onDestroy();
            mPlayerView = null;
        }
        mPlayConfig = null;
        stopPlay();
        String key = getIntent().getStringExtra("key");
        if( key != null && !key.equals("")){
            com.rzico.weex.model.info.Message message = new com.rzico.weex.model.info.Message();
            message.setType("error");
            message.setData("goback");
            message.setContent("返回");
            if(JSCallBaskManager.get(key)!= null){
                JSCallBaskManager.get(key).invoke(message);
                JSCallBaskManager.remove(key);
            }
        }
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);       //强制为横屏
        Log.d(TAG,"vrender onDestroy");
	}

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop(){
        super.onStop();

    }

    @Override
    public void onResume() {
        super.onResume();
        /**
         * 设置为横屏
         */
        if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }



    private boolean checkPlayUrl(final String playUrl) {
        if (TextUtils.isEmpty(playUrl) || (!playUrl.startsWith("http://") && !playUrl.startsWith("https://") && !playUrl.startsWith("rtmp://")  && !playUrl.startsWith("/"))) {
            Toast.makeText(getApplicationContext(), "播放地址不合法，直播目前仅支持rtmp,flv播放方式!", Toast.LENGTH_SHORT).show();
            return false;
        }

        switch (mActivityType) {
            case ACTIVITY_TYPE_LIVE_PLAY:
            {
                if (playUrl.startsWith("rtmp://")) {
                    mPlayType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
                } else if ((playUrl.startsWith("http://") || playUrl.startsWith("https://"))&& playUrl.contains(".flv")) {
                    mPlayType = TXLivePlayer.PLAY_TYPE_LIVE_FLV;
                } else {
                    Toast.makeText(getApplicationContext(), "播放地址不合法，直播目前仅支持rtmp,flv播放方式!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
                break;
            case ACTIVITY_TYPE_REALTIME_PLAY:
                mPlayType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP_ACC;
                break;
            default:
                Toast.makeText(getApplicationContext(), "播放地址不合法，目前仅支持rtmp,flv播放方式!", Toast.LENGTH_SHORT).show();
                return false;
        }
        return true;
    }


    private boolean startPlay(String url) {
//        String playUrl = "rtmp://47.75.7.152:1935/HKRepeaterBC01/video";
        String playUrl = url;

        if (!checkPlayUrl(playUrl)) {
            return false;
        }

        mRootView.setBackgroundColor(0xff000000);

        mLivePlayer.setPlayerView(mPlayerView);

        mLivePlayer.setPlayListener(this);
        // 硬件加速在1080p解码场景下效果显著，但细节之处并不如想象的那么美好：
        // (1) 只有 4.3 以上android系统才支持
        // (2) 兼容性我们目前还仅过了小米华为等常见机型，故这里的返回值您先不要太当真
        mLivePlayer.enableHardwareDecode(mHWDecode);
        mLivePlayer.setRenderRotation(mCurrentRenderRotation);
        mLivePlayer.setRenderMode(mCurrentRenderMode);
        //设置播放器缓存策略
        //这里将播放器的策略设置为自动调整，调整的范围设定为1到4s，您也可以通过setCacheTime将播放器策略设置为采用
        //固定缓存时间。如果您什么都不调用，播放器将采用默认的策略（默认策略为自动调整，调整范围为1到4s）
        //mLivePlayer.setCacheTime(5);

        mLivePlayer.setConfig(mPlayConfig);

        int result = mLivePlayer.startPlay(playUrl,mPlayType); // result返回值：0 success;  -1 empty url; -2 invalid url; -3 invalid playType;
        if (result != 0) {
            mRootView.setBackgroundResource(R.drawable.main_bkg);
            return false;
        }

        Log.w("video render","timetrack start play");

        startLoadingAnimation();


        mStartPlayTS = System.currentTimeMillis();

        return true;
    }

    private  void stopPlay() {
        mRootView.setBackgroundResource(R.drawable.main_bkg);
        stopLoadingAnimation();
        if (mLivePlayer != null) {
            mLivePlayer.stopRecord();
            mLivePlayer.setPlayListener(null);
            mLivePlayer.stopPlay(true);
        }
        mIsPlaying = false;
    }

    @Override
    public void onPlayEvent(int event, Bundle param) {
        String playEventLog = "receive event: " + event + ", " + param.getString(TXLiveConstants.EVT_DESCRIPTION);
        Log.d(TAG, playEventLog);

        if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
            stopLoadingAnimation();
            Log.d("AutoMonitor", "PlayFirstRender,cost=" +(System.currentTimeMillis()-mStartPlayTS));
        } else if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT || event == TXLiveConstants.PLAY_EVT_PLAY_END) {
            stopPlay();
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_LOADING){
            startLoadingAnimation();
        } else if (event == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {
            stopLoadingAnimation();
        } else if (event == TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION) {

        } else if (event == TXLiveConstants.PLAY_EVT_CHANGE_ROTATION) {
            return;
        }

        if (event < 0) {
            Toast.makeText(getApplicationContext(), param.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();
        }

    }

    //公用打印辅助函数
    protected String getNetStatusString(Bundle status) {
        String str = String.format("%-14s %-14s %-12s\n%-8s %-8s %-8s %-8s\n%-14s %-14s\n%-14s %-14s",
                "CPU:"+status.getString(TXLiveConstants.NET_STATUS_CPU_USAGE),
                "RES:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH)+"*"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT),
                "SPD:"+status.getInt(TXLiveConstants.NET_STATUS_NET_SPEED)+"Kbps",
                "JIT:"+status.getInt(TXLiveConstants.NET_STATUS_NET_JITTER),
                "FPS:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_FPS),
                "GOP:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_GOP)+"s",
                "ARA:"+status.getInt(TXLiveConstants.NET_STATUS_AUDIO_BITRATE)+"Kbps",
                "QUE:"+status.getInt(TXLiveConstants.NET_STATUS_CODEC_CACHE)
                        +"|"+status.getInt(TXLiveConstants.NET_STATUS_CACHE_SIZE)
                        +","+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_CACHE_SIZE)
                        +","+status.getInt(TXLiveConstants.NET_STATUS_V_DEC_CACHE_SIZE)
                        +"|"+status.getInt(TXLiveConstants.NET_STATUS_AV_RECV_INTERVAL)
                        +","+status.getInt(TXLiveConstants.NET_STATUS_AV_PLAY_INTERVAL)
                        +","+String.format("%.1f", status.getFloat(TXLiveConstants.NET_STATUS_AUDIO_PLAY_SPEED)).toString(),
                "VRA:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_BITRATE)+"Kbps",
                "SVR:"+status.getString(TXLiveConstants.NET_STATUS_SERVER_IP),
                "AUDIO:"+status.getString(TXLiveConstants.NET_STATUS_AUDIO_INFO));
        return str;
    }

    @Override
    public void onNetStatus(Bundle status) {
        String str = getNetStatusString(status);
        Log.d(TAG, "Current status, CPU:"+status.getString(TXLiveConstants.NET_STATUS_CPU_USAGE)+
                ", RES:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH)+"*"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT)+
                ", SPD:"+status.getInt(TXLiveConstants.NET_STATUS_NET_SPEED)+"Kbps"+
                ", FPS:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_FPS)+
                ", ARA:"+status.getInt(TXLiveConstants.NET_STATUS_AUDIO_BITRATE)+"Kbps"+
                ", VRA:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_BITRATE)+"Kbps");
        }

    public void setCacheStrategy(int nCacheStrategy) {
        if (mCacheStrategy == nCacheStrategy)   return;
        mCacheStrategy = nCacheStrategy;

        switch (nCacheStrategy) {
            case CACHE_STRATEGY_FAST:
                mPlayConfig.setAutoAdjustCacheTime(true);
                mPlayConfig.setCacheTime(CACHE_TIME_FAST);
                mPlayConfig.setMaxAutoAdjustCacheTime(CACHE_TIME_FAST);
                mPlayConfig.setMinAutoAdjustCacheTime(CACHE_TIME_FAST);
                mLivePlayer.setConfig(mPlayConfig);
                break;

            case CACHE_STRATEGY_SMOOTH:
                mPlayConfig.setAutoAdjustCacheTime(false);
                mPlayConfig.setCacheTime(CACHE_TIME_SMOOTH);
                mPlayConfig.setMaxAutoAdjustCacheTime(CACHE_TIME_SMOOTH);
                mPlayConfig.setMinAutoAdjustCacheTime(CACHE_TIME_FAST);
                mLivePlayer.setConfig(mPlayConfig);
                break;

            case CACHE_STRATEGY_AUTO:
                mPlayConfig.setAutoAdjustCacheTime(true);
                mPlayConfig.setCacheTime(CACHE_TIME_SMOOTH);
                mPlayConfig.setMaxAutoAdjustCacheTime(CACHE_TIME_SMOOTH);
                mPlayConfig.setMinAutoAdjustCacheTime(CACHE_TIME_FAST);
                mLivePlayer.setConfig(mPlayConfig);
                break;

            default:
                break;
        }
    }

    private void startLoadingAnimation() {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.VISIBLE);
            ((AnimationDrawable)mLoadingView.getDrawable()).start();
        }
    }

    private void stopLoadingAnimation() {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.GONE);
            ((AnimationDrawable)mLoadingView.getDrawable()).stop();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != 100 || data ==null || data.getExtras() == null || TextUtils.isEmpty(data.getExtras().getString("result"))) {
            return;
        }
        String result = data.getExtras().getString("result");

    }

    @Override
    public void onClick(View v) {

    }


    private static class TXFechPushUrlCall implements Callback {
        WeakReference<LivePlayerActivity> mPlayer;
        public TXFechPushUrlCall(LivePlayerActivity pusher) {
            mPlayer = new WeakReference<LivePlayerActivity>(pusher);
        }

        @Override
        public void onFailure(Call call, IOException e) {
            final LivePlayerActivity player = mPlayer.get();
            if (player != null) {
                player.mFetching = false;
                player.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(player, "获取测试地址失败。", Toast.LENGTH_SHORT).show();
                        player.mFetchProgressDialog.dismiss();
                    }
                });
            }
            Log.e(TAG, "fetch push url failed ");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                String rspString = response.body().string();
                final LivePlayerActivity player = mPlayer.get();
                if (player != null) {
                    try {
                        JSONObject jsonRsp = new JSONObject(rspString);
                        final String playUrl = jsonRsp.optString("url_rtmpacc");
                        player.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(player, "测试地址的影像来自在线UTC时间的录屏推流，推流工具采用移动直播 Windows SDK + VCam。", Toast.LENGTH_LONG).show();
                                player.mFetchProgressDialog.dismiss();
                            }
                        });

                    } catch(Exception e){
                        Log.e(TAG, "fetch push url error ");
                        Log.e(TAG, e.toString());
                    }

                } else {
                    player.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(player, "获取测试地址失败。", Toast.LENGTH_SHORT).show();
                            player.mFetchProgressDialog.dismiss();
                        }
                    });
                    Log.e(TAG, "fetch push url failed code: " + response.code());
                }
                player.mFetching = false;
            }
        }
    };
    private TXFechPushUrlCall mFechCallback = null;
    //获取推流地址
    private OkHttpClient mOkHttpClient = null;
    private boolean mFetching = false;
    private ProgressDialog mFetchProgressDialog;

    private void jumpToHelpPage() {
        Uri uri = Uri.parse("https://cloud.tencent.com/document/product/454/7886");
        if (mActivityType == ACTIVITY_TYPE_REALTIME_PLAY) {
            uri = Uri.parse("https://cloud.tencent.com/document/product/454/7886#RealTimePlay");
        }
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }
}