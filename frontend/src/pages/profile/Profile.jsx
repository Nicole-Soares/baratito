import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { obtenerPerfil, actualizarPerfil, cambiarPassword } from '../../service/profileService/profileService'
import './Profile.css'

function Profile() {
  const navigate = useNavigate()

  const [cargando, setCargando] = useState(true)
  const [errorCarga, setErrorCarga] = useState('')

  // --- Formulario de datos (nombre/email) ---
  const [nombre, setNombre] = useState('')
  const [email, setEmail] = useState('')
  const [passwordActualDatos, setPasswordActualDatos] = useState('')
  const [errorDatos, setErrorDatos] = useState('')
  const [exitoDatos, setExitoDatos] = useState('')
  const [loadingDatos, setLoadingDatos] = useState(false)

  // --- Formulario de contraseña ---
  const [passwordActual, setPasswordActual] = useState('')
  const [passwordNueva, setPasswordNueva] = useState('')
  const [confirmarPasswordNueva, setConfirmarPasswordNueva] = useState('')
  const [errorPassword, setErrorPassword] = useState('')
  const [exitoPassword, setExitoPassword] = useState('')
  const [loadingPassword, setLoadingPassword] = useState(false)

  useEffect(() => {
    const cargarPerfil = async () => {
      try {
        const data = await obtenerPerfil()
        setNombre(data.nombre)
        setEmail(data.email)
      } catch (err) {
        setErrorCarga(err.message || 'No se pudo cargar el perfil')
      } finally {
        setCargando(false)
      }
    }
    cargarPerfil()
  }, [])

  const handleSubmitDatos = async (e) => {
    e.preventDefault()

    if (!nombre.trim() || !email.trim() || !passwordActualDatos.trim()) {
      setErrorDatos('Completá todos los campos')
      return
    }

    setErrorDatos('')
    setExitoDatos('')
    setLoadingDatos(true)

    try {
      await actualizarPerfil({
        nombre: nombre.trim(),
        email: email.trim(),
        passwordActual: passwordActualDatos,
      })
      setExitoDatos('Datos actualizados correctamente')
      setPasswordActualDatos('')
    } catch (err) {
      setErrorDatos(err.message || 'No se pudo actualizar el perfil')
    } finally {
      setLoadingDatos(false)
    }
  }

  const handleSubmitPassword = async (e) => {
    e.preventDefault()

    if (!passwordActual.trim() || !passwordNueva.trim() || !confirmarPasswordNueva.trim()) {
      setErrorPassword('Completá todos los campos')
      return
    }

    if (passwordNueva !== confirmarPasswordNueva) {
      setErrorPassword('Las contraseñas nuevas no coinciden')
      return
    }

    if (passwordNueva.length < 6) {
      setErrorPassword('La nueva contraseña debe tener al menos 6 caracteres')
      return
    }

    setErrorPassword('')
    setExitoPassword('')
    setLoadingPassword(true)

    try {
      await cambiarPassword({ passwordActual, passwordNueva })
      setExitoPassword('Contraseña actualizada correctamente')
      setPasswordActual('')
      setPasswordNueva('')
      setConfirmarPasswordNueva('')
    } catch (err) {
      setErrorPassword(err.message || 'No se pudo cambiar la contraseña')
    } finally {
      setLoadingPassword(false)
    }
  }

  if (cargando) {
    return (
      <div className="profile-container">
        <p>Cargando perfil...</p>
      </div>
    )
  }

  if (errorCarga) {
    return (
      <div className="profile-container">
        <p className="profile-error">⚠ {errorCarga}</p>
      </div>
    )
  }

  return (
    <div className="profile-container">
      <header className="profile-header">
        <h1>Mi perfil</h1>
        <button className="back-button" onClick={() => navigate('/')}>
          ← Volver
        </button>
      </header>

      {/* --- Datos del perfil --- */}
      <section className="profile-section">
        <h2>Datos personales</h2>
        <form onSubmit={handleSubmitDatos} className="profile-form">
          <div className="form-group">
            <label htmlFor="nombre">Nombre</label>
            <input
              type="text"
              id="nombre"
              value={nombre}
              disabled={loadingDatos}
              onChange={(e) => {
                setNombre(e.target.value)
                if (errorDatos) setErrorDatos('')
              }}
            />
          </div>

          <div className="form-group">
            <label htmlFor="email">Correo electrónico</label>
            <input
              type="email"
              id="email"
              value={email}
              disabled={loadingDatos}
              onChange={(e) => {
                setEmail(e.target.value)
                if (errorDatos) setErrorDatos('')
              }}
            />
          </div>

          <div className="form-group">
            <label htmlFor="passwordActualDatos">Confirmá tu contraseña actual para guardar cambios</label>
            <input
              type="password"
              id="passwordActualDatos"
              placeholder="••••••••"
              value={passwordActualDatos}
              disabled={loadingDatos}
              onChange={(e) => {
                setPasswordActualDatos(e.target.value)
                if (errorDatos) setErrorDatos('')
              }}
            />
          </div>

          {errorDatos && <p className="profile-error">⚠ {errorDatos}</p>}
          {exitoDatos && <p className="profile-success">✓ {exitoDatos}</p>}

          <button type="submit" className="profile-submit-btn" disabled={loadingDatos}>
            {loadingDatos ? 'Guardando...' : 'Guardar cambios'}
          </button>
        </form>
      </section>

      {/* --- Cambio de contraseña --- */}
      <section className="profile-section">
        <h2>Cambiar contraseña</h2>
        <form onSubmit={handleSubmitPassword} className="profile-form">
          <div className="form-group">
            <label htmlFor="passwordActual">Contraseña actual</label>
            <input
              type="password"
              id="passwordActual"
              placeholder="••••••••"
              value={passwordActual}
              disabled={loadingPassword}
              onChange={(e) => {
                setPasswordActual(e.target.value)
                if (errorPassword) setErrorPassword('')
              }}
            />
          </div>

          <div className="form-group">
            <label htmlFor="passwordNueva">Nueva contraseña</label>
            <input
              type="password"
              id="passwordNueva"
              placeholder="Mínimo 6 caracteres"
              value={passwordNueva}
              disabled={loadingPassword}
              onChange={(e) => {
                setPasswordNueva(e.target.value)
                if (errorPassword) setErrorPassword('')
              }}
            />
          </div>

          <div className="form-group">
            <label htmlFor="confirmarPasswordNueva">Confirmar nueva contraseña</label>
            <input
              type="password"
              id="confirmarPasswordNueva"
              placeholder="Repetí la nueva contraseña"
              value={confirmarPasswordNueva}
              disabled={loadingPassword}
              onChange={(e) => {
                setConfirmarPasswordNueva(e.target.value)
                if (errorPassword) setErrorPassword('')
              }}
            />
          </div>

          {errorPassword && <p className="profile-error">⚠ {errorPassword}</p>}
          {exitoPassword && <p className="profile-success">✓ {exitoPassword}</p>}

          <button type="submit" className="profile-submit-btn" disabled={loadingPassword}>
            {loadingPassword ? 'Actualizando...' : 'Cambiar contraseña'}
          </button>
        </form>
      </section>
    </div>
  )
}

export default Profile