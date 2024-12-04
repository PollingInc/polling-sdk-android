package com.polling.sdk.api.parsers;

import com.google.gson.Gson;
import com.polling.sdk.api.models.SurveyResponse;
import com.polling.sdk.api.models.TriggeredSurvey;
import com.polling.sdk.utils.TimestampDelayer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class SurveyParser {
    public static SurveyResponse parseSurveyResponse(String jsonResponse) {
        Gson gson = new Gson();
        SurveyResponse survey =  gson.fromJson(jsonResponse, SurveyResponse.class);

        List<TriggeredSurvey> triggeredSurveys = survey.getTriggeredSurveys();

        if (triggeredSurveys == null) return survey;

        //This snippet below is to keep time delay precise, even if local time has any unexpected offset
        //With this code, existing stored survey needs to remain instead of getting replaced.
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        for(var t : triggeredSurveys)
        {
            long nowLong = System.currentTimeMillis();

            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(nowLong);

            if (t.getDelaySeconds() > 0) {
                calendar.add(Calendar.SECOND, t.getDelaySeconds());
                String delayedTimestamp = formatter.format(calendar.getTime());

                delayedTimestamp = delayedTimestamp.substring(0, 22) + ":" + delayedTimestamp.substring(22);
                t.setDelayedTimestamp(delayedTimestamp);
            }

        }

        return survey;
    }
}
