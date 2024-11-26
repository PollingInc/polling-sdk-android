package com.polling.sdk;

import android.content.Context;
import android.os.Handler;

import com.polling.sdk.api.models.TriggeredSurvey;
import com.polling.sdk.core.models.CallbackHandler;
import com.polling.sdk.core.models.RequestIdentification;
import com.polling.sdk.core.models.Survey;
import com.polling.sdk.core.network.WebRequestHandler;
import com.polling.sdk.core.network.WebRequestType;
import com.polling.sdk.core.utils.DataParser;
import com.polling.sdk.utils.LocalStorage;
import com.polling.sdk.core.utils.ViewType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Polling
{
    private String baseUrl = "https://app.polling.com";
    private String baseApiUrl = "https://api.polling.com";

    private RequestIdentification requestIdentification = new RequestIdentification();


    public boolean initialized = false;
    private String currentSurveyUuid = null;


    private Handler surveyPollHandler;
    private Runnable surveyPollRunnable;
    private int surveyPollRateMsec = 60_000;
    private int surveyClosePostponeMinutes  = 30;

    private boolean isSurveyCurrentlyVisible = false;

    private boolean isAvailableSurveysCheckDisabled = false;
    private Map<String, Object> cachedAvailableSurveys = new HashMap<>();
    private int numSurveysAvailable = 0;
    private CallbackHandler callbackHandler;

    private String surveyViewBaseUrl;
    private String surveyApiBaseUrlSDK;
    private String eventApiBaseUrl;
    private String surveyViewUrl;
    private String surveysDefaultEmbedViewUrl;
    private String surveyApiUrl;
    private String eventApiUrl;

    private LocalStorage localStorage;

    public Polling()
    {
        this.surveyViewBaseUrl = this.baseUrl + "/sdk";
        this.surveyApiBaseUrlSDK = this.baseApiUrl + "/api/sdk/surveys";
        this.eventApiBaseUrl = this.baseApiUrl + "/api/events/collect";

        surveyViewUrl = this.surveyViewBaseUrl + "/available-surveys";
        surveysDefaultEmbedViewUrl = this.baseUrl + "/embed/";
        surveyApiUrl = this.surveyApiBaseUrlSDK + "/available";
        eventApiUrl = this.eventApiBaseUrl;
    }




    public void initialize(SdkPayload sdkPayload) {
        if (this.initialized) {
            return;
        }

        localStorage = new LocalStorage(sdkPayload.context);


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

                                android.util.Log.d("Polling", "Trying to parse");
                                List<String> surveysList = (List<String>) surveys;

                                if (!surveysList.isEmpty()) {
                                    android.util.Log.d("Polling", "Trigger parse: " + surveysList.get(0));

                                    onTriggeredSurveysUpdated(surveysList);

                                }

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

    public List<String> getLocalSurveyResults(String surveyUuid)
    {
        return localStorage.getData(surveyUuid, (Set<String>) null);
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
        localStorage.saveData(surveyUuid, surveyResultData);
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


    private void onTriggeredSurveysUpdated(List<TriggeredSurvey> newSurveys)
    {
        List<TriggeredSurvey> storedSurveys = localStorage.getData("polling:triggered_surveys");

        List<TriggeredSurvey> newTriggeredSurveys = new ArrayList<>(storedSurveys);
        newTriggeredSurveys.addAll(newSurveys);


        Map<String, TriggeredSurvey> deduplicatedMap = new LinkedHashMap<>();
        for (TriggeredSurvey survey : newTriggeredSurveys) {
            deduplicatedMap.put(survey.getSurvey().getSurveyUuid(), survey);
        }

        List<TriggeredSurvey> deduplicatedSurveys = new ArrayList<>(deduplicatedMap.values());


        localStorage.saveData(
                "polling:triggered_surveys",
                deduplicatedSurveys
        );

        this.checkAvailableTriggeredSurveys();
    }







}
