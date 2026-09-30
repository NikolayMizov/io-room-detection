import axios from 'axios';

const fetchRooms = async (pageIndex, pageSize, searchInput, floorFilter, statusFilter) => {
    try {
        let params = { 
            pageIndex: pageIndex,
            pageSize: pageSize,
        };
        if (searchInput?.trim()) {
            params.searchInput = searchInput.toString();
        }
        if (floorFilter?.trim()) {
            params.floorNumber = floorFilter.toString();
        }
        if (statusFilter?.trim()) {
            params.status = statusFilter.toString();
        }
        const response = await axios.get('http://localhost:8080/rooms', {
            params: params
        });
        console.log('Fetched rooms:', response.data);
        return response.data.rooms;
    } catch (error) {
        console.error('Error fetching rooms:', error);
        throw error;
    }
};

export default fetchRooms;