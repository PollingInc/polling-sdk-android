package com.polling.sdk;

import android.content.Context;
import android.content.SharedPreferences;

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


    public String getData(String key, String defaultValue)
    {
        return sharedPreferences.getString(key, defaultValue);
    }

    public List<String> getData(String key, Set<String> defaultValue)
    {
        var set =  sharedPreferences.getStringSet(key, defaultValue);
        return new ArrayList<>(set);
    }

    public void clearData(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }
}
