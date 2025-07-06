package org.pet.project.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pet.project.dao.LocationDAO;
import org.pet.project.exception.LocationNotFoundException;
import org.pet.project.model.entity.Location;
import org.pet.project.model.entity.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationDAO locationDAO;


    public boolean isLocationAlreadyTrackedByUser(User user, BigDecimal latitude, BigDecimal longitude) {
        return locationDAO.findByUserAndLatitudeAndLongitude(user, latitude, longitude).isPresent();
    }

    public void deleteLocationByUser(User user, BigDecimal latitude, BigDecimal longitude) {


        Location location = locationDAO.findByUserAndLatitudeAndLongitude(user, latitude, longitude)
                        .orElseThrow(() -> new LocationNotFoundException("Location, which user tries to delete, is not found"));

        user.getLocations().remove(location);

        locationDAO.deleteByUserAndLatitudeAndLongitude(user, latitude, longitude);
        log.info("Deleted location {} for user {}", location.getName(), user.getLogin());
    }

}
