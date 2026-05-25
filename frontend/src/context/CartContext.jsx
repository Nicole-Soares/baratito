import { createContext, useContext, useState, useEffect } from 'react'

const CartContext = createContext()
export const useCart = () => useContext(CartContext)

export const CartProvider = ({ children }) => {
  const [cart, setCart] = useState({ productos: [], total: 0 })

  // Cargar carrito inicial desde backend
  useEffect(() => {
    fetch('http://localhost:8080/api/carrito')
      .then(res => res.json())
      .then(data => setCart(data))
      .catch(() => setCart({ productos: [], total: 0 }))
  }, [])

  const agregarProducto = async (productoId, nombreProducto) => {
    await fetch(`http://localhost:8080/api/carrito/${productoId}`, { method: 'POST' })
    const res = await fetch('http://localhost:8080/api/carrito')
    const data = await res.json()
    setCart(data)
    return `✓ ${nombreProducto} se agregó al carrito`
  }

 const quitarProducto = async (productoId, nombreProducto) => {
   try {
     const res = await fetch(`http://localhost:8080/api/carrito/${productoId}`, { method: 'DELETE' })
     if (!res.ok) throw new Error('Error al decrementar')

     const data = await res.json()
     setCart(data)
     return `✗ ${nombreProducto} se quitó del carrito`
   } catch (err) {
     return 'No se pudo quitar del carrito'
   }
 }


  return (
    <CartContext.Provider value={{ cart, agregarProducto, quitarProducto }}>
      {children}
    </CartContext.Provider>
  )
}
