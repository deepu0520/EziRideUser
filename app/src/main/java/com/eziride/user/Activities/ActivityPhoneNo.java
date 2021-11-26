package com.eziride.user.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.eziride.user.Helper.CustomDialog;
import com.eziride.user.Helper.URLHelper;
import com.eziride.user.R;
import com.eziride.user.RideApplication;
import com.eziride.user.Utils.MyEditText;
import com.eziride.user.Utils.MyTextView;
import com.eziride.user.Utils.OtpEntryEditText;
import com.eziride.user.Utils.Utilities;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ClickSend.Api.SmsApi;
import ClickSend.ApiClient;
import ClickSend.ApiException;
import ClickSend.Model.SmsMessage;
import ClickSend.Model.SmsMessageCollection;

public class ActivityPhoneNo extends AppCompatActivity {

    ImageView backArrow;
    LinearLayout viewMobile, viewOtp;
    int viewType = 0; ///0 for mobile,1 for otp
    CountryCodePicker ccp;
    Button sendOtp, submitOtp;
    MyEditText edtContact;
    ImageView editMobileNumber;
    MyTextView txtMobile, resentOtp;
    OtpEntryEditText edtOtp;
    String name = "", countryCode = "0", contactNumber = "0", fullContactNum = "0", otp = "0",userName="martinraco@bigpond.com",apiKey="11D6EDD2-63AC-9B1D-4A30-C1178689FBDD";
    CustomDialog customDialog;

    Utilities utils = new Utilities();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_no);

        backArrow = findViewById(R.id.backArrow);

        viewMobile = findViewById(R.id.viewMobile);
        viewOtp = findViewById(R.id.viewOtp);

        ccp = findViewById(R.id.ccp);
        edtContact = findViewById(R.id.contact_num);
        sendOtp = findViewById(R.id.nextIcon);


        editMobileNumber = findViewById(R.id.editNum);
        txtMobile = findViewById(R.id.numTextView);
        resentOtp = findViewById(R.id.resentOtp);
        edtOtp = findViewById(R.id.et_otp);
        submitOtp = findViewById(R.id.continueIcon);

        name = getIntent().getStringExtra("name");
        if (name == null) {
            name="unknown";
        }

        customDialog = new CustomDialog(ActivityPhoneNo.this);

        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryCode = ccp.getSelectedCountryCodeWithPlus();
                Toast.makeText(ActivityPhoneNo.this, "Updated " + countryCode, Toast.LENGTH_SHORT).show();
            }
        });

        sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.hideKeyboard(ActivityPhoneNo.this);

                contactNumber = edtContact.getText().toString();
                if (countryCode.equalsIgnoreCase("0"))
                    countryCode = ccp.getDefaultCountryCodeWithPlus();

                if (contactNumber.length() < 4) {
                    Toast.makeText(ActivityPhoneNo.this, "Please enter valid contact number ", Toast.LENGTH_SHORT).show();
                    edtContact.requestFocus();
                    viewType = 0;
                    return;

                } else {
                    fullContactNum = countryCode + contactNumber;
                    getSmsGatewayDetail();

                }
            }
        });

        resentOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog = new CustomDialog(ActivityPhoneNo.this);
                customDialog.setCancelable(false);
                if (customDialog != null)
                    customDialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendOtp();
                    }
                },2000);

            }
        });

        submitOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputOtp=edtOtp.getText().toString();
                Log.e("@@@@@@@input", "onClick: submit "+inputOtp );
                if (inputOtp.equalsIgnoreCase(otp)) {
                    Utilities.hideKeyboard(ActivityPhoneNo.this);
                    Intent intent = new Intent();
                    intent.putExtra("mobile", fullContactNum);
                    setResult(100, intent);
                    finish();//finishing activity
                }else{
                    displayMessage("Wrong otp,please enter correct otp");
                    edtOtp.setText("");
                }


            }
        });

        editMobileNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ActivityPhoneNo.this, "Click on edit", Toast.LENGTH_SHORT).show();
                Utilities.hideKeyboard(ActivityPhoneNo.this);

                viewType = 0;
                viewOtp.setVisibility(View.GONE);
                viewMobile.setVisibility(View.VISIBLE);
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.hideKeyboard(ActivityPhoneNo.this);

                if (viewType == 0) {
                    onBackPressed();
                } else {
                    viewType = 0;
                    viewOtp.setVisibility(View.GONE);
                    viewMobile.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    public String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        otp = String.format("%06d", number);
        Log.e("otttppp", "getRandomNumberString: otp is " + otp);
        return otp;
    }

    private void sendOtp() {


        ApiClient defaultClient = new ApiClient();
        defaultClient.setUsername(userName);
        defaultClient.setPassword(apiKey);
        SmsApi apiInstance = new SmsApi(defaultClient);

        String message="Dear " + name + ", your OTP is " + getRandomNumberString() + ".Use this code to activate your Ezi Ride log in.";
        Log.e("@@@@@", "sendOtp: message=="+message );


        SmsMessage smsMessage = new SmsMessage();
        smsMessage.body(message);
        smsMessage.to(fullContactNum);
        smsMessage.source("java");

        List<SmsMessage> smsMessageList = Arrays.asList(smsMessage);
        // SmsMessageCollection | SmsMessageCollection model
        SmsMessageCollection smsMessages = new SmsMessageCollection();
        smsMessages.messages(smsMessageList);
        try {
            String result = apiInstance.smsSendPost(smsMessages);
            System.out.println(result);
            JSONObject jsonObject = new JSONObject(result);
            String status = jsonObject.optString("response_code");
            Log.e("sendOtp: ", "status of otp is " + status);
            Log.e("sendOtp: ", "result of otp is " + result);
            if (status.equalsIgnoreCase("SUCCESS")) {
                viewMobile.setVisibility(View.GONE);
                viewOtp.setVisibility(View.VISIBLE);
                viewType = 1;
                txtMobile.setText(fullContactNum);
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                displayMessage("Otp send to given mobile number");
            } else {
                Toast.makeText(ActivityPhoneNo.this, "Some error occurred, please try again", Toast.LENGTH_SHORT).show();

            }
        } catch (ApiException | JSONException e) {
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            System.err.println("Exception when calling SmsApi#smsSendPost");
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("mobile", "0");
        setResult(100, intent);
        finish();//finishing activity
    }


    public void getSmsGatewayDetail() {
        customDialog = new CustomDialog(ActivityPhoneNo.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.base + URLHelper.SMS_GATEWAY_DETAIL,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("Response", response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    boolean status=jsonObject.optBoolean("status");
                    if (status) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        userName = data.optString("username");
                        apiKey = data.optString("api_key");
                        Log.e("url isssss", "@@@@@@" + data.optString("end_point_url"));
                    }
                    sendOtp();
                } catch (JSONException e) {
                    e.printStackTrace();
                    sendOtp();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                sendOtp();
            }
        });

        RideApplication.getInstance().addToRequestQueue(jsonObjectRequest,500);
    }

    public void displayMessage(String toastString) {
        utils.print("displayMessage", "" + toastString);
        try {

            Snackbar snackbar = Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_LONG).setDuration(Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            tv.setMaxLines(3);
            snackbar.show();

        } catch (Exception e) {
            try {
                Toast.makeText(ActivityPhoneNo.this, "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                e.printStackTrace();
            }
        }
    }
}

