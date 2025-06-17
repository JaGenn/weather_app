package org.pet.project.dao;

import lombok.RequiredArgsConstructor;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.pet.project.exception.DataBaseOperationException;
import org.pet.project.model.entity.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
}
