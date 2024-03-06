package com.polling.sdk;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.FrameLayout;
public class WebViewBottomCustom
{
    private Dialog dialog;
    private final String url;
    private final DialogRequest dialogRequest;

    public WebViewBottomCustom(String url, DialogRequest dialogRequest) {
        this.url = url;
        this.dialogRequest = dialogRequest;
        initializeDialog(dialogRequest.activity);
    }

    private void initializeDialog(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_bottom_sheet_webview);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setGravity(Gravity.BOTTOM);

        }

        WebView webView = dialog.findViewById(R.id.webview);
        String endpoint = dialogRequest.ApplyKeyToURL(url);
        WebViewConfigs.applyDefault(webView);
        webView.loadUrl(endpoint);
    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}

