package com.keldorn.dto;

import com.keldorn.entity.Todo;
import com.keldorn.enums.Priority;

import java.time.LocalDateTime;

public class CreateTodo {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private boolean completed;
    private Priority priority;
    private int userId;

    public CreateTodo() {
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

    public Priority getPriority() {
        return priority;
    }

    public int getUserId() {
        return userId;
    }

    public Todo getTodo() {
        return new Todo(title, description, dueDate, completed, priority);
    }
}
