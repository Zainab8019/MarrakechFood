import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import RestaurantList from './pages/RestaurantList';
import RestaurantDetail from './pages/RestaurantDetail';
import Cart from './pages/Cart';
import OrderTracking from './pages/OrderTracking';
import LivreurDashboard from './pages/LivreurDashboard';
import QRScanner from './pages/QRScanner';
function App() {
  const isAuthenticated = !!localStorage.getItem('clientId');

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/" element={<Navigate to="/login" />} />
      <Route path="/restaurants" element={<RestaurantList />} />
<Route path="/restaurant/:id" element={<RestaurantDetail />} /> 
     <Route path="/cart" element={<Cart />} />
<Route path="/order-tracking/:id" element={<OrderTracking />} /> 
      <Route path="/livreur/dashboard" element={<LivreurDashboard />} />
<Route path="/livreur/scan/:commandeId" element={<QRScanner />} /> 
      </Routes>
    </BrowserRouter>
  );
}
export default App;