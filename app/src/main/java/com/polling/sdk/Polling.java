package com.polling.sdk;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.polling.sdk.api.models.Reward;
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
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;

public class Polling
{
    private final String baseUrl = "https://app.polling.com";
    private final String baseApiUrl = "https://api.polling.com";

    private RequestIdentification requestIdentification = new RequestIdentification();


    public boolean initialized = false;
    private String currentSurveyUuid = null;


    private Handler surveyPollHandler;
    private Runnable surveyPollRunnable;

    private int surveyPollRateMsec = 3_600_000; //1 hour
    private int surveyClosePostponeMinutes  = 30;

    private boolean isSurveyCurrentlyVisible = false; //visible actually means "being displayed". Keeping name like in JS SDK.

    private boolean isAvailableSurveysCheckDisabled = false;
    private List<SurveyDetails> cachedAvailableSurveys;
    private int numSurveysAvailable = 0;
    private CallbackHandler callbackHandler;

    private String surveyViewBaseUrl;
    private String surveyApiBaseUrlSDK;
    private String eventApiBaseUrl;
    private String surveyViewUrl;
    private String surveysDefaultEmbedViewUrl;
    private String surveysEmbedViewCompletionUrl;
    private String surveyApiUrl;
    private String eventApiUrl;


    private SdkPayload _sdkPayload;

    private ViewType viewType = ViewType.Dialog;
    private final Handler surveyDispatcher = new Handler(); // Shared Handler



    public Polling()
    {
        this.surveyViewBaseUrl = this.baseUrl + "/sdk";
        this.surveyApiBaseUrlSDK = this.baseApiUrl + "/api/sdk/surveys";
        this.eventApiBaseUrl = this.baseApiUrl + "/api/events/collect";

        surveyViewUrl = this.surveyViewBaseUrl + "/available-surveys";
        surveysDefaultEmbedViewUrl = this.baseUrl + "/embed/";
        surveysEmbedViewCompletionUrl = this.surveyApiBaseUrlSDK + "/completed";
        surveyApiUrl = this.surveyApiBaseUrlSDK + "/available";
        eventApiUrl = this.eventApiBaseUrl;
    }

    //--------------------------------------------------------------------------------------------------
    public void initialize(SdkPayload sdkPayload) {
        if (this.initialized) {
            return;
        }

        LocalStorage.getInstance();
        LocalStorage.setup(sdkPayload.activity.getApplicationContext());

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
            public void onOpen()
            {
                Log.d("Polling", "Survey dialog was opened.");
                isSurveyCurrentlyVisible = true;
            }

            @Override
            public void onDismiss()
            {
                Log.d("Polling", "Survey dialog was dismissed.");
                isSurveyCurrentlyVisible = false;
            }

            @Override
            public void onPostpone(String surveyUuid) {
                onPostponeDefault(surveyUuid); //the only callback that is locked and can't be modified by client
            }

            @Override
            public void onCompletion(SurveyDetails surveyDetails)
            {
                if(!"available".equals(surveyDetails.getUserSurveyStatus()))
                {
                    removeTriggeredSurvey(surveyDetails.getUuid());
                }

                resetIntervalLogic(true); //seems to cause issue from here: Can't create handler inside thread
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
            public void onReward(Reward reward) {
                customerCallbacks.onReward(reward);
            }

            @Override
            public void onSurveyAvailable() {
                customerCallbacks.onSurveyAvailable();
            }
        };

        this.setupPostMessageBridge();

        resetIntervalLogic(true);
    }
    //--------------------------------------------------------------------------------------------------
    private void resetIntervalLogic(boolean runImmediately)
    {
        try
        {
            if (surveyPollHandler == null)
            {
                surveyPollHandler = new Handler(Looper.getMainLooper());
            }

            else
            {
                surveyPollHandler.removeCallbacksAndMessages(null);
            }

            surveyPollRunnable = new Runnable() {
                @Override
                public void run() {
                    if (surveyPollHandler == null) return;
                    intervalLogic(); // Executes the logic
                    surveyPollHandler.postDelayed(this, surveyPollRateMsec); // Re-schedules itself
                }
            };

            if(runImmediately)
            {
                intervalLogic(); //runs intervalLogic immediately and then let it sets to next interval
            }
            surveyPollHandler.post(surveyPollRunnable); // Schedules the first execution
        }
        catch (Exception e)
        {
            Log.e("Polling", "Failure on resetting interval logic.");
        }
    }


