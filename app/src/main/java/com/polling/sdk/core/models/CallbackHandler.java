package com.polling.sdk.core.models;

import com.polling.sdk.api.models.Reward;
import com.polling.sdk.api.models.SurveyDetails;

public interface CallbackHandler
{
    void onSuccess(String response);
    void onFailure(String error);
    void onReward(Reward reward); //optional
    default void onSurveyAvailable(){}; //optional
    default void onPostpone(String surveyUuid){}; //internal usage

    default void onOpen(){}; //internal usage
    default void onDismiss(){}; //internal usage

    default void onCompletion(SurveyDetails surveyDetails){}; //internal usage

}
