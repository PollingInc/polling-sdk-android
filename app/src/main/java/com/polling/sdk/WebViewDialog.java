package com.polling.sdk;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.webkit.WebView;
import java.net.URLEncoder;

public class WebViewDialog extends Dialog {
    /*
    private final String url;
    private final String customerId;
    private final String apiKey;


    public WebViewDialog(Context context, String url, String customerId, String apiKey) {
        super(context);
        this.url = url;
        this.customerId = customerId;
        this.apiKey = apiKey;
    }
    */
    public final DialogRequest dialog;
    public final String url;

    public WebViewDialog(String url, DialogRequest dialog)
    {
        super(dialog.activity);
        this.url = url;
        this.dialog = dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_webview);

        WebView webView = WebViewConfigs.applyDefault(findViewById(R.id.webview));

        String endpoint = dialog.ApplyKeyToURL(url);
        webView.loadUrl(endpoint);

    }
}


