package com.eziride.user.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.eziride.user.Helper.AppHelper;
import com.eziride.user.Helper.ConnectionHelper;
import com.eziride.user.Helper.CustomDialog;
import com.eziride.user.Helper.LocaleUtils;
import com.eziride.user.Helper.SharedHelper;
import com.eziride.user.Helper.URLHelper;
import com.eziride.user.Helper.VolleyMultipartRequest;
import com.eziride.user.R;
import com.eziride.user.RideApplication;
import com.eziride.user.Utils.Utilities;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.eziride.user.RideApplication.trimMessage;

public class EditProfile extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private static final int SELECT_PHOTO = 105;
    private static final int SELECT_CAMERA = 106;
    private static String TAG = "EditProfile";
    public Context context = EditProfile.this;
    public Activity activity = EditProfile.this;
    CustomDialog customDialog;
    ConnectionHelper helper;
    Boolean isInternet;
    Button saveBTN;
    ImageView backArrow;
    Button changePasswordTxt;
    EditText first_name, last_name, mobile_no;
    TextView email;
    ImageView profile_Image;
    Boolean isImageChanged = false;
    public static int deviceHeight;
    public static int deviceWidth;
    Uri uri;
    Utilities utils = new Utilities();
    Boolean isPermissionGivenAlready = false;
    RadioGroup genderGrp;
    RadioButton maleRbtn;
    RadioButton femaleRbtn;
    String gender = "";

    private Uri fileUri;
    private static String fileName;
    private static File mediaFile;
    private Uri cropOutputUri;

    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 102;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 101;

    public final String[] perssionsArrayCamera = new String[]{android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE};


    private interface GENDER {
        String MALE = "MALE";
        String FEMALE = "FEMALE";
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        findViewByIdandInitialization();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        deviceHeight = displayMetrics.heightPixels;
        deviceWidth = displayMetrics.widthPixels;
        if (SharedHelper.getKey(context, "login_by").equals("facebook") ||
                SharedHelper.getKey(context, "login_by").equals("google")) {
            changePasswordTxt.setVisibility(View.GONE);
        } else {
            changePasswordTxt.setVisibility(View.VISIBLE);
        }
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToMainActivity();
            }
        });

        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Pattern ps = Pattern.compile(".*[0-9].*");
                Matcher firstName = ps.matcher(first_name.getText().toString());
                Matcher lastName = ps.matcher(last_name.getText().toString());


                if (email.getText().toString().equals("") || email.getText().toString().length() == 0) {
                    displayMessage(context.getResources().getString(R.string.email_validation));
                } else if (mobile_no.getText().toString().equals("") || mobile_no.getText().toString().length() == 0) {
                    displayMessage(context.getResources().getString(R.string.mobile_number_empty));
                } else if (mobile_no.getText().toString().length() < 10 || mobile_no.getText().toString().length() > 20) {
                    displayMessage(context.getResources().getString(R.string.mobile_number_validation));
                } else if (first_name.getText().toString().equals("") || first_name.getText().toString().length() == 0) {
                    displayMessage(context.getResources().getString(R.string.first_name_empty));
                } else if (last_name.getText().toString().equals("") || last_name.getText().toString().length() == 0) {
                    displayMessage(context.getResources().getString(R.string.last_name_empty));
                } else if (firstName.matches()) {
                    displayMessage(context.getResources().getString(R.string.first_name_no_number));
                } else if (lastName.matches()) {
                    displayMessage(context.getResources().getString(R.string.last_name_no_number));
                } else {
                    if (isInternet) {
                        updateProfile();
                    } else {
                        displayMessage(context.getResources().getString(R.string.something_went_wrong_net));
                    }
                }


            }
        });


        changePasswordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, ChangePassword.class));
            }
        });

        profile_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkStoragePermission()) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                    } else {
                        goToImageIntent();
                    }
                } else {
                    goToImageIntent();
                }*/

                openBottomSheet();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }

  /*  @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    if(!isPermissionGivenAlready) {
                        goToImageIntent();
                    }
                }
            }
        }
    }*/

    public void goToImageIntent() {
        isPermissionGivenAlready = true;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_picture)), SELECT_PHOTO);
    }

  /*  @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            Bitmap bitmap = null;

            try {
                isImageChanged = true;
                Bitmap resizeImg = getBitmapFromUri(this, uri);
                if (resizeImg != null) {
                    Bitmap reRotateImg = AppHelper.modifyOrientation(resizeImg, AppHelper.getPath(this, uri));
                    profile_Image.setImageBitmap(reRotateImg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

    private static Bitmap getBitmapFromUri(@NonNull Context context, @NonNull Uri uri) throws IOException {
        Log.e(TAG, "getBitmapFromUri: Resize uri" + uri);
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        assert parcelFileDescriptor != null;
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        Log.e(TAG, "getBitmapFromUri: Height" + deviceHeight);
        Log.e(TAG, "getBitmapFromUri: width" + deviceWidth);
        int maxSize = Math.min(deviceHeight, deviceWidth);
        if (image != null) {
            Log.e(TAG, "getBitmapFromUri: Width" + image.getWidth());
            Log.e(TAG, "getBitmapFromUri: Height" + image.getHeight());
            int inWidth = image.getWidth();
            int inHeight = image.getHeight();
            int outWidth;
            int outHeight;
            if (inWidth > inHeight) {
                outWidth = maxSize;
                outHeight = (inHeight * maxSize) / inWidth;
            } else {
                outHeight = maxSize;
                outWidth = (inWidth * maxSize) / inHeight;
            }
            return Bitmap.createScaledBitmap(image, outWidth, outHeight, false);
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.valid_image), Toast.LENGTH_SHORT).show();
            return null;
        }

    }

    public void updateProfile() {
        if (isImageChanged) {
            updateProfileWithImage();
        } else {
            updateProfileWithoutImage();
        }
    }

    private void updateProfileWithImage() {
        isImageChanged = false;
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URLHelper.base + URLHelper.UseProfileUpdate, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();

                String res = new String(response.data);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    SharedHelper.putKey(context, "id", jsonObject.optString("id"));
                    SharedHelper.putKey(context, "first_name", jsonObject.optString("first_name"));
                    SharedHelper.putKey(context, "last_name", jsonObject.optString("last_name"));
                    SharedHelper.putKey(context, "email", jsonObject.optString("email"));
                    if (jsonObject.optString("picture").equals("") || jsonObject.optString("picture") == null) {
                        SharedHelper.putKey(context, "picture", "");
                    } else {
                        if (jsonObject.optString("picture").startsWith("http"))
                            SharedHelper.putKey(context, "picture", jsonObject.optString("picture"));
                        else
                            SharedHelper.putKey(context, "picture", URLHelper.avatar_base_url + jsonObject.optString("picture"));
                    }

                    SharedHelper.putKey(context, "gender", jsonObject.optString("gender"));
                    SharedHelper.putKey(context, "mobile", jsonObject.optString("mobile"));
                    SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                   /* SharedHelper.putKey(context, "payment_mode", jsonObject.optString("payment_mode"));
                    if (jsonObject.optString("payment_mode") != null && jsonObject.optString("payment_mode").equalsIgnoreCase("CARD")) {
                        try {
                            JSONObject cardObjectt = jsonObject.getJSONObject("card");
                            SharedHelper.putKey(context, "card_id", cardObjectt.optString("card_id"));
                            SharedHelper.putKey(context, "card_num", "XXXX-XXXX-XXXX-" + cardObjectt.optString("last_four"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }*/
                    GoToMainActivity();
                    Toast.makeText(EditProfile.this, context.getResources().getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                    //displayMessage(getString(R.string.update_success));

                } catch (JSONException e) {
                    e.printStackTrace();
                    displayMessage(context.getResources().getString(R.string.something_went_wrong));
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null) && customDialog.isShowing())
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
                                displayMessage(context.getResources().getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            refreshAccessToken("UPDATE_PROFILE_WITH_IMAGE");
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(context.getResources().getString(R.string.please_try_again));
                            }

                        } else if (response.statusCode == 503) {
                            displayMessage(context.getResources().getString(R.string.server_down));
                        } else {
                            displayMessage(context.getResources().getString(R.string.please_try_again));
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
                        updateProfileWithoutImage();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("first_name", first_name.getText().toString());
                params.put("last_name", last_name.getText().toString());
                params.put("email", email.getText().toString());
                params.put("mobile", mobile_no.getText().toString());
                params.put("gender", gender);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() throws AuthFailureError {
                Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();
                params.put("picture", new VolleyMultipartRequest.DataPart("userImage.jpg", AppHelper.getFileDataFromDrawable(profile_Image.getDrawable()), "image/jpeg"));
                return params;
            }
        };
        RideApplication.getInstance().addToRequestQueue(volleyMultipartRequest, 5000);

    }

    private void updateProfileWithoutImage() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URLHelper.base + URLHelper.UseProfileUpdate, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();

                String res = new String(response.data);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    SharedHelper.putKey(context, "id", jsonObject.optString("id"));
                    SharedHelper.putKey(context, "first_name", jsonObject.optString("first_name"));
                    SharedHelper.putKey(context, "last_name", jsonObject.optString("last_name"));
                    SharedHelper.putKey(context, "email", jsonObject.optString("email"));
                    if (jsonObject.optString("picture").equals("") || jsonObject.optString("picture") == null) {
                        SharedHelper.putKey(context, "picture", "");
                    } else {
                        if (jsonObject.optString("picture").startsWith("http"))
                            SharedHelper.putKey(context, "picture", jsonObject.optString("picture"));
                        else
                            SharedHelper.putKey(context, "picture", URLHelper.base + "public/storage/" + jsonObject.optString("picture"));
                    }

                    SharedHelper.putKey(context, "gender", jsonObject.optString("gender"));
                    SharedHelper.putKey(context, "mobile", jsonObject.optString("mobile"));
                    SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                    /*SharedHelper.putKey(context, "payment_mode", jsonObject.optString("payment_mode"));
                    if (jsonObject.optString("payment_mode") != null && jsonObject.optString("payment_mode").equalsIgnoreCase("CARD")) {
                        try {
                            JSONObject cardObjectt = jsonObject.getJSONObject("card");
                            SharedHelper.putKey(context, "card_id", cardObjectt.optString("card_id"));
                            SharedHelper.putKey(context, "card_num", "XXXX-XXXX-XXXX-" + cardObjectt.optString("last_four"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }*/
                    GoToMainActivity();
                    Toast.makeText(EditProfile.this, context.getResources().getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                    //displayMessage(getString(R.string.update_success));

                } catch (JSONException e) {
                    e.printStackTrace();
                    displayMessage(context.getResources().getString(R.string.something_went_wrong));
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null) && customDialog.isShowing())
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
                                displayMessage(context.getResources().getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            refreshAccessToken("UPDATE_PROFILE_WITHOUT_IMAGE");
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(context.getResources().getString(R.string.please_try_again));
                            }

                        } else if (response.statusCode == 503) {
                            displayMessage(context.getResources().getString(R.string.server_down));
                        } else {
                            displayMessage(context.getResources().getString(R.string.please_try_again));
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
                        updateProfileWithoutImage();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("first_name", first_name.getText().toString());
                params.put("last_name", last_name.getText().toString());
                params.put("email", email.getText().toString());
                params.put("mobile", mobile_no.getText().toString());
                params.put("picture", "");
                params.put("gender", gender);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };
        RideApplication.getInstance().addToRequestQueue(volleyMultipartRequest, 5000);

    }

    public void findViewByIdandInitialization() {
        email = (TextView) findViewById(R.id.email);
        first_name = (EditText) findViewById(R.id.first_name);
        last_name = (EditText) findViewById(R.id.last_name);
        mobile_no = (EditText) findViewById(R.id.mobile_no);
        saveBTN = (Button) findViewById(R.id.saveBTN);
        changePasswordTxt = (Button) findViewById(R.id.changePasswordTxt);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        profile_Image = (ImageView) findViewById(R.id.img_profile);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();
        genderGrp = (RadioGroup) findViewById(R.id.gender_group);
        maleRbtn = (RadioButton) findViewById(R.id.male_btn);
        femaleRbtn = (RadioButton) findViewById(R.id.female_btn);
        genderGrp.setOnCheckedChangeListener(this);
        gender = SharedHelper.getKey(context, "gender");
        Log.e(TAG, "findViewByIdandInitialization: " + gender);

      /*  if (gender != null && !gender.equalsIgnoreCase("null") && !gender.equalsIgnoreCase("")) {
            if (gender.equalsIgnoreCase(GENDER.MALE)) {
                gender = GENDER.MALE;
                maleRbtn.setChecked(true);
            } else if (gender.equalsIgnoreCase(GENDER.FEMALE)) {
                gender = GENDER.FEMALE;
                femaleRbtn.setChecked(true);
            }
        }*/
        //Assign current profile values to the edittext
        //Glide.with(activity).load(SharedHelper.getKey(context, "picture")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(profile_Image);
        if (!SharedHelper.getKey(context, "picture").equalsIgnoreCase("")
                && !SharedHelper.getKey(context, "picture").equalsIgnoreCase(null)
                && SharedHelper.getKey(context, "picture") != null) {
            Picasso.with(context)
                    .load(SharedHelper.getKey(context, "picture"))
                    .placeholder(R.drawable.ic_dummy_user)
                    .error(R.drawable.ic_dummy_user)
                    .into(profile_Image);
        } else {
            Picasso.with(context)
                    .load(R.drawable.ic_dummy_user)
                    .placeholder(R.drawable.ic_dummy_user)
                    .error(R.drawable.ic_dummy_user)
                    .into(profile_Image);
        }
        email.setText(SharedHelper.getKey(context, "email"));
        first_name.setText(SharedHelper.getKey(context, "first_name"));
        last_name.setText(SharedHelper.getKey(context, "last_name"));
        if (SharedHelper.getKey(context, "mobile") != null
                && !SharedHelper.getKey(context, "mobile").equals("null")
                && !SharedHelper.getKey(context, "mobile").equals("")) {
            mobile_no.setText(SharedHelper.getKey(context, "mobile"));
        }
        first_name.requestFocus();
    }

    public void GoToMainActivity() {
        Intent mainIntent = new Intent(activity, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            try {
                Toast.makeText(context, "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        GoToMainActivity();
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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.base + URLHelper.login, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                utils.print("SignUpResponse", response.toString());
                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                if (tag.equalsIgnoreCase("UPDATE_PROFILE_WITH_IMAGE")) {
                    updateProfileWithImage();
                } else {
                    updateProfileWithoutImage();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = "";
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                    utils.GoToBeginActivity(EditProfile.this);
                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
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

        RideApplication.getInstance().addToRequestQueue(jsonObjectRequest, 5000);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.male_btn:
                gender = GENDER.MALE;
                break;
            case R.id.female_btn:
                gender = GENDER.FEMALE;
                break;
        }
    }

    /////////////////////////////////////////////////////////

    public void openBottomSheet() {

        View view = getLayoutInflater().inflate(R.layout.dialog_choose_media, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);

        TextView dialog_gallery = view.findViewById(R.id.dialog_gallary);
        TextView dialog_camera = view.findViewById(R.id.dialog_camera);


        dialog_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open camera
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!AppHelper.hasPermissions(EditProfile.this, perssionsArrayCamera)) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                                && shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                && shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(EditProfile.this);
                            alertBuilder.setCancelable(true);
                            alertBuilder.setTitle(getResources().getString(R.string.permission_necessary));
                            alertBuilder.setMessage(getResources().getString(R.string.camera_permission_necessary));
                            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @TargetApi(Build.VERSION_CODES.M)
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CAMERA);
                                }
                            });
                            AlertDialog alert = alertBuilder.create();
                            alert.show();
                        } else {
                            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CAMERA);
                        }
                    } else
                        cameraIntent();
                } else
                    cameraIntent();
                dialog.dismiss();
            }
        });

        dialog_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = checkStoragePermission();
                if (!result) {
                    galleryIntent();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(EditProfile.this);
                            alertBuilder.setCancelable(true);
                            alertBuilder.setTitle(getResources().getString(R.string.permission_necessary));
                            alertBuilder.setMessage(getResources().getString(R.string.external_storage_permisson));
                            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @TargetApi(Build.VERSION_CODES.M)
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                                    // requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                                }
                            });
                            AlertDialog alert = alertBuilder.create();
                            alert.show();
                        } else {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                            //  requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    }
                }
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void galleryIntent() {
        /*Intent i = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_PHOTO);*/

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_picture)), SELECT_PHOTO);
    }

    private void cameraIntent() {
       /* Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);*/

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, SELECT_CAMERA);
    }

    private Uri getOutputMediaFileUri() {
        fileName = AppHelper.getFileName(1);
        mediaFile = AppHelper.getOutputMediaFile(fileName);
        return Uri.fromFile(mediaFile);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppHelper.hideKeyboard(EditProfile.this);
        if (resultCode == RESULT_OK) {
            Log.e("time 1", "-" + Calendar.getInstance().getTime());
            if (requestCode == SELECT_PHOTO) {
                //      progressBar.setVisibility(View.VISIBLE);

                try {

                    Uri imageUri = data.getData();
                    cropOutputUri = getOutputMediaFileUri();
                    Crop.of(imageUri, cropOutputUri).asSquare().start(EditProfile.this);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                }

                //   new ImageCompressionAsyncTask(true).execute(data.getDataString());

            } else if (requestCode == SELECT_CAMERA) {
                cropOutputUri = getOutputMediaFileUri();
                Crop.of(fileUri, cropOutputUri).asSquare().start(EditProfile.this);
            } else if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
                setImage(cropOutputUri);
            }
        }
    }


    private void setImage(Uri imageUri) {

        try {
            isImageChanged = true;
            Bitmap resizeImg = getBitmapFromUri(this, imageUri);
            if (resizeImg != null) {
                Bitmap reRotateImg = AppHelper.modifyOrientation(resizeImg, AppHelper.getPath(EditProfile.this, imageUri));
                profile_Image.setImageBitmap(reRotateImg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    galleryIntent();
                } else {
                    String permission = permissions[0];
                    boolean showRationale = shouldShowRequestPermissionRationale(permission);
                    if (!showRationale) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(EditProfile.this);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle(getResources().getString(R.string.permission_necessary));
                        alertBuilder.setMessage(getResources().getString(R.string.enable_external_storage));
                        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();
                    }
                }
                break;
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0 && AppHelper.hasPermissions(EditProfile.this, perssionsArrayCamera)) {
                    cameraIntent();
                } else {

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(EditProfile.this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle(getResources().getString(R.string.permission_necessary));
                    alertBuilder.setMessage(getResources().getString(R.string.enable_camera_permission));
                    alertBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, MY_PERMISSIONS_REQUEST_CAMERA);

                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                }
                break;

        }
    }


}
