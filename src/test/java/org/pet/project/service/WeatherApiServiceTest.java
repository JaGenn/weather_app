package org.pet.project.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pet.project.exception.LocationNotFoundException;
import org.pet.project.model.dto.api.LocationSearchCardDto;
import org.pet.project.model.dto.api.UserWeatherCardDto;
import org.pet.project.model.dto.api.WeatherApiResponse;
import org.pet.project.model.dto.api.entity.Coordinates;
import org.pet.project.model.dto.api.entity.Main;
import org.pet.project.model.dto.api.entity.Sys;
import org.pet.project.model.dto.api.entity.Weather;
import org.pet.project.model.entity.Location;
import org.pet.project.model.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.pet.project.util.MappingUtil.convertUnixToTime;


@ExtendWith(MockitoExtension.class)
class WeatherApiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WeatherApiService weatherApiService;


    @Test
    void findLocationByName_Success() {
        String locationName = "London";
        WeatherApiResponse mockResponse = getLondonDataByWeatherApi();
        when(restTemplate.getForObject(anyString(), eq(WeatherApiResponse.class)))
                .thenReturn(mockResponse);

        Optional<LocationSearchCardDto> result = weatherApiService.findLocationByName(locationName);

        assertTrue(result.isPresent());
        assertEquals(mockResponse.getName(), result.get().getName());
        assertEquals(mockResponse.getCoordinates(), result.get().getCoordinates());
        assertEquals("United Kingdom", result.get().getCountry());

    }

    @Test
    void wrongLocationName_ReturnsEmptyOptional() {
        String locationName = "wrongName";

        when(restTemplate.getForObject(anyString(), eq(WeatherApiResponse.class)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        Optional<LocationSearchCardDto> response = weatherApiService.findLocationByName(locationName);
        assertTrue(response.isEmpty());
    }

    @Test
    void findLocationByName_restClientError() {
        when(restTemplate.getForObject(anyString(), eq(WeatherApiResponse.class)))
                .thenThrow(new RestClientException("Connection refused"));

        LocationNotFoundException ex = assertThrows(LocationNotFoundException.class, () ->
                weatherApiService.findLocationByName("SomeCity"));

        assertTrue(ex.getMessage().contains("API request error"));
    }

    @Test
    void findWeatherByLocation_Success() {
        Location location = new Location("London", new User(),
                new BigDecimal("51.5085"), new BigDecimal("-0.1257"));

        WeatherApiResponse mockResponse = getLondonDataByWeatherApi();
        when(restTemplate.getForObject(anyString(), eq(WeatherApiResponse.class)))
                .thenReturn(mockResponse);

        UserWeatherCardDto response = weatherApiService.findWeatherByLocation(location);

        assertEquals(mockResponse.getName(), response.getName());
        assertEquals("United Kingdom", response.getCountry());
        assertEquals(mockResponse.getCoordinates(), response.getCoordinates());
        assertEquals(mockResponse.getWeather().getFirst().getIcon(), response.getIcon());
        assertEquals(mockResponse.getMain().getTemp(), response.getTemp());
        assertEquals(mockResponse.getMain().getFeelsLike(), response.getFeelsLike());
        assertEquals(mockResponse.getMain().getHumidity(), response.getHumidity());
        assertEquals(convertUnixToTime(mockResponse.getSys().getSunrise(), mockResponse.getTimezone()), response.getSunrise());
        assertEquals(convertUnixToTime(mockResponse.getSys().getSunset(), mockResponse.getTimezone()), response.getSunset());
    }

    @Test
    void findWeatherByLocation_nullResponse() {
        Location location = new Location();
        location.setLatitude(BigDecimal.ONE);
        location.setLongitude(BigDecimal.ONE);

        when(restTemplate.getForObject(anyString(), eq(WeatherApiResponse.class)))
                .thenReturn(null);

        LocationNotFoundException ex = assertThrows(LocationNotFoundException.class, () ->
                weatherApiService.findWeatherByLocation(location));

        assertTrue(ex.getMessage().contains("null response"));
    }

    @Test
    void findWeatherByLocation_restClientError() {
        Location location = new Location();
        location.setLatitude(BigDecimal.ONE);
        location.setLongitude(BigDecimal.ONE);

        when(restTemplate.getForObject(anyString(), eq(WeatherApiResponse.class)))
                .thenThrow(new RestClientException("Timeout"));

        LocationNotFoundException ex = assertThrows(LocationNotFoundException.class, () ->
                weatherApiService.findWeatherByLocation(location));

        assertTrue(ex.getMessage().contains("Failed to fetch weather data"));
    }


    private WeatherApiResponse getLondonDataByWeatherApi() {
        WeatherApiResponse mockResponse = new WeatherApiResponse();

        mockResponse.setName("London");

        mockResponse.setCoordinates(new Coordinates(new BigDecimal("-0.1257"), new BigDecimal("51.5085")));

        Weather weather = new Weather();
        String icon = "01d";
        weather.setIcon(icon);
        List<Weather> weathers = new ArrayList<>();
        weathers.add(weather);
        mockResponse.setWeather(weathers);

        Main main = new Main();
        main.setTemp(24);
        main.setFeelsLike(24);
        main.setHumidity(55);
        mockResponse.setMain(main);

        Sys sys = new Sys();
        sys.setCountry("GB");
        sys.setSunrise(1752119718L);
        sys.setSunset(1752178562L);
        mockResponse.setSys(sys);

        mockResponse.setTimezone(3600);

        return mockResponse;
    }


}