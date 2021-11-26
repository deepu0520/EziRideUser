package com.eziride.user.Activities;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.eziride.user.Helper.LocaleUtils;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.eziride.user.Helper.CustomDialog;
import com.eziride.user.Helper.SharedHelper;
import com.eziride.user.Helper.URLHelper;
import com.eziride.user.Models.CardInfo;
import com.eziride.user.R;
import com.eziride.user.RideApplication;
import com.eziride.user.Utils.MyBoldTextView;
import com.eziride.user.Utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.eziride.user.RideApplication.trimMessage;

public class ActivityWallet extends AppCompatActivity implements View.OnClickListener {

    private final int ADD_CARD_CODE = 435;
    private final int SELECT_CARD = 5555;

    private Button add_fund_button;
    private ProgressDialog loadingDialog;

    private Button add_money_button;
    private EditText money_et;
    private MyBoldTextView balance_tv;
    private Button one, two, three, add_money;
    private double update_amount = 0;
    private ArrayList<CardInfo> cardInfoArrayList;
    private String currency = "";
    private CustomDialog customDialog;
    private Context context;
    private TextView currencySymbol, lblPaymentChange, lblCardNumber;
    private LinearLayout lnrAddmoney, lnrClose, lnrWallet;
    private int selectedPosition = 0;
    Utilities utils = new Utilities();
    private CardInfo cardInfo;
    private ImageView backArrow;
    private String session_token, checksumhash, orderid, mid, cust_id, industry_type, email, channel_id, website, mobile, txn_amount, callback;
    boolean loading;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_wallet);
        cardInfoArrayList = new ArrayList<>();
        add_fund_button = (Button) findViewById(R.id.add_fund_button);
        add_money = (Button) findViewById(R.id.add_money);
        balance_tv = (MyBoldTextView) findViewById(R.id.balance_tv);
        currencySymbol = (TextView) findViewById(R.id.currencySymbol);

        context = this;
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);

        currencySymbol.setText(SharedHelper.getKey(context, "currency"));
        money_et = (EditText) findViewById(R.id.money_input);
        lblPaymentChange = (TextView) findViewById(R.id.lblPaymentChange);
        lblCardNumber = (TextView) findViewById(R.id.lblCardNumber);
        lnrClose = (LinearLayout) findViewById(R.id.lnrClose);
        lnrAddmoney = (LinearLayout) findViewById(R.id.lnrAddmoney);
        lnrWallet = (LinearLayout) findViewById(R.id.lnrWallet);
        one = (Button) findViewById(R.id.one);
        two = (Button) findViewById(R.id.two);
        three = (Button) findViewById(R.id.three);
        add_money = (Button) findViewById(R.id.add_money);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
       /* one.setText(SharedHelper.getKey(context, "currency") + "199");
        two.setText(SharedHelper.getKey(context, "currency") + "599");
        three.setText(SharedHelper.getKey(context, "currency") + "1099");*/

