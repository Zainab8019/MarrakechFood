import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { restaurantAPI } from '../services/api';

function AdminPlatForm() {
  const { restaurantId, platId } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    nom: '',
    description: '',
    prix: ''
  });

  useEffect(() => {
    if (platId) {
      fetchPlat();
    }
  }, [platId]);

  const fetchPlat = async () => {
    try {
      const response = await restaurantAPI.getPlatById(platId);
      setFormData(response.data);
    } catch (err) {
      alert('Erreur chargement du plat');
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      if (platId) {
        await restaurantAPI.updatePlat(platId, formData);
        alert('Plat modifié avec succès !');
      } else {
        await restaurantAPI.createPlat(restaurantId, formData);
        alert('Plat ajouté avec succès !');
      }
      navigate(`/restaurant/${restaurantId}`);
    } catch (err) {
      alert('Erreur : ' + (err.response?.data?.message || 'Vérifie les champs'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 500, margin: '50px auto', padding: 20, border: '1px solid #ddd', borderRadius: 10 }}>
      <h2>{platId ? '✏️ Modifier le plat' : '➕ Ajouter un plat'}</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label>Nom du plat :</label>
          <input name="nom" value={formData.nom} onChange={handleChange} required style={{ width: '100%', padding: 8, margin: '10px 0' }} />
        </div>
        <div>
          <label>Description :</label>
          <textarea name="description" value={formData.description} onChange={handleChange} style={{ width: '100%', padding: 8, margin: '10px 0' }} />
        </div>
        <div>
          <label>Prix (DH) :</label>
          <input type="number" step="0.5" name="prix" value={formData.prix} onChange={handleChange} required style={{ width: '100%', padding: 8, margin: '10px 0' }} />
        </div>
        <button type="submit" disabled={loading} style={{ width: '100%', padding: 10, background: '#FF6B35', color: 'white', border: 'none', borderRadius: 5 }}>
          {loading ? 'Enregistrement...' : (platId ? 'Modifier' : 'Ajouter')}
        </button>
      </form>
    </div>
  );
}

export default AdminPlatForm;