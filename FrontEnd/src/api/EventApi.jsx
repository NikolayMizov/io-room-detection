import axios from 'axios';

const baseUrl = "http://localhost:8080/events";

const EventApi = {
    getEventsById: (roomId) => axios.get(`${baseUrl}/${roomId}`)
        .then(response => response.data.roomEvents),
    getEventsByEmail: (roomEmail) => axios.get(`${baseUrl}/email/${roomEmail}`)
        .then(response => response.data.roomEvents),
    getEventsByEmailAndDateRange: (roomEmail, startDate, endDate) => axios.get(`${baseUrl}/email/${roomEmail}`, {
        params: {
            startDate: startDate,
            endDate: endDate
        }
    }).then(response => response.data.roomEvents)
};

export default EventApi;