import { useState, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import { useSearch } from '../../context/SearchContext'
import { useCart } from '../../context/CartContext'
import './Home.css'
import CardProducto from '../../components/cardproduct/CardProduct'

function Home() {
  const [query, setQuery] = useState('')
  const [agregados, setAgregados] = useState({})
  const [sugerencias, setSugerencias] = useState([])
  const { cart, popup, agregarProducto, quitarProducto } = useCart()
  const { resultados, setResultados, busquedaActual, setBusquedaActual, error, setError, loading, setLoading } = useSearch()
  const [mensajeCarrito, setMensajeCarrito] = useState('')
  const inputRef = useRef(null)
  const navigate = useNavigate()


  const totalProductos = cart.productos.reduce((acc, p) => acc + p.cantidad, 0)

    const handleIncrement = async (productoId, nombreProducto) => {
        agregarProducto(productoId, nombreProducto)

    }

  const handleDecrement = async (productoId, nombreProducto) => {
        quitarProducto(productoId, nombreProducto)
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
    setSugerencias([])

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
    } catch {
      setError('No se pudo conectar con el servidor')
      setResultados(null)
    } finally {
      setLoading(false)
    }
  }

  const obtenerSugerencias = async (texto) => {
    try {
      const res = await fetch(
        `http://localhost:8080/api/productos/sugerencias?query=${encodeURIComponent(texto)}`
      )

      const data = await res.json()

      setSugerencias(data)
    } catch (error) {
      console.error(error)
      setSugerencias([])
    }
  }

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') handleSearch()
  }

  const handleInputChange = (e) => {
    const texto = e.target.value

    setQuery(texto)

    if (error) setError('')

    if (texto.trim().length >= 2) {
      obtenerSugerencias(texto)
    } else {
      setSugerencias([])
    }
  }

const handleAgregar = async (productoId, nombreProducto) => {
  try {
    await fetch(`http://localhost:8080/api/carrito/${productoId}`, {
      method: 'POST'
    })
    setAgregados(prev => ({ ...prev, [productoId]: true }))
    setMensajeCarrito(`✓ ${nombreProducto} se agregó al carrito`)

    setTimeout(() => {
      setAgregados(prev => ({ ...prev, [productoId]: false }))
      setMensajeCarrito('')
    }, 2000) // dura 2 segundos
  } catch {
    setError('No se pudo agregar al carrito')
  }
}

  return (
    <div className="app">
   {popup && (
           <div className={`popup-carrito ${popup.tipo}`}>
             {popup.texto}
           </div>
         )}
     <header className="app-header">
       <div className="branding">
         <h1 className="app-logo">Baratito</h1>
         <p className="app-subtitle">Compará precios entre supermercados</p>
       </div>
       <button className="cart-button" onClick={() => navigate('/carrito')}>
         🛒 Carrito
         {totalProductos > 0 && (
             <span className="cart-badge">{totalProductos}</span>
           )}
       </button>
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
          {sugerencias.length > 0 && (
            <ul className="suggestions-list">
              {sugerencias.map((sugerencia) => (
                <li
                  key={sugerencia}
                  className="suggestion-item"
                  onClick={() => {
                    setQuery(sugerencia)
                    setSugerencias([])
                  }}
                >
                  {sugerencia}
                </li>
              ))}
            </ul>
          )}

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
                 {disponibles.map((producto) => (
                     <CardProducto
                       key={producto.id}
                       producto={producto}
                       cart={cart}
                     />
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
