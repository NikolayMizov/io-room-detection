package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.impl;

import lombok.AllArgsConstructor;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.CreateRoomUseCase;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.exception.InvalidCameraConnectionException;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.CreateRoomRequest;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.CreateRoomResponse;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.CameraConnectionRepository;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.MeetingRoomRepository;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.entity.CameraConnectionEntity;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.entity.MeetingRoomEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CreateRoomUseCaseImpl implements CreateRoomUseCase {
    private final MeetingRoomRepository meetingRoomRepository;
    private final CameraConnectionRepository cameraConnectionRepository;

    @Override
    public CreateRoomResponse createRoom(CreateRoomRequest request) {
        MeetingRoomEntity savedMeetingRoom = saveNewMeetingRoom(request);

        return CreateRoomResponse.builder()
                .roomId(savedMeetingRoom.getId())
                .build();
    }

    private MeetingRoomEntity saveNewMeetingRoom(CreateRoomRequest request) {
        Optional<CameraConnectionEntity> cameraConnectionEntity = this.cameraConnectionRepository.findById(request.getCameraConnectionId());
        if (cameraConnectionEntity.isEmpty()) {
            throw new InvalidCameraConnectionException("CAMERA_CONNECTION_ID_INVALID");
        }

        MeetingRoomEntity newMeetingRoom = MeetingRoomEntity.builder()
                .email(request.getEmail())
                .currentCapacity(0)
                .cameraConnection(cameraConnectionEntity.get())
                .build();

        if (request.getId() > 0) {
            newMeetingRoom.setId(request.getId());
        }

        try {
            return this.meetingRoomRepository.save(newMeetingRoom);
        }
        catch (Exception e) {
            this.meetingRoomRepository.deleteById(request.getId());
           return  this.meetingRoomRepository.save(newMeetingRoom);
        }

//        return this.meetingRoomRepository.save(newMeetingRoom);
    }
}
