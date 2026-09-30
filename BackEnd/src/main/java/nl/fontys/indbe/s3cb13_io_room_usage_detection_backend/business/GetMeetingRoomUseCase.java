package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business;

import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.GetMeetingRoomResponse;

public interface GetMeetingRoomUseCase {
    GetMeetingRoomResponse getMeetingRoom(long roomId);
    GetMeetingRoomResponse getMeetingRoom(String roomEmail);
}
