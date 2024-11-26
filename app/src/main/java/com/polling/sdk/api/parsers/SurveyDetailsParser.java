package com.polling.sdk.api.parsers;

import com.google.gson.Gson;
import com.polling.sdk.api.models.SurveyDetails;

import org.json.JSONException;
import org.json.JSONObject;

public class SurveyDetailsParser
{
    public static SurveyDetails parseSurveyResponse(String jsonResponse) {
        Gson gson = new Gson();

        try{
            JSONObject root = new JSONObject(jsonResponse);

            String data = root.getJSONObject("data").toString();
            return gson.fromJson(data, SurveyDetails.class);
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
