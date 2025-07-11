package org.pet.project.dao;


import org.hibernate.HibernateException;
import org.pet.project.exception.DataBaseOperationException;
import org.pet.project.model.entity.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class SessionDao extends CrudDao<Session> {



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
