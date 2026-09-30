package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CameraConnection {
    private Long id;
    private String macAddress;
    private LocalDateTime lastUpdated;
}
