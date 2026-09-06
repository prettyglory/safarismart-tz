package tz.ac.dit.safarismart.repository;

import tz.ac.dit.safarismart.entity.Attraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttractionRepository extends JpaRepository<Attraction, Long> {

    List<Attraction> findByDestinationIdAndActiveTrue(Long destinationId);

    // Used by the recommendation engine (Phase 7): attractions at a destination
    // whose interest_tags overlap with the tourist's requested interests.
    @Query(value = """
        SELECT * FROM attraction a
        WHERE a.destination_id = :destinationId
          AND a.is_active = TRUE
          AND a.interest_tags && CAST(:interests AS text[])
        """, nativeQuery = true)
    List<Attraction> findMatchingByDestinationAndInterests(
            @Param("destinationId") Long destinationId,
            @Param("interests") String[] interests
    );
}
