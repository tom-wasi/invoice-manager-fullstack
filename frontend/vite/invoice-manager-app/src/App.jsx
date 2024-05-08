import {createBrowserRouter, RouterProvider, createRoutesFromElements, Route} from "react-router-dom";
import React from "react";
import RootLayout from "./layouts/RootLayout.jsx";
import {Login} from "./pages/Login.jsx";
import ProtectedRoute from "./components/ProtectedRoute.jsx";
import {UserDashboard} from "./pages/UserDashboard.jsx";
import {Home} from "./Home.jsx";
import {Register} from "./pages/Register.jsx";
import {CompanyDashboard} from "./pages/CompanyDashboard.jsx";
import {ConfirmAccount} from "./pages/ConfirmAccount.jsx";
import {MyAccount} from "./pages/MyAccount.jsx";

const router = createBrowserRouter(
    createRoutesFromElements(
        <Route path='/' element={<RootLayout/>}>
            <Route
                index
                element={<Home/>}/>

            <Route
                path='confirm-account/:confirmationToken'
                element={<ConfirmAccount/>}
            />

            <Route
                path='user/:userId'
                element={<ProtectedRoute><UserDashboard/></ProtectedRoute>}
            />

            <Route
                path='user/:userId/company/:companyId'
                element={<ProtectedRoute><CompanyDashboard/></ProtectedRoute>}
            />

            <Route
                path='user/:userId/my-account'
                element={<ProtectedRoute><MyAccount/></ProtectedRoute>}
            />

            <Route
                path='login'
                element={<Login/>}
            />

            <Route
                path='register'
                element={<Register/>}
            />
        </Route>
    )
)

export function App() {
    return (
        <RouterProvider router={router}/>
    )
}