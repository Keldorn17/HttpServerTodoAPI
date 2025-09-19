package com.keldorn.api;

import com.keldorn.domain.entity.Todo;
import com.keldorn.dto.todo.CreateTodo;
import com.keldorn.dto.todo.TodoPatch;
import com.keldorn.mapper.TodoMapper;
import com.keldorn.mapper.UserMapper;
import com.keldorn.service.TodoService;
import com.keldorn.util.http.ControllerUtils;
import com.keldorn.util.http.UriSegmentReader;
import com.keldorn.util.json.JsonResponse;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;

public class TodoController implements HttpHandler {
    private final EntityManagerFactory emf;
    public final String USER_NOT_FOUND_ERROR = "User not found";
    public final String TODO_NOT_FOUND_ERROR = "Todo not found";

    public TodoController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (EntityManager entityManager = emf.createEntityManager()) {
            TodoService service = new TodoService(entityManager);
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET" -> handleGet(exchange, service);
                case "POST" -> handlePost(exchange, service);
                case "DELETE" -> handleDelete(exchange, service);
                case "PATCH" -> handlePatch(exchange, service);
                case "PUT" -> handlePut(exchange, service);
                default -> JsonResponse.sendUnsupportedMethod(exchange);
            }
        } catch (Exception e) {
            JsonResponse.sendError(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handlePut(HttpExchange exchange, TodoService service) throws IOException {
        ControllerUtils.getTodoId(exchange).ifPresent(id -> {
            try {
                ControllerUtils.updateService(exchange, () -> {
                    String data = ControllerUtils.readRequestBody(exchange);
                    TodoPatch dto = TodoMapper.handlePatchTodoRequest(data);
                    Todo todo = service.update(id, dto);
                    return TodoMapper.getTodoResponse(todo);
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void handleDelete(HttpExchange exchange, TodoService service) throws IOException {
        ControllerUtils.getTodoId(exchange).ifPresent(id -> {
            try {
                ControllerUtils.deleteIfExists(exchange, service.findById(id), service::delete,
                        TODO_NOT_FOUND_ERROR, "Todo successfully deleted");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void handlePost(HttpExchange exchange, TodoService service) throws IOException {
        String data = ControllerUtils.readRequestBody(exchange);
        CreateTodo dto = TodoMapper.handleCreateTodoRequest(data);
        Todo todo = service.create(dto);
        JsonResponse.writeResponse(exchange, HttpURLConnection.HTTP_CREATED,
                TodoMapper.getTodoResponse(todo));
    }

    private void handleGet(HttpExchange exchange, TodoService service) throws IOException {
        var dto = UriSegmentReader.getUriCompactAddress(exchange, 2);
        if (dto != null) {
            switch (dto.idCapsulatedUriString()) {
                case ApiRoutes.TODO_BY_ID ->
                        ControllerUtils.sendIfExists(exchange, service.findById(dto.id()),
                                TodoMapper::getTodoResponse, TODO_NOT_FOUND_ERROR);
                case ApiRoutes.TODO_USER ->
                        ControllerUtils.sendIfExists(exchange, service.findById(dto.id()),
                                t -> UserMapper.getJsonUser(t.getUser()), USER_NOT_FOUND_ERROR);
                default -> JsonResponse.sendUnknownEndpoint(exchange);
            }
        }
    }

    private void handlePatch(HttpExchange exchange, TodoService service) throws IOException {
        ControllerUtils.getTodoId(exchange).ifPresent(id -> {
            try {
                ControllerUtils.updateService(exchange, () -> {
                    String data = ControllerUtils.readRequestBody(exchange);
                    TodoPatch dto = TodoMapper.handlePatchTodoRequest(data);
                    Todo todo = service.patch(id, dto);
                    return TodoMapper.getTodoResponse(todo);
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
