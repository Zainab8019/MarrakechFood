import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { commandeAPI } from '../services/api';

function OrderTracking() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [commande, setCommande] = useState(null);
  const [loading, setLoading] = useState(true);
  const [validating, setValidating] = useState(false);

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

  const handleValider = async () => {
    setValidating(true);
    try {
      await commandeAPI.valider(id);
      alert('Commande validée avec succès ! QR code généré.');
      fetchCommande();
    } catch (err) {
      console.error(err);
      alert('Erreur lors de la validation : ' + (err.response?.data?.error || err.message));
    } finally {
      setValidating(false);
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
      
      {commande?.statut === 'EN_ATTENTE' && (
        <div style={{ textAlign: 'center', marginBottom: 20 }}>
          <button
            onClick={handleValider}
            disabled={validating}
            style={{
              padding: '10px 20px',
              backgroundColor: '#28a745',
              color: 'white',
              border: 'none',
              borderRadius: 5,
              cursor: validating ? 'not-allowed' : 'pointer',
              fontSize: 16
            }}
          >
            {validating ? 'Validation...' : 'Valider la commande'}
          </button>
        </div>
      )}
      
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
      
      {commande?.qrCodeBase64 ? (
        <div style={{ textAlign: 'center', marginTop: 30 }}>
          <h3>📱 QR Code de la commande</h3>
          <img 
            src={`data:image/png;base64,${commande.qrCodeBase64}`} 
            alt="QR Code"
            style={{ width: 200, height: 200, border: '1px solid #ddd', borderRadius: 10 }}
          />
          <p>Présentez ce QR code au livreur</p>
          {/* Affichage du texte du QR code pour éviter les erreurs de saisie */}
          <p style={{ fontSize: 14, fontFamily: 'monospace', marginTop: 5, backgroundColor: '#f5f5f5', padding: 8, borderRadius: 5 }}>
            Code : COMMANDE_{commande.id}
          </p>
        </div>
      ) : (
        <div style={{ textAlign: 'center', marginTop: 30, padding: 20, backgroundColor: '#f0f0f0', borderRadius: 8 }}>
          <p>⏳ Le QR code sera généré après validation de la commande</p>
        </div>
      )}
      
      <div style={{ marginTop: 30, padding: 20, backgroundColor: '#f5f5f5', borderRadius: 8 }}>
        <h3>📋 Détails commande</h3>
        <p><strong>Statut :</strong> {commande?.statut}</p>
        <p><strong>Total :</strong> {commande?.total} DH</p>
        <p><strong>Adresse :</strong> {commande?.adresseLivraison}</p>
        <p><strong>Date :</strong> {new Date(commande?.dateCommande).toLocaleString()}</p>
      </div>
      
      <button 
        onClick={() => navigate('/restaurants')}
        style={{ marginTop: 20, padding: 10, cursor: 'pointer', background: 'none', border: '1px solid #FF6B35', borderRadius: 5 }}
      >
        ← Retour aux restaurants
      </button>
    </div>
  );
}

export default OrderTracking;