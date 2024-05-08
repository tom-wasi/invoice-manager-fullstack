import {
    createContext,
    useContext,
    useState
} from "react";
import {jwtDecode} from "jwt-decode";
import {successNotification} from "../../services/notification.jsx";
import {login as clientLogin} from "../../services/client.jsx"

const AuthContext = createContext({});

const AuthProvider = ({children}) => {

    const [user, setUser] = useState(null);
    const login = async (emailAndPassword) => {
        return new Promise((resolve, reject) => {
            clientLogin(emailAndPassword).then(res => {
                setUser({
                    id: res.appUserDTO.id,
                    username: res.appUserDTO.username,
                    email: res.appUserDTO.email
                })
                resolve();
            }).catch(err => {
                reject(err.message || 'An error occurred');
            })
        })
    }
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
        localStorage.removeItem("token");
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