package com.polling.sdk.core.dialogs.helpers;

import com.polling.sdk.core.models.DialogRequest;
import com.polling.sdk.core.models.Survey;
import com.polling.sdk.core.dialogs.WebViewDialog;


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