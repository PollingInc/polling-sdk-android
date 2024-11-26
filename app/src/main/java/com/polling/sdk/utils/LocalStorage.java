package com.polling.sdk.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.polling.sdk.api.models.TriggeredSurvey;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LocalStorage
{
    private SharedPreferences sharedPreferences;

    public LocalStorage(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences("PollingUserData", Context.MODE_PRIVATE);
    }

    public void saveData(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void saveData(String key, Set<String> value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(key, value);
        editor.apply();
    }

    public void saveData(String key, List<TriggeredSurvey> data) {
        Gson gson = new Gson();
        String json = gson.toJson(data);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, json);
        editor.apply();
    }




    public String getData(String key, String defaultValue)
    {
        return sharedPreferences.getString(key, defaultValue);
    }

    public List<String> getData(String key, Set<String> defaultValue)
    {
        var set =  sharedPreferences.getStringSet(key, defaultValue);
        return new ArrayList<>(set);
    }

    public List<TriggeredSurvey> getData(String key) {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);

        if (json == null) {
            return new ArrayList<>(); // Return an empty list if no data is found
        }

        Type listType = new TypeToken<List<TriggeredSurvey>>() {}.getType();
        return gson.fromJson(json, listType);
    }




    public void clearData(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }
}
