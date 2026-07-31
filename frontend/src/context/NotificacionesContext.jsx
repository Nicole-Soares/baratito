import { createContext, useContext, useState, useEffect } from 'react'
import { obtenerNotificaciones, contarNoLeidas, marcarTodasComoLeidas } from '../service/notificacionesService/notificacionesService'
import { useAuth } from './AuthContext'

const NotificacionesContext = createContext()

export function NotificacionesProvider({ children }) {
  const { isLoggedIn } = useAuth()
  const [notificaciones, setNotificaciones] = useState([])
  const [noLeidas, setNoLeidas] = useState(0)

  const fetchContador = async () => {
    if (!isLoggedIn) {
      setNoLeidas(0)
      return
    }
    try {
      const data = await contarNoLeidas()
      setNoLeidas(data.count)
    } catch (err) {
      console.error('Error cargando contador de notificaciones:', err.message)
    }
  }

  useEffect(() => {
    fetchContador()
  }, [isLoggedIn])

  const fetchNotificaciones = async () => {
    if (!isLoggedIn) {
      setNotificaciones([])
      return
    }
    try {
      const data = await obtenerNotificaciones()
      setNotificaciones(Array.isArray(data) ? data : [])
    } catch (err) {
      console.error('Error cargando notificaciones:', err.message)
    }
  }

  const marcarComoLeidas = async () => {
    try {
      await marcarTodasComoLeidas()
      setNoLeidas(0)
    } catch (err) {
      console.error('Error marcando notificaciones como leídas:', err.message)
    }
  }

  return (
    <NotificacionesContext.Provider value={{
      notificaciones,
      noLeidas,
      fetchNotificaciones,
      fetchContador,
      marcarComoLeidas,
    }}>
      {children}
    </NotificacionesContext.Provider>
  )
}

export const useNotificaciones = () => useContext(NotificacionesContext)