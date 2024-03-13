package com.polling.sdk;

import android.content.DialogInterface;

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
        });



    }


    private void awaitForReward(DialogRequest dialog, WebRequestHandler.ResponseCallback apiCallback)
    {
        String url = "https://demo-api.polling.com/api/sdk/surveys/completed";
        WebRequestHandler.makeRequest(url,WebRequestType.GET,null, apiCallback);
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
        SurveyDataParser surveyParser = new SurveyDataParser(json);
        var surveys = surveyParser.getSurveys();

        surveysUid = new ArrayList<String>();

        for (var s : surveys)
        {
            surveysUid.add(s.get("uuid"));
        }

    }

    private void getRewardsPostDialog(String json, CallbackHandler callbackHandler)
    {
        SurveyDataParser surveyParser = new SurveyDataParser(json);
        var surveys = surveyParser.getSurveys();

        List<String> pendingUuids = new ArrayList<String>();

        for (var s : surveys)
        {
            var uuid = s.get("uuid");
            if(!surveysUid.contains(uuid))
            {
                pendingUuids.add(uuid);
            }
        }

        List<Map<String, String>> filteredSurveys = new ArrayList<>();

        for (Map<String, String> survey : surveys) {
            String uuid = survey.get("uuid");
            if (pendingUuids.contains(uuid)) {
                filteredSurveys.add(survey);
            }
        }

        if(!filteredSurveys.isEmpty())
        {
            callbackHandler.onSuccess(filteredSurveys.toString());
        }

    }


    public void availableSurveys(Survey survey)
    {
        String url = "https://demo.polling.com/sdk/available-surveys";
        showDialog(this.dialogRequest, url, survey);
    }


    public void singleSurvey(String surveyId, Survey survey)
    {
        String url = "https://demo.polling.com/sdk/survey/" + surveyId;
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
