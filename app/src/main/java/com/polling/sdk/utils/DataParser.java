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

    private Map<String, List<Map<String, Object>>> parseJson(String json)
    {

        if (json == null || json.trim().isEmpty()) {
            return null;
        }

        Gson gson = new Gson();

        Map<String, List<Map<String, Object>>> rawData = null;

        try
        {
            Type dataType = new TypeToken<Map<String, List<Map<String, Object>>>>() {
            }.getType();
            rawData = gson.fromJson(json, dataType);
        }
        catch (JsonSyntaxException e)
        {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }

        return rawData;

    }


    public void parseSurveys(String json) {

        try
        {
            this.surveys = new ArrayList<>();
            var rawData = this.parseJson(json);

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

    //----------------------------------------------------------------------------------------------

    public void parseGeneric(String json, boolean flattenNested)
    {
        try {
            this.generic = new ArrayList<>();
            var rawData = this.parseJson(json);

            if (rawData != null) {

                for (Map.Entry<String, List<Map<String, Object>>> entry : rawData.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    if (value instanceof List) {
                        List<?> list = (List<?>) value;
                        if (list.isEmpty()) {

                            Map<String, String> emptyEntry = new HashMap<>();
                            emptyEntry.put(key, "[]");
                            this.generic.add(emptyEntry);
                        } else if (list.get(0) instanceof Map) {

                            for (Object obj : list) {
                                if (obj instanceof Map) {
                                    Map<String, String> flattenedItem = new HashMap<>();
                                    for (Map.Entry<?, ?> field : ((Map<?, ?>) obj).entrySet()) {
                                        flattenedItem.put(field.getKey().toString(),
                                                field.getValue() != null ? field.getValue().toString() : "null");
                                    }
                                    this.generic.add(flattenedItem);
                                }
                            }
                        } else {
                            Map<String, String> listEntry = new HashMap<>();
                            listEntry.put(key, flattenList(list));
                            this.generic.add(listEntry);
                        }
                    } else {
                        Map<String, String> simpleEntry = new HashMap<>();
                        simpleEntry.put(key, value != null ? value.toString() : "null");
                        this.generic.add(simpleEntry);
                    }
                }
            }
        }
        catch (Exception e)
        {
            System.err.println("Error parsing generic JSON: " + e.getMessage());
        }
    }
    
    private String flattenList(List<?> list) {
        return new Gson().toJson(list);
    }

    public List<Map<String, String>> get() { return generic; }


}

