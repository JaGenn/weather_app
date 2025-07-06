package org.pet.project.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pet.project.exception.CookieNotFoundException;
import org.pet.project.model.dto.api.UserWeatherCardDto;
import org.pet.project.model.dto.api.entity.Coordinates;
import org.pet.project.model.entity.User;
import org.pet.project.service.LocationService;
import org.pet.project.service.UserSessionCheckService;
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

    private final UserSessionCheckService sessionCheckService;
    private final WeatherApiService weatherApiService;
    private final LocationService locationService;

    @GetMapping("/")
    public String getWeather(HttpServletRequest req, Model model) {

        Optional<User> optionalUser = sessionCheckService.getAuthenticatedUser(req, model);

        if (optionalUser.isEmpty()) {
            return "main-page";
        }

        User user = optionalUser.get();

        List<UserWeatherCardDto> userLocationCards = user.getLocations().stream()
                .map(weatherApiService::findWeatherByLocation).toList().reversed();

        model.addAttribute("isAuthenticated", true);
        model.addAttribute("login", user.getLogin());
        model.addAttribute("locations", userLocationCards);
        return "main-page";
    }

    @PostMapping("/delete")
    public String deleteWeatherCard(@ModelAttribute Coordinates coordinates, HttpServletRequest req, Model model) {

        User user = sessionCheckService.getAuthenticatedUser(req, model)
                .orElseThrow(() -> new CookieNotFoundException("User, which deletes location, is not found"));

        locationService.deleteLocationByUser(user, coordinates.getLat(), coordinates.getLon());

        return "redirect:/";
    }

}
