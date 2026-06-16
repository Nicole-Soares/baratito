import { createContext, useContext, useState, useEffect } from 'react'

const CartContext = createContext()

export function CartProvider({ children }) {
  const [cart, setCart] = useState({ productos: [], total: 0 })

  // estado del mensaje
  const [popup, setPopup] = useState(null) // Guardará { texto: '...', tipo: '...' }

  // Función auxiliar para mostrar el cartel con tiempo de vencimiento
  const mostrarPopup = (texto, tipo) => {
    setPopup({ texto, tipo })
    setTimeout(() => setPopup(null), 1500) // Desaparece a los 1.5 segundos
  }

  const fetchCart = async () => {
    try {
      const res = await fetch('http://localhost:8080/api/carrito')
      const data = await res.json()
      setCart(data)
    } catch (err) {
      console.error("Error cargando carrito:", err)
    }
  }

  useEffect(() => {
    fetchCart()
  }, [])

  const agregarProducto = async (productoId, nombreProducto) => {
    try {
      const res = await fetch(`http://localhost:8080/api/carrito/${productoId}`, { method: 'POST' })
      const data = await res.json()
      setCart(data)


      mostrarPopup(`✓ ${nombreProducto} agregado`, 'agregado')
    } catch {
      mostrarPopup('No se pudo agregar al carrito', 'error')
    }
  }

  const quitarProducto = async (productoId, nombreProducto) => {
    try {
      const res = await fetch(`http://localhost:8080/api/carrito/${productoId}`, { method: 'DELETE' })
      const data = await res.json()
      setCart(data)


      mostrarPopup(`✗ ${nombreProducto} eliminado`, 'quitado')
    } catch {
      mostrarPopup('No se pudo quitar del carrito', 'error')
    }
  }

<<<<<<< Updated upstream
=======
  const limpiarCarrito = () => {
  setCart({
    productos: [],
    total: 0
  })
  }

>>>>>>> Stashed changes
  return (

    <CartContext.Provider value={{ cart, popup, agregarProducto, quitarProducto, mostrarPopup }}>
      {children}
    </CartContext.Provider>
  )
}

export const useCart = () => useContext(CartContext)