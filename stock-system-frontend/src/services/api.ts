import axios from "axios";

// Axios instance configured for API calls
// Base URL is set from environment variable VITE_API_BASE
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE,
});

export default api;
