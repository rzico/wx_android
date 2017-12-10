package com.rzico.weex.module;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rzico.weex.R;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.ui.module.WXModalUIModule;
import com.taobao.weex.utils.WXLogUtils;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;

import static com.rzico.weex.R.color.white;

/**
 * Created by Jinlesoft on 2017/10/10.
 */

public class MYModalUIModule extends WXModalUIModule {

    private final String HINT = "placeholder";
    @JSMethod
    @Override
    public void prompt(String param, final JSCallback callback) {
        if (mWXSDKInstance.getContext() instanceof Activity) {
            String message = "";
            String defaultValue = "";
            String okTitle = OK;
            String cancelTitle = CANCEL;
            String placeholder = "";
            if (!TextUtils.isEmpty(param)) {
                try {
                    param = URLDecoder.decode(param, "utf-8");
                    JSONObject jsObj = JSON.parseObject(param);
                    message = jsObj.getString(MESSAGE);
                    okTitle = jsObj.getString(OK_TITLE);
                    cancelTitle = jsObj.getString(CANCEL_TITLE);
                    defaultValue = jsObj.getString(DEFAULT);
                    placeholder  = jsObj.getString(HINT);
                } catch (Exception e) {
                    WXLogUtils.e("[WXModalUIModule] confirm param parse error ", e);
                }
            }

            if (TextUtils.isEmpty(message)) {
                message = "";
            }


            AlertDialog.Builder builder = new AlertDialog.Builder(mWXSDKInstance.getContext());

            builder.setTitle(message);

            LinearLayout layout = new LinearLayout(mWXSDKInstance.getContext());
            LinearLayout.LayoutParams lLayoutlayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            lLayoutlayoutParams.setMargins(50, 0, 50, 0);
            layout.setLayoutParams(lLayoutlayoutParams);
            layout.setBackgroundResource(white);
            layout.setPadding(5, 5, 5, 5);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText editText = new EditText(mWXSDKInstance.getContext());

            LinearLayout.LayoutParams etParam = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    etParam.setMargins(50, 0, 50, 0);
            editText.setLayoutParams(etParam);
            editText.setHint(placeholder);
            editText.setText(defaultValue);
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.requestFocus();
            layout.addView(editText);
            builder.setView(layout);


            final String okTitleFinal = TextUtils.isEmpty(okTitle) ? OK : okTitle;
            final String cancelTitleFinal = TextUtils.isEmpty(cancelTitle) ? CANCEL : cancelTitle;
            builder.setPositiveButton(okTitleFinal, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (callback != null) {
                        Map<String, Object> result = new HashMap<String, Object>();
                        result.put(RESULT, okTitleFinal);
                        result.put(DATA, editText.getText().toString());
                        callback.invoke(result);
                    }
                }
            }).setNegativeButton(cancelTitleFinal, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (callback != null) {
                        Map<String, Object> result = new HashMap<String, Object>();
                        result.put(RESULT, cancelTitleFinal);
                        result.put(DATA, editText.getText().toString());
                        callback.invoke(result);
                    }

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
            editText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm= (InputMethodManager) mWXSDKInstance.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, 0);
                }
            }, 200);
            tracking(alertDialog);
        } else {
            WXLogUtils.e("when call prompt mWXSDKInstance.getContext() must instanceof Activity");
        }
    }
}
