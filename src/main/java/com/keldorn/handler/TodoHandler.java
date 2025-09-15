package com.keldorn.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.keldorn.dto.CreateTodo;
import com.keldorn.dto.TodoGet;
import com.keldorn.dto.TodoPatch;
import com.keldorn.entity.Todo;
import com.keldorn.enums.Priority;
import com.keldorn.util.LocalDateTimeAdapter;
import com.keldorn.util.PriorityAdapter;

import java.time.LocalDateTime;
import java.util.List;

public class TodoHandler {
    private final static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Priority.class, new PriorityAdapter())
            .create();

    public static CreateTodo handleCreateTodoRequest(String requestBody) {
        return gson.fromJson(requestBody, CreateTodo.class);
    }

    public static TodoPatch handlePatchTodoRequest(String requestBody) {
        return gson.fromJson(requestBody, TodoPatch.class);
    }

    public static String getTodoResponse(Todo todo) {
        return gson.toJson(new TodoGet(todo));
    }

    public static String getTodosResponse(List<Todo> todo) {
        return gson.toJson(todo.stream()
                .map(TodoGet::new)
                .toList());
    }
}
