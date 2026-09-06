package tz.ac.dit.safarismart.service.admin;

import tz.ac.dit.safarismart.dto.admin.RestaurantRequest;
import tz.ac.dit.safarismart.entity.Destination;
import tz.ac.dit.safarismart.entity.Restaurant;
import tz.ac.dit.safarismart.exception.ResourceNotFoundException;
import tz.ac.dit.safarismart.repository.DestinationRepository;
import tz.ac.dit.safarismart.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminRestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final DestinationRepository destinationRepository;

    public AdminRestaurantService(RestaurantRepository restaurantRepository,
                                   DestinationRepository destinationRepository) {
        this.restaurantRepository = restaurantRepository;
        this.destinationRepository = destinationRepository;
    }

    @Transactional(readOnly = true)
    public List<Restaurant> findAll() {
        return restaurantRepository.findAll();
    }

    public Restaurant create(RestaurantRequest request) {
        Destination destination = destinationRepository.findById(request.destinationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found: id=" + request.destinationId()));

        Restaurant restaurant = new Restaurant();
        applyRequest(restaurant, request, destination);
        return restaurantRepository.save(restaurant);
    }

    public Restaurant update(Long id, RestaurantRequest request) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found: id=" + id));
        Destination destination = destinationRepository.findById(request.destinationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found: id=" + request.destinationId()));

        applyRequest(restaurant, request, destination);
        return restaurantRepository.save(restaurant);
    }

    public void setActive(Long id, boolean active) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found: id=" + id));
        restaurant.setActive(active);
        restaurantRepository.save(restaurant);
    }

    private void applyRequest(Restaurant restaurant, RestaurantRequest request, Destination destination) {
        restaurant.setDestination(destination);
        restaurant.setName(request.name());
        restaurant.setCuisineType(request.cuisineType());
        restaurant.setPriceRange(request.priceRange());
    }
}
