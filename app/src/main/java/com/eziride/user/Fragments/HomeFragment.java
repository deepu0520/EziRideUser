package com.eziride.user.Fragments;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.eziride.user.Activities.AccessKeyActivity;
import com.eziride.user.Activities.BeginScreen;
import com.eziride.user.Activities.CustomGooglePlacesSearch;
import com.eziride.user.Activities.HistoryActivity;
import com.eziride.user.Activities.QRscan;
import com.eziride.user.Activities.ShowProfile;
import com.eziride.user.Constants.Constants;
import com.eziride.user.Helper.ConnectionHelper;
import com.eziride.user.Helper.CustomDialog;
import com.eziride.user.Helper.DataParser;
import com.eziride.user.Helper.SharedHelper;
import com.eziride.user.Helper.URLHelper;
import com.eziride.user.Models.AccessDetails;
import com.eziride.user.Models.CardInfo;
import com.eziride.user.Models.Driver;
import com.eziride.user.Models.PlacePredictions;
import com.eziride.user.R;
import com.eziride.user.Retrofit.ApiInterface;
import com.eziride.user.Retrofit.ResponseListener;
import com.eziride.user.Retrofit.RetrofitClient;
import com.eziride.user.RideApplication;
import com.eziride.user.Utils.MapAnimator;
import com.eziride.user.Utils.MyBoldTextView;
import com.eziride.user.Utils.MyButton;
import com.eziride.user.Utils.MyTextView;
import com.eziride.user.Utils.Utilities;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
import static com.eziride.user.RideApplication.trimMessage;

//import com.google.android.gms.location.places.ui.PlaceAutocomplete;

