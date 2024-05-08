import { createContext, useContext, useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";
import { successNotification } from "../services/notification.jsx";
import { login as clientLogin } from "../services/client.jsx"
import Cookies from 'js-cookie';

const AuthContext = createContext({});

const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);

    useEffect(() => {
        const storedUser = Cookies.get('user');
        if (storedUser) {
            setUser(JSON.parse(storedUser));
        }
    }, []);

    const login = async (emailAndPassword) => {
        return new Promise((resolve, reject) => {
            clientLogin(emailAndPassword).then(async (res) => {
                const { token, appUserDTO } = res;
                try {
                    setUser(appUserDTO);
                    Cookies.set('user', JSON.stringify(appUserDTO));
                    resolve();
                } catch (err) {
                    reject(err.message || 'An error occurred while fetching user details');
                }
            }).catch(err => {
                reject(err.message || 'An error occurred');
            })
        })
    };

const register = async (usernameEmailAndPassword) => {
        return new Promise((resolve, reject) => {
            register(usernameEmailAndPassword).then(res => {
                successNotification(res.data, res.data.message)
                resolve(res);
            }).catch(err => {
                reject(err);
            })

        })
    }

    const logout = () => {
            Cookies.remove("token");
            Cookies.remove('user');
            setUser(null);
        }

    const isUserAuthenticated = () => {
        const token = localStorage.getItem("token");
        if (!token) {
            return false;
        }
        const {exp: expiration} = jwtDecode(token);
        if (Date.now() > expiration * 1000) {
            logout()
            return false;
        }
        return true;
    }

    return (
        <AuthContext.Provider value={{
            user,
            register,
            login,
            logout,
            isUserAuthenticated
        }}>
            {children}
        </AuthContext.Provider>
    )
}

export const useAuth = () => useContext(AuthContext);

export default AuthProvider;