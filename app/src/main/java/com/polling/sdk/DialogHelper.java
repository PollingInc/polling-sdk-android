package com.polling.sdk;

public abstract class DialogHelper
{

    public DialogRequest dialog;

    public DialogHelper(DialogRequest dialog)
    {
        this.dialog = dialog;
    }

    public void showDialog(DialogRequest dialog, String url) {
        dialog.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new WebViewBottom(url, dialog).show(); //used as default, but can be overridden on extended classes
            }
        });
    }

    public void availableSurveys()
    {
        String url = "https://demo.polling.com/sdk/available-surveys";
        showDialog(this.dialog, url);
    }


    public void singleSurvey(String surveyId)
    {
        String url = "https://demo.polling.com/sdk/survey/" + surveyId;
        showDialog(this.dialog, url);
    }
}



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
