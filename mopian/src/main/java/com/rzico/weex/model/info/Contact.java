package com.rzico.weex.model.info;

import com.rzico.weex.utils.Utils;

/**
 * Created by Jinlesoft on 2017/10/25.
 * 手机联系人
 */

public class Contact {

    private String number;
    private String name;
    private String numberMd5;
    private String pinyin;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
        setNumberMd5(Utils.getMD5(number));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getNumberMd5() {
        return numberMd5;
    }

    public void setNumberMd5(String numberMd5) {
        this.numberMd5 = numberMd5;
    }
}
