package com.polling.sdk;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;

public class WebViewBottom extends Dialog
{
    //private Dialog dialog;
    private final String url;
    private final DialogRequest dialogRequest;

    public WebViewBottom(String url, DialogRequest dialogRequest) {
        super(dialogRequest.activity);
        this.url = url;
        this.dialogRequest = dialogRequest;
        //initializeDialog(dialogRequest.activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_bottom_sheet_webview);

        Window window = getWindow();
        if (window != null)
        {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setGravity(Gravity.BOTTOM);
        }

        WebView webView = findViewById(R.id.webview);

        String endpoint = dialogRequest.ApplyKeyToURL(url);
        endpoint = dialogRequest.ApplyCompletionBypassToURL(endpoint);

        WebViewConfigs.applyDefault(webView);
        webView.loadUrl(endpoint);

    }
}

