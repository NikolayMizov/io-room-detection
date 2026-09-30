package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain.ErrorResult;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain.FieldValidationError;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
final class ResponseBodyMatchers {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResponseBodyMatchers() {
        objectMapper.findAndRegisterModules();
    }

    public <T> ResultMatcher containsObjectAsJson(Object expectedObject, Class<T> targetClass) {
        return mvcResult -> {
            String json = mvcResult.getResponse().getContentAsString();
            T actualObject = objectMapper.readValue(json, targetClass);
            assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);
        };
    }

    public ResultMatcher containsError(String expectedFieldName, String expectedMessage) {
        return mvcResult -> {
            String json = mvcResult.getResponse().getContentAsString();
            ErrorResult errorResult = objectMapper.readValue(json, ErrorResult.class);
            List<FieldValidationError> fieldErrors = errorResult.getFieldErrors().stream()
                    .filter(fieldError -> fieldError.getField().equals(expectedFieldName))
                    .filter(fieldError -> fieldError.getMessage().equals(expectedMessage))
                    .collect(Collectors.toList());

            assertThat(fieldErrors)
                    .hasSize(1);
        };
    }

    public static ResponseBodyMatchers responseBody() {
        return new ResponseBodyMatchers();
    }
}
