package tz.ac.dit.safarismart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

@Entity
@Table(name = "transport_option")
@Getter
@Setter
@NoArgsConstructor
public class TransportOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destination_id", nullable = false)
    private Destination destination;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false)
    private TransportType type;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "price_min", nullable = false, precision = 12, scale = 2)
    private BigDecimal priceMin;

    @Column(name = "price_max", nullable = false, precision = 12, scale = 2)
    private BigDecimal priceMax;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