//        add_money.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //lnrAddmoney.setVisibility(View.VISIBLE);
//            }
//        });

        lblPaymentChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cardInfoArrayList.size() > 0) {
                    showChooser();
                } else {
                    gotoAddCard();
                }
            }
        });

        lblCardNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cardInfoArrayList.size() <= 0) {
                    gotoAddCard();
                }
            }
        });

        lnrClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lnrAddmoney.setVisibility(View.GONE);
            }
        });

        lnrWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        add_money.setOnClickListener(this);
        add_fund_button.setOnClickListener(this);
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setIndeterminate(true);
        loadingDialog.setMessage(context.getResources().getString(R.string.please_wait));

        session_token = SharedHelper.getKey(this, "access_token");

        getBalance();
        getCards(false);
    }

    private void getBalance() {
        if ((customDialog != null))
            customDialog.show();
        Ion.with(this)
                .load(URLHelper.base + URLHelper.getUserProfileUrl)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(ActivityWallet.this, "token_type") + " " + session_token)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> response) {
                        // response contains both the headers and the string result
                        if ((customDialog != null) && customDialog.isShowing())
                            customDialog.dismiss();
                        if (e != null) {
                            if (e instanceof TimeoutException) {
                                displayMessage(context.getResources().getString(R.string.please_try_again));
                            }
                            if (e instanceof NetworkErrorException) {
                                getBalance();
                            }
                            return;
                        }
                        if (response != null) {
                            if (response.getHeaders().code() == 200) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.getResult());
                                    currency = jsonObject.optString("currency");
                                    balance_tv.setText(jsonObject.optString("currency") + jsonObject.optString("wallet_balance"));
                                    SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            } else {
                                if ((customDialog != null) && customDialog.isShowing())
                                    customDialog.dismiss();
                                if (response.getHeaders().code() == 401) {
                                    refreshAccessToken("GET_BALANCE");
                                }
                            }
                        } else {

                        }
                    }
                });
    }

    private void refreshAccessToken(final String tag) {

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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.base + URLHelper.login, object, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                utils.print("SignUpResponse", response.toString());
                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                if (tag.equalsIgnoreCase("GET_BALANCE")) {
                    getBalance();
                } else if (tag.equalsIgnoreCase("GET_CARDS")) {
                    getCards(loading);
                } else {
                    addMoney(cardInfo);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = "";
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    SharedHelper.putKey(context, "loggedIn", context.getResources().getString(R.string.False));
                    utils.GoToBeginActivity(ActivityWallet.this);
                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        refreshAccessToken(tag);
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

        RideApplication.getInstance().addToRequestQueue(jsonObjectRequest,5000);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (lnrAddmoney.getVisibility() == View.VISIBLE) {
            lnrAddmoney.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    private void getCards(final boolean showLoading) {
        loading = showLoading;
        if (loading) {
            if (customDialog != null)
                customDialog.show();
        }
        Ion.with(this)
                .load(URLHelper.base + URLHelper.CARD_PAYMENT_LIST)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(ActivityWallet.this, "token_type") + " " + session_token)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> response) {
                        // response contains both the headers and the string result
                        if (response != null) {
                            if (showLoading) {
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();
                            }
                            if (e != null) {
                                if (e instanceof TimeoutException) {
                                    displayMessage(context.getResources().getString(R.string.please_try_again));
                                }
                                if (e instanceof NetworkErrorException) {
                                    getCards(showLoading);
                                }
                                return;
                            }
                            if (response.getHeaders().code() == 200) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response.getResult());
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject cardObj = jsonArray.getJSONObject(i);
                                        CardInfo card_Info = new CardInfo();
                                        card_Info.setCardId(cardObj.optString("card_id"));
                                        card_Info.setCardType(cardObj.optString("brand"));
                                        card_Info.setLastFour(cardObj.optString("last_four"));
                                        cardInfoArrayList.add(card_Info);

                                        if (i == 0) {
                                            lblCardNumber.setText("XXXX-XXXX-XXXX-" + card_Info.getLastFour());
                                            cardInfo = card_Info;
                                        }
                                    }

                                    if (showLoading) {
                                        if (cardInfoArrayList.size() > 0) {
                                            showChooser();
                                        }
                                    }
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            } else {
                                if (response.getHeaders().code() == 401) {
                                    refreshAccessToken("GET_CARDS");
                                }
                            }
                        }
                    }
                });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_fund_button:
                lnrAddmoney.setVisibility(View.VISIBLE);
                break;
            case R.id.add_money:
                if (money_et.getText().toString().isEmpty()) {
                    update_amount = 0;
                    Toast.makeText(this, getResources().getString(R.string.enter_amout), Toast.LENGTH_SHORT).show();
                } else {
                    update_amount = Double.parseDouble(money_et.getText().toString());


                    paytmchecksumgenerator();
                }
                break;

            case R.id.one:
                one.setBackground(getResources().getDrawable(R.drawable.border_stroke_black));
                two.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                three.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                money_et.setText("199");
                break;
            case R.id.two:
                one.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                two.setBackground(getResources().getDrawable(R.drawable.border_stroke_black));
                three.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                money_et.setText("599");
                break;
            case R.id.three:
                one.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                two.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                three.setBackground(getResources().getDrawable(R.drawable.border_stroke_black));
                money_et.setText("1099");
                break;
        }
    }

    private void gotoAddCard() {
        Intent mainIntent = new Intent(this, AddCard.class);
        startActivityForResult(mainIntent, ADD_CARD_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_CARD_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                boolean result = data.getBooleanExtra("isAdded", false);
                if (result) {
                    getCards(true);
                }
            }
        }
        if (requestCode == SELECT_CARD) {
            if (resultCode == Activity.RESULT_OK) {
                CardInfo cardInfo = data.getParcelableExtra("card_info");
                addMoney(cardInfo);
            }
        }
    }

    private void showChooser() {

        final String[] cardsList = new String[cardInfoArrayList.size()];

        for (int i = 0; i < cardInfoArrayList.size(); i++) {
            cardsList[i] = "XXXX-XXXX-XXXX-" + cardInfoArrayList.get(i).getLastFour();
        }

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle(context.getResources().getString(R.string.add_money_using));
        builderSingle.setSingleChoiceItems(cardsList, selectedPosition, null);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                R.layout.custom_tv);

        for (int j = 0; j < cardInfoArrayList.size(); j++) {
            String card = "";
            card = "XXXX-XXXX-XXXX-" + cardInfoArrayList.get(j).getLastFour();
            arrayAdapter.add(card);
        }
        builderSingle.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                Log.e("Items clicked===>", "" + selectedPosition);
                cardInfo = cardInfoArrayList.get(selectedPosition);
                lblCardNumber.setText("XXXX-XXXX-XXXX-" + cardInfo.getLastFour());
