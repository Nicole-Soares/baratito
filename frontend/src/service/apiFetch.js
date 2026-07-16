import Storage from './storage';

/**
 * Función auxiliar para manejar las peticiones a la API.
 * Se encarga de la lógica de autenticación y manejo de errores 401.
 */
const apiFetch = async (url, options = {}, message) => {

    const token = Storage.getToken();

 const headers = {
         'Content-Type': 'application/json',
         'Authorization': token ? `Bearer ${token}` : '',
         ...options.headers,
     };

    const fetchOptions = {
        ...options,
        headers: headers,
    };


    const response = await fetch(url, fetchOptions);

    // --- LÓGICA DE MANEJO DE ERRORES CENTRALIZADA ---

    // manejo del 401 (Unauthorized)
    if (response.status === 401) {
        console.error("Error 401: Token inválido o expirado.");
        Storage.clearToken();

        const error = new Error("Unauthorized");
        error.status = 401;
        throw error;

    }

    //manejo del 404 (Not Found)
    if (response.status === 404) {
        console.error("Error 404: Not Found");
        const error = new Error("Not Found");
        error.status = 404;
        throw error;

    }

    //Manejo de otros errores 4xx/5xx
    if (!response.ok) {
        let errorData = {};

        try {
            errorData = await response.json();
        } catch (e) {

            errorData.message = response.statusText || message;
            console.error(e);
        }

        throw new Error(errorData.message || message);
    }

    // manejo de la respuesta 204 (No Content)
    if (response.status === 204 || response.headers.get('content-length') === '0') {
        return {};
    }

    // respuesta 200, todo ok
    return response.json();
};

export default apiFetch;