package com.polling.sdk.api.models;

public class SurveyShort
{
    private String survey_uuid;
    private String name;


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
