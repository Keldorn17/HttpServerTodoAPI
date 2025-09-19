package com.keldorn.repository;

import com.keldorn.domain.entity.Todo;
import com.keldorn.domain.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
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

    public List<Todo> findTodosByUserId(int id) {
        return em.createQuery("SELECT t FROM Todo t WHERE t.user.userId = :userId", Todo.class)
                .setParameter("userId", id)
                .getResultList();
    }

    public void delete(User user) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.remove(user);
        transaction.commit();
    }
}
