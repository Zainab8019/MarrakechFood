import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { commandeAPI } from '../services/api';

function OrderTracking() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [commande, setCommande] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchCommande();
  }, [id]);

  const fetchCommande = async () => {
    try {
      const response = await commandeAPI.getById(id);
      setCommande(response.data);
    } catch (err) {
      console.error(err);
      alert('Erreur chargement commande');
    } finally {
      setLoading(false);
    }
  };

  const statutSteps = {
    'EN_ATTENTE': 1,
    'VALIDEE': 2,
    'EN_PREPARATION': 3,
    'EN_LIVRAISON': 4,
    'LIVREE': 5
  };

  const currentStep = statutSteps[commande?.statut] || 1;

  if (loading) return <div style={{ textAlign: 'center', marginTop: 50 }}>Chargement...</div>;

  return (
    <div style={{ maxWidth: 600, margin: '0 auto', padding: 20 }}>
      <h1 style={{ color: '#FF6B35' }}>📦 Suivi commande #{commande?.id}</h1>
      
      {/* Barre de progression des statuts */}
      <div style={{ marginTop: 30 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 10 }}>
          <span>📝 En attente</span>
          <span>✅ Validée</span>
          <span>🍳 En préparation</span>
          <span>🚚 En livraison</span>
          <span>🏠 Livrée</span>
        </div>
        <div style={{ height: 10, backgroundColor: '#eee', borderRadius: 5, overflow: 'hidden' }}>
          <div style={{ width: `${(currentStep / 5) * 100}%`, height: '100%', backgroundColor: '#FF6B35', transition: 'width 0.5s' }} />
        </div>
      </div>
      
      {/* QR Code */}
      {commande?.qrCodeBase64 && (
        <div style={{ textAlign: 'center', marginTop: 30 }}>
          <h3>QR Code de la commande</h3>
          <img 
            src={`data:image/png;base64,${commande.qrCodeBase64}`} 
            alt="QR Code"
            style={{ width: 200, height: 200, border: '1px solid #ddd', borderRadius: 10 }}
          />
          <p>Montrez ce QR code au livreur</p>
        </div>
      )}
      
      {/* Infos commande */}
      <div style={{ marginTop: 30, padding: 20, backgroundColor: '#f5f5f5', borderRadius: 8 }}>
        <h3>Détails commande</h3>
        <p><strong>Statut :</strong> {commande?.statut}</p>
        <p><strong>Total :</strong> {commande?.total} DH</p>
        <p><strong>Adresse :</strong> {commande?.adresseLivraison}</p>
        <p><strong>Date :</strong> {new Date(commande?.dateCommande).toLocaleString()}</p>
      </div>
      
      <button 
        onClick={() => navigate('/restaurants')}
        style={{ marginTop: 20, padding: 10, cursor: 'pointer' }}
      >
        ← Retour aux restaurants
      </button>
    </div>
  );
}

export default OrderTracking;