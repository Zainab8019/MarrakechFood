import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { commandeAPI, restaurantAPI } from '../services/api';

function Cart() {
  const [cart, setCart] = useState([]);
  const [adresse, setAdresse] = useState('');
  const [loading, setLoading] = useState(false);
  const [restaurants, setRestaurants] = useState([]);
  const [selectedRestaurantId, setSelectedRestaurantId] = useState('');
  const [showRestaurantSelector, setShowRestaurantSelector] = useState(false);
  const navigate = useNavigate();
  const clientId = localStorage.getItem('clientId');

  useEffect(() => {
    loadCart();
    loadRestaurants();
  }, []);

  const loadCart = () => {
    const savedCart = JSON.parse(localStorage.getItem('panier') || '[]');
    setCart(savedCart);
  };

  const loadRestaurants = async () => {
    try {
      const response = await restaurantAPI.getAll();
      setRestaurants(response.data);
      
      // Si un restaurant est déjà enregistré dans localStorage, l'utiliser
      const savedRestaurantId = localStorage.getItem('currentRestaurantId');
      if (savedRestaurantId && response.data.some(r => r.id === parseInt(savedRestaurantId))) {
        setSelectedRestaurantId(savedRestaurantId);
      } else if (response.data.length > 0) {
        setSelectedRestaurantId(response.data[0].id.toString());
        localStorage.setItem('currentRestaurantId', response.data[0].id);
      } else {
        setShowRestaurantSelector(true);
      }
    } catch (err) {
      console.error('Erreur chargement restaurants:', err);
      setShowRestaurantSelector(true);
    }
  };

  const updateQuantite = (id, newQuantite) => {
    if (newQuantite < 1) {
      removeItem(id);
      return;
    }
    const newCart = cart.map(item => 
      item.id === id ? { ...item, quantite: newQuantite } : item
    );
    setCart(newCart);
    localStorage.setItem('panier', JSON.stringify(newCart));
  };

  const removeItem = (id) => {
    const newCart = cart.filter(item => item.id !== id);
    setCart(newCart);
    localStorage.setItem('panier', JSON.stringify(newCart));
  };

  const total = cart.reduce((sum, item) => sum + (item.prix * item.quantite), 0);

  const handleRestaurantChange = (e) => {
    const newId = e.target.value;
    setSelectedRestaurantId(newId);
    localStorage.setItem('currentRestaurantId', newId);
  };

  const handleCommander = async () => {
    if (!adresse) {
      alert('Veuillez entrer votre adresse de livraison');
      return;
    }

    if (!clientId) {
      alert('Veuillez vous connecter');
      navigate('/login');
      return;
    }

    if (!selectedRestaurantId && restaurants.length === 0) {
      alert('Aucun restaurant disponible. Veuillez contacter l\'administrateur.');
      return;
    }

    const finalRestaurantId = selectedRestaurantId || restaurants[0]?.id;
    
    if (!finalRestaurantId) {
      alert('Veuillez sélectionner un restaurant');
      return;
    }

    setLoading(true);

    const commande = {
      clientId: parseInt(clientId),
      restaurantId: parseInt(finalRestaurantId),
      adresseLivraison: adresse,
      items: cart.map(item => ({
        platId: item.id,
        nomPlat: item.nom,
        prixUnitaire: item.prix,
        quantite: item.quantite
      }))
    };

    console.log('Commande envoyée:', JSON.stringify(commande, null, 2));

    try {
      const response = await commandeAPI.create(commande);
      console.log('Réponse reçue:', response.data);
      localStorage.removeItem('panier');
      alert('Commande créée avec succès !');
      navigate(`/order-tracking/${response.data.id}`);
    } catch (err) {
      console.error('Erreur:', err);
      if (err.response?.data) {
        alert(`Erreur: ${err.response.data.error || JSON.stringify(err.response.data)}`);
      } else if (err.response?.status === 400) {
        alert('Erreur de validation. Vérifiez que le client et le restaurant existent.');
      } else {
        alert('Erreur lors de la création de la commande');
      }
    } finally {
      setLoading(false);
    }
  };

  if (cart.length === 0) {
    return (
      <div style={{ textAlign: 'center', marginTop: 50 }}>
        <h2>🛒 Votre panier est vide</h2>
        <button onClick={() => navigate('/restaurants')}>Voir les restaurants</button>
      </div>
    );
  }

  return (
    <div style={{ maxWidth: 800, margin: '0 auto', padding: 20 }}>
      <h1 style={{ color: '#FF6B35' }}>🛒 Mon panier</h1>
      
      {/* Sélecteur de restaurant */}
      {restaurants.length > 0 && (
        <div style={{ marginBottom: 20, padding: 15, backgroundColor: '#f0f0f0', borderRadius: 8 }}>
          <label style={{ fontWeight: 'bold' }}>Restaurant : </label>
          <select 
            value={selectedRestaurantId} 
            onChange={handleRestaurantChange}
            style={{ marginLeft: 10, padding: 8, borderRadius: 5 }}
          >
            {restaurants.map(r => (
              <option key={r.id} value={r.id}>
                {r.nom} - {r.adresse}
              </option>
            ))}
          </select>
        </div>
      )}
      
      {/* Liste des articles */}
      {cart.map((item) => (
        <div key={item.id} style={{ border: '1px solid #ddd', borderRadius: 8, padding: 15, marginBottom: 10 }}>
          <h3>{item.nom}</h3>
          <p>{item.prix} DH</p>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            <button 
              onClick={() => updateQuantite(item.id, item.quantite - 1)}
              style={{ width: 30, height: 30, fontSize: 18, cursor: 'pointer' }}
            >
              -
            </button>
            <span style={{ minWidth: 30, textAlign: 'center' }}>{item.quantite}</span>
            <button 
              onClick={() => updateQuantite(item.id, item.quantite + 1)}
              style={{ width: 30, height: 30, fontSize: 18, cursor: 'pointer' }}
            >
              +
            </button>
            <button 
              onClick={() => removeItem(item.id)} 
              style={{ marginLeft: 20, color: 'red', background: 'none', border: 'none', cursor: 'pointer' }}
            >
              Supprimer
            </button>
          </div>
        </div>
      ))}
      
      {/* Total et validation */}
      <div style={{ marginTop: 20, padding: 20, backgroundColor: '#f5f5f5', borderRadius: 8 }}>
        <h3>Total : {total} DH</h3>
        
        <div style={{ marginTop: 15 }}>
          <label style={{ fontWeight: 'bold' }}>Adresse de livraison :</label>
          <input
            type="text"
            value={adresse}
            onChange={(e) => setAdresse(e.target.value)}
            placeholder="Votre adresse complète"
            style={{ width: '100%', padding: 10, marginTop: 5, borderRadius: 5, border: '1px solid #ccc' }}
            required
          />
        </div>
        
        <button 
          onClick={handleCommander}
          disabled={loading || restaurants.length === 0}
          style={{
            marginTop: 20,
            width: '100%',
            padding: 15,
            backgroundColor: (loading || restaurants.length === 0) ? '#ccc' : '#FF6B35',
            color: 'white',
            border: 'none',
            borderRadius: 8,
            fontSize: 16,
            cursor: (loading || restaurants.length === 0) ? 'not-allowed' : 'pointer'
          }}
        >
          {loading ? 'Commande en cours...' : 'Valider la commande'}
        </button>
        
        {restaurants.length === 0 && (
          <p style={{ color: 'red', marginTop: 10, fontSize: 14 }}>
            ⚠️ Aucun restaurant disponible. Veuillez contacter l'administrateur.
          </p>
        )}
      </div>
      
      <button 
        onClick={() => navigate('/restaurants')}
        style={{ marginTop: 20, padding: 10, cursor: 'pointer', background: 'none', border: '1px solid #FF6B35', borderRadius: 5 }}
      >
        ← Ajouter plus de plats
      </button>
    </div>
  );
}

export default Cart;