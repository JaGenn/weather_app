package org.pet.project.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pet.project.dao.CrudDao;
import org.pet.project.dao.LocationDao;
import org.pet.project.exception.LocationNotFoundException;
import org.pet.project.model.entity.Location;
import org.pet.project.model.entity.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationDao locationDao;


    public boolean isLocationAlreadyTrackedByUser(User user, BigDecimal latitude, BigDecimal longitude) {
        return locationDao.findByUserAndLatitudeAndLongitude(user, latitude, longitude).isPresent();
    }

    public void deleteLocationByUser(User user, BigDecimal latitude, BigDecimal longitude) {


        Location location = locationDao.findByUserAndLatitudeAndLongitude(user, latitude, longitude)
                        .orElseThrow(() -> new LocationNotFoundException("Location, which user tries to delete, is not found"));

        user.getLocations().remove(location);

        locationDao.deleteByUserAndLatitudeAndLongitude(user, latitude, longitude);
        log.info("Deleted location {} for user {}", location.getName(), user.getLogin());
    }

}
