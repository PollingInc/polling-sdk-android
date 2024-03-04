package com.polling.sdk;

import java.lang.reflect.Method;

public class UnityCallbackHandler implements CallbackHandler {
    private String gameObject;
    private String successCallback;
    private String errorCallback;

    public UnityCallbackHandler(String gameObject, String successCallback, String errorCallback) {
        this.gameObject = gameObject;
        this.successCallback = successCallback;
        this.errorCallback = errorCallback;
    }

    @Override
    public void onSuccess(String response) {
        // Use reflection to call UnitySendMessage
        invokeUnityMessage(successCallback, response);
    }

    @Override
    public void onFailure(String error) {
        // Use reflection to call UnitySendMessage
        invokeUnityMessage(errorCallback, error);
    }

    private void invokeUnityMessage(String methodName, String message) {

        // Reflection-based call to UnityPlayer.UnitySendMessage
        // Similar to the previous example provided for reflection
        try {
            Class<?> unityPlayerClass = Class.forName("com.unity3d.player.UnityPlayer");
            Method unitySendMessageMethod = unityPlayerClass.getMethod("UnitySendMessage", String.class, String.class, String.class);
            unitySendMessageMethod.invoke(null, gameObject, methodName, message);
        }
        catch (Exception e) {
            e.printStackTrace();
        }



    }

}
