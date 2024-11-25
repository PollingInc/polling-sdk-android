package com.polling.sdk;

import android.content.Context;
import android.os.Handler;

import com.polling.sdk.models.CallbackHandler;
import com.polling.sdk.models.RequestIdentification;
import com.polling.sdk.models.Survey;
import com.polling.sdk.network.WebRequestHandler;
import com.polling.sdk.network.WebRequestType;
import com.polling.sdk.utils.DataParser;
import com.polling.sdk.utils.ViewType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Polling
{
    String baseUrl = "https://app.polling.com";
    String baseApiUrl = "https://api.polling.com";

    RequestIdentification requestIdentification = new RequestIdentification();


    public boolean initialized = false;
    String currentSurveyUuid = null;


    private Handler surveyPollHandler;
    private Runnable surveyPollRunnable;
    int surveyPollRateMsec = 60_000;
    int surveyClosePostponeMinutes  = 30;

    boolean isSurveyCurrentlyVisible = false;

    boolean isAvailableSurveysCheckDisabled = false;
    private Map<String, Object> cachedAvailableSurveys = new HashMap<>();
    private int numSurveysAvailable = 0;
    CallbackHandler callbackHandler;

    String surveyViewBaseUrl;
    String surveyApiBaseUrlSDK;
    String eventApiBaseUrl;
    String surveyViewUrl = this.surveyViewBaseUrl + "/available-surveys";
    String surveysDefaultEmbedViewUrl = this.baseUrl + "/embed/";
    String surveyApiUrl = this.surveyApiBaseUrlSDK + "/available";
    String eventApiUrl = this.eventApiBaseUrl;

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
        updateUrls();
    }

    public void setApiKey(String apiKey) {
        this.requestIdentification.apiKey = apiKey;
        updateUrls();
    }


    public void logPurchase(int integerCents) {
        this.logEvent("Purchase", Integer.toString(integerCents));
    }

    public void logSession() {
        this.logEvent("Session", null);
    }

    public void logEvent(String eventName, String eventValue) {
        new Thread(() ->
        {
            try {
                WebRequestHandler.ResponseCallback apiCallbacks = new WebRequestHandler.ResponseCallback() {

                    @Override
                    public void onResponse(String response) {

                        DataParser dataParser = new DataParser();

                        dataParser.parse(response, false);
                        var parsedData = dataParser.get();


                        if (parsedData != null && !parsedData.isEmpty())
                        {
                            Map<String, Object> result = parsedData.get(0);

                            Object surveys = result.get("triggered_surveys");
                            android.util.Log.d("Polling", "Trigger results: " + surveys); //TEMPORARY


                            if (surveys instanceof List)
                            {

                                //TEMPORARY ------<
                                android.util.Log.d("Polling", "Trying to parse");
                                List<?> surveysList = (List<?>) surveys;

                                if (!surveysList.isEmpty()) {
                                    android.util.Log.d("Polling", "Trigger parse: " + surveysList.get(0));
                                }
                                //TEMPORARY ------>



                                //onTriggeredSurveysUpdated(surveys);
                            }

                        }
                    }

                    @Override
                    public void onError(String error) {
                        onFailure("Failed to log event:" + error);
                    }
                };

                String contentType = "application/x-www-form-urlencoded";
                String body = "event=" + eventName + "&value=" + eventValue;

                WebRequestHandler.makeRequest(this.eventApiUrl, WebRequestType.POST, body, apiCallbacks, contentType);

            } catch (Exception error) {
                this.callbackHandler.onFailure("Network error.");
            }
        }).start();
    }

    public void showSurvey(String surveyUuid, Context context) {
        if (this.isSurveyCurrentlyVisible) return;

        this.currentSurveyUuid = surveyUuid;

        Survey survey = new Survey(this.surveyViewBaseUrl, requestIdentification, null); //WILL IT HAVE NO CALLBACKS FOR THIS ONE? I DON'T THINK SO.
        survey.singleSurvey(surveyUuid, context, ViewType.Dialog);
    }


    public void showEmbedView(Context context) {
        if (this.isSurveyCurrentlyVisible) return;

        Survey survey = new Survey(this.surveysDefaultEmbedViewUrl,null, null);
        survey.defaultSurvey(context,ViewType.Dialog, false);
    }

    public void getLocalSurveyResults(String surveyUuid) {
        //return localStorage.getItem(surveyUuid);
    }

    private void updateUrls()
    {
        surveysDefaultEmbedViewUrl += requestIdentification.apiKey;

        this.surveysDefaultEmbedViewUrl = requestIdentification.ApplyKeyToURL(surveysDefaultEmbedViewUrl,"customer_id", null);
        this.surveyViewUrl = requestIdentification.ApplyKeyToURL(surveyViewUrl);
        this.surveyApiUrl = requestIdentification.ApplyKeyToURL(surveyApiUrl);
        this.eventApiUrl = requestIdentification.ApplyKeyToURL(eventApiUrl, "user", "api_key");
    }

    private void intervalLogic() {
        if (!this.initialized || this.requestIdentification.apiKey == null || this.requestIdentification.customerId == null) return;

        if (!this.isAvailableSurveysCheckDisabled) {
            //this.loadAvailableSurveys();
        }

        //this.checkAvailableTriggeredSurveys();
    }

    /**
     * Store the survey results
     */
    private void storeLocalSurveyResult(String surveyUuid, String surveyResultData)
    {
        //localStorage.setItem(surveyUuid, surveyResultData);
    }

    private void setupPostMessageBridge()
    {
        //seems to be unnecessary due to what we already have for this SDK in Java.
    }

    private void onFailure(String error)
    {
        this.callbackHandler.onFailure(error);
    }

    private void onSurveyAvailable()
    {
        this.callbackHandler.onSurveyAvailable();
    }

/*
    private void onTriggeredSurveysUpdated(List surveys) {
        // Add the new triggered surveys to the localstorage cache
        let newTriggeredSurveys = [
            ...JSON.parse(localStorage.getItem('polling:triggered_surveys') || '[]'),
            ...surveys
        ];

        // Remove duplicates, to prevent showing the same survey multiple times
        newTriggeredSurveys = newTriggeredSurveys.filter((obj, index) =>
        newTriggeredSurveys.findIndex((item) => item.location === obj.location) === index
        );

        localStorage.setItem(
                'polling:triggered_surveys',
                JSON.stringify(newTriggeredSurveys)
        );

        this.checkAvailableTriggeredSurveys();
    }
*/







}
