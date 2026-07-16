import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import { postNoAuth, ApiError } from '../../api/apiClient'
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
      const { data, headers } = await postNoAuth('/api/user/login', {
        email: email.trim(),
        password,
      })

      const authHeader = headers.get('Authorization')

      if (authHeader && authHeader.startsWith('Bearer ')) {
        const token = authHeader.substring(7)
        login(token, data)
        navigate('/')
      } else if (data.token) {
        login(data.token, data)
        navigate('/')
      } else {
        setError('No se pudo obtener el token de autenticación')
      }
    } catch (err) {
      if (err instanceof ApiError) {
        setError(err.message)
      } else {
        setError('No se pudo conectar con el servidor')
      }
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
