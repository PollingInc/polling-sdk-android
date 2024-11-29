package com.polling.sdk;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.polling.sdk.api.models.Reward;
import com.polling.sdk.core.models.CallbackHandler;
import com.polling.sdk.core.models.RequestIdentification;

public class TestActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_test);

        Button buttonLogEvent = findViewById(R.id.buttonLogEvent);

        //LOG EVENT
        buttonLogEvent.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testLogEvent();
            }
        });

    }



    public void testLogEvent()
    {

        RequestIdentification requestIdentification =
                new RequestIdentification(
                        "test-java18",
                        "H3uZsrv6B2qyRXGePLxQ9U8g7vilWFTjIhZO");

        CallbackHandler callbackHandler = new CallbackHandler() {
            @Override
            public void onSuccess(String response)
            {
                Log.d("Polling", "Source onSuccess: " + response);

            }

            @Override
            public void onFailure(String error)
            {
                Log.d("Polling", "Source onFailure: " + error);
            }

            @Override
            public void onReward(Reward reward)
            {
                Log.d("Polling", "Source onReward triggered. " + reward.getRewardName() + ": " + reward.getRewardAmount());
            }

            @Override
            public void onSurveyAvailable()
            {
                Log.d("Polling", "Source onSurveyAvailable triggered.");
            }
        };


        SdkPayload sdkPayload = new SdkPayload(this, requestIdentification, callbackHandler, false);

        Log.d("Polling", "Requesting Polling initialization.");
        Polling polling = new Polling();
        polling.initialize(sdkPayload);
        Log.d("Polling", "Polling initialized.");


        polling.setViewType("Bottom");

        Log.d("Polling", "Calling logEvent.");
        polling.logEvent("javaTest", "1");


    }
}
