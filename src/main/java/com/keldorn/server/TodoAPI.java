package com.keldorn.server;

import com.sun.net.httpserver.HttpServer;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.IOException;
import java.net.InetSocketAddress;

public class TodoAPI {
    private static EntityManagerFactory emf;

    public static void main(String[] args) {
        try {
            emf = Persistence.createEntityManagerFactory("main.java.com.keldorn.entity");
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            server.createContext("/users", new UserHttpHandler(emf));
            server.createContext("/todos", new TodoHttpHandler(emf));
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
