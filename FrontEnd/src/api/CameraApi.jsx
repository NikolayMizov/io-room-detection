import axios from 'axios';

const baseUrl = "http://localhost:8080/cameras";

const CameraApi = {
    getCameras: () => axios.get(baseUrl)
        .then(response => response.data.cameraConnections)
}

export default CameraApi;