import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { restaurantAPI } from '../services/api';

function AdminRestaurantForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    nom: '',
    adresse: '',
    telephone: '',
    typeCuisine: '',
    statut: 'OUVERT'
  });

  useEffect(() => {
    if (id) {
      fetchRestaurant();
    }
  }, [id]);

  const fetchRestaurant = async () => {
    try {
      const response = await restaurantAPI.getById(id);
      setFormData(response.data);
    } catch (err) {
      alert('Erreur chargement du restaurant');
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      if (id) {
        await restaurantAPI.update(id, formData);
        alert('Restaurant modifié avec succès !');
      } else {
        await restaurantAPI.create(formData);
        alert('Restaurant ajouté avec succès !');
      }
      navigate('/admin/restaurants');
    } catch (err) {
      alert('Erreur : ' + (err.response?.data?.message || 'Vérifie les champs'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 500, margin: '50px auto', padding: 20, border: '1px solid #ddd', borderRadius: 10 }}>
      <h2>{id ? '✏️ Modifier le restaurant' : '➕ Ajouter un restaurant'}</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label>Nom :</label>
          <input name="nom" value={formData.nom} onChange={handleChange} required style={{ width: '100%', padding: 8, margin: '10px 0' }} />
        </div>
        <div>
          <label>Adresse :</label>
          <input name="adresse" value={formData.adresse} onChange={handleChange} required style={{ width: '100%', padding: 8, margin: '10px 0' }} />
        </div>
        <div>
          <label>Téléphone :</label>
          <input name="telephone" value={formData.telephone} onChange={handleChange} style={{ width: '100%', padding: 8, margin: '10px 0' }} />
        </div>
        <div>
          <label>Type de cuisine :</label>
          <input name="typeCuisine" value={formData.typeCuisine} onChange={handleChange} style={{ width: '100%', padding: 8, margin: '10px 0' }} />
        </div>
        <div>
          <label>Statut :</label>
          <select name="statut" value={formData.statut} onChange={handleChange} style={{ width: '100%', padding: 8, margin: '10px 0' }}>
            <option value="OUVERT">Ouvert</option>
            <option value="FERME">Fermé</option>
          </select>
        </div>
        <button type="submit" disabled={loading} style={{ width: '100%', padding: 10, background: '#FF6B35', color: 'white', border: 'none', borderRadius: 5 }}>
          {loading ? 'Enregistrement...' : (id ? 'Modifier' : 'Ajouter')}
        </button>
      </form>
    </div>
  );
}

export default AdminRestaurantForm;