public class HomeFragment extends Fragment implements OnMapReadyCallback, LocationListener,
        GoogleMap.OnMarkerDragListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResponseListener, GoogleMap.OnCameraMoveListener {

    private static final String TAG = "HomeFragment";

    Activity activity;
    Context context;
    View rootView;
    HomeFragmentListener listener;
    double wallet_balance;
    private Random mRandom = new Random(1984);
    LayoutInflater inflater;
    AlertDialog reasonDialog;
    AlertDialog cancelRideDialog;
    String is_track = "";
    String strTimeTaken = "";
    ApiInterface mApiInterface;
    Button btnHome, btnWork;
    String strOTP = "";
    TextView lblOTP;
    TextView lblTotalDistance, lblWalletBalance;
    LinearLayout lnrOTP;
    private int SPECIFICDRIVER = 101;
    private MyBoldTextView lblCancellationCharge;
    private LinearLayout cancellationChargeLayout;
    private ImageView estimateFareInfo;
    private boolean isAllServiceRequest = false;
    private Toast toast;
    // private int paymentType = 0;  // 0 for cash not accepted and 1 for cash accepted

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCameraMove() {
        utils.print("Current marker", "Zoom Level " + mMap.getCameraPosition().zoom);
        cmPosition = mMap.getCameraPosition();
        if (marker != null) {
            if (!mMap.getProjection().getVisibleRegion().latLngBounds.contains(marker.getPosition())) {
                utils.print("Current marker", "Current Marker is not visible");
                if (mapfocus.getVisibility() == View.INVISIBLE) {
                    //mapfocus.setVisibility(View.VISIBLE);
                }
            } else {
                utils.print("Current marker", "Current Marker is visible");
                if (mapfocus.getVisibility() == View.VISIBLE) {
                    //mapfocus.setVisibility(View.INVISIBLE);
                }
                if (mMap.getCameraPosition().zoom < 16.0f) {
                    if (mapfocus.getVisibility() == View.INVISIBLE) {
                        //mapfocus.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    public interface HomeFragmentListener {
    }

    String isPaid = "", paymentMode = "";
    int totalRideAmount = 0, walletAmountDetected = 0, couponAmountDetected = 0;
    Utilities utils = new Utilities();
    int flowValue = 0;
    DrawerLayout drawer;
    int NAV_DRAWER = 0;
    String reqStatus = "";
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST = 18945;
    private final int ADD_CARD_CODE = 435;
    private static final int REQUEST_LOCATION = 1450;
    String feedBackRating = "1"/*, ratingReason = ""*/;
    private ArrayList<CardInfo> cardInfoArrayList = new ArrayList<>();
    double height;
    double width;
    public String PreviousStatus = "";
    public String CurrentStatus = "";
    Handler handleCheckStatus;
    String strPickLocation = "", strTag = "", strPickType = "";
    boolean once = true;
    int click = 1;
    boolean afterToday = false;
    boolean pick_first = true;
    Driver driver;

    //        <!-- Map frame -->
    LinearLayout mapLayout;
    SupportMapFragment mapFragment;
    GoogleMap mMap;
    int value;
    Marker marker;
    Double latitude, longitude;
    String currentAddress;
    GoogleApiClient mGoogleApiClient;

    //        <!-- Source and Destination Layout-->
    LinearLayout sourceAndDestinationLayout;
    FrameLayout frmDestination;
    MyBoldTextView destination;
    ImageView imgMenu, mapfocus, imgBack, shadowBack;
    View tripLine;
    LinearLayout errorLayout;
    ImageView destinationBorderImg;
    TextView frmSource, frmDest, txtChange;
    CardView srcDestLayout;


//       <!--1. Request to providers -->

    LinearLayout lnrRequestProviders;
    RecyclerView rcvServiceTypes;
    ImageView imgPaymentType;
    ImageView imgSos;
    ImageView imgShareRide;
    MyBoldTextView lblPaymentType, lblPaymentChange, booking_id;
    MyButton btnRequestRides;
    String scheduledDate = "";
    String scheduledTime = "";
    String cancalReason = "";

//        <!--1. Driver Details-->

    LinearLayout lnrHidePopup, lnrProviderPopup, lnrPriceBase, lnrPricemin, lnrPricekm;
    RelativeLayout lnrSearchAnimation;

    ImageView imgProviderPopup, barcode;
    MyBoldTextView lblPriceMin, lblBasePricePopup, lblCapacity, lblServiceName, lblPriceKm, lblCalculationType, lblProviderDesc;
    MyButton btnDonePopup;

//         <!--2. Approximate Rate ...-->

    LinearLayout lnrApproximate;
    LinearLayout lnrAvailableBalance;
    MyButton btnRequestRideConfirm;
    MyButton imgSchedule;
    CheckBox chkWallet;
    MyBoldTextView lblEta;
    MyBoldTextView lblType;
    MyBoldTextView lblApproxAmount, surgeDiscount, surgeTxt;
    View lineView;

    LinearLayout ScheduleLayout;
    MyBoldTextView scheduleDate;
    MyBoldTextView scheduleTime;
    MyButton scheduleBtn;
    DatePickerDialog datePickerDialog;

    LocationRequest mLocationRequest;

//         <!--3. Waiting For Providers ...-->

    private RelativeLayout lnrWaitingForProviders;
    private MyBoldTextView lblNoMatch;
    private MyButton btnCancelRide;
    private boolean mIsShowing;
    private boolean mIsHiding;
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

//         <!--4. Driver Accepted ...-->

    private LinearLayout lnrProviderAccepted, lnrAfterAcceptedStatus, AfterAcceptButtonLayout;
    private ImageView imgProvider, imgServiceRequested;
    private MyBoldTextView lblProvider, lblStatus, lblServiceRequested, lblModelNumber, lblSurgePrice;
    private RatingBar ratingProvider;
    private MyButton btnCall, btnCancelTrip;

//          <!--5. Invoice Layout ...-->

    private LinearLayout lnrInvoice;
    private MyBoldTextView lblBasePrice, lblDistanceCovered, lblExtraPrice, lblTimeTaken, lblDistancePrice, lblCommision, lblTaxPrice,
            lblTotalPrice, lblPaymentTypeInvoice, lblPaymentChangeInvoice, lblDiscountPrice, lblWalletPrice, lblCancelCharge;
    private ImageView imgPaymentTypeInvoice;
    //    private MyButton btnPayNow;
//    private MyButton btnPaymentDoneBtn;
    private LinearLayout discountDetectionLayout, walletDetectionLayout, llCancellationLayout;
    private LinearLayout bookingIDLayout;

//          <!--6. Rate provider Layout ...-->

    private LinearLayout lnrRateProvider;
    private MyBoldTextView lblProviderNameRate;
    private ImageView imgProviderRate;
    private RatingBar ratingProviderRate;
    private EditText txtCommentsRate;
    private Button btnSubmitReview;
    private Float pro_rating;

//            <!-- Static marker-->

    private RelativeLayout rtlStaticMarker;
    private ImageView imgDestination;
    private MyButton btnDone;
    private CameraPosition cmPosition;
    boolean directotripFlow = true;

    private String current_lat = "", current_lng = "", current_address = "", source_lat = "", source_lng = "", source_address = "",
            dest_lat = "", dest_lng = "", dest_address = "", extend_dest_lat = "", extend_dest_lng = "", extend_dest_address = "";

    //Internet
    ConnectionHelper helper;
    Boolean isInternet;
    //RecylerView
    int currentPostion = 0;
    CustomDialog customDialog;

    //MArkers
    private LatLng sourceLatLng;
    private LatLng destLatLng;
    private Marker sourceMarker;
    private Marker destinationMarker;
    private Marker providerMarker;
    ArrayList<Marker> lstProviderMarkers = new ArrayList<Marker>();
    private HashMap<Integer, Marker> mHashMap = new HashMap<Integer, Marker>();
    AlertDialog alert;
    //Animation
    Animation slide_down, slide_up, slide_up_top, slide_up_down;

    ParserTask parserTask;
    FetchUrl fetchUrl;
    String notificationTxt;
    boolean scheduleTrip = false;

    private LinearLayout lnrHomeWork, lnrHome, lnrWork;
    private String providerid, qrcode;
    //  private RecyclerView rcvReasonList;
    private JSONArray jArr;
//    MapRipple mapRipple;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            notificationTxt = bundle.getString("Notification");
            Log.e("HomeFragment", "onCreate : Notification" + notificationTxt);
        }

        /*String languageToLoad = SharedHelper.getKey(getActivity(), "language");
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
        }
        this.inflater = inflater;

        customDialog = new CustomDialog(context);

        try {
            if (customDialog != null)
                customDialog.show();
        } catch (Exception e) {
            Log.e(TAG, "onCreateView: exception is @@ " + e.getMessage());
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                init(rootView);
                //permission to access location
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Android M Permission check
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    initMap();
                    MapsInitializer.initialize(getActivity());
                }
            }
        }, 500);

        reqStatus = SharedHelper.getKey(context, "req_status");

        // markerList = new ArrayList<>();


        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        try {
            listener = (HomeFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement HomeFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void init(View rootView) {

        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();

        statusCheck();
//        <!-- Map frame -->
        mapLayout = rootView.findViewById(R.id.mapLayout);
        drawer = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);
        drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);

//        <!-- Source and Destination Layout-->
        sourceAndDestinationLayout = rootView.findViewById(R.id.sourceAndDestinationLayout);
        srcDestLayout = (CardView) rootView.findViewById(R.id.sourceDestLayout);
        frmSource = (TextView) rootView.findViewById(R.id.frmSource);
        lblOTP = (TextView) rootView.findViewById(R.id.lblOTP);
        lblTotalDistance = (TextView) rootView.findViewById(R.id.lblTotalDistance);
        lblWalletBalance = (TextView) rootView.findViewById(R.id.lblWalletBalance);
        lnrOTP = rootView.findViewById(R.id.lnrOTP);
        frmDest = (TextView) rootView.findViewById(R.id.frmDest);
        txtChange = (TextView) rootView.findViewById(R.id.txtChange);
        frmDestination = (FrameLayout) rootView.findViewById(R.id.frmDestination);
        destination = (MyBoldTextView) rootView.findViewById(R.id.destination);
        imgMenu = (ImageView) rootView.findViewById(R.id.imgMenu);
        imgSos = (ImageView) rootView.findViewById(R.id.imgSos);
        imgShareRide = (ImageView) rootView.findViewById(R.id.imgShareRide);
        mapfocus = (ImageView) rootView.findViewById(R.id.mapfocus);
        imgBack = (ImageView) rootView.findViewById(R.id.imgBack);
        shadowBack = (ImageView) rootView.findViewById(R.id.shadowBack);
        tripLine = (View) rootView.findViewById(R.id.trip_line);
        destinationBorderImg = (ImageView) rootView.findViewById(R.id.dest_border_img);
        barcode = rootView.findViewById(R.id.barcode);
//        <!-- Request to providers-->

        lnrRequestProviders = rootView.findViewById(R.id.lnrRequestProviders);
        rcvServiceTypes = (RecyclerView) rootView.findViewById(R.id.rcvServiceTypes);
        imgPaymentType = (ImageView) rootView.findViewById(R.id.imgPaymentType);
        lblPaymentType = (MyBoldTextView) rootView.findViewById(R.id.lblPaymentType);
        lblPaymentChange = (MyBoldTextView) rootView.findViewById(R.id.lblPaymentChange);
        booking_id = (MyBoldTextView) rootView.findViewById(R.id.booking_id);
        btnRequestRides = (MyButton) rootView.findViewById(R.id.btnRequestRides);

//        <!--  Driver and service type Details-->

        lnrSearchAnimation = (RelativeLayout) rootView.findViewById(R.id.lnrSearch);
        lnrProviderPopup = rootView.findViewById(R.id.lnrProviderPopup);
        lnrPriceBase = rootView.findViewById(R.id.lnrPriceBase);
        lnrPricekm = rootView.findViewById(R.id.lnrPricekm);
        lnrPricemin = rootView.findViewById(R.id.lnrPricemin);
        lnrHidePopup = rootView.findViewById(R.id.lnrHidePopup);
        imgProviderPopup = (ImageView) rootView.findViewById(R.id.imgProviderPopup);

        lblServiceName = (MyBoldTextView) rootView.findViewById(R.id.lblServiceName);
        lblCapacity = (MyBoldTextView) rootView.findViewById(R.id.lblCapacity);
        lblPriceKm = (MyBoldTextView) rootView.findViewById(R.id.lblPriceKm);
        lblPriceMin = (MyBoldTextView) rootView.findViewById(R.id.lblPriceMin);
        lblCalculationType = (MyBoldTextView) rootView.findViewById(R.id.lblCalculationType);
        lblBasePricePopup = (MyBoldTextView) rootView.findViewById(R.id.lblBasePricePopup);
        lblDistanceCovered = (MyBoldTextView) rootView.findViewById(R.id.lblDistanceCovered);
        lblProviderDesc = (MyBoldTextView) rootView.findViewById(R.id.lblProviderDesc);

        btnDonePopup = (MyButton) rootView.findViewById(R.id.btnDonePopup);


//         <!--2. Approximate Rate ...-->

        lnrApproximate = rootView.findViewById(R.id.lnrApproximate);
        lnrAvailableBalance = rootView.findViewById(R.id.lnrAvailableBalance);
        imgSchedule = (MyButton) rootView.findViewById(R.id.imgSchedule);
        chkWallet = (CheckBox) rootView.findViewById(R.id.chkWallet);
        lblEta = (MyBoldTextView) rootView.findViewById(R.id.lblEta);
        lblType = (MyBoldTextView) rootView.findViewById(R.id.lblType);
        lblApproxAmount = (MyBoldTextView) rootView.findViewById(R.id.lblApproxAmount);
        surgeDiscount = (MyBoldTextView) rootView.findViewById(R.id.surgeDiscount);
        surgeTxt = (MyBoldTextView) rootView.findViewById(R.id.surge_txt);
        btnRequestRideConfirm = (MyButton) rootView.findViewById(R.id.btnRequestRideConfirm);
        lineView = (View) rootView.findViewById(R.id.lineView);
        estimateFareInfo = rootView.findViewById(R.id.estimateFareInfo);

        //Schedule Layout
        ScheduleLayout = rootView.findViewById(R.id.ScheduleLayout);
        scheduleDate = (MyBoldTextView) rootView.findViewById(R.id.scheduleDate);
        scheduleTime = (MyBoldTextView) rootView.findViewById(R.id.scheduleTime);
        scheduleBtn = (MyButton) rootView.findViewById(R.id.scheduleBtn);

//         <!--3. Waiting For Providers ...-->

        lnrWaitingForProviders = (RelativeLayout) rootView.findViewById(R.id.lnrWaitingForProviders);
        lblNoMatch = (MyBoldTextView) rootView.findViewById(R.id.lblNoMatch);
        btnCancelRide = (MyButton) rootView.findViewById(R.id.btnCancelRide);

//          <!--4. Driver Accepted ...-->

        lnrProviderAccepted = rootView.findViewById(R.id.lnrProviderAccepted);
        lnrAfterAcceptedStatus = rootView.findViewById(R.id.lnrAfterAcceptedStatus);
        AfterAcceptButtonLayout = rootView.findViewById(R.id.AfterAcceptButtonLayout);
        imgProvider = (ImageView) rootView.findViewById(R.id.imgProvider);
        imgServiceRequested = (ImageView) rootView.findViewById(R.id.imgServiceRequested);
        lblProvider = (MyBoldTextView) rootView.findViewById(R.id.lblProvider);
        lblStatus = (MyBoldTextView) rootView.findViewById(R.id.lblStatus);
        lblSurgePrice = (MyBoldTextView) rootView.findViewById(R.id.lblSurgePrice);
        lblServiceRequested = (MyBoldTextView) rootView.findViewById(R.id.lblServiceRequested);
        lblModelNumber = (MyBoldTextView) rootView.findViewById(R.id.lblModelNumber);
        ratingProvider = (RatingBar) rootView.findViewById(R.id.ratingProvider);
        btnCall = (MyButton) rootView.findViewById(R.id.btnCall);
        btnCancelTrip = (MyButton) rootView.findViewById(R.id.btnCancelTrip);


//           <!--5. Invoice Layout ...-->

        lnrInvoice = rootView.findViewById(R.id.lnrInvoice);
        lblBasePrice = (MyBoldTextView) rootView.findViewById(R.id.lblBasePrice);
        lblExtraPrice = (MyBoldTextView) rootView.findViewById(R.id.lblExtraPrice);
        lblDistancePrice = (MyBoldTextView) rootView.findViewById(R.id.lblDistancePrice);
        lblCancellationCharge = (MyBoldTextView) rootView.findViewById(R.id.lblCancellationCharge);
        cancellationChargeLayout = (LinearLayout) rootView.findViewById(R.id.cancellationChargeLayout);
        lblTimeTaken = (MyBoldTextView) rootView.findViewById(R.id.lblTimeTaken);
        //lblCommision = (MyBoldTextView) rootView.findViewById(R.id.lblCommision);
        lblTaxPrice = (MyBoldTextView) rootView.findViewById(R.id.lblTaxPrice);
        lblTotalPrice = (MyBoldTextView) rootView.findViewById(R.id.lblTotalPrice);
        lblPaymentTypeInvoice = (MyBoldTextView) rootView.findViewById(R.id.lblPaymentTypeInvoice);
        imgPaymentTypeInvoice = (ImageView) rootView.findViewById(R.id.imgPaymentTypeInvoice);
//        btnPayNow = (MyButton) rootView.findViewById(R.id.btnPayNow);
//        btnPaymentDoneBtn = (MyButton) rootView.findViewById(R.id.btnPaymentDoneBtn);
        bookingIDLayout = rootView.findViewById(R.id.bookingIDLayout);
        walletDetectionLayout = rootView.findViewById(R.id.walletDetectionLayout);
        llCancellationLayout = rootView.findViewById(R.id.llCancellationLayout);
        discountDetectionLayout = rootView.findViewById(R.id.discountDetectionLayout);
        lblWalletPrice = (MyBoldTextView) rootView.findViewById(R.id.lblWalletPrice);
        lblCancelCharge = rootView.findViewById(R.id.lblCancelCharge);
        lblDiscountPrice = (MyBoldTextView) rootView.findViewById(R.id.lblDiscountPrice);


//          <!--6. Rate provider Layout ...-->

        lnrHomeWork = rootView.findViewById(R.id.lnrHomeWork);
        lnrHome = rootView.findViewById(R.id.lnrHome);
        lnrWork = rootView.findViewById(R.id.lnrWork);
        lnrRateProvider = rootView.findViewById(R.id.lnrRateProvider);
        lblProviderNameRate = (MyBoldTextView) rootView.findViewById(R.id.lblProviderName);
        imgProviderRate = (ImageView) rootView.findViewById(R.id.imgProviderRate);
        txtCommentsRate = (EditText) rootView.findViewById(R.id.txtComments);
        ratingProviderRate = (RatingBar) rootView.findViewById(R.id.ratingProviderRate);
        btnSubmitReview = (MyButton) rootView.findViewById(R.id.btnSubmitReview);
        // rcvReasonList = rootView.findViewById(R.id.rcvReasonList);
//            <!--Static marker-->

        rtlStaticMarker = (RelativeLayout) rootView.findViewById(R.id.rtlStaticMarker);
        imgDestination = (ImageView) rootView.findViewById(R.id.imgDestination);
        btnDone = (MyButton) rootView.findViewById(R.id.btnDone);
        btnHome = (Button) rootView.findViewById(R.id.btnHome);
        btnWork = (Button) rootView.findViewById(R.id.btnWork);

        getCards();

        getFavoriteLocations();

        checkStatus();

        handleCheckStatus = new Handler();
        //check status every 3 sec
        handleCheckStatus.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (helper.isConnectingToInternet()) {
                    if (!isAdded()) {
                        return;
                    }
                    checkStatus();
                    utils.print("Handler", "Called");
                    if (alert != null && alert.isShowing()) {
                        alert.dismiss();
                        alert = null;
                    }
                } else {
                    showDialog();
                }
                handleCheckStatus.postDelayed(this, 5000);
            }
        }, 5000);


        btnRequestRides.setOnClickListener(new OnClick());
        btnDonePopup.setOnClickListener(new OnClick());
        lnrHidePopup.setOnClickListener(new OnClick());
        btnRequestRideConfirm.setOnClickListener(new OnClick());
        estimateFareInfo.setOnClickListener(new OnClick());
        btnCancelRide.setOnClickListener(new OnClick());
        btnCancelTrip.setOnClickListener(new OnClick());
        btnCall.setOnClickListener(new OnClick());
//        btnPayNow.setOnClickListener(new OnClick());
//        btnPaymentDoneBtn.setOnClickListener(new OnClick());
        btnSubmitReview.setOnClickListener(new OnClick());
        btnHome.setOnClickListener(new OnClick());
        btnWork.setOnClickListener(new OnClick());
        btnDone.setOnClickListener(new OnClick());
        frmDestination.setOnClickListener(new OnClick());
        frmDest.setOnClickListener(new OnClick());
        lblPaymentChange.setOnClickListener(new OnClick());
        frmSource.setOnClickListener(new OnClick());
        txtChange.setOnClickListener(new OnClick());
        imgMenu.setOnClickListener(new OnClick());
        mapfocus.setOnClickListener(new OnClick());
        imgSchedule.setOnClickListener(new OnClick());
        imgBack.setOnClickListener(new OnClick());
        scheduleBtn.setOnClickListener(new OnClick());
        scheduleDate.setOnClickListener(new OnClick());
        scheduleTime.setOnClickListener(new OnClick());
        imgProvider.setOnClickListener(new OnClick());
        imgProviderRate.setOnClickListener(new OnClick());
        imgSos.setOnClickListener(new OnClick());
        imgShareRide.setOnClickListener(new OnClick());

        lnrRequestProviders.setOnClickListener(new OnClick());
        lnrProviderPopup.setOnClickListener(new OnClick());
        ScheduleLayout.setOnClickListener(new OnClick());
        lnrApproximate.setOnClickListener(new OnClick());
        lnrProviderAccepted.setOnClickListener(new OnClick());
        lnrInvoice.setOnClickListener(new OnClick());
        lnrRateProvider.setOnClickListener(new OnClick());
        lnrWaitingForProviders.setOnClickListener(new OnClick());
        barcode.setOnClickListener(new OnClick());
        flowValue = 0;
        layoutChanges();

        //Load animation
        slide_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        slide_up_top = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up_top);
        slide_up_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up_down);

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return true;

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (!reqStatus.equalsIgnoreCase("SEARCHING")) {
                        utils.print("", "Back key pressed!");
                        if (directotripFlow) {
                            if (lnrRequestProviders.getVisibility() == View.VISIBLE) {
                                flowValue = 0;
                                if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
                                    LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
                                    CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                }
                            } else if (lnrApproximate.getVisibility() == View.VISIBLE) {
                                flowValue = 1;
                            } else if (lnrWaitingForProviders.getVisibility() == View.VISIBLE) {
                                flowValue = 2;
                            } else if (ScheduleLayout.getVisibility() == View.VISIBLE) {
                                flowValue = 2;
                            } else {
                                getActivity().finish();
                            }
                        } else {
                            directotripFlow = true;
                            flowValue = 0;
                        }
                        layoutChanges();
                        return true;
                    }
                }
                return false;
            }
        });


    }


    @SuppressWarnings("MissingPermission")
    void initMap() {
        if (mMap == null) {
            //   SupportMapFragment mMapFragment = SupportMapFragment.newInstance();
            FragmentManager fm = getChildFragmentManager();
            mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.provider_map);

            mapFragment.getMapAsync(this);
        }

        if (mMap != null) {
            setupMap();
        }
    }

    @SuppressWarnings("MissingPermission")
    void setupMap() {
        if (mMap != null) {
//            mMap.setMyLocationEnabled(true);
//            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.setBuildingsEnabled(true);
            mMap.setMyLocationEnabled(false);
            mMap.setOnMarkerDragListener(this);
            mMap.setOnCameraMoveListener(this);
            mMap.getUiSettings().setRotateGesturesEnabled(false);
            mMap.getUiSettings().setTiltGesturesEnabled(false);
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    // Getting view from the layout file infowindowlayout.xml
                    View v = activity.getLayoutInflater().inflate(R.layout.info_window, null);

                    TextView lblAddress = (TextView) v.findViewById(R.id.lblAddress);
                    TextView lblTime = (TextView) v.findViewById(R.id.txtTime);

                    lblAddress.setText(marker.getSnippet());

                    if (strTimeTaken.length() > 0) {
                        lblTime.setText(strTimeTaken);
                    }

                    if (marker.getTitle() == null) {
                        return null;
                    }
                    if (marker.getTitle().equalsIgnoreCase("source") || marker.getTitle().equalsIgnoreCase("destination")) {
                        return v;
                    } else {
                        return null;
                    }
                }

                @Override
                public View getInfoContents(Marker marker) {
                    return null;
                }
            });
        }

    }


    @Override
    public void onLocationChanged(Location location) {
        try {
            if (marker != null) {
                marker.remove();
            }
            if (location != null && location.getLatitude() != 0 && location.getLongitude() != 0) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .anchor(0.5f, 0.75f)
                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
                marker = mMap.addMarker(markerOptions);

                // mMap is GoogleMap object, latLng is the location on map from which ripple should start

                Log.e("MAP", "onLocationChanged: 1 " + location.getLatitude());
                Log.e("MAP", "onLocationChanged: 2 " + location.getLongitude());
                current_lat = "" + location.getLatitude();
                current_lng = "" + location.getLongitude();

                if (source_lat.equalsIgnoreCase("") || source_lat.length() < 0) {
                    source_lat = current_lat;
                }
                if (source_lng.equalsIgnoreCase("") || source_lng.length() < 0) {
                    source_lng = current_lng;
                }

                if (value == 0) {
                    LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    mMap.setPadding(0, 0, 0, 0);
                    mMap.getUiSettings().setZoomControlsEnabled(false);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    mMap.getUiSettings().setMapToolbarEnabled(false);
                    mMap.getUiSettings().setCompassEnabled(false);

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    getAddressUsingLatLng(context, "" + latitude, "" + longitude);
                    source_lat = "" + latitude;
                    source_lng = "" + longitude;
                    value++;
                    if ((customDialog != null) && (customDialog.isShowing())) {
                        customDialog.dismiss();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class OnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnHome:
                    if (!SharedHelper.getKey(context, "home").equalsIgnoreCase("")) {
                        setHomeWorkAddress("home");
                    }
                    break;
                case R.id.btnWork:

                    if (!SharedHelper.getKey(context, "work").equalsIgnoreCase("")) {
                        setHomeWorkAddress("work");
                    }
                    break;
                case R.id.frmSource:
                    Intent intent = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                    intent.putExtra("cursor", "source");
                    intent.putExtra("s_address", frmSource.getText().toString());
                    intent.putExtra("d_address", frmDest.getText().toString());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                    break;
                case R.id.frmDestination:
                    if (CurrentStatus.equalsIgnoreCase("")) {
                        Intent intent2 = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                        intent2.putExtra("cursor", "destination");
                        intent2.putExtra("s_address", frmSource.getText().toString());
                        intent2.putExtra("d_address", frmDest.getText().toString());
                        startActivityForResult(intent2, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                    }
                    break;
                case R.id.txtChange:
                    Intent intent4 = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                    intent4.putExtra("cursor", "destination");
                    intent4.putExtra("s_address", frmSource.getText().toString());
                    intent4.putExtra("d_address", frmDest.getText().toString());
                    startActivityForResult(intent4, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                    break;
                case R.id.frmDest:
                    Intent intent3 = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                    intent3.putExtra("cursor", "destination");
                    intent3.putExtra("s_address", frmSource.getText().toString());
                    intent3.putExtra("d_address", destination.getText().toString());
                    intent3.putExtra("d_address", frmDest.getText().toString());
                    startActivityForResult(intent3, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                    break;
                case R.id.lblPaymentChange:
                    showChooser();
                    break;
                case R.id.btnRequestRides:
                    scheduledDate = "";
                    scheduledTime = "";
                    if (!frmSource.getText().toString().equalsIgnoreCase("") &&
                            !frmDest.getText().toString().equalsIgnoreCase("")) {
                        if (SharedHelper.getKey(context, "isCashAccept").equalsIgnoreCase("false") && SharedHelper.getKey(context, "payment_mode").equalsIgnoreCase("CASH")) {
                            showChangePaymentDialog();
                            //                            Toast.makeText(context, "Cash mode payment is not available currently, please change payment mode to card.", Toast.LENGTH_LONG).show();
                        } else {
                            getApproximateFare();
                            frmDest.setOnClickListener(null);
                            frmSource.setOnClickListener(null);
                            srcDestLayout.setOnClickListener(new OnClick());
                        }
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.pickup_drop), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btnRequestRideConfirm:
                    SharedHelper.putKey(context, "name", "");
                    scheduledDate = "";
                    scheduledTime = "";
                    sendRequest();
                    break;
                case R.id.estimateFareInfo:
                    showEstimateFareDialog();
                    break;
                case R.id.btnPayNow:
                    payNow();
                    break;
                case R.id.btnPaymentDoneBtn:
//                    btnPayNow.setVisibility(View.GONE);
//                    btnPaymentDoneBtn.setVisibility(View.GONE);
                    flowValue = 6;
                    layoutChanges();
                    break;
                case R.id.btnSubmitReview:
                    if (/*ratingReason.length() > 0 || */!feedBackRating.equalsIgnoreCase("1")) {
                        submitReviewCall();
                    } else {
                        Toast.makeText(context, getResources().getString(R.string.give_review), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.lnrHidePopup:
                case R.id.btnDonePopup:
                    lnrHidePopup.setVisibility(View.GONE);
                    flowValue = 1;
                    layoutChanges();
                    click = 1;
                    break;
                case R.id.btnCancelRide:
                    showCancelRideBeforeDriverAcceptDialog();
                    break;
                case R.id.btnCancelTrip:
                    if (btnCancelTrip.getText().toString().equals(context.getResources().getString(R.string.cancel_trip)))
                        showCancelRideDialog();
                    else {
                        String shareUrl = URLHelper.REDIRECT_SHARE_URL;
                        navigateToShareScreen(shareUrl);
                    }
                    break;
                case R.id.imgSos:
                    showSosPopUp();
                    break;
                case R.id.imgShareRide:
                    String url = "http://maps.google.com/maps?q=loc:";
                    navigateToShareScreen(url);
                    break;
                case R.id.imgProvider:
                    Intent intent1 = new Intent(activity, ShowProfile.class);
                    intent1.putExtra("driver", driver);
                    startActivity(intent1);
                    break;
                case R.id.imgProviderRate:
                    Intent intent5 = new Intent(activity, ShowProfile.class);
                    intent5.putExtra("driver", driver);
                    startActivity(intent5);
                    break;
                case R.id.btnCall:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 2);
                    } else {
                        Intent intentCall = new Intent(Intent.ACTION_CALL);
                        intentCall.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "provider_mobile_no")));
                        startActivity(intentCall);
                    }
                    break;
                case R.id.btnDone:
                    if (is_track.equalsIgnoreCase("YES") &&
                            (CurrentStatus.equalsIgnoreCase("STARTED") || CurrentStatus.equalsIgnoreCase("PICKEDUP")
                                    || CurrentStatus.equalsIgnoreCase("ARRIVED"))) {
                        extend_dest_lat = "" + cmPosition.target.latitude;
                        extend_dest_lng = "" + cmPosition.target.longitude;
                        showTripExtendAlert(extend_dest_lat, extend_dest_lng);
                    } else {
                        pick_first = true;
                        try {

                            utils.print("centerLat", cmPosition.target.latitude + "");
                            utils.print("centerLong", cmPosition.target.longitude + "");

                            Geocoder geocoder = null;
                            List<Address> addresses;
                            geocoder = new Geocoder(getActivity(), Locale.getDefault());

                            String city = "", state = "", address = "";

                            try {
                                addresses = geocoder.getFromLocation(cmPosition.target.latitude, cmPosition.target.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                address = addresses.get(0).getAddressLine(0);
                                city = addresses.get(0).getLocality();
                                state = addresses.get(0).getAdminArea();
                                ; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (strPickType.equalsIgnoreCase("source")) {
                                source_address = Utilities.getAddressUsingLatLng("source", frmSource, context, "" + cmPosition.target.latitude, "" + cmPosition.target.longitude);
                                source_lat = "" + cmPosition.target.latitude;
                                source_lng = "" + cmPosition.target.longitude;
                                if (dest_lat.equalsIgnoreCase("")) {
                                    Toast.makeText(context, getResources().getString(R.string.select_destination), Toast.LENGTH_SHORT).show();
                                    Intent intentDest = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                                    intentDest.putExtra("cursor", "destination");
                                    intentDest.putExtra("s_address", source_address);
                                    startActivityForResult(intentDest, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                                } else {
                                    source_lat = "" + cmPosition.target.latitude;
                                    source_lng = "" + cmPosition.target.longitude;

                                    mMap.clear();
                                    flowValue = 1;
                                    layoutChanges();
                                    strPickLocation = "";
                                    strPickType = "";
                                    getServiceList();

                                    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(cmPosition.target.latitude,
                                            cmPosition.target.longitude));
                                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
                                    mMap.moveCamera(center);
                                    mMap.moveCamera(zoom);

                                }
                            } else {

                                dest_lat = "" + cmPosition.target.latitude;
                                if (dest_lat.equalsIgnoreCase(source_lat)) {
                                    Toast.makeText(context, activity.getResources().getString(R.string.source_dest_not_same), Toast.LENGTH_SHORT).show();
                                    Intent intentDest = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                                    intentDest.putExtra("cursor", "destination");
                                    intentDest.putExtra("s_address", frmSource.getText().toString());
                                    startActivityForResult(intentDest, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                                } else {
                                    dest_address = Utilities.getAddressUsingLatLng("destination", frmDest, context, "" + cmPosition.target.latitude, "" + cmPosition.target.longitude);
                                    dest_lat = "" + cmPosition.target.latitude;
                                    dest_lng = "" + cmPosition.target.longitude;

                                    mMap.clear();
                                    flowValue = 1;
                                    layoutChanges();
                                    strPickLocation = "";
                                    strPickType = "";
                                    getServiceList();

                                    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(cmPosition.target.latitude,
                                            cmPosition.target.longitude));
                                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
                                    mMap.moveCamera(center);
                                    mMap.moveCamera(zoom);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, getResources().getString(R.string.get_address_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.imgBack:
                    if (directotripFlow) {
                        if (lnrRequestProviders.getVisibility() == View.VISIBLE) {
                            flowValue = 0;
                            srcDestLayout.setVisibility(View.GONE);
                            frmSource.setOnClickListener(new OnClick());
                            frmDest.setOnClickListener(new OnClick());
                            srcDestLayout.setOnClickListener(null);
                            if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
                                destinationBorderImg.setVisibility(View.VISIBLE);
                                //verticalView.setVisibility(View.GONE);
                                LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                srcDestLayout.setVisibility(View.GONE);
                            }
                        } else if (lnrApproximate.getVisibility() == View.VISIBLE) {
                            frmSource.setOnClickListener(new OnClick());
                            frmDest.setOnClickListener(new OnClick());
                            srcDestLayout.setOnClickListener(null);
                            flowValue = 1;
                        } else if (lnrWaitingForProviders.getVisibility() == View.VISIBLE) {
                            flowValue = 2;
                        } else if (ScheduleLayout.getVisibility() == View.VISIBLE) {
                            flowValue = 2;
                        }
                    } else {
                        directotripFlow = true;
                        flowValue = 0;
                    }
                    layoutChanges();
                    break;
                case R.id.imgMenu:
                    try {
                        if (NAV_DRAWER == 0) {
                            if (drawer != null)
                                drawer.openDrawer(Gravity.START);
                        } else {
                            NAV_DRAWER = 0;
                            if (drawer != null)
                                drawer.closeDrawers();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.mapfocus:
                    Double crtLat, crtLng;
                    if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
                        crtLat = Double.parseDouble(current_lat);
                        crtLng = Double.parseDouble(current_lng);

                        if (crtLat != null && crtLng != null) {
                            LatLng loc = new LatLng(crtLat, crtLng);
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(16).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            //mapfocus.setVisibility(View.INVISIBLE);
                        }
                    }
                    break;
                case R.id.imgSchedule:
                    flowValue = 7;
                    layoutChanges();
                    break;
                case R.id.scheduleBtn:
                    SharedHelper.putKey(context, "name", "");
                    if (scheduledDate != "" && scheduledTime != "") {
                        Date date = null;
                        try {
                            date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(scheduledDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long milliseconds = date.getTime();
                        if (!DateUtils.isToday(milliseconds)) {
                            sendRequest();
                        } else {
                            if (utils.checktimings(scheduledTime)) {
                                sendRequest();
                            } else {
                                Toast.makeText(activity, context.getResources().getString(R.string.different_time), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(activity, context.getResources().getString(R.string.choose_date_time), Toast.LENGTH_SHORT).show();
                    }

                    break;
                case R.id.scheduleDate:
                    // calender class's instance and get current date , month and year from calender
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR); // current year
                    int mMonth = c.get(Calendar.MONTH); // current month
                    int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                    // date picker dialog
                    datePickerDialog = new DatePickerDialog(activity,
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {

                                    // set day of month , month and year value in the edit text
                                    String choosedMonth = "";
                                    String choosedDate = "";
                                    String choosedDateFormat = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                    scheduledDate = choosedDateFormat;
                                    try {
                                        choosedMonth = utils.getMonth(choosedDateFormat);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if (dayOfMonth < 10) {
                                        choosedDate = "0" + dayOfMonth;
                                    } else {
                                        choosedDate = "" + dayOfMonth;
                                    }
                                    afterToday = utils.isAfterToday(year, monthOfYear, dayOfMonth);
                                    scheduleDate.setText(choosedDate + " " + choosedMonth + " " + year);
                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() + 7200000);
                    //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    datePickerDialog.getDatePicker().setMaxDate((System.currentTimeMillis() + 7200000) + (1000 * 60 * 60 * 24 * 7));
                    datePickerDialog.show();
                    break;
                case R.id.scheduleTime:


                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd:HH:mm");
                    String currentDateandTime = sdf.format(new Date());

                    Date date = null;
                    try {
                        date = sdf.parse(currentDateandTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.HOUR, 2);

//                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                        int callCount = 0;   //To track number of calls to onTimeSet()

                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                            if (callCount == 0) {
                                String choosedHour = "";
                                String choosedMinute = "";
                                String choosedTimeZone = "";
                                String choosedTime = "";

                                scheduledTime = selectedHour + ":" + selectedMinute;

                                if (selectedHour > 12) {
                                    choosedTimeZone = "PM";
                                    selectedHour = selectedHour - 12;
                                    if (selectedHour < 10) {
                                        choosedHour = "0" + selectedHour;
                                    } else {
                                        choosedHour = "" + selectedHour;
                                    }
                                } else {
                                    choosedTimeZone = "AM";
                                    if (selectedHour < 10) {
                                        choosedHour = "0" + selectedHour;
                                    } else {
                                        choosedHour = "" + selectedHour;
                                    }
                                }

                                if (selectedMinute < 10) {
                                    choosedMinute = "0" + selectedMinute;
                                } else {
                                    choosedMinute = "" + selectedMinute;
                                }
                                choosedTime = choosedHour + ":" + choosedMinute + " " + choosedTimeZone;

                                if (scheduledDate != "" && scheduledTime != "") {
                                    Date date = null;
                                    try {
                                        date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(scheduledDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    long milliseconds = date.getTime();
                                    if (!DateUtils.isToday(milliseconds)) {
                                        scheduleTime.setText(choosedTime);
                                    } else {
                                        if (utils.checktimings(scheduledTime)) {
                                            scheduleTime.setText(choosedTime);
                                        } else {
                                            Toast toast = new Toast(activity);
                                            toast.makeText(activity, context.getResources().getString(R.string.different_time), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    Toast.makeText(activity, context.getResources().getString(R.string.choose_date_time), Toast.LENGTH_SHORT).show();
                                }
                            }
                            callCount++;
                        }
                    }, hour, minute, false);//Yes 24 hour time
                    mTimePicker.setTitle(getResources().getString(R.string.select_time));
                    mTimePicker.show();
                    break;
                case R.id.barcode:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            StartScan();
                        } else {
                            getActivity().requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 5);
                        }
                    } else {
                        StartScan();
                    }
                    break;
            }
        }
    }

    public void StartScan() {
        Constants.QRCODESCAN = true;
        Intent i = new Intent(getContext(), QRscan.class);
        i.putExtra("cursor", "destination");
        i.putExtra("s_address", frmSource.getText().toString());
        i.putExtra("d_address", frmDest.getText().toString());
        startActivityForResult(i, SPECIFICDRIVER);
    }

    public void navigateToShareScreen(String shareUrl) {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            String name = SharedHelper.getKey(context, "first_name") + " " + SharedHelper.getKey(context, "last_name");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "ride-" + "Mr/Mrs." + name + " would like to share a ride with you at " +
                    shareUrl + current_lat + "," + current_lng);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, getResources().getString(R.string.shared_app_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    private void showSosPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(AccessDetails.siteTitle)
                .setIcon(AccessDetails.site_icon)
                .setMessage(context.getResources().getString(R.string.emaergeny_call))
                .setCancelable(false);
        builder.setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 3);
                } else {
                    Intent intentCall = new Intent(Intent.ACTION_CALL);
                    intentCall.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "sos")));
                    startActivity(intentCall);
                }
            }
        });
        builder.setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showCancelRideDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(AccessDetails.siteTitle)
                .setIcon(AccessDetails.site_icon)
                .setMessage(context.getResources().getString(R.string.cancel_ride_alert));
        builder.setCancelable(false);
        builder.setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showreasonDialog();
            }
        });
        builder.setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        cancelRideDialog = builder.create();
        cancelRideDialog.show();
    }

    private void showEstimateFareDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(getResources().getString(R.string.fare_est_dis))
                .setIcon(AccessDetails.site_icon)
                .setMessage(getResources().getString(R.string.fare_est_detail));
        builder.setCancelable(false);
        builder.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        cancelRideDialog = builder.create();
        cancelRideDialog.show();

        final Button positiveButton = cancelRideDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
        // positiveButtonLL.gravity = Gravity.CENTER;
        positiveButtonLL.width = ViewGroup.LayoutParams.MATCH_PARENT;
        positiveButton.setLayoutParams(positiveButtonLL);
    }

    private void showCancelRideBeforeDriverAcceptDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(AccessDetails.siteTitle)
                .setIcon(AccessDetails.site_icon)
                .setMessage(context.getResources().getString(R.string.cancel_ride_alert));
        builder.setCancelable(false);
        builder.setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showreasonDialog();
            }
        });
        builder.setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        cancelRideDialog = builder.create();
        cancelRideDialog.show();
    }

    private void showreasonDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.cancel_dialog, null);
        final EditText reasonEtxt = (EditText) view.findViewById(R.id.reason_etxt);
        Button submitBtn = (Button) view.findViewById(R.id.submit_btn);
        builder.setIcon(AccessDetails.site_icon)
                .setTitle(AccessDetails.siteTitle)
                .setView(view)
                .setCancelable(true);
        reasonDialog = builder.create();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancalReason = reasonEtxt.getText().toString();
                cancelRequest();
                reasonDialog.dismiss();
            }
        });
        reasonDialog.show();
    }

    void layoutChanges() {

//        for (int i = 0; i < lstProviderMarkers.size(); i++) {
//            lstProviderMarkers.get(i).remove();
//
//        }
        //  mHashMap.clear();
        //markerList.clear();
        try {
            utils.hideKeypad(getActivity(), getActivity().getCurrentFocus());
            if (lnrApproximate.getVisibility() == View.VISIBLE) {
                lnrApproximate.startAnimation(slide_down);
            } else if (ScheduleLayout.getVisibility() == View.VISIBLE) {
                ScheduleLayout.startAnimation(slide_down);
            } else if (lnrRequestProviders.getVisibility() == View.VISIBLE) {
                lnrRequestProviders.startAnimation(slide_down);
            } else if (lnrProviderPopup.getVisibility() == View.VISIBLE) {
                lnrProviderPopup.startAnimation(slide_down);
                lnrSearchAnimation.startAnimation(slide_up_down);
                lnrSearchAnimation.setVisibility(View.VISIBLE);
            } else if (lnrInvoice.getVisibility() == View.VISIBLE) {
                lnrInvoice.startAnimation(slide_down);
            } else if (lnrRateProvider.getVisibility() == View.VISIBLE) {
                lnrRateProvider.startAnimation(slide_down);
            } else if (lnrInvoice.getVisibility() == View.VISIBLE) {
                lnrInvoice.startAnimation(slide_down);
            }
            lnrRequestProviders.setVisibility(View.GONE);
            lnrProviderPopup.setVisibility(View.GONE);
            lnrApproximate.setVisibility(View.GONE);
            lnrWaitingForProviders.setVisibility(View.GONE);
            lnrProviderAccepted.setVisibility(View.GONE);
            srcDestLayout.setVisibility(View.GONE);
            lnrInvoice.setVisibility(View.GONE);
            lnrRateProvider.setVisibility(View.GONE);
            lnrHomeWork.setVisibility(View.GONE);
            ScheduleLayout.setVisibility(View.GONE);
            rtlStaticMarker.setVisibility(View.GONE);
            frmDestination.setVisibility(View.GONE);
            imgBack.setVisibility(View.GONE);
            imgBack.setVisibility(View.GONE);
            // imgSos.setVisibility(View.GONE);
            txtCommentsRate.setText("");
            scheduleDate.setText(context.getResources().getString(R.string.sample_date));
            scheduleTime.setText(context.getResources().getString(R.string.sample_time));
            if (flowValue == 0) {
                Constants.QRANDDIRECT = true;
                srcDestLayout.setVisibility(View.GONE);
                txtChange.setVisibility(View.GONE);
                frmSource.setOnClickListener(new OnClick());
                frmDest.setOnClickListener(new OnClick());
                srcDestLayout.setOnClickListener(null);
                if (mMap != null) {
                    mMap.clear();
                    stopAnim();
                    setupMap();
                }

                setCurrentAddress();
                if (!SharedHelper.getKey(context, "home").equalsIgnoreCase("")) {
                    lnrHome.setVisibility(View.VISIBLE);
                } else {
                    lnrHome.setVisibility(View.GONE);
                }
                if (!SharedHelper.getKey(context, "work").equalsIgnoreCase("")) {
                    lnrWork.setVisibility(View.VISIBLE);
                } else {
                    lnrWork.setVisibility(View.GONE);
                }
                if (lnrHome.getVisibility() == View.GONE && lnrWork.getVisibility() == View.GONE) {
                    lnrHomeWork.setVisibility(View.GONE);
                } else {
                    lnrHomeWork.setVisibility(View.VISIBLE);
                }

                frmDestination.setVisibility(View.VISIBLE);
                imgMenu.setVisibility(View.VISIBLE);
                destination.setText("");
                destination.setHint(context.getResources().getString(R.string.where_to_go));
                frmDest.setText("");
                dest_address = "";
                dest_lat = "";
                dest_lng = "";
                source_lat = "" + current_lat;
                source_lng = "" + current_lng;
                source_address = "" + current_address;
                sourceAndDestinationLayout.setVisibility(View.VISIBLE);
                getProvidersList("0");
            } else if (flowValue == 1) {
                frmSource.setVisibility(View.VISIBLE);
                destinationBorderImg.setVisibility(View.GONE);
                frmDestination.setVisibility(View.VISIBLE);
                imgBack.setVisibility(View.VISIBLE);
                lnrRequestProviders.startAnimation(slide_up);
                ////////////
               /* if (cardInfo.getLastFour().equals("CASH")) {
                    SharedHelper.putKey(context, "payment_mode", "CASH");
                    imgPaymentType.setImageResource(R.drawable.money_icon);
                    if (SharedHelper.getKey(context, "isCashAccept").equalsIgnoreCase("true"))
                        lblPaymentType.setText("CASH");
                    else
                        lblPaymentType.setText("CASH PAYMENT NOT AVAILABLE");

                } else {
                    SharedHelper.putKey(context, "card_id", cardInfo.getCardId());
                    SharedHelper.putKey(context, "payment_mode", "CARD");
                    SharedHelper.putKey(context, "card_num", "XXXX-XXXX-XXXX-" + cardInfo.getLastFour());
                    imgPaymentType.setImageResource(R.drawable.visa);
                    lblPaymentType.setText("XXXX-XXXX-XXXX-" + cardInfo.getLastFour());
                }*/
                ////////////////
                if (SharedHelper.getKey(context, "isCashAccept").equalsIgnoreCase("true") && SharedHelper.getKey(context, "payment_mode").equalsIgnoreCase("CASH")) {
                    imgPaymentType.setImageResource(R.drawable.money_icon);
                    lblPaymentType.setText("CASH");
                } else if (SharedHelper.getKey(context, "payment_mode").equalsIgnoreCase("CARD")) {
                    imgPaymentType.setImageResource(R.drawable.visa);
                    lblPaymentType.setText(SharedHelper.getKey(context, "card_num"));
                } else
                    lblPaymentType.setText(getString(R.string.no_cash_payment_txt));
                /////////////////////////////
                lnrRequestProviders.setVisibility(View.VISIBLE);
                srcDestLayout.setVisibility(View.VISIBLE);
                imgMenu.setVisibility(View.GONE);
                if (!Double.isNaN(wallet_balance) && wallet_balance > 0) {
                    if (lineView != null && chkWallet != null) {
                        lineView.setVisibility(View.VISIBLE);
                        chkWallet.setVisibility(View.VISIBLE);
                        lnrAvailableBalance.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (lineView != null && chkWallet != null) {
                        lineView.setVisibility(View.GONE);
                        chkWallet.setVisibility(View.GONE);
                        lnrAvailableBalance.setVisibility(View.GONE);
                    }
                }
                chkWallet.setChecked(false);
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(true);
                    destinationMarker.setDraggable(true);
                }
            } else if (flowValue == 2) {
                imgBack.setVisibility(View.VISIBLE);
                imgMenu.setVisibility(View.GONE);
                chkWallet.setChecked(false);
                lnrApproximate.startAnimation(slide_up);
                lnrApproximate.setVisibility(View.VISIBLE);
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(false);
                }
                if (Constants.QRANDDIRECT) {
                    imgSchedule.setVisibility(View.VISIBLE);
                } else {
                    imgSchedule.setVisibility(View.GONE);
                }
            } else if (flowValue == 3) {
                Constants.QRANDDIRECT = true;
                imgBack.setVisibility(View.VISIBLE);
                imgMenu.setVisibility(View.GONE);
                lnrWaitingForProviders.setVisibility(View.VISIBLE);
                srcDestLayout.setVisibility(View.GONE);
                //sourceAndDestinationLayout.setVisibility(View.GONE);
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(false);
                }
            } else if (flowValue == 4) {
                imgMenu.setVisibility(View.VISIBLE);
                lnrProviderAccepted.startAnimation(slide_up);
                lnrProviderAccepted.setVisibility(View.VISIBLE);
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(false);
                }
            } else if (flowValue == 5) {
                imgMenu.setVisibility(View.VISIBLE);
                lnrInvoice.startAnimation(slide_up);
                lnrInvoice.setVisibility(View.VISIBLE);
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(false);
                }
            } else if (flowValue == 6) {
                imgMenu.setVisibility(View.VISIBLE);
                lnrRateProvider.startAnimation(slide_up);
                lnrRateProvider.setVisibility(View.VISIBLE);
                // getReasonList();
                LayerDrawable drawable = (LayerDrawable) ratingProviderRate.getProgressDrawable();
                drawable.getDrawable(0).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                drawable.getDrawable(1).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
                drawable.getDrawable(2).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
                ratingProviderRate.setRating(1.0f);
                feedBackRating = "1";
                ratingProviderRate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                        if (rating < 1.0f) {
                            ratingProviderRate.setRating(1.0f);
                            feedBackRating = "1";
                        }
                        //  if (rating < 5.0f) {
                        //  if (jArr.length() > 0) {
                        // rcvReasonList.setVisibility(View.GONE);
                        //  txtCommentsRate.setVisibility(View.GONE);
                        // } else
                        txtCommentsRate.setVisibility(View.VISIBLE);
                        //  } else {
                        //  rcvReasonList.setVisibility(View.GONE);
                        txtCommentsRate.setVisibility(View.VISIBLE);
                        //  }
                        feedBackRating = String.valueOf((int) rating);
                    }
                });
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(false);
                }
            } else if (flowValue == 7) {
                imgBack.setVisibility(View.VISIBLE);
                ScheduleLayout.startAnimation(slide_up);
                ScheduleLayout.setVisibility(View.VISIBLE);
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(false);
                }
            } else if (flowValue == 8) {
                // clear all views
                shadowBack.setVisibility(View.GONE);
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(false);
                }
            } else if (flowValue == 9) {
                srcDestLayout.setVisibility(View.GONE);
                rtlStaticMarker.setVisibility(View.VISIBLE);
                shadowBack.setVisibility(View.GONE);
                frmDestination.setVisibility(View.GONE);
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(false);
                }
            } else if (flowValue == 10) {
                destination.setHint(context.getResources().getString(R.string.extend_trip));
                frmDestination.setVisibility(View.VISIBLE);
                imgMenu.setVisibility(View.VISIBLE);
                if (lnrProviderAccepted.getVisibility() == View.GONE) {
                    lnrProviderAccepted.startAnimation(slide_up);
                    lnrProviderAccepted.setVisibility(View.VISIBLE);
                }
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(true);
                }

                Utilities.getAddressUsingLatLng("destination", destination, context, "" + dest_lat,
                        "" + dest_lng);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  /*  private void getReasonList() {
        customDialog = new CustomDialog(getActivity());
        customDialog.setCancelable(false);
        customDialog.show();

        Call<ResponseBody> call = RetrofitClient.getLiveTrackingClient().create(ApiInterface.class)
                .getReasonList("XMLHttpRequest", "Bearer" +
                        " " + SharedHelper.getKey(getActivity(), "access_token"));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.e("sUCESS", "SUCESS" + response.body());
                customDialog.dismiss();
                if (response.body() != null) {
                    try {
                        String json = new String(response.body().bytes()); //String.valueOf(response.body());

                        if (response.isSuccessful()) {
                            jArr = new JSONArray(json);
                            if (jArr.length() > 0) {
                                rcvReasonList.setVisibility(View.VISIBLE);
                                ReasonListAdapter adapter = new ReasonListAdapter(jArr);
                                rcvReasonList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                rcvReasonList.setAdapter(adapter);
                            } else {
                                rcvReasonList.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(getActivity(), response.message(), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                customDialog.dismiss();
                Toast.makeText(getActivity(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });

    }*/


    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.style_json));

            if (!success) {
                utils.print("Map:Style", "Style parsing failed.");
            } else {
                utils.print("Map:Style", "Style Applied.");
            }
        } catch (Resources.NotFoundException e) {
            utils.print("Map:Style", "Can't find style. Error: ");
        }

        mMap = googleMap;

        setupMap();

        customDialog.dismiss();

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
//                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
//            mMap.setMyLocationEnabled(true);
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(context)
                        .setTitle(getResources().getString(R.string.location_permission_needed))
                        .setMessage(getResources().getString(R.string.location_permission_desc))
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        1);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        if (is_track.equalsIgnoreCase("YES") && CurrentStatus.equalsIgnoreCase("PICKEDUP")) {
            LatLng markerLocation = destinationMarker.getPosition();
            extend_dest_lat = "" + markerLocation.latitude;
            extend_dest_lng = "" + markerLocation.longitude;
            showTripExtendAlert(extend_dest_lat, extend_dest_lng);
        } else {
            String title = "";
            if (marker != null && marker.getTitle() != null) {
                title = marker.getTitle();
                if (sourceMarker != null && title.equalsIgnoreCase("Source")) {
                    LatLng markerLocation = sourceMarker.getPosition();
                    source_lat = markerLocation.latitude + "";
                    source_lng = markerLocation.longitude + "";
                    source_address = Utilities.getAddressUsingLatLng("source", frmSource, context, "" + source_lat,
                            "" + source_lng);

                } else if (destinationMarker != null && title.equalsIgnoreCase("Destination")) {

                    LatLng markerLocation = destinationMarker.getPosition();

                    dest_lat = "" + markerLocation.latitude;
                    dest_lng = "" + markerLocation.longitude;

                    dest_address = Utilities.getAddressUsingLatLng("destination", frmDest, context, "" + dest_lat,
                            "" + dest_lng);
                }
                mMap.clear();
                setValuesForSourceAndDestination();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                    initMap();
                    MapsInitializer.initialize(getActivity());
                } /*else {
                    showPermissionReqDialog();
                }*/
                break;
            case 2:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "provider_mobile_no")));
                    startActivity(intent);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                }
                break;
            case 3:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "sos")));
                    startActivity(intent);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 3);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showDialogForGPSIntent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(AccessDetails.siteTitle)
                .setIcon(AccessDetails.site_icon)
                .setMessage(getResources().getString(R.string.gps_message))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.enable_gps),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                activity.startActivity(callGPSSettingIntent);
                            }
                        });
        builder.setNegativeButton(getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert1 = builder.create();
        alert1.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST | requestCode == SPECIFICDRIVER) {
            if (parserTask != null) {
                parserTask = null;
            }

            if (fetchUrl != null) {
                fetchUrl = null;
            }

            if (resultCode == Activity.RESULT_OK) {
                if (marker != null) {
                    marker.remove();
                }
                PlacePredictions placePredictions;
                placePredictions = (PlacePredictions) data.getSerializableExtra("Location Address");
                strPickLocation = data.getExtras().getString("pick_location");
                strPickType = data.getExtras().getString("type");
                providerid = data.getExtras().getString("providerid");
                qrcode = data.getExtras().getString("qrcode");
                if (strPickLocation.equalsIgnoreCase("yes")) {
                    pick_first = true;
                    mMap.clear();
                    flowValue = 9;
                    layoutChanges();
                    float zoomLevel = 16.0f; //This goes up to 21
                    stopAnim();
                } else {
                    if (placePredictions != null) {
                        if (is_track.equalsIgnoreCase("YES") && CurrentStatus.equalsIgnoreCase("PICKEDUP")) {
                            extend_dest_lat = "" + placePredictions.strDestLatitude;
                            extend_dest_lng = "" + placePredictions.strDestLongitude;
                            showTripExtendAlert(extend_dest_lat, extend_dest_lng);
                        } else {
                            if (!placePredictions.strSourceAddress.equalsIgnoreCase("")) {
                                try {
                                    source_lat = "" + placePredictions.strSourceLatitude;
                                    source_lng = "" + placePredictions.strSourceLongitude;
//                                    source_address =  Utilities.getAddressUsingLatLng("source", frmSource, context, ""+source_lat,
//                                            ""+source_lng);
                                    source_address = "" + placePredictions.strSourceAddress;
                                    frmSource.setText(source_address);
                                    SharedHelper.putKey(context, "source", "" + source_address);
                                    if (!placePredictions.strSourceLatitude.equalsIgnoreCase("")
                                            && !placePredictions.strSourceLongitude.equalsIgnoreCase("")) {
                                        double latitude = Double.parseDouble(placePredictions.strSourceLatitude);
                                        double longitude = Double.parseDouble(placePredictions.strSourceLongitude);

                                        LatLng location = new LatLng(latitude, longitude);

                                        MarkerOptions markerOptions = new MarkerOptions()
                                                .position(location)
                                                .snippet(frmSource.getText().toString())
                                                .title("source")
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.yellow_pin));
                                        marker = mMap.addMarker(markerOptions);
                                        sourceMarker = mMap.addMarker(markerOptions);

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if (!placePredictions.strDestAddress.equalsIgnoreCase("")) {
                                dest_lat = "" + placePredictions.strDestLatitude;
                                dest_lng = "" + placePredictions.strDestLongitude;
//                                dest_address =  Utilities.getAddressUsingLatLng("destination", frmDest, context, ""+dest_lat,
//                                        ""+dest_lng);
                                dest_address = "" + placePredictions.strDestAddress;
                                SharedHelper.putKey(context, "destination", "" + dest_address);
                                frmDest.setText(dest_address);
                                SharedHelper.putKey(context, "current_status", "2");
                                if (source_lat != null && source_lng != null && !source_lng.equalsIgnoreCase("")
                                        && !source_lat.equalsIgnoreCase("")) {
                                    try {
                                        String url = getUrl(Double.parseDouble(source_lat), Double.parseDouble(source_lng)
                                                , Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));
                                        fetchUrl = new FetchUrl();
                                        fetchUrl.execute(url);
                                        LatLng location = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));

                                        if (sourceMarker != null)
                                            sourceMarker.remove();
                                        MarkerOptions markerOptions = new MarkerOptions()
                                                .position(location)
                                                .snippet(frmSource.getText().toString())
                                                .title("source")
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.yellow_pin));
                                        marker = mMap.addMarker(markerOptions);
                                        sourceMarker = mMap.addMarker(markerOptions);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("")) {
                                    try {
                                        destLatLng = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));
                                        if (destinationMarker != null)
                                            destinationMarker.remove();
                                        MarkerOptions destMarker = new MarkerOptions()
                                                .position(destLatLng).title("destinat" +
                                                        "ion").snippet(frmDest.getText().toString())
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));
                                        destinationMarker = mMap.addMarker(destMarker);
                                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                        builder.include(sourceMarker.getPosition());
                                        builder.include(destinationMarker.getPosition());
                                        LatLngBounds bounds = builder.build();
                                        int padding = 150; // offset from edges of the map in pixels
                                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                        mMap.moveCamera(cu);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            if (requestCode == SPECIFICDRIVER) {
                                directotripFlow = false;
                                setValuesForSourceAndDestination();
                                flowValue = 2;
                                if (dest_address.equalsIgnoreCase("")) {
                                    getProvidersList(SharedHelper.getKey(context, "service_type"));
                                } else {
                                   /* if (cardInfoArrayList.size() > 0) {
                                        getCardDetailsForPayment(cardInfoArrayList.get(0));
                                    } else if (SharedHelper.getKey(context, "isCashAccept").equalsIgnoreCase("true"))
                                        lblPaymentType.setText("CASH");
                                    else
                                        lblPaymentType.setText("CASH PAYMENT NOT AVAILABLE");*/
                                    if (SharedHelper.getKey(context, "isCashAccept").equalsIgnoreCase("true") && SharedHelper.getKey(context, "payment_mode").equalsIgnoreCase("CASH")) {
                                        imgPaymentType.setImageResource(R.drawable.money_icon);
                                        lblPaymentType.setText("CASH");
                                    } else if (SharedHelper.getKey(context, "payment_mode").equalsIgnoreCase("CARD")) {
                                        imgPaymentType.setImageResource(R.drawable.visa);
                                        lblPaymentType.setText(SharedHelper.getKey(context, "card_num"));
                                    } else
                                        lblPaymentType.setText(getString(R.string.no_cash_payment_txt));

                                    getProvidersList(SharedHelper.getKey(context, "service_type"));
                                }
                                if (!frmSource.getText().toString().equalsIgnoreCase("") &&
                                        !frmDest.getText().toString().equalsIgnoreCase("")) {
                                    getApproximateFare();
                                    frmDest.setOnClickListener(null);
                                    frmSource.setOnClickListener(null);
                                    srcDestLayout.setOnClickListener(new OnClick());
                                } else {
                                    Toast.makeText(context, context.getResources().getString(R.string.pickup_drop), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (dest_address.equalsIgnoreCase("")) {
                                    flowValue = 1;
//                            frmSource.setText(source_address);
                                    getServiceList();
                                } else {
                                    flowValue = 1;

                                    if (SharedHelper.getKey(context, "isCashAccept").equalsIgnoreCase("true") && SharedHelper.getKey(context, "payment_mode").equalsIgnoreCase("CASH")) {
                                        imgPaymentType.setImageResource(R.drawable.money_icon);
                                        lblPaymentType.setText("CASH");
                                    } else if (SharedHelper.getKey(context, "payment_mode").equalsIgnoreCase("CARD")) {
                                        imgPaymentType.setImageResource(R.drawable.visa);
                                        lblPaymentType.setText(SharedHelper.getKey(context, "card_num"));
                                    } else
                                        lblPaymentType.setText(getString(R.string.no_cash_payment_txt));

                                   /* if (cardInfoArrayList.size() > 0) {
                                        getCardDetailsForPayment(cardInfoArrayList.get(0));
                                    } else if (SharedHelper.getKey(context, "isCashAccept").equalsIgnoreCase("true"))
                                        lblPaymentType.setText("CASH");
                                    else
                                        lblPaymentType.setText("CASH PAYMENT NOT AVAILABLE");*/
                                    getServiceList();
                                }
                            }
                            layoutChanges();
                        }
                    }
                }
            } /*else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
            }*/ else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        if (requestCode == ADD_CARD_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                boolean result = data.getBooleanExtra("isAdded", false);
                if (result) {
                    getCards();
                }
            }
        }
        if (requestCode == 5555) {
            if (resultCode == Activity.RESULT_OK) {
                CardInfo cardInfo = data.getParcelableExtra("card_info");
                getCardDetailsForPayment(cardInfo);
            }
        }
        if (requestCode == REQUEST_LOCATION) {
            Log.e("GPS Result Status", "onActivityResult: " + requestCode);
            Log.e("GPS Result Status", "onActivityResult: " + data);
        } else {
            Log.e("GPS Result Status else", "onActivityResult: " + requestCode);
            Log.e("GPS Result Status else", "onActivityResult: " + data);
        }
    }

    private void showTripExtendAlert(final String latitude, final String longitude) {
        Utilities.getAddressUsingLatLng("destination", frmDest, context, latitude, longitude);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(AccessDetails.siteTitle)
                .setIcon(AccessDetails.site_icon)
                .setMessage(getString(R.string.extend_trip_alert));
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utilities.getAddressUsingLatLng("destination", destination, context, latitude, longitude);
                extendTripAPI(latitude, longitude);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Reset to previous seletion menu in navigation
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        final android.support.v7.app.AlertDialog dialog = builder.create();
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg) {
                dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.text_red));
                dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.text_red));
            }
        });
        dialog.show();
    }

    private void extendTripAPI(final String latitude, final String longitude) {
        ApiInterface mApiInterface = RetrofitClient.getLiveTrackingClient().create(ApiInterface.class);

        Call<ResponseBody> call = mApiInterface.extendTrip("XMLHttpRequest", SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"),
                SharedHelper.getKey(context, "request_id"), latitude, longitude, SharedHelper.getKey(context, "extend_address"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.e("sUCESS", "SUCESS" + response.body());
                if (response.body() != null) {
                    try {
                        String bodyString = new String(response.body().bytes());
                        Log.e("sUCESS", "bodyString" + bodyString);
                        try {
                            JSONObject jsonObj = new JSONObject(bodyString);
                            Toast.makeText(context, jsonObj.optString("message"), Toast.LENGTH_SHORT).show();
                            dest_lat = latitude;
                            dest_lng = longitude;
                            dest_address = SharedHelper.getKey(context, "extend_address");
                            mMap.clear();
                            setValuesForSourceAndDestination();
                            flowValue = 10;
                            layoutChanges();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    void showProviderPopup(JSONObject jsonObject) {
        lnrSearchAnimation.startAnimation(slide_up_top);
        lnrSearchAnimation.setVisibility(View.GONE);
        lnrProviderPopup.setVisibility(View.VISIBLE);
        lnrRequestProviders.setVisibility(View.GONE);


        Glide.with(activity).load(jsonObject.optString("image"))
                .apply(new RequestOptions().placeholder(R.drawable.pickup_drop_icon).dontAnimate().error(R.drawable.pickup_drop_icon))
                .into(imgProviderPopup);

        lnrPriceBase.setVisibility(View.GONE);
        lnrPricemin.setVisibility(View.GONE);
        lnrPricekm.setVisibility(View.GONE);

        if (jsonObject.optString("calculator").equalsIgnoreCase("MIN")
                || jsonObject.optString("calculator").equalsIgnoreCase("HOUR")) {
            lnrPriceBase.setVisibility(View.VISIBLE);
            lnrPricemin.setVisibility(View.VISIBLE);
            if (jsonObject.optString("calculator").equalsIgnoreCase("MIN")) {
                lblCalculationType.setText("Minutes");
            } else {
                lblCalculationType.setText("Hours");
            }
        } else if (jsonObject.optString("calculator").equalsIgnoreCase("DISTANCE")) {
            lnrPriceBase.setVisibility(View.VISIBLE);
            lnrPricekm.setVisibility(View.VISIBLE);
            lblCalculationType.setText("Distance");
        } else if (jsonObject.optString("calculator").equalsIgnoreCase("DISTANCEMIN")
                || jsonObject.optString("calculator").equalsIgnoreCase("DISTANCEHOUR")) {
            lnrPriceBase.setVisibility(View.VISIBLE);
            lnrPricemin.setVisibility(View.VISIBLE);
            lnrPricekm.setVisibility(View.VISIBLE);
            if (jsonObject.optString("calculator").equalsIgnoreCase("DISTANCEMIN")) {
                lblCalculationType.setText("Distance and Minutes");
            } else {
                lblCalculationType.setText("Distance and Hours");
            }
        }

        if (!jsonObject.optString("capacity").equalsIgnoreCase("null")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                lblCapacity.setText(jsonObject.optString("capacity"));
            } else {
                lblCapacity.setText(jsonObject.optString("capacity") + " peoples");
            }
        } else {
            lblCapacity.setVisibility(View.GONE);
        }

        lblServiceName.setText("" + jsonObject.optString("name"));
        lblBasePricePopup.setText(SharedHelper.getKey(context, "currency") + jsonObject.optString("fixed"));
        lblPriceKm.setText(SharedHelper.getKey(context, "currency") + jsonObject.optString("price"));
        lblPriceMin.setText(SharedHelper.getKey(context, "currency") + jsonObject.optString("minute"));
        if (jsonObject.optString("description").equalsIgnoreCase("null")) {
            lblProviderDesc.setVisibility(View.GONE);
        } else {
            lblProviderDesc.setVisibility(View.VISIBLE);
            lblProviderDesc.setText("" + jsonObject.optString("description"));
        }
    }

    public void setValuesForApproximateLayout() {
        if (isInternet) {
            String surge = SharedHelper.getKey(context, "surge");
            if (surge.equalsIgnoreCase("1")) {
                surgeDiscount.setVisibility(View.VISIBLE);
                surgeTxt.setVisibility(View.VISIBLE);
                surgeDiscount.setText(SharedHelper.getKey(context, "surge_value"));
            } else {
                surgeDiscount.setVisibility(View.GONE);
                surgeTxt.setVisibility(View.GONE);
            }
            lblApproxAmount.setText(SharedHelper.getKey(context, "currency") + "" + SharedHelper.getKey(context, "estimated_fare"));
            lblTotalDistance.setText("" + SharedHelper.getKey(context, "distance") + " KM");
            lblWalletBalance.setText(SharedHelper.getKey(context, "currency") + "" + SharedHelper.getKey(context, "wallet_balance"));
            lblEta.setText(SharedHelper.getKey(context, "eta_time"));

            if (!SharedHelper.getKey(context, "name").equalsIgnoreCase("")
                    && !SharedHelper.getKey(context, "name").equalsIgnoreCase(null)
                    && !SharedHelper.getKey(context, "name").equalsIgnoreCase("null")) {
                lblType.setText(SharedHelper.getKey(context, "name"));
            } else {
                lblType.setText("" + "Sedan");
            }

            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
        }
    }

    private void getCards() {
        Ion.with(this)
                .load(URLHelper.base + URLHelper.CARD_PAYMENT_LIST)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"))
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<com.koushikdutta.ion.Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, com.koushikdutta.ion.Response<String> response) {
                        // response contains both the headers and the string result
                        try {
                            if (response.getHeaders().code() == 200) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response.getResult());
                                    if (jsonArray.length() > 0) {
                                        CardInfo cardInfo = new CardInfo();
                                        cardInfo.setCardId("CASH");
                                        cardInfo.setCardType("CASH");
                                        cardInfo.setLastFour("CASH");
                                        cardInfoArrayList.add(cardInfo);
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject cardObj = jsonArray.getJSONObject(i);
                                            cardInfo = new CardInfo();
                                            cardInfo.setCardId(cardObj.optString("card_id"));
                                            cardInfo.setCardType(cardObj.optString("brand"));
                                            cardInfo.setLastFour(cardObj.optString("last_four"));
                                            cardInfoArrayList.add(cardInfo);
                                        }
                                    }

                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        } catch (Exception e2) {
                            e2.printStackTrace();
                            CardInfo cardInfo = new CardInfo();
                            cardInfo.setCardId("CASH");
                            cardInfo.setCardType("CASH");
                            cardInfo.setLastFour("CASH");
                            cardInfoArrayList.add(cardInfo);
                        }
                    }
                });

    }

    public void getServiceList() {

        try {
            customDialog = new CustomDialog(context);
            customDialog.setCancelable(false);

            if (customDialog != null) {
                customDialog.show();
            }

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.base + URLHelper.GET_SERVICE_LIST_API, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        utils.print("GetServices", response.toString());
                        if (SharedHelper.getKey(context, "service_type").equalsIgnoreCase("")) {
                            SharedHelper.putKey(context, "service_type", "" + response.optJSONObject(0).optString("id"));
                        }
                        if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();
                        if (response.length() > 0) {
                            currentPostion = 0;

                            SharedHelper.putKey(context, "service_type", "" + response.optJSONObject(currentPostion).optString("id"));
                            SharedHelper.putKey(context, "name", "" + response.optJSONObject(currentPostion).optString("name"));

                            ServiceListAdapter serviceListAdapter = new ServiceListAdapter(response);
                            rcvServiceTypes.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                            rcvServiceTypes.setAdapter(serviceListAdapter);
                            getProvidersList(SharedHelper.getKey(context, "service_type"));
                        }
                        if (mMap != null) {
                            mMap.clear();
                        }

                        setValuesForSourceAndDestination();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
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
                                        utils.displayMessage(getView(), context, errorObj.optString("message"));
                                    } catch (Exception e) {
                                        utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                                    }
                                    flowValue = 1;
                                    layoutChanges();
                                } else if (response.statusCode == 401) {
                                    refreshAccessToken("SERVICE_LIST");
                                } else if (response.statusCode == 422) {

                                    json = trimMessage(new String(response.data));
                                    if (json != "" && json != null) {
                                        utils.displayMessage(getView(), context, json);
                                    } else {
                                        utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                                    }
                                    flowValue = 1;
                                    layoutChanges();
                                } else if (response.statusCode == 503) {
                                    utils.displayMessage(getView(), context, context.getResources().getString(R.string.server_down));
                                    flowValue = 1;
                                    layoutChanges();
                                } else {
                                    utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                                    flowValue = 1;
                                    layoutChanges();
                                }

                            } catch (Exception e) {
                                utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                                flowValue = 1;
                                layoutChanges();
                            }

                        } else {
                            utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                            flowValue = 1;
                            layoutChanges();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " "
                            + SharedHelper.getKey(context, "access_token"));
                    return headers;
                }
            };

            RideApplication.getInstance().addToRequestQueue(jsonArrayRequest, 5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Log.e("current time:- ", "@@@@@@@ " + sdf.format(c.getTime()));
        return sdf.format(c.getTime());
    }

    public void getApproximateFare() {
        try {
            if (providerid == null) {
                providerid = "0";
            }
            customDialog = new CustomDialog(context);
            customDialog.setCancelable(false);
            if (customDialog != null)
                customDialog.show();
            JSONObject object = new JSONObject();
            String constructedURL;
            if (qrcode == null || qrcode.isEmpty()) {
                constructedURL = URLHelper.base + URLHelper.ESTIMATED_FARE_DETAILS_API + "" +
                        "?s_latitude=" + source_lat
                        + "&s_longitude=" + source_lng
                        + "&d_latitude=" + dest_lat
                        + "&d_longitude=" + dest_lng
                        + "&c_time=" + getCurrentTime()
                        + "&service_type=" + SharedHelper.getKey(context, "service_type") + "&provider_id=" + providerid + "&qr_code=" + qrcode;
            } else {
                constructedURL = URLHelper.base + URLHelper.ESTIMATED_FARE_DETAILS_API + "" +
                        "?s_latitude=" + source_lat
                        + "&s_longitude=" + source_lng
                        + "&d_latitude=" + dest_lat
                        + "&c_time=" + getCurrentTime()
                        + "&d_longitude=" + dest_lng + "&qr_code=" + qrcode + "&service_type=" + "" + "&provider_id=" + providerid;
            }

            Log.e("URL IS FOLLOWING:- ", "@@@@ " + constructedURL);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, constructedURL, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        if (!response.optString("estimated_fare").equalsIgnoreCase("")) {
                            utils.print("ApproximateResponse", response.toString());
                            SharedHelper.putKey(context, "estimated_fare", response.optString("estimated_fare"));
                            SharedHelper.putKey(context, "distance", response.optString("distance"));
                            SharedHelper.putKey(context, "eta_time", response.optString("time"));
                            SharedHelper.putKey(context, "surge", response.optString("surge"));
                            SharedHelper.putKey(context, "surge_value", response.optString("surge_value"));
                            double wallet_balance = response.optDouble("wallet_balance");
                            if (qrcode != null && !qrcode.isEmpty()) {
                                response.optJSONObject("service").optString("service_type_id");
                                SharedHelper.putKey(context, "service_type", "" + response.optJSONObject("service").optString("service_type_id"));
                                SharedHelper.putKey(context, "name", "" + response.optString("name"));
                                // providerid =    response.optJSONObject("service").optString("service_model");
                                lblType.setText(response.optJSONObject("service").optString("service_model"));
                                //SharedHelper.putKey(context, "name", response.optJSONObject("service").optString("service_model"));
                            }
                            SharedHelper.putKey(context, "wallet_balance", "" + response.optDouble("wallet_balance"));

                            if (!Double.isNaN(wallet_balance) && wallet_balance > 0) {
                                lineView.setVisibility(View.VISIBLE);
                                chkWallet.setVisibility(View.VISIBLE);
                                lnrAvailableBalance.setVisibility(View.VISIBLE);
                            } else {
                                lineView.setVisibility(View.GONE);
                                chkWallet.setVisibility(View.GONE);
                                lnrAvailableBalance.setVisibility(View.GONE);
                            }
                            flowValue = 2;
                            layoutChanges();

                            setValuesForApproximateLayout();
                        }
                    }
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
                                    utils.showAlert(context, errorObj.optString("error"));
                                } catch (Exception e) {
                                    utils.showAlert(context, context.getResources().getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 401) {
                                refreshAccessToken("APPROXIMATE_RATE");
                            } else if (response.statusCode == 422) {
                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    utils.showAlert(context, json);
                                } else {
                                    utils.showAlert(context, context.getResources().getString(R.string.please_try_again));
                                }
                            } else if (response.statusCode == 503) {
                                utils.showAlert(context, context.getResources().getString(R.string.server_down));
                            } else {
                                utils.showAlert(context, context.getResources().getString(R.string.please_try_again));
                            }

                        } catch (Exception e) {
                            utils.showAlert(context, context.getResources().getString(R.string.something_went_wrong));
                        }

                    } else {
                        utils.showAlert(context, context.getResources().getString(R.string.please_try_again));
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void getProvidersList(String strTag) {


        if (qrcode == null) {
            qrcode = "";
        }
        isAllServiceRequest = false;
        if (strTag != null && strTag.equalsIgnoreCase("0")) {
            isAllServiceRequest = true;
            strTag = "1";

        } else {
            isAllServiceRequest = false;
        }
        String providers_request = URLHelper.base + URLHelper.GET_PROVIDERS_LIST_API + "?" +
                "latitude=" + current_lat +
                "&longitude=" + current_lng +
                "&service=" + strTag +
                "&all_service=" + isAllServiceRequest +
                "&qr_code" + qrcode;

        utils.print("Get all providers", "" + providers_request);
        utils.print("service_type", "" + SharedHelper.getKey(context, "service_type"));


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(providers_request, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                utils.print("GetProvidersList", response.toString());

                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                for (int i = 0; i < lstProviderMarkers.size(); i++) {
                    lstProviderMarkers.get(i).remove();
                }

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObj = response.getJSONObject(i);
                        if (!jsonObj.getString("latitude").equalsIgnoreCase("13") && !jsonObj.getString("longitude").equalsIgnoreCase("80")) {
                            utils.print("GetProvidersList", jsonObj.getString("latitude") + "," + jsonObj.getString("longitude"));
                            if (!jsonObj.getString("latitude").equalsIgnoreCase("") && !jsonObj.getString("longitude").equalsIgnoreCase("")) {
                                Double proLat = Double.parseDouble(jsonObj.getString("latitude"));
                                Double proLng = Double.parseDouble(jsonObj.getString("longitude"));

                                if (mHashMap.containsKey(jsonObj.optInt("id"))) {
                                    Marker marker = mHashMap.get(jsonObj.optInt("id"));
                                    LatLng startPosition = marker.getPosition();
                                    LatLng newPos = new LatLng(proLat, proLng);
                                    Log.e(TAG, "onResponse:@@ hashmap contain..");
                                    marker.setPosition(newPos);
                                    marker.setRotation(getBearing(startPosition, newPos));
                                } else {


                                    MarkerOptions markerOptions = new MarkerOptions()
                                            .title(jsonObj.getString("email"))
                                            .anchor(0.5f, 0.75f)
                                            .position(new LatLng(proLat, proLng))
                                            .rotation(0.0f)
                                            .snippet(String.valueOf(jsonObj.getInt("id")))
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_location_icon));
                                    Marker marker = mMap.addMarker(markerOptions);
                                    lstProviderMarkers.add(marker);

                                }
                                builder.include(new LatLng(proLat, proLng));

                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
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
                                utils.showAlert(context, errorObj.optString("error"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (response.statusCode == 401) {
                            refreshAccessToken("PROVIDERS_LIST");
                        } else if (response.statusCode == 422) {
                            json = trimMessage(new String(response.data));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };
        RideApplication.getInstance().addToRequestQueue(jsonArrayRequest, 5000);
    }

    public void sendRequest() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        String strWallet = "";

        if (chkWallet.isChecked()) {
            strWallet = "1";
        } else {
            strWallet = "0";
        }
        if (providerid == null) {
            providerid = "";
        }
        if (qrcode == null) {
            qrcode = "";
        }
        ApiInterface mApiInterface = RetrofitClient.sendRequestAPI().create(ApiInterface.class);
        Call<ResponseBody> call;

        if (SharedHelper.getKey(context, "payment_mode").equals("CASH")) {

            if (scheduleDate != null && scheduledDate.length() > 0 && scheduleTime != null && scheduleTime.length() > 0) {
                call = mApiInterface.sendCashRequestScheduleAPI("XMLHttpRequest", SharedHelper.getKey(context, "token_type") + " "
                                + SharedHelper.getKey(context, "access_token"), "",
                        source_lat, source_lng, dest_lat, dest_lng, source_address,
                        SharedHelper.getKey(context, "destination"),
                        SharedHelper.getKey(context, "service_type"),
                        SharedHelper.getKey(context, "distance"),
                        scheduledDate, scheduledTime, SharedHelper.getKey(context, "payment_mode"), strWallet, /*providerid,*/ qrcode);
            } else {
                call = mApiInterface.sendCashRequestAPI("XMLHttpRequest", SharedHelper.getKey(context, "token_type") + " "
                                + SharedHelper.getKey(context, "access_token"), "",
                        source_lat, source_lng, dest_lat, dest_lng, source_address,
                        SharedHelper.getKey(context, "destination"),
                        SharedHelper.getKey(context, "service_type"),
                        SharedHelper.getKey(context, "distance"),
                        /* scheduledDate, scheduledTime,*/ SharedHelper.getKey(context, "payment_mode"), strWallet, /*providerid,*/ qrcode);
            }
        } else {

            if (scheduleDate != null && scheduledDate.length() > 0 && scheduleTime != null && scheduleTime.length() > 0) {

                call = mApiInterface.sendCardRequestScheduleAPI("XMLHttpRequest", SharedHelper.getKey(context, "token_type") + " "
                                + SharedHelper.getKey(context, "access_token"),
                        source_lat, source_lng, dest_lat, dest_lng, source_address,
                        SharedHelper.getKey(context, "destination"),
                        SharedHelper.getKey(context, "service_type"),
                        SharedHelper.getKey(context, "distance"),
                        scheduledDate, scheduledTime, SharedHelper.getKey(context, "payment_mode"), strWallet,
                        SharedHelper.getKey(context, "card_id"), /*providerid,*/ qrcode);

            } else {
                call = mApiInterface.sendCardRequestAPI("XMLHttpRequest", SharedHelper.getKey(context, "token_type") + " "
                                + SharedHelper.getKey(context, "access_token"),
                        source_lat, source_lng, dest_lat, dest_lng, source_address, SharedHelper.getKey(context, "destination"), SharedHelper.getKey(context, "service_type"),
                        SharedHelper.getKey(context, "distance"),/* scheduledDate, scheduledTime,*/ SharedHelper.getKey(context, "payment_mode"), strWallet,
                        SharedHelper.getKey(context, "card_id"), /*providerid,*/ qrcode);
            }
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.e("OkHttp", "SUCESS" + response.body());
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();

                if (response.body() != null) {
                    try {
                        String bodyString = new String(response.body().bytes());
                        Log.e("OkHttp", "bodyString" + bodyString);
                        try {
                            JSONObject jsonObj = new JSONObject(bodyString);
                            if (jsonObj.optString("request_id", "").equals("")) {
                                utils.displayMessage(getView(), context, jsonObj.optString("message"));
                            } else {
                                SharedHelper.putKey(context, "current_status", "");
                                SharedHelper.putKey(context, "request_id", "" + jsonObj.optString("request_id"));
                                if (!scheduledDate.equalsIgnoreCase("") && !scheduledTime.equalsIgnoreCase(""))
                                    scheduleTrip = true;
                                else
                                    scheduleTrip = false;

                                if (qrcode != null && qrcode.length() == 0) {
                                    flowValue = 3;
                                    layoutChanges();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
            }
        });
    }


    public void cancelRequest() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("cancel_reason", cancalReason);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.base + URLHelper.CANCEL_REQUEST_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                utils.print("CancelRequestResponse", response.toString());
                Toast.makeText(context, context.getResources().getString(R.string.request_cancel), Toast.LENGTH_SHORT).show();
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                mapClear();
                SharedHelper.putKey(context, "request_id", "");
                flowValue = 0;
                PreviousStatus = "";
                layoutChanges();
                setupMap();
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
                    flowValue = 4;
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                utils.displayMessage(getView(), context, errorObj.optString("message"));
                            } catch (Exception e) {
                                utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                            }
                            layoutChanges();
                        } else if (response.statusCode == 401) {
                            refreshAccessToken("CANCEL_REQUEST");
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                utils.displayMessage(getView(), context, json);
                            } else {
                                utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                            }
                            layoutChanges();
                        } else if (response.statusCode == 503) {
                            utils.displayMessage(getView(), context, context.getResources().getString(R.string.server_down));
                            layoutChanges();
                        } else {
                            utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                            layoutChanges();
                        }

                    } catch (Exception e) {
                        utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                        layoutChanges();
                    }

                } else {
                    utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                    layoutChanges();
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


    public void setValuesForSourceAndDestination() {
        if (isInternet) {
            if (!source_lat.equalsIgnoreCase("")) {
                if (!source_address.equalsIgnoreCase("")) {
//                    frmSource.setText(source_address);
                } else {
                    Utilities.getAddressUsingLatLng("source", frmSource, context, "" + source_lat, "" + source_lng);
                }
            } else {
                Utilities.getAddressUsingLatLng("source", frmSource, context, "" + current_lat, "" + current_lng);
            }

            if (!dest_lat.equalsIgnoreCase("")) {
                if (is_track.equalsIgnoreCase("YES") &&
                        (CurrentStatus.equalsIgnoreCase("STARTED") || CurrentStatus.equalsIgnoreCase("PICKEDUP")
                                || CurrentStatus.equalsIgnoreCase("ARRIVED"))) {
                    // Source Destination should not visible at the track
//                    destination.setText(SharedHelper.getKey(context, "extend_address"));
                } else {
                    destination.setText(SharedHelper.getKey(context, "destination"));
                    srcDestLayout.setVisibility(View.VISIBLE);
                }
            }


            if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                sourceLatLng = new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng));
            }
            if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("")) {
                destLatLng = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));
            }

            if (sourceLatLng != null && destLatLng != null) {
                utils.print("LatLng", "Source:" + sourceLatLng + " Destination: " + destLatLng);
                String url = getUrl(sourceLatLng.latitude, sourceLatLng.longitude, destLatLng.latitude, destLatLng.longitude);
                fetchUrl = new FetchUrl();
                fetchUrl.execute(url);
            }

        }
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
                if (tag.equalsIgnoreCase("SERVICE_LIST")) {
                    getServiceList();
                } else if (tag.equalsIgnoreCase("APPROXIMATE_RATE")) {
                    getApproximateFare();
                } else if (tag.equalsIgnoreCase("SEND_REQUEST")) {
                    sendRequest();
                } else if (tag.equalsIgnoreCase("CANCEL_REQUEST")) {
                    cancelRequest();
                } else if (tag.equalsIgnoreCase("PROVIDERS_LIST")) {
                    getProvidersList("0");
                } else if (tag.equalsIgnoreCase("SUBMIT_REVIEW")) {
                    submitReviewCall();
                } else if (tag.equalsIgnoreCase("PAY_NOW")) {
                    payNow();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = "";
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    SharedHelper.putKey(context, "loggedIn", context.getResources().getString(R.string.False));
//                    utils.GoToBeginActivity(getActivity());
                    Intent mainIntent;
                    if (AccessDetails.demo_build) {
                        mainIntent = new Intent(context, AccessKeyActivity.class);
                    } else {
                        mainIntent = new Intent(context, BeginScreen.class);
                    }
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(mainIntent);
                    activity.finish();
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

    private String getDirectionsUrl(LatLng sourceLatLng, LatLng destLatLng) {

        // Origin of routelng;
        String str_origin = "origin=" + source_lat + "," + source_lng;
        String str_dest = "destination=" + dest_lat + "," + dest_lng;
        // Sensor enabled
        String sensor = "sensor=false";
        // Waypoints
        String waypoints = "";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + waypoints;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        utils.print("url", url.toString());
        return url;

    }

    private void showChooser() {
        Intent intent = new Intent(getActivity(), com.eziride.user.Activities.Payment.class);
        startActivityForResult(intent, 5555);
    }

    private void getCardDetailsForPayment(CardInfo cardInfo) {
        if (cardInfo.getLastFour().equals("CASH")) {
            SharedHelper.putKey(context, "payment_mode", "CASH");
            imgPaymentType.setImageResource(R.drawable.money_icon);
            if (SharedHelper.getKey(context, "isCashAccept").equalsIgnoreCase("true"))
                lblPaymentType.setText("CASH");
            else
                lblPaymentType.setText(getString(R.string.no_cash_payment_txt));

        } else {
            SharedHelper.putKey(context, "card_id", cardInfo.getCardId());
            SharedHelper.putKey(context, "payment_mode", "CARD");
            SharedHelper.putKey(context, "card_num", "XXXX-XXXX-XXXX-" + cardInfo.getLastFour());
            imgPaymentType.setImageResource(R.drawable.visa);
            lblPaymentType.setText("XXXX-XXXX-XXXX-" + cardInfo.getLastFour());
        }
    }

    public void payNow() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("payment_mode", paymentMode);
            object.put("is_paid", isPaid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.base + URLHelper.PAY_NOW_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                utils.print("PayNowRequestResponse", response.toString());
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                flowValue = 6;
                layoutChanges();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                String json = "";
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                utils.displayMessage(getView(), context, errorObj.optString("message"));
                            } catch (Exception e) {
                                utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            refreshAccessToken("PAY_NOW");
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                utils.displayMessage(getView(), context, json);
                            } else {
                                utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            utils.displayMessage(getView(), context, context.getResources().getString(R.string.server_down));
                        } else {
                            utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                    }

                } else {
                    utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        RideApplication.getInstance().addToRequestQueue(jsonObjectRequest, 5000);

    }

   /* private void fund(){
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,url,
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(tag, response.toString());
                activity.hideDialog();
                try {
                    activity.onRequestServed(response, code);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(tag, "Error: " + error.getMessage());
                Log.e(tag, "Site Info Error: " + error.getMessage());
                Toast.makeText(activity.getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                activity.hideDialog();
                try {
                    activity.onRequestServed(null,code);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }) {

            */

    /**
     * Passing some request headers
     *//*
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("key", "Value");
                return headers;
            }
        };
    }*/
    private void checkStatus() {
        try {

            utils.print("Handler", "Inside");
            if (isInternet) {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                        URLHelper.base + URLHelper.REQUEST_STATUS_CHECK_API, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        SharedHelper.putKey(context, "req_status", "");
                        try {
                            if (customDialog != null && customDialog.isShowing()) {
                                customDialog.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        reqStatus = "";
                        utils.print("System Response11", "" + response.toString());

                        if (response.optJSONArray("data") != null && response.optJSONArray("data").length() > 0) {
                            utils.print("System response", "not null");
                            try {
                                JSONArray requestStatusCheck = response.optJSONArray("data");
                                JSONObject requestStatusCheckObject = requestStatusCheck.getJSONObject(0);
                                //Driver Detail
                                if (requestStatusCheckObject.optJSONObject("provider") != null) {
                                    driver = new Driver();
                                    driver.setFname(requestStatusCheckObject.optJSONObject("provider").optString("first_name"));
                                    driver.setLname(requestStatusCheckObject.optJSONObject("provider").optString("last_name"));
                                    driver.setEmail(requestStatusCheckObject.optJSONObject("provider").optString("email"));
                                    driver.setMobile(requestStatusCheckObject.optJSONObject("provider").optString("mobile"));
                                    driver.setImg(requestStatusCheckObject.optJSONObject("provider").optString("avatar"));
                                    driver.setRating(requestStatusCheckObject.optJSONObject("provider").optString("rating"));
                                }
                                String status = requestStatusCheckObject.optString("status");
                                String error_state = requestStatusCheckObject.optString("error_state");
                                is_track = requestStatusCheckObject.optString("is_track");
                                SharedHelper.putKey(context, "track_status", is_track);
                                reqStatus = requestStatusCheckObject.optString("status");
                                SharedHelper.putKey(context, "req_status", requestStatusCheckObject.optString("status"));
                                String wallet = requestStatusCheckObject.optString("use_wallet");
                                source_lat = requestStatusCheckObject.optString("s_latitude");
                                source_lng = requestStatusCheckObject.optString("s_longitude");
                                dest_lat = requestStatusCheckObject.optString("d_latitude");
                                dest_lng = requestStatusCheckObject.optString("d_longitude");
                                strOTP = requestStatusCheckObject.optString("otp");

                                if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                                    LatLng myLocation = new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng));
                                    CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                                }

                                // surge price
                                if (requestStatusCheckObject.optString("surge").equalsIgnoreCase("1")) {
                                    lblSurgePrice.setVisibility(View.VISIBLE);
                                } else {
                                    lblSurgePrice.setVisibility(View.GONE);
                                }

                                setTrackStatus();

                                utils.print("PreviousStatus", "" + PreviousStatus);

                                if (!PreviousStatus.equals(status)) {
                                    mMap.clear();
                                    PreviousStatus = status;
                                    flowValue = 8;
                                    layoutChanges();
                                    SharedHelper.putKey(context, "request_id", "" + requestStatusCheckObject.optString("id"));
                                    reCreateMap();
                                    CurrentStatus = status;
                                    utils.print("ResponseStatus", "SavedCurrentStatus: " + CurrentStatus + " Status: " + status);
                                    switch (status) {
                                        case "SEARCHING":
                                            show(lnrWaitingForProviders);
                                            //rippleBackground.startRippleAnimation();
                                            strTag = "search_completed";
                                            if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                                                LatLng myLocation1 = new LatLng(Double.parseDouble(source_lat),
                                                        Double.parseDouble(source_lng));
                                                CameraPosition cameraPosition1 = new CameraPosition.Builder().target(myLocation1).zoom(16).build();
                                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
                                            }
                                            break;
                                        case "CANCELLED":
                                            strTag = "";
                                            if (reasonDialog != null) {
                                                if (reasonDialog.isShowing()) {
                                                    reasonDialog.dismiss();
                                                }
                                            }
                                            if (cancelRideDialog != null) {
                                                if (cancelRideDialog.isShowing()) {
                                                    cancelRideDialog.dismiss();
                                                }
                                            }
                                            imgSos.setVisibility(View.GONE);
                                            break;
                                        case "ACCEPTED":
                                            strTag = "ride_accepted";
                                            try {
                                                JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                SharedHelper.putKey(context, "provider_mobile_no", "" + provider.optString("mobile"));
                                                lblProvider.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                                if (provider.optString("avatar").startsWith("http"))
                                                    Picasso.with(context).load(provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                else
                                                    Picasso.with(context).load(URLHelper.base + "/storage/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                lblServiceRequested.setText(service_type.optString("name"));
                                                lblModelNumber.setText(provider_service.optString("service_model") + "\n" + provider_service.optString("service_number"));
                                                Picasso.with(context).load(service_type.optString("image"))
                                                        .placeholder(R.drawable.car_select).error(R.drawable.car_select)
                                                        .into(imgServiceRequested);
                                                ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                                //lnrAfterAcceptedStatus.setVisibility(View.GONE);
                                                lblStatus.setText(context.getResources().getString(R.string.arriving));
                                                lnrOTP.setVisibility(View.VISIBLE);
                                                lblOTP.setText(context.getResources().getString(R.string.otp) + " " + strOTP);
                                                AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                                btnCancelTrip.setText(context.getResources().getString(R.string.cancel_trip));
                                                show(lnrProviderAccepted);
                                                flowValue = 9;
                                                layoutChanges();
                                                if (is_track.equalsIgnoreCase("YES")) {
                                                    flowValue = 10;
                                                    txtChange.setVisibility(View.GONE);
                                                } else {
                                                    flowValue = 4;
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        case "STARTED":
                                            strTag = "ride_started";
                                            try {
                                                JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                SharedHelper.putKey(context, "provider_mobile_no", "" + provider.optString("mobile"));
                                                lblProvider.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                                if (provider.optString("avatar").startsWith("http"))
                                                    Picasso.with(context).load(provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                else
                                                    Picasso.with(context).load(URLHelper.base + "/storage/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                lblServiceRequested.setText(service_type.optString("name"));
                                                lblModelNumber.setText(provider_service.optString("service_model") + "\n" + provider_service.optString("service_number"));
                                                Picasso.with(context).load(service_type.optString("image")).placeholder(R.drawable.car_select)
                                                        .error(R.drawable.car_select).into(imgServiceRequested);
                                                ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                                //lnrAfterAcceptedStatus.setVisibility(View.GONE);
                                                lblStatus.setText(context.getResources().getString(R.string.arriving));
                                                btnCancelTrip.setText(context.getResources().getString(R.string.cancel_trip));
                                                lnrOTP.setVisibility(View.VISIBLE);
                                                lblOTP.setText(context.getResources().getString(R.string.otp) + " " + strOTP);
                                                AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                                if (is_track.equalsIgnoreCase("YES")) {
                                                    flowValue = 10;
                                                    txtChange.setVisibility(View.GONE);
                                                } else {
                                                    flowValue = 4;
                                                }
                                                layoutChanges();
                                                if (!requestStatusCheckObject.optString("schedule_at").equalsIgnoreCase("null")) {
                                                    SharedHelper.putKey(context, "current_status", "");
                                                    Intent intent = new Intent(getActivity(), HistoryActivity.class);
                                                    intent.putExtra("tag", "upcoming");
                                                    startActivity(intent);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;

                                        case "ARRIVED":
                                            once = true;
                                            strTag = "ride_arrived";
                                            utils.print("MyTest", "ARRIVED");
                                            try {
                                                utils.print("MyTest", "ARRIVED TRY");
                                                JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                lblProvider.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                                if (provider.optString("avatar").startsWith("http"))
                                                    Picasso.with(context).load(provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                else
                                                    Picasso.with(context).load(URLHelper.base + "/storage/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                lblServiceRequested.setText(service_type.optString("name"));
                                                lblModelNumber.setText(provider_service.optString("service_model") + "\n" + provider_service.optString("service_number"));
                                                Picasso.with(context).load(service_type.optString("image")).placeholder(R.drawable.car_select).error(R.drawable.car_select).into(imgServiceRequested);
                                                ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                                lnrAfterAcceptedStatus.setVisibility(View.VISIBLE);
                                                tripLine.setVisibility(View.VISIBLE);
                                                lblStatus.setText(context.getResources().getString(R.string.arrived));
                                                btnCancelTrip.setText(context.getResources().getString(R.string.cancel_trip));
                                                lnrOTP.setVisibility(View.VISIBLE);
                                                lblOTP.setText(context.getResources().getString(R.string.otp) + " " + strOTP);
                                                AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                                //imgSos.setVisibility(View.VISIBLE);
                                                if (is_track.equalsIgnoreCase("YES")) {
                                                    flowValue = 10;
                                                    txtChange.setVisibility(View.GONE);
                                                } else {
                                                    flowValue = 4;
                                                }
                                                layoutChanges();
                                            } catch (Exception e) {
                                                utils.print("MyTest", "ARRIVED CATCH");
                                                e.printStackTrace();
                                            }
                                            break;

                                        case "PICKEDUP":
                                            once = true;
                                            strTag = "ride_picked";
                                            try {
                                                JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                lblProvider.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                                if (provider.optString("avatar").startsWith("http"))
                                                    Picasso.with(context).load(provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                else
                                                    Picasso.with(context).load(URLHelper.base + "/storage/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                lblServiceRequested.setText(service_type.optString("name"));
                                                lblModelNumber.setText(provider_service.optString("service_model") + "\n" + provider_service.optString("service_number"));
                                                Picasso.with(context).load(service_type.optString("image")).placeholder(R.drawable.car_select).error(R.drawable.car_select).into(imgServiceRequested);
                                                ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                                lnrAfterAcceptedStatus.setVisibility(View.VISIBLE);
                                                tripLine.setVisibility(View.VISIBLE);
                                                imgSos.setVisibility(View.VISIBLE);
                                                lnrOTP.setVisibility(View.GONE);
                                                lblStatus.setText(context.getResources().getString(R.string.picked_up));
                                                btnCancelTrip.setText(context.getResources().getString(R.string.share));
                                                AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                                imgSos.setVisibility(View.VISIBLE);
                                                if (is_track.equalsIgnoreCase("YES")) {
                                                    flowValue = 10;
                                                    txtChange.setVisibility(View.VISIBLE);
                                                } else {
                                                    flowValue = 4;
                                                    txtChange.setVisibility(View.GONE);
                                                }
                                                layoutChanges();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        case "DROPPED":
                                            once = true;
                                            strTag = "";
                                            imgSos.setVisibility(View.VISIBLE);
                                            try {
                                                JSONObject provider = requestStatusCheckObject.optJSONObject("provider");
                                                if (requestStatusCheckObject.optJSONObject("payment") != null) {
                                                    JSONObject payment = requestStatusCheckObject.optJSONObject("payment");
                                                    if (payment.optString("cancellation_penalty").equalsIgnoreCase("0")) {
                                                        llCancellationLayout.setVisibility(View.GONE);
                                                    } else {
                                                        llCancellationLayout.setVisibility(View.VISIBLE);
                                                        lblCancelCharge.setText("" + SharedHelper.getKey(context, "currency") + "" + payment.optString("cancellation_penalty"));
                                                    }

                                                    isPaid = requestStatusCheckObject.optString("paid");
                                                    totalRideAmount = payment.optInt("payable");
                                                    walletAmountDetected = payment.optInt("wallet");
                                                    couponAmountDetected = payment.optInt("discount");
                                                    paymentMode = requestStatusCheckObject.optString("payment_mode");
                                                    lblDistanceCovered.setText(requestStatusCheckObject.optString("distance") + " KM");
                                                    lblBasePrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("fixed"));
                                                    lblTaxPrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("tax"));
                                                    lblDistancePrice.setText(SharedHelper.getKey(context, "currency")
                                                            + "" + payment.optString("distance"));
                                                    lblTimeTaken.setText(requestStatusCheckObject.optString("travel_time") + " mins");
                                                    lblDiscountPrice.setText(SharedHelper.getKey(context, "currency") + "" + couponAmountDetected);
                                                    lblWalletPrice.setText(SharedHelper.getKey(context, "currency") + "" + walletAmountDetected);
                                                    //lblCommision.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("commision"));
                                                    lblTotalPrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("payable"));

                                                    //Review values set
                                                    lblProviderNameRate.setText(context.getResources().getString(R.string.rate_provider) + " " + provider.optString("first_name") + " " + provider.optString("last_name"));
                                                    if (provider.optString("avatar").startsWith("http")) {
                                                        Picasso.with(context).load(provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                    } else {
                                                        Picasso.with(context).load(URLHelper.base + "/storage/" + provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                    }

                                                    if (requestStatusCheckObject.optString("booking_id") != null &&
                                                            !requestStatusCheckObject.optString("booking_id").equalsIgnoreCase("")) {
                                                        booking_id.setText(requestStatusCheckObject.optString("booking_id"));
                                                    } else {
                                                        bookingIDLayout.setVisibility(View.GONE);
                                                        booking_id.setVisibility(View.GONE);
                                                    }

                                                    if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
//                                                        btnPayNow.setVisibility(View.GONE);
//                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
//                                                        btnPayNow.setVisibility(View.GONE);
//                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");

                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH") && walletAmountDetected > 0
                                                            && couponAmountDetected == 0 && totalRideAmount > 0) {
//                                                        btnPayNow.setVisibility(View.GONE);
//                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.GONE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected > 0 && couponAmountDetected == 0 &&
                                                            totalRideAmount == 0) {
//                                                        btnPayNow.setVisibility(View.GONE);
//                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.GONE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected == 0 && couponAmountDetected > 0 &&
                                                            totalRideAmount > 0) {
//                                                        btnPayNow.setVisibility(View.GONE);
//                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected == 0 && couponAmountDetected > 0 &&
                                                            totalRideAmount == 0) {
//                                                        btnPayNow.setVisibility(View.GONE);
//                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected == 0 && couponAmountDetected == 0 &&
                                                            totalRideAmount > 0) {
//                                                        btnPayNow.setVisibility(View.GONE);
//                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
//                                                        btnPayNow.setVisibility(View.GONE);
//                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
//                                                        btnPayNow.setVisibility(View.VISIBLE);
//                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
//                                                        btnPayNow.setVisibility(View.VISIBLE);
//                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.GONE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD") && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount == 0) {
//                                                        btnPayNow.setVisibility(View.GONE);
//                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.GONE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD") && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
//                                                        btnPayNow.setVisibility(View.VISIBLE);
//                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD") && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
//                                                        btnPayNow.setVisibility(View.GONE);
//                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected == 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
//                                                        btnPayNow.setVisibility(View.VISIBLE);
//                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
//                                                        btnPayNow.setVisibility(View.GONE);
//                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                        Toast.makeText(context, "payment failed", Toast.LENGTH_SHORT).show();
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH") && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
//                                                        btnPayNow.setVisibility(View.GONE);
//                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected == 0 && couponAmountDetected == 0 && totalRideAmount == 0) {
//                                                        btnPayNow.setVisibility(View.GONE);
//                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                        // btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                        //  btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                        //   btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                        //    btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                        //  btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;

                                        case "COMPLETED":
                                            strTag = "";
                                            try {
                                                if (requestStatusCheckObject.optJSONObject("payment") != null) {
                                                    JSONObject payment = requestStatusCheckObject.optJSONObject("payment");
                                                    JSONObject provider = requestStatusCheckObject.optJSONObject("provider");
                                                    isPaid = requestStatusCheckObject.optString("paid");
                                                    paymentMode = requestStatusCheckObject.optString("payment_mode");
                                                    imgSos.setVisibility(View.GONE);
                                                    totalRideAmount = payment.optInt("payable");
                                                    walletAmountDetected = payment.optInt("wallet");
                                                    couponAmountDetected = payment.optInt("discount");

                                                    lblBasePrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                            + payment.optString("fixed"));
                                                    lblTaxPrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                            + payment.optString("tax"));
                                                    lblDistancePrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                            + payment.optString("distance"));
                                                    lblTotalPrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                            + payment.optString("payable"));
                                                    lblDiscountPrice.setText(SharedHelper.getKey(context, "currency") + "" + couponAmountDetected);

                                                    lblTimeTaken.setText(requestStatusCheckObject.optString("travel_time"));

                                                    lblWalletPrice.setText(SharedHelper.getKey(context, "currency") + "" + walletAmountDetected);

                                                    //Review values set
                                                    lblProviderNameRate.setText(context.getResources().getString(R.string.rate_provider) + " " + provider.optString("first_name") + " " + provider.optString("last_name"));
                                                    if (provider.optString("avatar").startsWith("http")) {
                                                        Picasso.with(context).load(provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProviderRate);
                                                    } else {
                                                        Picasso.with(context).load(URLHelper.base + "/storage/" + provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProviderRate);
                                                    }

                                                    if (requestStatusCheckObject.optString("booking_id") != null &&
                                                            !requestStatusCheckObject.optString("booking_id").equalsIgnoreCase("")) {
                                                        booking_id.setText(requestStatusCheckObject.optString("booking_id"));
                                                    } else {
                                                        bookingIDLayout.setVisibility(View.GONE);
                                                        booking_id.setVisibility(View.GONE);
                                                    }

                                                    if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected > 0 && couponAmountDetected > 0
                                                            && totalRideAmount == 0) {
//                                                        btnPayNow.setVisibility(View.GONE);
//                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);

                                                        lblPaymentTypeInvoice.setText("CASH");

                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
//                                                        btnPayNow.setVisibility(View.GONE);
//                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
//                                                        btnPayNow.setVisibility(View.GONE);
//                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.GONE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount == 0) {
//                                                        btnPayNow.setVisibility(View.GONE);
//                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.GONE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
//                                                        btnPayNow.setVisibility(View.GONE);
//                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
//                                                        btnPayNow.setVisibility(View.GONE);
//                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected == 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
//                                                        btnPayNow.setVisibility(View.GONE);
//                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.GONE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected == 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                        //btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
//                                                        btnPayNow.setVisibility(View.GONE);
//                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
//                                                        btnPayNow.setVisibility(View.VISIBLE);
//                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
//                                                        btnPayNow.setVisibility(View.VISIBLE);
//                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.GONE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount == 0) {
//                                                        btnPayNow.setVisibility(View.GONE);
//                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.GONE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
//                                                        btnPayNow.setVisibility(View.VISIBLE);
//                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
//                                                        btnPayNow.setVisibility(View.GONE);
//                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD") && walletAmountDetected == 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
//                                                        btnPayNow.setVisibility(View.VISIBLE);
//                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.GONE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected == 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                        //  btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
//                                                        btnPayNow.setVisibility(View.GONE);
//                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
//                                                        btnPayNow.setVisibility(View.GONE);
//                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                        //btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected == 0 && couponAmountDetected == 0 && totalRideAmount == 0) {
                                                        //    btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                        //   btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                        //    btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                        //    btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                        // btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();

                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                        //  btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected == 0 && couponAmountDetected == 0 && totalRideAmount == 0) {
                                                        //   btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    }

                                                } else {
                                                    flowValue = 6;
                                                    layoutChanges();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                    }
                                } else if (status.equalsIgnoreCase("DROPPED") && isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD") && error_state != null && !error_state.equalsIgnoreCase("null")) {
                                    if (foregrounded())
                                        showCustomToast(rootView);
                                }
                                if ("ACCEPTED".equals(status) || "STARTED".equals(status) ||
                                        "ARRIVED".equals(status) || "PICKEDUP".equals(status) || "DROPPED".equals(status)) {
                                    utils.print("Livenavigation", "" + status);
                                    if (requestStatusCheckObject.optJSONObject("provider") != null) {
                                        utils.print("Destination Current Lat", "" + requestStatusCheckObject.optJSONObject("provider").optString("latitude"));
                                        utils.print("Destination Current Lng", "" + requestStatusCheckObject.optJSONObject("provider").optString("longitude"));
                                        liveNavigation(status, requestStatusCheckObject.getJSONObject("provider").optString("latitude"),
                                                requestStatusCheckObject.getJSONObject("provider").optString("longitude"));
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                            }
                        } else if (PreviousStatus.equalsIgnoreCase("SEARCHING")) {
                            SharedHelper.putKey(context, "current_status", "");
                            if (scheduledDate != null && scheduledTime != null && !scheduledDate.equalsIgnoreCase("")
                                    && !scheduledTime.equalsIgnoreCase("")) {

                            } else {
                                Toast.makeText(context, context.getResources().getString(R.string.no_drivers_found), Toast.LENGTH_SHORT).show();
                            }
                            strTag = "";
                            PreviousStatus = "";
                            flowValue = 0;
                            layoutChanges();
                            if (reasonDialog != null) {
                                if (reasonDialog.isShowing()) {
                                    reasonDialog.dismiss();
                                }
                            }
                            if (cancelRideDialog != null) {
                                if (cancelRideDialog.isShowing()) {
                                    cancelRideDialog.dismiss();
                                }
                            }
                            CurrentStatus = "";
                            mMap.clear();
                            mapClear();
                        } else if (PreviousStatus.equalsIgnoreCase("STARTED")) {
                            SharedHelper.putKey(context, "current_status", "");
                            Toast.makeText(context, context.getResources().getString(R.string.driver_busy), Toast.LENGTH_SHORT).show();
                            strTag = "";
                            PreviousStatus = "";
                            flowValue = 0;
                            layoutChanges();
                            if (reasonDialog != null) {
                                if (reasonDialog.isShowing()) {
                                    reasonDialog.dismiss();
                                }
                            }
                            if (cancelRideDialog != null) {
                                if (cancelRideDialog.isShowing()) {
                                    cancelRideDialog.dismiss();
                                }
                            }
                            CurrentStatus = "";
                            mMap.clear();
                            mapClear();
                        } else if (PreviousStatus.equalsIgnoreCase("ARRIVED")) {
                            SharedHelper.putKey(context, "current_status", "");
                            Toast.makeText(context, context.getResources().getString(R.string.driver_busy), Toast.LENGTH_SHORT).show();
                            strTag = "";
                            PreviousStatus = "";
                            flowValue = 0;
                            layoutChanges();
                            if (reasonDialog != null) {
                                if (reasonDialog.isShowing()) {
                                    reasonDialog.dismiss();
                                }
                            }
                            if (cancelRideDialog != null) {
                                if (cancelRideDialog.isShowing()) {
                                    cancelRideDialog.dismiss();
                                }
                            }
                            CurrentStatus = "";
                            mMap.clear();
                            mapClear();
                        } else {
                            if (flowValue == 0) {
                                getProvidersList("0");
                            } else if (flowValue == 1) {
                                getProvidersList(SharedHelper.getKey(context, "service_type"));
                            }
                            CurrentStatus = "";
                            imgSos.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        utils.print("Error", error.toString());
                        try {
                            if (customDialog != null && customDialog.isShowing()) {
                                customDialog.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        reqStatus = "";
                        SharedHelper.putKey(context, "req_status", "");
                    }
                }) {
                    @Override
                    public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                        utils.print("System Authorization", "token_type" + SharedHelper.getKey(context, "token_type"));
                        utils.print("System Authorization", "access_token" + SharedHelper.getKey(context, "access_token"));
                        return headers;
                    }
                };

                RideApplication.getInstance().addToRequestQueue(jsonObjectRequest, 5000);

            } else {
                utils.displayMessage(getView(), context, context.getResources().getString(R.string.oops_connect_your_internet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTrackStatus() {

    }

    private void mapClear() {
        if (parserTask != null) {
            parserTask.cancel(true);
        }
        if (fetchUrl != null) {
            fetchUrl.cancel(true);
        }
        mMap.clear();
        source_lat = "";
        source_lng = "";
        dest_lat = "";
        dest_lng = "";
        if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
            LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void reCreateMap() {
        if (mMap != null) {
            if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                sourceLatLng = new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng));
            }
            if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("")) {
                destLatLng = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));
            }
            utils.print("LatLng", "Source:" + sourceLatLng + " Destination: " + destLatLng);
            //String url = getDirectionsUrl(sourceLatLng, destLatLng);
            String url = getUrl(sourceLatLng.latitude, sourceLatLng.longitude, destLatLng.latitude, destLatLng.longitude);
            fetchUrl = new FetchUrl();
            fetchUrl.execute(url);
           /* DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);*/
        }
    }


    private void show(final View view) {
        mIsShowing = true;
        ViewPropertyAnimator animator = view.animate()
                .translationY(0)
                .setInterpolator(INTERPOLATOR)
                .setDuration(500);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mIsShowing = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // Canceling a show should hide the view
                mIsShowing = false;
                if (!mIsHiding) {
                    hide(view);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        animator.start();
    }

    private void hide(final View view) {
        mIsHiding = true;
        ViewPropertyAnimator animator = view.animate()
                .translationY(view.getHeight())
                .setInterpolator(INTERPOLATOR)
                .setDuration(200);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                // Prevent drawing the View after it is gone
                mIsHiding = false;
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // Canceling a hide should show the view
                mIsHiding = false;
                if (!mIsShowing) {
                    show(view);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        animator.start();
    }


    public void liveNavigation(String status, String lat, String lng) {
        Log.e("Livenavigation", "ProLat" + lat + " ProLng" + lng);
        if (!lat.equalsIgnoreCase("") && !lng.equalsIgnoreCase("")) {
            Double proLat = Double.parseDouble(lat);
            Double proLng = Double.parseDouble(lng);

            Float rotation = 0.0f;

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(proLat, proLng))
                    .rotation(rotation)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_location_icon));

            if (providerMarker != null) {
                rotation = getBearing(providerMarker.getPosition(), markerOptions.getPosition());
                markerOptions.rotation(rotation * (180.0f / (float) Math.PI));
                providerMarker.remove();
            }

            providerMarker = mMap.addMarker(markerOptions);
        }

    }

    public float getBearing(LatLng oldPosition, LatLng newPosition) {
        double deltaLongitude = newPosition.longitude - oldPosition.longitude;
        double deltaLatitude = newPosition.latitude - oldPosition.latitude;
        double angle = (Math.PI * .5f) - Math.atan(deltaLatitude / deltaLongitude);

        if (deltaLongitude > 0) {
            return (float) angle;
        } else if (deltaLongitude < 0) {
            return (float) (angle + Math.PI);
        } else if (deltaLatitude < 0) {
            return (float) Math.PI;
        }

        return 0.0f;
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            enableLoc();
        }
    }

    private void enableLoc() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.d("Location error", "Connected");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        mGoogleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                        Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                    }
                }).build();
        mGoogleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                Log.e("GPS Location", "onResult: " + result);
                Log.e("GPS Location", "onResult Status: " + result.getStatus());
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;

                    case LocationSettingsStatusCodes.CANCELED:
                        showDialogForGPSIntent();
                        break;
                }
            }
        });
//	        }

    }

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
                    /*if (paymentType != 0) {
                        if (SharedHelper.getKey(context, "isCashAccept").equalsIgnoreCase("true") && SharedHelper.getKey(context, "payment_mode").equalsIgnoreCase("CASH") && paymentType != 1) {
                            imgPaymentType.setImageResource(R.drawable.money_icon);
                            lblPaymentType.setText("CASH");
                            paymentType = 1;
                        } else if (SharedHelper.getKey(context, "payment_mode").equalsIgnoreCase("CARD") && paymentType != 2) {
                            imgPaymentType.setImageResource(R.drawable.visa);
                            lblPaymentType.setText(SharedHelper.getKey(context, "card_num"));
                            paymentType = 2;
                        } else if (paymentType != 3) {
                            lblPaymentType.setText(getString(R.string.no_cash_payment_txt));
                            paymentType = 3;
                        }
                    }*/
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


    public void submitReviewCall() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("rating", feedBackRating);

            //  if (feedBackRating.equalsIgnoreCase("5")) {
            // ratingReason = "";
            object.put("comment", "" + txtCommentsRate.getText().toString());
           /* } else {
                object.put("comment", ratingReason);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.base + URLHelper.RATE_PROVIDER_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                utils.print("SubmitRequestResponse", response.toString());
                utils.hideKeypad(context, activity.getCurrentFocus());
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                destination.setText("");
                frmDest.setText("");
                //  ratingReason = "";
                txtCommentsRate.setText("");
                feedBackRating = "0";
                mapClear();
                flowValue = 0;
                layoutChanges();
                if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
                    LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }

                checkPaymentType();
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
                                utils.displayMessage(getView(), context, errorObj.optString("message"));
                            } catch (Exception e) {
                                utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                            }

                        } else if (response.statusCode == 401) {
                            refreshAccessToken("SUBMIT_REVIEW");
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                utils.displayMessage(getView(), context, json);
                            } else {
                                utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            utils.displayMessage(getView(), context, context.getResources().getString(R.string.server_down));
                        } else {
                            utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                    }

                } else {
                    utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
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


    private class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.MyViewHolder> {
        JSONArray jsonArray;
        private SparseBooleanArray selectedItems;
        int selectedPosition;

        public ServiceListAdapter(JSONArray array) {
            this.jsonArray = array;
        }


        @Override
        public ServiceListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(getActivity()).inflate(R.layout.service_type_list_item, null);
            return new ServiceListAdapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ServiceListAdapter.MyViewHolder holder, final int position) {
            utils.print("Title: ", "" + jsonArray.optJSONObject(position).optString("name") + " Image: " + jsonArray.optJSONObject(position).optString("image") + " Grey_Image:" + jsonArray.optJSONObject(position).optString("grey_image"));

            holder.serviceTitle.setText(jsonArray.optJSONObject(position).optString("name"));
            if (position == currentPostion) {
                SharedHelper.putKey(context, "service_type", "" + jsonArray.optJSONObject(position).optString("id"));
                Glide.with(activity).load(jsonArray.optJSONObject(position).optString("image"))
                        .apply(new RequestOptions().placeholder(R.drawable.car_select).dontAnimate().error(R.drawable.car_select))
                        .into(holder.serviceImg);
                holder.selector_background.setBackgroundResource(R.drawable.full_rounded_button_accent);
                holder.serviceTitle.setTextColor(ContextCompat.getColor(context, R.color.text_color_white));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                        (Math.round(context.getResources().getDimension(R.dimen._50sdp)), Math.round(context.getResources().getDimension(R.dimen._50sdp)));
                holder.serviceImg.setLayoutParams(layoutParams);
            } else {
                Glide.with(activity).load(jsonArray.optJSONObject(position).optString("image"))
                        .apply(new RequestOptions().placeholder(R.drawable.car_select).dontAnimate().error(R.drawable.car_select))
                        .into(holder.serviceImg);
                holder.selector_background.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                holder.serviceTitle.setTextColor(ContextCompat.getColor(context, R.color.black_text_color));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                        (Math.round(context.getResources().getDimension(R.dimen._40sdp)), Math.round(context.getResources().getDimension(R.dimen._40sdp)));
                holder.serviceImg.setLayoutParams(layoutParams);
            }


            holder.linearLayoutOfList.setTag(position);

            holder.linearLayoutOfList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position == currentPostion) {
                        try {
                            lnrHidePopup.setVisibility(View.VISIBLE);
                            showProviderPopup(jsonArray.getJSONObject(position));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    currentPostion = Integer.parseInt(view.getTag().toString());
                    SharedHelper.putKey(context, "service_type", "" + jsonArray.optJSONObject(Integer.parseInt(view.getTag().toString())).optString("id"));
                    SharedHelper.putKey(context, "name", "" + jsonArray.optJSONObject(currentPostion).optString("name"));
                    notifyDataSetChanged();
                    utils.print("service_type", "" + SharedHelper.getKey(context, "service_type"));
                    utils.print("Service name", "" + SharedHelper.getKey(context, "name"));
                    getProvidersList(SharedHelper.getKey(context, "service_type"));
                }
            });
        }

        @Override
        public int getItemCount() {
            return jsonArray.length();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            MyTextView serviceTitle;
            ImageView serviceImg;
            LinearLayout linearLayoutOfList;
            FrameLayout selector_background;

            public MyViewHolder(View itemView) {
                super(itemView);
                serviceTitle = (MyTextView) itemView.findViewById(R.id.serviceItem);
                serviceImg = (ImageView) itemView.findViewById(R.id.serviceImg);
                linearLayoutOfList = itemView.findViewById(R.id.LinearLayoutOfList);
                selector_background = (FrameLayout) itemView.findViewById(R.id.selector_background);
                height = itemView.getHeight();
                width = itemView.getWidth();
            }
        }
    }

   /* private class ReasonListAdapter extends RecyclerView.Adapter<ReasonListAdapter.MyViewHolder> {
        JSONArray jsonArray;
        private int selectedposition = -1;

        ReasonListAdapter(JSONArray array) {
            this.jsonArray = array;
        }


        @Override
        public ReasonListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.reason_list_item, null);
            return new ReasonListAdapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ReasonListAdapter.MyViewHolder holder, final int position) {
            try {
                holder.rdReason.setText(jsonArray.optJSONObject(position).optString("reason"));
                if (position == selectedposition) {
                    holder.rdReason.setChecked(true);
                    ratingReason = jsonArray.getJSONObject(position).optString("reason");
                } else {
                    holder.rdReason.setChecked(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return jsonArray.length();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            RadioButton rdReason;

            MyViewHolder(View itemView) {
                super(itemView);
                rdReason = itemView.findViewById(R.id.rdReason);
                rdReason.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!rdReason.isSelected()) {
                            selectedposition = getAdapterPosition();
                            notifyDataSetChanged();
                        }
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            ratingReason = jsonArray.getJSONObject(getAdapterPosition()).optString("reason");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }*/


    private void startAnim(ArrayList<LatLng> routeList) {
        if (mMap != null && routeList.size() > 1) {
            MapAnimator.getInstance().animateRoute(context, mMap, routeList);
        }
    }

    @Override
    public void onDestroy() {
        handleCheckStatus.removeCallbacksAndMessages(null);
//        if (mapRipple != null && mapRipple.isAnimationRunning()) {
//            mapRipple.stopRippleMapAnimation();
//        }
        super.onDestroy();
    }

    private void stopAnim() {
        if (mMap != null) {
            MapAnimator.getInstance().stopAnim();
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(context.getResources().getString(R.string.connect_to_network))
                .setCancelable(false)
                .setPositiveButton(context.getResources().getString(R.string.connect_to_wifi), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
        if (alert == null) {
            alert = builder.create();
            alert.show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObj = new JSONObject(result);
                if (!jsonObj.optString("status").equalsIgnoreCase("ZERO_RESULTS")) {
                    parserTask = new ParserTask();
                    // Invokes the thread for parsing the JSON data
                    parserTask.execute(result);
                } else {
                    mMap.clear();
                    stopAnim();
                    flowValue = 0;
                    layoutChanges();
                    gotoCurrentPosition();
                    utils.displayMessage(getView(), context, context.getResources().getString(R.string.no_service));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        DataParser parser;

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            if (result != null) {
                // Traversing through all the routes
                if (result.size() > 0) {
                    for (int i = 0; i < result.size(); i++) {
                        points = new ArrayList<>();
                        lineOptions = new PolylineOptions();

                        // Fetching i-th route
                        List<HashMap<String, String>> path = result.get(i);

                        // Fetching all the points in i-th route
                        for (int j = 0; j < path.size(); j++) {
                            HashMap<String, String> point = path.get(j);

                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);

                            points.add(position);
                        }

                        // Adding all the points in the route to LineOptions
//                        lineOptions.addAll(points);
//                        lineOptions.width(15);
//                        lineOptions.color(getResources().getColor(R.color.text_red));

                        Log.d("onPostExecute", "onPostExecute lineoptions decoded");

                    }

                    if (flowValue == 1) {
                        if (sourceMarker != null && destinationMarker != null) {
                            sourceMarker.setDraggable(true);
                            destinationMarker.setDraggable(true);
                        }
                    } else {
                        if (is_track.equalsIgnoreCase("YES") &&
                                (CurrentStatus.equalsIgnoreCase("STARTED") || CurrentStatus.equalsIgnoreCase("PICKEDUP")
                                        || CurrentStatus.equalsIgnoreCase("ARRIVED"))) {
                            if (sourceMarker != null && destinationMarker != null) {
                                sourceMarker.setDraggable(false);
                                destinationMarker.setDraggable(true);
                            }
                        } else {
                            if (sourceMarker != null && destinationMarker != null) {
                                sourceMarker.setDraggable(false);
                                destinationMarker.setDraggable(false);
                            }
                        }
                    }
                    if (flowValue != 0) {
                        if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                            LatLng location = new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng));
                            //mMap.clear();
                            if (sourceMarker != null)
                                sourceMarker.remove();

                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(location).snippet(frmSource.getText().toString())
                                    .title("source").draggable(true)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.yellow_pin));
                            marker = mMap.addMarker(markerOptions);
                            sourceMarker = mMap.addMarker(markerOptions);
                            //CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(18).build();
                            //mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                        if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("")) {
                            destLatLng = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));
                            if (destinationMarker != null)
                                destinationMarker.remove();

                            MarkerOptions destMarker = new MarkerOptions()
                                    .position(destLatLng).title("destination").snippet(frmDest.getText().toString()).draggable(true)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));
                            destinationMarker = mMap.addMarker(destMarker);
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(sourceMarker.getPosition());
                            builder.include(destinationMarker.getPosition());
                            LatLngBounds bounds = builder.build();
                            int padding = 100; // offset from edges of the map in pixels
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                            mMap.moveCamera(cu);
                        }
                        strTimeTaken = parser.getEstimatedTime();
                    }
                } else {
                    mMap.clear();
                    flowValue = 0;
                    layoutChanges();
                    utils.displayMessage(getView(), context, context.getResources().getString(R.string.no_service));
                }

            }

            // Drawing polyline in the Google Map for the i-th route
            if (flowValue != 0) {
                if (lineOptions != null && points != null) {
                    //mMap.addPolyline(lineOptions);
                    startAnim(points);
                } else {
                    Log.d("onPostExecute", "without Polylines drawn");
                }
            }
        }
    }


    private String getUrl(double source_latitude, double source_longitude, double dest_latitude, double dest_longitude) {

        // Origin of route
        String str_origin = "origin=" + source_latitude + "," + source_longitude;

        // Destination of route
        String str_dest = "destination=" + dest_latitude + "," + dest_longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        //Key
        String key = "key=" + getString(R.string.google_map_api);

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!SharedHelper.getKey(context, "wallet_balance").equalsIgnoreCase("")) {
            wallet_balance = Double.parseDouble(SharedHelper.getKey(context, "wallet_balance"));
        }

        if (lnrHome != null && lnrWork != null) {
            getFavoriteLocations();
        }

        if (!Double.isNaN(wallet_balance) && wallet_balance > 0) {
            if (lineView != null && chkWallet != null) {
                lineView.setVisibility(View.VISIBLE);
                chkWallet.setVisibility(View.VISIBLE);
                lnrAvailableBalance.setVisibility(View.VISIBLE);
            }
        } else {
            if (lineView != null && chkWallet != null) {
                lineView.setVisibility(View.GONE);
                chkWallet.setVisibility(View.GONE);
                lnrAvailableBalance.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void getJSONArrayResult(String strTag, JSONArray response) {
        if (strTag.equalsIgnoreCase("Get Services")) {
            utils.print("GetServices", response.toString());
            if (SharedHelper.getKey(context, "service_type").equalsIgnoreCase("")) {
                SharedHelper.putKey(context, "service_type", "" + response.optJSONObject(0).optString("id"));
            }
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            if (response.length() > 0) {
                currentPostion = 0;
                ServiceListAdapter serviceListAdapter = new ServiceListAdapter(response);
                rcvServiceTypes.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                rcvServiceTypes.setAdapter(serviceListAdapter);
                getProvidersList(SharedHelper.getKey(context, "service_type"));
            }
            if (mMap != null) {
                mMap.clear();
            }
            setValuesForSourceAndDestination();
        }
    }


   /* private void checkPayment() {
        if (paymentMode.equalsIgnoreCase("CASH")) {
            if (isPaid.equalsIgnoreCase("0")) {
                if (walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
//                    btnPayNow.setVisibility(View.GONE);
//                    btnPaymentDoneBtn.setVisibility(View.GONE);
                    walletDetectionLayout.setVisibility(View.VISIBLE);
                    discountDetectionLayout.setVisibility(View.VISIBLE);
                    flowValue = 5;
                    layoutChanges();
                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                    lblPaymentTypeInvoice.setText("CASH");
                } else if (walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
//                    btnPayNow.setVisibility(View.GONE);
//                    btnPaymentDoneBtn.setVisibility(View.GONE);
                    walletDetectionLayout.setVisibility(View.VISIBLE);
                    discountDetectionLayout.setVisibility(View.GONE);
                    flowValue = 5;
                    layoutChanges();
                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                    lblPaymentTypeInvoice.setText("CASH");
                } else if (walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
//                    btnPayNow.setVisibility(View.GONE);
//                    btnPaymentDoneBtn.setVisibility(View.GONE);
                    walletDetectionLayout.setVisibility(View.GONE);
                    discountDetectionLayout.setVisibility(View.VISIBLE);
                    flowValue = 5;
                    layoutChanges();
                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                    lblPaymentTypeInvoice.setText("CASH");
                } else if (walletAmountDetected == 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
//                    btnPayNow.setVisibility(View.GONE);
//                    btnPaymentDoneBtn.setVisibility(View.GONE);
                    flowValue = 5;
                    layoutChanges();
                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                    lblPaymentTypeInvoice.setText("CASH");
                } else if (walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
//                    btnPayNow.setVisibility(View.GONE);
//                    btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                    walletDetectionLayout.setVisibility(View.GONE);
                    discountDetectionLayout.setVisibility(View.VISIBLE);
                    flowValue = 5;
                    layoutChanges();
                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                    lblPaymentTypeInvoice.setText("CASH");
                }
            } else {
                if (walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
//                    btnPayNow.setVisibility(View.GONE);
//                    btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                    walletDetectionLayout.setVisibility(View.VISIBLE);
                    discountDetectionLayout.setVisibility(View.VISIBLE);
                    flowValue = 5;
                    layoutChanges();
                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                    lblPaymentTypeInvoice.setText("CASH");
                } else if (walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount == 0) {
//                    btnPayNow.setVisibility(View.GONE);
//                    btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                    walletDetectionLayout.setVisibility(View.VISIBLE);
                    discountDetectionLayout.setVisibility(View.GONE);
                    flowValue = 5;
                    layoutChanges();
                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                    lblPaymentTypeInvoice.setText("CASH");
                } else if (walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
//                    btnPayNow.setVisibility(View.GONE);
//                    btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                    walletDetectionLayout.setVisibility(View.GONE);
                    discountDetectionLayout.setVisibility(View.VISIBLE);
                    flowValue = 5;
                    layoutChanges();
                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                    lblPaymentTypeInvoice.setText("CASH");
                } else if (walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
//                    btnPayNow.setVisibility(View.GONE);
                    flowValue = 6;
                    layoutChanges();
                } else if (walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
//                    btnPayNow.setVisibility(View.GONE);
                    flowValue = 6;
                    layoutChanges();
                } else if (walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
//                    btnPayNow.setVisibility(View.GONE);
                    flowValue = 6;
                    layoutChanges();
                }
            }
        } else {
            // card
            if (isPaid.equalsIgnoreCase("0")) {
                if (walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
//                    btnPayNow.setVisibility(View.VISIBLE);
//                    btnPaymentDoneBtn.setVisibility(View.GONE);
                    walletDetectionLayout.setVisibility(View.VISIBLE);
                    discountDetectionLayout.setVisibility(View.VISIBLE);
                    flowValue = 5;
                    layoutChanges();
                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                    lblPaymentTypeInvoice.setText("CARD");
                } else if (walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
//                    btnPayNow.setVisibility(View.VISIBLE);
//                    btnPaymentDoneBtn.setVisibility(View.GONE);
                    walletDetectionLayout.setVisibility(View.VISIBLE);
                    discountDetectionLayout.setVisibility(View.GONE);
                    flowValue = 5;
                    layoutChanges();
                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                    lblPaymentTypeInvoice.setText("CARD");
                } else if (walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
//                    btnPayNow.setVisibility(View.VISIBLE);
//                    btnPaymentDoneBtn.setVisibility(View.GONE);
                    walletDetectionLayout.setVisibility(View.GONE);
                    discountDetectionLayout.setVisibility(View.VISIBLE);
                    flowValue = 5;
                    layoutChanges();
                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                    lblPaymentTypeInvoice.setText("CARD");
                } else if (walletAmountDetected == 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
//                    btnPayNow.setVisibility(View.VISIBLE);
//                    btnPaymentDoneBtn.setVisibility(View.GONE);
                    flowValue = 5;
                    layoutChanges();
                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                    lblPaymentTypeInvoice.setText("CARD");
                } else if (walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
//                    btnPayNow.setVisibility(View.GONE);
//                    btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                    walletDetectionLayout.setVisibility(View.GONE);
                    discountDetectionLayout.setVisibility(View.VISIBLE);
                    flowValue = 5;
                    layoutChanges();
                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                    lblPaymentTypeInvoice.setText("CARD");
                }

                Toast.makeText(context, "payment failed", Toast.LENGTH_SHORT).show();
            } else {
                if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                        && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
//                    btnPayNow.setVisibility(View.GONE);
//                    btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                    walletDetectionLayout.setVisibility(View.VISIBLE);
                    discountDetectionLayout.setVisibility(View.VISIBLE);
                    flowValue = 5;
                    layoutChanges();
                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                    lblPaymentTypeInvoice.setText("CARD");
                } else if (walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount == 0) {
//                    btnPayNow.setVisibility(View.GONE);
//                    btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                    walletDetectionLayout.setVisibility(View.VISIBLE);
                    discountDetectionLayout.setVisibility(View.GONE);
                    flowValue = 5;
                    layoutChanges();
                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                    lblPaymentTypeInvoice.setText("CARD");
                } else if (walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
//                    btnPayNow.setVisibility(View.GONE);
//                    btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                    walletDetectionLayout.setVisibility(View.GONE);
                    discountDetectionLayout.setVisibility(View.VISIBLE);
                    flowValue = 5;
                    layoutChanges();
                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                    lblPaymentTypeInvoice.setText("CARD");
                }
            }
        }
    }*/

    private void setCurrentAddress() {
        Utilities.getAddressUsingLatLng("source", frmSource, context, "" + current_lat, "" + current_lng);
    }

    public void setHomeWorkAddress(String strTag) {
        if (strTag.equalsIgnoreCase("home")) {
            dest_lat = "" + SharedHelper.getKey(activity, "home_lat");
            dest_lng = "" + SharedHelper.getKey(activity, "home_lng");
            dest_address = "" + SharedHelper.getKey(activity, "home");
            SharedHelper.putKey(context, "destination", "" + SharedHelper.getKey(activity, "home"));
        } else {
            dest_lat = "" + SharedHelper.getKey(activity, "work_lat");
            dest_lng = "" + SharedHelper.getKey(activity, "work_lng");
            dest_address = "" + SharedHelper.getKey(activity, "work");
            SharedHelper.putKey(context, "destination", "" + SharedHelper.getKey(activity, "work"));
        }

        String strSourceAdd = frmSource.getText().toString();
        String strDestAdd = frmDest.getText().toString();

        if (!strSourceAdd.equalsIgnoreCase(dest_address)) {
            frmDest.setText(dest_address);
            SharedHelper.putKey(context, "current_status", "2");
            if (source_lat != null && source_lng != null && !source_lng.equalsIgnoreCase("")
                    && !source_lat.equalsIgnoreCase("")) {
                try {
                    String url = getUrl(Double.parseDouble(source_lat), Double.parseDouble(source_lng)
                            , Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));
                    fetchUrl = new FetchUrl();
                    fetchUrl.execute(url);
                    LatLng location = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));

                    if (sourceMarker != null)
                        sourceMarker.remove();
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(location).snippet(frmSource.getText().toString())
                            .title("source")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.yellow_pin));
                    marker = mMap.addMarker(markerOptions);
                    sourceMarker = mMap.addMarker(markerOptions);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("")) {
                try {
                    destLatLng = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));
                    if (destinationMarker != null)
                        destinationMarker.remove();
                    MarkerOptions destMarker = new MarkerOptions()
                            .position(destLatLng).title("destination").snippet(frmDest.getText().toString())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));
                    destinationMarker = mMap.addMarker(destMarker);
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(sourceMarker.getPosition());
                    builder.include(destinationMarker.getPosition());
                    LatLngBounds bounds = builder.build();
                    int padding = 150; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.moveCamera(cu);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (dest_address.equalsIgnoreCase("")) {
                flowValue = 1;
//                            frmSource.setText(source_address);
                getServiceList();
            } else {
                flowValue = 1;
                Log.e("checkPaymentType set", "setHomeWorkAddress: payment type check");
                if (SharedHelper.getKey(context, "isCashAccept").equalsIgnoreCase("true") && SharedHelper.getKey(context, "payment_mode").equalsIgnoreCase("CASH")) {
                    imgPaymentType.setImageResource(R.drawable.money_icon);
                    lblPaymentType.setText("CASH");
                    // paymentType = 1;
                } else if (SharedHelper.getKey(context, "payment_mode").equalsIgnoreCase("CARD")) {
                    imgPaymentType.setImageResource(R.drawable.visa);
                    lblPaymentType.setText(SharedHelper.getKey(context, "card_num"));
                    //  paymentType = 2;
                } else {
                    lblPaymentType.setText(getString(R.string.no_cash_payment_txt));
                    //  paymentType = 3;
                }


              /*  if (cardInfoArrayList.size() > 0) {
                    getCardDetailsForPayment(cardInfoArrayList.get(0));
                } else if (SharedHelper.getKey(context, "isCashAccept").equalsIgnoreCase("true"))
                    lblPaymentType.setText("CASH");
                else
                    lblPaymentType.setText("CASH PAYMENT NOT AVAILABLE");*/
                getServiceList();
            }
            layoutChanges();
        } else {
            Toast.makeText(context, activity.getResources().getString(R.string.source_dest_not_same), Toast.LENGTH_SHORT).show();
        }
    }

    public void gotoCurrentPosition() {
        if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
            LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    protected Marker createMarker(double latitude, double longitude, String title, String snippet) {

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_location_icon)));
    }


    private void getFavoriteLocations() {
        ApiInterface mApiInterface = RetrofitClient.getLiveTrackingClient().create(ApiInterface.class);

        Call<ResponseBody> call = mApiInterface.getFavoriteLocations("XMLHttpRequest",
                SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.e("sUCESS", "SUCESS" + response.body());
                if (response.body() != null) {
                    try {
                        String bodyString = new String(response.body().bytes());
                        Log.e("sUCESS", "bodyString" + bodyString);
                        try {
                            JSONObject jsonObj = new JSONObject(bodyString);
                            JSONArray homeArray = jsonObj.optJSONArray("home");
                            JSONArray workArray = jsonObj.optJSONArray("work");
                            JSONArray othersArray = jsonObj.optJSONArray("others");
                            JSONArray recentArray = jsonObj.optJSONArray("recent");
                            if (homeArray.length() > 0) {
                                lnrHome.setVisibility(View.VISIBLE);
                                SharedHelper.putKey(context, "home", homeArray.optJSONObject(0).optString("address"));
                                SharedHelper.putKey(context, "home_lat", homeArray.optJSONObject(0).optString("latitude"));
                                SharedHelper.putKey(context, "home_lng", homeArray.optJSONObject(0).optString("longitude"));
                            } else {
                                lnrHome.setVisibility(View.GONE);
                            }
                            if (workArray.length() > 0) {
                                lnrWork.setVisibility(View.VISIBLE);
                                SharedHelper.putKey(context, "work", workArray.optJSONObject(0).optString("address"));
                                SharedHelper.putKey(context, "work_lat", workArray.optJSONObject(0).optString("latitude"));
                                SharedHelper.putKey(context, "work_lng", workArray.optJSONObject(0).optString("longitude"));
                            } else {
                                lnrWork.setVisibility(View.GONE);
                            }
                            if (flowValue == 0) {
                                if (lnrHome.getVisibility() == View.GONE && lnrWork.getVisibility() == View.GONE) {
                                    lnrHomeWork.setVisibility(View.GONE);
                                } else {
                                    lnrHomeWork.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure", "onFailure" + call.request().url());
            }
        });
    }

    void getAddressUsingLatLng(final Context context, String latitude, String longitude) {
        mApiInterface = RetrofitClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> call = mApiInterface.getResponse(latitude + "," + longitude,
                context.getResources().getString(R.string.google_map_api));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.e("sUCESS", "SUCESS" + response.body());
                Log.e("sUCESS", "URL GET" + call.request().url().toString());
                if (response.body() != null) {
                    try {
                        String bodyString = new String(response.body().bytes());
                        Log.e("sUCESS", "bodyString" + bodyString);
                        try {
                            JSONObject jsonObj = new JSONObject(bodyString);
                            JSONArray jsonArray = jsonObj.optJSONArray("results");
                            if (jsonArray.length() > 0) {
                                currentAddress = jsonArray.optJSONObject(0).optString("formatted_address");
                                source_address = currentAddress;
                                current_address = currentAddress;
                                frmSource.setText("" + current_address);
                                SharedHelper.putKey(context, "source", "" + current_address);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure", "onFailure" + call.request().url());
            }
        });
    }

    private void showChangePaymentDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(getString(R.string.no_cash_accept_message))
                .setCancelable(false);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(getActivity(), com.eziride.user.Activities.Payment.class);
                startActivityForResult(intent, 5555);
                dialog.dismiss();

            }
        });
        builder.setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void showCustomToast(View view) {

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) view.findViewById(R.id.custom_toast_layout));
        if (toast != null) {
            toast.cancel();
        }
        toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 30);
        toast.setView(layout);
        toast.show();


    }

    public boolean foregrounded() {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE);
    }


}