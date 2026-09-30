package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.impl;

import lombok.AllArgsConstructor;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.GetCameraConnectionsUseCase;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.GetCameraConnectionsResponse;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain.CameraConnection;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.CameraConnectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GetCameraConnectionsUseCaseImpl implements GetCameraConnectionsUseCase {
    private final CameraConnectionRepository cameraConnectionRepository;

    @Override
    public GetCameraConnectionsResponse getCameraConnections() {
        List<CameraConnection> cameraConnections = this.cameraConnectionRepository.getUnassignedCameraConnections()
                .stream()
                .map(CameraConnectionConverter::convert)
                .toList();

        return GetCameraConnectionsResponse.builder()
                .cameraConnections(cameraConnections)
                .build();
    }
}
