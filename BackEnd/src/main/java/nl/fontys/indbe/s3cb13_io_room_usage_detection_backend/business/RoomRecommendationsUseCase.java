package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business;


import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain.MeetingRoom;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.entity.MeetingRoomEntity;

import java.util.List;
import java.util.Optional;

public interface RoomRecommendationsUseCase {
    Optional<List<MeetingRoomEntity>> roomRecommendations (MeetingRoom room);
}
