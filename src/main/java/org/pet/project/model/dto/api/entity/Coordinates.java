package org.pet.project.model.dto.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coordinates {

    private BigDecimal lon;
    private BigDecimal lat;
}
