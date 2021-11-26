package com.eziride.user.Utils;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by sis183 on 6/8/2017.
 */

public class AppWebViewClients extends WebViewClient {

    public AppWebViewClients() {

    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // TODO Auto-generated method stub
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        // TODO Auto-generated method stub
        super.onPageFinished(view, url);
        Utilities.dismissProgressDialog();
    }
}