package cz.cvut.fel.swa.rengars.notifications.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

    public static String toISOString(Date date) {
        final var tz = TimeZone.getTimeZone("UTC");
        final var df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        return df.format(date);
    }

    public static Date fromISOString(String date) {
        try {
            final var tz = TimeZone.getTimeZone("UTC");
            final var df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
            df.setTimeZone(tz);
            return df.parse(date);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }
}
