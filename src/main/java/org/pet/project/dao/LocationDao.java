package org.pet.project.dao;


import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.pet.project.exception.DataBaseOperationException;
import org.pet.project.model.entity.Location;
import org.pet.project.model.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Slf4j
@Repository
@Transactional
public class LocationDao extends CrudDao<Location> {


    public void deleteByUserAndLatitudeAndLongitude(User user, BigDecimal lat, BigDecimal lon) {
        Session session = sessionFactory.getCurrentSession();

        try {

            String hql = "DELETE FROM Location l WHERE l.user = :user AND l.latitude = :lat AND l.longitude = :lon";

            int deletedCount = session.createQuery(hql)
                    .setParameter("user", user)
                    .setParameter("lat", lat)
                    .setParameter("lon", lon)
                    .executeUpdate();
        } catch (HibernateException e) {
            log.error("Failed to delete location for user {}. Reason: {}", user.getLogin(), e.getMessage());
            throw new DataBaseOperationException("Data base operation error");
        }

    }
}
