package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "Reservation")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationEntity {
    @Id
    @Length(max = 152)
    @Column(name = "id")
    private String id;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "room_id")
    private MeetingRoomEntity meetingRoom;
    @NotNull
    @Min(0)
    @Column(name = "max_occupancy")
    private Integer maxOccupancy;
    @Length(max = 152)
    @Column(name = "series_master_id")
    private String seriesMasterId;
}
