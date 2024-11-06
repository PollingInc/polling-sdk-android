package com.polling.sdk;

public class WebViewBottomHelper extends DialogHelper
{
    public WebViewBottomHelper(DialogRequest dialog)
    {
        super(dialog);
    }
    public void showDialog(DialogRequest dialog, String url, Survey survey) {
        dialog.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                var webDialog = new WebViewBottom(url, dialog);
                runOverride(webDialog, survey);
            }
        });
    }

}
