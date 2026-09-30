package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Camera_Connection")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CameraConnectionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @OneToOne
    @PrimaryKeyJoinColumn(name="id", referencedColumnName="id")
    private MeetingRoomEntity meetingRoom;
    @NotBlank
    @Pattern(regexp = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})|([0-9a-fA-F]{4}\\.[0-9a-fA-F]{4}\\.[0-9a-fA-F]{4})$", message = "invalid format")
    @Column(name = "mac_address")
    private String macAddress;
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}
