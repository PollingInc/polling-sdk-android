package com.polling.sdk.api.models;

public class SurveyModel
{
    private String survey_uuid;
    private String name;

    // Getters and setters
    public String getSurveyUuid() {
        return survey_uuid;
    }

    public void setSurveyUuid(String survey_uuid) {
        this.survey_uuid = survey_uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
