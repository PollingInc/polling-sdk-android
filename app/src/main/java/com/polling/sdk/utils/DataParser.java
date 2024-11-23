package com.polling.sdk.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataParser {
    private List<Map<String, String>> surveys;
    private List<Map<String, String>> generic;

    public DataParser() {
        this.surveys = new ArrayList<>();
        this.generic = new ArrayList<>();
    }

    private Map<String, Object> parseJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }

        Gson gson = new Gson();
        Map<String, Object> rawData = null;

        try {
            Type dataType = new TypeToken<Map<String, Object>>() {}.getType();
            rawData = gson.fromJson(json, dataType);
        } catch (JsonSyntaxException e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }

        return rawData;
    }

    public List<Map<String, String>> parseSurveys(String json, boolean flattenNested) {
        this.parseGeneric(json, flattenNested);

        surveys = new ArrayList<>();

        if (this.generic != null && !this.generic.isEmpty()) {
            Map<String, String> genericData = this.generic.get(0);

            String rawSurveyDataJson = genericData.get("data");
            if (rawSurveyDataJson != null && !rawSurveyDataJson.isEmpty() && !rawSurveyDataJson.equals("null")) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
                List<Map<String, Object>> rawSurveys = gson.fromJson(rawSurveyDataJson, listType);

                for (Map<String, Object> survey : rawSurveys) {
                    Map<String, String> flattenedSurvey = new HashMap<>();

                    flattenedSurvey.put("uuid", (String) survey.get("uuid"));
                    flattenedSurvey.put("name", (String) survey.get("name"));
                    flattenedSurvey.put("started_at", (String) survey.get("started_at"));
                    flattenedSurvey.put("completed_at", (String) survey.get("completed_at"));

                    @SuppressWarnings("unchecked")
                    Map<String, Object> reward = (Map<String, Object>) survey.get("reward");
                    if (reward != null) {
                        flattenedSurvey.put("reward_amount", String.valueOf(reward.get("reward_amount")));
                        flattenedSurvey.put("reward_name", String.valueOf(reward.get("reward_name")));
                    }

                    surveys.add(flattenedSurvey);
                }
            }
        }

        return surveys;
    }

    public List<Map<String, String>> getSurveys() { return surveys; }

    public void parseGeneric(String json, boolean flattenNested) {
        try {
            Map<String, Object> rawData = this.parseJson(json);

            if (rawData != null) {
                Map<String, String> flattenedItem = new HashMap<>();
                for (Map.Entry<String, Object> entry : rawData.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    String processedValue = processValue(value, flattenNested);
                    flattenedItem.put(key, processedValue);
                }
                this.generic.add(flattenedItem);
            }
        } catch (Exception e) {
            System.err.println("Error parsing generic JSON: " + e.getMessage());
        }
    }

    private String processValue(Object value, boolean flattenNested) {
        if (value instanceof Map) {
            return flattenNested ? flattenNestedMap((Map<?, ?>) value) : value.toString();
        } else if (value instanceof List) {
            return flattenNested ? flattenList((List<?>) value) : value.toString();
        } else {
            return value != null ? value.toString() : "null";
        }
    }

    private String flattenNestedMap(Map<?, ?> map) {
        return new Gson().toJson(map);
    }

    private String flattenList(List<?> list) {
        return new Gson().toJson(list);
    }

    public List<Map<String, String>> get() { return generic; }
}
