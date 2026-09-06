package tz.ac.dit.safarismart.repository;

import tz.ac.dit.safarismart.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Trip> findByIdAndUserId(Long id, Long userId);
}
