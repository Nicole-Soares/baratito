import { jwtDecode } from 'jwt-decode';


const setToken = (token) => {
    localStorage.setItem("token", token);
};

const getToken = () => localStorage.getItem("token");


const clearToken = () => {
    localStorage.removeItem("token");
    localStorage.removeItem(AVATAR_URL_KEY);
};

const getUserId = () => {
    const token = getToken();
    if (!token) return null;

    try {
        const payload = jwtDecode(token);
        return payload ? payload.userId : null;
    } catch (error) {
        console.error("Error decodificando el token:", error);
        return null;
    }
};

const isTokenExpired = () => {
    const token = getToken();
    if (!token) return true;
    try {
      const { exp } = jwtDecode(token);
      const now = Date.now() / 1000;
      return exp < now;
    } catch {
      return true;
    }
  };

const Storage = {
    setToken,
    getToken,
    clearToken,
    getUserId,
    isTokenExpired,

};

export default Storage;