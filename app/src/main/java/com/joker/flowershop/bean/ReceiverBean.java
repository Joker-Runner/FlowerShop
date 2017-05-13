package com.joker.flowershop.bean;

/**
 * Created by joker on 5/13 0013.
 */

public class ReceiverBean {
    private String addressee;
    private String telephone;
    private String address;

    public ReceiverBean(String addressee, String telephone, String address) {
        this.addressee = addressee;
        this.telephone = telephone;
        this.address = address;
    }

    public String getAddressee() {
        return addressee;
    }

    public void setAddressee(String addressee) {
        this.addressee = addressee;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
