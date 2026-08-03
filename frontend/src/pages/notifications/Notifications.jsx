import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useNotificaciones } from '../../context/NotificacionesContext'
import './Notifications.css'

function formatearFecha(fechaISO) {
  const fecha = new Date(fechaISO)
  return fecha.toLocaleDateString('es-AR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

function Notifications() {
  const navigate = useNavigate()
  const { notificaciones, fetchNotificaciones, marcarComoLeidas } = useNotificaciones()

  useEffect(() => {
    fetchNotificaciones()
    marcarComoLeidas()
  }, [])

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
            <span className="empty-notifications-icon">🔔</span>
            <h2>No tenés notificaciones</h2>
            <p>Te vamos a avisar cuando bajen de precio los productos que marcaste como favoritos</p>
          </div>
        ) : (
          notificaciones.map((noti) => {
            const descuento = Math.round(
              ((noti.precioAnterior - noti.precioNuevo) / noti.precioAnterior) * 100
            )

            return (
              <div key={noti.id} className={`notification-card ${!noti.leida ? 'no-leida' : ''}`}>
                <div className="notification-img">
                  {noti.productoImagen ? (
                    <img src={noti.productoImagen} alt={noti.productoNombre} />
                  ) : (
                    <span className="notification-img-placeholder">🛒</span>
                  )}
                </div>

                <div className="notification-info">
                  <h3>{noti.productoNombre}</h3>
                  <p className="notification-precio">
                    <span className="precio-anterior">
                      ${noti.precioAnterior.toLocaleString('es-AR')}
                    </span>
                    <span className="precio-nuevo">
                      ${noti.precioNuevo.toLocaleString('es-AR')}
                    </span>
                    <span className="notification-descuento">-{descuento}%</span>
                  </p>
                  <p className="notification-fecha">{formatearFecha(noti.creada)}</p>
                </div>
              </div>
            )
          })
        )}
      </div>
    </div>
  )
}

export default Notifications