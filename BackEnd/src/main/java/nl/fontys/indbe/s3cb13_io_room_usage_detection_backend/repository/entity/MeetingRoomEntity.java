package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "Meeting_Room")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetingRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @NotBlank
    @Length(max = 100)
    @Column(name = "email")
    private String email;
    @NotNull
    @Min(0)
    @Column(name = "current_capacity")
    private Integer currentCapacity;
    @OneToOne
    @JoinColumn(name = "camera_connection_id")
    private CameraConnectionEntity cameraConnection;
}
