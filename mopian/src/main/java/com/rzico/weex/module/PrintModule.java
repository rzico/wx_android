package com.rzico.weex.module;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Base64;
import android.widget.Toast;

import com.rzico.weex.utils.BluetoothUtil;
import com.rzico.weex.utils.ESCUtil;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.common.WXModule;



import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Jinlesoft on 2017/11/18.
 */

public class PrintModule extends WXModule {


    public Context getContext() {
        return mWXSDKInstance.getContext();
    }

    @JSMethod
    public void print(String printString) throws UnsupportedEncodingException {

        BluetoothAdapter btAdapter = BluetoothUtil.getBTAdapter();
        BluetoothDevice device = BluetoothUtil.getDevice(btAdapter);
        // 1: Get BluetoothAdapter
        if (btAdapter == null) {
            Toast.makeText(getContext(), "请先打开蓝牙!", Toast.LENGTH_LONG).show();
            return;
        }
        // 2: Get Sunmi's InnerPrinter BluetoothDevice
        if (device == null) {
            Toast.makeText(getContext(), "请确认是否安装了内置打印机!", Toast.LENGTH_LONG).show();
            return;
        }
        // 3: Generate a order data
        byte[] data = Base64.decode(printString, Base64.DEFAULT);

        // 4: Using InnerPrinter print data
        BluetoothSocket socket = null;
        try {
            socket = BluetoothUtil.getSocket(device);
            BluetoothUtil.sendData(data, socket);
        } catch (IOException e) {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
    private byte[] generateMockData() {
        StringBuilder builder1 = new StringBuilder();
        StringBuilder builder2 = new StringBuilder();
        StringBuilder builder3 = new StringBuilder();
        StringBuilder builder4 = new StringBuilder();
        StringBuilder builder5 = new StringBuilder();
        StringBuilder builder6 = new StringBuilder();
        StringBuilder builder7 = new StringBuilder();
        StringBuilder builder8 = new StringBuilder();
        StringBuilder builder9 = new StringBuilder();
        StringBuilder builder10 = new StringBuilder();
        try {
            byte[] next2Line = ESCUtil.nextLine(2);
            byte[] title = "智 能 POS 签 购 单".getBytes("gb2312");

            byte[] boldOn = ESCUtil.boldOn();
            byte[] fontSize2Big = ESCUtil.fontSizeSetBig(3);
            byte[] center = ESCUtil.alignCenter();
            byte[] Stub = "------------商户存根------------".getBytes("gb2312");
            byte[] boldOff = ESCUtil.boldOff();
            byte[] fontSize2Small = ESCUtil.fontSizeSetBig(1);

            byte[] left = ESCUtil.alignLeft();

            byte[] merchantName = builder1.append("商户名：").append("").toString().getBytes("gb2312");
//			boldOn = ESCUtil.boldOn();
            byte[] fontSize1Big = ESCUtil.fontSizeSetBig(1);
            byte[] id = builder2.append("商户号：").append("").toString().getBytes("gb2312");
//			boldOff = ESCUtil.boldOff();
            byte[] fontSize1Small = ESCUtil.fontSizeSetSmall(1);

            next2Line = ESCUtil.nextLine(2);

            byte[] device = builder3.append("终端号：").append("").toString().getBytes("gb2312");
            byte[] nextLine = ESCUtil.nextLine(1);

            byte[] cashierName = builder4.append("收银员：").append("").toString().getBytes("gb2312");
            nextLine = ESCUtil.nextLine(1);

            byte[] line = "-------------------------------".getBytes("gb2312");
            nextLine = ESCUtil.nextLine(1);
            byte[] snNumber = builder5.append("流水号：").append("").toString().getBytes("gb2312");

            nextLine = ESCUtil.nextLine(1);

            byte[] type = builder7.append("交易类型：").append("").toString().getBytes("gb2312");
            byte[] next4Line = ESCUtil.nextLine(4);

            byte[] amount = builder8.append("金额：RMB ").append("").toString().getBytes("gb2312");
            byte[] signature = "支付人签名：".getBytes("gb2312");
            byte[] agreement = "确认以上交易同意将其记入商家账户".getBytes("gb2312");
            byte[] customerPhone = builder9.append("客服热线：").append("").toString().getBytes("gb2312");
            byte[] technical = builder10.append("技术支持：").append("").toString().getBytes("gb2312");
            next4Line = ESCUtil.nextLine(4);

            byte[] breakPartial = ESCUtil.feedPaperCutPartial();

            byte[][] cmdBytes = {center, fontSize1Small, boldOn, title, next2Line, boldOff, Stub, nextLine, left, merchantName,
                    nextLine, id, nextLine, device, nextLine, cashierName, nextLine, line,
                    nextLine, snNumber, nextLine, nextLine, type, nextLine, boldOn, amount, nextLine, boldOff,
                    signature, next2Line, line, nextLine, agreement, nextLine, customerPhone, nextLine, technical, next4Line,
                    breakPartial};

            return ESCUtil.byteMerger(cmdBytes);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
