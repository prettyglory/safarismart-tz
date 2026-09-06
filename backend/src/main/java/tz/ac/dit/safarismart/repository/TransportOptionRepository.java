package tz.ac.dit.safarismart.repository;

import tz.ac.dit.safarismart.entity.TransportOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransportOptionRepository extends JpaRepository<TransportOption, Long> {
    List<TransportOption> findByDestinationIdAndActiveTrue(Long destinationId);
}
