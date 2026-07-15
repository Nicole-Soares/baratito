export const secureFetch = async (url, options = {}) => {
  const token = localStorage.getItem('token');

  const headers = {
    'Content-Type': 'application/json',
    ...(token && { 'Authorization': `Bearer ${token}` }),
    ...options.headers
  };

  try {
      const response = await fetch(url, { ...options, headers });

      if (response.ok) return response;

      // Manejo centralizado según el código de estado
      switch (response.status) {
        case 401:
          // Token expirado o inválido
          localStorage.removeItem('token');
          window.location.href = '/login';
          throw new Error('Sesión expirada');

        case 403:
          // El usuario está logueado pero no tiene permiso (ej: es usuario común y quiere borrar producto)
          alert("No tenés permiso para realizar esta acción.");
          break;

        case 404:
          console.error("Recurso no encontrado:", url);
          break;

        case 500:
        case 503:
          // Error de servidor (caída de la DB, servidor offline, etc.)
          alert("El servidor está teniendo problemas. Intentalo de nuevo en unos minutos.");
          break;

        default:
          alert("Ocurrió un error inesperado. Por favor, recargá la página.");
      }

      return null; // Retornamos null para que el componente que llamó sepa que falló

    } catch (error) {
      // Aquí atrapamos errores de red (ej: el backend está apagado o no hay internet)
      console.error("Error de conexión:", error);
      throw error;
    }
  };