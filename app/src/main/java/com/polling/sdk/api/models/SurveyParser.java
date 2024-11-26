package com.polling.sdk.api.models;

import com.google.gson.Gson;

public class SurveyParser {
    public static SurveyResponse parseSurveyResponse(String jsonResponse) {
        Gson gson = new Gson();
        return gson.fromJson(jsonResponse, SurveyResponse.class);
    }
}
