package org.pet.project.model.dto.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pet.project.model.dto.api.entity.Coord;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationSearchCardDto {

    private String name;
    private String country;
    private Coord coord;

}
