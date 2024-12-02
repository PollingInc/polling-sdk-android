package com.polling.sdk.core.models;

import com.polling.sdk.api.models.Reward;

public interface CallbackHandler
{
    void onSuccess(String response);
    void onFailure(String error);
    void onReward(Reward reward); //optional
    default void onSurveyAvailable(){}; //optional
    default void onPostpone(String surveyUuid){};

}
