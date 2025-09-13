package com.keldorn.dto;

import com.keldorn.entity.Todo;

import java.time.LocalDateTime;

public class TodoGet {
    private final int todoId;
    private final String title;
    private final String description;
    private final LocalDateTime dueDate;
    private final boolean completed;
    private final int priority;
    private final int userId;

    public TodoGet(Todo todo) {
        this.todoId = todo.getTodoId();
        this.title = todo.getTitle();
        this.description = todo.getDescription();
        this.dueDate = todo.getDueDate();
        this.completed = todo.isCompleted();
        this.priority = todo.getPriority().ordinal();
        this.userId = todo.getUser().getUserId();
    }

    public int getTodoId() {
        return todoId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public int getPriority() {
        return priority;
    }

    public int getUserId() {
        return userId;
    }
}
