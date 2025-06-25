package org.pet.project.service;


import lombok.extern.slf4j.Slf4j;
import org.pet.project.exception.LocationNotFoundException;
import org.pet.project.model.dto.api.WeatherApiResponse;
import org.pet.project.model.dto.api.LocationSearchCardDto;
import org.pet.project.model.dto.api.UserWeatherCardDto;
import org.pet.project.model.dto.api.entity.Coord;
import org.pet.project.model.entity.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static org.pet.project.util.MappingUtil.*;

@Slf4j
@Service
@PropertySource("classpath:application.properties")
public class WeatherApiService {

    @Value("${weather.api.key}")
    private String API_KEY;

    @Autowired
    private RestTemplate restTemplate;

    public Optional<LocationSearchCardDto> findLocationByName(String locationName) {

        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + locationName + "&appid=" + API_KEY + "&units=metric";

        try {
            WeatherApiResponse response = restTemplate.getForObject(url, WeatherApiResponse.class);

            if (response == null) {
                log.warn("Weather API returned null response for location: {}", locationName);
                return Optional.empty();
            }

            LocationSearchCardDto locationSearchCardDto = convertToLocationDto(response);

            log.info("Location {} was found by name and converted to DTO", locationName);
            return Optional.of(locationSearchCardDto);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.info("Location not found: {}", locationName);
                return Optional.empty();
            }
            log.error("HTTP error while fetching location '{}': {}", locationName, e.getMessage());
            throw new LocationNotFoundException("Api returned error: " + e.getStatusCode());
        } catch (RestClientException e) {
            log.error("Error fetching location '{}': {}", locationName, e.getMessage());
            throw new LocationNotFoundException("API request error: " + e.getMessage());
        }
    }

    public UserWeatherCardDto findWeatherByLocation(Location location) {

        BigDecimal lat = location.getLatitude();
        BigDecimal lon = location.getLongitude();

        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + API_KEY + "&units=metric";

        try {
            WeatherApiResponse response = restTemplate.getForObject(url, WeatherApiResponse.class);

            if (response == null) {
                throw new LocationNotFoundException("Weather API returned null response for coordinates: lat=" + lat + ", lon=" + lon);
            }

            Coord coord = new Coord(lon, lat);

            log.info("Location {} was found by coordinates and converted to DTO", location.getName());
            return convertToWeatherDto(response, coord);

        } catch (RestClientException e) {
            log.error("Error fetching weather for coordinates lat={}, lon={}", lat, lon);
            throw new LocationNotFoundException("Failed to fetch weather data: " + e.getMessage());
        }
    }


}
