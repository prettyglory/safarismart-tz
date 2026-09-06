package tz.ac.dit.safarismart.repository;

import tz.ac.dit.safarismart.entity.Destination;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DestinationRepository extends JpaRepository<Destination, Long> {
    Optional<Destination> findByNameIgnoreCase(String name);
    List<Destination> findByRegionId(Long regionId);
}
