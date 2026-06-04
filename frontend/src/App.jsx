import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Home from './pages/home/Home'
import Cart from './pages/cart/Cart'
import Login from './pages/login/Login'
import Register from './pages/register/Register'
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
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
        </Routes>
      </BrowserRouter>
    </CartProvider>
    </SearchProvider>
  )
}

export default App
