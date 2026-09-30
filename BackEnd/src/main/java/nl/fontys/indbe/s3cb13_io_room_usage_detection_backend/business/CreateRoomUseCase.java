package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business;

import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.CreateRoomRequest;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.CreateRoomResponse;

public interface CreateRoomUseCase {
    CreateRoomResponse createRoom(CreateRoomRequest request);
}
