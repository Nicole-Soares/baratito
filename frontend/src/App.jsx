import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Home from './pages/home/Home'
import Cart from './pages/cart/Cart'
import Login from './pages/login/Login'
import Profile from './pages/profile/Profile'
import Notifications from './pages/notifications/Notifications'
import Register from './pages/register/Register'
import Favorites from './pages/favorites/Favorites'
import { CartProvider } from './context/CartContext'
import { SearchProvider } from './context/SearchContext'
import { AuthProvider } from './context/AuthContext'
import { FavoritosProvider } from './context/FavoritosContext'


function App() {
  return (

<AuthProvider>
   <SearchProvider>
    <FavoritosProvider>
    <CartProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/carrito" element={<Cart />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/perfil" element={<Profile />} />
          <Route path="/notificaciones" element={<Notifications />} />
          <Route path="/favoritos" element={<Favorites />} />
        </Routes>
      </BrowserRouter>
    </CartProvider>
    </FavoritosProvider>
    </SearchProvider>
    </AuthProvider>
  )
}

export default App
