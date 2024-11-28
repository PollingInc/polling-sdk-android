package com.polling.sdk.api.parsers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.polling.sdk.api.models.SurveyDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

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

    public static List<SurveyDetails> parseSurveysResponse(String jsonResponse) {
        Gson gson = new Gson();

        try {
            JSONObject root = new JSONObject(jsonResponse);

            String data = root.getJSONArray("data").toString();
            Type listType = new TypeToken<List<SurveyDetails>>() {}.getType();

            return gson.fromJson(data, listType);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    //----------------------------------------------------------------------------------------------

    public static String serializeSurveyDetails(SurveyDetails surveyDetails) {
        Gson gson = new Gson();
        return gson.toJson(surveyDetails);
    }

}
