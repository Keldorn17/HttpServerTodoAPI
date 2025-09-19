package com.keldorn.mapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.keldorn.dto.todo.CreateTodo;
import com.keldorn.dto.todo.TodoGet;
import com.keldorn.dto.todo.TodoPatch;
import com.keldorn.domain.entity.Todo;
import com.keldorn.domain.enums.Priority;
import com.keldorn.util.json.LocalDateTimeAdapter;
import com.keldorn.util.json.PriorityAdapter;

import java.time.LocalDateTime;
import java.util.List;

public class TodoMapper {
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
