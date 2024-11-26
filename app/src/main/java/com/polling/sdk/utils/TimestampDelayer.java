package com.polling.sdk.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimestampDelayer
{
    public static String addMinutesToTimestamp(String timestamp, int minutesToAdd) {
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
            Date date = isoFormat.parse(timestamp);

            // Add minutes
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, minutesToAdd);

            // Return the updated timestamp in ISO 8601 format
            return isoFormat.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return timestamp; // Return the original timestamp if parsing fails
        }
    }
}
