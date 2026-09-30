package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository;

import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.entity.ReservationEntity;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, String> {
    @Query("select r.maxOccupancy from ReservationEntity r where r.id = :id")
    Integer getReservationMaxOccupancyById(@Param("id") String id);

    @Modifying
    @Query("update ReservationEntity r set r.maxOccupancy = :maxOccupancy where r.id = :id")
    void updateReservationMaxOccupancyById(@Param("id") String id, @Param("maxOccupancy") int maxOccupancy);

    List<ReservationEntity> getAllBySeriesMasterId(@Length(max = 152) String seriesMasterId);
}
