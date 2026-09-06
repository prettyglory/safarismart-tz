package tz.ac.dit.safarismart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

@Entity
@Table(name = "inter_destination_route")
@Getter
@Setter
@NoArgsConstructor
public class InterDestinationRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_destination_id", nullable = false)
    private Destination fromDestination;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_destination_id", nullable = false)
    private Destination toDestination;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false)
    private TransportType type;

    @Column(name = "estimated_duration_hours", nullable = false, precision = 4, scale = 1)
    private BigDecimal estimatedDurationHours;

    @Column(name = "price_min", nullable = false, precision = 12, scale = 2)
    private BigDecimal priceMin;

    @Column(name = "price_max", nullable = false, precision = 12, scale = 2)
    private BigDecimal priceMax;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
