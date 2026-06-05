import { useState, useEffect, useCallback } from 'react'
import './HistorialModal.css'

const RANGOS = [7, 15, 30]

const COLORES_SOURCE = {
  jumbo:      '#009b3a',
  carrefour:  '#004a97',
  diaonline:  '#e30613',
  masonline:  '#ff6600',
  farmacity:  '#7c5cfc',
}
const COLORES_FALLBACK = ['#7c5cfc', '#1db954', '#ff6600', '#e30613', '#004a97']
const COLOR_DEFAULT = '#888899'

// ─── Gráfico de líneas en SVG puro ───────────────────────────────────────────

function LineChart({ datos, sources }) {
  const [tooltip, setTooltip] = useState(null)

  const W = 580, H = 220
  const PAD = { top: 12, right: 16, bottom: 40, left: 72 }
  const innerW = W - PAD.left - PAD.right
  const innerH = H - PAD.top - PAD.bottom

  if (!datos.length || !sources.length) return null

  // Rango de valores
  const allPrices = datos.flatMap(d => sources.map(s => d[s]).filter(Boolean))
  const minP = Math.min(...allPrices)
  const maxP = Math.max(...allPrices)
  const rangoP = maxP - minP || 1

  // Helpers de escala
  const xPos = i => PAD.left + (i / (datos.length - 1 || 1)) * innerW
  const yPos = v => PAD.top + innerH - ((v - minP) / rangoP) * innerH

  // Ticks del eje Y (4 valores)
  const yTicks = Array.from({ length: 4 }, (_, i) =>
    minP + (rangoP / 3) * i
  )

  // Ticks del eje X — mostrar máx 6 fechas
  const step = Math.max(1, Math.floor(datos.length / 6))
  const xTicks = datos
    .map((d, i) => ({ i, label: d.fecha.slice(5) }))
    .filter((_, i) => i % step === 0 || i === datos.length - 1)

  const getColor = (source, idx) =>
    COLORES_SOURCE[source] ?? COLORES_FALLBACK[idx % COLORES_FALLBACK.length]

  return (
    <div style={{ position: 'relative' }}>
      <svg
        viewBox={`0 0 ${W} ${H}`}
        style={{ width: '100%', height: 'auto', overflow: 'visible' }}
        onMouseLeave={() => setTooltip(null)}
      >
        {/* Grid horizontal */}
        {yTicks.map((v, i) => (
          <g key={i}>
            <line
              x1={PAD.left} y1={yPos(v)}
              x2={PAD.left + innerW} y2={yPos(v)}
              stroke="#2a2a3a" strokeWidth="1"
            />
            <text
              x={PAD.left - 8} y={yPos(v) + 4}
              textAnchor="end" fill="#888899" fontSize="10"
            >
              ${Math.round(v).toLocaleString('es-AR')}
            </text>
          </g>
        ))}

        {/* Eje X — fechas */}
        {xTicks.map(({ i, label }) => (
          <text
            key={i}
            x={xPos(i)} y={PAD.top + innerH + 16}
            textAnchor="middle" fill="#888899" fontSize="10"
          >
            {label}
          </text>
        ))}

        {/* Líneas por supermercado */}
        {sources.map((source, si) => {
          const color = getColor(source, si)
          const puntos = datos
            .map((d, i) => d[source] != null ? [xPos(i), yPos(d[source])] : null)
            .filter(Boolean)

          if (puntos.length < 2) return null

          const d = puntos.map((p, i) => `${i === 0 ? 'M' : 'L'}${p[0]},${p[1]}`).join(' ')

          return (
            <g key={source}>
              <path d={d} fill="none" stroke={color} strokeWidth="2" strokeLinejoin="round" />
              {puntos.map(([cx, cy], i) => (
                <circle
                  key={i} cx={cx} cy={cy} r="3"
                  fill={color} stroke="#1a1a24" strokeWidth="1.5"
                  style={{ cursor: 'pointer' }}
                  onMouseEnter={(e) => {
                    const precio = datos[
                      datos.findIndex((_, di) => datos[di][source] != null &&
                        Math.abs(xPos(di) - cx) < 1)
                    ]?.[source]
                    setTooltip({ x: cx, y: cy, source, precio, fecha: datos.find((d, di) => Math.abs(xPos(di) - cx) < 1)?.fecha })
                  }}
                />
              ))}
            </g>
          )
        })}

        {/* Tooltip */}
        {tooltip && (
          <g>
            <rect
              x={Math.min(tooltip.x + 8, W - 140)}
              y={tooltip.y - 30}
              width="130" height="40"
              rx="6" fill="#1a1a24" stroke="#2a2a3a"
            />
            <text
              x={Math.min(tooltip.x + 14, W - 134)}
              y={tooltip.y - 14}
              fill="#f0f0f5" fontSize="11" fontWeight="600"
            >
              {tooltip.source}
            </text>
            <text
              x={Math.min(tooltip.x + 14, W - 134)}
              y={tooltip.y + 2}
              fill="#c4b5fd" fontSize="11"
            >
              ${tooltip.precio?.toLocaleString('es-AR', { minimumFractionDigits: 2 })}
            </text>
          </g>
        )}
      </svg>

      {/* Leyenda */}
      <div style={{ display: 'flex', gap: 16, flexWrap: 'wrap', marginTop: 8, justifyContent: 'center' }}>
        {sources.map((source, si) => (
          <div key={source} style={{ display: 'flex', alignItems: 'center', gap: 6, fontSize: 12, color: '#f0f0f5' }}>
            <span style={{
              display: 'inline-block', width: 20, height: 3,
              background: getColor(source, si), borderRadius: 2
            }} />
            {source}
          </div>
        ))}
      </div>
    </div>
  )
}

