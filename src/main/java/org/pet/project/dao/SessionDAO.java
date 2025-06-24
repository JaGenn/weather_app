package org.pet.project.dao;

import lombok.RequiredArgsConstructor;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.pet.project.exception.DataBaseOperationException;
import org.pet.project.model.entity.Session;
import org.pet.project.model.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
@RequiredArgsConstructor
public class SessionDAO {

    private final SessionFactory sessionFactory;

    public void save(Session session) {

        try {
            sessionFactory.getCurrentSession().persist(session);
        } catch (HibernateException e) {
            throw new DataBaseOperationException("Failed to save session with id " + session.getId() + " to database");
        }
    }

    public Optional<Session> findById(UUID id) {

        String query = "FROM Session WHERE id = :id";
        try  {
            return sessionFactory.getCurrentSession().createQuery(query, Session.class)
                    .setParameter("id", id).uniqueResultOptional();

        } catch (HibernateException e) {
            throw new DataBaseOperationException("There was no Session in database");
        }
    }
}
