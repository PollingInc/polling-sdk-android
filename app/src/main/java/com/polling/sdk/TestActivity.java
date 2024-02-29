package com.polling.sdk;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set a simple layout with a button
        setContentView(R.layout.activity_test);

        Button buttonAvailableSurveysDialog = findViewById(R.id.buttonAvailableSurveysDialog);
        Button buttonAvailableSurveysBottom = findViewById(R.id.buttonAvailableSurveysBottom);

        Button buttonSingleSurveyDialog = findViewById(R.id.buttonSingleSurveyDialog);
        Button buttonSingleSurveyBottom = findViewById(R.id.buttonSingleSurveyBottom);

        Button buttonCompletedSurveysDialog = findViewById(R.id.buttonCompletedSurveysDialog);
        Button buttonCompletedSurveysBottom = findViewById(R.id.buttonCompletedSurveysBottom);


        // Set click listener for Available Surveys
        buttonAvailableSurveysDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newDialog().availableSurveys();
            }
        });

        buttonAvailableSurveysBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewBottom newBottom = newBottom("https://demo.polling.com/sdk/available-surveys");
                newBottom.show(getSupportFragmentManager(), newBottom.getTag());
            }
        });




        // Set click listener for Single Survey
        buttonSingleSurveyDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newDialog().singleSurvey("3875c65f-1e7a-411f-b8c3-be2ce19a9c6e");
            }
        });

        // Set click listener for Completed Surveys
        buttonCompletedSurveysDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newDialog().completedSurveys();
            }
        });
    }

    private WebViewDialogHelper newDialog()
    {
        DialogRequest dialogRequest = new DialogRequest();
        dialogRequest.activity = TestActivity.this;

        int random = new Random().nextInt(1000);
        dialogRequest.customerId = String.valueOf(random);
        dialogRequest.apiKey = "cli_wZJW1tH39TfUMbEumPLrDy15EXDqJA0a";

        return new WebViewDialogHelper(dialogRequest);
    }

    private WebViewBottom newBottom(String url)
    {
        int random = new Random().nextInt(1000);
        String customerId = String.valueOf(random);
        String apiKey = "cli_wZJW1tH39TfUMbEumPLrDy15EXDqJA0a";

        return new WebViewBottom(url, customerId, apiKey);
    }
}
