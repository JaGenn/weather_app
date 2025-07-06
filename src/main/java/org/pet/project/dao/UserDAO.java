package org.pet.project.dao;


import lombok.RequiredArgsConstructor;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.pet.project.exception.DataBaseOperationException;
import org.pet.project.exception.UserExistsException;
import org.pet.project.model.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class UserDAO {


    private final SessionFactory sessionFactory;

    public void save(User user) {

        try {
            sessionFactory.getCurrentSession().persist(user);
        } catch (HibernateException e) {
            throw new UserExistsException("User with login " + user.getLogin() + " already exists");
        }
    }

    public Optional<User> fingByLogin(String login) {
        String query = "FROM User WHERE login = :login";
        try  {
            return sessionFactory.getCurrentSession().createQuery(query, User.class)
                    .setParameter("login", login).uniqueResultOptional();

        } catch (HibernateException e) {
            throw new DataBaseOperationException("There is no user with login " + login + " in database");
        }
    }

    public void updateUser(User user) {

        try {
            sessionFactory.getCurrentSession().merge(user);
        } catch (HibernateException e) {
            throw new DataBaseOperationException("Update user data with login " + user.getLogin() + " was failed");
        }
    }
}
