import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { clientAPI } from '../services/api';

function Login({ setIsAuthenticated, setUserRole }) {
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
      
      // Stocker les infos de base
      localStorage.setItem('clientId', response.data.clientId);
      localStorage.setItem('email', response.data.email);
      
      // Déterminer le rôle selon l'email (simulation pour la démo)
      let role = 'CLIENT';
      if (email === 'admin@marrakechfood.com') {
        role = 'ADMIN';
      } else if (email === 'livreur@marrakechfood.com') {
        role = 'LIVREUR';
      } else if (email.includes('livreur')) {
        role = 'LIVREUR';
      }
      
      // Stocker le rôle
      localStorage.setItem('role', role);
      
      // Mettre à jour les states du parent (App.js)
      if (setIsAuthenticated) setIsAuthenticated(true);
      if (setUserRole) setUserRole(role);
      
      alert(`Connexion réussie en tant que ${role} !`);
      
      // Rediriger selon le rôle
      if (role === 'ADMIN') {
        navigate('/admin/restaurants');
      } else if (role === 'LIVREUR') {
        navigate('/livreur/dashboard');
      } else {
        navigate('/restaurants');
      }
      
    } catch (err) {
      setError('Email ou mot de passe incorrect');
      console.error('Erreur login:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 400, margin: '50px auto', padding: 20, border: '1px solid #ccc', borderRadius: 10 }}>
      <h2 style={{ textAlign: 'center', color: '#FF6B35' }}>🍽️ Marrakech Food</h2>
      <h3>Connexion</h3>
      
      {error && <p style={{ color: 'red' }}>{error}</p>}
      
      <form onSubmit={handleSubmit}>
        <div>
          <label>Email :</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="ex: client@test.com"
            style={{ width: '100%', padding: 8, margin: '10px 0', borderRadius: 4, border: '1px solid #ccc' }}
            required
          />
        </div>
        <div>
          <label>Mot de passe :</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="1234"
            style={{ width: '100%', padding: 8, margin: '10px 0', borderRadius: 4, border: '1px solid #ccc' }}
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
            cursor: loading ? 'not-allowed' : 'pointer',
            fontSize: 16
          }}
        >
          {loading ? 'Connexion...' : 'Se connecter'}
        </button>
        <hr />
<button 
  onClick={() => navigate('/livreur/dashboard')}
  style={{ marginTop: 10, background: '#6c757d', color: 'white', padding: 8, borderRadius: 5, border: 'none', cursor: 'pointer' }}
>
  🚚 Mode Livreur
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