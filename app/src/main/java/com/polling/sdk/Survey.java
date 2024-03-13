package com.polling.sdk;

import android.app.Activity;
import android.content.Context;

public class Survey
{
    private final RequestIdentification requestIdentification;
    public CallbackHandler callbackHandler;

    public Survey(RequestIdentification requestIdentification, CallbackHandler callbackHandler)
    {
        this.requestIdentification = requestIdentification;
        this.callbackHandler = callbackHandler;
    }

    public DialogHelper dialogHelper(Context context, ViewType viewType)
    {
        switch (viewType) {
            case Dialog ->
            {
                DialogRequest dialogRequest = new DialogRequest(
                        (Activity) context,
                        requestIdentification.customerId,
                        requestIdentification.apiKey);

                return new WebViewDialogHelper(dialogRequest);
            }
            case Bottom ->
            {
                DialogRequest dialogRequest = new DialogRequest(
                        (Activity) context,
                        requestIdentification.customerId,
                        requestIdentification.apiKey);

                return new WebViewBottomHelper(dialogRequest);
            }
        }

        return null;
    }


    public void updateCallbacks(CallbackHandler callbackHandler)
    {
        this.callbackHandler = callbackHandler;
    }

    public void availableSurveys()
    {
        String url = "https://demo-api.polling.com/api/sdk/surveys/available";
        requestSurvey(url);
    }

    public void availableSurveys(Context context, String viewTypeStr)
    {
        ViewType viewType = ViewType.valueOf(viewTypeStr);
        availableSurveys(context, viewType);
    }
    public void availableSurveys(Context context, ViewType viewType)
    {
        DialogHelper dialog = dialogHelper(context, viewType);

        if(dialog != null) dialog.availableSurveys(this);
        else this.availableSurveys();
    }

    public void singleSurvey(String surveyId)
    {
        String url = "https://demo-api.polling.com/api/sdk/surveys/" + surveyId;
        requestSurvey(url);
    }

    public void singleSurvey(String surveyId, Context context, String viewTypeStr)
    {
        ViewType viewType = ViewType.valueOf(viewTypeStr);
        singleSurvey(surveyId, context, viewType);
    }

    public void singleSurvey(String surveyId, Context context, ViewType viewType)
    {
        DialogHelper dialog = dialogHelper(context, viewType);

        if(dialog != null) dialog.singleSurvey(surveyId, this);
        else this.singleSurvey(surveyId);
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
