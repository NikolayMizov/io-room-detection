package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.CreateCameraConnectionRequest;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.CreateCameraConnectionResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CamerasController.class)
class CamerasControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCameraReport_shouldCreateCameraReportAndReturn201Response_whenRequestIsValid() throws Exception {
        CreateCameraConnectionRequest request = CreateCameraConnectionRequest.builder()
                .macAddress("11:11:11:11:11:11")
                .nrOfOccupants(0)
                .build();
        CreateCameraConnectionResponse response = CreateCameraConnectionResponse.builder()
                .cameraId(1L)
                .build();

        mockMvc.perform(post("/cameras")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", APPLICATION_JSON_VALUE))
                .andExpect(ResponseBodyMatchers.responseBody().containsObjectAsJson(response, CreateCameraConnectionResponse.class));
    }

    @Test
    void createCameraReport_shouldNotCreateCameraReportAndReturn400Response_whenRequestIsInvalid() throws Exception {
        CreateCameraConnectionRequest request = CreateCameraConnectionRequest.builder()
                .macAddress("000000000000")
                .nrOfOccupants(-1)
                .build();

        mockMvc.perform(post("/cameras")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", APPLICATION_JSON_VALUE))
                .andExpect(ResponseBodyMatchers.responseBody().containsError("macAddress", "invalid format"))
                .andExpect(ResponseBodyMatchers.responseBody().containsError("nrOfOccupants", "must be greater than or equal to 0"));
    }
}