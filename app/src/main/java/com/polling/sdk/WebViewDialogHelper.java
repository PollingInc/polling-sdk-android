package com.polling.sdk;

import android.app.Activity;


public class WebViewDialogHelper extends DialogHelper
{
    public WebViewDialogHelper(DialogRequest dialog) {
        super(dialog);
    }

    public void showDialog(DialogRequest dialog, String url, Survey survey) {
        dialog.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                var webDialog = new WebViewDialog(url, dialog);
                runOverride(webDialog, survey);
            }
        });
    }

}