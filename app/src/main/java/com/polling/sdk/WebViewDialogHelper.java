package com.polling.sdk;

import android.app.Activity;
import android.content.Context;

public class WebViewDialogHelper {

    public static void showDialog(Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new WebViewDialog(activity).show();
            }
        });
    }
}