package com.keldorn.repository;

import com.keldorn.dto.todo.TodoPatch;
import com.keldorn.domain.entity.Todo;
import com.keldorn.domain.enums.Priority;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    public Todo patch(TodoPatch todoPatch, int todoId) throws InvocationTargetException, IllegalAccessException {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaUpdate<Todo> update = builder.createCriteriaUpdate(Todo.class);
        Root<Todo> root = update.from(Todo.class);

        for (Method method : todoPatch.getClass().getMethods()) {
            if ((method.getName().startsWith("get") || method.getName().startsWith("is"))
                && !method.getName().equals("getTodoId")
                && !method.getName().equals("getClass")) {
                Object value = method.invoke(todoPatch);
                if (value != null) {
                    String methodName = method.getName().startsWith("get")
                            ? method.getName().substring(3)
                            : method.getName().substring(2);
                    String fieldName = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
                    update.set(root.get(fieldName), value);
                }
            }
        }

        update.where(builder.equal(root.get("todoId"), todoId));
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createQuery(update).executeUpdate();
            tx.commit();

            Todo todo = em.find(Todo.class, todoId);
            em.refresh(todo);
            return todo;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    public Todo put(TodoPatch dto, int todoId) {
        Todo todo = em.find(Todo.class, todoId);
        if (dto.getTitle() != null) {
            todo.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            todo.setDescription(dto.getDescription());
        }
        if (dto.getDueDate() != null) {
            todo.setDueDate(dto.getDueDate());
        }
        if (dto.isCompleted() != null) {
            todo.setCompleted(dto.isCompleted());
        }
        if (dto.getPriority() != null) {
            todo.setPriority(Priority.values()[dto.getPriority()]);
        }

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.merge(todo);
        tx.commit();
        return todo;
    }
}
