package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.impl;

import lombok.AllArgsConstructor;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.GetAllRoomsUseCase;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.RoomRecommendationsUseCase;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.GetAllRoomsRequest;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.microsoftGraphApi.EmailClient;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain.MeetingRoom;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain.RoomEventStatus;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
@AllArgsConstructor
public class EventMeetingScheduler {

    private GetAllRoomsUseCase getAllRoomsUseCase;
    private RoomRecommendationsUseCase roomRecommendationsUseCase;
    private ReservationRepository reservationRepository;

    @Autowired
    private EmailClient emailClient;

    @Scheduled(cron = "0 * * * * *") // every minute
    private void checkMeetingEnd() {
        GetAllRoomsRequest request = GetAllRoomsRequest.builder().pageSize(100).pageIndex(0).build();
        List<MeetingRoom> rooms = getAllRoomsUseCase.getAllRooms(request).getRooms();

        for (MeetingRoom room : rooms) {
            if (room.getStatus() != RoomEventStatus.OCCUPIED_NOW){
                return;
            }
            if (room.getRoomEvent() == null){
                return;
            }

            if (isMeetingEnding(room.getRoomEvent().getEndTime())){
                if (reservationRepository.getReservationMaxOccupancyById(room.getRoomEvent().getId()) <= 0){
                    emailClient.sendWarningAbsence(room.getRoomEvent().getOrganizerEmail(), room.getRoomEvent().getOrganizerName(), null);
                }
                if (!isThresholdReached(room)){
                     emailClient.sendRoomRecommendations(room.getRoomEvent().getOrganizerEmail(), room.getRoomEvent().getOrganizerName(), null, room, roomRecommendationsUseCase.roomRecommendations(room).get());
                }
            }
        }
    }

    private boolean isMeetingEnding(LocalDateTime endTime) {
        return endTime.equals(LocalDateTime.now()) || (endTime.isAfter(LocalDateTime.now()) && endTime.isBefore(LocalDateTime.now().plusMinutes(1)));
    }

    private boolean isThresholdReached(MeetingRoom room) {
        int maxMeetingAttendees = reservationRepository.getReservationMaxOccupancyById(room.getRoomEvent().getId());
        int roomCapacity = room.getMaxCapacity();
        double attendeesPercentage = (double) maxMeetingAttendees /roomCapacity;
        double threshold = 0.6;

        return attendeesPercentage > threshold;
    }
}
