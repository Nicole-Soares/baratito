import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Home from './pages/home/Home'
import Cart from './pages/cart/Cart'
import Login from './pages/login/Login'
import Profile from './pages/profile/Profile'
import Notifications from './pages/notifications/Notifications'
import Register from './pages/register/Register'
import Favorites from './pages/favorites/Favorites'
import RequireAuth from './components/RequireAuth'
import { CartProvider } from './context/CartContext'
import { SearchProvider } from './context/SearchContext'
import { AuthProvider } from './context/AuthContext'
import { FavoritosProvider } from './context/FavoritosContext'
import { NotificacionesProvider } from './context/NotificacionesContext'

function App() {
  return (
    <AuthProvider>
      <SearchProvider>
        <FavoritosProvider>
          <NotificacionesProvider>
            <CartProvider>
              <BrowserRouter>
                <Routes>
                  <Route path="/" element={<Home />} />
                  <Route path="/login" element={<Login />} />
                  <Route path="/register" element={<Register />} />

                  {/* Carrito: NO requiere sesión, funciona en modo anónimo (memoria) */}
                  <Route path="/carrito" element={<Cart />} />

                  {/* Estas sí requieren cuenta */}
                  <Route path="/perfil" element={
                    <RequireAuth><Profile /></RequireAuth>
                  } />
                  <Route path="/notificaciones" element={
                    <RequireAuth><Notifications /></RequireAuth>
                  } />
                  <Route path="/favoritos" element={
                    <RequireAuth><Favorites /></RequireAuth>
                  } />
                </Routes>
              </BrowserRouter>
            </CartProvider>
          </NotificacionesProvider>
        </FavoritosProvider>
      </SearchProvider>
    </AuthProvider>
  )
}

export default App