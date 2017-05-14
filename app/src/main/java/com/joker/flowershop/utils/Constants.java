package com.joker.flowershop.utils;

/**
 * 静态工具常量类
 * Created by joker on 4/11 0011.
 */
public class Constants {

    /**
     * IP 和端口号
     */
//    private static final String IP = "123.206.201.169";
    private static final String IP = "192.168.31.191";
    private static final String PORT = "8080";

    // 获取URL(获取到项目根目录)
    public static String getURL() {
        return "http://" + IP + ":" + PORT + "/FlowerShop";
    }

    /**
     * 请求码
     */
    public static final int REQUEST_CODE_LOGIN = 347;


    /**
     * 初始化设置 SharedPreferences
     */
    public static final String INIT_SETTING_SHARED = "init_sharedPreferences";
    // 是否已登录
    public static final String LOGGED_IN = "logged_in";
    // 登录用户ID
    public static final String LOGGED_USER_ID = "logged_user_id";
    // 默认收货地址Json
    public static final String DEFAULT_RECEIVER = "default_receiver";

    /**
     * 订单状态
     */
    // 待发货
    public static final int WAITING_FOR_DELIVERY = 1;
    // 已发货
    public static final int DELIVERED = 2;
    // 已收货
    public static final int ARRIVED = 3;

}
