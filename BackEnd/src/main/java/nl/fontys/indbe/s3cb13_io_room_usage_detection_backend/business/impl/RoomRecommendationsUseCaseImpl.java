package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.impl;

import com.microsoft.graph.models.*;
import com.microsoft.graph.users.item.calendar.getschedule.GetSchedulePostResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.RoomRecommendationsUseCase;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.microsoftGraphApi.EventApi;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.microsoftGraphApi.RoomApi;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain.MeetingRoom;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.MeetingRoomRepository;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.ReservationRepository;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.entity.MeetingRoomEntity;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.entity.ReservationEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class RoomRecommendationsUseCaseImpl implements RoomRecommendationsUseCase {

    private final ReservationRepository reservationRepository;
    private final MeetingRoomRepository roomRepository;
    private final RoomApi roomApi;
    private final EventApi eventApi;

    private final double ATTENDANCE_THRESHOLD = 0.6;
    private final double THRESHOLD = 0.5;
    private final int MIN_MEETINGS = 2;


    @Override
    public Optional<List<MeetingRoomEntity>> roomRecommendations(MeetingRoom room) {

        ReservationEntity currentReservation = reservationRepository.findById(room.getRoomEvent().getId()).orElse(null);

        if (currentReservation != null && currentReservation.getSeriesMasterId() != null){

            List<ReservationEntity> series = reservationRepository.getAllBySeriesMasterId(currentReservation.getSeriesMasterId());

            if (series == null && series.size() >= MIN_MEETINGS ){
                return Optional.empty();
            }

            int roomThreshold = (int) (room.getMaxCapacity() * ATTENDANCE_THRESHOLD);
            int seriesThreshold = (int) (series.size() * THRESHOLD);

            int count = 0;
            int totSeriesOccupancy = 0;

            for (ReservationEntity reservation : series){
                if (reservation.getMaxOccupancy() < roomThreshold){
                    count++;
                    totSeriesOccupancy += reservation.getMaxOccupancy();
                }
            }

            int avrSeriesOccupancy = totSeriesOccupancy / count;

            if (count >= seriesThreshold){

                Event seriesMaster = eventApi.getSeriesMasterEvent(room.getEmail(), currentReservation.getSeriesMasterId());
                PatternedRecurrence info = seriesMaster.getRecurrence();

                RecurrencePattern pattern = info.getPattern();
                RecurrenceRange range = info.getRange();

                List<Room> rooms = roomApi.getRoomsWithCapacity(avrSeriesOccupancy);

                List<LocalDateTime[]> occurrences = generateOccurrences(
                        pattern.getType().value,
                        pattern.getDaysOfWeek(),
                        range.getStartDate(),
                        range.getEndDate(),
                        room.getRoomEvent().getStartTime().toLocalTime(),
                        room.getRoomEvent().getEndTime().toLocalTime()
                );

                List<MeetingRoomEntity> availableRooms = new ArrayList<>();


                for (Room r : rooms) {
                    if (r.getEmailAddress().equals(room.getEmail())) { break;}

                    if (isRoomAvailableForAllOccurrences(r.getEmailAddress(), occurrences)) {
                        availableRooms.add(roomRepository.getMeetingRoomEntityByEmail(r.getEmailAddress()));
                    }
                }
                return Optional.of(availableRooms);
            }
        }

        return Optional.empty();
    }

    private List<LocalDateTime[]> generateOccurrences(
            String recurrenceType,
            List<DayOfWeek> daysOfWeek,
            LocalDate startDate,
            LocalDate endDate,
            LocalTime startTime,
            LocalTime endTime
    ) {
        List<LocalDateTime[]> occurrences = new ArrayList<>();
        LocalDate currentDate = startDate;

        switch (recurrenceType.toLowerCase()) {
            case "daily":
                while (!currentDate.isAfter(endDate)) {
                    occurrences.add(new LocalDateTime[]{
                            LocalDateTime.of(currentDate, startTime),
                            LocalDateTime.of(currentDate, endTime)
                    });
                    currentDate = currentDate.plusDays(1);
                }
                break;

            case "weekly":
                while (!currentDate.isAfter(endDate)) {
                    if (daysOfWeek.contains(currentDate.getDayOfWeek())) {
                        occurrences.add(new LocalDateTime[]{
                                LocalDateTime.of(currentDate, startTime),
                                LocalDateTime.of(currentDate, endTime)
                        });
                    }
                    currentDate = currentDate.plusDays(1);
                }
                break;

            case "absolutemonthly":
                // Add occurrences on the same day of the month
                while (!currentDate.isAfter(endDate)) {
                    if (currentDate.getDayOfMonth() == startDate.getDayOfMonth()) {
                        occurrences.add(new LocalDateTime[]{
                                LocalDateTime.of(currentDate, startTime),
                                LocalDateTime.of(currentDate, endTime)
                        });
                    }
                    currentDate = currentDate.plusMonths(1);
                }
                break;

            case "relativemonthly":
                // Handle cases like "First Monday of the month"
                while (!currentDate.isAfter(endDate)) {
                    if (daysOfWeek.contains(currentDate.getDayOfWeek()) && isRelativeMatch(currentDate, startDate)) {
                        occurrences.add(new LocalDateTime[]{
                                LocalDateTime.of(currentDate, startTime),
                                LocalDateTime.of(currentDate, endTime)
                        });
                    }
                    currentDate = currentDate.plusDays(1);
                }
                break;

            default:
                throw new IllegalArgumentException("Unsupported recurrence type: " + recurrenceType);
        }

        return occurrences;
    }

    private boolean isRelativeMatch(LocalDate currentDate, LocalDate startDate) {
        int weekOfMonth = (currentDate.getDayOfMonth() - 1) / 7 + 1; // Calculate week of the month
        int startWeekOfMonth = (startDate.getDayOfMonth() - 1) / 7 + 1;
        return weekOfMonth == startWeekOfMonth;
    }

    private boolean isRoomAvailableForAllOccurrences(String roomEmail, List<LocalDateTime[]> occurrences) {
        for (LocalDateTime[] occurrence : occurrences) {

            GetSchedulePostResponse response = eventApi.getRoomAvailability(roomEmail, occurrence[0], occurrence[1]);

            if (response != null && response.getValue() != null && !response.getValue().isEmpty()) {
                ScheduleInformation schedule = response.getValue().get(0);
                return isAvailable(schedule, occurrence[0], occurrence[1]);
            }
            return false;
        }
        return true;
    }

    private boolean isAvailable(ScheduleInformation schedule, LocalDateTime startTime, LocalDateTime endTime) {
        String availability = schedule.getAvailabilityView();
        int startIndex = (int) Duration.between(startTime.toLocalDate().atStartOfDay(), startTime).toMinutes() / 30;
        int endIndex = (int) Duration.between(startTime.toLocalDate().atStartOfDay(), endTime).toMinutes() / 30;

        for (int i = startIndex; i < endIndex; i++) {
            if (availability.charAt(i) != '0') { // '0' means available
                return false;
            }
        }
        return true;
    }
}
