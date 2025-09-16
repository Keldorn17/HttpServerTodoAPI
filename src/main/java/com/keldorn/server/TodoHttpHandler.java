package com.keldorn.server;

import com.google.gson.JsonSyntaxException;
import com.keldorn.constants.ApiRoutes;
import com.keldorn.constants.TodoConstant;
import com.keldorn.dto.CreateTodo;
import com.keldorn.dto.TodoPatch;
import com.keldorn.entity.Todo;
import com.keldorn.handler.UserHandler;
import com.keldorn.repository.TodoRepository;
import com.keldorn.repository.UserRepository;
import com.keldorn.util.HttpHelper;
import com.keldorn.handler.TodoHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;

public class TodoHttpHandler implements HttpHandler {
    private final EntityManagerFactory emf;

    public TodoHttpHandler(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (EntityManager entityManager = emf.createEntityManager()) {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET" -> handleGet(exchange, entityManager);
                case "POST" -> handlePost(exchange, entityManager);
                case "DELETE" -> handleDelete(exchange, entityManager);
                case "PATCH" -> handlePatch(exchange, entityManager);
                case "PUT" -> handlePut(exchange, entityManager);
                default -> HttpHelper.sendJsonUnsupportedMethod(exchange);
            }
        } catch (Exception e) {
            HttpHelper.sendJsonError(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handlePut(HttpExchange exchange, EntityManager entityManager) throws IOException {
        var todoId = HttpHelper.getIdFromURI(exchange, 2, TodoConstant.TODO_ID_MISSING);
        if (todoId.isPresent()) {
            String data = new String(exchange.getRequestBody().readAllBytes());
            try {
                TodoPatch dto = TodoHandler.handlePatchTodoRequest(data);
                Todo todo = new TodoRepository(entityManager).put(dto, todoId.getAsInt());
                HttpHelper.writeResponse(exchange, HttpURLConnection.HTTP_OK,
                        TodoHandler.getTodoResponse(todo));
            } catch (JsonSyntaxException e) {
                HttpHelper.sendJsonMalformedJson(exchange);
            } catch (Exception e) {
                HttpHelper.sendJsonError(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR,
                        "Unexpected error: " + e.getMessage());
            }
        }
    }

    private void handleDelete(HttpExchange exchange, EntityManager entityManager) throws IOException {
        var todoId = HttpHelper.getIdFromURI(exchange, 2, TodoConstant.TODO_ID_MISSING);
        if (todoId.isPresent()) {
            TodoRepository todoRepository = new TodoRepository(entityManager);
            Todo todo = todoRepository.findById(todoId.getAsInt());
            if (todo == null) {
                HttpHelper.sendJsonError(exchange, HttpURLConnection.HTTP_NOT_FOUND, TodoConstant.TODO_NOT_FOUND);
            } else {
                todoRepository.delete(todo);
                HttpHelper.sendJsonResult(exchange, HttpURLConnection.HTTP_OK,
                        "Todo successfully deleted");
            }
        }
    }

    private void handlePost(HttpExchange exchange, EntityManager entityManager) throws IOException {
        String data = new String(exchange.getRequestBody().readAllBytes());
        CreateTodo dto = TodoHandler.handleCreateTodoRequest(data);
        Todo todo = dto.getTodo();
        todo.setUser(new UserRepository(entityManager).findById(dto.getUserId()));
        new TodoRepository(entityManager).save(todo);
        HttpHelper.writeResponse(exchange, HttpURLConnection.HTTP_CREATED,
                TodoHandler.getTodoResponse(todo));
    }

    private void handleGet(HttpExchange exchange, EntityManager entityManager) throws IOException {
        var dto = HttpHelper.getUriCompactAddress(exchange, 2, TodoConstant.TODO_ID_MISSING);
        if (dto != null) {
            switch (dto.idCapsulatedUriString()) {
                case ApiRoutes.TODO_BY_ID -> handleGetTodos(dto.id(), exchange, entityManager);
                case ApiRoutes.TODO_USER -> handleGetTodosUser(dto.id(), exchange, entityManager);
                default -> HttpHelper.sendJsonUnknownEndpoint(exchange);
            }
        }
    }

    private void handleGetTodos(int id, HttpExchange exchange, EntityManager entityManager) throws IOException {
        Todo todo = new TodoRepository(entityManager).findById(id);
        if (todo == null) {
            HttpHelper.sendJsonError(exchange, HttpURLConnection.HTTP_NOT_FOUND, TodoConstant.TODO_NOT_FOUND);
        } else {
            HttpHelper.writeResponse(exchange, HttpURLConnection.HTTP_OK,
                    TodoHandler.getTodoResponse(todo));
        }
    }

    private void handleGetTodosUser(int id, HttpExchange exchange, EntityManager entityManager) throws IOException {
        Todo todo = new TodoRepository(entityManager).findById(id);
        if (todo == null) {
            HttpHelper.sendJsonError(exchange, HttpURLConnection.HTTP_NOT_FOUND, TodoConstant.TODO_NOT_FOUND);
        } else {
            HttpHelper.writeResponse(exchange, HttpURLConnection.HTTP_OK,
                    UserHandler.getJsonUser(todo.getUser()));
        }
    }

    private void handlePatch(HttpExchange exchange, EntityManager entityManager) throws IOException {
        var todoId = HttpHelper.getIdFromURI(exchange, 2, TodoConstant.TODO_ID_MISSING);
        if (todoId.isPresent()) {
            String data = new String(exchange.getRequestBody().readAllBytes());
            try {
                TodoPatch dto = TodoHandler.handlePatchTodoRequest(data);
                Todo todo = new TodoRepository(entityManager).patch(dto, todoId.getAsInt());
                HttpHelper.writeResponse(exchange, HttpURLConnection.HTTP_OK,
                        TodoHandler.getTodoResponse(todo));
            } catch (JsonSyntaxException e) {
                HttpHelper.sendJsonMalformedJson(exchange);
            } catch (InvocationTargetException | IllegalAccessException e) {
                HttpHelper.sendJsonError(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR,
                        "Internal server error: " + e.getMessage());
            } catch (Exception e) {
                HttpHelper.sendJsonError(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR,
                        "Unexpected error: " + e.getMessage());
            }
        }
    }
}
