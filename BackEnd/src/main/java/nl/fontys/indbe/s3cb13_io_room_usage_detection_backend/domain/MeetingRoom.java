package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingRoom {
    private Long id;
    private String email;
    private String name;
    private int maxCapacity;
    private CameraConnection cameraConnection;
    private int currentCapacity;
    private RoomEventStatus status;
    private RoomEvent roomEvent;
}
