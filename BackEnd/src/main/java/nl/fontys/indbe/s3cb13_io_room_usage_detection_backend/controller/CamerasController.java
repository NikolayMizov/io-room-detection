package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.CreateCameraConnectionUseCase;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.UpdateCameraConnectionUseCase;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.CreateCameraConnectionRequest;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.CreateCameraConnectionResponse;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.GetCameraConnectionsUseCase;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.GetCameraConnectionsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cameras")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class CamerasController {

    private final UpdateCameraConnectionUseCase updateCameraConnectionUseCase;

    private final GetCameraConnectionsUseCase getCameraConnectionsUseCase;

    @PostMapping
    public ResponseEntity<CreateCameraConnectionResponse> createCameraConnection(@Valid @RequestBody CreateCameraConnectionRequest request) {
        CreateCameraConnectionResponse response = updateCameraConnectionUseCase.updateCameraConnection(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<GetCameraConnectionsResponse> getCameraConnections() {
        GetCameraConnectionsResponse response = this.getCameraConnectionsUseCase.getCameraConnections();
        return ResponseEntity.ok(response);
    }
}
