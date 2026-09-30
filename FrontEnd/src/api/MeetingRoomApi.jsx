import axios from "axios";

const BASE_API_URL = "https://graph.microsoft.com/v1.0";
const BASE_BACKEND_URL = "http://localhost:8080/rooms"

const MeetingRoomApi = {
    getAllMeetingRooms: (placeId, pageIndex, pageSize) => 
    axios.get(`${BASE_API_URL}/places/microsoft.graph.room`, {
        placeId: placeId,
        pageIndex: pageIndex,
        pageSize: pageSize
    }),
    getMeetingRoomById: (id) => axios.get(`${BASE_BACKEND_URL}/${id}`)
        .then(response => response.data.meetingRoom),
    getMeetingRoomByEmail: (email) => axios.get(`${BASE_BACKEND_URL}/email/${email}`)
        .then(response => response.data.meetingRoom),
    createMeetingRoom: (meetingRoom) => axios.post(BASE_BACKEND_URL, meetingRoom)
}

export default MeetingRoomApi