package com.keldorn.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.keldorn.dto.CreateUser;
import com.keldorn.dto.UserResponseDto;
import com.keldorn.entity.User;
import com.keldorn.enums.Priority;

import java.time.LocalDateTime;

public class UserHandler {
    private final static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Priority.class, new PriorityAdapter())
            .create();

    public static User handleCreateUserRequest(String requestBody) {
        CreateUser dto = gson.fromJson(requestBody, CreateUser.class);
        return new User(dto.getEmail(), dto.getName());
    }

    public static String getJsonUser(User user) {
        return gson.toJson(new UserResponseDto(user));
    }
}
