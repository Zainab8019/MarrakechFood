import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { restaurantAPI } from '../services/api';

function RestaurantDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [restaurant, setRestaurant] = useState(null);
  const [plats, setPlats] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchData();
  }, [id]);

  const fetchData = async () => {
    try {
      const [restoRes, platsRes] = await Promise.all([
        restaurantAPI.getById(id),
        restaurantAPI.getPlats(id)
      ]);
      setRestaurant(restoRes.data);
      setPlats(platsRes.data);
    } catch (err) {
      setError('Erreur chargement du restaurant');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const ajouterAuPanier = (plat) => {
    const panier = JSON.parse(localStorage.getItem('panier') || '[]');
    const existing = panier.find(p => p.id === plat.id);
    
    if (existing) {
      existing.quantite++;
    } else {
      panier.push({ ...plat, quantite: 1, restaurantId: parseInt(id) });
    }
    
    localStorage.setItem('panier', JSON.stringify(panier));
    alert(`${plat.nom} ajouté au panier !`);
  };

  const handleDeletePlat = async (platId, platNom) => {
    if (window.confirm(`Supprimer le plat "${platNom}" ?`)) {
      try {
        await restaurantAPI.deletePlat(platId);
        alert('Plat supprimé');
        fetchData();
      } catch (err) {
        alert('Erreur suppression');
      }
    }
  };

  if (loading) return <div style={{ textAlign: 'center', marginTop: 50 }}>Chargement...</div>;
  if (error) return <div style={{ color: 'red', textAlign: 'center', marginTop: 50 }}>{error}</div>;

  return (
    <div style={{ maxWidth: 800, margin: '0 auto', padding: 20 }}>
      <button onClick={() => navigate('/restaurants')} style={{ marginBottom: 20, padding: '8px 16px', cursor: 'pointer' }}>
        ← Retour
      </button>
      
      <h1 style={{ color: '#FF6B35' }}>{restaurant?.nom}</h1>
      <p>📍 {restaurant?.adresse}</p>
      <p>🍳 {restaurant?.typeCuisine}</p>
      
      <button 
        onClick={() => navigate(`/admin/plat/add/${id}`)} 
        style={{ marginBottom: 20, marginLeft: 20, padding: 10, backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: 5, cursor: 'pointer' }}
      >
        ➕ Ajouter un plat
      </button>
      
      <h2 style={{ marginTop: 30 }}>Notre carte</h2>
      
      <div style={{ display: 'flex', flexDirection: 'column', gap: 15 }}>
        {plats.map((plat) => (
          <div key={plat.id} style={{ border: '1px solid #ddd', borderRadius: 8, padding: 15 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <h3 style={{ margin: '0 0 5px 0' }}>{plat.nom}</h3>
                <p style={{ margin: 0, color: '#666' }}>{plat.description}</p>
                <p style={{ margin: '10px 0 0 0', fontWeight: 'bold', color: '#FF6B35' }}>{plat.prix} DH</p>
              </div>
              <button 
                onClick={() => ajouterAuPanier(plat)}
                style={{ 
                  padding: '10px 20px', 
                  backgroundColor: '#FF6B35', 
                  color: 'white', 
                  border: 'none', 
                  borderRadius: 5,
                  cursor: 'pointer'
                }}
              >
                Ajouter
              </button>
            </div>
            <div style={{ display: 'flex', gap: 10, marginTop: 10 }}>
              <button 
                onClick={() => navigate(`/admin/plat/edit/${plat.id}/restaurant/${id}`)} 
                style={{ padding: '5px 10px', cursor: 'pointer' }}
              >
                ✏️ Modifier
              </button>
              <button 
                onClick={() => handleDeletePlat(plat.id, plat.nom)} 
                style={{ padding: '5px 10px', cursor: 'pointer', backgroundColor: '#dc3545', color: 'white', border: 'none' }}
              >
                🗑️ Supprimer
              </button>
            </div>
          </div>
        ))}
      </div>
      
      <button 
        onClick={() => navigate('/cart')}
        style={{ 
          position: 'fixed', 
          bottom: 20, 
          right: 20, 
          padding: '15px 25px',
          backgroundColor: '#FF6B35', 
          color: 'white',
          border: 'none',
          borderRadius: 50,
          fontSize: 16,
          cursor: 'pointer',
          boxShadow: '0 4px 8px rgba(0,0,0,0.2)'
        }}
      >
        🛒 Voir le panier
      </button>
    </div>
  );
}

export default RestaurantDetail;