package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.controller;

import lombok.AllArgsConstructor;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.exception.InvalidRoomIdException;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.impl.GetRoomEventsUseCaseImpl;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.GetRoomEventsRequest;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.GetRoomEventsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/events")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class EventsController {

    private static final Logger logger = LoggerFactory.getLogger(EventsController.class);
    private GetRoomEventsUseCaseImpl getRoomEventsUseCase;

    @GetMapping("{roomId}")
    public ResponseEntity<GetRoomEventsResponse> getRoomEvents(@PathVariable long roomId,
                                                               @RequestParam(value = "startDate", required = false) String startDate,
                                                               @RequestParam(value = "endDate", required = false) String endDate) {
        GetRoomEventsRequest request;

        if (startDate != null && endDate != null) {
            request = GetRoomEventsRequest.builder()
                    .roomId(roomId)
                    .startTime(LocalDate.parse(startDate).atStartOfDay())
                    .endTime(LocalDate.parse(endDate).atTime(LocalTime.MAX))
                    .build();
        } else if (startDate != null) {
            LocalDateTime startTime = LocalDate.parse(startDate).atStartOfDay();
            LocalDateTime endTime = LocalDate.parse(startDate).atTime(LocalTime.MAX);
            request = GetRoomEventsRequest.builder()
                    .roomId(roomId)
                    .startTime(startTime)
                    .endTime(endTime)
                    .build();
        } else {
            request = GetRoomEventsRequest.builder()
                    .roomId(roomId)
                    .startTime(LocalDate.now().atStartOfDay())
                    .endTime(LocalTime.MAX.atDate(LocalDate.now()))
                    .build();
        }

        GetRoomEventsResponse response = getRoomEventsUseCase.getRoomEvents(request);
        if (response == null || response.getRoomEvents().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        logger.info("Fetched room events for roomId {}: {}", roomId, response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/email/{roomEmail}")
    public ResponseEntity<GetRoomEventsResponse> getRoomEvents(@PathVariable String roomEmail,
                                                               @RequestParam(value = "startDate", required = false) String startDate,
                                                               @RequestParam(value = "endDate", required = false) String endDate) {
        GetRoomEventsRequest request;

        if (startDate != null && endDate != null) {
            request = GetRoomEventsRequest.builder()
                    .roomEmail(roomEmail)
                    .startTime(LocalDate.parse(startDate).atStartOfDay())
                    .endTime(LocalDate.parse(endDate).atTime(LocalTime.MAX))
                    .build();
        } else if (startDate != null) {
            LocalDateTime startTime = LocalDate.parse(startDate).atStartOfDay();
            LocalDateTime endTime = LocalDate.parse(startDate).atTime(LocalTime.MAX);
            request = GetRoomEventsRequest.builder()
                    .roomEmail(roomEmail)
                    .startTime(startTime)
                    .endTime(endTime)
                    .build();
        } else {
            request = GetRoomEventsRequest.builder()
                    .roomEmail(roomEmail)
                    .startTime(LocalDate.now().atStartOfDay())
                    .endTime(LocalTime.MAX.atDate(LocalDate.now()))
                    .build();
        }

        GetRoomEventsResponse response = getRoomEventsUseCase.getRoomEvents(request);
        if (response == null || response.getRoomEvents().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        logger.info("Fetched room events for roomEmail {}: {}", roomEmail, response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ExceptionHandler(InvalidRoomIdException.class)
    @ResponseBody
    public ResponseEntity<Object> handleInvalidRoomIdException(InvalidRoomIdException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
