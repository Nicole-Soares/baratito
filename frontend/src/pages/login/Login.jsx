import { useState, useEffect } from 'react'
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

  useEffect(() => {
    const mensaje = sessionStorage.getItem('authMessage')
    if (mensaje) {
      setError(mensaje)
      sessionStorage.removeItem('authMessage')
    }
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()

    if (!email.trim() || !password.trim()) {
      setError('Por favor, completa todos los campos')
      return
    }

    setError('')
    setLoading(true)

    try {
      await login(email.trim(), password)
      navigate('/')
    } catch (err) {
      setError(err.message || 'No se pudo conectar con el servidor')
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
                disabled={loading}
                aria-invalid={error && !email ? 'true' : 'false'}
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
                disabled={loading}
                aria-invalid={error && !password ? 'true' : 'false'}
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