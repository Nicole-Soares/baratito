import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import './Notifications.css'

function Notifications() {
  const navigate = useNavigate()

  // Lista mockeada de alertas lógicas para Baratito
  const [notificaciones, setNotificaciones] = useState([
    {
      id: 1,
      tipo: 'baja_precio',
      titulo: '📉 ¡Bajó de precio!',
      mensaje: 'La Yerba Mate Chamigo 500 Gr bajó un 10% en diaonline.',
      fecha: 'Hace 10 min',
      leida: false
    },
    {
      id: 2,
      tipo: 'stock',
      titulo: '⚠️ Alerta de Stock',
      mensaje: 'Pocas unidades disponibles del producto que guardaste.',
      fecha: 'Hace 2 horas',
      leida: true
    }
  ])

  const marcarComoLeida = (id) => {
    setNotificaciones(prev =>
      prev.map(notif => notif.id === id ? { ...notif, leida: true } : notif)
    )
  }

  return (
    <div className="notifications-container">
      <header className="notifications-header">
        <h1 className="notifications-title">Notificaciones</h1>
        <button className="back-button" onClick={() => navigate('/')}>
          ← Volver
        </button>
      </header>

      <div className="notifications-list">
        {notificaciones.length === 0 ? (
          <div className="empty-notifications">
            <p>No tenés ninguna notificación por ahora.</p>
          </div>
        ) : (
          notificaciones.map(notif => (
            <div
              key={notif.id}
              className={`notification-card ${!notif.leida ? 'unread' : ''}`}
              onClick={() => marcarComoLeida(notif.id)}
            >
              <div className="notification-content">
                <div className="notification-top-row">
                  <h3>{notif.titulo}</h3>
                  <span className="notification-time">{notif.fecha}</span>
                </div>
                <p>{notif.mensaje}</p>
              </div>
              {!notif.leida && <span className="unread-dot"></span>}
            </div>
          ))
        )}
      </div>
    </div>
  )
}

export default Notifications