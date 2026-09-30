package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.impl;

import com.microsoft.graph.models.Event;
import lombok.AllArgsConstructor;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.GetRoomEventsUseCase;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.exception.InvalidRoomIdException;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.GetRoomEventsRequest;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.GetRoomEventsResponse;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.microsoftGraphApi.EventApi;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain.RoomEvent;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.MeetingRoomRepository;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.ReservationRepository;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.entity.MeetingRoomEntity;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.entity.ReservationEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GetRoomEventsUseCaseImpl implements GetRoomEventsUseCase {

    private final MeetingRoomRepository meetingRoomRepository;
    private final EventApi eventApi;
    private final ReservationRepository reservationRepository;

    @Override
    public GetRoomEventsResponse getRoomEvents(GetRoomEventsRequest request) {
        String email = getEmailFromRequest(request);

        List<Event> events = getRoomEventsApi(email, request.getStartTime(), request.getEndTime());

        List<RoomEvent> roomEvents = events.stream()
                .map(RoomEventConverter::convertRoomEvent)
                .toList();

        List<RoomEvent> roomEventsList = getRoomEventsDatabase(roomEvents);

        return GetRoomEventsResponse.builder().roomEvents(roomEventsList).build();
    }

    private String getEmailFromRequest(GetRoomEventsRequest request) {
        if (request.getRoomEmail() != null) {
            return request.getRoomEmail();
        }
        MeetingRoomEntity meetingRoomEntity = meetingRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new InvalidRoomIdException("Invalid room ID: " + request.getRoomId()));
        return meetingRoomEntity.getEmail();
    }

    private List<Event> getRoomEventsApi(String email, LocalDateTime startTime, LocalDateTime endTime) {
        return eventApi.getRoomEvents(email, startTime, endTime);
    }

    private List<RoomEvent> getRoomEventsDatabase(List<RoomEvent> roomEvents) {
        List<RoomEvent> roomEventsList = new ArrayList<>();
        for (RoomEvent roomEvent : roomEvents) {
            Optional<ReservationEntity> reservationEntity = reservationRepository.findById(roomEvent.getId());
            if (reservationEntity.isPresent()) {
                roomEvent.setMaxOccupancy(reservationEntity.get().getMaxOccupancy());
            }

            roomEventsList.add(roomEvent);
        }

        return roomEventsList;
    }
}
