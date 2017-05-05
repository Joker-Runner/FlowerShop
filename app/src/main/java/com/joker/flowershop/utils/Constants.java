package com.joker.flowershop.utils;

/**
 * 静态工具常量类
 * Created by joker on 4/11 0011.
 */
public class Constants {

    public static final String IP = "123.206.201.169";
//    public static final String IP = "192.168.31.191";
    public static final String PORT = "8080";

    /**
     * 获取URL(获取到项目根目录)
     *
     * @return 返回URL
     */
    public static String getURL(){
        return "http://"+ IP+":"+PORT+"/FlowerShop";
    }
}
