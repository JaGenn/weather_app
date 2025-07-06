package org.pet.project.model.dto.api;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pet.project.model.dto.api.entity.Coord;
import org.pet.project.model.dto.api.entity.Main;
import org.pet.project.model.dto.api.entity.Sys;
import org.pet.project.model.dto.api.entity.Weather;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherApiResponse {

    private String name;
    private Coord coord;
    private List<Weather> weather;
    private Main main;
    private Sys sys;
    private Integer timezone;
}
