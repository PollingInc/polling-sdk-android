package com.polling.sdk;

public class WebViewBottomHelper extends DialogHelper
{
    public WebViewBottomHelper(DialogRequest dialog)
    {
        super(dialog);
    }
    public void showDialog(DialogRequest dialog, String url) {
        dialog.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new WebViewBottom(url, dialog).show();
            }
        });
    }

}
