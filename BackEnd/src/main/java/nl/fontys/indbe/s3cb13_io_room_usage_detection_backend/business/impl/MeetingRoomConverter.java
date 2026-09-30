package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.impl;

import com.microsoft.graph.models.Room;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain.CameraConnection;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain.MeetingRoom;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain.RoomEvent;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain.RoomEventStatus;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.entity.MeetingRoomEntity;

final class MeetingRoomConverter {
    private MeetingRoomConverter() {

    }

    static MeetingRoom convertMeetingRoom(Room room, MeetingRoomEntity meetingRoomEntity, RoomEvent roomEvent, RoomEventStatus status) {
        // if Room is not yet saved in dbs
        if (meetingRoomEntity == null) {
            return MeetingRoom.builder()
                    .id(0L)
                    .email(room.getEmailAddress())
                    .name(room.getNickname())
                    .cameraConnection(null)
                    .maxCapacity(room.getCapacity() == null? 0 : room.getCapacity())
                    .currentCapacity(0)
                    .roomEvent(roomEvent)
                    .status(status)
                    .build();
        }

        return MeetingRoom.builder()
                .id(meetingRoomEntity.getId())
                .email(room.getEmailAddress())
                .name(room.getNickname())
                .cameraConnection(CameraConnection.builder()
                        .id(meetingRoomEntity.getCameraConnection().getId())
                        .macAddress(meetingRoomEntity.getCameraConnection().getMacAddress())
                        .build()
                )
                .maxCapacity(room.getCapacity() == null? 0 : room.getCapacity())
                .currentCapacity(meetingRoomEntity.getCurrentCapacity() == null? 0 : meetingRoomEntity.getCurrentCapacity())
                .roomEvent(roomEvent)
                .status(status)
                .build();
    }

}
