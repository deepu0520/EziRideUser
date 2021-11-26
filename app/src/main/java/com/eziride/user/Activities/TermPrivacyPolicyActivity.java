package com.eziride.user.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.eziride.user.Helper.URLHelper;
import com.eziride.user.R;
import com.eziride.user.Utils.AppWebViewClients;
import com.eziride.user.Utils.MyBoldTextView;
import com.eziride.user.Utils.Utilities;

public class TermPrivacyPolicyActivity extends AppCompatActivity {

    WebView dataView;
    MyBoldTextView header;
    String url = "";
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_privacy_policy);
        header = findViewById(R.id.page_header);
        dataView = findViewById(R.id.webview);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        String from = getIntent().getStringExtra("requestType");
        if (from != null && from.equalsIgnoreCase("terms")) {
            header.setText(R.string.action_term_condition);

            url = URLHelper.base + "user-terms-and-conditions";
        } else {
            header.setText(R.string.action_privacy_policy);
            url = URLHelper.base + "user-privacy-policy";
        }

        Log.e("request type", "@@@@@@ " + from);

        loadUrl();

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        // web.loadUrl("file:///android_asset/term_condtion.html");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }


    private void loadUrl() {
        dataView.getSettings().setJavaScriptEnabled(true);
        dataView.getSettings().setPluginState(WebSettings.PluginState.ON);
        dataView.getSettings().setAllowFileAccess(true);
        dataView.getSettings().setLoadsImagesAutomatically(true);
        dataView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        dataView.getSettings().setBuiltInZoomControls(false);
        dataView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        // cms_view.loadUrl("http://www.google.com/");
        Log.e("TAG", "onCreateView: url is" + url);
        Utilities.showProgressDialog(this);
        dataView.loadUrl(url);
        dataView.setWebViewClient(new AppWebViewClients());
    }


}
