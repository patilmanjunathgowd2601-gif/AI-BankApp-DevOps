import axios from 'axios';

// In production this is injected via nginx env substitution (see nginx.conf / entrypoint);
// in local dev it falls back to the CRA proxy target.
const API_BASE_URL = window._env_?.API_BASE_URL ?? process.env.REACT_APP_API_BASE_URL ?? 'http://localhost:8080';

const client = axios.create({ baseURL: API_BASE_URL });

client.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default client;
