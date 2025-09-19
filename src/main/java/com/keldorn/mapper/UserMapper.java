package com.keldorn.mapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.keldorn.dto.user.CreateUser;
import com.keldorn.dto.user.UserDto;
import com.keldorn.dto.user.UserResponseDto;
import com.keldorn.domain.entity.User;
import com.keldorn.util.json.LocalDateTimeAdapter;

import java.time.LocalDateTime;

public class UserMapper {
    private final static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public static User handleCreateUserRequest(String requestBody) {
        CreateUser dto = gson.fromJson(requestBody, CreateUser.class);
        return new User(dto.getEmail(), dto.getName());
    }

    public static String getJsonUserDetailed(User user) {
        return gson.toJson(new UserResponseDto(user));
    }

    public static String getJsonUser(User user) {
        return gson.toJson(new UserDto(user));
    }
}
