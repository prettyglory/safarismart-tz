package tz.ac.dit.safarismart.service.admin;

import tz.ac.dit.safarismart.dto.admin.InterDestinationRouteRequest;
import tz.ac.dit.safarismart.entity.Destination;
import tz.ac.dit.safarismart.entity.InterDestinationRoute;
import tz.ac.dit.safarismart.exception.ResourceNotFoundException;
import tz.ac.dit.safarismart.repository.DestinationRepository;
import tz.ac.dit.safarismart.repository.InterDestinationRouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminInterDestinationRouteService {

    private final InterDestinationRouteRepository routeRepository;
    private final DestinationRepository destinationRepository;

    public AdminInterDestinationRouteService(InterDestinationRouteRepository routeRepository,
                                              DestinationRepository destinationRepository) {
        this.routeRepository = routeRepository;
        this.destinationRepository = destinationRepository;
    }

    @Transactional(readOnly = true)
    public List<InterDestinationRoute> findAll() {
        return routeRepository.findAll();
    }

    public InterDestinationRoute create(InterDestinationRouteRequest request) {
        validate(request);
        Destination from = destinationRepository.findById(request.fromDestinationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found: id=" + request.fromDestinationId()));
        Destination to = destinationRepository.findById(request.toDestinationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found: id=" + request.toDestinationId()));

        InterDestinationRoute route = new InterDestinationRoute();
        applyRequest(route, request, from, to);
        return routeRepository.save(route);
    }

    public InterDestinationRoute update(Long id, InterDestinationRouteRequest request) {
        validate(request);
        InterDestinationRoute route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found: id=" + id));
        Destination from = destinationRepository.findById(request.fromDestinationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found: id=" + request.fromDestinationId()));
        Destination to = destinationRepository.findById(request.toDestinationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found: id=" + request.toDestinationId()));

        applyRequest(route, request, from, to);
        return routeRepository.save(route);
    }

    public void setActive(Long id, boolean active) {
        InterDestinationRoute route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found: id=" + id));
        route.setActive(active);
        routeRepository.save(route);
    }

    private void validate(InterDestinationRouteRequest request) {
        if (request.fromDestinationId().equals(request.toDestinationId())) {
            throw new IllegalArgumentException("fromDestinationId and toDestinationId must differ");
        }
        if (request.priceMax().compareTo(request.priceMin()) < 0) {
            throw new IllegalArgumentException("priceMax must be greater than or equal to priceMin");
        }
    }

    private void applyRequest(InterDestinationRoute route, InterDestinationRouteRequest request,
                               Destination from, Destination to) {
        route.setFromDestination(from);
        route.setToDestination(to);
        route.setType(request.type());
        route.setEstimatedDurationHours(request.estimatedDurationHours());
        route.setPriceMin(request.priceMin());
        route.setPriceMax(request.priceMax());
    }
}
