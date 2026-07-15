import { useNavigate } from 'react-router-dom'
import { useFavoritos } from '../../context/FavoritosContext'
import { useAuth } from '../../context/AuthContext'
import './Favorites.css'

function Favorites() {
  const navigate = useNavigate()
  const { user, isLoggedIn } = useAuth()
  const { favoritos, toggleFavorito, popup } = useFavoritos()

  if (!isLoggedIn) {
    return (
      <div className="favorites-container">
        <header className="favorites-header">
          <h1 className="favorites-title">Mis Favoritos</h1>
          <button className="back-button" onClick={() => navigate('/')}>
            ← Volver
          </button>
        </header>
        <div className="empty-favorites">
          <span className="empty-favorites-icon">🔒</span>
          <h2>Iniciá sesión para ver tus favoritos</h2>
          <button className="login-btn" onClick={() => navigate('/login')}>
            Iniciar sesión
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className="favorites-container">
      {popup && (
        <div className={`popup-favoritos ${popup.tipo}`}>
          {popup.texto}
        </div>
      )}

      <header className="favorites-header">
        <h1 className="favorites-title">Mis Favoritos</h1>
        <button className="back-button" onClick={() => navigate('/')}>
          ← Volver
        </button>
      </header>

      <div className="favorites-list">
        {favoritos.length === 0 ? (
          <div className="empty-favorites">
            <span className="empty-favorites-icon">❤️</span>
            <h2>No tenés favoritos todavía</h2>
            <p>Buscá productos y tocá el corazón para guardarlos acá</p>
          </div>
        ) : (
          favoritos.map(producto => (
            <div key={producto.id} className="favorite-card">
              <div className="favorite-left">
                <img
                  src={producto.imagen}
                  alt={producto.nombre}
                  className="favorite-image"
                />
                <div className="favorite-info">
                  <h3>{producto.nombre}</h3>
                  <p className="favorite-source">{producto.source}</p>
                  <p className="favorite-price">
                    ${producto.precio.toLocaleString('es-AR', { minimumFractionDigits: 2 })}
                  </p>
                </div>
              </div>
              <div className="favorite-actions">
                <a
                  href={producto.link}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="favorite-go-link"
                >
                  Ver en tienda
                </a>
                <button
                  className="favorite-remove-btn"
                  onClick={() => toggleFavorito(producto.id, producto.nombre)}
                  title="Quitar de favoritos"
                >
                  ✗ Quitar
                </button>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  )
}

export default Favorites
