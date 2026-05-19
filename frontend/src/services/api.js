import axios from 'axios';

const API_CONFIG = {
  client: 'http://localhost:8089',
  restaurant: 'http://localhost:8083',
  commande: 'http://localhost:8091',   
  livreur: 'http://localhost:8085'
};

// Client Service
export const clientAPI = {
  login: (data) => axios.post(`${API_CONFIG.client}/api/clients/connexion`, data),
  register: (data) => axios.post(`${API_CONFIG.client}/api/clients/inscrire`, data),
  getById: (id) => axios.get(`${API_CONFIG.client}/api/clients/${id}`)
};

// Restaurant Service
export const restaurantAPI = {
  getAll: () => axios.get(`${API_CONFIG.restaurant}/api/restaurants`),
  getById: (id) => axios.get(`${API_CONFIG.restaurant}/api/restaurants/${id}`),
  getPlats: (restaurantId) => axios.get(`${API_CONFIG.restaurant}/api/restaurants/${restaurantId}/plats`),
  // Ajout CRUD restaurants
  create: (data) => axios.post(`${API_CONFIG.restaurant}/api/restaurants`, data),
  update: (id, data) => axios.put(`${API_CONFIG.restaurant}/api/restaurants/${id}`, data),
  delete: (id) => axios.delete(`${API_CONFIG.restaurant}/api/restaurants/${id}`),
  // Ajout CRUD plats
  getPlatById: (id) => axios.get(`${API_CONFIG.restaurant}/api/plats/${id}`),
  createPlat: (restaurantId, data) => axios.post(`${API_CONFIG.restaurant}/api/restaurants/${restaurantId}/plats`, data),
  updatePlat: (id, data) => axios.put(`${API_CONFIG.restaurant}/api/plats/${id}`, data),
  deletePlat: (id) => axios.delete(`${API_CONFIG.restaurant}/api/restaurants/plats/${id}`)
};

// Commande Service
export const commandeAPI = {
  create: (data) => axios.post(`${API_CONFIG.commande}/api/commandes`, data),
  getByClient: (clientId) => axios.get(`${API_CONFIG.commande}/api/commandes/client/${clientId}`),
  getById: (id) => axios.get(`${API_CONFIG.commande}/api/commandes/${id}`),
  valider: (id) => axios.put(`${API_CONFIG.commande}/api/commandes/${id}/valider`),
  // Ajout pour récupérer commandes par statut 
  getByStatut: (statut) => axios.get(`${API_CONFIG.commande}/api/commandes?statut=${statut}`)
};

// Livreur Service
export const livreurAPI = {
  getDisponibles: () => axios.get(`${API_CONFIG.livreur}/api/livreurs/disponibles`),
  scanQR: (qrData) => axios.post(`${API_CONFIG.livreur}/api/livreurs/scan-qr`, { qrCodeData: qrData })
};

export default {
  clientAPI,
  restaurantAPI,
  commandeAPI,
  livreurAPI
};