package com.polling.sdk;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SurveyDataParser {
    private List<Map<String, String>> surveys;

    public SurveyDataParser(String jsonInput) {
        this.surveys = new ArrayList<>();
        parseJson(jsonInput);
    }

    private void parseJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            // Handle the case where the JSON string is null or empty
            return;
        }

        Gson gson = new Gson();
        try {
            Type dataType = new TypeToken<Map<String, List<Map<String, Object>>>>() {}.getType();
            Map<String, List<Map<String, Object>>> rawData = gson.fromJson(json, dataType);

            // Check if the 'data' key exists and has content
            if (rawData != null && rawData.containsKey("data") && !rawData.get("data").isEmpty()) {
                for (Map<String, Object> survey : rawData.get("data")) {
                    Map<String, String> flattenedSurvey = new HashMap<>();
                    flattenedSurvey.put("uuid", (String) survey.get("uuid"));
                    flattenedSurvey.put("name", (String) survey.get("name"));
                    flattenedSurvey.put("started_at", (String) survey.get("started_at"));
                    flattenedSurvey.put("completed_at", (String) survey.get("completed_at"));

                    @SuppressWarnings("unchecked")
                    Map<String, Object> reward = (Map<String, Object>) survey.get("reward");
                    // Ensure reward is not null and contains the keys before accessing them
                    if (reward != null) {
                        flattenedSurvey.put("reward_amount", String.valueOf(reward.get("reward_amount")));
                        flattenedSurvey.put("reward_name", String.valueOf(reward.get("reward_name")));
                    }

                    this.surveys.add(flattenedSurvey);
                }
            }
        } catch (JsonSyntaxException e) {
            // Log the exception or handle it according to your application's needs
            System.err.println("Error parsing JSON: " + e.getMessage());
        }
    }



    public List<Map<String, String>> getSurveys() {
        return surveys;
    }
}

