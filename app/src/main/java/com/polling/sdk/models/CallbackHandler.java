package com.polling.sdk.models;

public interface CallbackHandler
{
    void onSuccess(String response);
    void onFailure(String error);
    void OnReward();
    void OnSurveyAvailable();

}
