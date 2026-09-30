package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidCameraConnectionException extends ResponseStatusException {
    public InvalidCameraConnectionException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
