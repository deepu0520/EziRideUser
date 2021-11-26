package com.eziride.user.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.eziride.user.BuildConfig;
import com.eziride.user.Helper.ConnectionHelper;
import com.eziride.user.Helper.LocaleUtils;
import com.eziride.user.Helper.SharedHelper;
import com.eziride.user.Helper.URLHelper;
import com.eziride.user.Models.AccessDetails;
import com.eziride.user.R;
import com.eziride.user.RideApplication;
import com.eziride.user.Utils.Utilities;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.eziride.user.RideApplication.trimMessage;


public class SplashScreen extends AppCompatActivity {

    String TAG = "SplashActivity";
    public Activity activity = SplashScreen.this;
    public Context context = SplashScreen.this;
    ConnectionHelper helper;
    Boolean isInternet;
    String device_token, device_UDID;
    Handler handleCheckStatus;
    int retryCount = 0;
    AlertDialog alert;
    TextView lblVersion;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        //ForceUpdateChecker.with(this).onUpdateNeeded(this).check();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();
        lblVersion = (TextView) findViewById(R.id.lblVersion);
        Log.v("VERSION NAME", "" + BuildConfig.VERSION_NAME);
        Log.v("VERSION CODE", "" + BuildConfig.VERSION_CODE);
        lblVersion.setText(getResources().getString(R.string.version) + " " + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")");

        checkPaymentType();

//        WebView view = (WebView) findViewById(R.id.myWebView);
//        view.getSettings().setLoadWithOverviewMode(true);
//        view.getSettings().setUseWideViewPort(true);
//        view.setBackgroundColor(0x00000000);
//        view.loadUrl("file:///android_asset/driver_animation.gif");

//        getAccess();
        handleCheckStatus = new Handler();
        // check status every 3 sec


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        handleCheckStatus.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.w("Handler", "Called");
                if (helper.isConnectingToInternet()) {
                    if (SharedHelper.getKey(context, "loggedIn").equalsIgnoreCase(context.getResources().getString(R.string.True))) {
                        GetToken();
                        GoToMainActivity();
                    } else {
                        GoToBeginActivity();
                        handleCheckStatus.removeCallbacksAndMessages(null);
                    }
                    if (alert != null && alert.isShowing()) {
                        alert.dismiss();
                    }
                } else {
                    showDialog();
                    handleCheckStatus.postDelayed(this, 3000);
                }
            }
        }, 3000);
