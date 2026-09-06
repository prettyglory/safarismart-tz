import axiosClient from './axiosClient';

export function register({ fullName, email, password }) {
  return axiosClient.post('/auth/register', { fullName, email, password }).then((res) => res.data);
}

export function login({ email, password }) {
  return axiosClient.post('/auth/login', { email, password }).then((res) => res.data);
}
