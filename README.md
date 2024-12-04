# Polling.com Java/Android SDK

## Introduction

Polling.com Java SDK is a Java/Native Android library for interacting with Polling.com services by using an AAR package.

This SDK allows you to send events, log sessions and purchases, display embedded survey pages or show a specific survey seamlessly within your web application.

Polling SDK provides an easy way to integrate polling functionality into your Android projects. This guide walks you through integrating the `.aar` package and initializing the SDK.

---

## Installation

### Step 1: Add the `.aar` to your Project
1. Copy the `.aar` file into your project. It's recommended to place it in a dedicated `libs` folder (e.g., `app/libs`).
   
2. Update your `build.gradle` file to include the `libs` directory:
   ```gradle
   android {
       ...
       repositories {
           flatDir {
               dirs 'libs'
           }
       }
   }

   dependencies {
       implementation(name: 'polling-release', ext: 'aar')
   }
   ```

3. Sync your Gradle files with the project to make sure the .aar is correctly included.

### Step 2: Import the SDK and initialize
1. After integrating the .aar, you can import the Polling class into your project:
   ```import com.polling.sdk.Polling;```
2. Create required objects to compose a SdkPayload
   (There are some requirements in SdkPayload to initialize the SDK that may be different than [Polling JS SDK](https://github.com/PollingInc/polling-sdk-js)):

   ---
   #### 2.1 RequestIdentification
   Define a request identification by providing a:
   * **Customer ID:** this is what you use in your application to identify your user
   * **API Key:** found in Polling dashboard in Embeds

   Here's how it looks:
   ```
     RequestIdentification requestIdentification =
       new RequestIdentification(
         customerId,
         apiKey
       );
   ```
   ---
   #### 2.2 CallbackHandler
   The available callbacks are:
   * **onSuccess:** Triggered when a survey has completed and returns the survey in a JSON
   * **onFailure:** Triggered on errors and surveys failures. Useful to treat errors.
   * **onReward:** Triggered to reward the user after completing a survey. You ideally can write your code to deal with rewards here.
   * **onSurveyAvailable:** Triggered when there is(are) survey(s) available.
  
   Define the functions that will run as callbacks from the survey using overrides.
   
   Example of usage:
   ```
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
   ```
   ---
   #### 2.3 SdkPayload
   This is the requirement that puts everything together.
   The parameters are:
   * **Activity:** Android apps uses Activity. You should use that to target the application you want to render your webviews.
   * **RequestIdentification:** This is what we have created on step **2.1**
   * **CallbackHandler:** This is what we have created on step **2.2**
   * **Boolean:**  Default is false, but if true, it will disable the display of available surveys, so you would have to trigger them directly with **showSurvey(surveyUuid)**;

   And that's how we use it:
   ```
      SdkPayload sdkPayload = new SdkPayload(this.activity, requestIdentification, callbackHandler, false);
   ```
   ---
   #### 2.4 Initialize()
   Finally, we can initialize the SDK by doing this:
   ```
     Polling polling = new Polling();
     polling.initialize(sdkPayload);
   ```

   It's recommended to keep `polling` variable in a global scope of your class, so you can use it anywhere. Also, you can check if it initialized by checking if `polling.initiailized` is set to `true`.

   ---

   ### Step 3: Set the desired View type:
   For the Java SDK, we use a WebView to render the contents of the survey.
   With that, we create an overlay in your application that does not obstruct the view of your application.

   We have two different styles of overlay available by now:
   * **Dialog:** Opens like a centered popup in the middle of the screen as a square that keeps its edges free to view the background.
   * **Bottom:** Opens like a sheet in the bottom of the screen. Uses more screen and occupies full-width with no edges.
   They can be set in `polling.setViewType(ViewRype viewType)` OR `polling.setViewType(String viewTypeStr)`
   

   ### Step 4: Use the SDK:
   You can use it just as is in the JS docs in "Available Methods" section:
   https://docs.polling.com/integration/javascript-sdk

   Remember, if you set the last SdkPayload argument as false, some surveys may show automatically as intended and configured in Polling.com dashboard.
   Here's what you can do:
   `polling.logSession()` - Logs a simple Session event for the given user
   `polling.logPurchase(int integerCents)` - Logs a Purchase event for the given user with the amount in cents
   `polling.logEvent(String eventName, String | int eventValue)` - Sends a custom event name and value - NOTE: This method is only available for Business+ plans.
   `polling.showEmbedView()` - Opens the Polling.com embed view popup, which will show the user's surveys (list of surveys, random or a fixed survey depending on the user's settings)
   `polling.showSurvey(String surveyUuid)` - Opens a popup with a specific survey by its UUID
   `polling.setApiKey(String apiKey)` - Changes the API key on the fly, useful if you want to handle multiple embeds with a single SDK instance
   `polling.setCustomerId(String customerId)` - Changes the Customer ID on the fly
   `polling.setViewType(ViewType viewType | String viewTypeStr)` - Changes the View type mode on the fly
   
   ---

   ### Usage example

   Here's the full example of a Activity using Polling:
   ```
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
      
          Polling polling;
      
          @Override
          protected void onCreate(Bundle savedInstanceState) {
              super.onCreate(savedInstanceState);
      
      
              setContentView(R.layout.activity_test);
      
              initializePolling();
      
              Button buttonLogEvent = findViewById(R.id.buttonLogEvent);
              Button buttonEmbedSurvey = findViewById(R.id.buttonEmbedSurvey);
      
              //LOG EVENT
              buttonLogEvent.setOnClickListener(
                      new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      testLogEvent();
                  }
              });

              //SHOW EMBED
              buttonEmbedSurvey.setOnClickListener(
                      new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      testEmbedSurvey();
                  }
              });
      
          }
      
          public void initializePolling()
          {
              RequestIdentification requestIdentification =
                      new RequestIdentification(
                              "test-java-user",
                              "myApiKey0000...");
      
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
              this.polling = new Polling();
              this.polling.initialize(sdkPayload);
              Log.d("Polling", "Polling initialized.");
      
      
              this.polling.setViewType("Bottom");
      
          }
      
          public void testLogEvent()
          {
              Log.d("Polling", "Calling logEvent.");
              polling.logEvent("javaTest", "1");
          }

          public void testEmbedSurvey()
          {
              Log.d("Polling", "Calling showEmbedView.");
              polling.showEmbedView();
          }
   
      }

   ```
   
   

   
   
