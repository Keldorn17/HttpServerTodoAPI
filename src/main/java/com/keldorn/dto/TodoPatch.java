package com.keldorn.dto;

import com.keldorn.entity.Todo;

import java.time.LocalDateTime;

public class TodoPatch {
    private final String title;
    private final String description;
    private final LocalDateTime dueDate;
    private final Boolean completed;
    private final Integer priority;

    public TodoPatch(Todo todo) {
        this.title = todo.getTitle();
        this.description = todo.getDescription();
        this.dueDate = todo.getDueDate();
        this.completed = todo.isCompleted();
        this.priority = todo.getPriority().ordinal();
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

    public Boolean isCompleted() {
        return completed;
    }

    public Integer getPriority() {
        return priority;
    }
}
