import React, { useState } from 'react'
import { useCart } from '../../context/CartContext'
import { useFavoritos } from '../../context/FavoritosContext'
import HistorialModal from '../historialmodal/HistorialModal'

function CardProducto({ producto }) {
  const { cart, agregarProducto, quitarProducto } = useCart()
  const { esFavorito, toggleFavorito } = useFavoritos()
  const [mostrarHistorial, setMostrarHistorial] = useState(false)

  const favorito = esFavorito(producto.id)

  const item = cart?.productos?.find(
    p => p.nombre === producto.nombre && p.imagen === producto.imagen
  )

  return (
    <>
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

          {/* Botón de historial debajo de la info */}
          <button
            className="historial-btn"
            onClick={() => setMostrarHistorial(true)}
          >
            📈 Ver historial de precios
          </button>
        </div>

        {/* Acciones */}
        <div className="product-actions-container">
          <div className="product-actions-group">
            {/* Botón de favorito */}
            <button
              className={`favorite-btn ${favorito ? 'active' : ''}`}
              onClick={() => toggleFavorito(producto.id, producto.nombre)}
              title={favorito ? 'Quitar de favoritos' : 'Agregar a favoritos'}
            >
              {favorito ? '❤️' : '🤍'}
            </button>

            {/* Logo del Supermercado */}
            <img
              className="supermarket-logo"
              src={`/logos/${producto.source.toLowerCase()}.png`}
              alt={producto.source}
            />

            {/* Bloque de Botones Alineados */}
            <div className="product-buttons-row">
              {item ? (
                <div className="quantity-controls">
                  <button onClick={() => quitarProducto(producto.id, producto.nombre)}>-</button>
                  <span className="quantity-badge">{item.cantidad}</span>
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

              {/* Botón de enlace externo */}
              <a
                href={producto.link}
                target="_blank"
                rel="noopener noreferrer"
                className="product-go-link"
              >
                Ver en tienda
              </a>
            </div>
          </div>
        </div>
      </li>

      {/* Modal de historial — se monta fuera del <li> para evitar problemas de z-index */}
      {mostrarHistorial && (
        <HistorialModal
          producto={producto}
          onClose={() => setMostrarHistorial(false)}
        />
      )}
    </>
  )
}

export default CardProducto