//                addMoney(cardInfoArrayList.get(selectedPosition));
            }
        });
        builderSingle.setNegativeButton(
                getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
//        builderSingle.setAdapter(
//                arrayAdapter,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        addMoney(cardInfoArrayList.get(which));
//                    }
//                });
        builderSingle.show();
    }

    private void addMoney(final CardInfo cardInfo) {
        if (customDialog != null)
            customDialog.show();

        JsonObject json = new JsonObject();
        json.addProperty("card_id", cardInfo.getCardId());
        json.addProperty("amount", money_et.getText().toString());

        Ion.with(this)
                .load(URLHelper.base + URLHelper.addCardUrl)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(ActivityWallet.this, "token_type") + " " + session_token)
                .setJsonObjectBody(json)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> response) {
                        // response contains both the headers and the string result

                        if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();

                        if (e != null) {
                            if (e instanceof TimeoutException) {
                                displayMessage(context.getResources().getString(R.string.please_try_again));
                            }
                            if (e instanceof NetworkErrorException) {
                                addMoney(cardInfo);
                            }
                            return;
                        }

                        if (response.getHeaders().code() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.getResult());
                                Toast.makeText(ActivityWallet.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                                JSONObject userObj = jsonObject.getJSONObject("user");
                                balance_tv.setText(currency + userObj.optString("wallet_balance"));
                                SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                                money_et.setText("");
                                lnrAddmoney.setVisibility(View.GONE);
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                            try {
                                if (response != null && response.getHeaders() != null) {
                                    if (response.getHeaders().code() == 401) {
                                        refreshAccessToken("ADD_MONEY");
                                    }
                                }
                            } catch (Exception exception) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        Toast.makeText(context, toastString, Toast.LENGTH_SHORT).show();
    }

    public void paytmchecksumgenerator() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("amount", update_amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.GENERATECHECKSUM, object, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("PaytmchecksumResponse", response.toString());
                customDialog.dismiss();
                checksumhash = response.optString("CHECKSUMHASH");
                orderid = response.optString("ORDER_ID");
                mid = response.optString("MID");
                cust_id = response.optString("CUST_ID");
                industry_type = response.optString("INDUSTRY_TYPE_ID");
                channel_id = response.optString("CHANNEL_ID");
                txn_amount = response.optString("TXN_AMOUNT");
                mobile = response.optString("MOBILE_NO");
                //email = response.optString("EMAIL");
                website = response.optString("WEBSITE");
                callback = response.optString("CALLBACK_URL");
                if (!checksumhash.equalsIgnoreCase("")) {

                    onStartTransaction();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("error"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                            //StatusHandler();
                        } else if (response.statusCode == 401) {
                            //refreshAccessToken("SEND_REQUEST");
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }
                            //StatusHandler();
                        } else if (response.statusCode == 503) {
                            displayMessage(getString(R.string.server_down));
                            //StatusHandler();
                        } else {
                            displayMessage(getString(R.string.please_try_again));
                            //StatusHandler();
                        }

                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                        //StatusHandler();
                    }

                } else {
                    displayMessage(getString(R.string.please_try_again));
                    //StatusHandler();
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

        RideApplication.getInstance().addToRequestQueue(jsonObjectRequest,5000);
    }


    public void onStartTransaction() {
        PaytmPGService Service = PaytmPGService.getStagingService();
        Map<String, String> paramMap = new HashMap<String, String>();


        paramMap.put("MID", mid);
        paramMap.put("ORDER_ID", orderid);
        paramMap.put("CUST_ID", cust_id);
        paramMap.put("INDUSTRY_TYPE_ID", industry_type);
        paramMap.put("CHANNEL_ID", channel_id);
        //paramMap.put("TXN_AMOUNT" , paytm_amount);
        paramMap.put("TXN_AMOUNT", txn_amount);
        //paramMap.put("MOBILE_NO", mobile);
        paramMap.put("WEBSITE", website);
        //paramMap.put("EMAIL", email);
        paramMap.put("CALLBACK_URL", callback);
        paramMap.put("CHECKSUMHASH", checksumhash);
        PaytmOrder Order = new PaytmOrder(paramMap);
        Log.v("Paytmrequest", paramMap.toString());


        Service.initialize(Order, null);

        Service.startPaymentTransaction(ActivityWallet.this, true, true,
                new PaytmPaymentTransactionCallback() {

                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {

                    }

                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                        Log.d("LOG", "Payment Transaction : " + inResponse);
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.transaction_resopnse) + inResponse.toString(), Toast.LENGTH_LONG).show();
                        paytmresponse();
                    }

                    @Override
                    public void networkNotAvailable() {
                        // If network is not
                        // available, then this
                        // method gets called.
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        // This method gets called if client authentication
                        // failed. // Failure may be due to following reasons //
                        // 1. Server error or downtime. // 2. Server unable to
                        // generate checksum or checksum response is not in
                        // proper format. // 3. Server failed to authenticate
                        // that client. That is value of payt_STATUS is 2. //
                        // Error Message describes the reason for failure.
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {

                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.payment_failed), Toast.LENGTH_LONG).show();
                    }

                });
    }


    public void paytmresponse() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();

        JSONObject object = new JSONObject();
        try {
            //object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("ORDERID", orderid);
            Log.e("paytmresponse: ", "" + object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.PAYTMRESPONSE, object, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject message) {
                Log.v("PaytmtxnResponse", "" + message.toString());
                customDialog.dismiss();
                Toast.makeText(ActivityWallet.this, message.optString("message"), Toast.LENGTH_SHORT).show();
                getBalance();
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("error"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                            //StatusHandler();
                        } else if (response.statusCode == 401) {
                            //refreshAccessToken("SEND_REQUEST");
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }
                            //StatusHandler();
                        } else if (response.statusCode == 503) {
                            displayMessage(getString(R.string.server_down));
                            //StatusHandler();
                        } else {
                            displayMessage(getString(R.string.please_try_again));
                            //StatusHandler();
                        }

                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                        //StatusHandler();
                    }

                } else {
                    displayMessage(getString(R.string.please_try_again));
                    //StatusHandler();
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

        RideApplication.getInstance().addToRequestQueue(jsonObjectRequest,5000);
    }

}
