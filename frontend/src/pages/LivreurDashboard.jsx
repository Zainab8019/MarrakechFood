import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { commandeAPI } from '../services/api';

function LivreurDashboard() {
  const [commandes, setCommandes] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    fetchCommandes();
  }, []);

  const fetchCommandes = async () => {
    try {
      const response = await commandeAPI.getByStatut('VALIDEE');
      setCommandes(response.data);
    } catch (err) {
      console.error(err);
      alert('Erreur chargement commandes');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div>Chargement...</div>;

  return (
    <div style={{ maxWidth: 800, margin: '0 auto', padding: 20 }}>
      <h1 style={{ color: '#FF6B35' }}>🚚 Commandes à livrer</h1>
      {commandes.length === 0 ? (
        <p>Aucune commande en attente.</p>
      ) : (
        commandes.map(cmd => (
          <div key={cmd.id} style={{ border: '1px solid #ddd', margin: 10, padding: 15, borderRadius: 8 }}>
            <h3>Commande #{cmd.id}</h3>
            <p>Adresse : {cmd.adresseLivraison}</p>
            <p>Total : {cmd.total} DH</p>
            <button onClick={() => navigate(`/livreur/scan/${cmd.id}`)}>
              📷 Scanner QR
            </button>
          </div>
        ))
      )}
    </div>
  );
}

export default LivreurDashboard;