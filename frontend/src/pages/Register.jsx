import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { clientAPI } from '../services/api';

function Register() {
  const [formData, setFormData] = useState({
    nom: '',
    email: '',
    motDePasse: '',
    adresse: '',
    telephone: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await clientAPI.register(formData);
      alert('Inscription réussie ! Vous pouvez vous connecter.');
      navigate('/login');
    } catch (err) {
      setError('Erreur lors de l\'inscription. Email peut-être déjà utilisé.');
      console.error('Erreur register:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 500, margin: '50px auto', padding: 20, border: '1px solid #ccc', borderRadius: 10 }}>
      <h2 style={{ textAlign: 'center', color: '#FF6B35' }}>Inscription</h2>
      
      {error && <p style={{ color: 'red' }}>{error}</p>}
      
      <form onSubmit={handleSubmit}>
        <div>
          <label>Nom :</label>
          <input
            name="nom"
            value={formData.nom}
            onChange={handleChange}
            style={{ width: '100%', padding: 8, margin: '10px 0' }}
            required
          />
        </div>
        <div>
          <label>Email :</label>
          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            style={{ width: '100%', padding: 8, margin: '10px 0' }}
            required
          />
        </div>
        <div>
          <label>Mot de passe :</label>
          <input
            type="password"
            name="motDePasse"
            value={formData.motDePasse}
            onChange={handleChange}
            style={{ width: '100%', padding: 8, margin: '10px 0' }}
            required
          />
        </div>
        <div>
          <label>Téléphone :</label>
          <input
            name="telephone"
            value={formData.telephone}
            onChange={handleChange}
            style={{ width: '100%', padding: 8, margin: '10px 0' }}
          />
        </div>
        <div>
          <label>Adresse :</label>
          <input
            name="adresse"
            value={formData.adresse}
            onChange={handleChange}
            style={{ width: '100%', padding: 8, margin: '10px 0' }}
          />
        </div>
        <button 
          type="submit" 
          disabled={loading}
          style={{ 
            width: '100%', 
            padding: 10, 
            background: '#FF6B35', 
            color: 'white', 
            border: 'none', 
            borderRadius: 5,
            marginTop: 10
          }}
        >
          {loading ? 'Inscription...' : "S'inscrire"}
        </button>
      </form>
      
      <p style={{ textAlign: 'center', marginTop: 20 }}>
        Déjà un compte ?{' '}
        <button 
          onClick={() => navigate('/login')}
          style={{ background: 'none', border: 'none', color: '#FF6B35', cursor: 'pointer' }}
        >
          Se connecter
        </button>
      </p>
    </div>
  );
}

export default Register;