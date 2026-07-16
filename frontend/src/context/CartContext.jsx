import { createContext, useContext, useState, useEffect } from 'react'
import { get, post, del, ApiError } from '../api/apiClient'

const CartContext = createContext()

export function CartProvider({ children }) {
  const [cart, setCart] = useState({ productos: [], total: 0 })
  const [popup, setPopup] = useState(null)

  const mostrarPopup = (texto, tipo) => {
    setPopup({ texto, tipo })
    setTimeout(() => setPopup(null), 1500)
  }

  const fetchCart = async () => {
    try {
      const { data } = await get('/api/carrito')
      setCart(data)
    } catch (err) {
      console.error('Error cargando carrito:', err.message)
    }
  }

  useEffect(() => {
    fetchCart()
  }, [])

  const agregarProducto = async (productoId, nombreProducto) => {
    try {
      const { data } = await post(`/api/carrito/${productoId}`)
      setCart(data)
      mostrarPopup(`✓ ${nombreProducto} agregado`, 'agregado')
    } catch (err) {
      mostrarPopup(err.message || 'No se pudo agregar al carrito', 'error')
    }
  }

  const quitarProducto = async (productoId, nombreProducto) => {
    try {
      const { data } = await del(`/api/carrito/${productoId}`)
      setCart(data)
      mostrarPopup(`✗ ${nombreProducto} eliminado`, 'quitado')
    } catch (err) {
      mostrarPopup(err.message || 'No se pudo quitar del carrito', 'error')
    }
  }

  return (
    <CartContext.Provider value={{ cart, popup, agregarProducto, quitarProducto, mostrarPopup }}>
      {children}
    </CartContext.Provider>
  )
}

export const useCart = () => useContext(CartContext)
