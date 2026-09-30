package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.microsoftGraphApi;

import com.microsoft.graph.models.*;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.graph.users.item.calendar.getschedule.GetSchedulePostRequestBody;
import com.microsoft.graph.users.item.calendar.getschedule.GetSchedulePostResponse;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.impl.DateTimeConverter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
public class EventApi {
    private final GraphServiceClient graphServiceClient;

    public EventApi (GetGraphServiceClient getGraphClient) {
        graphServiceClient = getGraphClient.getGraphServiceClient();
    }

    public Event getCurrentRoomEvent(String roomEmail) {
        String nowUtc = DateTimeConverter.getCurrentUtcDateTimeAsString();

        String ongoingOrNextEventFilter = String.format(
                "(start/dateTime le '%s' and end/dateTime ge '%s') or start/dateTime ge '%s'",
                nowUtc, nowUtc, nowUtc
        );

        EventCollectionResponse result = graphServiceClient.users()
                .byUserId(roomEmail)
                .calendarView()
                .get(RequestConfiguration -> {
                    RequestConfiguration.queryParameters.startDateTime = nowUtc;
                    RequestConfiguration.queryParameters.endDateTime = nowUtc;
                    RequestConfiguration.queryParameters.filter = ongoingOrNextEventFilter;
                    RequestConfiguration.queryParameters.orderby = new String[]{"start/dateTime asc"};
                    RequestConfiguration.queryParameters.top = 1;
                });

        if (result.getValue() == null) {
            return null;
        }

        return result.getValue().get(0);
    }

    public List<Event> getRoomEvents(String roomEmail, LocalDateTime startDate, LocalDateTime endDate) {
        final String TIMEZONE_SUFFIX_UTC = "Z";

        EventCollectionResponse result = graphServiceClient.users()
                .byUserId(roomEmail)
                .calendar()
                .calendarView()
                .get(RequestConfiguration -> {
                    RequestConfiguration.queryParameters.startDateTime = DateTimeConverter.formatLocalDateTimeForApi(startDate, TIMEZONE_SUFFIX_UTC);
                    RequestConfiguration.queryParameters.endDateTime = DateTimeConverter.formatLocalDateTimeForApi(endDate.truncatedTo(ChronoUnit.SECONDS), TIMEZONE_SUFFIX_UTC);
                    RequestConfiguration.queryParameters.orderby = new String[]{"start/dateTime asc"};
                });

        if (result.getValue() == null) {
            return null;
        }

        return result.getValue();
    }

    public Event getSeriesMasterEvent(String roomEmail, String seriesMasterId){
        return graphServiceClient.users()
                .byUserId(roomEmail)
                .calendar()
                .events()
                .byEventId(seriesMasterId)
                .get();
    }

    public Event getCurrentRoomEvent(String roomEmail, String roomEventId) {
        String nowUtc = DateTimeConverter.getCurrentUtcDateTimeAsString();


        Event result = graphServiceClient.users()
                .byUserId(roomEmail)
                .calendar()
                .events()
                .byEventId(roomEventId)
                .get(RequestConfiguration -> {
                    RequestConfiguration.queryParameters.select = new String[]{"seriesMasterId"};
                });

        if (result == null) {
            return null;
        }

        return result;
    }

    public GetSchedulePostResponse getRoomAvailability(String roomEmail, LocalDateTime startDate, LocalDateTime endDate) {

        // Convert LocalDateTime to DateTimeTimeZone
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        DateTimeTimeZone startTime = new DateTimeTimeZone();
        startTime.setDateTime(startDate.format(formatter));
        startTime.setTimeZone("UTC");

        DateTimeTimeZone endTime = new DateTimeTimeZone();
        endTime.setDateTime(endDate.format(formatter));
        endTime.setTimeZone("UTC");

        // Prepare request body
        GetSchedulePostRequestBody body = new GetSchedulePostRequestBody();
        body.setSchedules(Collections.singletonList(roomEmail));
        body.setStartTime(startTime);
        body.setEndTime(endTime);
        body.setAvailabilityViewInterval(15);

        // Execute the request for a specific user/room
        GetSchedulePostResponse schedules = graphServiceClient
                .users()
                .byUserId(roomEmail)
                .calendar()
                .getSchedule()
                .post(body);

        System.out.println(schedules.getValue());
        System.out.println(schedules.getValue().size());

        return schedules;
    }

}
