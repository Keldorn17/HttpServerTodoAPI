package com.keldorn.server;

import com.keldorn.constants.UserConstants;
import com.keldorn.entity.User;
import com.keldorn.exception.InvalidEmailException;
import com.keldorn.handler.TodoHandler;
import com.keldorn.handler.UserHandler;
import com.keldorn.repository.UserRepository;
import com.keldorn.util.HttpHelper;
import com.keldorn.util.ValidifyEmail;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;

public class UserHttpHandler implements HttpHandler {
    private final EntityManagerFactory emf;

    public UserHttpHandler(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (EntityManager entityManager = emf.createEntityManager()) {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "POST" -> handlePost(exchange, entityManager);
                case "GET" -> handleGet(exchange, entityManager);
                default -> HttpHelper.sendJsonError(exchange, HttpURLConnection.HTTP_BAD_METHOD,
                        "Unsupported method");
            }
        } catch (Exception e) {
            HttpHelper.sendJsonError(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange, EntityManager entityManager) throws IOException {
        var dto = HttpHelper.getUriCompactAddress(exchange, 2, "User ID missing");
        if (dto != null) {
            switch (dto.idCapsulatedUriString()) {
                case UserConstants.USER_TEMPLATE -> handleGetUser(dto.id(), entityManager, exchange);
                case UserConstants.USER_TODOS_TEMPLATE -> handleGetUserTodos(dto.id(), entityManager, exchange);
                default -> HttpHelper.sendJsonError(exchange, HttpURLConnection.HTTP_NOT_FOUND,
                        "This endpoint does not exists.");
            }
        }
    }

    private void handlePost(HttpExchange exchange, EntityManager entityManager) throws IOException, InvalidEmailException {
        String data = new String(exchange.getRequestBody().readAllBytes());
        User user = UserHandler.handleCreateUserRequest(data);

        ValidifyEmail.validify(user.getEmail());
        new UserRepository(entityManager).save(user);
        HttpHelper.writeResponse(exchange, HttpURLConnection.HTTP_CREATED,
                "{\"userId\": %d}".formatted(user.getUserId()));
    }

    private static void handleGetUser(int id, EntityManager entityManager, HttpExchange exchange)
            throws IOException {
        User user = new UserRepository(entityManager).findById(id);

        if (user == null) {
            HttpHelper.sendJsonError(exchange, HttpURLConnection.HTTP_NOT_FOUND, "User not found");
        } else {
            HttpHelper.writeResponse(exchange, HttpURLConnection.HTTP_OK,
                    UserHandler.getJsonUser(user));
        }
    }

    private static void handleGetUserTodos(int id, EntityManager entityManager, HttpExchange exchange)
            throws IOException {
        var todos = new UserRepository(entityManager).findTodosByUserId(id);

        if (todos.isEmpty()) {
            HttpHelper.sendJsonError(exchange, HttpURLConnection.HTTP_NOT_FOUND, "User not found");
        } else {
            HttpHelper.writeResponse(exchange, HttpURLConnection.HTTP_OK,
                    TodoHandler.getTodosResponse(todos));
        }
    }
}
