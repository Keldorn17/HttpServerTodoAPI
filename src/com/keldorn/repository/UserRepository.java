package com.keldorn.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import com.keldorn.entity.User;

import java.util.stream.Stream;

public class UserRepository {
    private final EntityManager em;

    public UserRepository(EntityManager em) {
        this.em = em;
    }

    public void save(User user) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.persist(user);
        transaction.commit();
    }

    public User findById(int id) {
        return em.find(User.class, id);
    }

    public Stream<User> findAll() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultStream();
    }

    public void delete(User user) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.remove(user);
        transaction.commit();
    }
}
