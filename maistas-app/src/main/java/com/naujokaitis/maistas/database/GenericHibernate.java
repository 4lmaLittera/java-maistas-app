package com.naujokaitis.maistas.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.hibernate.Hibernate;

import java.util.List;
import java.util.function.Consumer;

public class GenericHibernate<T> {

    private final Class<T> entityClass;

    public GenericHibernate(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public void save(T entity) {
        executeInTransaction(em -> em.persist(entity));
    }

    public T find(Object id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            T entity = em.find(entityClass, id);
            if (entity != null) {
                Hibernate.initialize(entity);
            }
            return entity;
        } finally {
            em.close();
        }
    }

    public T update(T entity) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T merged = em.merge(entity);
            tx.commit();
            return merged;
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(T entity) {
        executeInTransaction(em -> {
            T managed = em.contains(entity) ? entity : em.merge(entity);
            em.remove(managed);
        });
    }

    public List<T> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String ql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            List<T> results = em.createQuery(ql, entityClass).getResultList();
            // Initialize proxies to avoid LazyInitializationException
            for (T entity : results) {
                Hibernate.initialize(entity);
            }
            return results;
        } finally {
            em.close();
        }
    }

    private void executeInTransaction(Consumer<EntityManager> action) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            action.accept(em);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
