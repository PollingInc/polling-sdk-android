package com.polling.sdk;

public class Survey
{
    RequestIdentification requestIdentification;

    public Survey(RequestIdentification requestIdentification)
    {
        this.requestIdentification = requestIdentification;
    }
    public void availableSurvey(WebRequestHandler.ResponseCallback responseCallbacks)
    {
        String url = "https://demo-api.polling.com/api/sdk/surveys/available";
        requestSurvey(url, responseCallbacks);
    }

    public void singleSurvey(String surveyId, WebRequestHandler.ResponseCallback responseCallbacks)
    {
        String url = "https://demo-api.polling.com/api/sdk/surveys/" + surveyId;
        requestSurvey(url, responseCallbacks);
    }

    public void completedSurveys(WebRequestHandler.ResponseCallback responseCallbacks)
    {
        String url = "https://demo-api.polling.com/api/sdk/surveys/completed";
        requestSurvey(url, responseCallbacks);
    }

    private void requestSurvey(String url, WebRequestHandler.ResponseCallback responseCallbacks)
    {
        WebRequestHandler.makeRequest(url, WebRequestType.GET, null,responseCallbacks);
    }
}
