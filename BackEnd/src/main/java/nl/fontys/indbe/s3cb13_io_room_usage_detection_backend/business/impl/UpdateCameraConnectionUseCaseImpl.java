package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.CreateCameraConnectionUseCase;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.GetMeetingRoomUseCase;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.UpdateCameraConnectionUseCase;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.CreateCameraConnectionRequest;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.CreateCameraConnectionResponse;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message.GetMeetingRoomResponse;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.microsoftGraphApi.EventApi;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain.RoomEventStatus;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.CameraConnectionRepository;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.MeetingRoomRepository;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.ReservationRepository;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.entity.CameraConnectionEntity;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.entity.MeetingRoomEntity;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.entity.ReservationEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class UpdateCameraConnectionUseCaseImpl implements UpdateCameraConnectionUseCase {

    private final CameraConnectionRepository cameraConnectionRepository;
    private final MeetingRoomRepository meetingRoomRepository;
    private final ReservationRepository reservationRepository;
    private final CreateCameraConnectionUseCase createCameraConnectionUseCase;
    private final GetMeetingRoomUseCase getMeetingRoomUseCase;
    private final EventApi eventApi;

    @Transactional
    @Override
    public CreateCameraConnectionResponse updateCameraConnection(CreateCameraConnectionRequest request) {
        if (!cameraConnectionRepository.existsCameraConnectionEntityByMacAddress(request.getMacAddress())) {
            try {
                return createCameraConnectionUseCase.createCameraConnection(request);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        if (meetingRoomRepository.isMacAddressAssignedToMeetingRoom(request.getMacAddress())) {
            CameraConnectionEntity cameraConnectionEntity = cameraConnectionRepository.findCameraConnectionEntityByMacAddress(request.getMacAddress());
            MeetingRoomEntity meetingRoomEntity = meetingRoomRepository.findMeetingRoomEntitiesByCameraConnection(cameraConnectionEntity);

            meetingRoomRepository.updateCurrentCapacityByMeetingRoomId(meetingRoomEntity.getId(), request.getNrOfOccupants());
            cameraConnectionRepository.updateLastUpdated(cameraConnectionEntity.getId(), LocalDateTime.now());

            GetMeetingRoomResponse getMeetingRoomResponse = getMeetingRoomUseCase.getMeetingRoom(meetingRoomEntity.getEmail());
            if (getMeetingRoomResponse.getMeetingRoom().getStatus() == RoomEventStatus.OCCUPIED_NOW) {
                String roomEventId = getMeetingRoomResponse.getMeetingRoom().getRoomEvent().getId();
                Integer maxOccupancy = reservationRepository.getReservationMaxOccupancyById(roomEventId);

                 String seriesMasterId = eventApi.getCurrentRoomEvent(getMeetingRoomResponse.getMeetingRoom().getEmail(), roomEventId).getSeriesMasterId();

                if (maxOccupancy == null) {
                    ReservationEntity reservationEntity = ReservationEntity.builder()
                            .id(roomEventId)
                            .meetingRoom(meetingRoomEntity)
                            .maxOccupancy(request.getNrOfOccupants())
                            .seriesMasterId(seriesMasterId)
                            .build();
                    reservationRepository.save(reservationEntity);
                }
                else if (request.getNrOfOccupants() > maxOccupancy) {
                    reservationRepository.updateReservationMaxOccupancyById(roomEventId, request.getNrOfOccupants());
                }
            }

            return CreateCameraConnectionResponse.builder()
                    .cameraId(cameraConnectionEntity.getId())
                    .build();
        }
        return null;
    }
}
