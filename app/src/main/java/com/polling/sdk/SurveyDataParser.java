package com.polling.sdk;

import com.google.gson.Gson;
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
        Gson gson = new Gson();
        Type dataType = new TypeToken<Map<String, List<Map<String, Object>>>>() {}.getType();
        Map<String, List<Map<String, Object>>> rawData = gson.fromJson(json, dataType);

        for (Map<String, Object> survey : rawData.get("data")) {
            Map<String, String> flattenedSurvey = new HashMap<>();
            flattenedSurvey.put("uuid", (String) survey.get("uuid"));
            flattenedSurvey.put("name", (String) survey.get("name"));
            flattenedSurvey.put("started_at", (String) survey.get("started_at"));
            flattenedSurvey.put("completed_at", (String) survey.get("completed_at"));

            @SuppressWarnings("unchecked")
            Map<String, String> reward = (Map<String, String>) survey.get("reward");
            flattenedSurvey.putAll(reward); // This assumes reward's keys are unique and don't clash with survey's keys

            this.surveys.add(flattenedSurvey);
        }
    }

    public List<Map<String, String>> getSurveys() {
        return surveys;
    }
}

