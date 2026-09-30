import {Navigate, Route, Routes, BrowserRouter as Router} from "react-router-dom";
import AllRoomsPage from './components/Pages/AllRoomsPage';
import RoomInfoPage from './components/Pages/RoomInfoPage';
import UpdateRoomPage from "./components/Pages/UpdateRoomPage"
import LogInPage from './components/Pages/LogInPage'
import './App.css'

function App() {
  return (
      <Router>
          <Routes>
              <Route index element={<Navigate to="rooms" replace={true} />} />
              <Route path="/rooms">
                <Route index element={<AllRoomsPage />} />
                <Route path=":email">
                    <Route index element={<RoomInfoPage />} />
                    <Route path="update" element={<UpdateRoomPage />} />
                </Route>
              </Route>
              <Route path="/login" element={<LogInPage />} />
          </Routes>
      </Router>
  )
}

export default App
