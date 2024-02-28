package com.polling.sdk;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Random;

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set a simple layout with a button
        setContentView(R.layout.activity_test);

        Button testButton = findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                newDialog().availableSurveys();
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
}
