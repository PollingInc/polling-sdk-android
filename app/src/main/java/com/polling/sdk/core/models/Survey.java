package com.polling.sdk.core.models;

import android.app.Activity;
import android.content.Context;

import com.polling.sdk.core.dialogs.helpers.DialogHelper;
import com.polling.sdk.core.dialogs.helpers.WebViewBottomHelper;
import com.polling.sdk.core.dialogs.helpers.WebViewDialogHelper;
import com.polling.sdk.core.network.WebRequestHandler;
import com.polling.sdk.core.network.WebRequestType;
import com.polling.sdk.core.utils.ViewType;

public class Survey
{
    private final RequestIdentification requestIdentification;
    public CallbackHandler callbackHandler;

    public String url;

    public String completionUrl;

    public Survey(String url, RequestIdentification requestIdentification, CallbackHandler callbackHandler, String completionUrl)
    {
        this.url = url;
        this.requestIdentification = requestIdentification;
        this.callbackHandler = callbackHandler;
        this.completionUrl = completionUrl;
    }

    private DialogHelper dialogHelper(Context context, ViewType viewType)
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

    //----------------------------------------------------------------------------------------------
    public void updateCallbacks(CallbackHandler callbackHandler)
    {
        this.callbackHandler = callbackHandler;
    }


    //----------------------------------------------------------------------------------------------
    public void defaultSurvey(Context context, String viewTypeStr, boolean applyKey)
    {
        ViewType viewType = ViewType.valueOf(viewTypeStr);
        defaultSurvey(context, viewType, applyKey);
    }

    public void defaultSurvey(Context context, ViewType viewType, boolean applyKey)
    {
        DialogHelper dialog = dialogHelper(context, viewType);

        String finalUrl = url;

        if(applyKey)
        {
            finalUrl = requestIdentification.ApplyKeyToURL(url);
        }


        if(dialog != null) dialog.defaultSurvey(this, finalUrl);

    }

    //----------------------------------------------------------------------------------------------
    public void availableSurveys(Context context, String viewTypeStr)
    {
        ViewType viewType = ViewType.valueOf(viewTypeStr);
        availableSurveys(context, viewType);
    }
    public void availableSurveys(Context context, ViewType viewType)
    {
        DialogHelper dialog = dialogHelper(context, viewType);

        String finalUrl = requestIdentification.ApplyKeyToURL(url);

        if(dialog != null) dialog.defaultSurvey(this, finalUrl);
    }

    //----------------------------------------------------------------------------------------------
    public void singleSurvey(String surveyId, Context context, String viewTypeStr)
    {
        ViewType viewType = ViewType.valueOf(viewTypeStr);
        singleSurvey(surveyId, context, viewType);
    }

    public void singleSurvey(String surveyId, Context context, ViewType viewType)
    {
        DialogHelper dialog = dialogHelper(context, viewType);

        String finalUrl = (url.endsWith("/") ? url : url + "/") + "survey/" + surveyId;
        finalUrl = requestIdentification.ApplyKeyToURL(finalUrl);

        if(dialog != null) dialog.singleSurvey(surveyId, this, finalUrl);
    }

    //----------------------------------------------------------------------------------------------
    public void completedSurveys()
    {
        String url = "https://demo-api.polling.com/api/sdk/surveys/completed";
        requestSurvey(url);
    }

    //----------------------------------------------------------------------------------------------
    private void requestSurvey(String url)
    {
        String finalUrl = requestIdentification.ApplyKeyToURL(url);

        WebRequestHandler.makeRequest(finalUrl, WebRequestType.GET, null,
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
