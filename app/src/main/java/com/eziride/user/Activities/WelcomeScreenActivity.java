package com.eziride.user.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.eziride.user.Helper.LocaleUtils;
import com.eziride.user.Helper.SharedHelper;
import com.eziride.user.Models.AccessDetails;
import com.eziride.user.R;
import com.eziride.user.RideApplication;
import com.eziride.user.Utils.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WelcomeScreenActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    Button loginButton, signUpButton;
    TextView skipBtn;
    LinearLayout social_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome_screen);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        loginButton = (Button) findViewById(R.id.sign_in_btn);
        skipBtn = (TextView) findViewById(R.id.skip);
        signUpButton = (Button) findViewById(R.id.sign_up_btn);
        social_layout = (LinearLayout) findViewById(R.id.social_layout);
        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3
        };
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeScreenActivity.this, ActivityEmail.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
//                finish();
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeScreenActivity.this, RegisterActivity.class).putExtra("signup", true).putExtra("viewpager", "yes").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
//                finish();
            }
        });

        social_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeScreenActivity.this, ActivitySocialLogin.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
//                finish();
            }
        });
        overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
//        skipBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(WelcomeScreenActivity.this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//                overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
//                finish();
//
//            }
//        });
        // adding bottom dots
        addBottomDots(0);
        // making notification bar transparent
        changeStatusBarColor();
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        if (!AccessDetails.demo_build) {
            if (SharedHelper.getKey(WelcomeScreenActivity.this, "access_username").equalsIgnoreCase("") && SharedHelper.getKey(WelcomeScreenActivity.this, "access_password").equalsIgnoreCase("")){
                accessKeyAPI();
            }
        }

    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];
        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
            dots[currentPage].setTextColor(colorsActive[currentPage]);
            dots[currentPage].startAnimation(animation);
        }

    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }


    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) viewPager.getLayoutParams();
//            params.setMargins((int) ((position + positionOffset) * 500), 0, 0, 0);
//            viewPager.setLayoutParams(params);

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

   /* @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
    }*/
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            if (position == 0){
                TextView txtFreshDesc = (TextView) view.findViewById(R.id.txtFreshDesc);
                txtFreshDesc.setText(getResources().getString(R.string.introducing) + " " + AccessDetails.siteTitle + " " +
                        getResources().getString(R.string.fresh_description));
            }
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    public void accessKeyAPI() {

        JSONObject object = new JSONObject();
        try {
            object.put("username", AccessDetails.username);
            object.put("accesskey",AccessDetails.password);
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
                            json = RideApplication.trimMessage(new String(response.data));
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

    private void processResponse(final JSONObject response) {
        try {
            AccessDetails accessDetails = new AccessDetails();
            accessDetails.status = response.optBoolean("status");

            if (accessDetails.status) {
                JSONArray jsonArrayData = response.optJSONArray("data");

                JSONObject jsonObjectData = jsonArrayData.optJSONObject(0);
                accessDetails.id = jsonObjectData.optInt("id");
                accessDetails.clientName = jsonObjectData.optString("client_name");
                accessDetails.email = jsonObjectData.optString("email");
                accessDetails.product = jsonObjectData.optString("product");
                accessDetails.username = jsonObjectData.optString("username");
                SharedHelper.putKey(WelcomeScreenActivity.this, "access_username", accessDetails.username);
                accessDetails.password = jsonObjectData.optString("password");
                SharedHelper.putKey(WelcomeScreenActivity.this, "access_password", accessDetails.password);
                accessDetails.passport = jsonObjectData.optString("passport");
                accessDetails.clientid = jsonObjectData.optInt("clientid");
                accessDetails.serviceurl = jsonObjectData.optString("serviceurl");
                accessDetails.isActive = jsonObjectData.optInt("is_active");
                accessDetails.createdAt = jsonObjectData.optString("created_at");
                accessDetails.updatedAt = jsonObjectData.optString("updated_at");
                accessDetails.isPaid = jsonObjectData.optInt("is_paid");
                accessDetails.isValid = jsonObjectData.optInt("is_valid");

                JSONObject jsonObjectSettings = response.optJSONObject("setting");

                accessDetails.siteTitle = jsonObjectSettings.optString("site_title");
                SharedHelper.putKey(WelcomeScreenActivity.this, "app_name", accessDetails.siteTitle);
                accessDetails.siteLogo = jsonObjectSettings.optString("site_logo");
                accessDetails.siteEmailLogo = jsonObjectSettings.optString("site_email_logo");
                accessDetails.siteIcon = jsonObjectSettings.optString("site_icon");
                accessDetails.site_icon = Utilities.drawableFromUrl(WelcomeScreenActivity.this, accessDetails.siteIcon);
                accessDetails.siteCopyright = jsonObjectSettings.optString("site_copyright");
                accessDetails.providerSelectTimeout = jsonObjectSettings.optString("provider_select_timeout");
                accessDetails.providerSearchRadius = jsonObjectSettings.optString("provider_search_radius");
                accessDetails.basePrice = jsonObjectSettings.optString("base_price");
                accessDetails.pricePerMinute = jsonObjectSettings.optString("price_per_minute");
                accessDetails.taxPercentage = jsonObjectSettings.optString("tax_percentage");
                accessDetails.stripeSecretKey = jsonObjectSettings.optString("stripe_secret_key");
                accessDetails.stripePublishableKey = jsonObjectSettings.optString("stripe_publishable_key");
                accessDetails.cash = jsonObjectSettings.optString("CASH");
                accessDetails.card = jsonObjectSettings.optString("CARD");
                accessDetails.manualRequest = jsonObjectSettings.optString("manual_request");
                accessDetails.defaultLang = jsonObjectSettings.optString("default_lang");
                accessDetails.currency = jsonObjectSettings.optString("currency");
                accessDetails.distance = jsonObjectSettings.optString("distance");
                accessDetails.scheduledCancelTimeExceed = jsonObjectSettings.optString("scheduled_cancel_time_exceed");
                accessDetails.pricePerKilometer = jsonObjectSettings.optString("price_per_kilometer");
                accessDetails.commissionPercentage = jsonObjectSettings.optString("commission_percentage");
                accessDetails.storeLinkAndroid = jsonObjectSettings.optString("store_link_android");
                accessDetails.storeLinkIos = jsonObjectSettings.optString("store_link_ios");
                accessDetails.dailyTarget = jsonObjectSettings.optString("daily_target");
                accessDetails.surgePercentage = jsonObjectSettings.optString("surge_percentage");
                accessDetails.surgeTrigger = jsonObjectSettings.optString("surge_trigger");
                accessDetails.demoMode = jsonObjectSettings.optString("demo_mode");
                accessDetails.bookingPrefix = jsonObjectSettings.optString("booking_prefix");
                accessDetails.sosNumber = jsonObjectSettings.optString("sos_number");
                accessDetails.contactNumber = jsonObjectSettings.optString("contact_number");
                accessDetails.contactEmail = jsonObjectSettings.optString("contact_email");
                accessDetails.socialLogin = jsonObjectSettings.optString("social_login");
            }
        } catch (Exception e){
            e.printStackTrace();
        }


    }

    public void displayMessage(String toastString){
        Log.e("displayMessage",""+toastString);
        Toast.makeText(WelcomeScreenActivity.this, toastString, Toast.LENGTH_SHORT).show();
    }
}

