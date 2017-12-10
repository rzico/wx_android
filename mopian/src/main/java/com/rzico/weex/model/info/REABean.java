package com.rzico.weex.model.info;

/**
 * Created by Jinlesoft on 2017/9/20.
 */

public class REABean {
    private REABean.data data;

    public REABean.data getData() {
        return data;
    }

    public void setData(REABean.data data) {
        this.data = data;
    }

    public class data{
        private String exponent;

        private String key;

        private String modulus;

        public String getExponent() {
            return exponent;
        }

        public void setExponent(String exponent) {
            this.exponent = exponent;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getModulus() {
            return modulus;
        }

        public void setModulus(String modulus) {
            this.modulus = modulus;
        }
    }
}
