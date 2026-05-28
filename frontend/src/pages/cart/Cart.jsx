import { useNavigate } from 'react-router-dom'
import { useCart } from '../../context/CartContext'
import './Cart.css'

function Cart() {
  const navigate = useNavigate()
  const { cart, quitarProducto, popup } = useCart()

  // los datos del carrito estan en el context
  if (!cart || !cart.productos) return <p>Cargando carrito...</p>

  return (
    <div className="cart-container">
      {/* POPUP GLOBAL */}
      {popup && (
        <div className={`popup-carrito ${popup.tipo}`}>
          {popup.texto}
        </div>
      )}

      <h1 className="cart-title">Mi carrito</h1>

      <button className="back-button" onClick={() => navigate('/')}>
        ← Volver
      </button>

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

    </div>
  )
}

export default Cart