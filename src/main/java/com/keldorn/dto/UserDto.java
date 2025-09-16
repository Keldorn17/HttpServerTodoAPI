package com.keldorn.dto;

import com.keldorn.entity.User;

public class UserDto {
    private final int userId;
    private final String email;
    private final String name;

    public UserDto(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.name = user.getName();
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
}
