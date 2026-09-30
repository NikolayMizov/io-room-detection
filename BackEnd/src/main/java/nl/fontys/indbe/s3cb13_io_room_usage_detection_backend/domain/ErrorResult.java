package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public final class ErrorResult {
    private final List<FieldValidationError> fieldErrors = new ArrayList<>();
}
