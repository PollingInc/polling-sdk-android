package com.polling.sdk;

import android.app.Activity;
import android.content.Context;

public class WebViewDialogHelper {

    public String currentUrl;

    public static void showDialog(Activity activity, String url, String customerId, String apiKey) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new WebViewDialog(activity, url, customerId, apiKey).show();
            }
        });
    }

    public void showDialog(Activity activity, String customerId, String apiKey)
    {
        showDialog(activity, currentUrl, customerId, apiKey);
    }


    public void availableSurveys()
    {
        this.currentUrl = "https://demo.polling.com/sdk/available-surveys";
    }

    public void displaySurvey()
    {
        //showDialog(Activity activity);
    }
}