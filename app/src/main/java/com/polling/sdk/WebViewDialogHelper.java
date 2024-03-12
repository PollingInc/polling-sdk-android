package com.polling.sdk;

import android.app.Activity;


public class WebViewDialogHelper extends DialogHelper
{
    public WebViewDialogHelper(DialogRequest dialog) {
        super(dialog);
    }

    public void showDialog(DialogRequest dialog, String url) {
        dialog.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new WebViewDialog(url, dialog).show();
            }
        });
    }

}