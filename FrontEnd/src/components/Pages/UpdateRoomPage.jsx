import Layout from "../Layout";
import logo from '../../assets/img/IO_Logo.png';
import '../../assets/css/updateRoom.css'
import React, { useState, useEffect } from 'react';
import MeetingRoomApi from "../../api/MeetingRoomApi";
import CameraApi from "../../api/CameraApi";
import {useNavigate, useParams} from 'react-router-dom';


function UpdateRoomPage() {
    const initialRoomState = {
        id: 0,
        email: "",
        name: "",
        maxCapacity: 0,
        cameraConnection: null,
        currentCapacity: 0,
        status: "",
        roomEvent: null
    }
    const [room, setRoom] = useState(initialRoomState);
    const [camera, setCamera] = useState(0);
    const [cameras, setCameras] = useState([]);
    const navigate = useNavigate();
    const params = useParams();

    const fetchRoom = () => {
        MeetingRoomApi.getMeetingRoomByEmail(params.email)
            .then(data => setRoom(data))
            .catch(error => console.log(error));
    }

    const fetchCameras = () => {
        CameraApi.getCameras()
            .then(data => setCameras(data))
            .catch(error => console.log(error));
    }

    const handleSubmit = e =>  {
        e.preventDefault();
        const newMeetingRoom = {
            email: room.email,
            cameraConnectionId: camera
        }
        MeetingRoomApi.createMeetingRoom(newMeetingRoom)
            .then(() => navigate(`/rooms/${params.email}`))
            .catch(error => console.log(error));
    }

    const cameraChanged = e => {
        setCamera(e.target.value);
    }

    useEffect(() => {
        fetchCameras();
        fetchRoom();
    }, [])

    const sidebar = (
        <div className="sidebar">
            <div className="img-div">
                <a href="/rooms"><img src={logo} className="logo" alt="IO_Logo"/></a>
            </div>

            <h2>
                {(() => {
                    const [campus, floor, ...nameParts] = room.name.split(" - ");
                    const roomName = nameParts.join(" ");
                    return `${roomName} - Floor ${floor}`;
                })()}
            </h2>
            <div className="sidebar-buttons">
                <a href={`/rooms/${room.email}`} className="btn btn-update custom-update-btn">Cancel</a>               
            </div>
        </div>
    );

    const mainContent = (
        <div className="main-content">
            <form className="add-room-form" onSubmit={handleSubmit}>
                <div className="add-room-form__wrapper">
                    <div className="add-room-form__grid">
                        <label htmlFor="roomCamera">
                            Camera:
                        </label>
                        <select
                            name="roomCamera"
                            id="roomCamera"
                            onClick={cameraChanged}
                            required
                        >
                            <option value="" disabled selected>Select a camera</option>
                            {cameras.map(camera => {
                                return (
                                    <option value={camera.id} key={camera.id}>{camera.macAddress}</option>
                                )
                            })}
                        </select>
                    </div>
                    <button className="updatebutton" type="submit">
                        Update room
                    </button>
                </div>
            </form>
        </div>
    );
    return <Layout sidebarContent={sidebar} rightSideContent={mainContent} />;
}

export default UpdateRoomPage;