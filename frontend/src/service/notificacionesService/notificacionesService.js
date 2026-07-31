import apiFetch from "../apiFetch";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

// GET /api/notificaciones
export const obtenerNotificaciones = async () => {
    return apiFetch(
        `${API_BASE_URL}/api/notificaciones`,
        { method: "GET" },
        "No se pudieron obtener las notificaciones"
    );
};

// GET /api/notificaciones/no-leidas/count
export const contarNoLeidas = async () => {
    return apiFetch(
        `${API_BASE_URL}/api/notificaciones/no-leidas/count`,
        { method: "GET" },
        "No se pudo obtener el contador de notificaciones"
    );
};

// PUT /api/notificaciones/marcar-leidas
export const marcarTodasComoLeidas = async () => {
    return apiFetch(
        `${API_BASE_URL}/api/notificaciones/marcar-leidas`,
        { method: "PUT" },
        "No se pudieron marcar las notificaciones como leídas"
    );
};