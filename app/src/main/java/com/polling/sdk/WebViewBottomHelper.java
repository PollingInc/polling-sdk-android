package com.polling.sdk;

public class WebViewBottomHelper 
{
    public DialogRequest dialog;

    public WebViewBottomHelper(DialogRequest dialog)
    {
        this.dialog = dialog;
    }

    public void showDialog(DialogRequest dialog, String url) {
        dialog.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new WebViewBottom(url, dialog).show();
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
