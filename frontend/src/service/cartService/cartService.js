import apiFetch from "../apiFetch";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export const obtenerCarrito = async () => {
    return apiFetch(
        `${API_BASE_URL}/api/carrito`,
        { method: "GET" },
        "No se pudo cargar el carrito"
    );
};

export const agregarAlCarrito = async (productoId) => {
    return apiFetch(
        `${API_BASE_URL}/api/carrito/${productoId}`,
        { method: "POST" },
        "No se pudo agregar al carrito"
    );
};

export const quitarDelCarrito = async (productoId) => {
    return apiFetch(
        `${API_BASE_URL}/api/carrito/${productoId}`,
        { method: "DELETE" },
        "No se pudo quitar del carrito"
    );
};