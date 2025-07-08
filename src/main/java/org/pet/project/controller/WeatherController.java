package org.pet.project.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pet.project.dao.LocationDao;
import org.pet.project.exception.CookieNotFoundException;
import org.pet.project.model.dto.api.UserWeatherCardDto;
import org.pet.project.model.dto.api.entity.Coordinates;
import org.pet.project.model.entity.User;
import org.pet.project.service.WeatherApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


import java.util.*;


@Slf4j
@Controller
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherApiService weatherApiService;
    private final LocationDao locationDao;

    @GetMapping("/")
    public String getWeather(HttpServletRequest req, Model model) {

        User user = (User) req.getAttribute("user");

        if (user == null) {
            return "main-page";
        }


        List<UserWeatherCardDto> userLocationCards = user.getLocations()
                .stream()
                .map(weatherApiService::findWeatherByLocation)
                .toList()
                .reversed();

        model.addAttribute("login", user.getLogin());
        model.addAttribute("locations", userLocationCards);
        return "main-page";
    }

    @PostMapping("/delete")
    public String deleteWeatherCard(@ModelAttribute Coordinates coordinates, HttpServletRequest req) {

        User user = (User) req.getAttribute("user");
        if (user == null) {
            throw new CookieNotFoundException("User, which deletes location, is not found");
        }

        locationDao.deleteByUserAndLatitudeAndLongitude(user, coordinates.getLat(), coordinates.getLon());
        log.info("Deleted location for user {}", user.getLogin());

        return "redirect:/";
    }

}
