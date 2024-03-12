package com.polling.sdk;

import android.app.Activity;
import android.content.Context;

public class Survey
{
    private final RequestIdentification requestIdentification;
    private CallbackHandler callbackHandler;

    public Survey(RequestIdentification requestIdentification, CallbackHandler callbackHandler)
    {
        this.requestIdentification = requestIdentification;
        this.callbackHandler = callbackHandler;
    }

    public void assignCallbacks(CallbackHandler callbackHandler)
    {
        this.callbackHandler = callbackHandler;
    }

    public void availableSurveys()
    {
        String url = "https://demo-api.polling.com/api/sdk/surveys/available";
        requestSurvey(url);
    }

    public void availableSurveys(Context context, ViewType viewType, RequestIdentification requestIdentification)
    {
        switch (viewType) {
            case None ->
            {
                this.availableSurveys();
            }
            case Dialog ->
            {
                DialogRequest dialogRequest = new DialogRequest(
                        (Activity) context,
                        requestIdentification.customerId,
                        requestIdentification.apiKey);

                WebViewDialogHelper dialog = new WebViewDialogHelper(dialogRequest);

                dialog.availableSurveys();
            }
            case Bottom ->
            {
                DialogRequest dialogRequest = new DialogRequest(
                        (Activity) context,
                        requestIdentification.customerId,
                        requestIdentification.apiKey);

                WebViewBottomHelper bottom = new WebViewBottomHelper(dialogRequest);
                bottom.availableSurveys();
            }
        }

    }

    public void singleSurvey(String surveyId)
    {
        String url = "https://demo-api.polling.com/api/sdk/surveys/" + surveyId;
        requestSurvey(url);
    }

    public void completedSurveys()
    {
        String url = "https://demo-api.polling.com/api/sdk/surveys/completed";
        requestSurvey(url);
    }

    private void requestSurvey(String url)
    {
        url = requestIdentification.ApplyKeyToURL(url);
        url = requestIdentification.ApplyCompletionBypassToURL(url);


        WebRequestHandler.makeRequest(url, WebRequestType.GET, null,
                new WebRequestHandler.ResponseCallback() {
                    @Override
                    public void onResponse(String response) {
                        callbackHandler.onSuccess(response);
                    }

                    @Override
                    public void onError(String error) {
                        callbackHandler.onFailure(error);
                    }
                }
        );
    }
}
