package com.keldorn.dto.todo;

import com.keldorn.domain.entity.Todo;

public class TodoResponseDto {
    private final int todoId;
    private final String title;
    private final String description;
    private final boolean completed;
    private final int priority;

    public TodoResponseDto(Todo todo) {
        this.todoId = todo.getTodoId();
        this.title = todo.getTitle();
        this.description = todo.getDescription();
        this.completed = todo.isCompleted();
        this.priority = todo.getPriority().ordinal();
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

    public boolean isCompleted() {
        return completed;
    }

    public int getPriority() {
        return priority;
    }
}
