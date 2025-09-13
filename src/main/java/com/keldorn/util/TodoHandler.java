package com.keldorn.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.keldorn.dto.CreateTodo;
import com.keldorn.enums.Priority;

import java.time.LocalDateTime;

public class TodoHandler {
    private final static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Priority.class, new PriorityAdapter())
            .create();

    public static CreateTodo handleCreateTodoRequest(String requestBody) {
        return gson.fromJson(requestBody, CreateTodo.class);
    }
}
