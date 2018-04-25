package com.rzico.weex.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rzico.weex.model.event.MessageBus;
import com.rzico.weex.module.JSCallBaskManager;
import com.google.zxing.integration.android.IntentIntegrator;
import com.rzico.weex.R;
import com.rzico.weex.module.AlbumModule;
import com.rzico.weex.module.WXEventModule;
import com.rzico.weex.utils.Player;
import com.rzico.weex.utils.Utils;
import com.rzico.weex.utils.weex.CommonUtils;
import com.rzico.weex.utils.weex.DevOptionHandler;
import com.rzico.weex.utils.weex.ShakeDetector;
import com.rzico.weex.utils.weex.constants.Constants;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.ui.component.NestedContainer;
import com.taobao.weex.utils.WXSoInstallMgrSdk;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.rzico.weex.utils.photo.PhotoHandle.REQUEST_PHOTOHANDLER;
import static com.yalantis.ucrop.UCrop.REQUEST_CROP;

/**
 * 装载wxpage的activity
 */
public class WXPageActivity extends AbsWeexActivity implements
    WXSDKInstance.NestedInstanceInterceptor {

  private static final String TAG = "WXPageActivity";
  private boolean isFirst = true;
  private ProgressBar mProgressBar;
  private TextView mTipView;
  private AlertDialog mDevOptionsDialog;
  private boolean mIsShakeDetectorStarted = false;
  private boolean mIsDevSupportEnabled = WXEnvironment.isApkDebugable();
  private final LinkedHashMap<String, DevOptionHandler> mCustomDevOptions = new LinkedHashMap<>();
  private ShakeDetector mShakeDetector;

  @Override
  public void onCreateNestInstance(WXSDKInstance instance, NestedContainer container) {
    Log.d(TAG, "Nested Instance created.");
  }

  Handler mHandler = new Handler(){
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      if(Build.VERSION.SDK_INT >= 23){
        if (!Settings.canDrawOverlays(WXPageActivity.this)) {
          Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                  Uri.parse("package:" + getPackageName()));
          startActivityForResult(intent,10);
        }else {
          showDevOptionsDialog();
        }
      }else{
        showDevOptionsDialog();
      }
    }
  };

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void handleEvent(MessageBus messageBus){
    if(messageBus.getMessageType() == MessageBus.Type.GLOBAL){
      getWXSDKInstance().fireGlobalEventCallback(messageBus.getEventKey(), messageBus.getParams());
    }

  }
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_wxpage);
    EventBus.getDefault().register(this);
//    if (Constant.userId == 0) {
//      if (null != savedInstanceState) {
//        // activity由系统打开 (是由于手机内存不够,activity在后台被系统回收,再打开时出现的现象)
//        // 因为系统加载的所有的Activity不在同一个线程,所以要结束除了loginActivity之外的其他线程
//        android.os.Process.killProcess(android.os.Process.myPid());
//      } else {
//        this.finish();
//      }
//      return;
//    }
    mContainer = (ViewGroup) findViewById(R.id.container);
    mProgressBar = (ProgressBar) findViewById(R.id.progress);
    mTipView = (TextView) findViewById(R.id.index_tip);
