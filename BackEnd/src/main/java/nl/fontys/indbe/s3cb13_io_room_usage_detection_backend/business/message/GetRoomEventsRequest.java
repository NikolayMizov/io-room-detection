package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetRoomEventsRequest {
    private Long roomId;
    private String roomEmail;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
