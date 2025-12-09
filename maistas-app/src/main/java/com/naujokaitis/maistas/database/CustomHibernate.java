package com.naujokaitis.maistas.database;

import com.naujokaitis.maistas.model.Restaurant;
import com.naujokaitis.maistas.model.Review;
import com.naujokaitis.maistas.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.UUID;

public class CustomHibernate {

    public User findUserByUsername(String username) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Review> findReviewsByRestaurantId(UUID restaurantId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Review> query = em.createQuery(
                    "SELECT r FROM Review r LEFT JOIN FETCH r.author LEFT JOIN FETCH r.targetRestaurant WHERE r.targetRestaurant.id = :restaurantId",
                    Review.class);
            query.setParameter("restaurantId", restaurantId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Restaurant findRestaurantWithPricingRules(UUID restaurantId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Restaurant> query = em.createQuery(
                    "SELECT DISTINCT r FROM Restaurant r LEFT JOIN FETCH r.pricingRules WHERE r.id = :id",
                    Restaurant.class);
            query.setParameter("id", restaurantId);
            Restaurant restaurant = query.getSingleResult();

            // Manually initialize the embedded ElementCollection (hours map)
            // since JOIN FETCH doesn't work with @ElementCollection in @Embeddable
            if (restaurant.getOperatingHours() != null) {
                restaurant.getOperatingHours().getHours().size(); // force initialization
            }

            return restaurant;
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public Double calculateAverageRating(UUID restaurantId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Double> query = em.createQuery(
                    "SELECT AVG(r.rating) FROM Review r WHERE r.targetRestaurant.id = :restaurantId",
                    Double.class);
            query.setParameter("restaurantId", restaurantId);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }

    public void updateRestaurantRating(UUID restaurantId) {
        Double avgRating = calculateAverageRating(restaurantId);
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Restaurant restaurant = em.find(Restaurant.class, restaurantId);
            if (restaurant != null) {
                restaurant.setRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : null);
                em.merge(restaurant);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public com.naujokaitis.maistas.model.ChatThread findChatThreadWithMessages(UUID threadId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<com.naujokaitis.maistas.model.ChatThread> query = em.createQuery(
                    "SELECT DISTINCT ct FROM ChatThread ct LEFT JOIN FETCH ct.messages m LEFT JOIN FETCH m.author WHERE ct.id = :id ORDER BY m.sentAt ASC",
                    com.naujokaitis.maistas.model.ChatThread.class);
            query.setParameter("id", threadId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}
