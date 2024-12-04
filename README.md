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

   
