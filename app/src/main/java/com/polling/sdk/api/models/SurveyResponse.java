package com.polling.sdk.api.models;

import java.util.List;

public class SurveyResponse
{
    private String message;
    private List<TriggeredSurvey> triggered_surveys;

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<TriggeredSurvey> getTriggeredSurveys() {
        return triggered_surveys;
    }

    public void setTriggeredSurveys(List<TriggeredSurvey> triggered_surveys) {
        this.triggered_surveys = triggered_surveys;
    }
}
