package com.polling.sdk.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.webkit.WebView;

import com.polling.sdk.models.DialogRequest;
import com.polling.sdk.R;
import com.polling.sdk.dialogs.helpers.WebViewConfigs;

public class WebViewDialog extends Dialog {
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
        endpoint = dialog.ApplyCompletionBypassToURL(endpoint);
        webView.loadUrl(endpoint);
    }
}


