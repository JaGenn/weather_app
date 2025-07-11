package org.pet.project.model.dto.api;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pet.project.model.dto.api.entity.Coordinates;
import org.pet.project.model.dto.api.entity.Main;
import org.pet.project.model.dto.api.entity.Sys;
import org.pet.project.model.dto.api.entity.Weather;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherApiResponse {

    private String name;
    @JsonProperty("coord")
    private Coordinates coordinates;
    private List<Weather> weather;
    private Main main;
    private Sys sys;
    private Integer timezone;
}
