import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import './Login.css'

function Login() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const { login } = useAuth()

  const handleSubmit = async (e) => {
    e.preventDefault()

    if (!email.trim() || !password.trim()) {
      setError('Por favor, completa todos los campos')
      return
    }

    setError('')
    setLoading(true)

    try {
      const res = await fetch('http://localhost:8080/api/user/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email: email.trim(), password }), // en el login no mandamos el nombre, el back no espera el nombre
      })

      const authHeader = res.headers.get('Authorization')

      let data = {}
      const contentType = res.headers.get("content-type")
      if (contentType && contentType.includes("application/json")) {
        data = await res.json()
      }

      if (!res.ok) {
        setError(data.error || 'Credenciales incorrectas')
        return
      }

      // Procesamos la respuesta correcta
      if (authHeader && authHeader.startsWith('Bearer ')) {
        const token = authHeader.substring(7)
        login(token, data) // Mandamos token y los datos del usuario (id, nombre, email)
        navigate('/')
      } else if (data.token) {
        login(data.token, data)
        navigate('/')
      } else {
        setError('No se pudo obtener el token de autenticación')
      }

    } catch (err) {
      console.error(err)
      setError('No se pudo conectar con el servidor')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-container">
      <button className="back-btn" onClick={() => navigate('/')}>
        ← Volver al inicio
      </button>

      <div className="login-card">
        <header className="login-header">
          <h1 className="app-logo">Baratito</h1>
          <p className="login-subtitle">Iniciá sesión para guardar tus carritos</p>
        </header>

        <form onSubmit={handleSubmit} className="login-form">
          <div className="form-group">
            <label htmlFor="email">Correo Electrónico</label>
            <div className={`input-wrapper ${error && !email ? 'input-wrapper--error' : ''}`}>
              <span className="input-icon">✉</span>
              <input
                type="email"
                id="email"
                placeholder="ejemplo@correo.com"
                value={email}
                onChange={(e) => {
                  setEmail(e.target.value)
                  if (error) setError('')
                }}
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="password">Contraseña</label>
            <div className={`input-wrapper ${error && !password ? 'input-wrapper--error' : ''}`}>
              <span className="input-icon">🔒</span>
              <input
                type="password"
                id="password"
                placeholder="••••••••"
                value={password}
                onChange={(e) => {
                  setPassword(e.target.value)
                  if (error) setError('')
                }}
              />
            </div>
          </div>

          {error && <p className="login-error">⚠ {error}</p>}

          <button type="submit" className="login-submit-btn" disabled={loading}>
            {loading ? 'Ingresando...' : 'Iniciar Sesión'}
          </button>
        </form>

        <footer className="login-footer">
          <p>¿No tenés una cuenta? <span className="register-link" onClick={() => navigate('/register')}>Registrate acá</span></p>
        </footer>
      </div>
    </div>
  )
}

export default Login