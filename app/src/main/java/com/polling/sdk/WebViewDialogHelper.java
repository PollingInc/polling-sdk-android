package com.polling.sdk;

import android.app.Activity;
import android.content.Context;


/*

EXAMPLE WEB VIEW
https://demo.polling.com/sdk/available-surveys?customer_id=123&api_key=cli_wZJW1tH39TfUMbEumPLrDy15EXDqJA0a

Available surveys
GET     /api/sdk/surveys/available?customer_id=[ID]&api_key=cli_[KEY]
WebView https://demo.polling.com/sdk/available-surveys

Specific survey
GET     /api/sdk/surveys/[UUID]?customer_id={ID}&api_key=cli_[KEY]
WebView https://demo.polling.com/sdk/survey/[uuid]

Completed surveys:
GET     /api/sdk/surveys/completed?customer_id=[ID]&api_key=cli_[KEY]
WebView https://demo-api.polling.com/api/sdk/surveys/completed
*/

public class WebViewDialogHelper {

    public String currentUrl;
    public DialogRequest dialog;

    public WebViewDialogHelper(DialogRequest dialog)
    {
        this.dialog = dialog;
    }


    public static void showDialog(Activity activity, String url, String customerId, String apiKey) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new WebViewDialog(activity, url, customerId, apiKey).show();
            }
        });
    }

    /*
    public void showDialog(){
        showDialog(dialog);
    }
    */
    public void showDialog(DialogRequest dialog)
    {
        showDialog(dialog.activity, currentUrl, dialog.customerId, dialog.apiKey);
    }

    public void availableSurveys()
    {
        this.currentUrl = "https://demo.polling.com/sdk/available-surveys";
        showDialog(this.dialog);
    }


    public void singleSurvey(String surveyId)
    {
        this.currentUrl = "https://demo.polling.com/sdk/survey/" + surveyId;
        showDialog(this.dialog);
    }

    public void completedSurveys()
    {
        this.currentUrl = "https://demo-api.polling.com/api/sdk/surveys/completed";
        showDialog(this.dialog);
    }


}