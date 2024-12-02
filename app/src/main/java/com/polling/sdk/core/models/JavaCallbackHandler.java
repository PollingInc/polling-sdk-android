package com.polling.sdk.core.models;

import com.polling.sdk.api.models.Reward;

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

    @Override
    public void onReward(Reward reward) {

    }
}
