package tz.ac.dit.safarismart.repository;

import tz.ac.dit.safarismart.entity.ResponsibleTourismGuideline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResponsibleTourismGuidelineRepository extends JpaRepository<ResponsibleTourismGuideline, Long> {
    List<ResponsibleTourismGuideline> findByDestinationId(Long destinationId);
    List<ResponsibleTourismGuideline> findByRegionId(Long regionId);
    List<ResponsibleTourismGuideline> findByRegionIdIsNullAndDestinationIdIsNull(); // general guidelines
}
