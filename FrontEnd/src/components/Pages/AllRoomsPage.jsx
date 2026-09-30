import Layout from '../Layout';
import '../../assets/css/allRooms.css';
import fetchRooms from "../../api/fetchRooms.js";
import logo from '../../assets/img/IO_Logo.png';
import userIcon from '../../assets/img/user.png';
import searchIcon from '../../assets/img/search.png';
import React, { useEffect, useState } from "react";
import { Link } from 'react-router-dom';
import EventApi from "../../api/EventApi.jsx";

function PeopleAmount({ label }) {
    return (
        <span className="people-amount">
            <img className="people_img" src={userIcon} alt="person icon" />
            <span>{label}</span>
        </span>
    );
}

function AllRoomsPage() {
    const [rooms, setRooms] = useState([]);
    const [statusFilter, setStatusFilter] = useState("");
    const [floorFilter, setFloorFilter] = useState("");
    const [searchInput, setSearchInput] = useState();
    const [pageIndex, setPageIndex] = useState(0);
    const [pageSize] = useState(10);
    const [allRooms, setAllRooms] = useState([]);
    const [disableNext, setDisableNext] = useState([]);
    const [loading, setLoading] = useState(true); // Add loading state

    const onChangeSearchInput = e => {
        setSearchInput(e.target.value);
        setPageIndex(0);
    }

    const handleStatusChange = (e) => {
        setStatusFilter(e.target.value);
        setPageIndex(0);
    }

    const handleFloorChange = (e) => {
        setFloorFilter(e.target.value);
        setPageIndex(0);
    }

    const fetchRoomsWithEvents = async (pageIndex, pageSize, searchInput, floorFilter, statusFilter) => {
        try {
            setLoading(true);
            console.log('Fetching rooms...');
            const roomsData = await fetchRooms(pageIndex, pageSize, searchInput, floorFilter, statusFilter);
            console.log('Fetched rooms data:', roomsData);
            
            if (roomsData.length === 0) {
                console.warn('No rooms available for the applied filters.');
                setRooms([]);
                setDisableNext(true);
                setLoading(false);
                return;
            }
            const roomsWithEvents = await Promise.all(roomsData.map(async (room) => {
                if (room.name.toLowerCase().includes('testruimte')) {
                    console.log(`Fetching events for room: ${room.email}`);
                    const events = await EventApi.getEventsByEmail(room.email);
                    console.log(`Fetched events for room ${room.email}:`, events);
                    return { ...room, meetings: events };
                } else {
                    return { ...room, meetings: [] };
                }
            }));
            console.log('Rooms with events:', roomsWithEvents);
            setRooms(roomsWithEvents);
            if (roomsWithEvents.length < pageSize) {
                setDisableNext(true);
            } else {
                setDisableNext(false);
            }
            setLoading(false);
        } catch (error) {
            console.error('Error fetching rooms:', error);
            setLoading(false);
        }
    }

    const handleSearch = e => {
        e.preventDefault();
        fetchRoomsWithEvents(pageIndex, pageSize, searchInput, floorFilter, statusFilter);
    }


    useEffect(() => {
        fetchRoomsWithEvents(pageIndex, pageSize, searchInput, floorFilter, statusFilter);
    }, [floorFilter, statusFilter, pageIndex, pageSize]);

    useEffect(() => {
        if (!searchInput?.trim()) {
            const loadAllRooms = async () => {
                try {
                    const allRoomsData = await fetchRooms();  // Fetch all rooms
                    setAllRooms(allRoomsData);  // Store all rooms in state
                } catch (error) {
                    console.error("Error loading all rooms:", error);
                }
            };
            loadAllRooms();
            const interval = setInterval(() => {
                loadAllRooms();
            }, 30000);

            return () => clearInterval(interval);
        }
    }, [searchInput]);

    const handleNextPage = () => {
        if ((pageIndex + 1) * pageSize < allRooms.length) {
            setPageIndex(pageIndex + 1);
        }
    };

    const handlePreviousPage = () => {
        if (pageIndex > 0) {
            setPageIndex(pageIndex - 1);
        }
    };

    const getNextMeetingTime = (meetings, status) => {
        const now = new Date();
        if (!Array.isArray(meetings)) {
            return "No meetings available";
        }
        const todayMeetings = meetings.filter(meeting => new Date(meeting.startTime).getDate() === now.getDate());

        if (todayMeetings.length === 0) {
            return "Until end of the day";
        }

        if (status === 'OCCUPIED_NOW') {
            const currentMeeting = todayMeetings.find(meeting => new Date(meeting.startTime) <= now && new Date(meeting.endTime) > now);
            if (currentMeeting) {
                return `Until ${new Date(currentMeeting.endTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: false })}`;
            }
        }

        const nextMeeting = todayMeetings.find(meeting => new Date(meeting.startTime) > now);
        return nextMeeting ? `Until ${new Date(nextMeeting.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: false })}` : "Until end of the day";
    };

    const sidebarContent = (
        <div className="sidebar">
            <div className="img-div">
                <a href="/rooms"><img src={logo} className="logo" alt="IO_Logo" /></a>
            </div>
            <h2>Filters</h2>
            <div className="filter">
                <label>Status:</label>
                <select name="status" id="status" value={statusFilter} onChange={handleStatusChange}>
                    <option value="">All</option>
                    <option value="AVAILABLE">Available</option>
                    <option value="OCCUPIED_SOON">Meeting Soon</option>
                    <option value="OCCUPIED_NOW">Occupied</option>
                </select>
            </div>

            <div className="filter">
                <label>Floor:</label>
                <select name="floor" id="floor" value={floorFilter} onChange={handleFloorChange}>
                    <option value="">All</option>
                    <option value="0">Floor 0</option>
                    <option value="1">Floor 1</option>
                    <option value="2">Floor 2</option>
                    <option value="3">Floor 3</option>
                    <option value="4">Floor 4</option>
                    <option value="5">Floor 5</option>
                </select>
            </div>
            <br />
            <form>
                <button className="btn" type="button" onClick={() => { setStatusFilter(""); setFloorFilter(""); }}>Clear Filters</button>
            </form>
            <a href="/login" className="btn login-btn custom-login-btn">Log In</a>
        </div>
    );

    const statusElem = (status) => {
        return status === 'OCCUPIED_NOW'
            ? <span className="text-occupied">Occupied</span>
            : (status === 'AVAILABLE'
                ? <span className="text-available">Available</span>
                : (status === 'OCCUPIED_SOON'
                    ? <span className="text-meeting_soon">Meeting Soon</span>
                    : <span>Unknown</span>));
    }

    const mainContent = (
        <div className="main-content">
            <form onSubmit={handleSearch}>
                <div className="search-bar">
                    <input type="text" placeholder="Search Rooms" required onChange={onChangeSearchInput} />
                    <button className="btn" type="submit"><img src={searchIcon} alt="search icon" /></button>
                </div>
            </form>

            {loading ? (
                <div className="loading">Loading...</div> // Display loading indicator
            ) : (
                <div className="rooms-list">
                    {rooms.length > 0 ? (
                        // Sort rooms by floor before mapping
                    [...rooms]
                        .sort((a, b) => {
                            const floorA = parseInt(a.name.split(" - ")[1]); // Extract floor number
                            const floorB = parseInt(b.name.split(" - ")[1]);
                            return floorB - floorA; // Sort ascending
                        })
                        .map(room => {
                            // Extract parts of the name and reformat
                            const [campus, floor, ...nameParts] = room.name.split(" - ");
                            const roomName = nameParts.join(" ");
                            const formattedRoomName = `${roomName} - Floor ${floor}`;

                            return (
                                <Link to={room.email.toString()} key={room.email}>
                                    <div className={`indiv-room border-${room.status}`}>
                                        <div className="room-left">
                                            <h2>{formattedRoomName}</h2>
                                            <PeopleAmount label={`${room.currentCapacity}/10`} />
                                        </div>
                                        <div className="room-right">
                                            <p className={`room-status text-${room.status}`}>{statusElem(room.status)}</p>
                                            <p className="until">{getNextMeetingTime(room.meetings, room.status)}</p>
                                        </div>
                                    </div>
                                </Link>
                            );
                        })
                ) : (
                    <p>No rooms available</p>
                )}
            </div>
        )}
            <div className="pagination">
                <button onClick={handlePreviousPage} disabled={pageIndex === 0} className="btn custom-pagin-btn">Previous</button>
                <button onClick={handleNextPage} disabled={disableNext} className="btn custom-pagin-btn">Next</button>
            </div>
        </div>
    );

    return (
        <Layout sidebarContent={sidebarContent} rightSideContent={mainContent} />
    );
}

export default AllRoomsPage;
