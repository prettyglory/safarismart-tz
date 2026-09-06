import { createContext, useContext, useEffect, useState } from 'react';
import * as authApi from '../api/authApi';

const AuthContext = createContext(null);

function loadStoredUser() {
  const raw = localStorage.getItem('safarismart_user');
  return raw ? JSON.parse(raw) : null;
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(loadStoredUser);

  useEffect(() => {
    function handleUnauthorized() {
      setUser(null);
    }
    window.addEventListener('safarismart:unauthorized', handleUnauthorized);
    return () => window.removeEventListener('safarismart:unauthorized', handleUnauthorized);
  }, []);

  function persistSession(authResponse) {
    localStorage.setItem('safarismart_token', authResponse.token);
    const sessionUser = {
      userId: authResponse.userId,
      fullName: authResponse.fullName,
      email: authResponse.email,
      role: authResponse.role,
    };
    localStorage.setItem('safarismart_user', JSON.stringify(sessionUser));
    setUser(sessionUser);
  }

  async function login(credentials) {
    const response = await authApi.login(credentials);
    persistSession(response);
    return response;
  }

  async function register(details) {
    const response = await authApi.register(details);
    persistSession(response);
    return response;
  }

  function logout() {
    localStorage.removeItem('safarismart_token');
    localStorage.removeItem('safarismart_user');
    setUser(null);
  }

  const value = {
    user,
    isAuthenticated: Boolean(user),
    isAdmin: user?.role === 'ADMIN',
    login,
    register,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
