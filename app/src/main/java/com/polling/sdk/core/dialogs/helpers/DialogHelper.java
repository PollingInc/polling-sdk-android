package com.polling.sdk.core.dialogs.helpers;

import android.content.DialogInterface;
import android.app.Dialog;
import android.util.Log;

import com.polling.sdk.api.models.SurveyDetails;
import com.polling.sdk.api.parsers.SurveyDetailsParser;
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
        awaitForReward(dialogRequest, prePostRewardCallback(false, null, survey), survey);

        var dialog = new WebViewBottom(url, dialogRequest); //used as default, but can be overridden on extended classes

        dialog.show();
        dialog.setOnDismissListener
                (
                        new DialogInterface.OnDismissListener()
                        {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface)
                            {
                                awaitForReward(dialogRequest, prePostRewardCallback(true, survey.callbackHandler, survey), survey);
                            }
                        }
                );
    }

    public void runOverride(Dialog dialog, Survey survey)
    {
        awaitForReward(dialogRequest, prePostRewardCallback(false, null, survey), survey);
        dialog.show();
        dialog.setOnDismissListener
                (
                        new DialogInterface.OnDismissListener()
                        {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface)
                            {
                                awaitForReward(dialogRequest, prePostRewardCallback(true, survey.callbackHandler, survey), survey);
                            }
                        }
                );

    }


    private void awaitForReward(DialogRequest dialog, WebRequestHandler.ResponseCallback apiCallback, Survey survey)
    {
        WebRequestHandler.makeRequest(survey.completionUrl, WebRequestType.GET,null, apiCallback);
    }

    private WebRequestHandler.ResponseCallback prePostRewardCallback(Boolean post, CallbackHandler callback, Survey survey)
    {
        return new WebRequestHandler.ResponseCallback() {
            @Override
            public void onResponse (String response)
            {
                Log.d("Polling", "onResponse json: " + response);
                List<SurveyDetails> surveysDetails = SurveyDetailsParser.parseSurveysResponse(response);

                String result = "None";
                if(surveysDetails != null){
                    result = surveysDetails.get(0).getUuid();
                }

                Log.d("Polling", "onResponse surveyDetails: " + result);

                if(!post) getRewardsPreDialog(surveysDetails, survey);
                else getRewardsPostDialog(surveysDetails, callback, survey);
            }
            @Override
            public void onError (String error)
            {
            }
        };
    }


    private void getRewardsPreDialog(List<SurveyDetails> surveyDetails, Survey survey)
    {
        if(surveyDetails == null) return;

        Log.i("Polling", "Pre-dialog available");


        for (var s : surveyDetails)
        {
            surveysUid.add(s.getUuid());
        }

    }

    private void getRewardsPostDialog(List<SurveyDetails> surveyDetails, CallbackHandler callbackHandler, Survey survey)
    {
        Log.w("Polling", "Entering post dialog");

        if(surveyDetails == null) return;

        for (var s : surveyDetails)
        {
            String uuid = s.getUuid();

            if(uuid.equals(survey.surveyUuid))
            {
                String status = s.getUserSurveyStatus();

                if(!status.equals( "complete"))
                {
                    callbackHandler.onPostpone(uuid);
                    return;
                }

                String json = SurveyDetailsParser.serializeSurveyDetails(s);
                callbackHandler.onSuccess(json);

            }
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
