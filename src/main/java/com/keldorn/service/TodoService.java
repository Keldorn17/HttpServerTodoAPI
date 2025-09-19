package com.keldorn.service;

import com.keldorn.domain.entity.Todo;
import com.keldorn.dto.todo.CreateTodo;
import com.keldorn.dto.todo.TodoPatch;
import com.keldorn.repository.TodoRepository;
import com.keldorn.repository.UserRepository;
import jakarta.persistence.EntityManager;

import java.lang.reflect.InvocationTargetException;

public class TodoService {
    private final TodoRepository todoRepo;
    private final UserRepository userRepo;

    public TodoService(EntityManager em) {
        this.todoRepo = new TodoRepository(em);
        this.userRepo = new UserRepository(em);
    }

    public Todo update(int todoId, TodoPatch dto) {
        return todoRepo.put(dto, todoId);
    }

    public Todo patch(int todoId, TodoPatch dto) throws InvocationTargetException, IllegalAccessException {
        return todoRepo.patch(dto, todoId);
    }

    public void delete(Todo todo) {
        todoRepo.delete(todo);
    }

    public Todo findById(int todoId) {
        return todoRepo.findById(todoId);
    }

    public Todo create(CreateTodo dto) {
        Todo todo = dto.getTodo();
        todo.setUser(userRepo.findById(dto.getUserId()));
        todoRepo.save(todo);
        return todo;
    }
}
