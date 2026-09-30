package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.impl;

import com.microsoft.graph.models.Event;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain.RoomEvent;

import java.time.LocalDateTime;

final class RoomEventConverter {
    private RoomEventConverter() {}

    public static RoomEvent convertRoomEvent(Event event) {
        final String TIMEZONE_SUFFIX_UTC = "Z";

        LocalDateTime startDateTime = DateTimeConverter.convertUtcToLocal(event.getStart().getDateTime() + TIMEZONE_SUFFIX_UTC, TIMEZONE_SUFFIX_UTC);
        LocalDateTime endDateTime = DateTimeConverter.convertUtcToLocal(event.getEnd().getDateTime() + TIMEZONE_SUFFIX_UTC, TIMEZONE_SUFFIX_UTC);

        return RoomEvent.builder()
                .id(event.getId())
                .roomEmail(event.getLocation().getLocationUri())
                .startTime(startDateTime)
                .endTime(endDateTime)
                .organizerName(event.getOrganizer().getEmailAddress().getName())
                .organizerEmail(event.getOrganizer().getEmailAddress().getAddress())
                .build();
    }
}
