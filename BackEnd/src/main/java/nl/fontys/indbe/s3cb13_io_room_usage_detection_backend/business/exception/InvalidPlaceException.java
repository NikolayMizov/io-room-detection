package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.exception;

public class InvalidPlaceException extends RuntimeException {
    public InvalidPlaceException(String message) {
        super(message);
    }
}
