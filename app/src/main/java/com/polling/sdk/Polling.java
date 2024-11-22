package com.polling.sdk;

import android.os.Handler;

import com.polling.sdk.models.CallbackHandler;
import com.polling.sdk.models.RequestIdentification;

import java.util.HashMap;
import java.util.Map;

public class Polling
{
    String baseUrl = "https://app.polling.com";
    String baseApiUrl = "https://api.polling.com";

    RequestIdentification requestIdentification;


    public boolean initialized = false;
    String currentSurveyUuid = null;


    private Handler surveyPollHandler;
    private Runnable surveyPollRunnable;
    int surveyPollRateMsec = 60_000;
    int surveyClosePostponeMinutes  = 30;

    //boolean isSurveyCurrentlyVisible = false;

    boolean isAvailableSurveysCheckDisabled = false;
    private Map<String, Object> cachedAvailableSurveys = new HashMap<>();
    private int numSurveysAvailable = 0;
    CallbackHandler callbackHandler;

    String surveyViewBaseUrl;
    String surveyApiBaseUrlSDK;
    String eventApiBaseUrl;
    String surveyViewUrl;
    String surveysDefaultEmbedViewUrl;
    String surveyApiUrl;
    String eventApiUrl;

    public Polling()
    {
        this.surveyViewBaseUrl = this.baseUrl + "/sdk";
        this.surveyApiBaseUrlSDK = this.baseApiUrl + "/api/sdk/surveys";
        this.eventApiBaseUrl = this.baseApiUrl + "/api/events/collect";
    }




    public void initialize(SdkPayload sdkPayload) {
        if (this.initialized) {
            return;
        }

        var customerPayload = sdkPayload.requestIdentification;
        var customerCallbacks = sdkPayload.callbackHandler;

        this.initialized = true;
        this.isAvailableSurveysCheckDisabled = sdkPayload.disableAvailableSurveysPoll || false;

        if (customerPayload.customerId != null)
        {
            this.setCustomerId(customerPayload.customerId);
        }

        if (customerPayload.apiKey != null)
        {
            this.setApiKey(customerPayload.apiKey);
        }

        this.callbackHandler = customerCallbacks;

        this.setupPostMessageBridge();

        if (surveyPollHandler != null) {
            surveyPollHandler.removeCallbacksAndMessages(null);
        }

        surveyPollHandler = new Handler();
        surveyPollRunnable = new Runnable() {
            @Override
            public void run() {
                intervalLogic(); // Executes the logic
                surveyPollHandler.postDelayed(this, surveyPollRateMsec); // Re-schedules itself
            }
        };

        surveyPollHandler.post(surveyPollRunnable); // Schedules the first execution
        intervalLogic(); // Executes immediately

        return;
    }

    public void setCustomerId(String customerId) {
        this.requestIdentification.customerId = customerId;
        this.updateUrls();
    }

    public void setApiKey(String apiKey) {
        this.requestIdentification.apiKey = apiKey;
        this.updateUrls();
    }


    public void logPurchase(int integerCents) {
        this.logEvent("Purchase", Integer.toString(integerCents));
    }

    public logSession() {
        this.logEvent("Session");
    }

    public void logEvent(String eventName, String eventValue) {
        new Thread(() -> {
            try {
            const response = await fetch(this.eventApiUrl !, {
                        method:'POST',
                        headers:{
                    'Content-Type':'application/x-www-form-urlencoded'
                },
                body:
                new URLSearchParams({
                        event:eventName,
                        value:eventValue as any
                })
            });

                if (!response.ok) {
                    this.onFailure('Failed to log event: ' + response.status);
                    return this;
                }

            const responseData = await response.json();

                if (responseData ?.triggered_surveys ?.length){
                    this.onTriggeredSurveysUpdated(responseData.triggered_surveys as TriggeredSurvey[])
                    ;
                }
            } catch (Exception error) {
                this.callbackHandler.onFailure("Network error.");
            }


            return;
        }.start();
    }


}
