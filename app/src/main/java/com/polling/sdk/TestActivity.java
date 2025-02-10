package com.polling.sdk;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.polling.sdk.api.models.Reward;
import com.polling.sdk.core.models.CallbackHandler;
import com.polling.sdk.core.models.RequestIdentification;
import com.polling.sdk.core.utils.ViewType;

public class TestActivity extends Activity
{

    Polling polling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_test);

        initializePolling();

        EditText inputSurveyUuid = findViewById(R.id.inputSurveyUuid);
        Button buttonShowSurvey = findViewById(R.id.buttonShowSurvey);

        Button buttonLogEvent = findViewById(R.id.buttonLogEvent);
        Button buttonShowEmbed = findViewById(R.id.buttonShowEmbed);

        //SHOW SURVEY
        buttonShowSurvey.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testShowSurvey(inputSurveyUuid.getText().toString()); }
        });

        //LOG EVENT
        buttonLogEvent.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testLogEvent();
            }
        });

        //SHOW EMBED
        buttonShowEmbed.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testShowEmbed();
                    }
        });

    }

    public void initializePolling()
    {
        RequestIdentification requestIdentification =
                new RequestIdentification(
                        "test-java" + System.currentTimeMillis(),
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
                Log.d("Polling", "Source onReward triggered. " +
                        reward.getRewardName() + ": " + reward.getRewardAmount() +
                        " | extra json: " + reward.getCompleteExtraJson());
            }

            @Override
            public void onSurveyAvailable()
            {
                Log.d("Polling", "Source onSurveyAvailable triggered.");
            }
        };


        SdkPayload sdkPayload = new SdkPayload(this, requestIdentification, callbackHandler, false);

        Log.d("Polling", "Requesting Polling initialization.");
        this.polling = new Polling();
        this.polling.initialize(sdkPayload);
        Log.d("Polling", "Polling initialized.");


        this.polling.setViewType(ViewType.Bottom);

    }

    public void testShowSurvey(String uuid)
    {
        Log.d("Polling", "Calling showSurvey with uuid: " + uuid);
        polling.showSurvey(uuid, this);
    }

    public void testLogEvent()
    {
        Log.d("Polling", "Calling logEvent.");
        polling.logEvent("javaTest", "1");
    }

    public void testShowEmbed()
    {
        Log.d("Polling", "Calling logEvent.");
        polling.showEmbedView(this);
    }
}
