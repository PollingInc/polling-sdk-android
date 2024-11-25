package com.polling.sdk.models;

public interface CallbackHandler
{
    void onSuccess(String response);
    void onFailure(String error);
    default void onReward(){}; //optional
    default void onSurveyAvailable(){}; //optional

}
