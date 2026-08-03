import apiFetch from "../apiFetch";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

// GET /api/user/me
export const obtenerPerfil = async () => {
    return apiFetch(
        `${API_BASE_URL}/api/user/me`,
        { method: "GET" },
        "No se pudo obtener el perfil"
    );
};

// PUT /api/user/perfil
export const actualizarPerfil = async ({ nombre, email, passwordActual }) => {
    return apiFetch(
        `${API_BASE_URL}/api/user/perfil`,
        {
            method: "PUT",
            body: JSON.stringify({ nombre, email, passwordActual }),
        },
        "No se pudo actualizar el perfil"
    );
};

// PUT /api/user/password
export const cambiarPassword = async ({ passwordActual, passwordNueva }) => {
    return apiFetch(
        `${API_BASE_URL}/api/user/password`,
        {
            method: "PUT",
            body: JSON.stringify({ passwordActual, passwordNueva }),
        },
        "No se pudo cambiar la contraseña"
    );
};