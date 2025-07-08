package org.pet.project.dao;


import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.pet.project.exception.DataBaseOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


public abstract class CrudDao<T> {

    @Autowired
    protected SessionFactory sessionFactory;

    @Transactional
    public void save(T entity) {
        try {
            sessionFactory.getCurrentSession().persist(entity);
        } catch (HibernateException e) {
            throw new DataBaseOperationException("Failed to save entity to database: " + e.getMessage());
        }
    }

    @Transactional
    public void update(T entity) {
        try {
            sessionFactory.getCurrentSession().merge(entity);
        } catch (HibernateException e) {
            throw new DataBaseOperationException("Update entity was failed: " + e.getMessage());
        }
    }
}
