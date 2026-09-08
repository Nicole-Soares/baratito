const BASE_URL = import.meta.env.VITE_BACKEND_URL || 'http://localhost:8080'

class ApiError {
  constructor(status, message, fields = null) {
    this.status = status
    this.message = message
    this.fields = fields
  }
}

function getToken() {
  return localStorage.getItem('token')
}

function buildHeaders(custom = {}) {
  const headers = { ...custom }
  const token = getToken()
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }
  return headers
}

async function request(endpoint, options = {}) {
  const { headers: customHeaders, ...rest } = options
  const url = `${BASE_URL}${endpoint}`

  let res
  try {
    res = await fetch(url, {
      ...rest,
      headers: buildHeaders(customHeaders),
    })
  } catch {
    throw new ApiError(0, 'No se pudo conectar con el servidor')
  }

  const contentType = res.headers.get('content-type')
  let data = null
  if (contentType && contentType.includes('application/json')) {
    data = await res.json()
  }

  if (!res.ok) {
    if (res.status === 401) {
      throw new ApiError(401, 'Sesión expirada. Iniciá sesión nuevamente')
    }
    if (res.status === 403) {
      throw new ApiError(403, 'No tenés permiso para realizar esta acción')
    }
    if (res.status === 404) {
      throw new ApiError(404, 'Recurso no encontrado')
    }
    if (res.status >= 500) {
      throw new ApiError(res.status, data?.error || 'Error del servidor. Intentá de nuevo más tarde')
    }
    // Errores 400: validación o negocio
    if (data) {
      if (data.error) {
        throw new ApiError(400, data.error)
      }
      // Errores de validación con campos
      const messages = Object.values(data).filter(v => typeof v === 'string')
      if (messages.length > 0) {
        throw new ApiError(400, messages.join('. '), data)
      }
    }
    throw new ApiError(res.status, 'Ocurrió un error inesperado')
  }

  return { data, headers: res.headers }
}

export function get(endpoint) {
  return request(endpoint, { method: 'GET' })
}

export function post(endpoint, body) {
  return request(endpoint, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  })
}

export function del(endpoint) {
  return request(endpoint, { method: 'DELETE' })
}

export function postNoAuth(endpoint, body) {
  return request(endpoint, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  })
}

export { ApiError }
