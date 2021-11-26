package com.eziride.user.Helper;

import android.os.Environment;

public class URLHelper {
    //public static final String base = "http://ride.venturedemos.com/";

    //developer server url
//    public static final String base = "https://projects.thesparxitsolutions.com/SIS545/public/";
//    public static final String avatar_base_url = "https://projects.thesparxitsolutions.com/SIS545/public/storage/";

    //live server url
    public static final String base = "http://eziride.com.au/admin/";
    public static final String avatar_base_url = "http://eziride.com.au/admin/public/storage/";


    public static final String access_url = "http://ride.venturedemos.com/";
    public static final String map_address_url = "https://maps.googleapis.com/maps/api/geocode/";
    //public static final String base = "http://6586b14b.ngrok.io/";
    public static final String REDIRECT_URL = base;
    public static final String REDIRECT_SHARE_URL = "http://maps.google.com/maps?q=loc:";
    public static final String APP_URL = "https://play.google.com/store/apps/details?id=";
    public static final int client_id = 2;
    public static final String client_secret = "CdIp5wWKfqxD8qLYARKqSCypc7HCpXzZlSdnjLSn";

    public static final String login = "oauth/token";
    public static final String register = "api/user/signup";
    public static final String UserProfile = "api/user/details";
    public static final String CHECK_MAIL_ALREADY_REGISTERED = "api/user/verify";
    public static final String UseProfileUpdate = "api/user/update/profile";
    public static final String getUserProfileUrl = "api/user/details";
    public static final String GET_SERVICE_LIST_API = "api/user/services";
    public static final String REQUEST_STATUS_CHECK_API = "api/user/request/check";
    public static final String ESTIMATED_FARE_DETAILS_API = "api/user/estimated/fare";
    public static final String SEND_REQUEST_API = "api/user/send/request";
    public static final String CANCEL_REQUEST_API = "api/user/cancel/request";
    public static final String PAY_NOW_API = "api/user/payment";
    public static final String RATE_PROVIDER_API = "api/user/rate/provider";
    public static final String CARD_PAYMENT_LIST = "api/user/card";
    public static final String ADD_CARD_TO_ACCOUNT_API = "api/user/card";
    public static final String DELETE_CARD_FROM_ACCOUNT_API = "api/user/card/destory";
    public static final String GET_HISTORY_API = "api/user/trips";
    public static final String GET_HISTORY_DETAILS_API = "api/user/trip/details";
    public static final String addCardUrl = "api/user/add/money";
    public static final String COUPON_LIST_API = "api/user/promocodes";
    public static final String ADD_COUPON_API = "api/user/promocode/add";
    public static final String CHANGE_PASSWORD_API = "api/user/change/password";
    public static final String UPCOMING_TRIP_DETAILS = "api/user/upcoming/trip/details";
    public static final String UPCOMING_TRIPS = "api/user/upcoming/trips";
    public static final String GET_PROVIDERS_LIST_API = "api/user/show/providers";
    public static final String FORGET_PASSWORD = "api/user/forgot/password";
    public static final String RESET_PASSWORD = "api/user/reset/password";
    public static final String FACEBOOK_LOGIN = "api/user/auth/facebook";
    public static final String GOOGLE_LOGIN = "api/user/auth/google";
    public static final String LOGOUT = "api/user/logout";
    public static final String HELP = "api/user/help";
    public static final String GET_WALLET_HISTORY = "api/user/wallet/passbook";
    public static final String GET_COUPON_HISTORY = "api/user/promo/passbook";
    public static final String EXTEND_TRIP = "api/user/update/request";
    public static final String SAVE_LOCATION = "api/user/location";
    public static final String REASONLIST = "api/user/commentreason";
    public static final String PAYMENT_TYPE = "api/user/cash-detail";
    public static final String SMS_GATEWAY_DETAIL = "api/user/sms-detail";

    /*________For_PayTM__________*/
    public static String GENERATECHECKSUM = base + "api/user/paytm/generatechecksum";
    public static String PAYTMRESPONSE = base + "api/user/paytm/statusupdate";

    /*_________________For file storage_________________*/
    public static String filePath = Environment.getExternalStorageDirectory().toString() + "/EziRideUser/";
}
