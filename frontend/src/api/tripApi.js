import axiosClient from './axiosClient';

export function generateTrip(request) {
  return axiosClient.post('/trips/generate', request).then((res) => res.data);
}

export function saveTrip(itinerary) {
  const payload = {
    totalDays: itinerary.totalDays,
    budget: itinerary.budget ?? null,
    travelers: itinerary.travelers,
    interests: itinerary.interests,
    travelStyle: itinerary.travelStyle,
    language: itinerary.language,
    legs: itinerary.legs.map((leg) => ({
      destinationId: leg.destinationId,
      sequenceOrder: leg.sequenceOrder,
      daysAllocated: leg.daysAllocated,
      days: leg.days.map((day) => ({
        dayNumberInLeg: day.dayNumberInLeg,
        narrative: day.narrative,
        items: day.items
          .filter((item) => item.type !== 'NOTE')
          .map((item) => ({
            type: item.type,
            refId: item.refId,
            costMin: item.costMin,
            costMax: item.costMax,
            notes: item.notes,
          })),
      })),
    })),
  };
  return axiosClient.post('/trips', payload).then((res) => res.data);
}

export function listSavedTrips() {
  return axiosClient.get('/trips').then((res) => res.data);
}

export function getSavedTrip(id) {
  return axiosClient.get(`/trips/${id}`).then((res) => res.data.itinerary);
}

export function deleteSavedTrip(id) {
  return axiosClient.delete(`/trips/${id}`);
}
