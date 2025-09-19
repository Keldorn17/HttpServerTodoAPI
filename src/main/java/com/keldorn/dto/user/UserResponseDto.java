package com.keldorn.dto.user;

import com.keldorn.domain.entity.User;
import com.keldorn.dto.todo.TodoResponseDto;

import java.util.List;

public class UserResponseDto {
    private final int userId;
    private final String email;
    private final String name;
    private final List<TodoResponseDto> todos;

    public UserResponseDto(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.todos = user.getTodos().stream()
                .map(TodoResponseDto::new)
                .toList();
    }

    public int getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public List<TodoResponseDto> getTodos() {
        return todos;
    }
}
