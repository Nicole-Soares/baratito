import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Home from './Componentes/Home'
import Cart from './Componentes/Cart'
import { CartProvider } from './context/CartContext'
import { SearchProvider } from './context/SearchContext'

function App() {
  return (

   <SearchProvider>
    <CartProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/carrito" element={<Cart />} />
        </Routes>
      </BrowserRouter>
    </CartProvider>
    </SearchProvider>
  )
}

export default App
