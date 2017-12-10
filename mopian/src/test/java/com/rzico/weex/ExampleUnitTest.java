package com.rzico.weex;

import android.util.Base64;

import com.google.gson.Gson;
import com.rzico.weex.activity.BaseActivity;
import com.rzico.weex.model.info.Message;
import com.rzico.weex.model.info.REABean;
import com.rzico.weex.net.HttpRequest;
import com.rzico.weex.net.XRequest;
import com.rzico.weex.utils.RSAUtil;
import com.rzico.weex.utils.RSAUtils;

import org.junit.Test;

import java.security.PublicKey;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        new XRequest(WXApplication.getActivity(), Constant.PUBLIC_KEY).setOnRequestListener(new HttpRequest.OnRequestListener() {
            @Override
            public void onSuccess(BaseActivity activity, String result) {
                try {
                    REABean entity= new Gson().fromJson(result, REABean.class);
                    if(entity!=null){
                        RSAUtil rsaUtil = new RSAUtil(WXApplication.getActivity());

//                        System.out.println("exponent:" + entity.getExponent());
//                        System.out.println("key:" + entity.getKey());
//                        System.out.println("modulus:" + entity.getModulus());
//            byte[] enData = rsaUtil.encryptData(data.getBytes("UTF-8"), , entity.getData().getExponent());
//            String afterencrypt = new String(Base64.encode(enData, Base64.NO_WRAP),"UTF-8");

                        PublicKey publicKey = RSAUtils.getPublicKey(Base64.decode(entity.getData().getModulus(),Base64.NO_WRAP), Base64.decode(entity.getData().getExponent(),Base64.NO_WRAP));
                        String afterencrypt = RSAUtils.encrypt(publicKey, "13400766646");
//                        System.out.println("key加密后:" + afterencrypt);
                        Message message = new Message();
                        message.setType("success");
                        message.setContent("加密成功");
                        message.setData(afterencrypt);

                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Message message = new Message();
                    message.setType("error");
                    message.setContent("加密失败");
                }
            }
            @Override
            public void onFail(BaseActivity activity, int code) {
                Message message = new Message();
                message.setType("error");
                message.setContent("获取公钥失败");
            }
        }).execute();

    }

}