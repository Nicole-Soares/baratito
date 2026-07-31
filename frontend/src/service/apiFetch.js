import Storage from './storage';

const apiFetch = async (url, options = {}, message) => {
    const token = Storage.getToken();

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
        ...options.headers,
    };

    const fetchOptions = {
        ...options,
        headers,
    };

    const response = await fetch(url, fetchOptions);

    if (response.status === 401) {
        console.error("Error 401: Token inválido o expirado.");
        Storage.clearToken();

        // Avisamos a toda la app (AuthContext está escuchando esto)
        window.dispatchEvent(new CustomEvent('auth:unauthorized'));

        const error = new Error("Tu sesión expiró. Iniciá sesión de nuevo.");
        error.status = 401;
        throw error;
    }

    if (response.status === 404) {
        console.error("Error 404: Not Found");
        const error = new Error("Not Found");
        error.status = 404;
        throw error;
    }

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

    if (response.status === 204 || response.headers.get('content-length') === '0') {
        return {};
    }

    const data = await response.json();

    const authHeader = response.headers.get('Authorization');
    if (authHeader) {
        data.token = authHeader.replace('Bearer ', '');
    }

    return data;
};

export default apiFetch;