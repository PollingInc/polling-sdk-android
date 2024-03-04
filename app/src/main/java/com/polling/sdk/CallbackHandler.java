package com.polling.sdk;

public interface CallbackHandler
{
    void onSuccess(String response);
    void onFailure(String error);
}
