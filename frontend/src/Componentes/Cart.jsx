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

      <h1>Mi carrito</h1>

      <button className="back-button"
              onClick={() => navigate('/')} >
        ← Volver
      </button>

      <div className="cart-list">

        {cart.productos.map(producto => (

          <div
            key={producto.id}
            className="cart-card"
          >

            <img
              src={producto.imagen}
              alt={producto.nombre}
              width="120"
            />

            <div>

              <h3>{producto.nombre}</h3>

              <p>
                Supermercado: {producto.source}
              </p>

              <p>
                Precio: ${producto.precio}
              </p>

              <p>
                Cantidad: {producto.cantidad}
              </p>

              <p>
                Subtotal: $
                {(producto.precio * producto.cantidad).toLocaleString('es-AR')}
              </p>

            </div>

          </div>

        ))}

      </div>

      <h2>
        Total: ${cart.total.toLocaleString('es-AR')}
      </h2>

    </div>
  )
}

export default Cart