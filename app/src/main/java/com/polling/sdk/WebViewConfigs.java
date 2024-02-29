package com.polling.sdk;

import android.webkit.WebView;

public class WebViewConfigs
{
    public static WebView applyDefault(WebView webView)
    {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        //webView.getSettings().setTextZoom(percent);

        return webView;
    }
}
