package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.impl;

import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain.RoomEvent;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain.RoomEventStatus;

import java.time.LocalDateTime;

final class GetRoomEventStatus {
    private GetRoomEventStatus() {}

    static RoomEventStatus getRoomEventStatus(RoomEvent roomEvent) {
        int OFFSET_FOR_OCCUPIED_SOON = 30;

        LocalDateTime now = LocalDateTime.now();

        if (roomEvent == null) {
            return RoomEventStatus.AVAILABLE;
        }

        LocalDateTime startTime = roomEvent.getStartTime();
        LocalDateTime endTime = roomEvent.getEndTime();

        if ((startTime.isBefore(now) || startTime.equals(now)) && endTime.isAfter(now)) {
            return RoomEventStatus.OCCUPIED_NOW;
        }
        LocalDateTime offset = now.plusMinutes(OFFSET_FOR_OCCUPIED_SOON);
        if (startTime.isAfter(now) && (offset.isAfter(startTime) || offset.equals(startTime))) {
            return RoomEventStatus.OCCUPIED_SOON;
        }
        return RoomEventStatus.AVAILABLE;
    }
}
