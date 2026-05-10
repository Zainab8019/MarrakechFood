import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { clientAPI } from '../services/api';

function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await clientAPI.login({ email, motDePasse: password });
      localStorage.setItem('clientId', response.data.clientId);
      localStorage.setItem('email', response.data.email);
      alert('Connexion réussie !');
      navigate('/restaurants');
    } catch (err) {
      setError('Email ou mot de passe incorrect');
      console.error('Erreur login:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 400, margin: '50px auto', padding: 20, border: '1px solid #ccc', borderRadius: 10 }}>
      <h2 style={{ textAlign: 'center', color: '#FF6B35' }}>Marrakech Food</h2>
      <h3>Connexion</h3>
      
      {error && <p style={{ color: 'red' }}>{error}</p>}
      
      <form onSubmit={handleSubmit}>
        <div>
          <label>Email :</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            style={{ width: '100%', padding: 8, margin: '10px 0' }}
            required
          />
        </div>
        <div>
          <label>Mot de passe :</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            style={{ width: '100%', padding: 8, margin: '10px 0' }}
            required
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
            cursor: loading ? 'not-allowed' : 'pointer'
          }}
        >
          {loading ? 'Connexion...' : 'Se connecter'}
        </button>
      </form>
      
      <p style={{ textAlign: 'center', marginTop: 20 }}>
        Pas de compte ?{' '}
        <button 
          onClick={() => navigate('/register')}
          style={{ background: 'none', border: 'none', color: '#FF6B35', cursor: 'pointer' }}
        >
          S'inscrire
        </button>
      </p>
    </div>
  );
}

export default Login;