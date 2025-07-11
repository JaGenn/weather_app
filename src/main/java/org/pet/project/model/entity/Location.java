package org.pet.project.model.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "locations", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "latitude", "longitude"}))
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "name", nullable = false)
    private String name;

    @NonNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NonNull
    @Column(name = "latitude", nullable = false)
    private BigDecimal latitude;

    @NonNull
    @Column(name = "longitude", nullable = false)
    private BigDecimal longitude;

}
