import apiFetch from "../apiFetch";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

// GET /api/favoritos
export const obtenerFavoritos = async () => {
    return apiFetch(`${API_BASE_URL}/api/favoritos`, {
        method: "GET",
    }, "Error al obtener los favoritos");
};

// POST /api/favoritos/{productoId}
export const agregarFavorito = async (productoId) => {
    return apiFetch(`${API_BASE_URL}/api/favoritos/${productoId}`, {
        method: "POST"
    }, "Fallo al agregar a favoritos.");
};

// DELETE /api/favoritos/{productoId}

export const quitarFavorito = async (productoId) => {
    return apiFetch(`${API_BASE_URL}/api/favoritos/${productoId}`, {
        method: "DELETE"
    }, "Fallo al eliminar de favoritos.");
};