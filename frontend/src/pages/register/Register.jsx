import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import './Register.css'

function Register() {
  const [nombre, setNombre] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const { register } = useAuth()

  const handleSubmit = async (e) => {
    e.preventDefault()

    if (!nombre.trim() || !email.trim() || !password.trim() || !confirmPassword.trim()) {
      setError('Por favor, completa todos los campos')
      return
    }

    if (password !== confirmPassword) {
      setError('Las contraseñas no coinciden')
      return
    }

    if (password.length < 6) {
      setError('La contraseña debe tener al menos 6 caracteres')
      return
    }

    setError('')
    setLoading(true)

    try {
      await register({
        nombre: nombre.trim(),
        email: email.trim(),
        password,
      })
      // register() del contexto ya guarda el token y setea el user
      navigate('/')
    } catch (err) {
      setError(err.message || 'No se pudo conectar con el servidor')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="register-container">
      <button className="back-btn" onClick={() => navigate('/')}>
        ← Volver al inicio
      </button>

      <div className="register-card">
        <header className="register-header">
          <h1 className="app-logo">Baratito</h1>
          <p className="register-subtitle">Creá tu cuenta para empezar a comparar</p>
        </header>

        <form onSubmit={handleSubmit} className="register-form">
          <div className="form-group">
            <label htmlFor="nombre">Nombre Completo</label>
            <div className={`input-wrapper ${error && !nombre ? 'input-wrapper--error' : ''}`}>
              <span className="input-icon">👤</span>
              <input
                type="text"
                id="nombre"
                placeholder="Tu nombre"
                value={nombre}
                disabled={loading}
                onChange={(e) => {
                  setNombre(e.target.value)
                  if (error) setError('')
                }}
              />
            </div>
          </div>

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
                placeholder="Mínimo 6 caracteres"
                value={password}
                disabled={loading}
                onChange={(e) => {
                  setPassword(e.target.value)
                  if (error) setError('')
                }}
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword">Confirmar Contraseña</label>
            <div className={`input-wrapper ${error && password !== confirmPassword ? 'input-wrapper--error' : ''}`}>
              <span className="input-icon">🔄</span>
              <input
                type="password"
                id="confirmPassword"
                placeholder="Repetí tu contraseña"
                value={confirmPassword}
                disabled={loading}
                onChange={(e) => {
                  setConfirmPassword(e.target.value)
                  if (error) setError('')
                }}
              />
            </div>
          </div>

          {error && <p className="register-error">⚠ {error}</p>}

          <button type="submit" className="register-submit-btn" disabled={loading}>
            {loading ? 'Creando cuenta...' : 'Registrarse'}
          </button>
        </form>

        <footer className="register-footer">
          <p>¿Ya tenés cuenta? <span className="login-link" onClick={() => navigate('/login')}>Iniciá sesión acá</span></p>
        </footer>
      </div>
    </div>
  )
}

export default Register