import React from 'react'
import { useCart } from '../../context/CartContext' // 👈 Asegurate de que la ruta a tu contexto sea la correcta

function CardProducto({ producto }) {
  // Consumimos todo lo que necesitamos directamente del estado global
  const { cart, agregarProducto, quitarProducto } = useCart()

  // Buscamos si el ítem actual ya está agregado en el carrito
  const item = cart?.productos?.find(
    p => p.nombre === producto.nombre && p.imagen === producto.imagen
  )

  return (
    <li className="product-card">
      {/* Imagen del Producto */}
      <div className="product-img">
        {producto.imagen ? (
          <img src={producto.imagen} alt={producto.nombre} />
        ) : (
          <span className="product-img-placeholder">🛒</span>
        )}
      </div>

      {/* Información del Producto */}
      <div className="product-info">
        <span className="product-name">{producto.nombre}</span>

        <div className="product-price-main">
          ${producto.precio.toLocaleString('es-AR', { minimumFractionDigits: 2 })}
          {producto.precioLista > producto.precio && (
            <span className="product-price-lista">
              ${producto.precioLista.toLocaleString('es-AR', { minimumFractionDigits: 2 })}
            </span>
          )}
        </div>

        <div className="product-price-unit">
          (${producto.precio.toLocaleString('es-AR', { minimumFractionDigits: 2 })} x UN)
        </div>

        <div className="product-updated">
          Actualizado el: {producto.actualizado}
        </div>
      </div>

      {/* Acciones (Logo Supermercado y Botones del Carrito) */}
      <div className="product-actions-container">
        <div className="product-actions-group">
          <img
            className="supermarket-logo"
            src={`/logos/${producto.source.toLowerCase()}.png`}
            alt={producto.source}
          />

          {item ? (
            <div className="quantity-controls" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <button onClick={() => quitarProducto(producto.id, producto.nombre)}>-</button>
              <span style={{ color: 'white' }}>{item.cantidad}</span>
              <button onClick={() => agregarProducto(producto.id, producto.nombre)}>+</button>
            </div>
          ) : (
            <button
              className="product-add-btn"
              onClick={() => agregarProducto(producto.id, producto.nombre)}
            >
              +
            </button>
          )}
        </div>
      </div>
    </li>
  )
}

export default CardProducto