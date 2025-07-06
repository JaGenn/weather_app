package org.pet.project.dao;

import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.pet.project.exception.DataBaseOperationException;
import org.pet.project.model.entity.Location;
import org.pet.project.model.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Repository
@Transactional
@RequiredArgsConstructor
public class LocationDAO {

    private final SessionFactory sessionFactory;

    public void save(Location location) {

        try {
            sessionFactory.getCurrentSession().persist(location);
        } catch (HibernateException e) {
            throw new DataBaseOperationException("Failed to save location " + location.getName() + " to database");
        }
    }

    public Optional<Location> findByUserAndLatitudeAndLongitude(User user, BigDecimal lat, BigDecimal lon) {
        Session session = sessionFactory.getCurrentSession();

        try {
            String hql = "FROM Location l WHERE l.user = :user AND l.latitude = :lat AND l.longitude = :lon";

            Location result = session.createQuery(hql, Location.class)
                    .setParameter("user", user)
                    .setParameter("lat", lat)
                    .setParameter("lon", lon)
                    .getSingleResult();

            return Optional.of(result);

        } catch (NoResultException e) {
            return Optional.empty();
        } catch (HibernateException e) {
            log.error("Location search for user {} was failed", user.getLogin());
            throw new DataBaseOperationException("Data base operation error");
        }
    }

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
            log.error("Location delete for user {} was failed", user.getLogin());
            throw new DataBaseOperationException("Data base operation error");
        }

    }
}
