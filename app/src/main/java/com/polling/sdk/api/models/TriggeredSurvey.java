package com.polling.sdk.api.models;

public class TriggeredSurvey
{
    private SurveyModel survey;
    private int delay_seconds;
    private String delayed_timestamp;

    // Getters and setters
    public SurveyModel getSurvey() {
        return survey;
    }

    public void setSurvey(SurveyModel survey) {
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
