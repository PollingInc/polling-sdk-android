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
    private List<Map<String, Object>> generic;

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
        this.parse(json, flattenNested); // Parse JSON generically

        surveys = new ArrayList<>();

        // Ensure generic is not null or empty
        if (this.generic != null && !this.generic.isEmpty()) {
            Map<String, Object> genericData = this.generic.get(0); // Access the first parsed item

            // Access the "data" field, which is expected to be a list
            Object rawSurveyData = genericData.get("data");

            if (rawSurveyData instanceof List) {
                List<Map<String, Object>> rawSurveys = (List<Map<String, Object>>) rawSurveyData;

                for (Map<String, Object> survey : rawSurveys) {
                    Map<String, String> flattenedSurvey = new HashMap<>();

                    // Extract known fields from each survey
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

    //----------------------------------------------------------------------------------------------
    public void parse(String json, boolean flattenNested) {
        try {
            Map<String, Object> rawData = this.parseJson(json);

            if (rawData != null) {
                Map<String, Object> flattenedItem = new HashMap<>();
                for (Map.Entry<String, Object> entry : rawData.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    Object processedValue = processValue(value, flattenNested);
                    flattenedItem.put(key, processedValue);
                }
                this.generic.add(flattenedItem);
            }
        } catch (Exception e) {
            System.err.println("Error parsing generic JSON: " + e.getMessage());
        }
    }

    private Object processValue(Object value, boolean flattenNested) {
        if (value instanceof Map) {
            return flattenNested ? flattenNestedMap((Map<?, ?>) value) : value;
        } else if (value instanceof List) {
            return flattenNested ? flattenList((List<?>) value) : value;
        } else {
            return value != null ? value : "null";
        }
    }

    private String flattenNestedMap(Map<?, ?> map) {
        return new Gson().toJson(map);
    }

    private String flattenList(List<?> list) {
        return new Gson().toJson(list);
    }

    public List<Map<String, Object>> get() { return generic; }
}
