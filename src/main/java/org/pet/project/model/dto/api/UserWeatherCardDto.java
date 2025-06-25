package org.pet.project.model.dto.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pet.project.model.dto.api.entity.Coord;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWeatherCardDto {

    private String name;
    private String country;
    private String icon;
    private Integer temp;
    private Integer feelsLike;
    private Integer humidity;
    private String sunrise;
    private String sunset;
    private Coord coord;

}
