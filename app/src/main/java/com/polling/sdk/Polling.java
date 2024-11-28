package com.polling.sdk;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.polling.sdk.api.models.SurveyDetails;
import com.polling.sdk.api.parsers.SurveyDetailsParser;
import com.polling.sdk.core.models.Survey;
import com.polling.sdk.api.parsers.SurveyParser;
import com.polling.sdk.api.models.SurveyResponse;
import com.polling.sdk.api.models.TriggeredSurvey;

import com.polling.sdk.core.models.CallbackHandler;
import com.polling.sdk.core.models.RequestIdentification;
import com.polling.sdk.core.network.WebRequestHandler;
import com.polling.sdk.core.network.WebRequestType;

import com.polling.sdk.utils.LocalStorage;
import com.polling.sdk.core.utils.ViewType;
import com.polling.sdk.utils.TimestampDelayer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

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
    private List<SurveyDetails> cachedAvailableSurveys;
    private int numSurveysAvailable = 0;
    private CallbackHandler callbackHandler;

    private String surveyViewBaseUrl;
    private String surveyApiBaseUrlSDK;
    private String eventApiBaseUrl;
    private String surveyViewUrl;
    private String surveysDefaultEmbedViewUrl;
    private String surveyApiUrl;
    private String eventApiUrl;


    private SdkPayload _sdkPayload;
    private LocalStorage localStorage;

    private ViewType viewType = ViewType.Dialog;

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
        _sdkPayload = sdkPayload;


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




        this.callbackHandler = new CallbackHandler() {
            @Override
            public void onPostpone(String surveyUuid) {
                onPostponeDefault(surveyUuid); //the only callback that is locked and can't be modified by client
            }

            @Override
            public void onSuccess(String response) {
                customerCallbacks.onSuccess(response);
            }

            @Override
            public void onFailure(String error) {
                customerCallbacks.onFailure(error);
            }

            @Override
            public void onReward() {
                customerCallbacks.onReward();
            }

            @Override
            public void onSurveyAvailable() {
                customerCallbacks.onSurveyAvailable();
            }
        };








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

    public void setViewType(String viewType) {
        this.viewType = ViewType.valueOf(viewType);
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
                    public void onResponse(String response)
                    {

                        SurveyResponse surveyResponse = SurveyParser.parseSurveyResponse(response);

                        List<TriggeredSurvey> triggeredSurveys = surveyResponse.getTriggeredSurveys();

                        if(triggeredSurveys != null && !triggeredSurveys.isEmpty())
                        {
                            onTriggeredSurveysUpdated(triggeredSurveys);

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
        String completionUrl = baseApiUrl + "/api/sdk/surveys/" + surveyUuid;

        Survey survey = new Survey(this.surveyViewBaseUrl, requestIdentification, null, completionUrl, surveyUuid); //WILL IT HAVE NO CALLBACKS FOR THIS ONE? I DON'T THINK SO.
        survey.singleSurvey(surveyUuid, context, this.viewType);
    }


    public void showEmbedView(Context context) {
        if (this.isSurveyCurrentlyVisible) return;

        String completionUrl = baseApiUrl + "/api/sdk/surveys/" + currentSurveyUuid; //CHECK IF THIS CODE IS RIGHT LATER <----------------------------------------------------

        Survey survey = new Survey(this.surveysDefaultEmbedViewUrl,null, null, completionUrl, currentSurveyUuid);
        survey.defaultSurvey(context, this.viewType, false);
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
            this.loadAvailableSurveys();
        }

        this.checkAvailableTriggeredSurveys();
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

    private void onPostponeDefault(String uuid)
    {
        postponeTriggeredSurvey(uuid);
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

    private void removeTriggeredSurvey(String surveyUuid)
    {
        List<TriggeredSurvey> triggeredSurveys = localStorage.getData("polling:triggered_surveys");

        if (triggeredSurveys == null) {
            return;
        }

        Iterator<TriggeredSurvey> iterator = triggeredSurveys.iterator();
        while (iterator.hasNext()) {
            TriggeredSurvey survey = iterator.next();
            if (survey.getSurvey().getSurveyUuid().equals(surveyUuid)) {
                iterator.remove();
            }
        }

        localStorage.saveData("polling:triggered_surveys", triggeredSurveys);
    }

    private void postponeTriggeredSurvey(String surveyUuid)
    {

        List<TriggeredSurvey> triggeredSurveys = localStorage.getData("polling:triggered_surveys");
        if (triggeredSurveys == null) return;

        TriggeredSurvey triggeredSurvey = null;

        for (TriggeredSurvey survey : triggeredSurveys) {
            if (survey.getSurvey().getSurveyUuid().equals(surveyUuid)) {
                triggeredSurvey = survey;
                break;
            }
        }

        if (triggeredSurvey == null) return;


        int currentDelay = triggeredSurvey.getDelaySeconds();
        triggeredSurvey.setDelaySeconds(currentDelay + (surveyClosePostponeMinutes * 60));

        String currentTimestamp = triggeredSurvey.getDelayedTimestamp();
        String updatedTimestamp = TimestampDelayer.addMinutesToTimestamp(currentTimestamp, surveyClosePostponeMinutes);
        triggeredSurvey.setDelayedTimestamp(updatedTimestamp);

        for (int i = 0; i < triggeredSurveys.size(); i++) {
            if (triggeredSurveys.get(i).getSurvey().getSurveyUuid().equals(surveyUuid)) {
                triggeredSurveys.set(i, triggeredSurvey);
                break;
            }
        }

        localStorage.saveData("polling:triggered_surveys", triggeredSurveys);
    }


    private void checkAvailableTriggeredSurveys() {
        if (isSurveyCurrentlyVisible) {
            return;
        }


        List<TriggeredSurvey> triggeredSurveys = localStorage.getData("polling:triggered_surveys");

        if (triggeredSurveys == null || triggeredSurveys.isEmpty()) {
            return;
        }

        long now = System.currentTimeMillis();

        TriggeredSurvey triggeredSurvey = null;
        for (TriggeredSurvey survey : triggeredSurveys) {
            try {
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                Date delayedTimestamp = isoFormat.parse(survey.getDelayedTimestamp());

                if (delayedTimestamp.getTime() < now) {
                    triggeredSurvey = survey;
                    break;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (triggeredSurvey == null) {
            return;
        }

        final TriggeredSurvey surveyToCheck = triggeredSurvey;


        new Thread(() -> {
            SurveyDetails surveyDetails = getSurveyDetails(surveyToCheck.getSurvey().getSurveyUuid());


            new Handler(Looper.getMainLooper()).post(() -> {
                if (surveyDetails == null || !"available".equals(surveyDetails.getUserSurveyStatus())) {
                    removeTriggeredSurvey(surveyToCheck.getSurvey().getSurveyUuid());
                    checkAvailableTriggeredSurveys();
                } else {
                    showSurvey(surveyToCheck.getSurvey().getSurveyUuid(), _sdkPayload.context);
                }
            });
        }).start();
    }


    private void loadAvailableSurveys() {
        try {

            WebRequestHandler.ResponseCallback apiCallbacks = new WebRequestHandler.ResponseCallback() {

                @Override
                public void onResponse(String response)
                {
                    cachedAvailableSurveys = SurveyDetailsParser.parseSurveysResponse(response);
                    onSurveysUpdated();
                }

                @Override
                public void onError(String error) {
                    onFailure("Failed to load survey details: " + error);
                }
            };


            WebRequestHandler.makeRequest(
                    this.surveyApiUrl,WebRequestType.GET, null, apiCallbacks);


        } catch (Exception error) {
            this.onFailure("Network error.");
        }

    }



    private SurveyDetails getSurveyDetails(String surveyUuid) {
        String url = baseApiUrl + "/api/sdk/surveys/" + surveyUuid;
        url = requestIdentification.ApplyKeyToURL(url);

        CountDownLatch latch = new CountDownLatch(1);
        SurveyDetails[] result = new SurveyDetails[1]; //storing in an array as a trick

        try {
            WebRequestHandler.ResponseCallback apiCallbacks = new WebRequestHandler.ResponseCallback() {

                @Override
                public void onResponse(String response) {
                    result[0] = SurveyDetailsParser.parseSurveyResponse(response); //still part of the trick
                    latch.countDown();
                }

                @Override
                public void onError(String error) {
                    onFailure("Failed to load survey details: " + error);
                    latch.countDown();
                }
            };

            WebRequestHandler.makeRequest(url, WebRequestType.GET, null, apiCallbacks);
            latch.await();

        } catch (Exception e) {
            onFailure("Network error.");
            e.printStackTrace();
        }

        return result[0];
    }

    /**
     * Callback method that is triggered when the available surveys are updated
     */
    private void onSurveysUpdated() {
        int previousSurveysAvailable = this.numSurveysAvailable;

        if (previousSurveysAvailable == 0 && this.numSurveysAvailable > 0) {
            this.onSurveyAvailable();
        }

        this.numSurveysAvailable = this.cachedAvailableSurveys.size();
    }






}
