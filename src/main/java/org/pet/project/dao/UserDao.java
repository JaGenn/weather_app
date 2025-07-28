package org.pet.project.dao;


import org.hibernate.HibernateException;
import org.pet.project.exception.DataBaseOperationException;
import org.pet.project.model.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public class UserDao extends CrudDao<User> {


    public Optional<User> findByLogin(String login) {
        String query = "FROM User WHERE login = :login";
        try  {
            return sessionFactory.getCurrentSession().createQuery(query, User.class)
                    .setParameter("login", login).uniqueResultOptional();

        } catch (HibernateException e) {
            throw new DataBaseOperationException("There is no user with login " + login + " in database");
        }
    }

    public void update(User user) {

        try {
            sessionFactory.getCurrentSession().merge(user);
        } catch (HibernateException e) {
            throw new DataBaseOperationException("Update user data with login " + user.getLogin() + " was failed");
        }
    }
}
