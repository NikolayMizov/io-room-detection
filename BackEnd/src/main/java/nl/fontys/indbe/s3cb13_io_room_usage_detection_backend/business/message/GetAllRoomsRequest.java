package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetAllRoomsRequest {
    private String placeId;
    private int pageSize;
    private int pageIndex;
    private String status;
    private int floorNumber;
    private String searchInput;
}
