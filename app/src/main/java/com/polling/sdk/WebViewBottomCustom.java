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

        /*
        int webViewHeight = convertDpToPx(context, 275);
        ViewGroup.LayoutParams webViewParams = webView.getLayoutParams();
        webViewParams.height = webViewHeight;
        //webViewParams.height += webViewHeight;

        webView.setLayoutParams(webViewParams);
         */

        String endpoint = dialogRequest.ApplyKeyToURL(url);
        endpoint = dialogRequest.ApplyCompletionBypassToURL(endpoint);

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

    private int convertDpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}

