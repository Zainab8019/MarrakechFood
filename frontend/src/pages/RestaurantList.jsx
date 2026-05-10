import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { restaurantAPI } from '../services/api';

function RestaurantList() {
  const [restaurants, setRestaurants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    fetchRestaurants();
  }, []);

  const fetchRestaurants = async () => {
    try {
      const response = await restaurantAPI.getAll();
      setRestaurants(response.data);
    } catch (err) {
      setError('Erreur chargement des restaurants');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div style={{ textAlign: 'center', marginTop: 50 }}>Chargement...</div>;
  if (error) return <div style={{ color: 'red', textAlign: 'center', marginTop: 50 }}>{error}</div>;

  return (
    <div style={{ maxWidth: 1200, margin: '0 auto', padding: 20 }}>
      <h1 style={{ color: '#FF6B35' }}>🍽️ Marrakech Food</h1>
      <h2>Nos restaurants</h2>
      
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: 20 }}>
        {restaurants.map((restaurant) => (
          <div 
            key={restaurant.id} 
            onClick={() => navigate(`/restaurant/${restaurant.id}`)}
            style={{ 
              border: '1px solid #ddd', 
              borderRadius: 10, 
              padding: 15,
              cursor: 'pointer',
              transition: '0.3s',
              backgroundColor: 'white'
            }}
            onMouseEnter={(e) => e.currentTarget.style.boxShadow = '0 4px 8px rgba(0,0,0,0.1)'}
            onMouseLeave={(e) => e.currentTarget.style.boxShadow = 'none'}
          >
            <h3 style={{ margin: '0 0 10px 0', color: '#FF6B35' }}>{restaurant.nom}</h3>
            <p style={{ margin: '5px 0', color: '#666' }}>📍 {restaurant.adresse}</p>
            <p style={{ margin: '5px 0', color: '#666' }}>🍳 {restaurant.typeCuisine || 'Cuisine variée'}</p>
            <p style={{ margin: '5px 0', color: '#888' }}>⭐ {restaurant.note || '4.5'} / 5</p>
          </div>
        ))}
      </div>
    </div>
  );
}

export default RestaurantList;