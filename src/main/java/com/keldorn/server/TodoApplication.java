package com.keldorn.server;

import com.keldorn.api.ApiRoutes;
import com.keldorn.api.TodoController;
import com.keldorn.api.UserController;
import com.keldorn.util.db.JpaUtil;
import com.sun.net.httpserver.HttpServer;
import jakarta.persistence.EntityManagerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public class TodoApplication {
    private static EntityManagerFactory emf;

    public static void main(String[] args) {
        try {
            emf = JpaUtil.getEntityManagerFactory();
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            server.createContext(ApiRoutes.BASE_USERS, new UserController(emf));
            server.createContext(ApiRoutes.BASE_TODOS, new TodoController(emf));
            server.start();
            System.out.println("Server is listening on port 8080...");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (emf != null && emf.isOpen()) {
                    emf.close();
                }
            }));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
