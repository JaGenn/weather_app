package org.pet.project.util;

import org.pet.project.model.dto.api.LocationSearchCardDto;
import org.pet.project.model.dto.api.UserWeatherCardDto;
import org.pet.project.model.dto.api.WeatherApiResponse;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MappingUtil {


    public static LocationSearchCardDto convertToLocationDto(WeatherApiResponse apiResponse) {

        String encodedCountryName = convertCountryCodeToString(apiResponse.getSys().getCountry());

        return new LocationSearchCardDto(
                apiResponse.getName(),
                encodedCountryName,
                apiResponse.getCoordinates()
        );
    }


    public static UserWeatherCardDto convertToWeatherDto(WeatherApiResponse apiResponse) {

        String encodedCountryName = convertCountryCodeToString(apiResponse.getSys().getCountry());
        String encodedSunrise = convertUnixToTime(apiResponse.getSys().getSunrise(), apiResponse.getTimezone());
        String encodedSunset = convertUnixToTime(apiResponse.getSys().getSunset(), apiResponse.getTimezone());

        return new UserWeatherCardDto(
                apiResponse.getName(),
                encodedCountryName,
                apiResponse.getWeather().getFirst().getIcon(),
                apiResponse.getMain().getTemp(),
                apiResponse.getMain().getFeelsLike(),
                apiResponse.getMain().getHumidity(),
                encodedSunrise,
                encodedSunset
        );
    }

    public static String convertUnixToTime(long unixTime, int timezoneOffsetSeconds) {
        // Создаем Instant из Unix-времени
        Instant instant = Instant.ofEpochSecond(unixTime);

        // Применяем смещение временной зоны
        ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(timezoneOffsetSeconds);

        // Форматируем в читаемый вид (HH:mm)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(zoneOffset);
        return formatter.format(instant);
    }

    public static String convertCountryCodeToString(String countryCode) {
        if (countryCode == null || countryCode.isBlank()) {
            return "Unknown country";
        }
        Locale locale = new Locale("", countryCode);
        return locale.getDisplayCountry(Locale.ENGLISH);
    }
}
