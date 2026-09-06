import axios from 'axios';

const axiosClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1',
  headers: { 'Content-Type': 'application/json' },
});

// Attach the JWT (if present) to every outgoing request.
axiosClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('safarismart_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// On any 401, clear the stored session -- the AuthContext listens for this
// via a custom event rather than importing itself here (avoids a circular
// dependency between the client and the context that uses it).
axiosClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('safarismart_token');
      localStorage.removeItem('safarismart_user');
      window.dispatchEvent(new Event('safarismart:unauthorized'));
    }
    return Promise.reject(error);
  }
);

export default axiosClient;
