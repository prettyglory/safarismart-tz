package tz.ac.dit.safarismart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "responsible_tourism_guideline")
@Getter
@Setter
@NoArgsConstructor
public class ResponsibleTourismGuideline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id")
    private Destination destination;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(name = "guideline_text", nullable = false, columnDefinition = "TEXT")
    private String guidelineText;
}
