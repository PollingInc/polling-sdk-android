package com.polling.sdk.core.models;

import android.app.Activity;

public class DialogRequest extends RequestIdentification
{
    public Activity activity;

    public DialogRequest(Activity activity, String customerId, String apiKey)
    {
        super(customerId, apiKey);
        this.activity = activity;
    }





}
