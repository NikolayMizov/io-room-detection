package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class DateTimeConverter {
    private DateTimeConverter() {}

    // Convert a UTC datetime string to LocalDateTime based on provided timezone
    public static LocalDateTime convertUtcToLocal(String utcDateTime, String targetTimeZone) {
        ZonedDateTime utcZonedDateTime = ZonedDateTime.parse(utcDateTime);
        ZonedDateTime targetZonedDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.of(targetTimeZone));
        return targetZonedDateTime.toLocalDateTime();
    }

    // Convert the current local time to UTC for filtering
    public static String getCurrentUtcDateTimeAsString() {
        ZonedDateTime utcNow = ZonedDateTime.now(ZoneId.of("UTC"));
        return utcNow.format(DateTimeFormatter.ISO_INSTANT);
    }

    // Convert a LocalDateTime to a formatted string for API filtering
    public static String formatLocalDateTimeForApi(LocalDateTime localDateTime, String timeZone) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of(timeZone));
        return zonedDateTime.format(DateTimeFormatter.ISO_INSTANT);
    }

}
