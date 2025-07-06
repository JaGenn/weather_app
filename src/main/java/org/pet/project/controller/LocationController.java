package org.pet.project.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pet.project.dao.LocationDAO;
import org.pet.project.exception.CookieNotFoundException;
import org.pet.project.model.dto.api.LocationSearchCardDto;
import org.pet.project.model.entity.Location;
import org.pet.project.model.entity.User;
import org.pet.project.service.LocationService;
import org.pet.project.service.UserSessionCheckService;
import org.pet.project.service.WeatherApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.util.Optional;


@Slf4j
@Controller
@RequiredArgsConstructor
public class LocationController {

    private final WeatherApiService weatherApiService;
    private final UserSessionCheckService userSessionCheckService;
    private final LocationDAO locationDAO;
    private final LocationService locationService;

    @GetMapping("/search")
    public String searchLocation(@RequestParam("location") String locationName, HttpServletRequest req, Model model) {

        Optional<User> user = userSessionCheckService.getAuthenticatedUser(req, model);

        if (user.isEmpty()) {
            return "main-page";
        }
        if (locationName == null || locationName.isBlank() || !isLocationValid(locationName)) {
            addUserAndQueryToModel(user.get(), locationName, model);
            model.addAttribute("error", "Название локации не может содержать цифры, символы или быть пустым");
            return "search-page";
        }

        Optional<LocationSearchCardDto> locationSearchCardDto = weatherApiService.findLocationByName(locationName);

        if (locationSearchCardDto.isEmpty()) {
            addUserAndQueryToModel(user.get(), locationName, model);
            model.addAttribute("error", "Локация не найдена. Попробуйте другой запрос.");
            return "search-page";
        }

        model.addAttribute("locationCard", locationSearchCardDto.get());
        addUserAndQueryToModel(user.get(), locationName, model);
        return "search-page";
    }

    @PostMapping("/track")
    public String trackLocation(@ModelAttribute("locationCard") LocationSearchCardDto cardDto,
                                HttpServletRequest req, Model model) {

        String locationName = cardDto.getName();
        BigDecimal lat = cardDto.getCoordinates().getLat();
        BigDecimal lon = cardDto.getCoordinates().getLon();

        User user = userSessionCheckService.getAuthenticatedUser(req, model)
                .orElseThrow(() -> new CookieNotFoundException("User, which requests location, is not found"));

        if (locationService.isLocationAlreadyTrackedByUser(user, lat, lon)) {
            addUserAndQueryToModel(user, locationName, model);
            model.addAttribute("success", "Вы уже добавили эту локацию. Выберите другую.");
            model.addAttribute("locationCard", null);
            return "search-page";
        }

        Location location = new Location(locationName, user, lat, lon);
        locationDAO.save(location);

        user.getLocations().add(location);

        return "redirect:/";
    }

    private boolean isLocationValid(String locationName) {
        // Разрешаем только буквы (без цифр и символов)
        return locationName.matches("^[a-zA-Zа-яА-ЯёЁ\\s]+$");
    }

    private void addUserAndQueryToModel (User user, String locationName, Model model) {
        model.addAttribute("isAuthenticated", true);
        model.addAttribute("login", user.getLogin());
        model.addAttribute("searchQuery", locationName);
    }
}
