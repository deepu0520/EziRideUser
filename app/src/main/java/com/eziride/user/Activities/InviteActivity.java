package com.eziride.user.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.eziride.user.Helper.CustomDialog;
import com.eziride.user.Helper.LocaleUtils;
import com.eziride.user.Helper.SharedHelper;
import com.eziride.user.Helper.URLHelper;
import com.eziride.user.R;
import com.eziride.user.RideApplication;
import com.eziride.user.Utils.Utilities;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.eziride.user.RideApplication.trimMessage;


public class InviteActivity extends AppCompatActivity {

    private static final String TAG = "InviteActivity";
    private TextView referralCode;
    private TextView referralEarning, totalMember;
    private ImageView backArrow;
    private CustomDialog customDialog;
    private ConnectionHelper helper;
    private Utilities utils = new Utilities();
    private String device_UDID;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        helper = new ConnectionHelper(this);
        getToken();
        Button invite = findViewById(R.id.invite);
        referralCode = findViewById(R.id.referral_code);
        referralEarning = findViewById(R.id.referral_earning);
        totalMember = findViewById(R.id.total_member);
        backArrow = findViewById(R.id.backArrow);

        getProfile();
        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteReferral();
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void inviteReferral() {
        String shareRideText = getResources().getString(R.string.hey_check_out)+"\n" + getString(R.string.app_name) + " https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n"+getResources().getString(R.string.my_referal_code)+" " + referralCode.getText().toString();
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareRideText);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (Exception e) {
            Toast.makeText(InviteActivity.this, getResources().getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    public void getProfile() {
        if (helper.isConnectingToInternet()) {
            customDialog = new CustomDialog(InviteActivity.this);
            customDialog.setCancelable(false);
            customDialog.show();
            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.base + URLHelper.UserProfile +
                    "?device_type=android&device_id=" + device_UDID + "&device_token=" + SharedHelper.getKey(InviteActivity.this, "device_token"), object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();

                    referralCode.setText(response.optString("referral_code"));
                    referralEarning.setText(response.optString("referral_earning") != null &&
                            response.optString("referral_earning").length() > 0 ? response.optString("referral_earning") : " - ");
                    totalMember.setText(response.optString("usage_count") != null && !response.optString("usage_count").equalsIgnoreCase("null") &&
                            response.optString("usage_count").length() > 0 ? response.optString("usage_count") : " - ");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
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
                                try {
                                    if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                        //refreshAccessToken();
                                    } else {
                                        displayMessage(errorObj.optString("message"));
                                    }
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }

                            } else if (response.statusCode == 422) {

                                json = trimMessage(new String(response.data));
                                if (!json.equals("") && json != null) {
                                    displayMessage(json);
                                } else {
                                    displayMessage(getString(R.string.please_try_again));
                                }

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
                            getProfile();
                        }
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "" + SharedHelper.getKey(InviteActivity.this, "token_type") + " " + SharedHelper.getKey(InviteActivity.this, "access_token"));
                    return headers;
                }
            };

            RideApplication.getInstance().addToRequestQueue(jsonObjectRequest,5000);
        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }
    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        Toast.makeText(InviteActivity.this, toastString, Toast.LENGTH_SHORT).show();
    }

    private void getToken() {
        try {
            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            utils.print(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            utils.print(TAG, "Failed to complete device UDID");
        }
    }
}