//        handleCheckStatus.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Log.w("Handler", "Called");
//                if (helper.isConnectingToInternet()) {
//                    if (SharedHelper.getKey(context, "loggedIn").equalsIgnoreCase(context.getResources().getString(R.string.True))) {
//                        GetToken();
//                        accessKeyAPI();
//                    } else {
//                        if (AccessDetails.demo_build) {
//                            if (SharedHelper.getKey(SplashScreen.this, "access_username").equalsIgnoreCase("") && SharedHelper.getKey(SplashScreen.this, "access_password").equalsIgnoreCase("")) {
//                                GoToAccessActivity();
//                            } else {
//                                accessKeyAPI();
//                            }
//                        } else {
//                            accessKeyAPI();
//                        }
//                        handleCheckStatus.removeCallbacksAndMessages(null);
//                    }
//                    if (alert != null && alert.isShowing()) {
//                        alert.dismiss();
//                    }
//                } else {
//                    showDialog();
//                    handleCheckStatus.postDelayed(this, 3000);
//                }
//            }
//        }, 3000);
    }

    public void accessKeyAPI() {

        JSONObject object = new JSONObject();
        try {
            if (AccessDetails.demo_build) {
                object.put("username", SharedHelper.getKey(SplashScreen.this, "access_username"));
                object.put("accesskey", SharedHelper.getKey(SplashScreen.this, "access_password"));
            } else {
                object.put("username", AccessDetails.username);
                object.put("accesskey", AccessDetails.password);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, AccessDetails.access_login, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                processResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            displayMessage(errorObj.optString("message"));
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            displayMessage(getString(R.string.server_down));
                        } else {
                            displayMessage(getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                    }

                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        displayMessage(getString(R.string.timed_out));
                    }
                }
                GoToAccessActivity();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        RideApplication.getInstance().addToRequestQueue(jsonObjectRequest, 5000);
    }

    private void processResponse(final JSONObject response) {
        try {
            //AccessDetails accessDetails = new AccessDetails();
            AccessDetails.status = response.optBoolean("status");

            if (AccessDetails.status) {
                JSONArray jsonArrayData = response.optJSONArray("data");

                JSONObject jsonObjectData = jsonArrayData.optJSONObject(0);
                AccessDetails.id = jsonObjectData.optInt("id");
                AccessDetails.clientName = jsonObjectData.optString("client_name");
                AccessDetails.email = jsonObjectData.optString("email");
                AccessDetails.product = jsonObjectData.optString("product");
                AccessDetails.username = jsonObjectData.optString("username");
                SharedHelper.putKey(SplashScreen.this, "access_username", AccessDetails.username);
                AccessDetails.password = jsonObjectData.optString("password");
                SharedHelper.putKey(SplashScreen.this, "access_password", AccessDetails.password);
                AccessDetails.passport = jsonObjectData.optString("passport");
                AccessDetails.clientid = jsonObjectData.optInt("clientid");
                AccessDetails.serviceurl = jsonObjectData.optString("serviceurl");
                AccessDetails.isActive = jsonObjectData.optInt("is_active");
                AccessDetails.createdAt = jsonObjectData.optString("created_at");
                AccessDetails.updatedAt = jsonObjectData.optString("updated_at");
                AccessDetails.isPaid = jsonObjectData.optInt("is_paid");
                AccessDetails.isValid = jsonObjectData.optInt("is_valid");

                JSONObject jsonObjectSettings = response.optJSONObject("setting");

                AccessDetails.siteTitle = jsonObjectSettings.optString("site_title");
                SharedHelper.putKey(SplashScreen.this, "app_name", AccessDetails.siteTitle);
                AccessDetails.siteLogo = jsonObjectSettings.optString("site_logo");
                AccessDetails.siteEmailLogo = jsonObjectSettings.optString("site_email_logo");
                AccessDetails.siteIcon = jsonObjectSettings.optString("site_icon");
                AccessDetails.site_icon = Utilities.drawableFromUrl(SplashScreen.this, AccessDetails.siteIcon);
                AccessDetails.siteCopyright = jsonObjectSettings.optString("site_copyright");
                AccessDetails.providerSelectTimeout = jsonObjectSettings.optString("provider_select_timeout");
                AccessDetails.providerSearchRadius = jsonObjectSettings.optString("provider_search_radius");
                AccessDetails.basePrice = jsonObjectSettings.optString("base_price");
                AccessDetails.pricePerMinute = jsonObjectSettings.optString("price_per_minute");
                AccessDetails.taxPercentage = jsonObjectSettings.optString("tax_percentage");
                AccessDetails.stripeSecretKey = jsonObjectSettings.optString("stripe_secret_key");
                AccessDetails.stripePublishableKey = jsonObjectSettings.optString("stripe_publishable_key");
                AccessDetails.cash = jsonObjectSettings.optString("CASH");
                AccessDetails.card = jsonObjectSettings.optString("CARD");
                AccessDetails.manualRequest = jsonObjectSettings.optString("manual_request");
                AccessDetails.defaultLang = jsonObjectSettings.optString("default_lang");
                AccessDetails.currency = jsonObjectSettings.optString("currency");
                AccessDetails.distance = jsonObjectSettings.optString("distance");
                AccessDetails.scheduledCancelTimeExceed = jsonObjectSettings.optString("scheduled_cancel_time_exceed");
                AccessDetails.pricePerKilometer = jsonObjectSettings.optString("price_per_kilometer");
                AccessDetails.commissionPercentage = jsonObjectSettings.optString("commission_percentage");
                AccessDetails.storeLinkAndroid = jsonObjectSettings.optString("store_link_android");
                AccessDetails.storeLinkIos = jsonObjectSettings.optString("store_link_ios");
                AccessDetails.dailyTarget = jsonObjectSettings.optString("daily_target");
                AccessDetails.surgePercentage = jsonObjectSettings.optString("surge_percentage");
                AccessDetails.surgeTrigger = jsonObjectSettings.optString("surge_trigger");
                AccessDetails.demoMode = jsonObjectSettings.optString("demo_mode");
                AccessDetails.bookingPrefix = jsonObjectSettings.optString("booking_prefix");
                AccessDetails.sosNumber = jsonObjectSettings.optString("sos_number");
                AccessDetails.contactNumber = jsonObjectSettings.optString("contact_number");
                AccessDetails.contactEmail = jsonObjectSettings.optString("contact_email");
                AccessDetails.socialLogin = jsonObjectSettings.optString("social_login");

                if (AccessDetails.isValid == 1) {
                    if (SharedHelper.getKey(context, "loggedIn").equalsIgnoreCase(context.getResources().getString(R.string.True))) {
                        getProfile();
                    } else {
                        GoToBeginActivity();
                    }
                } else {
                    displayMessage(getResources().getString(R.string.demo_expired));
                }

            } else {
                displayMessage(response.optString("message"));

                if (AccessDetails.demo_build) {
                    GoToAccessActivity();
                } else {
                    GoToBeginActivity();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void getProfile() {
        retryCount++;
        Log.e("GetPostAPI", "" + URLHelper.base + URLHelper.UserProfile + "?device_type=android&device_id=" + device_UDID + "&device_token=" + device_token);
        JSONObject object = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.base + URLHelper.UserProfile + "" +
                "?device_type=android&device_id=" + device_UDID + "&device_token=" + device_token, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                SharedHelper.putKey(context, "id", response.optString("id"));
                SharedHelper.putKey(context, "first_name", response.optString("first_name"));
                SharedHelper.putKey(context, "last_name", response.optString("last_name"));
                SharedHelper.putKey(context, "email", response.optString("email"));
                if (response.optString("picture").startsWith("http"))
                    SharedHelper.putKey(context, "picture", response.optString("picture"));
                else
                    SharedHelper.putKey(context, "picture", URLHelper.base + "/storage/" + response.optString("picture"));
                SharedHelper.putKey(context, "gender", response.optString("gender"));
                SharedHelper.putKey(context, "mobile", response.optString("mobile"));
                SharedHelper.putKey(context, "wallet_balance", response.optString("wallet_balance"));
                SharedHelper.putKey(context, "payment_mode", response.optString("payment_mode"));
                if (response.optString("payment_mode") != null && response.optString("payment_mode").equalsIgnoreCase("CARD")) {
                    try {
                        JSONObject cardObjectt = response.getJSONObject("card");
                        SharedHelper.putKey(context, "card_id", cardObjectt.optString("card_id"));
                        SharedHelper.putKey(context, "card_num", "XXXX-XXXX-XXXX-" + cardObjectt.optString("last_four"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                if (!response.optString("currency").equalsIgnoreCase("") && response.optString("currency") != null)
                    SharedHelper.putKey(context, "currency", response.optString("currency"));
                else
                    SharedHelper.putKey(context, "currency", "$");
                SharedHelper.putKey(context, "sos", response.optString("sos"));
                SharedHelper.putKey(context, "cancelation_charge", response.optString("cancellation_fare"));
                Log.e(TAG, "onResponse: Sos Call" + response.optString("sos"));
                SharedHelper.putKey(context, "loggedIn", context.getResources().getString(R.string.True));
                GoToMainActivity();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (retryCount < 5) {
                    getProfile();
                } else if (retryCount >= 5) {
//                        if (AccessDetails.username.equalsIgnoreCase("") && AccessDetails.passport.equalsIgnoreCase("")){
//                            GoToAccessActivity();
//                        }else{
                    GoToBeginActivity();
//                        }
                }
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(context.getResources().getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            refreshAccessToken();
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(context.getResources().getString(R.string.please_try_again));
                            }

                        } else if (response.statusCode == 503) {
                            displayMessage(context.getResources().getString(R.string.server_down));
                        }
                    } catch (Exception e) {
                        displayMessage(context.getResources().getString(R.string.something_went_wrong));
                    }

                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        getProfile();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        RideApplication.getInstance().addToRequestQueue(jsonObjectRequest, 5000);
    }


    @Override
    protected void onDestroy() {
        handleCheckStatus.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void refreshAccessToken() {
        JSONObject object = new JSONObject();
        try {
            object.put("grant_type", "refresh_token");
            object.put("client_id", URLHelper.client_id);
            object.put("client_secret", URLHelper.client_secret);
            object.put("refresh_token", SharedHelper.getKey(context, "refresh_token"));
            object.put("scope", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GetToken();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.base + URLHelper.login, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("SignUpResponse", response.toString());
                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                getProfile();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    SharedHelper.putKey(context, "loggedIn", context.getResources().getString(R.string.False));
//                        if (AccessDetails.username.equalsIgnoreCase("") && AccessDetails.passport.equalsIgnoreCase("")){
//                            GoToAccessActivity();
//                        }else{
                    GoToBeginActivity();
//                        }
                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        refreshAccessToken();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        RideApplication.getInstance().addToRequestQueue(jsonObjectRequest, 5000);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void GoToMainActivity() {
        Intent mainIntent = new Intent(activity, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(activity, BeginScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        activity.finish();
    }

    public void GoToAccessActivity() {
        Intent mainIntent = new Intent(activity, AccessKeyActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        activity.finish();
    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        Toast.makeText(activity, toastString, Toast.LENGTH_SHORT).show();
    }

    public void GetToken() {
        try {
            if (!SharedHelper.getKey(context, "device_token").equals("") && SharedHelper.getKey(context, "device_token") != null) {
                device_token = SharedHelper.getKey(context, "device_token");
                Log.i(TAG, "GCM Registration Token: " + device_token);
            } else {
                device_token = "" + FirebaseInstanceId.getInstance().getToken();
                SharedHelper.putKey(context, "device_token", "" + FirebaseInstanceId.getInstance().getToken());
                Log.i(TAG, "Failed to complete token refresh: " + device_token);
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            Log.d(TAG, "Failed to complete token refresh", e);
        }

        try {
            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            Log.i(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            Log.d(TAG, "Failed to complete device UDID");
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(context.getResources().getString(R.string.connect_to_network))
                .setCancelable(false)
                .setPositiveButton(context.getResources().getString(R.string.connect_to_wifi), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton(context.getResources().getString(R.string.quit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        if (alert == null) {
            alert = builder.create();
            alert.show();
        }
    }

//
//    @Override
//    public void onUpdateNeeded(final String updateUrl) {
//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setTitle(getResources().getString(R.string.new_version_available))
//                .setMessage(getResources().getString(R.string.update_to_continue))
//                .setPositiveButton(getResources().getString(R.string.update),
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                redirectStore(updateUrl);
//                            }
//                        }).setNegativeButton(getResources().getString(R.string.no_thanks),
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
//                            }
//                        }).create();
//        dialog.show();
//    }
//
//    private void redirectStore(String updateUrl) {
//        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//    }

    private void checkPaymentType() {
        try {

            //  if (isInternet) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.base + URLHelper.PAYMENT_TYPE, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Utilities.print("checkPaymentType Response11", "checkPaymentType checkPaymentType @@@@" + response.toString());
                    String cashAcceptStatus = response.optString("value").equalsIgnoreCase("1") ? "true" : "false";
                    Utilities.print("checkPaymentType Response11", "checkPaymentType data @@@@" + cashAcceptStatus);
                    SharedHelper.putKey(context, "isCashAccept", cashAcceptStatus);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    SharedHelper.putKey(context, "isCashAccept", "true");
                }
            });

            RideApplication.getInstance().addToRequestQueue(jsonObjectRequest, 1000);
            //  }

        } catch (Exception e) {
            SharedHelper.putKey(context, "isCashAccept", "true");
            Log.e("checkPayment() error", "@@ " + e.getMessage());
        }
    }


}
