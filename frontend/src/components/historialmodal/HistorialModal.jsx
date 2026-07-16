import { useState, useEffect, useCallback } from 'react'
import { get } from '../../api/apiClient'
import './HistorialModal.css'

const RANGOS = [7, 15, 30]
const COLORES_SOURCE = { jumbo: '#009b3a', carrefour: '#004a97', diaonline: '#e30613', masonline: '#ff6600', farmacity: '#7c5cfc' }
const COLORES_FALLBACK = ['#7c5cfc', '#1db954', '#ff6600', '#e30613', '#004a97']

function LineChart({ datos, sources }) {
  const W = 580, H = 220
  const PAD = { top: 20, right: 16, bottom: 40, left: 72 }
  const innerW = W - PAD.left - PAD.right
  const innerH = H - PAD.top - PAD.bottom

  if (!datos.length || !sources.length) return null

  const allPrices = datos.flatMap(d => sources.map(s => d[s]).filter(v => v != null))
  const minP = Math.min(...allPrices)
  const maxP = Math.max(...allPrices)

  const margen = (maxP - minP) * 0.5 || 50
  const minScaled = minP - margen
  const maxScaled = maxP + margen
  const rangoP = maxScaled - minScaled

  const xPos = i => PAD.left + (i / (datos.length - 1 || 1)) * innerW
  const yPos = v => PAD.top + innerH - ((v - minScaled) / rangoP) * innerH
  const yTicks = [minScaled, minScaled + rangoP/3, minScaled + (rangoP/3)*2, maxScaled]

  return (
    <svg viewBox={`0 0 ${W} ${H}`} style={{ width: '100%', height: 'auto', overflow: 'visible' }}>
      {yTicks.map((v, i) => (
        <g key={i}>
          <line x1={PAD.left} y1={yPos(v)} x2={PAD.left + innerW} y2={yPos(v)} stroke="#2a2a3a" strokeWidth="1" />
          <text x={PAD.left - 8} y={yPos(v) + 4} textAnchor="end" fill="#888899" fontSize="10">
            ${Math.round(v).toLocaleString('es-AR')}
          </text>
        </g>
      ))}

     {sources.map((source, si) => {
       const color = COLORES_SOURCE[source.toLowerCase()] ?? COLORES_FALLBACK[si % COLORES_FALLBACK.length]
       const puntos = datos.map((d, i) => d[source] != null ? [xPos(i), yPos(d[source])] : null).filter(Boolean)

       if (puntos.length === 0) return null

       // Si solo hay un punto, dibujamos un círculo en lugar de un path
       if (puntos.length === 1) {
         return (
           <circle
             key={source} cx={puntos[0][0]} cy={puntos[0][1]} r="4"
             fill={color}
           />
         )
       }

       // Si hay 2 o más, dibujamos la línea
       const d = puntos.map((p, i) => `${i === 0 ? 'M' : 'L'}${p[0]},${p[1]}`).join(' ')
       return <path key={source} d={d} fill="none" stroke={color} strokeWidth="3" />
     })}
    </svg>
  )
}

export default function HistorialModal({ producto, onClose }) {
  const [dias, setDias] = useState(30)
  const [datos, setDatos] = useState([])
  const [loading, setLoading] = useState(true)

  const fetchHistorial = useCallback(async () => {
    setLoading(true)
    try {
      const { data: raw } = await get(`/api/productos/historial?link=${encodeURIComponent(producto.link)}&dias=${dias}`)

      const porFecha = {}
      raw.forEach(({ fecha, source, precio }) => {
        if (!porFecha[fecha]) porFecha[fecha] = { fecha }
        porFecha[fecha][source] = parseFloat(precio)
      })
      setDatos(Object.values(porFecha).sort((a, b) => a.fecha.localeCompare(b.fecha)))
    } catch (e) { console.error(e) } finally { setLoading(false) }
  }, [producto.link, dias])

  useEffect(() => { fetchHistorial() }, [fetchHistorial])

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-container" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <h2 className="modal-nombre">{producto.nombre}</h2>
          <button className="modal-close" onClick={onClose}>✕</button>
        </div>
        <div className="modal-rangos">
          {RANGOS.map(r => (
            <button key={r} className={`rango-btn ${dias === r ? 'rango-btn--activo' : ''}`} onClick={() => setDias(r)}>
              {r} días
            </button>
          ))}
        </div>
        <div className="modal-body">
          {loading ? <div>Cargando...</div> : datos.length > 0 ?
            <LineChart datos={datos} sources={Object.keys(datos[0]).filter(k => k !== 'fecha')} /> : <div>Sin datos</div>}
        </div>
      </div>
    </div>
  )
}