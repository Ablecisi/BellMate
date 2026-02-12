package com.bellmate.constant;

/**
 * ailianlian
 * com.ailianlian.ablecisi.constant
 * SharedPreferencesConstant <br>
 * 登录相关的SharedPreferences常量
 * @author Ablecisi
 * @version 1.0
 * 2025/6/13
 * 星期五
 * 23:08
 */
public class SharedPreferencesConstant {
    /*登录*/
    public static final String LOGIN_PREF_NAME = "login_pref"; // SharedPreferences名称
    public static final String LOGIN_KEY_LOGIN_TYPE = "login_type"; // 登录类型
    public static final String LOGIN_KEY_USERNAME = "username"; // 用户名
    public static final String LOGIN_KEY_TOKEN = "token"; // 用户Token
    public static final String LOGIN_KEY_USER_ID = "user_id"; // 用户ID
    public static final String LOGIN_KEY_AVATAR_URL = "avatar_url"; // 用户头像URL
    public static final String LOGIN_KEY_IS_LOGGED_IN = "is_logged_in"; // 是否已登录
    public static final String LOGIN_NOT_LOGGED_IN = "not_logged_in"; // 未登录标识
    public static final String LOGIN_KEY_NAME = "name"; // 用户昵称

    /*主题 */
    public static final String THEME_PREF_NAME = "app_settings";
    public static final String THEME_KEY_DARK_MODE = "dark_mode";
}
