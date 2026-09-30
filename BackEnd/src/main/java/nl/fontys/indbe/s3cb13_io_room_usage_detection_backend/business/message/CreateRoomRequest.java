package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoomRequest {
    @Min(0)
    private long id;
    @NotBlank
    @Length(max = 100)
    private String email;
    @Min(1)
    private long cameraConnectionId;
}
