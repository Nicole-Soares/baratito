import apiFetch from "../apiFetch";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

// POST /api/user/register
export const registrarse = async (usuarioData) => {
    return apiFetch(`${API_BASE_URL}/api/user/register`, {
        method: "POST",
        body: JSON.stringify(usuarioData),
    }, "Error al registrarse");
};

// POST /api/user/login
export const loguearse = async (email, password) => {
    return apiFetch(`${API_BASE_URL}/api/user/login`, {
        method: "POST",
        body: JSON.stringify({ email, password }),
    }, "Fallo al loguearse.");
};