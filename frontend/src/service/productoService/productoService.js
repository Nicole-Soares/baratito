import apiFetch from "../apiFetch";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

// GET /api/productos/buscar
export const buscarProductos = async (nombre) => {
    return apiFetch(
        `${API_BASE_URL}/api/productos/buscar?nombre=${encodeURIComponent(nombre)}`,
        { method: "GET" },
        "No se pudo realizar la búsqueda"
    );
};

// GET /api/productos/sugerencias
export const obtenerSugerencias = async (texto) => {
    return apiFetch(
        `${API_BASE_URL}/api/productos/sugerencias?query=${encodeURIComponent(texto)}`,
        { method: "GET" },
        "No se pudieron obtener sugerencias"
    );
};