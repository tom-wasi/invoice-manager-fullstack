import {Outlet, useLocation} from 'react-router-dom';
import Navbar from "../components/Navbar.jsx";

export default function RootLayout() {
    const location = useLocation();
    const hideOnRoutes = ['/login', '/register'];

    return(
        <div>
            {!hideOnRoutes.includes(location.pathname) && <Navbar />}
            <Outlet />
        </div>
    )
}