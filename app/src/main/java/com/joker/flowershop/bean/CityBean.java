package com.joker.flowershop.bean;

import android.support.annotation.NonNull;

/**
 * Created by joker on 5/29 0029.
 */

public class CityBean implements Comparable {
    private String pr;
    private String code;
    private String city;

    public String getPr() {
        return pr;
    }

    public void setPr(String pr) {
        this.pr = pr;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return 0;
    }
}
