package com.keldorn.dto;

import com.keldorn.entity.User;

import java.util.List;

public class UserResponseDto {
    private int userId;
    private String email;
    private String name;
    private List<TodoResponseDto> todos;

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
