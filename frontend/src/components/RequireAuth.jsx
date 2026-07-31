import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

function RequireAuth({ children }) {
  const { isLoggedIn, checkingSession } = useAuth()
  const location = useLocation()

  if (checkingSession) {
    return (
      <div className="auth-checking">
        <div className="loading-spinner" />
      </div>
    )
  }

  if (!isLoggedIn) {
    return <Navigate to="/login" state={{ from: location }} replace />
  }

  return children
}

export default RequireAuth