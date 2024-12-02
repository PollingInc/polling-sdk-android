package com.polling.sdk.core.models;

import android.util.Log;

import com.polling.sdk.api.models.Reward;

import java.lang.reflect.Method;

public class UnityCallbackHandler implements CallbackHandler {
    private final String gameObject;
    private final String successCallback;
    private final String errorCallback;
    private final String rewardCallback;

    public UnityCallbackHandler(String gameObject, String successCallback, String errorCallback, String rewardCallback) {
        this.gameObject = gameObject;
        this.successCallback = successCallback;
        this.errorCallback = errorCallback;
        this.rewardCallback = rewardCallback;
    }

    @Override
    public void onSuccess(String response) {
        invokeUnityMessage(successCallback, response);
    }

    @Override
    public void onFailure(String error) {
        invokeUnityMessage(errorCallback, error);
    }

    @Override
    public void onReward(Reward reward) { invokeUnityMessage(rewardCallback, Reward.serialize(reward)); }

    private void invokeUnityMessage(String methodName, String message)
    {
        try {
            Log.w("Polling", "Invoking on Unity " + methodName + " for " + gameObject);
            Class<?> unityPlayerClass = Class.forName("com.unity3d.player.UnityPlayer");
            Method unitySendMessageMethod = unityPlayerClass.getMethod("UnitySendMessage", String.class, String.class, String.class);
            unitySendMessageMethod.invoke(null, gameObject, methodName, message);
        }
        catch (Exception e) {
            e.printStackTrace();
        }



    }

}