//   mContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//     @Override
//     public void onGlobalLayout() {
//       int heightDff = mContainer.getRootView().getHeight() - mContainer.getHeight();
//       if(heightDff > 100){
//         Toast.makeText(WXPageActivity.this, "有软键盘", Toast.LENGTH_SHORT).show();
//       }else {
//         Toast.makeText(WXPageActivity.this, "没有软键盘", Toast.LENGTH_SHORT).show();
//       }
//     }
//   });
    if (mIsDevSupportEnabled && !CommonUtils.hasHardwareMenuKey()) {
      mShakeDetector = new ShakeDetector(new ShakeDetector.ShakeListener() {
        @Override
        public void onShake() {
          if(Utils.isApkDebugable(WXPageActivity.this)){
            mHandler.sendEmptyMessage(0);
          }
        }
      });
    }

    Uri uri = getIntent().getData();
    Bundle bundle = getIntent().getExtras();
    if (uri != null) {
      mUri = uri;
    }
    if(getIntent().getStringExtra("isLocal") != null){
      isLocalUrl = getIntent().getStringExtra("isLocal").equals("true");
    }

    if (bundle != null) {
      String bundleUrl = bundle.getString(Constants.PARAM_BUNDLE_URL);
      if (!TextUtils.isEmpty(bundleUrl)) {
        mUri = Uri.parse(bundleUrl);
      }
    }

    if (mUri == null) {
      Toast.makeText(this, "the uri is empty!", Toast.LENGTH_SHORT).show();
      finish();
      return;
    }


    if (!WXSoInstallMgrSdk.isCPUSupport()) {
      mProgressBar.setVisibility(View.INVISIBLE);
      mTipView.setText(R.string.cpu_not_support_tip);
      return;
    }

    loadUrl(getUrl(mUri));
  }

  @Override
  public void onResume() {
    super.onResume();
    if(!isFirst){
//      Toast.makeText(WXPageActivity.this, "onResume", Toast.LENGTH_SHORT).show();
      Map<String,Object> params=new HashMap<>();
      params.put("key","value");

    mInstance.fireGlobalEventCallback("onResume", params);
    }
    isFirst = false;
    if (!mIsShakeDetectorStarted && mShakeDetector != null) {
      mShakeDetector.start((SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE));
      mIsShakeDetectorStarted = true;
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    if (mIsShakeDetectorStarted && mShakeDetector != null) {
      mShakeDetector.stop();
      mIsShakeDetectorStarted = false;
    }
  }

  private String getUrl(Uri uri) {
    String url = uri.toString();
    String scheme = uri.getScheme();
    if (uri.isHierarchical()) {
      if (TextUtils.equals(scheme, "http") || TextUtils.equals(scheme, "https")) {
        String weexTpl = uri.getQueryParameter(Constants.WEEX_TPL_KEY);
        if (!TextUtils.isEmpty(weexTpl)) {
          url = weexTpl;
        }
      }
    }
    return url;
  }

  protected void preRenderPage() {
    mProgressBar.setVisibility(View.VISIBLE);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    Intent intent = new Intent("requestPermission");
    intent.putExtra("REQUEST_PERMISSION_CODE", requestCode);
    intent.putExtra("permissions", permissions);
    intent.putExtra("grantResults", grantResults);
    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
  }

  @Override
  public void onRenderSuccess(WXSDKInstance instance, int width, int height) {
    //渲染成功后
    mProgressBar.setVisibility(View.GONE);
    mTipView.setVisibility(View.GONE);
  }

  @Override
  public void onException(WXSDKInstance instance, String errCode, String msg) {
    mProgressBar.setVisibility(View.GONE);
    mTipView.setVisibility(View.VISIBLE);
      mTipView.setText(R.string.index_tip);

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(isLocalPage() ? R.menu.main_scan : R.menu.main, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_refresh:
        createWeexInstance();
        renderPage();
        break;
      case R.id.action_scan:
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan a barcode");
        //integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.setPrompt(getString(R.string.capture_qrcode_prompt));
        integrator.initiateScan();
        break;
      case android.R.id.home:
        finish();
      default:
        break;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == 10) {
      if(Build.VERSION.SDK_INT >= 23){
        if (!Settings.canDrawOverlays(this)) {
          // SYSTEM_ALERT_WINDOW permission not granted...
          Toast.makeText(WXPageActivity.this,"not granted",Toast.LENGTH_SHORT);
        }
      }
    }
    if(requestCode == REQUEST_PHOTOHANDLER || requestCode == REQUEST_CROP){//而代表请求裁剪
      AlbumModule.get().onActivityResult(requestCode, resultCode, data);
    }else{
      WXEventModule.get().onActivityResult(requestCode, resultCode, data);
    }
//    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//    if (result != null) {
//      if (result.getContents() == null) {
//        Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
//      } else {
//        handleDecodeInternally(result.getContents());
//      }
//    }
//    super.onActivityResult(requestCode, resultCode, data);
  }

  // Put up our own UI for how to handle the decoded contents.
  private void handleDecodeInternally(String code) {

    if (!TextUtils.isEmpty(code)) {
      Uri uri = Uri.parse(code);
      if (uri.getQueryParameterNames().contains("bundle")) {
        WXEnvironment.sDynamicMode = uri.getBooleanQueryParameter("debug", false);
        WXEnvironment.sDynamicUrl = uri.getQueryParameter("bundle");
        String tip = WXEnvironment.sDynamicMode ? "Has switched to Dynamic Mode" : "Has switched to Normal Mode";
        Toast.makeText(this, tip, Toast.LENGTH_SHORT).show();
        finish();
        return;
      } else if (uri.getQueryParameterNames().contains("_wx_devtool")) {
        WXEnvironment.sRemoteDebugProxyUrl = uri.getQueryParameter("_wx_devtool");
        WXEnvironment.sDebugServerConnectable = true;
        WXSDKEngine.reload();
        Toast.makeText(this, "devtool", Toast.LENGTH_SHORT).show();
        return;
      } else if (code.contains("_wx_debug")) {
        uri = Uri.parse(code);
        String debug_url = uri.getQueryParameter("_wx_debug");
        finish();
      } else {
        Toast.makeText(this, code, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Constants.ACTION_OPEN_URL);
        intent.setPackage(getPackageName());
        intent.setData(Uri.parse(code));
        startActivity(intent);
      }
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Player.getInstance().stop();
    EventBus.getDefault().unregister(this);
    if (mShakeDetector != null) {
      mShakeDetector.stop();
    }
    String key = getIntent().getStringExtra("key");
    if( key != null && !key.equals("")){
//      com.rzico.weex.model.info.Message message = new com.rzico.weex.model.info.Message().success("返回");
      com.rzico.weex.model.info.Message message = new com.rzico.weex.model.info.Message();
      message.setType("error");
      message.setData("goback");
      message.setContent("返回");
      if(JSCallBaskManager.get(key)!= null){
        JSCallBaskManager.get(key).invoke(message);
        JSCallBaskManager.remove(key);
      }
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {

    }
    return super.onKeyDown(keyCode, event);
  }

  public void showDevOptionsDialog() {
    if (mDevOptionsDialog != null || !mIsDevSupportEnabled || ActivityManager.isUserAMonkey()) {
      return;
    }
    LinkedHashMap<String, DevOptionHandler> options = new LinkedHashMap<>();
    /* register standard options */
    options.put(
        getString(R.string.scan_qr_code), new DevOptionHandler() {
          @Override
          public void onOptionSelected() {
            IntentIntegrator integrator = new IntentIntegrator(WXPageActivity.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            integrator.setPrompt("Scan a barcode");
            //integrator.setCameraId(0);  // Use a specific camera of the device
            integrator.setBeepEnabled(true);
            integrator.setOrientationLocked(false);
            integrator.setBarcodeImageEnabled(true);
            integrator.setPrompt(getString(R.string.capture_qrcode_prompt));
            integrator.initiateScan();
          }
        });
    options.put(
        getString(R.string.page_refresh), new DevOptionHandler() {
          @Override
          public void onOptionSelected() {
            createWeexInstance();
            renderPage();
          }
        });

    if (mCustomDevOptions.size() > 0) {
      options.putAll(mCustomDevOptions);
    }

    final DevOptionHandler[] optionHandlers = options.values().toArray(new DevOptionHandler[0]);

    mDevOptionsDialog =
        new AlertDialog.Builder(WXPageActivity.this)
            .setItems(
                options.keySet().toArray(new String[0]),
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    optionHandlers[which].onOptionSelected();
                    mDevOptionsDialog = null;
                  }
                })
            .setOnCancelListener(new DialogInterface.OnCancelListener() {
              @Override
              public void onCancel(DialogInterface dialog) {
                mDevOptionsDialog = null;
              }
            })
            .create();
    mDevOptionsDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    mDevOptionsDialog.show();
  }

  public void addCustomDevOption(
      String optionName,
      DevOptionHandler optionHandler) {
    mCustomDevOptions.put(optionName, optionHandler);
  }

}
