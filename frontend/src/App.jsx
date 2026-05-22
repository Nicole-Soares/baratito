import { BrowserRouter, Routes, Route } from 'react-router-dom'

import Home from './Componentes/Home'
import Cart from './Componentes/Cart'

function App() {

  return (
    <BrowserRouter>

      <Routes>

        <Route path="/" element={<Home />} />
        <Route path="/carrito" element={<Cart />} />

      </Routes>

    </BrowserRouter>
  )
}

export default App