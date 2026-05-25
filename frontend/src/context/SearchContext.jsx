// src/context/SearchContext.js
import { createContext, useContext, useState } from 'react'

const SearchContext = createContext()
export const useSearch = () => useContext(SearchContext)

export const SearchProvider = ({ children }) => {
  const [resultados, setResultados] = useState(null)
  const [busquedaActual, setBusquedaActual] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  return (
    <SearchContext.Provider value={{
      resultados, setResultados,
      busquedaActual, setBusquedaActual,
      error, setError,
      loading, setLoading
    }}>
      {children}
    </SearchContext.Provider>
  )
}
