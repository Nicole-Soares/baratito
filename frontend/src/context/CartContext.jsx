import { createContext, useContext, useState, useEffect, useRef } from 'react'
import { obtenerCarrito, agregarAlCarrito, quitarDelCarrito } from '../service/cartService/cartService'
import { useAuth } from './AuthContext'

const CartContext = createContext()

const carritoVacio = { productos: [], total: 0 }

export function CartProvider({ children }) {
  const [cart, setCart] = useState(carritoVacio)
  const [popup, setPopup] = useState(null)
  const { isLoggedIn } = useAuth()
  const popupTimeoutRef = useRef(null)


// ---- Mensaje

  const mostrarPopup = (texto, tipo) => {
    if (popupTimeoutRef.current) {
      clearTimeout(popupTimeoutRef.current)
    }
    setPopup({ texto, tipo })
    popupTimeoutRef.current = setTimeout(() => setPopup(null), 1500)
  }

// -- obtenemos de la base de datos los productos agregados anteriormente
  const fetchCart = async () => {
    try {
      const data = await obtenerCarrito()
      setCart(data)
    } catch (err) {
      console.error('Error cargando carrito:', err.message)
    }
  }

// -- lo primero que se ejecuta en entrar a la pagina
  useEffect(() => {
    if (isLoggedIn) {
      fetchCart()
    } else {
      setCart(carritoVacio)
    }
  }, [isLoggedIn])


  // ---- los productos se guardan en memoria ----

  const agregarProductoLocal = (producto) => {
    setCart(prev => {
      const existe = prev.productos.find(p => p.productoId === producto.id)

      const nuevosProductos = existe
        ? prev.productos.map(p =>
            p.productoId === producto.id ? { ...p, cantidad: p.cantidad + 1 } : p
          )
        : [
            ...prev.productos,
            {
              productoId: producto.id,
              nombre: producto.nombre,
              source: producto.source,
              precio: producto.precio,
              imagen: producto.imagen,
              cantidad: 1,
            },
          ]

      const total = nuevosProductos.reduce((acc, p) => acc + p.precio * p.cantidad, 0)
      return { productos: nuevosProductos, total }
    })
  }

  const quitarProductoLocal = (productoId) => {
    setCart(prev => {
      const item = prev.productos.find(p => p.productoId === productoId)
      if (!item) return prev

      const nuevaCantidad = item.cantidad - 1

      const nuevosProductos = nuevaCantidad <= 0
        ? prev.productos.filter(p => p.productoId !== productoId)
        : prev.productos.map(p =>
            p.productoId === productoId ? { ...p, cantidad: nuevaCantidad } : p
          )

      const total = nuevosProductos.reduce((acc, p) => acc + p.precio * p.cantidad, 0)
      return { productos: nuevosProductos, total }
    })
  }

  // ---- si no esta logueado, se agregan los productos en memoria, sino en la base de datos ----

  const agregarProducto = async (producto) => {
    if (!isLoggedIn) {
      agregarProductoLocal(producto)
      mostrarPopup(`✓ ${producto.nombre} agregado`, 'agregado')
      return
    }

    try {
      const data = await agregarAlCarrito(producto.id)
      setCart(data)
      mostrarPopup(`✓ ${producto.nombre} agregado`, 'agregado')
    } catch (err) {
      mostrarPopup(err.message || 'No se pudo agregar al carrito', 'error')
    }
  }

  const quitarProducto = async (productoId, nombreProducto) => {
    if (!isLoggedIn) {
      quitarProductoLocal(productoId)
      mostrarPopup(`✗ ${nombreProducto} eliminado`, 'quitado')
      return
    }

    try {
      const data = await quitarDelCarrito(productoId)
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