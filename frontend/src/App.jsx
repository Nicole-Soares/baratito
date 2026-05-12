import { useState, useRef } from 'react'
import './App.css'

function App() {
  const [query, setQuery] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [resultados, setResultados] = useState(null)
  const [busquedaActual, setBusquedaActual] = useState('')
  const inputRef = useRef(null)

  const formatearFecha = (fechaIso) => {
    const fecha = new Date(fechaIso)

    return fecha.toLocaleDateString('es-AR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    })
  }

  const estaDesactualizado = (fechaIso) => {
    const fecha = new Date(fechaIso)
    const ahora = new Date()

    const diferenciaMs = ahora - fecha

    const diferenciaDias = diferenciaMs / (1000 * 60 * 60 * 24)

    return diferenciaDias > 3
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
        <h1 className="app-logo">Baratito</h1>
        <p className="app-subtitle">Compará precios entre supermercados</p>
      </header>

      <main className="app-main">
        {/* Barra de búsqueda */}
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
            <button
              className="search-btn"
              onClick={handleSearch}
              disabled={loading}
            >
              {loading ? 'Buscando...' : 'Buscar'}
            </button>
          </div>

          {error && (
            <p className="search-error">⚠ {error}</p>
          )}
        </div>

        {/* Estado de carga */}
        {loading && (
          <div className="loading-state">
            <div className="loading-spinner" />
            <p>Buscando precios para <strong>"{busquedaActual}"</strong>...</p>
          </div>
        )}

        {/* Resultados */}
        {!loading && resultados !== null && (
          <div className="results-section">
            <p className="results-meta">
              {resultados.total > 0
                ? `${resultados.total} resultado${resultados.total !== 1 ? 's' : ''} para "${resultados.busqueda}"`
                : `No se encontraron productos para "${resultados.busqueda}"`
              }
            </p>

            {resultados.resultados.length === 0 && (
              <div className="no-results">
                <span className="no-results-icon">🛒</span>
                <p>Probá con otro nombre o revisá la ortografía</p>
              </div>
            )}

            <ul className="results-list">
              {resultados.resultados.map((producto, i) => {
                return (
                  <li key={i} className="product-card">
                    {/* Imagen placeholder */}
                    <div className="product-img">
                      {producto.imagen
                        ? <img src={producto.imagen} alt={producto.nombre} />
                        : <span className="product-img-placeholder">🛒</span>
                      }
                    </div>

                    {/* Info central */}
                    <div className="product-info">
                      <div className="product-header">
                        <a className="product-name">{producto.nombre}</a>
                        <span className="product-super-tag">{producto.supermercado}</span>
                      </div>
                      <div className="product-price-main">
                        ${producto.precio.toLocaleString('es-AR', { minimumFractionDigits: 2 })}
                      </div>
                      <div className="product-price-unit">
                        ($ {producto.precio.toLocaleString('es-AR', { minimumFractionDigits: 2 })} x UN)
                      </div>
                      <div
                        className={`product-updated ${
                          estaDesactualizado(producto.actualizado)
                            ? 'product-updated-warning'
                            : ''
                        }`}
                      >
                          Actualizado el: {formatearFecha(producto.actualizado)}
                      </div>
                    </div>

                    {/* Botón + */}
                    <button className="product-add-btn">+</button>
                  </li>
                )
              })}
            </ul>
          </div>
        )}
      </main>
    </div>
  )
}

export default App