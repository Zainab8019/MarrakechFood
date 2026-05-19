import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate, Link } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import RestaurantList from './pages/RestaurantList';
import RestaurantDetail from './pages/RestaurantDetail';
import Cart from './pages/Cart';
import OrderTracking from './pages/OrderTracking';
import AdminRestaurantForm from './pages/AdminRestaurantForm';
import AdminPlatForm from './pages/AdminPlatForm';
import LivreurDashboard from './pages/LivreurDashboard';
import QRScanner from './pages/QRScanner';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [userRole, setUserRole] = useState(null);

  useEffect(() => {
    const clientId = localStorage.getItem('clientId');
    const role = localStorage.getItem('role');
    setIsAuthenticated(!!clientId);
    setUserRole(role);
  }, []);

  const handleLogout = () => {
    localStorage.clear();
    setIsAuthenticated(false);
    setUserRole(null);
    window.location.href = '/login';
  };

  const navStyle = {
    backgroundColor: '#FF6B35',
    padding: '10px 20px',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    color: 'white'
  };

  const linkStyle = {
    color: 'white',
    textDecoration: 'none',
    marginLeft: 20,
    cursor: 'pointer'
  };

  return (
    <BrowserRouter>
      {isAuthenticated && (
        <div style={navStyle}>
          <div style={{ fontWeight: 'bold', fontSize: 20 }}>
            🍽️ Marrakech Food
          </div>
          <div>
            <Link to="/restaurants" style={linkStyle}>🏠 Accueil</Link>
            <Link to="/cart" style={linkStyle}>🛒 Panier</Link>
            
            {userRole === 'ADMIN' && (
              <>
                <Link to="/admin/restaurant/add" style={linkStyle}>➕ Ajouter restaurant</Link>
                <Link to="/admin/restaurants" style={linkStyle}>📋 Gérer restaurants</Link>
              </>
            )}
            
            {userRole === 'LIVREUR' && (
              <>
                <Link to="/livreur/dashboard" style={linkStyle}>🚚 Commandes à livrer</Link>
              </>
            )}
            
            <button onClick={handleLogout} style={{ ...linkStyle, background: 'none', border: 'none' }}>
              🚪 Déconnexion
            </button>
          </div>
        </div>
      )}

      <Routes>
        <Route path="/login" element={<Login setIsAuthenticated={setIsAuthenticated} setUserRole={setUserRole} />} />
        <Route path="/register" element={<Register />} />
        <Route path="/" element={<Navigate to="/login" />} />
        <Route path="/restaurants" element={<RestaurantList />} />
        <Route path="/restaurant/:id" element={<RestaurantDetail />} />
        <Route path="/cart" element={<Cart />} />
        <Route path="/order-tracking/:id" element={<OrderTracking />} />
        
        <Route path="/admin/restaurant/add" element={<AdminRestaurantForm />} />
        <Route path="/admin/restaurant/edit/:id" element={<AdminRestaurantForm />} />
        <Route path="/admin/restaurants" element={<RestaurantList isAdmin={true} />} />
        <Route path="/admin/plat/add/:restaurantId" element={<AdminPlatForm />} />
        <Route path="/admin/plat/edit/:platId/restaurant/:restaurantId" element={<AdminPlatForm />} />
        
        <Route path="/livreur/dashboard" element={<LivreurDashboard />} />
        <Route path="/livreur/scan/:commandeId" element={<QRScanner />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;