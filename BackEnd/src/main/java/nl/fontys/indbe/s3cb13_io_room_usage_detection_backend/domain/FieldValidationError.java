package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class FieldValidationError {
    private String field;
    private String message;
}
