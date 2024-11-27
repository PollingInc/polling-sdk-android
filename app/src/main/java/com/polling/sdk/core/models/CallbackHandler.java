package com.polling.sdk.core.models;

public interface CallbackHandler
{
    void onSuccess(String response);
    void onFailure(String error);
    default void onReward(){}; //optional
    default void onSurveyAvailable(){}; //optional
    default void onPostpone(String surveyUuid){};

}
