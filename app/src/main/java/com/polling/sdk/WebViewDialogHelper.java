package com.polling.sdk;

import android.app.Activity;


/*

EXAMPLE WEB VIEW
https://demo.polling.com/sdk/available-surveys?customer_id=123&api_key=cli_wZJW1tH39TfUMbEumPLrDy15EXDqJA0a

Available surveys
GET     https://demo-api.polling.com/api/sdk/surveys/available?customer_id=[ID]&api_key=cli_[KEY]
WebView https://demo.polling.com/sdk/available-surveys?customer_id=[ID]&api_key=cli_[KEY]

Specific survey
GET     https://demo-api.polling.com/api/sdk/surveys/[UUID]?customer_id={ID}&api_key=cli_[KEY]
WebView https://demo.polling.com/sdk/survey/[uuid]?customer_id=[ID]&api_key=cli_[KEY]

Completed surveys:
GET     https://demo-api.polling.com/api/sdk/surveys/completed?customer_id=[ID]&api_key=cli_[KEY]

*/

public class WebViewDialogHelper {

    public String currentUrl;
    public DialogRequest dialog;

    public WebViewDialogHelper(DialogRequest dialog)
    {
        this.dialog = dialog;
    }

    public void showDialog(DialogRequest dialog) {
        dialog.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new WebViewDialog(currentUrl, dialog).show();
            }
        });
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