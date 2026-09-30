package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository;

import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.entity.CameraConnectionEntity;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.entity.MeetingRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetingRoomRepository extends JpaRepository<MeetingRoomEntity, Long> {
    MeetingRoomEntity getMeetingRoomEntityByEmail(String emailAddress);

    @Modifying
    @Query("UPDATE MeetingRoomEntity m SET m.currentCapacity = :currentCapacity WHERE m.id = :meetingRoomId")
    void updateCurrentCapacityByMeetingRoomId(@Param("meetingRoomId") Long meetingRoomId, @Param("currentCapacity") int currentCapacity);

    @Query("SELECT count(m) > 0 FROM MeetingRoomEntity m WHERE m.cameraConnection.macAddress = :macAddress")
    boolean isMacAddressAssignedToMeetingRoom(@Param("macAddress") String macAddress);

    MeetingRoomEntity findMeetingRoomEntitiesByCameraConnection(CameraConnectionEntity cameraConnection);
}
