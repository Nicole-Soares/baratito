import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import './Register.css'
import { useAuth } from '../../context/AuthContext'

function Register() {
  const [nombre, setNombre] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const { login } = useAuth()

  const handleSubmit = async (e) => {
      e.preventDefault()

      // Validaciones
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
        const res = await fetch('http://localhost:8080/api/user/register', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            nombre: nombre.trim(),
            email: email.trim(),
            password
          }),
        })


        const authHeader = res.headers.get('Authorization')

        const data = await res.json()

        if (!res.ok) {
          setError(data.error || 'Ocurrió un error al registrarse')
          return
        }


        if (authHeader && authHeader.startsWith('Bearer ')) {
          const token = authHeader.substring(7)
          login(token, data) //
        } else if (data.token) {
          login(data.token, data)
        }

        navigate('/')
      } catch (err) {
        console.error(err) 
        setError('No se pudo conectar con el servidor')
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
          {/* Campo Nombre */}
          <div className="form-group">
            <label htmlFor="nombre">Nombre Completo</label>
            <div className={`input-wrapper ${error && !nombre ? 'input-wrapper--error' : ''}`}>
              <span className="input-icon">👤</span>
              <input
                type="text"
                id="nombre"
                placeholder="Tu nombre"
                value={nombre}
                onChange={(e) => {
                  setNombre(e.target.value)
                  if (error) setError('')
                }}
              />
            </div>
          </div>

          {/* Campo Email */}
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

          {/* Campo Contraseña */}
          <div className="form-group">
            <label htmlFor="password">Contraseña</label>
            <div className={`input-wrapper ${error && !password ? 'input-wrapper--error' : ''}`}>
              <span className="input-icon">🔒</span>
              <input
                type="password"
                id="password"
                placeholder="Mínimo 6 caracteres"
                value={password}
                onChange={(e) => {
                  setPassword(e.target.value)
                  if (error) setError('')
                }}
              />
            </div>
          </div>

          {/* Campo Confirmar Contraseña */}
          <div className="form-group">
            <label htmlFor="confirmPassword">Confirmar Contraseña</label>
            <div className={`input-wrapper ${error && password !== confirmPassword ? 'input-wrapper--error' : ''}`}>
              <span className="input-icon">🔄</span>
              <input
                type="password"
                id="confirmPassword"
                placeholder="Repetí tu contraseña"
                value={confirmPassword}
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