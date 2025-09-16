package com.keldorn.server;

import com.keldorn.constants.ApiRoutes;
import com.keldorn.constants.UserConstant;
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
import org.hibernate.exception.ConstraintViolationException;

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
                case "DELETE" -> handleDelete(exchange, entityManager);
                default -> HttpHelper.sendJsonUnsupportedMethod(exchange);
            }
        } catch (Exception e) {
            HttpHelper.sendJsonError(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handleDelete(HttpExchange exchange, EntityManager entityManager) throws IOException {
        var userId = HttpHelper.getIdFromURI(exchange, 2, UserConstant.USER_ID_MISSING);
        if (userId.isPresent()) {
            UserRepository userRepository = new UserRepository(entityManager);
            User user = userRepository.findById(userId.getAsInt());
            if (user == null) {
                HttpHelper.sendJsonError(exchange, HttpURLConnection.HTTP_NOT_FOUND, UserConstant.USER_NOT_FOUND);
            } else {
                userRepository.delete(user);
                HttpHelper.sendJsonResult(exchange, HttpURLConnection.HTTP_OK, "User successfully deleted.");
            }
        }
    }

    private void handleGet(HttpExchange exchange, EntityManager entityManager) throws IOException {
        var dto = HttpHelper.getUriCompactAddress(exchange, 2, UserConstant.USER_ID_MISSING);
        if (dto != null) {
            switch (dto.idCapsulatedUriString()) {
                case ApiRoutes.USER_BY_ID -> handleGetUser(dto.id(), entityManager, exchange);
                case ApiRoutes.USER_TODOS -> handleGetUserTodos(dto.id(), entityManager, exchange);
                default -> HttpHelper.sendJsonUnknownEndpoint(exchange);
            }
        }
    }

    private void handlePost(HttpExchange exchange, EntityManager entityManager) throws IOException, InvalidEmailException {
        String data = new String(exchange.getRequestBody().readAllBytes());
        User user = UserHandler.handleCreateUserRequest(data);
        ValidifyEmail.validify(user.getEmail());
        try {
            new UserRepository(entityManager).save(user);
        } catch (ConstraintViolationException e) {
            HttpHelper.sendJsonError(exchange, HttpURLConnection.HTTP_CONFLICT, "Email already exists");
        }
        HttpHelper.writeResponse(exchange, HttpURLConnection.HTTP_CREATED,
                "{\"userId\": %d}".formatted(user.getUserId()));
    }

    private static void handleGetUser(int id, EntityManager entityManager, HttpExchange exchange)
            throws IOException {
        User user = new UserRepository(entityManager).findById(id);

        if (user == null) {
            HttpHelper.sendJsonError(exchange, HttpURLConnection.HTTP_NOT_FOUND, UserConstant.USER_NOT_FOUND);
        } else {
            HttpHelper.writeResponse(exchange, HttpURLConnection.HTTP_OK,
                    UserHandler.getJsonUserDetailed(user));
        }
    }

    private static void handleGetUserTodos(int id, EntityManager entityManager, HttpExchange exchange)
            throws IOException {
        var todos = new UserRepository(entityManager).findTodosByUserId(id);

        if (todos.isEmpty()) {
            HttpHelper.sendJsonError(exchange, HttpURLConnection.HTTP_NOT_FOUND, UserConstant.USER_NOT_FOUND);
        } else {
            HttpHelper.writeResponse(exchange, HttpURLConnection.HTTP_OK,
                    TodoHandler.getTodosResponse(todos));
        }
    }
}
