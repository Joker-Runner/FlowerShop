package com.joker.flowershop.utils;

/**
 * 静态工具常量类
 * Created by joker on 4/11 0011.
 */
public class Constants {

    /**
     * IP 和端口号
     */
    private static final String IP = "123.206.201.169";
    //    private static final String IP = "192.168.31.191";
//    private static final String IP = "10.66.88.105";
    private static final String PORT = "8080";

    // 获取URL(获取到项目根目录)
    public static String getURL() {
        return "http://" + IP + ":" + PORT + "/FlowerShop";
    }

    /**
     * startActivityForResult请求码
     */
    // 点击登录按钮
    public static final int REQUEST_CODE_LOGIN = 0x000001;
    // 注册
    public static final int REQUEST_CODE_SIGN_UP = 0x000002;
    // 修改用户名
//    public static final int REQUEST_CODE_SETTING_USERNAME = 0x000003;
//    // 修改密码
//    public static final int REQUEST_CODE_SETTING_PASSWORD = 0x000004;

    public static final int REQUEST_CODE_SELECT_CITY = 0x000083;

    /**
     * startActivityForResult，登录返回Intent
     */
    // 是否登陆成功
    public static final String LOGGED_IN_INTENT = "logged_in_intent";
    // 登陆成功后用户Bean
    public static final String LOGGED_USER_INTENT = "logged_user_intent";

    /**
     * 初始化设置 SharedPreferences
     */
    public static final String INIT_SETTING_SHARED = "init_sharedPreferences";
    // 是否已登录
    public static final String LOGGED_IN = "logged_in";
    // 登录用户ID
    public static final String LOGGED_USER_ID = "logged_user_id";
    // 登录的用户
    public static final String LOGGED_USER_JSON = "logged_user";
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
