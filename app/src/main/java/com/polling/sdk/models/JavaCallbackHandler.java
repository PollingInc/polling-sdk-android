package com.polling.sdk.models;

public class JavaCallbackHandler implements CallbackHandler {
    @Override
    public void onSuccess(String response)
    {
        System.out.println(response);
    }

    @Override
    public void onFailure(String error)
    {
        System.out.println(error);
    }
}
