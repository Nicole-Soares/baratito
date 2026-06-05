import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import './Profile.css'

function Profile() {
  const navigate = useNavigate()
  const { user } = useAuth()

  // por si es null
  const usuarioInfo = user || {
    id: "---",
    nombre: "Invitado",
    email: "sin-email@baratito.com",
    miembroDesde: "N/A"
  }

  return (
    <div className="profile-container">
      <header className="profile-header">
        <h1 className="profile-title">Mi Perfil</h1>
        <button className="back-button" onClick={() => navigate('/')}>
          ← Volver
        </button>
      </header>

      <div className="profile-card">
        <div className="profile-avatar-row">
          <div className="profile-avatar">👤</div>
          <div className="profile-welcome">
            <h2>¡Hola, {usuarioInfo.nombre}!</h2>
            <p>Usuario de Baratito</p>
          </div>
        </div>

        <hr className="profile-divider" />

        <div className="profile-details">
          {usuarioInfo.id && (
            <div className="detail-group">
              <label>ID de Usuario</label>
              <p>#{usuarioInfo.id}</p>
            </div>
          )}

          <div className="detail-group">
            <label>Nombre Completo</label>
            <p>{usuarioInfo.nombre}</p>
          </div>

          <div className="detail-group">
            <label>Correo Electrónico</label>
            <p>{usuarioInfo.email}</p>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Profile