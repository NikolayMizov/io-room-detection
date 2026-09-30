package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.exception;

public class InvalidRoomIdException extends RuntimeException {
    public InvalidRoomIdException(String message) {
        super(message);
    }
}
