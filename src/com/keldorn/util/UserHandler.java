package com.keldorn.util;

import com.google.gson.Gson;
import com.keldorn.dto.CreateUser;
import com.keldorn.entity.User;

public class UserHandler {
    private final static Gson gson = new Gson();

    public static User handleCreateUserRequest(String requestBody) {
        CreateUser dto = gson.fromJson(requestBody, CreateUser.class);
        return new User(dto.getEmail(), dto.getName());
    }
}
