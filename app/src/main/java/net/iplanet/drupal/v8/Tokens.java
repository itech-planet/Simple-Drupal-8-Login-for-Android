package net.iplanet.drupal.v8;

public class Tokens {
    public static final String APP_NAME = "Simple Drupal 8 Login";
    public static final String APP_VERSION = "v1.0";
    public static final String KEY_LOGIN = "login";
    public static final String KEY_NAME = "name";
    public static final String KEY_PASS = "pass";
    public static final String KEY_CSRF_TOKEN_RESPONSE = "csrf_token";
    public static final String KEY_CSRF_TOKEN_REQUEST = "X-CSRF-Token";
    public static final String KEY_LOGOUT_TOKEN = "logout_token";
    public static final String KEY_AUTHORIZATION = "Authorization";
    public static final String serv_end_Pnt_LOGIN = "http://<DRUPAL-8-APP-URL>/user/login?_format=json";
    public static final String serv_end_Pnt_LOGOUT = "http://<DRUPAL-8-APP-URL>/user/logout";
}
