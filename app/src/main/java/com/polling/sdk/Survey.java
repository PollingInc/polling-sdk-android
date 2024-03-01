package com.polling.sdk;

public class Survey
{
    DialogRequest dialog;

    public Survey(DialogRequest dialog)
    {
        this.dialog = dialog;
    }
    public void availableSurvey()
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
        WebRequestHandler.makeRequest(url, WebRequestType.GET, null, new WebRequestHandler.ResponseCallback() {
            @Override
            public void onResponse(String response) {
                // Handle response
            }

            @Override
            public void onError(String error) {
                // Handle error
            }
        });
    }
}
