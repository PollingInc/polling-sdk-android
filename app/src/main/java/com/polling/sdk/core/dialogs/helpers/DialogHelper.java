package com.polling.sdk.core.dialogs.helpers;

import android.content.DialogInterface;
import android.app.Dialog;
import android.util.Log;

import com.polling.sdk.core.utils.DataParser;
import com.polling.sdk.core.models.CallbackHandler;
import com.polling.sdk.core.models.DialogRequest;
import com.polling.sdk.core.models.Survey;
import com.polling.sdk.core.network.WebRequestHandler;
import com.polling.sdk.core.network.WebRequestType;
import com.polling.sdk.core.dialogs.WebViewBottom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class DialogHelper
{

    public DialogRequest dialogRequest;

    private List<String> surveysUid;

    public DialogHelper(DialogRequest dialog)
    {
        this.dialogRequest = dialog;
    }

    public void showDialog(DialogRequest dialogRequest, String url, Survey survey)
    {
        dialogRequest.activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                runDefault(url, survey);
            }
        });
    }

    private void runDefault(String url, Survey survey)
    {
        awaitForReward(dialogRequest, prePostRewardCallback(false, null));

        var dialog = new WebViewBottom(url, dialogRequest); //used as default, but can be overridden on extended classes

        dialog.show();
        dialog.setOnDismissListener
                (
                        new DialogInterface.OnDismissListener()
                        {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface)
                            {
                                awaitForReward(dialogRequest, prePostRewardCallback(true, survey.callbackHandler));
                            }
                        }
                );
    }

    public void runOverride(Dialog dialog, Survey survey)
    {
        awaitForReward(dialogRequest, prePostRewardCallback(false, null));
        dialog.show();
        dialog.setOnDismissListener
                (
                        new DialogInterface.OnDismissListener()
                        {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface)
                            {
                                awaitForReward(dialogRequest, prePostRewardCallback(true, survey.callbackHandler));
                            }
                        }
                );

    }


    private void awaitForReward(DialogRequest dialog, WebRequestHandler.ResponseCallback apiCallback)
    {
        String url = "https://demo-api.polling.com/api/sdk/surveys/completed";
        url = dialog.ApplyKeyToURL(url);

        WebRequestHandler.makeRequest(url, WebRequestType.GET,null, apiCallback);
    }

    private WebRequestHandler.ResponseCallback prePostRewardCallback(Boolean post, CallbackHandler callback)
    {
        return new WebRequestHandler.ResponseCallback() {
            @Override
            public void onResponse (String response)
            {
                if(!post) getRewardsPreDialog(response);
                else getRewardsPostDialog(response, callback);
            }
            @Override
            public void onError (String error)
            {
            }
        };
    }


    private void getRewardsPreDialog(String json)
    {
        Log.i("Polling", "Parsing JSON: " + json);

        DataParser surveyParser = new DataParser();

        surveyParser.parseSurveys(json, true);
        var surveys = surveyParser.getSurveys();

        surveysUid = new ArrayList<String>();

        for (var s : surveys)
        {
            surveysUid.add(s.get("uuid"));
        }

    }

    private void getRewardsPostDialog(String json, CallbackHandler callbackHandler)
    {
        Log.w("Polling", "Entering post dialog");

        DataParser surveyParser = new DataParser();

        surveyParser.parseSurveys(json, true);
        var surveys = surveyParser.getSurveys();

        List<String> pendingUuids = new ArrayList<String>();

        for (var s : surveys)
        {
            var uuid = s.get("uuid");
            if(!surveysUid.contains(uuid))
            {
                Log.w("Polling", "Pending Uuid: " + uuid);
                pendingUuids.add(uuid);
            }
            else Log.w("Polling", "Not pending Uuid: " + uuid);
        }

        List<Map<String, String>> filteredSurveys = new ArrayList<>();

        for (Map<String, String> survey : surveys) {
            String uuid = survey.get("uuid");
            if (pendingUuids.contains(uuid)) {
                Log.w("Polling:", "Filtered survey uuid: " + uuid);
                filteredSurveys.add(survey);
            }
        }

        if(!filteredSurveys.isEmpty())
        {
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("[");

            for (int i = 0; i < filteredSurveys.size(); i++) {
                Map<String, String> survey = filteredSurveys.get(i);
                jsonBuilder.append("{");
                for (String key : survey.keySet()) {
                    jsonBuilder.append("\"").append(key).append("\":\"").append(survey.get(key)).append("\",");
                }
                // Remove the trailing comma
                if (!survey.isEmpty()) {
                    jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
                }
                jsonBuilder.append("}");
                if (i < filteredSurveys.size() - 1) {
                    jsonBuilder.append(",");
                }
            }

            jsonBuilder.append("]");
            Log.w("Polling", "Rewards json: " + jsonBuilder.toString());

            callbackHandler.onSuccess(jsonBuilder.toString());



        }

    }


    public void defaultSurvey(Survey survey, String url)
    {
        showDialog(this.dialogRequest, url, survey);
    }


    public void singleSurvey(String surveyId, Survey survey, String url)
    {
        showDialog(this.dialogRequest, url, survey);
    }
}



/*

EXAMPLE WEB VIEW
https://demo.polling.com/sdk/available-surveys?customer_id=123&api_key=cli_wZJW1tH39TfUMbEumPLrDy15EXDqJA0a

Available surveys
GET     https://demo-api.polling.com/api/sdk/surveys/available?customer_id=[ID]&api_key=cli_[KEY]
WebView https://demo.polling.com/sdk/available-surveys?customer_id=[ID]&api_key=cli_[KEY]

Specific survey
GET     https://demo-api.polling.com/api/sdk/surveys/[UUID]?customer_id={ID}&api_key=cli_[KEY]
WebView https://demo.polling.com/sdk/survey/[uuid]?customer_id=[ID]&api_key=cli_[KEY]

Completed surveys:
GET     https://demo-api.polling.com/api/sdk/surveys/completed?customer_id=[ID]&api_key=cli_[KEY]

*/
