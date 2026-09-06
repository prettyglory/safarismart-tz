import axiosClient from './axiosClient';

// Regions
export const listRegions = () => axiosClient.get('/admin/regions').then((r) => r.data);
export const createRegion = (payload) => axiosClient.post('/admin/regions', payload).then((r) => r.data);
export const updateRegion = (id, payload) => axiosClient.put(`/admin/regions/${id}`, payload).then((r) => r.data);
export const deleteRegion = (id) => axiosClient.delete(`/admin/regions/${id}`);

// Destinations (list reuses the public endpoint -- destinations have no active flag)
export const listDestinations = () => axiosClient.get('/destinations').then((r) => r.data);
export const createDestination = (payload) => axiosClient.post('/admin/destinations', payload).then((r) => r.data);
export const updateDestination = (id, payload) => axiosClient.put(`/admin/destinations/${id}`, payload).then((r) => r.data);
export const deleteDestination = (id) => axiosClient.delete(`/admin/destinations/${id}`);

// Attractions
export const listAttractions = () => axiosClient.get('/admin/attractions').then((r) => r.data);
export const createAttraction = (payload) => axiosClient.post('/admin/attractions', payload).then((r) => r.data);
export const updateAttraction = (id, payload) => axiosClient.put(`/admin/attractions/${id}`, payload).then((r) => r.data);
export const setAttractionActive = (id, active) =>
  axiosClient.patch(`/admin/attractions/${id}/active?active=${active}`);

// Accommodations
export const listAccommodations = () => axiosClient.get('/admin/accommodations').then((r) => r.data);
export const createAccommodation = (payload) => axiosClient.post('/admin/accommodations', payload).then((r) => r.data);
export const updateAccommodation = (id, payload) => axiosClient.put(`/admin/accommodations/${id}`, payload).then((r) => r.data);
export const setAccommodationActive = (id, active) =>
  axiosClient.patch(`/admin/accommodations/${id}/active?active=${active}`);

// Transport options
export const listTransportOptions = () => axiosClient.get('/admin/transport-options').then((r) => r.data);
export const createTransportOption = (payload) => axiosClient.post('/admin/transport-options', payload).then((r) => r.data);
export const updateTransportOption = (id, payload) => axiosClient.put(`/admin/transport-options/${id}`, payload).then((r) => r.data);
export const setTransportOptionActive = (id, active) =>
  axiosClient.patch(`/admin/transport-options/${id}/active?active=${active}`);

// Inter-destination routes
export const listRoutes = () => axiosClient.get('/admin/routes').then((r) => r.data);
export const createRoute = (payload) => axiosClient.post('/admin/routes', payload).then((r) => r.data);
export const updateRoute = (id, payload) => axiosClient.put(`/admin/routes/${id}`, payload).then((r) => r.data);
export const setRouteActive = (id, active) => axiosClient.patch(`/admin/routes/${id}/active?active=${active}`);

// Restaurants
export const listRestaurants = () => axiosClient.get('/admin/restaurants').then((r) => r.data);
export const createRestaurant = (payload) => axiosClient.post('/admin/restaurants', payload).then((r) => r.data);
export const updateRestaurant = (id, payload) => axiosClient.put(`/admin/restaurants/${id}`, payload).then((r) => r.data);
export const setRestaurantActive = (id, active) =>
  axiosClient.patch(`/admin/restaurants/${id}/active?active=${active}`);

// Responsible tourism guidelines
export const listGuidelines = () => axiosClient.get('/admin/guidelines').then((r) => r.data);
export const createGuideline = (payload) => axiosClient.post('/admin/guidelines', payload).then((r) => r.data);
export const updateGuideline = (id, payload) => axiosClient.put(`/admin/guidelines/${id}`, payload).then((r) => r.data);
export const deleteGuideline = (id) => axiosClient.delete(`/admin/guidelines/${id}`);

// Users
export const listUsers = () => axiosClient.get('/admin/users').then((r) => r.data);
export const setUserActive = (id, active) => axiosClient.patch(`/admin/users/${id}/active?active=${active}`).then((r) => r.data);
