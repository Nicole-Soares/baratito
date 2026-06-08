import { createContext, useContext, useState, useEffect } from "react"

const AuthContext = createContext()

export function AuthProvider({ children }) {
  const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem("token"))

  // 1. Inicializamos el estado del usuario leyendo de localStorage si existe
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem("user")
    return savedUser ? JSON.parse(savedUser) : null
  })

  useEffect(() => {
    const checkToken = () => {
      setIsLoggedIn(!!localStorage.getItem("token"))
      // Sincronizamos también el usuario por si cambia el storage
      const savedUser = localStorage.getItem("user")
      setUser(savedUser ? JSON.parse(savedUser) : null)
    }
    window.addEventListener("storage", checkToken)
    return () => window.removeEventListener("storage", checkToken)
  }, [])

  // 2. Modificamos login para recibir el token y el objeto con los datos del usuario
  const login = (token, userData) => {
    localStorage.setItem("token", token)

    if (userData) {
      localStorage.setItem("user", JSON.stringify(userData))
      setUser(userData)
    }

    setIsLoggedIn(true)
  }

  // 3. Al cerrar sesión limpiamos todo junto
  const logout = () => {
    localStorage.removeItem("token")
    localStorage.removeItem("user")
    setUser(null)
    setIsLoggedIn(false)
  }

  // 4. Agregamos 'user' al value para que lo pueda usar la página de perfil
  return (
    <AuthContext.Provider value={{ isLoggedIn, user, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)