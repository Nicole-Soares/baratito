import { createContext, useContext, useState, useEffect } from 'react'
import { useAuth } from './AuthContext'
import { obtenerFavoritos, agregarFavorito, quitarFavorito } from '../service/favoritos/favoritoService';

const FavoritosContext = createContext()

export function FavoritosProvider({ children }) {
  const { isLoggedIn } = useAuth()
  const [favoritos, setFavoritos] = useState([])
  const [popup, setPopup] = useState(null)

  const mostrarPopup = (texto, tipo) => {
    setPopup({ texto, tipo })
    setTimeout(() => setPopup(null), 1500)
  }

  const fetchFavoritos = async () => {
    if (!isLoggedIn) {
      setFavoritos([])
      return
    }
    try {
      const data = await obtenerFavoritos() // Asumiendo que apiFetch ya devuelve el JSON
      setFavoritos(Array.isArray(data) ? data : [])
    } catch (err) {
      console.error('Error cargando favoritos:', err)
      setFavoritos([])
    }
  }

  useEffect(() => {
    fetchFavoritos()
  }, [isLoggedIn])

  const esFavorito = (productoId) => {
    return favoritos.some(f => f.id === productoId)
  }

  const toggleFavorito = async (producto) => {
    if (!isLoggedIn) {
      mostrarPopup('Iniciá sesión para usar favoritos', 'error')
      return
    }

    const { id, nombre } = producto

    try {
      if (esFavorito(id)) {
        await quitarFavorito(id)
        setFavoritos(prev => prev.filter(f => f.id !== id))
        mostrarPopup(`✗ ${nombre} quitado de favoritos`, 'quitado')
      } else {
        await agregarFavorito(id)
        setFavoritos(prev => [...prev, producto]) // Actualización instantánea
        mostrarPopup(`✓ ${nombre} agregado a favoritos`, 'agregado')
      }
    } catch (err) {
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