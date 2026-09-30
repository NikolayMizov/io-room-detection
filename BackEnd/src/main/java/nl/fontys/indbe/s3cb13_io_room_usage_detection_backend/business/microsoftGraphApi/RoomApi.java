package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.microsoftGraphApi;

import com.microsoft.graph.models.Room;
import com.microsoft.graph.models.RoomCollectionResponse;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoomApi {

    private final GraphServiceClient graphServiceClient;

    public RoomApi (GetGraphServiceClient getGraphClient) {
        graphServiceClient = getGraphClient.getGraphServiceClient();
    }

    public List<Room> getAllRooms(String placeId, int pageIndex, int pageSize, String searchInput) {
        // default Eindhoven for now
        if (placeId == null) {
            placeId = "rooms.eindhoven@iodigital.com";
        }

        RoomCollectionResponse result = graphServiceClient.places()
                .graphRoom()
                .get(RequestConfiguration -> {
                    RequestConfiguration.queryParameters.filter = "endsWith(emailAddress, 'eindhoven@iodigital.com')";
                    if (searchInput == null || searchInput.isBlank()) {
                        RequestConfiguration.queryParameters.skip = pageIndex * pageSize;
                        RequestConfiguration.queryParameters.top = pageSize;
                    }
                });

        return result.getValue();
    }

    public Room getRoom(String roomEmail) {
        // default Eindhoven for now
        String placeId = "rooms.eindhoven@iodigital.com";

        String filter =  String.format("emailAddress eq '%s'", roomEmail);

        RoomCollectionResponse result = graphServiceClient.places()
                .graphRoom()
                .get(RequestConfiguration -> {
                    RequestConfiguration.queryParameters.filter = filter;
                    RequestConfiguration.queryParameters.top = 1;
                });
        return result.getValue().get(0);
    }

    public List<Room> getRoomsWithCapacity(int capacity) {

        String filter =  String.format("endsWith(emailAddress, 'eindhoven@iodigital.com') AND capacity eq '%d'", capacity );


        RoomCollectionResponse result = graphServiceClient.places()
                .graphRoom()
                .get(RequestConfiguration -> {
                    RequestConfiguration.queryParameters.filter = filter;
                });
        return result.getValue();
    }

}
