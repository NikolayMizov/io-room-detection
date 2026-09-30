package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business;

import com.microsoft.graph.models.*;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.impl.RoomRecommendationsUseCaseImpl;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.microsoftGraphApi.EventApi;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.microsoftGraphApi.RoomApi;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain.MeetingRoom;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain.RoomEvent;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.MeetingRoomRepository;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.ReservationRepository;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.entity.MeetingRoomEntity;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.entity.ReservationEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoomRecommendationsUseCaseImplTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private MeetingRoomRepository meetingRoomRepository;
    @Mock
    private RoomApi roomApi;
    @Mock
    private EventApi eventApi;

    @InjectMocks
    private RoomRecommendationsUseCaseImpl roomRecommendationsUseCase;


    @Test
    void shouldRecommendOtherAvailableRooms() {
        // Arrange
        ReservationEntity currentReservation = ReservationEntity.builder().id("111:111").seriesMasterId("1").maxOccupancy(5).build();
        ReservationEntity reservation2 = ReservationEntity.builder().id("222:222").seriesMasterId("1").maxOccupancy(4).build();
        ReservationEntity reservation3 = ReservationEntity.builder().id("333:333").seriesMasterId("1").maxOccupancy(5).build();
        ReservationEntity reservation4 = ReservationEntity.builder().id("444:444").seriesMasterId("1").maxOccupancy(5).build();
        List<ReservationEntity> reservations = List.of(currentReservation, reservation2, reservation3, reservation4);

        MeetingRoom currentRoom = MeetingRoom.builder()
                .roomEvent(RoomEvent.builder()
                        .id("1")
                        .roomEmail("currentRoom")
                        .startTime(LocalDateTime.of(2025,1,11,12,0))
                        .endTime(LocalDateTime.of(2025,1,11,12,30))
                        .build())
                .maxCapacity(10)
                .build();

        Room room1 = new Room();
        room1.setEmailAddress("room1Email");
        room1.setCapacity(5);

        Room room2 = new Room();
        room1.setEmailAddress("room1Email");
        room1.setCapacity(5);

        Room room3 = new Room();
        room1.setEmailAddress("room1Email");
        room1.setCapacity(5);

        // Act


        // Assert

    }

    @Test
    void shouldRecommendAvailableRooms() {
        // Arrange
        String roomEmail = "room@example.com";
        MeetingRoom meetingRoom = new MeetingRoom();
        RoomEvent roomEvent = new RoomEvent();
        roomEvent.setStartTime(LocalDateTime.of(2024, 1, 1, 9, 0));
        roomEvent.setEndTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        roomEvent.setId("res123");
        meetingRoom.setRoomEvent(roomEvent);
        meetingRoom.setEmail(roomEmail);
        meetingRoom.setMaxCapacity(10);

        ReservationEntity currentReservation = new ReservationEntity();
        currentReservation.setSeriesMasterId("series123");
        currentReservation.setMaxOccupancy(4);

        List<ReservationEntity> seriesReservations = new ArrayList<>();
        ReservationEntity reservation1 = new ReservationEntity();
        reservation1.setMaxOccupancy(4);
        ReservationEntity reservation2 = new ReservationEntity();
        reservation2.setMaxOccupancy(3);
        ReservationEntity reservation3 = new ReservationEntity();
        reservation3.setMaxOccupancy(5);
        ReservationEntity reservation4 = new ReservationEntity();
        reservation4.setMaxOccupancy(4);
        seriesReservations.add(reservation1);
        seriesReservations.add(reservation2);
        seriesReservations.add(reservation3);
        seriesReservations.add(reservation4);

        Event seriesMasterEvent = new Event();
        PatternedRecurrence recurrence = new PatternedRecurrence();
        RecurrencePattern pattern = new RecurrencePattern();
        pattern.setType(RecurrencePatternType.Daily);
        recurrence.setPattern(pattern);
        RecurrenceRange range = new RecurrenceRange();
        range.setStartDate(LocalDate.of(2024, 1, 1));
        range.setEndDate(LocalDate.of(2024, 1, 31));
        recurrence.setRange(range);
        seriesMasterEvent.setRecurrence(recurrence);

        Room room1 = new Room();
        room1.setEmailAddress("room1@example.com");
        Room room2 = new Room();
        room2.setEmailAddress("room2@example.com");
        List<Room> rooms = List.of(room1, room2);


        when(reservationRepository.findById("res123")).thenReturn(Optional.of(currentReservation));
        when(reservationRepository.getAllBySeriesMasterId("series123")).thenReturn(seriesReservations);
        when(eventApi.getSeriesMasterEvent(roomEmail, "series123")).thenReturn(seriesMasterEvent);
        when(roomApi.getRoomsWithCapacity(4)).thenReturn(rooms);
        when(meetingRoomRepository.getMeetingRoomEntityByEmail("room1@example.com")).thenReturn(new MeetingRoomEntity());
        when(meetingRoomRepository.getMeetingRoomEntityByEmail("room2@example.com")).thenReturn(new MeetingRoomEntity());

        when(eventApi.getRoomAvailability(anyString(), any(), any())).thenReturn(null); // Simplified for this example

        // Act
        Optional<List<MeetingRoomEntity>> recommendations = roomRecommendationsUseCase.roomRecommendations(meetingRoom);

        // Assert
        assertEquals(2, recommendations.orElseThrow().size());
        verify(reservationRepository).findById("res123");
        verify(reservationRepository).getAllBySeriesMasterId("series123");
        verify(eventApi).getSeriesMasterEvent(roomEmail, "series123");
        verify(roomApi).getRoomsWithCapacity(4);
        verify(meetingRoomRepository).getMeetingRoomEntityByEmail("room1@example.com");
        verify(meetingRoomRepository).getMeetingRoomEntityByEmail("room2@example.com");
    }
}
