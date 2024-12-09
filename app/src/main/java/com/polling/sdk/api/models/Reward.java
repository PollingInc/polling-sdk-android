package com.polling.sdk.api.models;

import com.google.gson.Gson;

public class Reward {
    private String reward_amount;
    private String reward_name;
    private String complete_extra_json;

    public String getRewardAmount() {
        return reward_amount;
    }

    public String getRewardName() {
        return reward_name;
    }

    public String getCompleteExtraJson()
    {
        return complete_extra_json;
    }

    public static String serialize(Reward reward) {
        if(reward == null) return null;
        Gson gson = new Gson();
        return gson.toJson(reward);
    }
}
