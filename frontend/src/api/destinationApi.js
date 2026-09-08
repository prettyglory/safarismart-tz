import axiosClient from './axiosClient';

export function getAllDestinations() {
  return axiosClient.get('/destinations').then((res) => {
    const payload = res.data;
    const destinations = Array.isArray(payload)
      ? payload
      : payload?.data || payload?.destinations || payload?.content || [];

    return Array.isArray(destinations) ? destinations : [];
  });
}

export function getDestination(id) {
  return axiosClient.get(`/destinations/${id}`).then((res) => res.data);
}

export function getDestinationAttractions(id) {
  return axiosClient.get(`/destinations/${id}/attractions`).then((res) => res.data);
}
