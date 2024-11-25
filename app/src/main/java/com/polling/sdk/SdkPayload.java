package com.polling.sdk;

import android.content.Context;

import com.polling.sdk.models.CallbackHandler;
import com.polling.sdk.models.RequestIdentification;

public class SdkPayload
{
    public SdkPayload(Context context, RequestIdentification requestIdentification, CallbackHandler callbackHandler, boolean disableAvailableSurveysPoll)
    {
        this.context = context;
        this.requestIdentification = requestIdentification;
        this.callbackHandler = callbackHandler;
        this.disableAvailableSurveysPoll = disableAvailableSurveysPoll;
    }

    public Context context;
    public RequestIdentification requestIdentification;
    public CallbackHandler callbackHandler;
    boolean disableAvailableSurveysPoll;

}
