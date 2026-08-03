import { createContext, useContext, useState, useEffect } from "react";
import { registrarse, loguearse } from "../service/authService/authService.js";
import storage from "../service/storage.js";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [checkingSession, setCheckingSession] = useState(true);

    useEffect(() => {
        const token = storage.getToken();

        if (token && !storage.isTokenExpired()) {
            const userId = storage.getUserId();
            setUser({ id: userId });
        } else {
            storage.clearToken();
        }

        setCheckingSession(false);
    }, []);

    const applyAuthResponse = (data) => {
        const { token, ...usuario } = data;
        storage.setToken(token);
        setUser(usuario);
    };

    const login = async (email, password) => {
        const data = await loguearse(email, password);
        applyAuthResponse(data);
    };

    const register = async (usuarioData) => {
        const data = await registrarse(usuarioData);
        applyAuthResponse(data);
    };

    const logout = () => {
        storage.clearToken();
        setUser(null);
    };

    // Escucha global: cualquier request que reciba 401 dispara este evento,
    // y acá reaccionamos deslogueando en toda la app automáticamente.
  useEffect(() => {
      const handleUnauthorized = () => {
          sessionStorage.setItem('authMessage', 'Tu sesión expiró. Iniciá sesión de nuevo.');
          logout();
      };

      window.addEventListener('auth:unauthorized', handleUnauthorized);
      return () => window.removeEventListener('auth:unauthorized', handleUnauthorized);
  }, []);
    return (
        <AuthContext.Provider value={{
            user,
            isLoggedIn: !!user,
            checkingSession,
            login,
            register,
            logout
        }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);