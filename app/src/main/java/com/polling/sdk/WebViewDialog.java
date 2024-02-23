package com.polling.sdk;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.webkit.WebView;

import android.util.Log;
import java.net.URLEncoder;

public class WebViewDialog extends Dialog {

    public WebViewDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_webview);

        WebView webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);


        StringBuffer buffer = new StringBuffer("https://demo.polling.com/sdk/available-surveys");
        buffer.append("?customer_id="+ URLEncoder.encode("1200"));
        buffer.append("&api_key="+URLEncoder.encode("cli_wZJW1tH39TfUMbEumPLrDy15EXDqJA0a"));

        Log.d("TestActivity",buffer.toString());

        webView.loadUrl(buffer.toString());


        //webView.loadUrl("https://demo.polling.com/sdk/available-surveys?customer_id=123&api_key=cli_wZJW1tH39TfUMbEumPLrDy15EXDqJA0a");
    }
}


