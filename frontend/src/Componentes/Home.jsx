import { useState, useRef } from 'react'
import Cart from './Cart'
import { useNavigate } from 'react-router-dom'
import './Home.css'

function Home() {
  const [query, setQuery] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [resultados, setResultados] = useState(null)
  const [busquedaActual, setBusquedaActual] = useState('')
  const inputRef = useRef(null)
  const navigate = useNavigate()
  
  const estaDesactualizado = (fechaIso) => {
    const fecha = new Date(fechaIso)
    const ahora = new Date()
    const diferenciaMs = ahora - fecha
    return (diferenciaMs / (1000 * 60 * 60 * 24)) > 3
  }

  const handleSearch = async () => {
    if (!query.trim()) {
      setError('Ingrese un producto')
      inputRef.current?.focus()
      return
    }
    setError('')
    setResultados(null)
    setLoading(true)
    setBusquedaActual(query.trim())

    try {
      const res = await fetch(
        `http://localhost:8080/api/productos/buscar?nombre=${encodeURIComponent(query.trim())}`
      )
      const data = await res.json()
      if (!res.ok) {
        setError(data.error || 'Ocurrió un error en la búsqueda')
        setResultados(null)
        return
      }
      setResultados(data)
    } catch (error) {
      setError('No se pudo conectar con el servidor')
      setResultados(null)
    } finally {
      setLoading(false)
    }
  }

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') handleSearch()
  }

  const handleInputChange = (e) => {
    setQuery(e.target.value)
    if (error) setError('')
  }

  return (
    <div className="app">
      <header className="app-header">

        <button className="cart-button"
                onClick={() => navigate('/carrito')} >
          🛒 Carrito
        </button>
        <h1 className="app-logo">
          Baratito
        </h1>
        <p className="app-subtitle">
          Compará precios entre supermercados
        </p>
      </header>

      <main className="app-main">
        <div className="search-section">
          <div className={`search-bar ${error ? 'search-bar--error' : ''}`}>
            <span className="search-icon">🔍</span>
            <input
              ref={inputRef}
              type="text"
              className="search-input"
              placeholder="Buscar producto..."
              value={query}
              onChange={handleInputChange}
              onKeyDown={handleKeyDown}
            />
            <button className="search-btn" onClick={handleSearch} disabled={loading}>
              {loading ? 'Buscando...' : 'Buscar'}
            </button>
          </div>
          {error && <p className="search-error">⚠ {error}</p>}
        </div>

        {loading && (
          <div className="loading-state">
            <div className="loading-spinner" />
            <p>Buscando precios para <strong>"{busquedaActual}"</strong>...</p>
          </div>
        )}

        {!loading && resultados !== null && (
          <div className="results-section">
            {(() => {
              const disponibles = resultados.resultados.filter(p => p.disponibilidad)
              return (
                <>
                  <p className="results-meta">
                    {disponibles.length > 0
                      ? `${disponibles.length} resultado${disponibles.length !== 1 ? 's' : ''} para "${resultados.busqueda}"`
                      : `No se encontraron productos para "${resultados.busqueda}"`
                    }
                  </p>
                  <ul className="results-list">
                    {disponibles.map((producto, i) => (
                <li key={i} className="product-card">
                  <div className="product-img">
                    {producto.imagen ? (
                      <img src={producto.imagen} alt={producto.nombre} />
                    ) : (
                      <span className="product-img-placeholder">🛒</span>
                    )}
                  </div>

                  <div className="product-info">
                    <a className="product-name">{producto.nombre}</a>
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

                  <div className="product-actions-container">
                    <div className="product-actions-group">
                      <img
                        className="supermarket-logo"
                        src={`/logos/${producto.source.toLowerCase()}.png`}
                        alt={producto.source}
                      />
                      <button className="product-add-btn">+</button>
                    </div>
                  </div>
                </li>
                    ))}
                  </ul>
                </>
              )
            })()}
          </div>
        )}
      </main>
    </div>
  )
}

export default Home