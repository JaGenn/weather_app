package org.pet.project.model.dto.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pet.project.model.dto.api.entity.Coordinates;

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
    private Coordinates coordinates;

    public UserWeatherCardDto(String name, String country, String icon, Integer temp, Integer feelsLike, Integer humidity, String sunrise, String sunset) {
        this.name = name;
        this.country = country;
        this.icon = icon;
        this.temp = temp;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }
}
