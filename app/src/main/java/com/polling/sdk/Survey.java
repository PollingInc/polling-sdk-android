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
