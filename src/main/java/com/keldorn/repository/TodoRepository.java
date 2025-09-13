package com.keldorn.repository;

import com.keldorn.entity.Todo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.stream.Stream;

public class TodoRepository {
    private final EntityManager em;

    public TodoRepository(EntityManager em) {
        this.em = em;
    }

    public void save(Todo todo) {
        var tx = em.getTransaction();
        tx.begin();
        em.persist(todo);
        tx.commit();
    }

    public Todo findById(int id) {
        return em.find(Todo.class, id);
    }

    public Stream<Todo> findAll() {
        return em.createQuery("SELECT t FROM Todo t", Todo.class).getResultStream();
    }

    public void delete(Todo todo) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.remove(todo);
        transaction.commit();
    }
}
