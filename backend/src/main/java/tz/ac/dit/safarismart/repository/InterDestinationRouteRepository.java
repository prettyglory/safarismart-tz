package tz.ac.dit.safarismart.repository;

import tz.ac.dit.safarismart.entity.InterDestinationRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterDestinationRouteRepository extends JpaRepository<InterDestinationRoute, Long> {
    List<InterDestinationRoute> findByFromDestinationIdAndToDestinationIdAndActiveTrue(
            Long fromDestinationId, Long toDestinationId
    );
}
