package tz.ac.dit.safarismart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// Exactly one of attraction / accommodation / transport / route must be set.
// Enforced by a DB CHECK constraint (see V1__init_schema.sql) and re-validated
// in the service layer before persistence.
@Entity
@Table(name = "itinerary_item")
@Getter
@Setter
@NoArgsConstructor
public class ItineraryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "itinerary_day_id", nullable = false)
    private ItineraryDay itineraryDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attraction_id")
    private Attraction attraction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id")
    private Accommodation accommodation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transport_id")
    private TransportOption transport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private InterDestinationRoute route;

    @Column(name = "estimated_cost_min", nullable = false, precision = 12, scale = 2)
    private BigDecimal estimatedCostMin;

    @Column(name = "estimated_cost_max", nullable = false, precision = 12, scale = 2)
    private BigDecimal estimatedCostMax;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
