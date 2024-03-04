package com.polling.sdk;

public class Survey
{
    RequestIdentification requestIdentification;
    CallbackHandler callbackHandler;

    public Survey(RequestIdentification requestIdentification, CallbackHandler callbackHandler)
    {
        this.requestIdentification = requestIdentification;
        this.callbackHandler = callbackHandler;
    }
    public void availableSurvey(WebRequestHandler.ResponseCallback responseCallbacks)
    {
        String url = "https://demo-api.polling.com/api/sdk/surveys/available";
        requestSurvey(url);
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
