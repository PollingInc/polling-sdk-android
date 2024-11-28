package com.polling.sdk;

import android.app.Activity;
import android.content.Context;

import com.polling.sdk.core.models.CallbackHandler;
import com.polling.sdk.core.models.RequestIdentification;

public class SdkPayload
{
    public SdkPayload(Activity activity, RequestIdentification requestIdentification, CallbackHandler callbackHandler, boolean disableAvailableSurveysPoll)
    {
        this.activity = activity;
        this.requestIdentification = requestIdentification;
        this.callbackHandler = callbackHandler;
        this.disableAvailableSurveysPoll = disableAvailableSurveysPoll;
    }

    public Activity activity;
    public RequestIdentification requestIdentification;
    public CallbackHandler callbackHandler;
    boolean disableAvailableSurveysPoll;

}
