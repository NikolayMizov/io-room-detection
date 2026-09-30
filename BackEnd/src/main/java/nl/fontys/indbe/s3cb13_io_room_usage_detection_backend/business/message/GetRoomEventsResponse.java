package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain.RoomEvent;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetRoomEventsResponse {
    private List<RoomEvent> roomEvents;
}
