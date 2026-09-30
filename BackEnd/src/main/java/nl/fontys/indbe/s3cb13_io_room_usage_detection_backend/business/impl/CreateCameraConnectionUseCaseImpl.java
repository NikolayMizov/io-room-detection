package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.CreateCameraConnectionUseCase;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.exception.CameraConnectionAlreadyExistsException;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.exception.InvalidCameraConnectionException;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.CreateCameraConnectionRequest;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.CreateCameraConnectionResponse;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.CameraConnectionRepository;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.MeetingRoomRepository;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.entity.CameraConnectionEntity;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.entity.MeetingRoomEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CreateCameraConnectionUseCaseImpl implements CreateCameraConnectionUseCase {

    private final CameraConnectionRepository cameraConnectionRepository;

    @Override
    public CreateCameraConnectionResponse createCameraConnection(CreateCameraConnectionRequest request) {

        if (cameraConnectionRepository.existsCameraConnectionEntityByMacAddress(request.getMacAddress())) {
            throw new CameraConnectionAlreadyExistsException("CAMERA_CONNECTION_ALREADY_EXISTS");
        }
            CameraConnectionEntity cameraConnectionEntity = saveNewCameraConnection(request);

            return CreateCameraConnectionResponse.builder()
                    .cameraId(cameraConnectionEntity.getId())
                    .build();
    }


    private CameraConnectionEntity saveNewCameraConnection(CreateCameraConnectionRequest request) {
        CameraConnectionEntity cameraConnectionEntity = CameraConnectionEntity.builder()
                .macAddress(request.getMacAddress())
                .build();

        return cameraConnectionRepository.save(cameraConnectionEntity);
    }
}
