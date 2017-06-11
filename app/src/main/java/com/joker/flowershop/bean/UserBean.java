package com.joker.flowershop.bean;

import java.io.Serializable;

/**
 * Created by joker on 5/20 0020.
 */
// {"id":1,"email":"admin@qq.com","passWord":"admin","username":"Admin","icon":"http://123.206.201.169:8080/FlowerShop/flower/1495088033874.jpg"}
public class UserBean implements Serializable {
    private int id;
    private String email;
    private String passWord;
    private String username;
    private String icon;
    private String cityCode;
    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}