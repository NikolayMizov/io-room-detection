package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.CreateRoomUseCase;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.GetAllRoomsUseCase;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.GetMeetingRoomUseCase;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.exception.InvalidPlaceException;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.CreateRoomRequest;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.CreateRoomResponse;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.GetAllRoomsRequest;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.GetAllRoomsResponse;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.GetMeetingRoomResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class RoomsController {
    private final CreateRoomUseCase createRoomUseCase;
    private final GetAllRoomsUseCase getAllRoomsUseCase;
    private final GetMeetingRoomUseCase getMeetingRoomUseCase;

    /**
     * @return response
     *
     * @should return 200 response with an array of MeetingRooms
     *
     * @should return 400 response when placeId is invalid
     */

    @GetMapping
    public ResponseEntity<GetAllRoomsResponse> getAllRooms(@RequestParam(value = "placeId", required = false, defaultValue = "rooms.eindhoven@iodigital.com") String placeId,
                                                           @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                                                           @RequestParam(value = "pageSize", required = false, defaultValue = "250") int pageSize,
                                                           @RequestParam(value = "status", required = false, defaultValue = "" ) String status,
                                                           @RequestParam(value = "floorNumber", required = false, defaultValue = "-1") int floorNumber,
                                                           @RequestParam(value = "searchInput", required = false, defaultValue = "") String searchInput) {
        GetAllRoomsRequest request = new GetAllRoomsRequest();
        request.setPlaceId(placeId);
        request.setPageIndex(pageIndex);
        request.setPageSize(pageSize);
        request.setStatus(status);
        request.setFloorNumber(floorNumber);
        request.setSearchInput(searchInput);
        GetAllRoomsResponse response = getAllRoomsUseCase.getAllRooms(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("{roomId}")
    public ResponseEntity<GetMeetingRoomResponse> getMeetingRoom(@PathVariable long roomId) {
        GetMeetingRoomResponse response = getMeetingRoomUseCase.getMeetingRoom(roomId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/email/{roomEmail}")
    public ResponseEntity<GetMeetingRoomResponse> getMeetingRoom(@PathVariable String roomEmail) {
        GetMeetingRoomResponse response = getMeetingRoomUseCase.getMeetingRoom(roomEmail);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ExceptionHandler(InvalidPlaceException.class)
    @ResponseBody
    public ResponseEntity<Object> handleInvalidPlaceException(InvalidPlaceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @PostMapping
    public ResponseEntity<CreateRoomResponse> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        CreateRoomResponse response = this.createRoomUseCase.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
