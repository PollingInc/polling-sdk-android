package com.polling.sdk;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.webkit.WebView;
import java.net.URLEncoder;

public class WebViewDialog extends Dialog {
    private final String url;
    private final String customerId;
    private final String apiKey;


    public WebViewDialog(Context context, String url, String customerId, String apiKey) {
        super(context);
        this.url = url;
        this.customerId = customerId;
        this.apiKey = apiKey;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_webview);

        WebView webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        //webView.getSettings().setTextZoom(percent);

        StringBuilder buffer = new StringBuilder(url);

        if (customerId != null && !customerId.isEmpty() && apiKey != null && !apiKey.isEmpty()) {
            buffer.append("?customer_id=").append(URLEncoder.encode(customerId));
            buffer.append("&api_key=").append(URLEncoder.encode(apiKey));
        }
        //else ?

        webView.loadUrl(buffer.toString());
    }
}


