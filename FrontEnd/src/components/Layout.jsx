import '../assets/css/layout.css';
import {Outlet} from "react-router-dom";

function Layout({sidebarContent, rightSideContent}) {
    return (
        <div>
            <div className="container">
                <div className="sidebar">

                    {sidebarContent}
                </div>
                <div className="right-side">
                    {rightSideContent || <Outlet />}
                </div>
            </div>
        </div>
    )
}

export default Layout;