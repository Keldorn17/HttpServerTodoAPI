package com.keldorn.dto;

import com.keldorn.entity.Todo;

public class TodoResponseDto {
    private int todoId;
    private String title;
    private String description;
    private boolean completed;
    private int priority;

    public TodoResponseDto(Todo todo) {
        this.todoId = todo.getTodoId();
        this.title = todo.getTitle();
        this.description = todo.getDescription();
        this.completed = todo.isCompleted();
        this.priority = todo.getPriority().ordinal();
    }

    public int getTodoId() { return todoId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public boolean isCompleted() { return completed; }
    public int getPriority() { return priority; }
}
