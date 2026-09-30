package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business;

import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.GetCameraConnectionsResponse;

public interface GetCameraConnectionsUseCase {
    GetCameraConnectionsResponse getCameraConnections();
}
