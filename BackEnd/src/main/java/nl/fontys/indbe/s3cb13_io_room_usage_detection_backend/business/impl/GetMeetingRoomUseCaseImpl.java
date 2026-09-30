package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.impl;

import com.microsoft.graph.models.Room;
import lombok.AllArgsConstructor;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.GetCurrentRoomEventUseCase;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.GetMeetingRoomUseCase;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.exception.InvalidRoomIdException;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.GetMeetingRoomResponse;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.microsoftGraphApi.RoomApi;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain.MeetingRoom;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain.RoomEvent;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain.RoomEventStatus;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.MeetingRoomRepository;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.entity.MeetingRoomEntity;
import org.springframework.stereotype.Service;

import static nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.impl.GetRoomEventStatus.getRoomEventStatus;

@Service
@AllArgsConstructor
public class GetMeetingRoomUseCaseImpl implements GetMeetingRoomUseCase {

    private final MeetingRoomRepository meetingRoomRepository;
    private final RoomApi roomApi;
    private final GetCurrentRoomEventUseCase getCurrentRoomEventUseCase;


    @Override
    public GetMeetingRoomResponse getMeetingRoom(long roomId) {
        MeetingRoomEntity meetingRoomEntity = meetingRoomRepository.findById(roomId)
                .orElseThrow(() -> new InvalidRoomIdException("Invalid Room Id"));
        return buildMeetingRoomResponse(meetingRoomEntity);
    }

    @Override
    public GetMeetingRoomResponse getMeetingRoom(String roomEmail) {
        MeetingRoomEntity meetingRoomEntity = meetingRoomRepository.getMeetingRoomEntityByEmail(roomEmail);

        if (meetingRoomEntity == null) {
            return buildMeetingRoomResponse(roomEmail);
        }
        return buildMeetingRoomResponse(meetingRoomEntity);
    }

    private GetMeetingRoomResponse buildMeetingRoomResponse(MeetingRoomEntity meetingRoomEntity) {
        Room room = getRoomByEmail(meetingRoomEntity.getEmail());
        RoomEvent roomEvent = getCurrentRoomEvent(room.getEmailAddress());
        RoomEventStatus status = getRoomEventStatus(roomEvent);

        MeetingRoom meetingRoom = MeetingRoomConverter.convertMeetingRoom(room, meetingRoomEntity, roomEvent, status);
        return GetMeetingRoomResponse.builder().meetingRoom(meetingRoom).build();
    }

    private GetMeetingRoomResponse buildMeetingRoomResponse(String email) {
        Room room = getRoomByEmail(email);
        RoomEvent roomEvent = getCurrentRoomEvent(room.getEmailAddress());
        RoomEventStatus status = getRoomEventStatus(roomEvent);

        // Null for MeetingRoomEntity
        MeetingRoom meetingRoom = MeetingRoomConverter.convertMeetingRoom(room, null, roomEvent, status);
        return GetMeetingRoomResponse.builder().meetingRoom(meetingRoom).build();
    }

    private Room getRoomByEmail(String email) {
        return roomApi.getRoom(email);
    }

    private RoomEvent getCurrentRoomEvent(String email) {
        try {
            return getCurrentRoomEventUseCase.getCurrentRoomEvent(email);
        } catch (Exception e) {
            System.err.println("Error fetching room event for email: " + email + " - " + e.getMessage());
            return null; // Return null -> error
        }
    }
}