    //--------------------------------------------------------------------------------------------------
    private void setCustomerId(String customerId) {
        this.requestIdentification.customerId = customerId;
        updateUrls();
    }

    private void setApiKey(String apiKey) {
        this.requestIdentification.apiKey = apiKey;
        updateUrls();
    }

    public void setViewType(String viewType) {
        this.viewType = ViewType.valueOf(viewType);
    }

    public void setViewType(ViewType viewType) {
        this.viewType = viewType;
    }

    //--------------------------------------------------------------------------------------------------
    public void logPurchase(int integerCents)
    {
        this.logEvent("Purchase", Integer.toString(integerCents));
    }

    public void logSession() {
        this.logEvent("Session", null);
    }

    public void logEvent(String eventName, String eventValue)
    {
        Log.d("Polling", "logEvent called");

        new Thread(() ->
        {
            try {
                WebRequestHandler.ResponseCallback apiCallbacks = new WebRequestHandler.ResponseCallback() {

                    @Override
                    public void onResponse(String response)
                    {
                        Log.d("Polling", "logEvent API response received.");

                        SurveyResponse surveyResponse = SurveyParser.parseSurveyResponse(response);

                        List<TriggeredSurvey> triggeredSurveys = surveyResponse.getTriggeredSurveys();

                        if(triggeredSurveys != null && !triggeredSurveys.isEmpty())
                        {
                            Log.d("Polling", "logEvent calling onTriggeredSurveysUpdated");
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

                resetIntervalLogic(true);


                Log.d("Polling", "Web request sent to: " + eventApiUrl);

            } catch (Exception error) {
                this.callbackHandler.onFailure("Network error.");
            }
        }).start();
    }

    //--------------------------------------------------------------------------------------------------
    public void showSurvey(String surveyUuid, Activity activity) {

        Log.d("Polling", "showSurvey requested on Polling class");

        if (this.isSurveyCurrentlyVisible) return;

        this.currentSurveyUuid = surveyUuid;
        String completionUrl = baseApiUrl + "/api/sdk/surveys/" + surveyUuid;
        completionUrl = requestIdentification.ApplyKeyToURL(completionUrl);

        Survey survey = new Survey(this.surveyViewBaseUrl, requestIdentification, this.callbackHandler, completionUrl, surveyUuid); //WILL IT HAVE NO CALLBACKS FOR THIS ONE? I DON'T THINK SO.
        survey.singleSurvey(surveyUuid, activity, this.viewType);
    }


    public void showEmbedView(Activity activity) {
        if (this.isSurveyCurrentlyVisible) return;


        Survey survey = new Survey(this.surveysDefaultEmbedViewUrl,null, this.callbackHandler, surveysEmbedViewCompletionUrl, currentSurveyUuid, true);
        survey.defaultSurvey(activity, this.viewType, false);
    }

    //--------------------------------------------------------------------------------------------------
    public List<String> getLocalSurveyResults(String surveyUuid)
    {
        return LocalStorage.getData(surveyUuid, (Set<String>) null);
    }

    //--------------------------------------------------------------------------------------------------
    private void updateUrls()
    {
        if(requestIdentification.apiKey != null)
        {
            surveysDefaultEmbedViewUrl += requestIdentification.apiKey;
        }


        this.surveysDefaultEmbedViewUrl = requestIdentification.ApplyKeyToURL(surveysDefaultEmbedViewUrl,"customer_id", null);
        this.surveysEmbedViewCompletionUrl = requestIdentification.ApplyKeyToURL(surveysEmbedViewCompletionUrl);
        this.surveyViewUrl = requestIdentification.ApplyKeyToURL(surveyViewUrl);
        this.surveyApiUrl = requestIdentification.ApplyKeyToURL(surveyApiUrl);
        this.eventApiUrl = requestIdentification.ApplyKeyToURL(eventApiUrl, "user", "api_key");
    }

    //--------------------------------------------------------------------------------------------------
    private void intervalLogic() {
        if (!this.initialized || this.requestIdentification.apiKey == null || this.requestIdentification.customerId == null) return;

        if (!this.isAvailableSurveysCheckDisabled) {
            this.loadAvailableSurveys();
        }

        Log.d("Polling","checkAvailableTriggeredSurveys called by intervalLogic");
        this.checkAvailableTriggeredSurveys();
    }

    //--------------------------------------------------------------------------------------------------
    /**
     * Store the survey results
     */
    private void storeLocalSurveyResult(String surveyUuid, String surveyResultData)
    {
        LocalStorage.saveData(surveyUuid, surveyResultData);
    }

    //--------------------------------------------------------------------------------------------------
    private void setupPostMessageBridge()
    {
        //seems to be unnecessary due to what we already have for this SDK in Java.
    }

    //--------------------------------------------------------------------------------------------------
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

    //--------------------------------------------------------------------------------------------------
    private void onTriggeredSurveysUpdated(List<TriggeredSurvey> newSurveys)
    {
        List<TriggeredSurvey> storedSurveys = LocalStorage.getData("polling:triggered_surveys");

        Map<String, TriggeredSurvey> deduplicatedMap = new LinkedHashMap<>();

        for (TriggeredSurvey survey : storedSurveys) {
            deduplicatedMap.put(survey.getSurvey().getSurveyUuid(), survey);
        }

        for (TriggeredSurvey survey : newSurveys) {
            String surveyUuid = survey.getSurvey().getSurveyUuid();
            if (!deduplicatedMap.containsKey(surveyUuid)) {
                deduplicatedMap.put(surveyUuid, survey);
            }
        }

        List<TriggeredSurvey> deduplicateSurveys = new ArrayList<>(deduplicatedMap.values());

        LocalStorage.saveData(
                "polling:triggered_surveys",
                deduplicateSurveys
        );

        Log.d("Polling","checkAvailableTriggeredSurveys called by onTriggeredSurveysUpdated");
        this.checkAvailableTriggeredSurveys();
    }

    private void removeTriggeredSurvey(String surveyUuid)
    {
        List<TriggeredSurvey> triggeredSurveys = LocalStorage.getData("polling:triggered_surveys");

        if (triggeredSurveys == null) {
            return;
        }

        Iterator<TriggeredSurvey> iterator = triggeredSurveys.iterator();
        while (iterator.hasNext()) {
            TriggeredSurvey survey = iterator.next();
            if (survey.getSurvey().getSurveyUuid().equals(surveyUuid))
            {
                Log.d("Polling", "Deleting survey " + surveyUuid);
                iterator.remove();
            }
        }

        LocalStorage.saveData("polling:triggered_surveys", triggeredSurveys);
    }

    private void postponeTriggeredSurvey(String surveyUuid)
    {

        List<TriggeredSurvey> triggeredSurveys = LocalStorage.getData("polling:triggered_surveys");
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

        LocalStorage.saveData("polling:triggered_surveys", triggeredSurveys);
    }


    private void checkAvailableTriggeredSurveys() {
        if (isSurveyCurrentlyVisible) {
            return;
        }

        List<TriggeredSurvey> triggeredSurveys = LocalStorage.getData("polling:triggered_surveys");

        if (triggeredSurveys == null || triggeredSurveys.isEmpty()) {
           Log.d("Polling", "No triggered surveys available.");
            return;
        }

        Log.d("Polling", "Triggered survey(s) available.");
        long now = System.currentTimeMillis();

        for (TriggeredSurvey survey : triggeredSurveys) {
            try {
                String delayedTimestampStr = survey.getDelayedTimestamp();

                if (delayedTimestampStr.endsWith("Z")) {
                    delayedTimestampStr = delayedTimestampStr.replace("Z", "+0000");
                }

                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date delayedTimestamp = isoFormat.parse(delayedTimestampStr);

                // DEBUG -----------------------------
                Log.d("Polling", "API delay in seconds: " + survey.getDelaySeconds());
                Log.d("Polling", "API delayed timestamp: "+ survey.getDelayedTimestamp());
                Log.d("Polling", "Delayed timestamp (UTC): " + delayedTimestamp);

                // Ensure "now" is interpreted in UTC
                Date nowDate = new Date(now);
                Log.d("Polling", "Current time (UTC): " + nowDate);
                // DEBUG -----------------------------

                int delayInSeconds;

                if (delayedTimestamp.getTime() < now)
                {
                    delayInSeconds = 0;
                }

                else
                {
                    delayInSeconds = survey.getDelaySeconds();
                }


                Log.d("Polling", "Final delay is " + delayInSeconds + "for survey " + survey.getSurvey().getSurveyUuid());


                if(survey.isInUse) continue;

                final TriggeredSurvey finalSurvey = survey;

                var surveyRunnable = new Runnable(){

                    @Override
                    public void run()
                    {
                        var savedSurveys = LocalStorage.getData("polling:triggered_surveys");

                        if(savedSurveys.contains(finalSurvey))
                        {
                            int index = savedSurveys.indexOf(finalSurvey);
                            finalSurvey.isInUse = true;
                            savedSurveys.set(index, finalSurvey);
                            LocalStorage.saveData("polling:triggered_surveys", savedSurveys);
                        }


                        final SurveyDetails surveyDetails = getSurveyDetails(finalSurvey.getSurvey().getSurveyUuid());

                        if (surveyDetails == null || !"available".equals(surveyDetails.getUserSurveyStatus())) {
                            Log.d("Polling", "None of the present surveys are in available status.");
                            removeTriggeredSurvey(finalSurvey.getSurvey().getSurveyUuid());

                            Log.d("Polling", "checkAvailableTriggeredSurveys called by itself");
                            checkAvailableTriggeredSurveys();
                        } else {
                            Log.d("Polling", "Found survey in available status. Requesting showSurvey");
                            showSurvey(finalSurvey.getSurvey().getSurveyUuid(), _sdkPayload.activity);
                        }

                        savedSurveys = LocalStorage.getData("polling:triggered_surveys");

                        if(savedSurveys.contains(finalSurvey))
                        {
                            int index = savedSurveys.indexOf(finalSurvey);
                            finalSurvey.isInUse = false;
                            savedSurveys.set(index, finalSurvey);
                            LocalStorage.saveData("polling:triggered_surveys", savedSurveys);
                        }
                    }
                };

                surveyDispatcher.postDelayed(surveyRunnable, delayInSeconds * 1000L);

                Log.d("Polling", "Triggered survey should be delayed.");

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    //--------------------------------------------------------------------------------------------------
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

    //--------------------------------------------------------------------------------------------------
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

    //--------------------------------------------------------------------------------------------------
    /**
     * Callback method that is triggered when the available surveys are updated
     */
    private void onSurveysUpdated()
    {
        var previousSurveysAvailable = this.numSurveysAvailable;
        this.numSurveysAvailable = this.cachedAvailableSurveys.size();

        if (previousSurveysAvailable == 0 && this.numSurveysAvailable > 0) {
            this.onSurveyAvailable();
        }

    }

}
