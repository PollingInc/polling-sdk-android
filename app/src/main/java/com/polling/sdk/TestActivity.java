package com.polling.sdk;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
        Button buttonAvailableSurveysApi = findViewById(R.id.buttonAvailableSurveysApi);

        Button buttonSingleSurveyDialog = findViewById(R.id.buttonSingleSurveyDialog);
        Button buttonSingleSurveyBottom = findViewById(R.id.buttonSingleSurveyBottom);
        Button buttonSingleSurveyApi = findViewById(R.id.buttonSingleSurveyApi);


        Button buttonCompletedSurveysApi = findViewById(R.id.buttonCompletedSurveysApi);


        //Available Surveys
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


        buttonAvailableSurveysApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Survey survey = newApi();

                TextView dataField = (TextView) findViewById(R.id.apiResponseData);
                survey.availableSurvey(
                        new WebRequestHandler.ResponseCallback() {
                            @Override
                            public void onResponse(String response) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateText(response);
                                    }
                                });

                            }

                            @Override
                            public void onError(String error) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateText(error);
                                    }
                                });
                            }
                        }
                );
            }
        });




        //Single Survey
        buttonSingleSurveyDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newDialog().singleSurvey("3875c65f-1e7a-411f-b8c3-be2ce19a9c6e");
            }
        });


        buttonSingleSurveyBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewBottom newBottom = newBottom("https://demo.polling.com/sdk/survey/3875c65f-1e7a-411f-b8c3-be2ce19a9c6e");
                newBottom.show(getSupportFragmentManager(), newBottom.getTag());
            }
        });


        buttonSingleSurveyApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Survey survey = newApi();

                TextView dataField = (TextView) findViewById(R.id.apiResponseData);
                survey.singleSurvey("3875c65f-1e7a-411f-b8c3-be2ce19a9c6e");
            }
        });




        //Completed Surveys
        buttonCompletedSurveysApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonCompletedSurveysApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Survey survey = newApi();

                TextView dataField = (TextView) findViewById(R.id.apiResponseData);
                survey.completedSurveys();
            }
        });

    }

    private WebViewDialogHelper newDialog()
    {

        Activity activity = TestActivity.this;

        int random = new Random().nextInt(1000);
        String customerId = "199";//String.valueOf(random);
        String apiKey = "cli_wZJW1tH39TfUMbEumPLrDy15EXDqJA0a";

        DialogRequest dialogRequest = new DialogRequest
        (
            activity,
            customerId,
            apiKey
        );


        return new WebViewDialogHelper(dialogRequest);
    }

    private WebViewBottom newBottom(String url)
    {
        int random = new Random().nextInt(1000);
        String customerId = "199";//String.valueOf(random);
        String apiKey = "cli_wZJW1tH39TfUMbEumPLrDy15EXDqJA0a";

        RequestIdentification requestIdentification = new RequestIdentification
        (
            customerId,
            apiKey
        );

        return new WebViewBottom(url, requestIdentification);
    }

    private Survey newApi()
    {
        int random = new Random().nextInt(1000);
        String customerId = "199";//String.valueOf(random);
        String apiKey = "cli_wZJW1tH39TfUMbEumPLrDy15EXDqJA0a";

        RequestIdentification requestIdentification = new RequestIdentification
        (
                customerId,
                apiKey
        );

        return new Survey(requestIdentification,


                new JavaCallbackHandler() {
                    @Override
                    public void onSuccess(String response) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateText(response);
                            }
                        });

                    }

                    @Override
                    public void onFailure(String error) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateText(error);
                            }
                        });
                    }
                }

                );
    }

    private void updateText(String text)
    {
        TextView dataField = (TextView) findViewById(R.id.apiResponseData);
        dataField.setText(text);
    }


}
