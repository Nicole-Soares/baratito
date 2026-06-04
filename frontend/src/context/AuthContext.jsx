import { createContext, useContext, useState, useEffect } from "react"

const AuthContext = createContext()

export function AuthProvider({ children }) {
  const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem("token"))

  useEffect(() => {
    const checkToken = () => setIsLoggedIn(!!localStorage.getItem("token"))
    window.addEventListener("storage", checkToken)
    return () => window.removeEventListener("storage", checkToken)
  }, [])

  const login = (token) => {
    localStorage.setItem("token", token)
    setIsLoggedIn(true)
  }

  const logout = () => {
    localStorage.removeItem("token")
    setIsLoggedIn(false)
  }

  return (
    <AuthContext.Provider value={{ isLoggedIn, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
