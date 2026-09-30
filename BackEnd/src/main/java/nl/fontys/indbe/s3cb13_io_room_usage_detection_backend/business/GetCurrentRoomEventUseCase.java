package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business;

import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain.RoomEvent;

public interface GetCurrentRoomEventUseCase {
    RoomEvent getCurrentRoomEvent(String roomEmail);
}
