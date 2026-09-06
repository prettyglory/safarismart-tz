package tz.ac.dit.safarismart.repository;

import tz.ac.dit.safarismart.entity.Accommodation;
import tz.ac.dit.safarismart.entity.TravelStyle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
    List<Accommodation> findByDestinationIdAndStyleAndActiveTrue(Long destinationId, TravelStyle style);
}
