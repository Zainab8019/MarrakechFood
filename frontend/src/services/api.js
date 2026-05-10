import axios from 'axios';

// Configuration des URLs de vos microservices
const API_CONFIG = {
  client: 'http://localhost:8089',
  restaurant: 'http://localhost:8083',
  commande: 'http://localhost:8091',
  livreur: 'http://localhost:8085'
};

// Client Service (Authentification)
export const clientAPI = {
  login: (data) => axios.post(`${API_CONFIG.client}/api/clients/connexion`, data),
  register: (data) => axios.post(`${API_CONFIG.client}/api/clients/inscrire`, data),
  getById: (id) => axios.get(`${API_CONFIG.client}/api/clients/${id}`)
};

// Restaurant Service
export const restaurantAPI = {
  getAll: () => axios.get(`${API_CONFIG.restaurant}/api/restaurants`),
  getById: (id) => axios.get(`${API_CONFIG.restaurant}/api/restaurants/${id}`),
  getPlats: (restaurantId) => axios.get(`${API_CONFIG.restaurant}/api/restaurants/${restaurantId}/plats`)
};

// Commande Service
export const commandeAPI = {
  create: (data) => axios.post(`${API_CONFIG.commande}/api/commandes`, data),
  getByClient: (clientId) => axios.get(`${API_CONFIG.commande}/api/commandes/client/${clientId}`),
  getById: (id) => axios.get(`${API_CONFIG.commande}/api/commandes/${id}`),
  valider: (id) => axios.put(`${API_CONFIG.commande}/api/commandes/${id}/valider`)
};

// Livreur Service
export const livreurAPI = {
  getDisponibles: () => axios.get(`${API_CONFIG.livreur}/api/livreurs/disponibles`),
  scanQR: (qrData) => axios.post(`${API_CONFIG.livreur}/api/livreurs/scan-jr`, { qrCodeData: qrData })
};

export default {
  clientAPI,
  restaurantAPI,
  commandeAPI,
  livreurAPI
};