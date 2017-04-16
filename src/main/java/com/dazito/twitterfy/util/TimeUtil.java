package com.dazito.twitterfy.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Created by daz on 15/04/2017.
 */
public final class TimeUtil {

    private TimeUtil() {}

    public static long currentTimestampUtc() {
        return ZonedDateTime
                .now(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli();
    }

    public static String parseTimestampToDateText(long timestamp) {
        ZonedDateTime dateTime = Instant
                .ofEpochMilli(timestamp)
                .atZone(ZoneId.of("UTC"));

        final String month = dateTime.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        final String dayOfWeek = dateTime
                .getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.US);
        final int dayOfMonth = dateTime.getDayOfMonth();

        final String hour = getFormattedHour(dateTime);
        final String minute = getFormattedMinute(dateTime);

        return new StringBuilder()
                .append(dayOfWeek)
                .append(", ")
                .append(dayOfMonth)
                .append(" ")
                .append(month)
                .append(" - ")
                .append(hour)
                .append(":")
                .append(minute)
                .append("\t")
                .toString();
    }

    public static String getFormattedHour(ZonedDateTime dateTime) {
        if(dateTime.getHour() < 10) {
            return "0" + dateTime.getHour();
        }

        return String.valueOf(dateTime.getHour());
    }

    public static String getFormattedMinute(ZonedDateTime dateTime) {
        if(dateTime.getMinute() < 10) {
            return "0" + dateTime.getMinute();
        }

        return String.valueOf(dateTime.getMinute());
    }
}
