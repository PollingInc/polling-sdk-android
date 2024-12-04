package com.polling.sdk.api.models;

public class TriggeredSurvey
{
    private SurveyShort survey;
    private int delay_seconds;
    private String delayed_timestamp;

    public boolean isInUse;


    // Getters and setters
    public SurveyShort getSurvey() {
        return survey;
    }

    public void setSurvey(SurveyShort survey) {
        this.survey = survey;
    }

    public int getDelaySeconds() {
        return delay_seconds;
    }

    public void setDelaySeconds(int delay_seconds) {
        this.delay_seconds = delay_seconds;
    }

    public String getDelayedTimestamp() {
        return delayed_timestamp;
    }

    public void setDelayedTimestamp(String delayed_timestamp) {
        this.delayed_timestamp = delayed_timestamp;
    }

}
