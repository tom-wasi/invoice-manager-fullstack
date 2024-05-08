import React from 'react'
import ReactDOM from 'react-dom/client'
import {ChakraProvider, ColorModeScript, createStandaloneToast} from '@chakra-ui/react'
import {App} from "./App.jsx";
import AuthProvider from "./context/AuthContext.jsx";

const { ToastContainer } = createStandaloneToast();

ReactDOM.createRoot(document.getElementById('root')).render(
    <React.StrictMode>
        <ColorModeScript initialColorMode={"light"} />
        <ChakraProvider>
            <AuthProvider>
                <App/>
            </AuthProvider>
            <ToastContainer/>
        </ChakraProvider>
    </React.StrictMode>
)