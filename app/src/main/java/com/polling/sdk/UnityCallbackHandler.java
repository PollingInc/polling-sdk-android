package com.polling.sdk;

import android.util.Log;

import java.lang.reflect.Method;

public class UnityCallbackHandler implements CallbackHandler {
    private final String gameObject;
    private final String successCallback;
    private final String errorCallback;

    public UnityCallbackHandler(String gameObject, String successCallback, String errorCallback) {
        this.gameObject = gameObject;
        this.successCallback = successCallback;
        this.errorCallback = errorCallback;
    }

    @Override
    public void onSuccess(String response) {
        invokeUnityMessage(successCallback, response);
    }

    @Override
    public void onFailure(String error) {
        invokeUnityMessage(errorCallback, error);
    }

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
