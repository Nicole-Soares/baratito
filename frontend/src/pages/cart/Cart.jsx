import { useNavigate } from 'react-router-dom'
import { useCart } from '../../context/CartContext'
import { useEffect, useState } from "react"
import './Cart.css'

function Cart() {
  const navigate = useNavigate()
  const { cart, agregarProducto, quitarProducto, popup, limpiarCarrito } = useCart()
  const [historial, setHistorial] = useState([])

  useEffect(() => {

  const cargarHistorial = async () => {

    const token = localStorage.getItem("token")

    if (!token) return

    try {

      const res = await fetch(
        "http://localhost:8080/api/historial",
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      )

      const data = await res.json()

      setHistorial(data)

    } catch (error) {
      console.error(error)
    }
  }

  cargarHistorial()

  }, [])

  // Los datos del carrito están en el context
  if (!cart || !cart.productos) return <p>Cargando carrito...</p>

  return (
    <div className="cart-container">
      {/* POPUP GLOBAL */}
      {popup && (
        <div className={`popup-carrito ${popup.tipo}`}>
          {popup.texto}
        </div>
      )}

      {/* Encabezado alineado con las tarjetas */}
      <header className="cart-header">
        <h1 className="cart-title">Mi carrito</h1>
        <button className="back-button" onClick={() => navigate('/')}>
          ← Volver
        </button>
      </header>

      <div className="cart-list">
        {cart.productos.length === 0 ? (
          <div className="empty-cart">
            <span className="empty-cart-icon">🛒</span>
            <h2>El carrito está vacío</h2>
            <p>Agregá productos para comenzar</p>
          </div>
        ) : (
          cart.productos.map(producto => (
            <div key={producto.id} className="cart-card">
              <div className="cart-left">
                <img
                  src={producto.imagen}
                  alt={producto.nombre}
                  className="cart-image"
                />
                <div className="cart-info">
                  <h3>{producto.nombre}</h3>
                  <p className="cart-source">{producto.source}</p>
                  <p className="cart-price">${producto.precio}</p>
                  <p className="cart-quantity">Cantidad: {producto.cantidad}</p>
                  <p className="cart-subtotal">
                    Subtotal: ${(producto.precio * producto.cantidad).toLocaleString('es-AR')}
                  </p>
                </div>
              </div>
              <button
                className="cart-remove-btn"
                onClick={() => quitarProducto(producto.productoId, producto.nombre)}
                title="Quitar del carrito"
              >
                🗑 Quitar
              </button>
            </div>
          ))
        )}
      </div>

      {cart.productos.length > 0 && (
        <div className="cart-total">
          <span>Total del carrito</span>
          <span>${cart.total.toLocaleString('es-AR')}</span>
        </div>
      )}
      {historial.length > 0 && (
        <div className="historial-section">
        
          <h2 className="historial-title">
            Productos agregados anteriormente
          </h2>

          <div className="historial-scroll">

            {historial.map(producto => (
            
              <div key={producto.id}
                   className="historial-card">
                <img
                  src={producto.imagen}
                  alt={producto.nombre}
                  className="historial-image"
                />
                <p className="historial-name">
                  {producto.nombre}
                </p>
                <p className="historial-price">
                  ${producto.precio}
                </p>
                <button className="historial-add-btn"
                        onClick={() => agregarProducto(
                                        producto.productoId,
                                        producto.nombre)}>
                  ➕ Volver a agregar
                </button>
              </div>

            ))}

          </div>
          
        </div>
      )}      

    </div>
  )
}

export default Cart