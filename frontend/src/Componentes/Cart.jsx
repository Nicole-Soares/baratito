import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'  
import './Cart.css'

function Cart() {

  const [cart, setCart] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const navigate = useNavigate()

  useEffect(() => {

    fetch('http://localhost:8080/api/carrito')
      .then(res => res.json())
      .then(data => setCart(data))
      .catch(() => setError('No se pudo cargar el carrito'))
      .finally(() => setLoading(false))

  }, [])

  if (loading) {
    return <p>Cargando carrito...</p>
  }

  if (error) {
    return <p>{error}</p>
  }

  return (
    <div className="cart-container">

      <h1 className="cart-title">Mi carrito</h1>

      <button className="back-button"
              onClick={() => navigate('/')} >
        ← Volver
      </button>

      <div className="cart-list">
        {cart.productos.length === 0 ? (
          <div className="empty-cart">
            <span className="empty-cart-icon">
              🛒
            </span>
            <h2>
              El carrito está vacío
            </h2>
            <p>
              Agregá productos para comenzar
            </p>
          </div>
        ) : (
          cart.productos.map(producto => (
            <div key={producto.id}
                 className="cart-card">
              <div className="cart-left">
                <img src={producto.imagen}
                     alt={producto.nombre}
                     className="cart-image" />
                <div className="cart-info">
                  <h3>{producto.nombre}</h3>
                  <p className="cart-source">
                    {producto.source}
                  </p>
                  <p className="cart-price">
                    ${producto.precio}
                  </p>
                  <p className="cart-quantity">
                    Cantidad: {producto.cantidad}
                  </p>
                  <p className="cart-subtotal">
                    Subtotal: ${(producto.precio * producto.cantidad).toLocaleString('es-AR')}
                  </p>
                </div>
              </div>
            </div>
          ))
        )}
      </div>
      {cart.productos.length > 0 && (
        <div className="cart-total">
          <span>Total del carrito</span>
          <span> ${cart.total.toLocaleString('es-AR')}</span>
        </div>
      )}

      <h2>
        Total: ${cart.total.toLocaleString('es-AR')}
      </h2>

    </div>
    
  )
}

export default Cart