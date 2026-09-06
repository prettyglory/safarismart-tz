package tz.ac.dit.safarismart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "attraction")
@Getter
@Setter
@NoArgsConstructor
public class Attraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destination_id", nullable = false)
    private Destination destination;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 100)
    private String category;

    // Maps to Postgres TEXT[]
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "interest_tags", nullable = false, columnDefinition = "text[]")
    private String[] interestTags = new String[0];

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "entrance_fee_min", nullable = false, precision = 12, scale = 2)
    private BigDecimal entranceFeeMin;

    @Column(name = "entrance_fee_max", nullable = false, precision = 12, scale = 2)
    private BigDecimal entranceFeeMax;

    @Column(name = "avg_duration_hours", nullable = false, precision = 4, scale = 1)
    private BigDecimal avgDurationHours;

    @Column(name = "is_community_based", nullable = false)
    private boolean communityBased = false;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }
}
