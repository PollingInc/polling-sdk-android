package com.polling.sdk.api.models;

public class SurveyDetails {
    private String uuid;
    private String name;
    private Reward reward;
    private int question_count;
    private String user_survey_status;
    private String completed_at;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Reward getReward() {
        return reward;
    }

    public void setReward(Reward reward) {
        this.reward = reward;
    }

    public int getQuestionCount() {
        return question_count;
    }

    public void setQuestionCount(int question_count) {
        this.question_count = question_count;
    }

    public String getUserSurveyStatus() {
        return user_survey_status;
    }

    public void setUserSurveyStatus(String user_survey_status) {
        this.user_survey_status = user_survey_status;
    }

    public String getCompletedAt() {
        return completed_at;
    }

    public void setCompletedAt(String completed_at) {
        this.completed_at = completed_at;
    }
}