// ─── Modal principal ──────────────────────────────────────────────────────────

function HistorialModal({ producto, onClose }) {
  const [dias, setDias] = useState(30)
  const [datos, setDatos] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const fetchHistorial = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const res = await fetch(
        `http://localhost:8080/api/productos/historial?link=${encodeURIComponent(producto.link)}&dias=${dias}`
      )
      if (!res.ok) throw new Error('Error al obtener historial')
      const raw = await res.json()

      const porFecha = {}
      raw.forEach(({ fecha, source, precio }) => {
        if (!porFecha[fecha]) porFecha[fecha] = { fecha }
        porFecha[fecha][source] = precio
      })
      setDatos(Object.values(porFecha).sort((a, b) => a.fecha.localeCompare(b.fecha)))
    } catch {
      setError('No se pudo cargar el historial.')
    } finally {
      setLoading(false)
    }
  }, [producto.link, dias])

  useEffect(() => { fetchHistorial() }, [fetchHistorial])

  useEffect(() => {
    const handleKeyDown = (e) => { if (e.key === 'Escape') onClose() }
    document.addEventListener('keydown', handleKeyDown)
    return () => document.removeEventListener('keydown', handleKeyDown)
  }, [onClose])

  const sources = datos.length > 0
    ? Object.keys(datos[0]).filter(k => k !== 'fecha')
    : []

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-container" onClick={e => e.stopPropagation()}>

        <div className="modal-header">
          <div className="modal-titulo">
            <span className="modal-icono">📈</span>
            <div>
              <h2 className="modal-nombre">{producto.nombre}</h2>
              <p className="modal-subtitulo">Historial de precios</p>
            </div>
          </div>
          <button className="modal-close" onClick={onClose} aria-label="Cerrar">✕</button>
        </div>

        <div className="modal-rangos">
          {RANGOS.map(r => (
            <button
              key={r}
              className={`rango-btn ${dias === r ? 'rango-btn--activo' : ''}`}
              onClick={() => setDias(r)}
            >
              {r} días
            </button>
          ))}
        </div>

        <div className="modal-body">
          {loading && (
            <div className="modal-loading">
              <div className="modal-spinner" />
              <span>Cargando historial...</span>
            </div>
          )}
          {!loading && error && <p className="modal-error">{error}</p>}
          {!loading && !error && datos.length === 0 && (
            <p className="modal-vacio">
              No hay datos históricos para este producto todavía.<br />
              Los precios se acumulan con el uso de la aplicación.
            </p>
          )}
          {!loading && !error && datos.length > 0 && (
            <LineChart datos={datos} sources={sources} />
          )}
        </div>

      </div>
    </div>
  )
}

export default HistorialModal