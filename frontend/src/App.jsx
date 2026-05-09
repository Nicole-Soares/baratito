import { useState, useRef } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from './assets/vite.svg'
import heroImg from './assets/hero.png'
import './App.css'

function App() {
  const [query, setQuery] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [resultados, setResultados] = useState(null)
  const [busquedaActual, setBusquedaActual] = useState('')
  const inputRef = useRef(null)
 
  const handleSearch = async () => {
    // Validar campo vacío
    if (!query.trim()) {
      setError('Ingrese un producto')
      inputRef.current?.focus()
      return
    }
 
    setError('')
 
    // Limpiar resultados anteriores antes de mostrar los nuevos
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
 
          {/* Mensaje de error campo vacío */}
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
              {resultados.resultados.map((producto, i) => (
                <li key={i} className="product-card">
                  <div className="product-info">
                    <span className="product-category">
                      {producto.categoria}
                    </span>
                    <h3 className="product-name">
                      {producto.nombre}
                    </h3>
                    <p>{producto.supermercado}</p>
                    <strong>
                      ${producto.precio.toLocaleString('es-AR')}
                    </strong>
                  </div>
                </li>
              ))}
            </ul>
          </div>
        )}
      </main>
    </div>
  )
}
 
export default App
