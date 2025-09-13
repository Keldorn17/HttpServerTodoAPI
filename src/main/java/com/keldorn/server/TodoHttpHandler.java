package com.keldorn.server;

import com.keldorn.dto.CreateTodo;
import com.keldorn.entity.Todo;
import com.keldorn.entity.User;
import com.keldorn.repository.TodoRepository;
import com.keldorn.repository.UserRepository;
import com.keldorn.util.HttpHelper;
import com.keldorn.util.TodoHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.io.IOException;
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
                default -> HttpHelper.writeResponse("{\"error\":\"Unsupported method\"}",
                        HttpURLConnection.HTTP_BAD_METHOD, exchange);
            }
        } catch (Exception e) {
            String error = "{\"error\":\"" + e.getMessage() + "\"}";
            HttpHelper.writeResponse(error, HttpURLConnection.HTTP_INTERNAL_ERROR, exchange);
        } finally {
            exchange.close();
        }
    }

    private void handleDelete(HttpExchange exchange, EntityManager entityManager) throws IOException {
        String error = "{\"error\":\"Todo ID missing\"}";
        int todoId = HttpHelper.getIdFromURI(exchange, 2, error);
        if (todoId != -1) {
            handleDeleteTodoResponse(todoId, entityManager, exchange);
        }
    }

    private void handlePost(HttpExchange exchange, EntityManager entityManager) throws IOException {
        String data = new String(exchange.getRequestBody().readAllBytes());
        CreateTodo dto = TodoHandler.handleCreateTodoRequest(data);
        Todo todo = saveTodo(dto, entityManager);
        String response = "{\"todoId\": %d}".formatted(todo.getTodoId());
        HttpHelper.writeResponse(response, HttpURLConnection.HTTP_CREATED, exchange);
    }

    private void handleGet(HttpExchange exchange, EntityManager entityManager) throws IOException {
        String error = "{\"error\":\"Todo ID missing\"}";
        int todoId = HttpHelper.getIdFromURI(exchange, 2, error);
        if (todoId != -1) {
            handleGetTodoResponse(todoId, entityManager, exchange);
        }
    }

    private Todo saveTodo(CreateTodo dto, EntityManager entityManager) {
        Todo todo = dto.getTodo();
        User user = new UserRepository(entityManager).findById(dto.getUserId());
        todo.setUser(user);
        new TodoRepository(entityManager).save(todo);
        return todo;
    }

    private static void handleGetTodoResponse(int id, EntityManager entityManager, HttpExchange exchange)
            throws IOException {
        Todo todo = new TodoRepository(entityManager).findById(id);
        if (todo == null) {
            String notFound = "{\"error\":\"Todo not found\"}";
            HttpHelper.writeResponse(notFound, HttpURLConnection.HTTP_NOT_FOUND, exchange);
        } else {
            String response = """
                    {
                      "title": "%s",
                      "description": "%s",
                      "dueDate": "%s",
                      "completed": %s,
                      "priority": %d,
                      "userId": %d
                    }""".formatted(todo.getTitle(), todo.getDescription(), todo.getDueDate(), todo.isCompleted(),
                    todo.getPriority().ordinal(), todo.getUser().getUserId());
            HttpHelper.writeResponse(response, HttpURLConnection.HTTP_OK, exchange);
        }
    }

    private static void handleDeleteTodoResponse(int id, EntityManager entityManager, HttpExchange exchange)
            throws IOException {
        TodoRepository todoRepository = new TodoRepository(entityManager);
        Todo todo = todoRepository.findById(id);
        if (todo == null) {
            String notFound = "{\"error\":\"Todo not found\"}";
            HttpHelper.writeResponse(notFound, HttpURLConnection.HTTP_NOT_FOUND, exchange);
        } else {
            todoRepository.delete(todo);
            String response = "{\"result\": \"Todo successfully deleted\"}";
            HttpHelper.writeResponse(response, HttpURLConnection.HTTP_OK, exchange);
        }

    }
}
