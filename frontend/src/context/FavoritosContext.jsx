import { createContext, useContext, useState, useEffect } from 'react'
import { useAuth } from './AuthContext'

const FavoritosContext = createContext()

export function FavoritosProvider({ children }) {
  const { isLoggedIn, user } = useAuth()
  const [favoritos, setFavoritos] = useState([])
  const [popup, setPopup] = useState(null)

  const mostrarPopup = (texto, tipo) => {
    setPopup({ texto, tipo })
    setTimeout(() => setPopup(null), 1500)
  }

  const getToken = () => localStorage.getItem('token')

  const fetchFavoritos = async () => {
    if (!isLoggedIn) {
      setFavoritos([])
      return
    }
    try {
      const res = await fetch('http://localhost:8080/api/favoritos', {
        headers: { Authorization: `Bearer ${getToken()}` }
      })
      if (res.ok) {
        const data = await res.json()
        setFavoritos(data)
      }
    } catch (err) {
      console.error('Error cargando favoritos:', err)
    }
  }

  useEffect(() => {
    fetchFavoritos()
  }, [isLoggedIn])

  const esFavorito = (productoId) => {
    return favoritos.some(f => f.id === productoId)
  }

  const toggleFavorito = async (productoId, nombreProducto) => {
    if (!isLoggedIn) {
      mostrarPopup('Iniciá sesión para usar favoritos', 'error')
      return
    }

    try {
      if (esFavorito(productoId)) {
        const res = await fetch(`http://localhost:8080/api/favoritos/${productoId}`, {
          method: 'DELETE',
          headers: { Authorization: `Bearer ${getToken()}` }
        })
        if (res.ok) {
          setFavoritos(prev => prev.filter(f => f.id !== productoId))
          mostrarPopup(`✗ ${nombreProducto} quitado de favoritos`, 'quitado')
        }
      } else {
        const res = await fetch(`http://localhost:8080/api/favoritos/${productoId}`, {
          method: 'POST',
          headers: { Authorization: `Bearer ${getToken()}` }
        })
        if (res.ok) {
          await fetchFavoritos()
          mostrarPopup(`✓ ${nombreProducto} agregado a favoritos`, 'agregado')
        }
      }
    } catch {
      mostrarPopup('No se pudo actualizar favoritos', 'error')
    }
  }

  return (
    <FavoritosContext.Provider value={{ favoritos, popup, esFavorito, toggleFavorito }}>
      {children}
    </FavoritosContext.Provider>
  )
}

export const useFavoritos = () => useContext(FavoritosContext)
