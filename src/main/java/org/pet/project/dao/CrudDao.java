package org.pet.project.dao;


import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.pet.project.exception.DataBaseOperationException;
import org.pet.project.exception.UniqueConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


public abstract class CrudDao<T> {

    @Autowired
    protected SessionFactory sessionFactory;

    @Transactional
    public void save(T entity) {
        try {
            sessionFactory.getCurrentSession().persist(entity);
        } catch (ConstraintViolationException e) {
            throw new UniqueConstraintViolationException("Entity violates unique constraint", e);
        } catch (HibernateException e) {
            throw new DataBaseOperationException("Failed to save entity to database", e);
        }
    }

}
