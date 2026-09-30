package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.exception;

public class CameraConnectionAlreadyExistsException extends RuntimeException {
    public CameraConnectionAlreadyExistsException(String message) {
        super(message);
    }
}
