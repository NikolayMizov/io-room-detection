package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.impl;

import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain.CameraConnection;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.entity.CameraConnectionEntity;

public final class CameraConnectionConverter {
    private CameraConnectionConverter() {
    }

    public static CameraConnection convert(CameraConnectionEntity cameraConnection) {
        return CameraConnection.builder()
                .id(cameraConnection.getId())
                .macAddress(cameraConnection.getMacAddress())
                .lastUpdated(cameraConnection.getLastUpdated())
                .build();
    }
}
