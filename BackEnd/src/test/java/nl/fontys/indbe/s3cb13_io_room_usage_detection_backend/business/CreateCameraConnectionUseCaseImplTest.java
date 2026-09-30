package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business;

import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.impl.CreateCameraConnectionUseCaseImpl;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.CreateCameraConnectionRequest;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.CreateCameraConnectionResponse;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.CameraConnectionRepository;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.MeetingRoomRepository;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.entity.CameraConnectionEntity;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.entity.MeetingRoomEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateCameraConnectionUseCaseImplTest {

    @Mock
    private CameraConnectionRepository cameraConnectionRepository;

    @Mock
    private MeetingRoomRepository meetingRoomRepository;

    @InjectMocks
    private CreateCameraConnectionUseCaseImpl createCameraConnectionUseCase;


    @Test
    void testCreateCameraConnection_ExistingMacAddressAssignedToRoom() {
        // Arrange
        String macAddress = "AA:BB:CC:DD:EE:FF";
        int nrOfOccupants = 5;
        long meetingRoomId = 2L;
        Long cameraId = 1L;

        CreateCameraConnectionRequest request = CreateCameraConnectionRequest.builder()
                .macAddress(macAddress)
                .nrOfOccupants(nrOfOccupants)
                .build();

        CameraConnectionEntity cameraConnectionEntity = CameraConnectionEntity.builder()
                .id(cameraId)
                .macAddress(macAddress)
                .build();

        MeetingRoomEntity meetingRoomEntity = MeetingRoomEntity.builder()
                .id(meetingRoomId)
                .build();

        when(meetingRoomRepository.isMacAddressAssignedToMeetingRoom(macAddress)).thenReturn(true);
        when(cameraConnectionRepository.findCameraConnectionEntityByMacAddress(macAddress)).thenReturn(cameraConnectionEntity);
        when(meetingRoomRepository.findMeetingRoomEntitiesByCameraConnection(cameraConnectionEntity)).thenReturn(meetingRoomEntity);

        // Act
        CreateCameraConnectionResponse response = createCameraConnectionUseCase.createCameraConnection(request);

        // Assert
        assertNotNull(response);
        assertEquals(cameraId, response.getCameraId());
        verify(meetingRoomRepository).updateCurrentCapacityByMeetingRoomId(meetingRoomId, nrOfOccupants);
    }

    @Test
    void testCreateCameraConnection_NewCameraConnection() {
        // Arrange
        String macAddress = "AA:BB:CC:DD:EE:FF";
        Long cameraId = 1L;

        CreateCameraConnectionRequest request = CreateCameraConnectionRequest.builder()
                .macAddress(macAddress)
                .build();

        CameraConnectionEntity newCameraConnectionEntity = CameraConnectionEntity.builder()
                .id(cameraId)
                .macAddress(macAddress)
                .build();

        when(meetingRoomRepository.isMacAddressAssignedToMeetingRoom(macAddress)).thenReturn(false);
        when(cameraConnectionRepository.existsCameraConnectionEntityByMacAddress(macAddress)).thenReturn(false);
        when(cameraConnectionRepository.save(any(CameraConnectionEntity.class))).thenReturn(newCameraConnectionEntity);

        // Act
        CreateCameraConnectionResponse response = createCameraConnectionUseCase.createCameraConnection(request);

        // Assert
        assertNotNull(response);
        assertEquals(cameraId, response.getCameraId());
        verify(cameraConnectionRepository).save(any(CameraConnectionEntity.class));
    }

    @Test
    void testCreateCameraConnection_CameraExistsButNotAssignedToRoom() {
        // Arrange
        String macAddress = "AA:BB:CC:DD:EE:FF";

        CreateCameraConnectionRequest request = CreateCameraConnectionRequest.builder()
                .macAddress(macAddress)
                .build();

        when(meetingRoomRepository.isMacAddressAssignedToMeetingRoom(macAddress)).thenReturn(false);
        when(cameraConnectionRepository.existsCameraConnectionEntityByMacAddress(macAddress)).thenReturn(true);

        // Act
        CreateCameraConnectionResponse response = createCameraConnectionUseCase.createCameraConnection(request);

        // Assert
        assertNull(response);
    }
}
