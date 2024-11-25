package com.polling.sdk;

import com.polling.sdk.models.CallbackHandler;
import com.polling.sdk.models.RequestIdentification;

public class SdkPayload
{
    public SdkPayload(RequestIdentification requestIdentification, CallbackHandler callbackHandler, boolean disableAvailableSurveysPoll)
    {
       this.requestIdentification = requestIdentification;
       this.callbackHandler = callbackHandler;
       this.disableAvailableSurveysPoll = disableAvailableSurveysPoll;
    }

    public RequestIdentification requestIdentification;
    public CallbackHandler callbackHandler;
    boolean disableAvailableSurveysPoll;

}
