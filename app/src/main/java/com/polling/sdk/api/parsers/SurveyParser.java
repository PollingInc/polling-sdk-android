package com.polling.sdk.api.parsers;

import com.google.gson.Gson;
import com.polling.sdk.api.models.SurveyResponse;

public class SurveyParser {
    public static SurveyResponse parseSurveyResponse(String jsonResponse) {
        Gson gson = new Gson();
        return gson.fromJson(jsonResponse, SurveyResponse.class);
    }
}
